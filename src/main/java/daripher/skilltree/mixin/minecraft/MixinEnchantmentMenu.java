package daripher.skilltree.mixin.minecraft;

import java.util.List;
import java.util.Optional;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import daripher.skilltree.api.PlayerContainer;
import daripher.skilltree.api.SkillTreeEnchantmentMenu;
import daripher.skilltree.enchantment.EnchantmentHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.ForgeEventFactory;

@Mixin(EnchantmentMenu.class)
public abstract class MixinEnchantmentMenu implements PlayerContainer, SkillTreeEnchantmentMenu {
	private @Shadow @Final DataSlot enchantmentSeed;
	public @Shadow @Final int[] costs;
	private Player player;
	private int[] costsBeforeReduction = new int[3];

	@Inject(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/inventory/ContainerLevelAccess;)V", at = @At("TAIL"))
	private void setPlayer(int windowId, Inventory inventory, ContainerLevelAccess levelAccess, CallbackInfo callbackInfo) {
		player = inventory.player;
	}

	@Redirect(method = { "lambda$slotsChanged$0",
			"m_39483_" }, at = @At(value = "INVOKE", target = "Lnet/minecraftforge/event/ForgeEventFactory;onEnchantmentLevelSet(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;IILnet/minecraft/world/item/ItemStack;I)I"))
	private int reduceLevelRequirements(Level level, BlockPos pos, int slot, int power, ItemStack itemStack, int enchantmentLevel) {
		int levelRequirement = ForgeEventFactory.onEnchantmentLevelSet(level, pos, slot, power, itemStack, costs[slot]);
		costsBeforeReduction[slot] = levelRequirement;
		int reducedRequirement = EnchantmentHelper.reduceLevelRequirement(levelRequirement, player);
		return reducedRequirement;
	}

	@Redirect(method = { "lambda$slotsChanged$0", "m_39483_" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/EnchantmentMenu;getEnchantmentList(Lnet/minecraft/world/item/ItemStack;II)Ljava/util/List;"))
	private List<EnchantmentInstance> amplifyEnchantmentsVisually(EnchantmentMenu menu, ItemStack itemStack, int slot, int cost) {
		List<EnchantmentInstance> enchantments = getEnchantmentList(itemStack, slot, costsBeforeReduction[slot]);
		var random = RandomSource.create(enchantmentSeed.get());
		EnchantmentHelper.amplifyEnchantments(enchantments, random, player);
		return enchantments;
	}

	@Redirect(method = { "lambda$clickMenuButton$1", "m_39475_" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/EnchantmentMenu;getEnchantmentList(Lnet/minecraft/world/item/ItemStack;II)Ljava/util/List;"))
	private List<EnchantmentInstance> amplifyEnchantmentsOnButtonClick(EnchantmentMenu menu, ItemStack itemStack, int slot, int cost) {
		List<EnchantmentInstance> enchantments = getEnchantmentList(itemStack, slot, costsBeforeReduction[slot]);
		var random = RandomSource.create(enchantmentSeed.get());
		EnchantmentHelper.amplifyEnchantments(enchantments, random, player);
		return enchantments;
	}

	@Shadow
	private List<EnchantmentInstance> getEnchantmentList(ItemStack stack, int slot, int cost) {
		return null;
	}

	@Override
	public Optional<Player> getPlayer() {
		return Optional.ofNullable(player);
	}

	@Override
	public void setPlayer(Player player) {
		this.player = player;
	}

	@Override
	public int[] getCostsBeforeReduction() {
		return costsBeforeReduction;
	}

	@Override
	public int getEnchantmentSeed() {
		return enchantmentSeed.get();
	}
}
