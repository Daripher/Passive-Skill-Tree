package daripher.skilltree.mixin.minecraft;

import daripher.skilltree.container.InteractiveContainer;
import daripher.skilltree.mixin.CampfireBlockEntityAccessor;
import daripher.skilltree.skill.bonus.SkillBonusHandler;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CampfireBlockEntity.class)
public class CampfireBlockEntityMixin implements InteractiveContainer {
  private @Nullable Player player;

  @Inject(method = "cookTick", at = @At("HEAD"))
  private static void setCookedFoodBonuses(
      Level level,
      BlockPos pos,
      BlockState state,
      CampfireBlockEntity entity,
      CallbackInfo callback) {
    if (!(entity instanceof InteractiveContainer container)) return;
    CampfireBlockEntityAccessor accessor = (CampfireBlockEntityAccessor) entity;
    Optional<Player> player = container.getUser();
    if (player.isEmpty()) return;
    for (int i = 0; i < entity.getItems().size(); i++) {
      ItemStack stack = entity.getItems().get(i);
      if (stack.isEmpty()) continue;
      if (accessor.getCookingProgress()[i] + 1 < accessor.getCookingTime()[i]) continue;
      SkillBonusHandler.itemCrafted(player.get(), stack);
    }
  }

  @Override
  public Optional<Player> getUser() {
    return Optional.ofNullable(player);
  }

  @Override
  public void setUser(@Nullable Player player) {
    this.player = player;
  }
}
