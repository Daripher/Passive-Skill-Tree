package daripher.skilltree.item.gem;

import daripher.skilltree.entity.player.PlayerExtension;
import daripher.skilltree.init.PSTCreativeTabs;
import daripher.skilltree.init.PSTItems;
import daripher.skilltree.item.ItemHelper;
import daripher.skilltree.skill.bonus.item.ItemBonus;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.RegistryObject;

public class IrisciteItem extends GemItem {
  private static final List<ItemBonus<?>> SIMPLE_GEM_BONUSES = new ArrayList<>();

  public IrisciteItem() {
    super(new Properties().tab(PSTCreativeTabs.SKILLTREE));
  }

  @Override
  public @Nullable ItemBonus<?> getGemBonus(
      Player player, ItemStack itemStack, ItemStack gemStack) {
    RandomSource random = createRandomSource(player, itemStack);
    if (SIMPLE_GEM_BONUSES.isEmpty()) initSimpleGemBonuses();
    return SIMPLE_GEM_BONUSES.get(random.nextInt(SIMPLE_GEM_BONUSES.size()));
  }

  @Override
  public boolean canInsertInto(Player player, ItemStack stack, ItemStack gemStack, int socket) {
    if (!ItemHelper.isRing(stack) && !ItemHelper.isNecklace(stack)) return false;
    return super.canInsertInto(player, stack, gemStack, socket);
  }

  protected void initSimpleGemBonuses() {
    PSTItems.REGISTRY.getEntries().stream()
        .map(RegistryObject::get)
        .filter(SimpleGemItem.class::isInstance)
        .map(SimpleGemItem.class::cast)
        .map(SimpleGemItem::getBonusesList)
        .forEach(SIMPLE_GEM_BONUSES::addAll);
  }

  protected RandomSource createRandomSource(Player player, ItemStack itemStack) {
    RandomSource random = RandomSource.create();
    int seed = ((PlayerExtension) player).getGemsRandomSeed();
    int slot = Player.getEquipmentSlotForItem(itemStack).ordinal();
    random.setSeed(seed + slot);
    return random;
  }

  @Override
  protected void appendBonusesTooltip(ItemStack stack, List<Component> components) {
    MutableComponent gemClass = formatGemClass("jewelry").withStyle(ChatFormatting.GRAY);
    MutableComponent bonus =
        Component.translatable(getDescriptionId() + ".tooltip").withStyle(ChatFormatting.BLUE);
    components.add(gemClass.append(bonus));
  }
}
