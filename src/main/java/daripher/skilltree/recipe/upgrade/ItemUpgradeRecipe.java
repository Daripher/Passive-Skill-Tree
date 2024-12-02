package daripher.skilltree.recipe.upgrade;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import daripher.skilltree.data.serializers.SerializationHelper;
import daripher.skilltree.init.PSTRecipeSerializers;
import daripher.skilltree.item.ItemHelper;
import daripher.skilltree.network.NetworkHelper;
import daripher.skilltree.recipe.SkillRequiringRecipe;
import daripher.skilltree.skill.bonus.condition.item.ItemCondition;
import daripher.skilltree.skill.bonus.item.ItemBonus;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemUpgradeRecipe extends ChancedUpgradeRecipe implements SkillRequiringRecipe {
  private static final String UPGRADES_TAG = "PstUpgrades";
  private final ItemCondition baseCondition;
  private final Ingredient addition;
  private final ItemBonus<?> itemBonus;
  private final int maxUpgrades;
  private final float[] upgradeChances;

  public ItemUpgradeRecipe(ResourceLocation id, ItemCondition baseCondition, Ingredient addition, ItemBonus<?> itemBonus, int maxUpgrades,
                           float[] upgradeChances) {
    super(id, Ingredient.EMPTY, Ingredient.EMPTY, addition, ItemStack.EMPTY);
    this.baseCondition = baseCondition;
    this.addition = addition;
    this.itemBonus = itemBonus;
    this.maxUpgrades = maxUpgrades;
    this.upgradeChances = upgradeChances;
  }

  @Override
  public boolean matches(@NotNull Container container, @NotNull Level level) {
    if (!canUseRecipe(container, this)) return false;
    ItemStack baseItem = container.getItem(SmithingMenu.BASE_SLOT);
    ItemStack additionItem = container.getItem(SmithingMenu.ADDITIONAL_SLOT);
    return baseCondition.met(baseItem) && addition.test(additionItem) && getUpgradeLevel(baseItem) < maxUpgrades;
  }

  @Override
  public @NotNull ItemStack assemble(@NotNull Container container, @NotNull RegistryAccess registryAccess) {
    if (!canUseRecipe(container, this)) return ItemStack.EMPTY;
    ItemStack additionItem = container.getItem(SmithingMenu.ADDITIONAL_SLOT);
    if (!addition.test(additionItem)) return ItemStack.EMPTY;
    ItemStack baseItem = container.getItem(SmithingMenu.BASE_SLOT);
    if (!baseCondition.met(baseItem)) return ItemStack.EMPTY;
    int upgradeLevel = getUpgradeLevel(baseItem);
    if (upgradeLevel >= maxUpgrades) return ItemStack.EMPTY;
    ItemStack result = baseItem.copy();
    CompoundTag upgradesTag = new CompoundTag();
    upgradeLevel++;
    upgradesTag.putInt(getId().toString(), upgradeLevel);
    ItemBonus<?> itemBonusCopy = itemBonus.copy();
    ItemBonus<?> finalBonus = itemBonusCopy.multiply(upgradeLevel);
    CompoundTag bonusTag = new CompoundTag();
    SerializationHelper.serializeItemBonus(bonusTag, finalBonus);
    upgradesTag.put("Upgrade", bonusTag);
    CompoundTag resultTag = result.getOrCreateTag();
    resultTag.put(UPGRADES_TAG, upgradesTag);
    ItemHelper.refreshDurabilityBonuses(result);
    return result;
  }

  @Override
  public boolean isSpecial() {
    return true;
  }

  @Override
  public float getUpgradeChance(SmithingMenu menu) {
    Slot baseSlot = menu.getSlot(SmithingMenu.BASE_SLOT);
    ItemStack baseItem = baseSlot.getItem();
    int upgradeLevel = getUpgradeLevel(baseItem);
    return upgradeChances[Math.min(upgradeLevel, upgradeChances.length - 1)];
  }

  private int getUpgradeLevel(ItemStack itemStack) {
    CompoundTag tag = itemStack.getTag();
    if (tag == null) return 0;
    if (!tag.contains(UPGRADES_TAG)) return 0;
    CompoundTag upgradesTag = tag.getCompound(UPGRADES_TAG);
    String upgradeId = getId().toString();
    if (!upgradesTag.contains(upgradeId)) return 0;
    return upgradesTag.getInt(upgradeId);
  }

  @Override
  public void craftingFailed(SmithingMenu menu, Player player) {
    consumeIngredient(menu);
    player.level()
        .playSound(null, player, SoundEvents.ITEM_BREAK, SoundSource.PLAYERS, 1F, 1F);
  }

  private static void consumeIngredient(SmithingMenu menu) {
    Slot additionalSlot = menu.getSlot(SmithingMenu.ADDITIONAL_SLOT);
    ItemStack additionalItem = additionalSlot.getItem();
    additionalItem.shrink(1);
    additionalSlot.set(additionalItem);
  }

  @Override
  public @NotNull RecipeSerializer<?> getSerializer() {
    return PSTRecipeSerializers.ITEM_UPGRADE.get();
  }

  public static @Nullable ItemBonus<?> getUpgradedItemBonus(ItemStack itemStack) {
    CompoundTag tag = itemStack.getTag();
    if (tag == null) return null;
    if (!tag.contains(UPGRADES_TAG)) return null;
    CompoundTag upgradesTag = tag.getCompound(UPGRADES_TAG);
    if (!upgradesTag.contains("Upgrade")) return null;
    return SerializationHelper.deserializeItemBonus(upgradesTag.getCompound("Upgrade"));
  }

  public static class Serializer implements RecipeSerializer<ItemUpgradeRecipe> {
    @Override
    public @NotNull ItemUpgradeRecipe fromJson(@NotNull ResourceLocation id, @NotNull JsonObject jsonObject) {
      ItemCondition baseCondition = SerializationHelper.deserializeItemCondition(jsonObject);
      Ingredient addition = Ingredient.fromJson(jsonObject.get("addition"));
      ItemBonus<?> itemBonus = SerializationHelper.deserializeItemBonus(jsonObject);
      int maxUpgrades = jsonObject.get("max_upgrades")
          .getAsInt();
      JsonArray upgradeChancesJson = jsonObject.get("upgrade_chances")
          .getAsJsonArray();
      float[] upgradeChances = new float[upgradeChancesJson.size()];
      for (int i = 0; i < upgradeChancesJson.size(); i++) {
        upgradeChances[i] = upgradeChancesJson.get(i)
            .getAsFloat();
      }
      return new ItemUpgradeRecipe(id, baseCondition, addition, itemBonus, maxUpgrades, upgradeChances);
    }

    @Override
    public ItemUpgradeRecipe fromNetwork(@NotNull ResourceLocation id, @NotNull FriendlyByteBuf buf) {
      ItemCondition baseCondition = NetworkHelper.readItemCondition(buf);
      Ingredient addition = Ingredient.fromNetwork(buf);
      ItemBonus<?> itemBonus = NetworkHelper.readItemBonus(buf);
      int maxUpgrades = buf.readInt();
      int upgradeChancesSize = buf.readInt();
      float[] upgradeChances = new float[upgradeChancesSize];
      for (int i = 0; i < upgradeChancesSize; i++) {
        upgradeChances[i] = buf.readFloat();
      }
      return new ItemUpgradeRecipe(id, baseCondition, addition, itemBonus, maxUpgrades, upgradeChances);
    }

    @Override
    public void toNetwork(@NotNull FriendlyByteBuf buf, @NotNull ItemUpgradeRecipe recipe) {
      NetworkHelper.writeItemCondition(buf, recipe.baseCondition);
      recipe.addition.toNetwork(buf);
      NetworkHelper.writeItemBonus(buf, recipe.itemBonus);
      buf.writeInt(recipe.maxUpgrades);
      buf.writeInt(recipe.upgradeChances.length);
      for (int i = 0; i < recipe.upgradeChances.length; i++) {
        buf.writeFloat(recipe.upgradeChances[i]);
      }
    }
  }
}
