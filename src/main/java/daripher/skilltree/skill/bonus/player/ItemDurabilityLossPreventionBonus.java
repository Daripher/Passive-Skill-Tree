package daripher.skilltree.skill.bonus.player;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.data.serializers.SerializationHelper;
import daripher.skilltree.init.PSTSkillBonuses;
import daripher.skilltree.network.NetworkHelper;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.multiplier.LivingMultiplier;
import daripher.skilltree.skill.bonus.multiplier.NoneLivingMultiplier;
import daripher.skilltree.skill.bonus.predicate.item.ItemStackPredicate;
import daripher.skilltree.skill.bonus.predicate.item.NoneItemStackPredicate;
import daripher.skilltree.skill.bonus.predicate.living.LivingEntityPredicate;
import daripher.skilltree.skill.bonus.predicate.living.NoneLivingEntityPredicate;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.function.Consumer;

public final class ItemDurabilityLossPreventionBonus implements SkillBonus<ItemDurabilityLossPreventionBonus> {
    private float chance;
    private @Nonnull LivingMultiplier playerMultiplier = NoneLivingMultiplier.INSTANCE;
    private @Nonnull LivingEntityPredicate playerCondition = NoneLivingEntityPredicate.INSTANCE;
    private @Nonnull ItemStackPredicate itemStackPredicate = NoneItemStackPredicate.INSTANCE;

    public ItemDurabilityLossPreventionBonus(float chance) {
        this.chance = chance;
    }

    public float getDurabilityLossChanceReduction(Player player, ItemStack itemStack) {
        if (!playerCondition.test(player)) {
            return 0f;
        }
        if (!itemStackPredicate.test(itemStack)) {
            return 0f;
        }
        return chance * playerMultiplier.getValue(player);
    }

    @Override
    public SkillBonus.Serializer getSerializer() {
        return PSTSkillBonuses.ITEM_DURABILITY_LOSS_AVOIDANCE.get();
    }

    @Override
    public ItemDurabilityLossPreventionBonus copy() {
        ItemDurabilityLossPreventionBonus bonus = new ItemDurabilityLossPreventionBonus(chance);
        bonus.playerMultiplier = this.playerMultiplier;
        bonus.playerCondition = this.playerCondition;
        bonus.itemStackPredicate = this.itemStackPredicate;
        return bonus;
    }

    @Override
    public ItemDurabilityLossPreventionBonus multiply(double multiplier) {
        chance *= (float) multiplier;
        return this;
    }

    @Override
    public boolean canMerge(SkillBonus<?> other) {
        if (!(other instanceof ItemDurabilityLossPreventionBonus otherBonus)) {
            return false;
        }
        if (!Objects.equals(otherBonus.playerMultiplier, this.playerMultiplier)) {
            return false;
        }
        if (!Objects.equals(otherBonus.itemStackPredicate, this.itemStackPredicate)) {
            return false;
        }
        return Objects.equals(otherBonus.playerCondition, this.playerCondition);
    }

    @Override
    public SkillBonus<ItemDurabilityLossPreventionBonus> merge(SkillBonus<?> other) {
        if (!(other instanceof ItemDurabilityLossPreventionBonus otherBonus)) {
            throw new IllegalArgumentException();
        }
        float mergedChance = otherBonus.chance + this.chance;
        ItemDurabilityLossPreventionBonus mergedBonus = new ItemDurabilityLossPreventionBonus(mergedChance);
        mergedBonus.playerMultiplier = this.playerMultiplier;
        mergedBonus.playerCondition = this.playerCondition;
        mergedBonus.itemStackPredicate = this.itemStackPredicate;
        return mergedBonus;
    }

    @Override
    public MutableComponent getSimpleTooltip() {
        MutableComponent tooltip;
        if (chance < 1f) {
            tooltip = Component.translatable(getDescriptionId() + ".chance", itemStackPredicate.getTooltip());
            tooltip = TooltipHelper.getSkillBonusTooltip(tooltip, chance, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        } else {
            tooltip = Component.translatable(getDescriptionId(), itemStackPredicate.getTooltip());
        }
        tooltip = playerMultiplier.getTooltip(tooltip, Target.PLAYER);
        tooltip = playerCondition.getTooltip(tooltip, Target.PLAYER);
        return tooltip.withStyle(TooltipHelper.getSkillBonusStyle(isPositive()));
    }

    @Override
    public boolean isPositive() {
        return chance > 0;
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, Consumer<ItemDurabilityLossPreventionBonus> consumer) {
        editor.addLabel(0, 0, "Chance", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addNumericTextField(0, 0, 50, 14, chance).setNumericResponder(value -> selectChance(consumer, value));
        editor.increaseHeight(19);
        editor.addLabel(0, 0, "Item Condition", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addSelectionMenu(0, 0, 200, itemStackPredicate).setResponder(condition -> selectItemCondition(editor, consumer, condition))
                .setMenuInitFunc(() -> addItemConditionWidgets(editor, consumer));
        editor.increaseHeight(19);
        editor.addLabel(0, 0, "Player Condition", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addSelectionMenu(0, 0, 200, playerCondition).setResponder(condition -> selectPlayerCondition(editor, consumer, condition))
                .setMenuInitFunc(() -> addPlayerConditionWidgets(editor, consumer));
        editor.increaseHeight(19);
        editor.addLabel(0, 0, "Player Multiplier", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addSelectionMenu(0, 0, 200, playerMultiplier)
                .setResponder(multiplier -> selectPlayerMultiplier(editor, consumer, multiplier))
                .setMenuInitFunc(() -> addPlayerMultiplierWidgets(editor, consumer));
        editor.increaseHeight(19);
    }

    private void selectChance(Consumer<ItemDurabilityLossPreventionBonus> consumer, Double value) {
        setChance(value.floatValue());
        consumer.accept(this.copy());
    }

    private void addPlayerMultiplierWidgets(SkillTreeEditor editor, Consumer<ItemDurabilityLossPreventionBonus> consumer) {
        playerMultiplier.addEditorWidgets(editor, multiplier -> {
            setPlayerMultiplier(multiplier);
            consumer.accept(this.copy());
        });
    }

    private void selectPlayerMultiplier(SkillTreeEditor editor, Consumer<ItemDurabilityLossPreventionBonus> consumer, LivingMultiplier multiplier) {
        setPlayerMultiplier(multiplier);
        consumer.accept(this.copy());
        editor.rebuildWidgets();
    }

    private void addPlayerConditionWidgets(SkillTreeEditor editor, Consumer<ItemDurabilityLossPreventionBonus> consumer) {
        playerCondition.addEditorWidgets(editor, c -> {
            setPlayerCondition(c);
            consumer.accept(this.copy());
        });
    }

    private void selectPlayerCondition(SkillTreeEditor editor, Consumer<ItemDurabilityLossPreventionBonus> consumer, LivingEntityPredicate condition) {
        setPlayerCondition(condition);
        consumer.accept(this.copy());
        editor.rebuildWidgets();
    }

    private void addItemConditionWidgets(SkillTreeEditor editor, Consumer<ItemDurabilityLossPreventionBonus> consumer) {
        itemStackPredicate.addEditorWidgets(editor, c -> {
            setItemCondition(c);
            consumer.accept(this.copy());
        });
    }

    private void selectItemCondition(SkillTreeEditor editor, Consumer<ItemDurabilityLossPreventionBonus> consumer, ItemStackPredicate condition) {
        setItemCondition(condition);
        consumer.accept(this.copy());
        editor.rebuildWidgets();
    }

    public SkillBonus<?> setPlayerCondition(LivingEntityPredicate condition) {
        this.playerCondition = condition;
        return this;
    }

    public SkillBonus<?> setItemCondition(ItemStackPredicate condition) {
        this.itemStackPredicate = condition;
        return this;
    }

    public SkillBonus<?> setPlayerMultiplier(LivingMultiplier multiplier) {
        this.playerMultiplier = multiplier;
        return this;
    }

    public void setChance(float chance) {
        this.chance = chance;
    }

    public static class Serializer implements SkillBonus.Serializer {
        @Override
        public ItemDurabilityLossPreventionBonus deserialize(JsonObject json) throws JsonParseException {
            float chance = SerializationHelper.getElement(json, "chance").getAsFloat();
            ItemDurabilityLossPreventionBonus bonus = new ItemDurabilityLossPreventionBonus(chance);
            bonus.playerMultiplier = SerializationHelper.deserializeLivingMultiplier(json, "player_multiplier");
            bonus.playerCondition = SerializationHelper.deserializeLivingCondition(json, "player_condition");
            bonus.itemStackPredicate = SerializationHelper.deserializeItemPredicate(json);
            return bonus;
        }

        @Override
        public void serialize(JsonObject json, SkillBonus<?> bonus) {
            if (!(bonus instanceof ItemDurabilityLossPreventionBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            json.addProperty("chance", aBonus.chance);
            SerializationHelper.serializeLivingMultiplier(json, aBonus.playerMultiplier, "player_multiplier");
            SerializationHelper.serializeLivingCondition(json, aBonus.playerCondition, "player_condition");
            SerializationHelper.serializeItemPredicate(json, aBonus.itemStackPredicate);
        }

        @Override
        public ItemDurabilityLossPreventionBonus deserialize(CompoundTag tag) {
            float chance = tag.getFloat("chance");
            ItemDurabilityLossPreventionBonus bonus = new ItemDurabilityLossPreventionBonus(chance);
            bonus.playerMultiplier = SerializationHelper.deserializeLivingMultiplier(tag, "player_multiplier");
            bonus.playerCondition = SerializationHelper.deserializeLivingCondition(tag, "player_condition");
            bonus.itemStackPredicate = SerializationHelper.deserializeItemPredicate(tag);
            return bonus;
        }

        @Override
        public CompoundTag serialize(SkillBonus<?> bonus) {
            if (!(bonus instanceof ItemDurabilityLossPreventionBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            CompoundTag tag = new CompoundTag();
            tag.putFloat("chance", aBonus.chance);
            SerializationHelper.serializeLivingMultiplier(tag, aBonus.playerMultiplier, "player_multiplier");
            SerializationHelper.serializeLivingCondition(tag, aBonus.playerCondition, "player_condition");
            SerializationHelper.serializeItemPredicate(tag, aBonus.itemStackPredicate);
            return tag;
        }

        @Override
        public ItemDurabilityLossPreventionBonus deserialize(FriendlyByteBuf buf) {
            float chance = buf.readFloat();
            ItemDurabilityLossPreventionBonus bonus = new ItemDurabilityLossPreventionBonus(chance);
            bonus.playerMultiplier = NetworkHelper.readLivingMultiplier(buf);
            bonus.playerCondition = NetworkHelper.readLivingCondition(buf);
            bonus.itemStackPredicate = NetworkHelper.readItemPredicate(buf);
            return bonus;
        }

        @Override
        public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
            if (!(bonus instanceof ItemDurabilityLossPreventionBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            buf.writeFloat(aBonus.chance);
            NetworkHelper.writeLivingMultiplier(buf, aBonus.playerMultiplier);
            NetworkHelper.writeLivingCondition(buf, aBonus.playerCondition);
            NetworkHelper.writeItemPredicate(buf, aBonus.itemStackPredicate);
        }

        @Override
        public SkillBonus<?> createDefaultInstance() {
            return new ItemDurabilityLossPreventionBonus(0.1f);
        }
    }
}
