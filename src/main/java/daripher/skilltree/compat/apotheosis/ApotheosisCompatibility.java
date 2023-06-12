package daripher.skilltree.compat.apotheosis;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.compat.apotheosis.datagen.ModGemProvider;
import daripher.skilltree.gem.GemHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import shadows.apotheosis.adventure.affix.socket.SocketHelper;
import shadows.apotheosis.adventure.affix.socket.gem.Gem;
import shadows.apotheosis.adventure.affix.socket.gem.GemManager;

public enum ApotheosisCompatibility {
	ISNTANCE;

	public void addCompatibility() {
		var forgeEventBus = MinecraftForge.EVENT_BUS;
		forgeEventBus.addListener(this::addAdditionalSocketTooltip);
		var modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		modEventBus.addListener(this::generateGems);
	}

	private void generateGems(GatherDataEvent event) {
		var dataGenerator = event.getGenerator();
		var existingFileHelper = event.getExistingFileHelper();
		dataGenerator.addProvider(event.includeServer(), new ModGemProvider(dataGenerator, existingFileHelper));
	}

	public int getGemsCount(ItemStack itemStack) {
		return SocketHelper.getActiveGems(itemStack).size();
	}

	public void dropGemFromOre(Player player, ServerLevel level, BlockPos blockPos) {
		var gemStack = GemManager.createRandomGemStack(player.getRandom(), level, player.getLuck(), this::shouldDropFromOre);
		Block.popResource(level, blockPos, gemStack);
	}

	private boolean shouldDropFromOre(Gem gem) {
		return gem.getDimensions().contains(new ResourceLocation(SkillTreeMod.MOD_ID, "fake_dimension"));
	}

	private void addAdditionalSocketTooltip(ItemTooltipEvent event) {
		if (GemHelper.hasAdditionalSocket(event.getItemStack())) {
			var additionalSocketTooltip = Component.translatable("gemstone.additional_socket").withStyle(ChatFormatting.YELLOW);
			event.getToolTip().add(1, additionalSocketTooltip);
		}
	}
}
