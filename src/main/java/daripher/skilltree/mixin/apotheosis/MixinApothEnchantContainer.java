package daripher.skilltree.mixin.apotheosis;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import daripher.skilltree.api.PlayerContainer;
import daripher.skilltree.api.SkillTreeEnchantmentMenu;
import daripher.skilltree.enchantment.EnchantmentHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
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
		var costs = ((EnchantmentMenu) (Object) this).costs;
		var levelRequirement = ForgeEventFactory.onEnchantmentLevelSet(level, pos, slot, power, itemStack, costs[slot]);
		var costsBeforeReduction = ((SkillTreeEnchantmentMenu) (Object) this).getCostsBeforeReduction();
		costsBeforeReduction[slot] = levelRequirement;
		var player = ((PlayerContainer) (Object) this).getPlayer().orElseThrow(NullPointerException::new);
		var reducedRequirement = EnchantmentHelper.reduceLevelRequirement(levelRequirement, player);
		return reducedRequirement;
	}

	@Redirect(method = "lambda$slotsChanged$1", at = @At(value = "INVOKE", target = "Lshadows/apotheosis/ench/table/ApothEnchantContainer;getEnchantmentList(Lnet/minecraft/world/item/ItemStack;II)Ljava/util/List;"))
	private List<EnchantmentInstance> amplifyEnchantmentsVisually(ApothEnchantContainer menu, ItemStack itemStack, int slot, int cost) {
		var costsBeforeReduction = ((SkillTreeEnchantmentMenu) (Object) this).getCostsBeforeReduction();
		var enchantments = getEnchantmentList(itemStack, slot, costsBeforeReduction[slot]);
		var enchantmentSeed = ((SkillTreeEnchantmentMenu) (Object) this).getEnchantmentSeed();
		var random = RandomSource.create(enchantmentSeed);
		var player = ((PlayerContainer) (Object) this).getPlayer().orElseThrow(NullPointerException::new);
		EnchantmentHelper.amplifyEnchantments(enchantments, random, player);
		return enchantments;
	}

	@Redirect(method = "lambda$clickMenuButton$0", at = @At(value = "INVOKE", target = "Lshadows/apotheosis/ench/table/ApothEnchantContainer;getEnchantmentList(Lnet/minecraft/world/item/ItemStack;II)Ljava/util/List;"))
	private List<EnchantmentInstance> amplifyEnchantmentsOnButtonClick(ApothEnchantContainer menu, ItemStack itemStack, int slot, int cost) {
		var costsBeforeReduction = ((SkillTreeEnchantmentMenu) (Object) this).getCostsBeforeReduction();
		var enchantments = getEnchantmentList(itemStack, slot, costsBeforeReduction[slot]);
		var enchantmentSeed = ((SkillTreeEnchantmentMenu) (Object) this).getEnchantmentSeed();
		var random = RandomSource.create(enchantmentSeed);
		var player = ((PlayerContainer) (Object) this).getPlayer().orElseThrow(NullPointerException::new);
		EnchantmentHelper.amplifyEnchantments(enchantments, random, player);
		return enchantments;
	}

	private @Shadow List<EnchantmentInstance> getEnchantmentList(ItemStack stack, int enchantSlot, int level) {
		return null;
	}
}
