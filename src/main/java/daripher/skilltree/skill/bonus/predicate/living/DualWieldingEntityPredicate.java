package daripher.skilltree.skill.bonus.predicate.living;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.data.serializers.SerializationHelper;
import daripher.skilltree.entity.player.PlayerHelper;
import daripher.skilltree.init.PSTLivingEntityPredicates;
import daripher.skilltree.network.NetworkHelper;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.predicate.item.EquipmentPredicate;
import daripher.skilltree.skill.bonus.predicate.item.ItemStackPredicate;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.function.Consumer;

public final class DualWieldingEntityPredicate implements LivingEntityPredicate {
    private @Nonnull ItemStackPredicate weaponPredicate;

    public DualWieldingEntityPredicate(@Nonnull ItemStackPredicate weaponPredicate) {
        this.weaponPredicate = weaponPredicate;
    }

    @Override
    public boolean test(LivingEntity living) {
        return PlayerHelper.getItemsInHands(living).allMatch(weaponPredicate);
    }

    @Override
    public MutableComponent getTooltip(MutableComponent bonusTooltip, SkillBonus.Target target) {
        String key = getDescriptionId();
        Component targetDescription = Component.translatable("%s.target.%s".formatted(key, target.getName()));
        Component itemDescription = weaponPredicate.getTooltip();
        return Component.translatable(key, bonusTooltip, targetDescription, itemDescription);
    }

    @Override
    public LivingEntityPredicate.Serializer getSerializer() {
        return PSTLivingEntityPredicates.DUAL_WIELDING.get();
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, Consumer<LivingEntityPredicate> consumer) {
        weaponPredicate.addEditorWidgets(editor, c -> {
            setWeaponPredicate(c);
            consumer.accept(this);
        });
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DualWieldingEntityPredicate that = (DualWieldingEntityPredicate) o;
        return Objects.equals(weaponPredicate, that.weaponPredicate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(weaponPredicate);
    }

    public void setWeaponPredicate(@Nonnull ItemStackPredicate weaponPredicate) {
        this.weaponPredicate = weaponPredicate;
    }

    public static class Serializer implements LivingEntityPredicate.Serializer {
        @Override
        public LivingEntityPredicate deserialize(JsonObject json) throws JsonParseException {
            return new DualWieldingEntityPredicate(SerializationHelper.deserializeItemPredicate(json));
        }

        @Override
        public void serialize(JsonObject json, LivingEntityPredicate predicate) {
            DualWieldingEntityPredicate validPredicate = validatePredicate(predicate);
            SerializationHelper.serializeItemPredicate(json, validPredicate.weaponPredicate);
        }

        @Override
        public LivingEntityPredicate deserialize(CompoundTag tag) {
            return new DualWieldingEntityPredicate(SerializationHelper.deserializeItemPredicate(tag));
        }

        @Override
        public CompoundTag serialize(LivingEntityPredicate predicate) {
            DualWieldingEntityPredicate validPredicate = validatePredicate(predicate);
            CompoundTag tag = new CompoundTag();
            SerializationHelper.serializeItemPredicate(tag, validPredicate.weaponPredicate);
            return tag;
        }

        @Override
        public LivingEntityPredicate deserialize(FriendlyByteBuf buf) {
            return new DualWieldingEntityPredicate(NetworkHelper.readItemPredicate(buf));
        }

        @Override
        public void serialize(FriendlyByteBuf buf, LivingEntityPredicate predicate) {
            DualWieldingEntityPredicate validPredicate = validatePredicate(predicate);
            NetworkHelper.writeItemPredicate(buf, validPredicate.weaponPredicate);
        }

        private static @NotNull DualWieldingEntityPredicate validatePredicate(LivingEntityPredicate predicate) {
            if (!(predicate instanceof DualWieldingEntityPredicate validPredicate)) {
                throw new IllegalArgumentException("Expected DualWieldingEntityPredicate, got: " + predicate);
            }
            return validPredicate;
        }

        @Override
        public LivingEntityPredicate createDefaultInstance() {
            return new DualWieldingEntityPredicate(new EquipmentPredicate(EquipmentPredicate.Type.WEAPON));
        }
    }
}
