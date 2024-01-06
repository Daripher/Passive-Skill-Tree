package daripher.skilltree.item.gem;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.compat.apotheosis.ApotheosisCompatibility;
import daripher.skilltree.data.SerializationHelper;
import daripher.skilltree.data.reloader.GemTypesReloader;
import daripher.skilltree.entity.player.PlayerHelper;
import daripher.skilltree.init.PSTCreativeTabs;
import daripher.skilltree.init.PSTItems;
import daripher.skilltree.item.ItemHelper;
import daripher.skilltree.item.gem.bonus.GemBonusProvider;
import daripher.skilltree.skill.bonus.item.ItemBonus;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class GemItem extends Item {
  public GemItem() {
    super(new Properties().tab(PSTCreativeTabs.SKILLTREE));
  }

  @Override
  public void fillItemCategory(
      @NotNull CreativeModeTab category, @NotNull NonNullList<ItemStack> items) {
    if (!allowedIn(category)) return;
    GemTypesReloader.getGemTypes().values().stream()
        .sorted()
        .map(GemItem::getDefaultGemStack)
        .forEach(items::add);
  }

  @Override
  public @NotNull String getDescriptionId(@NotNull ItemStack stack) {
    String gemId = getGemType(stack).id().toString().replace(":", ".");
    return getDescriptionId() + "." + gemId;
  }

  @Override
  public void appendHoverText(
      @NotNull ItemStack stack,
      @Nullable Level level,
      @NotNull List<Component> components,
      @NotNull TooltipFlag tooltipFlag) {
    if (SkillTreeMod.apotheosisEnabled()) {
      components.add(Component.translatable("gem.disabled").withStyle(ChatFormatting.RED));
      return;
    }
    Component gemTooltip = Component.translatable("gem.tooltip").withStyle(ChatFormatting.YELLOW);
    components.add(gemTooltip);
    appendItemBonusesTooltips(stack, components);
  }

  private static void appendItemBonusesTooltips(
      @NotNull ItemStack stack, @NotNull List<Component> components) {
    getGemType(stack)
        .bonuses()
        .forEach(
            (c, b) -> {
              Component itemDescription = c.getTooltip();
              Component bonusDescription = b.getTooltip();
              Component tooltip =
                  Component.translatable("gem_class_format", itemDescription, bonusDescription)
                      .withStyle(ChatFormatting.GRAY);
              components.add(tooltip);
            });
  }

  public static GemType getGemType(ItemStack gemStack) {
    if (!gemStack.hasTag()) return GemTypesReloader.NO_TYPE;
    ResourceLocation id = new ResourceLocation(gemStack.getOrCreateTag().getString("type"));
    return GemTypesReloader.getGemTypeById(id);
  }

  public static boolean canInsertGem(Player player, ItemStack itemStack, ItemStack gemStack) {
    if (!ItemHelper.canInsertGem(itemStack)) return false;
    GemBonusProvider bonusProvider = getGemType(gemStack).getBonusProvider(itemStack);
    if (bonusProvider == null) return false;
    return bonusProvider.canApply(player, itemStack);
  }

  public static void insertGem(Player player, ItemStack itemStack, ItemStack gemStack) {
    GemBonusProvider bonusProvider = getGemType(gemStack).getBonusProvider(itemStack);
    if (bonusProvider == null) return;
    bonusProvider.addGemBonus(player, itemStack, gemStack);
  }

  public static void addGemBonus(
      @Nonnull Player player,
      @Nonnull ItemStack itemStack,
      @Nonnull ItemStack gemStack,
      @Nonnull ItemBonus<?> bonus) {
    GemType gemType = getGemType(gemStack);
    float gemPower = PlayerHelper.getGemPower(player, itemStack);
    bonus = bonus.copy().multiply(gemPower);
    ListTag bonusesTag = itemStack.getOrCreateTag().getList("gem_bonuses", Tag.TAG_COMPOUND);
    ListTag gemsTag = itemStack.getOrCreateTag().getList("gems", Tag.TAG_STRING);
    CompoundTag bonusTag = new CompoundTag();
    SerializationHelper.serializeItemBonus(bonusTag, bonus);
    bonusesTag.add(bonusTag);
    gemsTag.add(StringTag.valueOf(gemType.id().toString()));
    itemStack.getOrCreateTag().put("gem_bonuses", bonusesTag);
    itemStack.getOrCreateTag().put("gems", gemsTag);
  }

  public static void removeGemBonuses(ItemStack stack) {
    if (!stack.hasTag()) return;
    stack.getOrCreateTag().remove("gem_bonuses");
    stack.getOrCreateTag().remove("gems");
  }

  public static boolean hasGem(ItemStack stack, int socket) {
    return getGemBonuses(stack).size() > socket;
  }

  public static ItemStack getDefaultGemStack(GemType gemType) {
    ItemStack gemStack = new ItemStack(PSTItems.GEM.get());
    gemStack.getOrCreateTag().putString("type", gemType.id().toString());
    return gemStack;
  }

  public static List<? extends ItemBonus<?>> getGemBonuses(ItemStack stack) {
    if (!stack.hasTag()) return List.of();
    return stack.getOrCreateTag().getList("gem_bonuses", Tag.TAG_COMPOUND).stream()
        .map(CompoundTag.class::cast)
        .map(SerializationHelper::deserializeItemBonus)
        .toList();
  }

  public static List<ItemStack> getGems(ItemStack stack) {
    if (SkillTreeMod.apotheosisEnabled()) return ApotheosisCompatibility.INSTANCE.getGems(stack);
    if (!stack.hasTag()) return List.of();
    return stack.getOrCreateTag().getList("gems", Tag.TAG_STRING).stream()
        .map(StringTag.class::cast)
        .map(StringTag::getAsString)
        .map(ResourceLocation::new)
        .map(GemTypesReloader::getGemTypeById)
        .map(GemItem::getDefaultGemStack)
        .toList();
  }

  @SubscribeEvent
  public static void addGemModels(ModelEvent.RegisterAdditional event) {
    Set<ResourceLocation> textures =
        Minecraft.getInstance()
            .getResourceManager()
            .listResources(
                "models",
                l ->
                    SkillTreeMod.MOD_ID.equals(l.getNamespace())
                        && l.getPath().contains("/gems/")
                        && l.getPath().endsWith(".json"))
            .keySet();
    textures.stream()
        .map(
            l -> l.getPath().substring("models/".length(), l.getPath().length() - ".json".length()))
        .map(path -> new ResourceLocation(SkillTreeMod.MOD_ID, path))
        .forEach(event::register);
  }

  @SubscribeEvent
  public static void replaceGemModel(ModelEvent.BakingCompleted event) {
    ModelResourceLocation model =
        new ModelResourceLocation(new ResourceLocation(SkillTreeMod.MOD_ID, "gem"), "inventory");
    BakedModel oldModel = event.getModels().get(model);
    if (oldModel != null) {
      event.getModels().put(model, new GemModel(oldModel, event.getModelBakery()));
    }
  }
}
