package daripher.skilltree.enchantment;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.init.PSTEnchantments;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

@Mod.EventBusSubscriber(modid = SkillTreeMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SteelMindEnchantment extends CraftableEnchantment {
  public SteelMindEnchantment() {
    super(3, EnchantmentCategory.ARMOR_HEAD, new EquipmentSlot[]{EquipmentSlot.HEAD});
  }

  @Override
  protected boolean checkCompatibility(@NotNull Enchantment other) {
    return other != Enchantments.MENDING;
  }

  @SubscribeEvent
  public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
    Player player = event.player;
    Level level = player.level();
    if (level.isClientSide) return;
    int enchantmentLevel = EnchantmentHelper.getEnchantmentLevel(PSTEnchantments.STEEL_MIND.get(), player);
    if (enchantmentLevel == 0) return;
    // 5 exp per minute (per level)
    int frequency = 1200 / (5 * enchantmentLevel);
    if (player.tickCount % frequency != 0) return;
    ExperienceOrb expOrb = new ExperienceOrb(level, player.getX(), player.getY(), player.getZ(), 1);
    level.addFreshEntity(expOrb);
  }
}
