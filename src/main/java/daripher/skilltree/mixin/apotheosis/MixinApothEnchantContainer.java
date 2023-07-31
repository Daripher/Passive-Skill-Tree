package daripher.skilltree.mixin.apotheosis;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import daripher.skilltree.api.PlayerContainer;
import daripher.skilltree.api.EnchantmentMenuExtention;
import daripher.skilltree.enchantment.EnchantmentHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.ForgeEventFactory;
import shadows.apotheosis.ench.table.ApothEnchantContainer;

@Mixin(value = ApothEnchantContainer.class, remap = false)
public class MixinApothEnchantContainer {
	@Redirect(method = "lambda$slotsChanged$1", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/event/ForgeEventFactory;onEnchantmentLevelSet(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;IILnet/minecraft/world/item/ItemStack;I)I"))
	private int reduceLevelRequirements(Level level, BlockPos pos, int slot, int power, ItemStack itemStack, int enchantmentLevel) {
		int[] costs = ((EnchantmentMenu) (Object) this).costs;
		int levelRequirement = ForgeEventFactory.onEnchantmentLevelSet(level, pos, slot, power, itemStack, costs[slot]);
		int[] costsBeforeReduction = ((EnchantmentMenuExtention) (Object) this).getCostsBeforeReduction();
		costsBeforeReduction[slot] = levelRequirement;
		Player player = ((PlayerContainer) (Object) this).getPlayer().orElseThrow(NullPointerException::new);
		int reducedRequirement = EnchantmentHelper.adjustLevelRequirement(levelRequirement, player);
		return reducedRequirement;
	}

	@Redirect(method = "lambda$slotsChanged$1", at = @At(value = "INVOKE", target = "Lshadows/apotheosis/ench/table/ApothEnchantContainer;getEnchantmentList(Lnet/minecraft/world/item/ItemStack;II)Ljava/util/List;"))
	private List<EnchantmentInstance> amplifyEnchantmentsVisually(ApothEnchantContainer menu, ItemStack itemStack, int slot, int cost) {
		List<EnchantmentInstance> enchantments = amplifyEnchantments(itemStack, slot);
		return enchantments;
	}

	@Redirect(method = "lambda$clickMenuButton$0", at = @At(value = "INVOKE", target = "Lshadows/apotheosis/ench/table/ApothEnchantContainer;getEnchantmentList(Lnet/minecraft/world/item/ItemStack;II)Ljava/util/List;"))
	private List<EnchantmentInstance> amplifyEnchantmentsOnButtonClick(ApothEnchantContainer menu, ItemStack itemStack, int slot, int cost) {
		List<EnchantmentInstance> enchantments = amplifyEnchantments(itemStack, slot);
		return enchantments;
	}

	protected List<EnchantmentInstance> amplifyEnchantments(ItemStack itemStack, int slot) {
		var enchantmentMenu = (EnchantmentMenuExtention) (Object) this;
		int[] costsBeforeReduction = enchantmentMenu.getCostsBeforeReduction();
		List<EnchantmentInstance> enchantments = getEnchantmentList(itemStack, slot, costsBeforeReduction[slot]);
		int enchantmentSeed = enchantmentMenu.getEnchantmentSeed();
		var random = RandomSource.create(enchantmentSeed);
		var playerContainer = (PlayerContainer) (Object) this;
		Player player = playerContainer.getPlayer().orElseThrow(NullPointerException::new);
		EnchantmentHelper.amplifyEnchantments(enchantments, random, player);
		return enchantments;
	}

	private @Shadow List<EnchantmentInstance> getEnchantmentList(ItemStack stack, int enchantSlot, int level) {
		return null;
	}
}
