package daripher.skilltree.compat.ironsspellbooks.skill.bonus;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.compat.ironsspellbooks.IronsSpellbooksCompat;
import daripher.skilltree.data.serializers.SerializationHelper;
import daripher.skilltree.network.NetworkHelper;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.multiplier.LivingMultiplier;
import daripher.skilltree.skill.bonus.multiplier.NoneLivingMultiplier;
import daripher.skilltree.skill.bonus.predicate.living.LivingEntityPredicate;
import daripher.skilltree.skill.bonus.predicate.living.NoneLivingEntityPredicate;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class SpellLevelSkillBonus implements SkillBonus<SpellLevelSkillBonus> {
    private @Nonnull LivingEntityPredicate playerCondition = NoneLivingEntityPredicate.INSTANCE;
    private @Nonnull LivingMultiplier playerMultiplier = NoneLivingMultiplier.INSTANCE;
    private @Nonnull ResourceLocation spellId;
    private int bonusLevels;

    public SpellLevelSkillBonus(@Nonnull ResourceLocation spellId, int spellLevel) {
        this.spellId = spellId;
        this.bonusLevels = spellLevel;
    }

    public int getBonusLevels(Player player) {
        if (!playerCondition.test(player)) {
            return 0;
        }
        return (int) (playerMultiplier.getValue(player) * bonusLevels);
    }

    public @Nonnull ResourceLocation getSpellId() {
        return spellId;
    }

    @Override
    public SkillBonus.Serializer getSerializer() {
        return IronsSpellbooksCompat.SPELL_LEVEL_BONUS.get();
    }

    @Override
    public SpellLevelSkillBonus copy() {
        SpellLevelSkillBonus bonus = new SpellLevelSkillBonus(spellId, bonusLevels);
        bonus.playerCondition = this.playerCondition;
        bonus.playerMultiplier = this.playerMultiplier;
        return bonus;
    }

    @Override
    public SpellLevelSkillBonus multiply(double multiplier) {
        SpellLevelSkillBonus bonus = copy();
        bonus.setBonusLevels((int) (bonusLevels * multiplier));
        return bonus;
    }

    @Override
    public boolean canMerge(SkillBonus<?> other) {
        if (!(other instanceof SpellLevelSkillBonus otherBonus)) {
            return false;
        }
        if (!Objects.equals(otherBonus.playerMultiplier, this.playerMultiplier)) {
            return false;
        }
        if (!Objects.equals(otherBonus.playerCondition, this.playerCondition)) {
            return false;
        }
        return otherBonus.getSpellId().equals(this.getSpellId());
    }

    @Override
    public SkillBonus<SpellLevelSkillBonus> merge(SkillBonus<?> other) {
        if (!(other instanceof SpellLevelSkillBonus otherBonus)) {
            throw new IllegalArgumentException();
        }
        SpellLevelSkillBonus bonus = copy();
        bonus.setBonusLevels(otherBonus.bonusLevels + this.bonusLevels);
        return bonus;
    }

    @Override
    public MutableComponent getTooltip() {
        LocalPlayer clientPlayer = Minecraft.getInstance().player;
        Component spellName = SpellRegistry.getSpell(spellId).getDisplayName(clientPlayer);
        AttributeModifier.Operation operation = AttributeModifier.Operation.ADDITION;
        MutableComponent tooltip = Component.translatable(getDescriptionId(), spellName);
        tooltip = TooltipHelper.getSkillBonusTooltip(tooltip, bonusLevels, operation);
        tooltip = playerCondition.getTooltip(tooltip, Target.PLAYER);
        tooltip = playerMultiplier.getTooltip(tooltip, Target.PLAYER);
        return tooltip.withStyle(TooltipHelper.getSkillBonusStyle(isPositive()));
    }

    @Override
    public boolean isPositive() {
        return bonusLevels > 0;
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, int row, Consumer<SpellLevelSkillBonus> consumer) {
        editor.addLabel(0, 0, "Spell", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        List<ResourceLocation> spellIds = SpellRegistry.getEnabledSpells().stream().map(AbstractSpell::getSpellId)
                .map(ResourceLocation::parse).toList();
        editor.addSelectionMenu(0, 0, 200, spellIds).setValue(spellId)
                .setElementNameGetter(spellId -> Component.literal(spellId.toString()))
                .setResponder(spellId -> selectSpellId(editor, consumer, spellId));
        editor.increaseHeight(19);
        editor.addLabel(0, 0, "Bonus Levels", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addNumericTextField(0, 0, 50, 14, bonusLevels).setNumericFilter(v -> v != 0 && v % 1 == 0)
                .setNumericResponder(spellLevel -> selectBonusLevels(consumer, spellLevel));
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

    private void selectBonusLevels(Consumer<SpellLevelSkillBonus> consumer, Double spellLevel) {
        setBonusLevels(spellLevel.intValue());
        consumer.accept(this);
    }

    public void setBonusLevels(int bonusLevels) {
        this.bonusLevels = bonusLevels;
    }

    private void selectSpellId(SkillTreeEditor editor, Consumer<SpellLevelSkillBonus> consumer, ResourceLocation spellId) {
        setSpellId(spellId);
        consumer.accept(this.copy());
        editor.rebuildWidgets();
    }

    public void setSpellId(@Nonnull ResourceLocation spellId) {
        this.spellId = spellId;
    }

    private void addPlayerConditionWidgets(SkillTreeEditor editor, Consumer<SpellLevelSkillBonus> consumer) {
        playerCondition.addEditorWidgets(editor, c -> {
            setPlayerCondition(c);
            consumer.accept(this.copy());
        });
    }

    private void selectPlayerCondition(SkillTreeEditor editor, Consumer<SpellLevelSkillBonus> consumer, LivingEntityPredicate condition) {
        setPlayerCondition(condition);
        consumer.accept(this.copy());
        editor.rebuildWidgets();
    }

    public SkillBonus<?> setPlayerCondition(LivingEntityPredicate condition) {
        this.playerCondition = condition;
        return this;
    }

    private void addPlayerMultiplierWidgets(SkillTreeEditor editor, Consumer<SpellLevelSkillBonus> consumer) {
        playerMultiplier.addEditorWidgets(editor, m -> {
            setMultiplier(m);
            consumer.accept(this.copy());
        });
    }

    private void selectPlayerMultiplier(SkillTreeEditor editor, Consumer<SpellLevelSkillBonus> consumer, LivingMultiplier multiplier) {
        setMultiplier(multiplier);
        consumer.accept(this.copy());
        editor.rebuildWidgets();
    }

    public SkillBonus<?> setMultiplier(LivingMultiplier multiplier) {
        this.playerMultiplier = multiplier;
        return this;
    }

    @Nonnull
    public LivingEntityPredicate getPlayerCondition() {
        return playerCondition;
    }

    public static class Serializer implements SkillBonus.Serializer {
        @Override
        public SpellLevelSkillBonus deserialize(JsonObject json) throws JsonParseException {
            ResourceLocation spellId = ResourceLocation.parse(json.get("spell_id").getAsString());
            int bonusLevels = json.get("bonus_levels").getAsInt();
            SpellLevelSkillBonus bonus = new SpellLevelSkillBonus(spellId, bonusLevels);
            bonus.playerCondition = SerializationHelper.deserializeLivingCondition(json, "player_condition");
            bonus.playerMultiplier = SerializationHelper.deserializeLivingMultiplier(json, "player_multiplier");
            return bonus;
        }

        @Override
        public void serialize(JsonObject json, SkillBonus<?> bonus) {
            if (!(bonus instanceof SpellLevelSkillBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            json.addProperty("spell_id", aBonus.spellId.toString());
            json.addProperty("bonus_levels", aBonus.bonusLevels);
            SerializationHelper.serializeLivingCondition(json, aBonus.playerCondition, "player_condition");
            SerializationHelper.serializeLivingMultiplier(json, aBonus.playerMultiplier, "player_multiplier");
        }

        @Override
        public SpellLevelSkillBonus deserialize(CompoundTag tag) {
            ResourceLocation spellId = ResourceLocation.parse(tag.getString("spell_id"));
            int bonusLevels = tag.getInt("bonus_levels");
            SpellLevelSkillBonus bonus = new SpellLevelSkillBonus(spellId, bonusLevels);
            bonus.playerCondition = SerializationHelper.deserializeLivingCondition(tag, "player_condition");
            bonus.playerMultiplier = SerializationHelper.deserializeLivingMultiplier(tag, "player_multiplier");
            return bonus;
        }

        @Override
        public CompoundTag serialize(SkillBonus<?> bonus) {
            if (!(bonus instanceof SpellLevelSkillBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            CompoundTag tag = new CompoundTag();
            tag.putString("spell_id", aBonus.spellId.toString());
            tag.putInt("bonus_levels", aBonus.bonusLevels);
            SerializationHelper.serializeLivingCondition(tag, aBonus.playerCondition, "player_condition");
            SerializationHelper.serializeLivingMultiplier(tag, aBonus.playerMultiplier, "player_multiplier");
            return tag;
        }

        @Override
        public SpellLevelSkillBonus deserialize(FriendlyByteBuf buf) {
            ResourceLocation spellId = buf.readResourceLocation();
            int bonusLevels = buf.readInt();
            SpellLevelSkillBonus bonus = new SpellLevelSkillBonus(spellId, bonusLevels);
            bonus.playerCondition = NetworkHelper.readLivingCondition(buf);
            bonus.playerMultiplier = NetworkHelper.readLivingMultiplier(buf);
            return bonus;
        }

        @Override
        public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
            if (!(bonus instanceof SpellLevelSkillBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            buf.writeResourceLocation(aBonus.spellId);
            buf.writeInt(aBonus.bonusLevels);
            NetworkHelper.writeLivingCondition(buf, aBonus.playerCondition);
            NetworkHelper.writeLivingMultiplier(buf, aBonus.playerMultiplier);
        }

        @Override
        public SkillBonus<?> createDefaultInstance() {
            return new SpellLevelSkillBonus(ResourceLocation.parse("irons_spellbooks:teleport"), 1);
        }
    }
}
