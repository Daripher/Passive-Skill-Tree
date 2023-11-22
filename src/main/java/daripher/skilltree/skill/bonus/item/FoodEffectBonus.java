package daripher.skilltree.skill.bonus.item;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.screen.SkillTreeEditor;
import daripher.skilltree.data.SerializationHelper;
import daripher.skilltree.effect.SkillBonusEffect;
import daripher.skilltree.init.PSTItemBonuses;
import daripher.skilltree.network.NetworkHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;

public record FoodEffectBonus(MobEffectInstance effect) implements ItemBonus<FoodEffectBonus> {
  @Override
  public boolean canMerge(ItemBonus<?> other) {
    if (!(other instanceof FoodEffectBonus otherBonus)) return false;
    return otherBonus.effect.getEffect() == effect.getEffect();
  }

  @Override
  public FoodEffectBonus merge(ItemBonus<?> other) {
    if (!(other instanceof FoodEffectBonus otherBonus)) {
      throw new IllegalArgumentException();
    }
    int amplifier = effect.getAmplifier() + otherBonus.effect.getAmplifier() + 1;
    return new FoodEffectBonus(
        new MobEffectInstance(effect.getEffect(), effect.getDuration(), amplifier));
  }

  @Override
  public FoodEffectBonus copy() {
    return new FoodEffectBonus(new MobEffectInstance(effect));
  }

  @Override
  public FoodEffectBonus multiply(double multiplier) {
    int amplifier = (int) (effect.getAmplifier() * multiplier);
    return new FoodEffectBonus(
        new MobEffectInstance(effect.getEffect(), effect.getDuration(), amplifier));
  }

  @Override
  public ItemBonus.Serializer getSerializer() {
    return PSTItemBonuses.FOOD_EFFECT.get();
  }

  @Override
  public MutableComponent getTooltip() {
    Component effectDescription = null;
    if (effect.getEffect() instanceof SkillBonusEffect skillEffect) {
      effectDescription =
          skillEffect
              .getBonus()
              .copy()
              .multiply(effect.getAmplifier() + 1)
              .getTooltip()
              .setStyle(Style.EMPTY);
    } else {
      effectDescription = effect.getEffect().getDisplayName();
      if (effect.getAmplifier() > 0) {
        MutableComponent amplifier =
            Component.translatable("potion.potency." + effect.getAmplifier());
        effectDescription =
            Component.translatable("potion.withAmplifier", effectDescription, amplifier);
      }
    }
    String timeDescription = MobEffectUtil.formatDuration(effect, 1f);
    return Component.translatable(getDescriptionId(), effectDescription, timeDescription);
  }

  @Override
  public void addEditorWidgets(SkillTreeEditor editor, int row) {
    // TODO
  }

  public static class Serializer implements ItemBonus.Serializer {
    @Override
    public ItemBonus<?> deserialize(JsonObject json) throws JsonParseException {
      return new FoodEffectBonus(SerializationHelper.deserializeEffectInstance(json));
    }

    @Override
    public void serialize(JsonObject json, ItemBonus<?> bonus) {
      if (!(bonus instanceof FoodEffectBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      SerializationHelper.serializeEffectInstance(json, aBonus.effect);
    }

    @Override
    public ItemBonus<?> deserialize(CompoundTag tag) {
      return new FoodEffectBonus(SerializationHelper.deserializeEffectInstance(tag));
    }

    @Override
    public CompoundTag serialize(ItemBonus<?> bonus) {
      if (!(bonus instanceof FoodEffectBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      SerializationHelper.serializeEffectInstance(tag, aBonus.effect);
      return tag;
    }

    @Override
    public ItemBonus<?> deserialize(FriendlyByteBuf buf) {
      return new FoodEffectBonus(NetworkHelper.readEffectInstance(buf));
    }

    @Override
    public void serialize(FriendlyByteBuf buf, ItemBonus<?> bonus) {
      if (!(bonus instanceof FoodEffectBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      NetworkHelper.writeEffectInstance(buf, aBonus.effect);
    }
  }
}
