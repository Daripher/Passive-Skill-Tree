package daripher.skilltree.compat.ironsspellbooks.skill.bonus;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.compat.ironsspellbooks.IronsSpellbooksCompat;
import daripher.skilltree.data.serializers.SerializationHelper;
import daripher.skilltree.network.NetworkHelper;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.TickingSkillBonus;
import daripher.skilltree.skill.bonus.predicate.living.LivingEntityPredicate;
import daripher.skilltree.skill.bonus.predicate.living.NoneLivingEntityPredicate;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.SpellData;
import io.redspace.ironsspellbooks.network.EquipmentChangedPacket;
import io.redspace.ironsspellbooks.setup.PacketDistributor;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class GrantSpellSkillBonus implements SkillBonus<GrantSpellSkillBonus>, TickingSkillBonus {
    private @Nonnull LivingEntityPredicate playerCondition = NoneLivingEntityPredicate.INSTANCE;
    private @Nonnull ResourceLocation spellId;
    private int spellLevel;
    private final UUID uuid = UUID.randomUUID();

    public GrantSpellSkillBonus(@Nonnull ResourceLocation spellId, int spellLevel) {
        this.spellId = spellId;
        this.spellLevel = spellLevel;
    }

    @Override
    public void tick(ServerPlayer player) {
        if (playerCondition.test(player) && !IronsSpellbooksCompat.INSTANCE.hasPlayerSpell(player, uuid)) {
            IronsSpellbooksCompat.INSTANCE.addPlayerSpell(player, uuid);
            PacketDistributor.sendToPlayer(player, new EquipmentChangedPacket());
            return;
        }
        if (!playerCondition.test(player) && IronsSpellbooksCompat.INSTANCE.hasPlayerSpell(player, uuid)) {
            IronsSpellbooksCompat.INSTANCE.removePlayerSpell(player, uuid);
            PacketDistributor.sendToPlayer(player, new EquipmentChangedPacket());
        }
    }

    @Nullable
    public SpellData getSpellData() {
        return new SpellData(SpellRegistry.getSpell(spellId), spellLevel);
    }

    @Override
    public SkillBonus.Serializer getSerializer() {
        return IronsSpellbooksCompat.GRANT_SPELL_BONUS.get();
    }

    @Override
    public GrantSpellSkillBonus copy() {
        GrantSpellSkillBonus bonus = new GrantSpellSkillBonus(spellId, spellLevel);
        bonus.playerCondition = this.playerCondition;
        return bonus;
    }

    @Override
    public GrantSpellSkillBonus multiply(double multiplier) {
        return this;
    }

    @Override
    public boolean canMerge(SkillBonus<?> other) {
        return false;
    }

    @Override
    public SkillBonus<GrantSpellSkillBonus> merge(SkillBonus<?> other) {
        return this;
    }

    @Override
    public MutableComponent getTooltip() {
        LocalPlayer clientPlayer = Minecraft.getInstance().player;
        Component spellName = SpellRegistry.getSpell(spellId).getDisplayName(clientPlayer);
        MutableComponent tooltip = Component.translatable(getDescriptionId(), spellName, spellLevel);
        tooltip = playerCondition.getTooltip(tooltip, Target.PLAYER);
        return tooltip.withStyle(TooltipHelper.getSkillBonusStyle(isPositive()));
    }

    @Override
    public boolean isPositive() {
        return true;
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, int row, Consumer<GrantSpellSkillBonus> consumer) {
        editor.addLabel(0, 0, "Spell", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        List<ResourceLocation> spellIds = SpellRegistry.getEnabledSpells().stream().map(AbstractSpell::getSpellId)
                .map(ResourceLocation::parse).toList();
        editor.addSelectionMenu(0, 0, 200, spellIds).setValue(spellId)
                .setElementNameGetter(spellId -> Component.literal(spellId.toString()))
                .setResponder(spellId -> selectSpellId(editor, consumer, spellId));
        editor.increaseHeight(19);
        editor.addLabel(0, 0, "Level", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addNumericTextField(0, 0, 50, 14, spellLevel).setNumericResponder(spellLevel -> selectSpellLevel(consumer, spellLevel));
        editor.increaseHeight(19);
        editor.addLabel(0, 0, "Player Condition", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addSelectionMenu(0, 0, 200, playerCondition).setResponder(condition -> selectPlayerCondition(editor, consumer, condition))
                .setMenuInitFunc(() -> addPlayerConditionWidgets(editor, consumer));
        editor.increaseHeight(19);
    }

    private void selectSpellLevel(Consumer<GrantSpellSkillBonus> consumer, Double spellLevel) {
        setSpellLevel(spellLevel.intValue());
        consumer.accept(this);
    }

    public void setSpellLevel(int spellLevel) {
        this.spellLevel = spellLevel;
    }

    private void selectSpellId(SkillTreeEditor editor, Consumer<GrantSpellSkillBonus> consumer, ResourceLocation spellId) {
        setSpellId(spellId);
        consumer.accept(this.copy());
        editor.rebuildWidgets();
    }

    public void setSpellId(@Nonnull ResourceLocation spellId) {
        this.spellId = spellId;
    }

    private void addPlayerConditionWidgets(SkillTreeEditor editor, Consumer<GrantSpellSkillBonus> consumer) {
        playerCondition.addEditorWidgets(editor, c -> {
            setPlayerCondition(c);
            consumer.accept(this.copy());
        });
    }

    private void selectPlayerCondition(SkillTreeEditor editor, Consumer<GrantSpellSkillBonus> consumer, LivingEntityPredicate condition) {
        setPlayerCondition(condition);
        consumer.accept(this.copy());
        editor.rebuildWidgets();
    }

    public SkillBonus<?> setPlayerCondition(LivingEntityPredicate condition) {
        this.playerCondition = condition;
        return this;
    }

    @Nonnull
    public LivingEntityPredicate getPlayerCondition() {
        return playerCondition;
    }

    public static class Serializer implements SkillBonus.Serializer {
        @Override
        public GrantSpellSkillBonus deserialize(JsonObject json) throws JsonParseException {
            ResourceLocation spellId = ResourceLocation.parse(json.get("spell_id").getAsString());
            int spellLevel = json.get("spell_level").getAsInt();
            GrantSpellSkillBonus bonus = new GrantSpellSkillBonus(spellId, spellLevel);
            bonus.playerCondition = SerializationHelper.deserializeLivingCondition(json, "player_condition");
            return bonus;
        }

        @Override
        public void serialize(JsonObject json, SkillBonus<?> bonus) {
            if (!(bonus instanceof GrantSpellSkillBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            json.addProperty("spell_id", aBonus.spellId.toString());
            json.addProperty("spell_level", aBonus.spellLevel);
            SerializationHelper.serializeLivingCondition(json, aBonus.playerCondition, "player_condition");
        }

        @Override
        public GrantSpellSkillBonus deserialize(CompoundTag tag) {
            ResourceLocation spellId = ResourceLocation.parse(tag.getString("spell_id"));
            int spellLevel = tag.getInt("spell_level");
            GrantSpellSkillBonus bonus = new GrantSpellSkillBonus(spellId, spellLevel);
            bonus.playerCondition = SerializationHelper.deserializeLivingCondition(tag, "player_condition");
            return bonus;
        }

        @Override
        public CompoundTag serialize(SkillBonus<?> bonus) {
            if (!(bonus instanceof GrantSpellSkillBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            CompoundTag tag = new CompoundTag();
            tag.putString("spell_id", aBonus.spellId.toString());
            tag.putInt("spell_level", aBonus.spellLevel);
            SerializationHelper.serializeLivingCondition(tag, aBonus.playerCondition, "player_condition");
            return tag;
        }

        @Override
        public GrantSpellSkillBonus deserialize(FriendlyByteBuf buf) {
            ResourceLocation spellId = buf.readResourceLocation();
            int spellLevel = buf.readInt();
            GrantSpellSkillBonus bonus = new GrantSpellSkillBonus(spellId, spellLevel);
            bonus.playerCondition = NetworkHelper.readLivingCondition(buf);
            return bonus;
        }

        @Override
        public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
            if (!(bonus instanceof GrantSpellSkillBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            buf.writeResourceLocation(aBonus.spellId);
            buf.writeInt(aBonus.spellLevel);
            NetworkHelper.writeLivingCondition(buf, aBonus.playerCondition);
        }

        @Override
        public SkillBonus<?> createDefaultInstance() {
            return new GrantSpellSkillBonus(ResourceLocation.parse("irons_spellbooks:teleport"), 1);
        }
    }
}
