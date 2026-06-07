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
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.function.Consumer;

public final class ItemUsageSpeedBonus implements SkillBonus<ItemUsageSpeedBonus> {
    private float multiplier;
    private @Nonnull LivingMultiplier playerMultiplier = NoneLivingMultiplier.INSTANCE;
    private @Nonnull LivingEntityPredicate playerCondition = NoneLivingEntityPredicate.INSTANCE;
    private @Nonnull ItemStackPredicate itemStackPredicate = NoneItemStackPredicate.INSTANCE;

    public ItemUsageSpeedBonus(float multiplier) {
        this.multiplier = multiplier;
    }

    public float getMultiplier(Player player, ItemStack itemStack) {
        if (!playerCondition.test(player)) {
            return 0f;
        }
        if (!itemStackPredicate.test(itemStack)) {
            return 0f;
        }
        return multiplier * playerMultiplier.getValue(player);
    }

    @Override
    public SkillBonus.Serializer getSerializer() {
        return PSTSkillBonuses.ITEM_USAGE_SPEED.get();
    }

    @Override
    public ItemUsageSpeedBonus copy() {
        ItemUsageSpeedBonus bonus = new ItemUsageSpeedBonus(multiplier);
        bonus.playerMultiplier = this.playerMultiplier;
        bonus.playerCondition = this.playerCondition;
        bonus.itemStackPredicate = this.itemStackPredicate;
        return bonus;
    }

    @Override
    public ItemUsageSpeedBonus multiply(double multiplier) {
        this.multiplier *= (float) multiplier;
        return this;
    }

    @Override
    public boolean canMerge(SkillBonus<?> other) {
        if (!(other instanceof ItemUsageSpeedBonus otherBonus)) {
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
    public SkillBonus<ItemUsageSpeedBonus> merge(SkillBonus<?> other) {
        if (!(other instanceof ItemUsageSpeedBonus otherBonus)) {
            throw new IllegalArgumentException();
        }
        float mergedMultiplier = otherBonus.multiplier + this.multiplier;
        ItemUsageSpeedBonus mergedBonus = new ItemUsageSpeedBonus(mergedMultiplier);
        mergedBonus.playerMultiplier = this.playerMultiplier;
        mergedBonus.playerCondition = this.playerCondition;
        mergedBonus.itemStackPredicate = this.itemStackPredicate;
        return mergedBonus;
    }

    @Override
    public MutableComponent getTooltip() {
        MutableComponent tooltip;
        String keySuffix = isPositive() ? "positive" : "negative";
        String multiplierString = TooltipHelper.formatNumber(Mth.abs(multiplier) * 100);
        String descriptionKey = getDescriptionId() + "." + keySuffix;
        Component itemConditionTooltip = itemStackPredicate.getTooltip("plural");
        tooltip = Component.translatable(descriptionKey, itemConditionTooltip, multiplierString);
        tooltip = playerMultiplier.getTooltip(tooltip, Target.PLAYER);
        tooltip = playerCondition.getTooltip(tooltip, Target.PLAYER);
        return tooltip.withStyle(TooltipHelper.getSkillBonusStyle(isPositive()));
    }

    @Override
    public boolean isPositive() {
        return multiplier > 0;
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, int row, Consumer<ItemUsageSpeedBonus> consumer) {
        editor.addLabel(0, 0, "Multiplier", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addNumericTextField(0, 0, 50, 14, multiplier).setNumericResponder(value -> selectMultiplier(consumer, value));
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

    private void selectMultiplier(Consumer<ItemUsageSpeedBonus> consumer, Double value) {
        setMultiplier(value.floatValue());
        consumer.accept(this.copy());
    }

    private void addPlayerMultiplierWidgets(SkillTreeEditor editor, Consumer<ItemUsageSpeedBonus> consumer) {
        playerMultiplier.addEditorWidgets(editor, multiplier -> {
            setPlayerMultiplier(multiplier);
            consumer.accept(this.copy());
        });
    }

    private void selectPlayerMultiplier(SkillTreeEditor editor, Consumer<ItemUsageSpeedBonus> consumer, LivingMultiplier multiplier) {
        setPlayerMultiplier(multiplier);
        consumer.accept(this.copy());
        editor.rebuildWidgets();
    }

    private void addPlayerConditionWidgets(SkillTreeEditor editor, Consumer<ItemUsageSpeedBonus> consumer) {
        playerCondition.addEditorWidgets(editor, c -> {
            setPlayerCondition(c);
            consumer.accept(this.copy());
        });
    }

    private void selectPlayerCondition(SkillTreeEditor editor, Consumer<ItemUsageSpeedBonus> consumer, LivingEntityPredicate condition) {
        setPlayerCondition(condition);
        consumer.accept(this.copy());
        editor.rebuildWidgets();
    }

    private void addItemConditionWidgets(SkillTreeEditor editor, Consumer<ItemUsageSpeedBonus> consumer) {
        itemStackPredicate.addEditorWidgets(editor, c -> {
            setItemCondition(c);
            consumer.accept(this.copy());
        });
    }

    private void selectItemCondition(SkillTreeEditor editor, Consumer<ItemUsageSpeedBonus> consumer, ItemStackPredicate condition) {
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

    public void setMultiplier(float multiplier) {
        this.multiplier = multiplier;
    }

    public static class Serializer implements SkillBonus.Serializer {
        @Override
        public ItemUsageSpeedBonus deserialize(JsonObject json) throws JsonParseException {
            float multiplier = SerializationHelper.getElement(json, "multiplier").getAsFloat();
            ItemUsageSpeedBonus bonus = new ItemUsageSpeedBonus(multiplier);
            bonus.playerMultiplier = SerializationHelper.deserializeLivingMultiplier(json, "player_multiplier");
            bonus.playerCondition = SerializationHelper.deserializeLivingCondition(json, "player_condition");
            bonus.itemStackPredicate = SerializationHelper.deserializeItemPredicate(json);
            return bonus;
        }

        @Override
        public void serialize(JsonObject json, SkillBonus<?> bonus) {
            if (!(bonus instanceof ItemUsageSpeedBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            json.addProperty("multiplier", aBonus.multiplier);
            SerializationHelper.serializeLivingMultiplier(json, aBonus.playerMultiplier, "player_multiplier");
            SerializationHelper.serializeLivingCondition(json, aBonus.playerCondition, "player_condition");
            SerializationHelper.serializeItemPredicate(json, aBonus.itemStackPredicate);
        }

        @Override
        public ItemUsageSpeedBonus deserialize(CompoundTag tag) {
            float multiplier = tag.getFloat("multiplier");
            ItemUsageSpeedBonus bonus = new ItemUsageSpeedBonus(multiplier);
            bonus.playerMultiplier = SerializationHelper.deserializeLivingMultiplier(tag, "player_multiplier");
            bonus.playerCondition = SerializationHelper.deserializeLivingCondition(tag, "player_condition");
            bonus.itemStackPredicate = SerializationHelper.deserializeItemPredicate(tag);
            return bonus;
        }

        @Override
        public CompoundTag serialize(SkillBonus<?> bonus) {
            if (!(bonus instanceof ItemUsageSpeedBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            CompoundTag tag = new CompoundTag();
            tag.putFloat("multiplier", aBonus.multiplier);
            SerializationHelper.serializeLivingMultiplier(tag, aBonus.playerMultiplier, "player_multiplier");
            SerializationHelper.serializeLivingCondition(tag, aBonus.playerCondition, "player_condition");
            SerializationHelper.serializeItemPredicate(tag, aBonus.itemStackPredicate);
            return tag;
        }

        @Override
        public ItemUsageSpeedBonus deserialize(FriendlyByteBuf buf) {
            float multiplier = buf.readFloat();
            ItemUsageSpeedBonus bonus = new ItemUsageSpeedBonus(multiplier);
            bonus.playerMultiplier = NetworkHelper.readLivingMultiplier(buf);
            bonus.playerCondition = NetworkHelper.readLivingCondition(buf);
            bonus.itemStackPredicate = NetworkHelper.readItemPredicate(buf);
            return bonus;
        }

        @Override
        public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
            if (!(bonus instanceof ItemUsageSpeedBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            buf.writeFloat(aBonus.multiplier);
            NetworkHelper.writeLivingMultiplier(buf, aBonus.playerMultiplier);
            NetworkHelper.writeLivingCondition(buf, aBonus.playerCondition);
            NetworkHelper.writeItemPredicate(buf, aBonus.itemStackPredicate);
        }

        @Override
        public SkillBonus<?> createDefaultInstance() {
            return new ItemUsageSpeedBonus(0.1f);
        }
    }
}
