package daripher.skilltree.compat.apotheosis;

import daripher.skilltree.SkillTreeMod;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import shadows.apotheosis.Apotheosis;
import shadows.apotheosis.adventure.affix.socket.SocketHelper;
import shadows.apotheosis.adventure.affix.socket.gem.Gem;
import shadows.apotheosis.adventure.affix.socket.gem.GemManager;

public enum ApotheosisCompatibility {
	ISNTANCE;

	public void addCompatibility() {
		var modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		modEventBus.addListener(this::generateGems);
	}

	private void generateGems(GatherDataEvent event) {
		var dataGenerator = event.getGenerator();
		var existingFileHelper = event.getExistingFileHelper();
		dataGenerator.addProvider(event.includeServer(), new ModGemProvider(dataGenerator, existingFileHelper));
	}

	public boolean adventureModuleEnabled() {
		return Apotheosis.enableAdventure;
	}

	public int getGemsCount(ItemStack itemStack) {
		if (!ApotheosisCompatibility.ISNTANCE.adventureModuleEnabled()) return 0;
		return SocketHelper.getActiveGems(itemStack).size();
	}

	public void dropGemFromOre(Player player, ServerLevel level, BlockPos blockPos) {
		var gemStack = GemManager.createRandomGemStack(player.getRandom(), level, player.getLuck(), this::shouldDropFromOre);
		Block.popResource(level, blockPos, gemStack);
	}

	private boolean shouldDropFromOre(Gem gem) {
		return gem.getDimensions().contains(new ResourceLocation(SkillTreeMod.MOD_ID, "fake_dimension"));
	}
}
