package daripher.skilltree.skill.bonus.predicate.living;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.data.serializers.SerializationHelper;
import daripher.skilltree.entity.player.PlayerHelper;
import daripher.skilltree.init.PSTLivingConditions;
import daripher.skilltree.network.NetworkHelper;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.predicate.item.EquipmentPredicate;
import daripher.skilltree.skill.bonus.predicate.item.ItemStackPredicate;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.function.Consumer;

public final class DualWieldingEntityPredicate implements LivingEntityPredicate {
  private @Nonnull ItemStackPredicate weaponCondition;

  public DualWieldingEntityPredicate(@Nonnull ItemStackPredicate weaponCondition) {
    this.weaponCondition = weaponCondition;
  }

  @Override
  public boolean test(LivingEntity living) {
    return PlayerHelper.getItemsInHands(living).allMatch(weaponCondition);
  }

  @Override
  public MutableComponent getTooltip(MutableComponent bonusTooltip, SkillBonus.Target target) {
    String key = getDescriptionId();
    Component targetDescription = Component.translatable("%s.target.%s".formatted(key, target.getName()));
    Component itemDescription = weaponCondition.getTooltip();
    return Component.translatable(key, bonusTooltip, targetDescription, itemDescription);
  }

  @Override
  public LivingEntityPredicate.Serializer getSerializer() {
    return PSTLivingConditions.DUAL_WIELDING.get();
  }

  @Override
  public void addEditorWidgets(SkillTreeEditor editor, Consumer<LivingEntityPredicate> consumer) {
    weaponCondition.addEditorWidgets(
        editor,
        c -> {
          setWeaponCondition(c);
          consumer.accept(this);
        });
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    DualWieldingEntityPredicate that = (DualWieldingEntityPredicate) o;
    return Objects.equals(weaponCondition, that.weaponCondition);
  }

  @Override
  public int hashCode() {
    return Objects.hash(weaponCondition);
  }

  public void setWeaponCondition(@Nonnull ItemStackPredicate weaponCondition) {
    this.weaponCondition = weaponCondition;
  }

  public static class Serializer implements LivingEntityPredicate.Serializer {
    @Override
    public LivingEntityPredicate deserialize(JsonObject json) throws JsonParseException {
      return new DualWieldingEntityPredicate(SerializationHelper.deserializeItemCondition(json));
    }

    @Override
    public void serialize(JsonObject json, LivingEntityPredicate condition) {
      if (!(condition instanceof DualWieldingEntityPredicate aCondition)) {
        throw new IllegalArgumentException();
      }
      SerializationHelper.serializeItemCondition(json, aCondition.weaponCondition);
    }

    @Override
    public LivingEntityPredicate deserialize(CompoundTag tag) {
      return new DualWieldingEntityPredicate(SerializationHelper.deserializeItemCondition(tag));
    }

    @Override
    public CompoundTag serialize(LivingEntityPredicate condition) {
      if (!(condition instanceof DualWieldingEntityPredicate aCondition)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      SerializationHelper.serializeItemCondition(tag, aCondition.weaponCondition);
      return tag;
    }

    @Override
    public LivingEntityPredicate deserialize(FriendlyByteBuf buf) {
      return new DualWieldingEntityPredicate(NetworkHelper.readItemCondition(buf));
    }

    @Override
    public void serialize(FriendlyByteBuf buf, LivingEntityPredicate condition) {
      if (!(condition instanceof DualWieldingEntityPredicate aCondition)) {
        throw new IllegalArgumentException();
      }
      NetworkHelper.writeItemCondition(buf, aCondition.weaponCondition);
    }

    @Override
    public LivingEntityPredicate createDefaultInstance() {
      return new DualWieldingEntityPredicate(new EquipmentPredicate(EquipmentPredicate.Type.WEAPON));
    }
  }
}
