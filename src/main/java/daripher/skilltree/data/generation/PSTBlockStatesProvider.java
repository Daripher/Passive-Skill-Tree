package daripher.skilltree.data.generation;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.init.PSTBlocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class PSTBlockStatesProvider extends BlockStateProvider {
    public PSTBlockStatesProvider(DataGenerator dataGenerator, ExistingFileHelper existingFileHelper) {
        super(dataGenerator.getPackOutput(), SkillTreeMod.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlockState(PSTBlocks.WORKBENCH, this::orientableModelWithBottom);
    }

    private void simpleBlockState(RegistryObject<Block> blockRegistryObject, Function<ResourceLocation, ModelFile> modelFileProvider) {
        ResourceLocation blockId = blockRegistryObject.getId();
        ModelFile modelFile = modelFileProvider.apply(blockId);
        ConfiguredModel configuredModel = new ConfiguredModel(modelFile);
        getVariantBuilder(blockRegistryObject.get()).partialState().setModels(configuredModel);
        itemModels().withExistingParent(blockId.toString(), modLoc("block/" + blockId.getPath()));
    }

    private ModelFile orientableModelWithBottom(ResourceLocation blockId) {
        return models().orientableWithBottom(blockId.toString(), subTexture(blockId, "side"), subTexture(blockId, "front"), subTexture(blockId, "bottom"), subTexture(blockId, "top"));
    }

    @NotNull
    private static ResourceLocation subTexture(ResourceLocation blockId, String name) {
        return blockId.withPath("block/" + blockId.getPath() + "_" + name);
    }
}
