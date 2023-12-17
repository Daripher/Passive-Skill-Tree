package daripher.skilltree.skill.bonus.multiplier;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.client.screen.SkillTreeEditorScreen;
import daripher.skilltree.compat.apotheosis.ApotheosisCompatibility;
import daripher.skilltree.data.SerializationHelper;
import daripher.skilltree.entity.player.PlayerHelper;
import daripher.skilltree.init.PSTItemConditions;
import daripher.skilltree.init.PSTLivingMultipliers;
import daripher.skilltree.item.gem.GemBonusHandler;
import daripher.skilltree.network.NetworkHelper;
import daripher.skilltree.skill.bonus.condition.item.EquipmentCondition;
import daripher.skilltree.skill.bonus.condition.item.ItemCondition;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public final class GemsAmountMultiplier implements LivingMultiplier {
  private @Nonnull ItemCondition itemCondition;

  public GemsAmountMultiplier(@Nonnull ItemCondition itemCondition) {
    this.itemCondition = itemCondition;
  }

  @Override
  public float getValue(LivingEntity entity) {
    return getGems(PlayerHelper.getAllEquipment(entity).filter(itemCondition::met));
  }

  private int getGems(Stream<ItemStack> items) {
    Function<ItemStack, List<ItemStack>> mapper =
        SkillTreeMod.apotheosisEnabled()
            ? ApotheosisCompatibility.INSTANCE::getGems
            : GemBonusHandler::getGems;
    return items.map(mapper).map(List::size).reduce(Integer::sum).orElse(0);
  }

  @Override
  public MutableComponent getTooltip(MutableComponent bonusTooltip) {
    Component itemDescription = itemCondition.getTooltip("where");
    return Component.translatable(getDescriptionId(), bonusTooltip, itemDescription);
  }

  @Override
  public void addEditorWidgets(SkillTreeEditorScreen editor, Consumer<LivingMultiplier> consumer) {
    editor.addLabel(0, 0, "Item Condition", ChatFormatting.GREEN);
    editor.shiftWidgets(0, 19);
    editor
        .addDropDownList(0, 0, 200, 14, 10, itemCondition, PSTItemConditions.conditionsList())
        .setToNameFunc(c -> Component.literal(PSTItemConditions.getName(c)))
        .setResponder(
            c -> {
              setItemCondition(c);
              consumer.accept(this);
              editor.rebuildWidgets();
            });
    editor.shiftWidgets(0, 19);
    itemCondition.addEditorWidgets(
        editor,
        c -> {
          setItemCondition(c);
          consumer.accept(this);
        });
  }

  @Override
  public LivingMultiplier.Serializer getSerializer() {
    return PSTLivingMultipliers.GEMS_AMOUNT.get();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    GemsAmountMultiplier that = (GemsAmountMultiplier) o;
    return Objects.equals(itemCondition, that.itemCondition);
  }

  @Override
  public int hashCode() {
    return Objects.hash(itemCondition);
  }

  public void setItemCondition(@Nonnull ItemCondition itemCondition) {
    this.itemCondition = itemCondition;
  }

  @Override
  public String toString() {
    return "GemsAmountMultiplier[" + "itemCondition=" + itemCondition + ']';
  }

  public static class Serializer implements LivingMultiplier.Serializer {
    @Override
    public LivingMultiplier deserialize(JsonObject json) throws JsonParseException {
      ItemCondition itemCondition = SerializationHelper.deserializeItemCondition(json);
      return new GemsAmountMultiplier(itemCondition);
    }

    @Override
    public void serialize(JsonObject json, LivingMultiplier multiplier) {
      if (!(multiplier instanceof GemsAmountMultiplier aMultiplier)) {
        throw new IllegalArgumentException();
      }
      SerializationHelper.serializeItemCondition(json, aMultiplier.itemCondition);
    }

    @Override
    public LivingMultiplier deserialize(CompoundTag tag) {
      ItemCondition itemCondition = SerializationHelper.deserializeItemCondition(tag);
      return new GemsAmountMultiplier(itemCondition);
    }

    @Override
    public CompoundTag serialize(LivingMultiplier multiplier) {
      if (!(multiplier instanceof GemsAmountMultiplier aMultiplier)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      SerializationHelper.serializeItemCondition(tag, aMultiplier.itemCondition);
      return tag;
    }

    @Override
    public LivingMultiplier deserialize(FriendlyByteBuf buf) {
      return new GemsAmountMultiplier(NetworkHelper.readItemCondition(buf));
    }

    @Override
    public void serialize(FriendlyByteBuf buf, LivingMultiplier multiplier) {
      if (!(multiplier instanceof GemsAmountMultiplier aMultiplier)) {
        throw new IllegalArgumentException();
      }
      NetworkHelper.writeItemCondition(buf, aMultiplier.itemCondition);
    }

    @Override
    public LivingMultiplier createDefaultInstance() {
      return new GemsAmountMultiplier(new EquipmentCondition());
    }
  }
}
