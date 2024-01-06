package daripher.skilltree.item.gem;

import daripher.skilltree.SkillTreeMod;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("deprecation")
public class GemModel implements BakedModel {
  private final BakedModel original;
  private final ItemOverrides itemHandler;

  public GemModel(BakedModel original) {
    this.original = original;
    this.itemHandler =
        new ItemOverrides() {
          @Override
          public BakedModel resolve(
              @NotNull BakedModel original,
              @NotNull ItemStack stack,
              @Nullable ClientLevel world,
              @Nullable LivingEntity entity,
              int seed) {
            return GemModel.this.resolve(stack);
          }
        };
  }

  public BakedModel resolve(ItemStack stack) {
    GemType gemType = GemItem.getGemType(stack);
    ResourceLocation modelId =
        new ResourceLocation(SkillTreeMod.MOD_ID, "item/gems/" + gemType.id().getPath());
    return Minecraft.getInstance().getModelManager().getModel(modelId);
  }

  @Override
  public @NotNull ItemOverrides getOverrides() {
    return this.itemHandler;
  }

  @Override
  @Deprecated
  public @NotNull List<BakedQuad> getQuads(
      BlockState state, Direction direction, @NotNull RandomSource random) {
    return this.original.getQuads(state, direction, random);
  }

  @Override
  public boolean useAmbientOcclusion() {
    return this.original.useAmbientOcclusion();
  }

  @Override
  public boolean isGui3d() {
    return this.original.isGui3d();
  }

  @Override
  public boolean usesBlockLight() {
    return this.original.usesBlockLight();
  }

  @Override
  public boolean isCustomRenderer() {
    return this.original.isCustomRenderer();
  }

  @Override
  @Deprecated
  public @NotNull TextureAtlasSprite getParticleIcon() {
    return this.original.getParticleIcon();
  }

  @Override
  @Deprecated
  public @NotNull ItemTransforms getTransforms() {
    return this.original.getTransforms();
  }
}
