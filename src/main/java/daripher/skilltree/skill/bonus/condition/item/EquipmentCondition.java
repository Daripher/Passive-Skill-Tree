package daripher.skilltree.skill.bonus.condition.item;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.screen.SkillTreeEditorScreen;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.init.PSTItemConditions;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;

public class EquipmentCondition implements ItemCondition {
  public Type type;

  public EquipmentCondition(Type type) {
    this.type = type;
  }

  @Override
  public boolean met(ItemStack stack) {
    return switch (type) {
      case ARMOR -> isArmor(stack);
      case AXE -> isAxe(stack);
      case BOOTS -> isBoots(stack);
      case BOW -> isBow(stack);
      case HOE -> isHoe(stack);
      case TOOL -> isTool(stack);
      case SWORD -> isSword(stack);
      case HELMET -> isHelmet(stack);
      case SHIELD -> isShield(stack);
      case SHOVEL -> isShovel(stack);
      case CHESTPLATE -> isChestplate(stack);
      case WEAPON -> isWeapon(stack);
      case CROSSBOW -> isCrossbow(stack);
      case PICKAXE -> isPickaxe(stack);
      case TRIDENT -> isTrident(stack);
      case LEGGINGS -> isLeggings(stack);
      case MELEE_WEAPON -> isMeleeWeapon(stack);
      case RANGED_WEAPON -> isRangedWeapon(stack);
      default -> isEquipment(stack);
    };
  }

  public static boolean isEquipment(ItemStack stack) {
    return isArmor(stack) || isWeapon(stack) || isShield(stack) || isTool(stack);
  }

  public static boolean isRangedWeapon(ItemStack stack) {
    return isCrossbow(stack) || isBow(stack);
  }

  public static boolean isMeleeWeapon(ItemStack stack) {
    return isSword(stack) || isAxe(stack) || isTrident(stack);
  }

  public static boolean isLeggings(ItemStack stack) {
    return stack.getItem() instanceof ArmorItem armor && armor.getSlot() == EquipmentSlot.LEGS
        || stack.is(Tags.Items.ARMORS_LEGGINGS);
  }

  public static boolean isTrident(ItemStack stack) {
    ResourceLocation id = ForgeRegistries.ITEMS.getKey(stack.getItem());
    if (Objects.requireNonNull(id).toString().equals("tetra:modular_single")) return true;
    return stack.getItem() instanceof TridentItem || stack.is(Tags.Items.TOOLS_TRIDENTS);
  }

  public static boolean isPickaxe(ItemStack stack) {
    return stack.getItem() instanceof PickaxeItem || stack.is(Tags.Items.TOOLS_PICKAXES);
  }

  public static boolean isCrossbow(ItemStack stack) {
    ResourceLocation id = ForgeRegistries.ITEMS.getKey(stack.getItem());
    if (Objects.requireNonNull(id).toString().equals("tetra:modular_crossbow")) return true;
    return stack.getItem() instanceof CrossbowItem || stack.is(Tags.Items.TOOLS_CROSSBOWS);
  }

  public static boolean isWeapon(ItemStack stack) {
    return isMeleeWeapon(stack) || isRangedWeapon(stack);
  }

  public static boolean isChestplate(ItemStack stack) {
    return stack.getItem() instanceof ArmorItem armor && armor.getSlot() == EquipmentSlot.CHEST
        || stack.is(Tags.Items.ARMORS_CHESTPLATES);
  }

  public static boolean isShovel(ItemStack stack) {
    return stack.getItem() instanceof ShovelItem || stack.is(Tags.Items.TOOLS_SHOVELS);
  }

  public static boolean isShield(ItemStack stack) {
    ResourceLocation id = ForgeRegistries.ITEMS.getKey(stack.getItem());
    if (Objects.requireNonNull(id).toString().equals("tetra:modular_shield")) return true;
    return stack.getItem() instanceof ShieldItem || stack.is(Tags.Items.TOOLS_SHIELDS);
  }

  public static boolean isHelmet(ItemStack stack) {
    return stack.getItem() instanceof ArmorItem armor && armor.getSlot() == EquipmentSlot.HEAD
        || stack.is(Tags.Items.ARMORS_HELMETS);
  }

  public static boolean isSword(ItemStack stack) {
    ResourceLocation id = ForgeRegistries.ITEMS.getKey(stack.getItem());
    if (Objects.requireNonNull(id).toString().equals("tetra:modular_sword")) return true;
    return stack.getItem() instanceof SwordItem || stack.is(Tags.Items.TOOLS_SWORDS);
  }

  public static boolean isTool(ItemStack stack) {
    return stack.getItem() instanceof DiggerItem;
  }

  public static boolean isHoe(ItemStack stack) {
    return stack.getItem() instanceof HoeItem || stack.is(Tags.Items.TOOLS_HOES);
  }

  public static boolean isBow(ItemStack stack) {
    ResourceLocation id = ForgeRegistries.ITEMS.getKey(stack.getItem());
    if (Objects.requireNonNull(id).toString().equals("tetra:modular_bow")) return true;
    return stack.getItem() instanceof BowItem || stack.is(Tags.Items.TOOLS_BOWS);
  }

  public static boolean isBoots(ItemStack stack) {
    return stack.getItem() instanceof ArmorItem armor && armor.getSlot() == EquipmentSlot.FEET
        || stack.is(Tags.Items.ARMORS_BOOTS);
  }

  public static boolean isAxe(ItemStack stack) {
    return stack.getItem() instanceof AxeItem || stack.is(Tags.Items.TOOLS_AXES);
  }

  public static boolean isArmor(ItemStack stack) {
    return isHelmet(stack) || isBoots(stack) || isChestplate(stack) || isLeggings(stack);
  }

  @Override
  public String getDescriptionId() {
    return ItemCondition.super.getDescriptionId() + "." + type.name().toLowerCase();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    EquipmentCondition that = (EquipmentCondition) o;
    return Objects.equals(type, that.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type);
  }

  @Override
  public ItemCondition.Serializer getSerializer() {
    return PSTItemConditions.EQUIPMENT_TYPE.get();
  }

  @Override
  public void addEditorWidgets(SkillTreeEditorScreen editor, Consumer<ItemCondition> consumer) {
    editor.addLabel(0, 0, "Type", ChatFormatting.GREEN);
    editor.shiftWidgets(0, 19);
    editor
        .addDropDownList(0, 0, 200, 14, 8, type)
        .setToNameFunc(Type::getName)
        .setResponder(
            t -> {
              setType(t);
              consumer.accept(this);
            });
    editor.shiftWidgets(0, 19);
  }

  public void setType(Type type) {
    this.type = type;
  }

  public enum Type {
    ANY,
    HELMET,
    CHESTPLATE,
    LEGGINGS,
    BOOTS,
    ARMOR,
    SHIELD,
    WEAPON,
    SWORD,
    AXE,
    TRIDENT,
    MELEE_WEAPON,
    BOW,
    CROSSBOW,
    RANGED_WEAPON,
    PICKAXE,
    HOE,
    SHOVEL,
    TOOL;

    public Component getName() {
      return Component.literal(TooltipHelper.idToName(name().toLowerCase()));
    }
  }

  public static class Serializer implements ItemCondition.Serializer {
    @Override
    public ItemCondition deserialize(JsonObject json) throws JsonParseException {
      Type type = Type.valueOf(json.get("equipment_type").getAsString().toUpperCase());
      return new EquipmentCondition(type);
    }

    @Override
    public void serialize(JsonObject json, ItemCondition condition) {
      if (!(condition instanceof EquipmentCondition aCondition)) {
        throw new IllegalArgumentException();
      }
      json.addProperty("equipment_type", aCondition.type.name().toLowerCase());
    }

    @Override
    public ItemCondition deserialize(CompoundTag tag) {
      Type type = Type.valueOf(tag.getString("equipment_type").toUpperCase());
      return new EquipmentCondition(type);
    }

    @Override
    public CompoundTag serialize(ItemCondition condition) {
      if (!(condition instanceof EquipmentCondition aCondition)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      tag.putString("equipment_type", aCondition.type.name().toLowerCase());
      return tag;
    }

    @Override
    public ItemCondition deserialize(FriendlyByteBuf buf) {
      return new EquipmentCondition(Type.values()[buf.readInt()]);
    }

    @Override
    public void serialize(FriendlyByteBuf buf, ItemCondition condition) {
      if (!(condition instanceof EquipmentCondition aCondition)) {
        throw new IllegalArgumentException();
      }
      buf.writeInt(aCondition.type.ordinal());
    }

    @Override
    public ItemCondition createDefaultInstance() {
      return new EquipmentCondition(Type.ANY);
    }
  }
}
