package daripher.skilltree.skill.bonus.condition.living;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.screen.SkillTreeEditorScreen;
import daripher.skilltree.data.SerializationHelper;
import daripher.skilltree.entity.player.PlayerHelper;
import daripher.skilltree.init.PSTLivingConditions;
import daripher.skilltree.network.NetworkHelper;
import daripher.skilltree.skill.bonus.condition.item.WeaponCondition;
import java.util.Objects;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;

public final class DualWieldingCondition implements LivingCondition {
  private @Nonnull WeaponCondition weaponCondition;

  public DualWieldingCondition(@Nonnull WeaponCondition weaponCondition) {
    this.weaponCondition = weaponCondition;
  }

  @Override
  public boolean met(LivingEntity living) {
    return PlayerHelper.getItemsInHands(living).allMatch(weaponCondition::met);
  }

  @Override
  public MutableComponent getTooltip(MutableComponent bonusTooltip, String target) {
    String key = getDescriptionId();
    Component targetDescription = Component.translatable("%s.target.%s".formatted(key, target));
    Component itemDescription = weaponCondition.getTooltip();
    return Component.translatable(key, bonusTooltip, targetDescription, itemDescription);
  }

  @Override
  public LivingCondition.Serializer getSerializer() {
    return PSTLivingConditions.DUAL_WIELDING.get();
  }

  @Override
  public void addEditorWidgets(SkillTreeEditorScreen editor, Consumer<LivingCondition> consumer) {
    weaponCondition.addEditorWidgets(
        editor,
        c -> {
          setWeaponCondition((WeaponCondition) c);
          consumer.accept(this);
        });
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    DualWieldingCondition that = (DualWieldingCondition) o;
    return Objects.equals(weaponCondition, that.weaponCondition);
  }

  @Override
  public int hashCode() {
    return Objects.hash(weaponCondition);
  }

  public void setWeaponCondition(@Nonnull WeaponCondition weaponCondition) {
    this.weaponCondition = weaponCondition;
  }

  public static class Serializer implements LivingCondition.Serializer {
    @Override
    public LivingCondition deserialize(JsonObject json) throws JsonParseException {
      return new DualWieldingCondition(
          (WeaponCondition) SerializationHelper.deserializeItemCondition(json));
    }

    @Override
    public void serialize(JsonObject json, LivingCondition condition) {
      if (!(condition instanceof DualWieldingCondition aCondition)) {
        throw new IllegalArgumentException();
      }
      SerializationHelper.serializeItemCondition(json, aCondition.weaponCondition);
    }

    @Override
    public LivingCondition deserialize(CompoundTag tag) {
      return new DualWieldingCondition(
          (WeaponCondition) SerializationHelper.deserializeItemCondition(tag));
    }

    @Override
    public CompoundTag serialize(LivingCondition condition) {
      if (!(condition instanceof DualWieldingCondition aCondition)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      SerializationHelper.serializeItemCondition(tag, aCondition.weaponCondition);
      return tag;
    }

    @Override
    public LivingCondition deserialize(FriendlyByteBuf buf) {
      return new DualWieldingCondition((WeaponCondition) NetworkHelper.readItemCondition(buf));
    }

    @Override
    public void serialize(FriendlyByteBuf buf, LivingCondition condition) {
      if (!(condition instanceof DualWieldingCondition aCondition)) {
        throw new IllegalArgumentException();
      }
      NetworkHelper.writeItemCondition(buf, aCondition.weaponCondition);
    }

    @Override
    public LivingCondition createDefaultInstance() {
      return new DualWieldingCondition(new WeaponCondition(WeaponCondition.Type.ANY));
    }
  }
}
