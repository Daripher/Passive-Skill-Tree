package daripher.skilltree.skill.bonus.predicate.item;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.init.predicate.PSTItemPredicates;
import daripher.skilltree.init.PSTTags;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;
import net.neoforged.neoforge.common.Tags;
import daripher.skilltree.util.ForgeRegistries;

import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;

public class EquipmentPredicate implements ItemStackPredicate {
    public Type type;

    public EquipmentPredicate(Type type) {
        this.type = type;
    }

    @Override
    public boolean test(ItemStack stack) {
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
        return isCrossbow(stack) || isBow(stack) || stack.is(PSTTags.Items.RANGED_WEAPON);
    }

    public static boolean isMeleeWeapon(ItemStack stack) {
        return isSword(stack) || isAxe(stack) || isTrident(stack) || stack.is(PSTTags.Items.MELEE_WEAPON);
    }

    public static boolean isLeggings(ItemStack stack) {
        return stack.getItem() instanceof ArmorItem armor
                        && armor.getEquipmentSlot() == EquipmentSlot.LEGS
                || stack.is(ItemTags.LEG_ARMOR);
    }

    public static boolean isTrident(ItemStack stack) {
        ResourceLocation id = ForgeRegistries.ITEMS.getKey(stack.getItem());
        if (Objects.requireNonNull(id).toString().equals("tetra:modular_single")) {
            return true;
        }
        return stack.getItem() instanceof TridentItem || stack.is(Tags.Items.TOOLS_SPEAR);
    }

    public static boolean isPickaxe(ItemStack stack) {
        return stack.getItem() instanceof PickaxeItem || stack.is(ItemTags.PICKAXES);
    }

    public static boolean isCrossbow(ItemStack stack) {
        ResourceLocation id = ForgeRegistries.ITEMS.getKey(stack.getItem());
        if (Objects.requireNonNull(id).toString().equals("tetra:modular_crossbow")) {
            return true;
        }
        return stack.getItem() instanceof CrossbowItem || stack.is(Tags.Items.TOOLS_CROSSBOW);
    }

    public static boolean isWeapon(ItemStack stack) {
        return isMeleeWeapon(stack) || isRangedWeapon(stack);
    }

    public static boolean isPotion(ItemStack stack) {
        return stack.getItem() instanceof PotionItem;
    }

    public static boolean isChestplate(ItemStack stack) {
        return stack.getItem() instanceof ArmorItem armor
                        && armor.getEquipmentSlot() == EquipmentSlot.CHEST
                || stack.is(ItemTags.CHEST_ARMOR);
    }

    public static boolean isShovel(ItemStack stack) {
        return stack.getItem() instanceof ShovelItem || stack.is(ItemTags.SHOVELS);
    }

    public static boolean isShield(ItemStack stack) {
        ResourceLocation id = ForgeRegistries.ITEMS.getKey(stack.getItem());
        if (Objects.requireNonNull(id).toString().equals("tetra:modular_shield")) {
            return true;
        }
        return stack.getItem() instanceof ShieldItem || stack.is(Tags.Items.TOOLS_SHIELD);
    }

    public static boolean isHelmet(ItemStack stack) {
        return stack.getItem() instanceof ArmorItem armor
                        && armor.getEquipmentSlot() == EquipmentSlot.HEAD
                || stack.is(ItemTags.HEAD_ARMOR);
    }

    public static boolean isSword(ItemStack stack) {
        ResourceLocation id = ForgeRegistries.ITEMS.getKey(stack.getItem());
        if (Objects.requireNonNull(id).toString().equals("tetra:modular_sword")) {
            return true;
        }
        return stack.getItem() instanceof SwordItem || stack.is(ItemTags.SWORDS);
    }

    public static boolean isTool(ItemStack stack) {
        return stack.getItem() instanceof DiggerItem || stack.is(Tags.Items.TOOLS);
    }

    public static boolean isHoe(ItemStack stack) {
        return stack.getItem() instanceof HoeItem || stack.is(ItemTags.HOES);
    }

    public static boolean isBow(ItemStack stack) {
        ResourceLocation id = ForgeRegistries.ITEMS.getKey(stack.getItem());
        if (Objects.requireNonNull(id).toString().equals("tetra:modular_bow")) {
            return true;
        }
        return stack.getItem() instanceof BowItem || stack.is(Tags.Items.TOOLS_BOW);
    }

    public static boolean isBoots(ItemStack stack) {
        return stack.getItem() instanceof ArmorItem armor
                        && armor.getEquipmentSlot() == EquipmentSlot.FEET
                || stack.is(ItemTags.FOOT_ARMOR);
    }

    public static boolean isAxe(ItemStack stack) {
        return stack.getItem() instanceof AxeItem || stack.is(ItemTags.AXES);
    }

    public static boolean isArmor(ItemStack stack) {
        return isHelmet(stack) || isBoots(stack) || isChestplate(stack) || isLeggings(stack);
    }

    @Override
    public String getDescriptionId() {
        return ItemStackPredicate.super.getDescriptionId() + "." + type.name().toLowerCase(Locale.ROOT);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EquipmentPredicate that = (EquipmentPredicate) o;
        return Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }

    @Override
    public ItemStackPredicate.Serializer getSerializer() {
        return PSTItemPredicates.EQUIPMENT_TYPE.get();
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, Consumer<ItemStackPredicate> consumer) {
        editor.addLabel(0, 0, "Type", ChatFormatting.GREEN);
        editor.increaseHeight(19);
        editor.addSelectionMenu(0, 0, 200, type).setResponder(type -> selectEquipmentType(consumer, type))
                .setElementNameGetter(Type::getName);
        editor.increaseHeight(19);
    }

    private void selectEquipmentType(Consumer<ItemStackPredicate> consumer, Type type) {
        setType(type);
        consumer.accept(this);
    }

    public void setType(Type type) {
        this.type = type;
    }

    public enum Type {
        ANY, HELMET, CHESTPLATE, LEGGINGS, BOOTS, ARMOR, SHIELD, WEAPON, SWORD, AXE, TRIDENT, MELEE_WEAPON, BOW, CROSSBOW, RANGED_WEAPON, PICKAXE, HOE, SHOVEL, TOOL;

        public Component getName() {
            return Component.literal(TooltipHelper.idToName(name().toLowerCase(Locale.ROOT)));
        }
    }

    public static class Serializer implements ItemStackPredicate.Serializer {
        @Override
        public ItemStackPredicate deserialize(JsonObject json) throws JsonParseException {
            Type type = Type.valueOf(json.get("equipment_type").getAsString().toUpperCase(Locale.ROOT));
            return new EquipmentPredicate(type);
        }

        @Override
        public void serialize(JsonObject json, ItemStackPredicate condition) {
            if (!(condition instanceof EquipmentPredicate aCondition)) {
                throw new IllegalArgumentException();
            }
            json.addProperty("equipment_type", aCondition.type.name().toLowerCase(Locale.ROOT));
        }

        @Override
        public ItemStackPredicate deserialize(CompoundTag tag) {
            Type type = Type.valueOf(tag.getString("equipment_type").toUpperCase(Locale.ROOT));
            return new EquipmentPredicate(type);
        }

        @Override
        public CompoundTag serialize(ItemStackPredicate condition) {
            if (!(condition instanceof EquipmentPredicate aCondition)) {
                throw new IllegalArgumentException();
            }
            CompoundTag tag = new CompoundTag();
            tag.putString("equipment_type", aCondition.type.name().toLowerCase(Locale.ROOT));
            return tag;
        }

        @Override
        public ItemStackPredicate deserialize(FriendlyByteBuf buf) {
            return new EquipmentPredicate(Type.values()[buf.readInt()]);
        }

        @Override
        public void serialize(FriendlyByteBuf buf, ItemStackPredicate condition) {
            if (!(condition instanceof EquipmentPredicate aCondition)) {
                throw new IllegalArgumentException();
            }
            buf.writeInt(aCondition.type.ordinal());
        }

        @Override
        public ItemStackPredicate createDefaultInstance() {
            return new EquipmentPredicate(Type.ANY);
        }
    }
}
