package daripher.skilltree.mixin.davespotioneering;

import daripher.skilltree.container.InteractiveContainer;
import daripher.skilltree.skill.bonus.SkillBonusHandler;
import java.util.Optional;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfar.davespotioneering.blockentity.AdvancedBrewingStandBlockEntity;
import tfar.davespotioneering.inv.BrewingHandler;

@Mixin(value = AdvancedBrewingStandBlockEntity.class, remap = false)
public class AdvancedBrewingStandBlockEntityMixin {
  private @Shadow @Final BrewingHandler brewingHandler;

  @Inject(method = "brewPotions", at = @At("TAIL"))
  private void enhanceBrewedPotions(CallbackInfo callbackInfo) {
    InteractiveContainer container = (InteractiveContainer) this;
    Optional<Player> user = container.getUser();
    if (user.isEmpty()) return;
    brewingHandler.getStacks().forEach(s -> SkillBonusHandler.itemCrafted(user.get(), s));
  }
}
