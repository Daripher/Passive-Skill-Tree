package daripher.skilltree.mixin.minecraft;

import daripher.skilltree.api.CampfireBlockEntityExtension;
import daripher.skilltree.api.PlayerContainer;
import daripher.skilltree.skill.bonus.SkillBonusHandler;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CampfireBlockEntity.class)
public class CampfireBlockEntityMixin implements PlayerContainer, CampfireBlockEntityExtension {
  private @Nullable Player player;
  private @Shadow @Final int[] cookingProgress;
  private @Shadow @Final int[] cookingTime;

  @Inject(method = "cookTick", at = @At("HEAD"))
  private static void setCookedFoodBonuses(
      Level level,
      BlockPos pos,
      BlockState state,
      CampfireBlockEntity entity,
      CallbackInfo callback) {
    if (!(entity instanceof CampfireBlockEntityExtension extension)) return;
    if (!(entity instanceof PlayerContainer container)) return;
    Optional<Player> player = container.getViewingPlayer();
    if (player.isEmpty()) return;
    for (int i = 0; i < entity.getItems().size(); i++) {
      ItemStack stack = entity.getItems().get(i);
      if (stack.isEmpty()) continue;
      if (extension.getCookingProgress()[i] + 1 < extension.getCookingTime()[i]) continue;
      SkillBonusHandler.itemCrafted(player.get(), stack);
    }
  }

  @Override
  public Optional<Player> getViewingPlayer() {
    return Optional.ofNullable(player);
  }

  @Override
  public void setViewingPlayer(@Nullable Player player) {
    this.player = player;
  }

  @Override
  public int[] getCookingProgress() {
    return cookingProgress;
  }

  @Override
  public int[] getCookingTime() {
    return cookingTime;
  }
}
