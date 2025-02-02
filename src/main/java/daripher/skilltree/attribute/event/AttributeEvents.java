package daripher.skilltree.attribute.event;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.config.ServerConfig;
import daripher.skilltree.init.PSTAttributes;
import daripher.skilltree.skill.bonus.condition.item.EquipmentCondition;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.GrindstoneEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class AttributeEvents {
  @SubscribeEvent
  public static void applyLifeRegenerationBonus(PlayerTickEvent event) {
    if (event.phase == Phase.END || event.player.level().isClientSide) return;
    if (event.player.getFoodData().getFoodLevel() == 0) return;
    float lifeRegeneration =
        (float) event.player.getAttributeValue(PSTAttributes.REGENERATION.get());
    if (event.player.getHealth() != event.player.getMaxHealth()
        && event.player.tickCount % 20 == 0) {
      event.player.heal(lifeRegeneration);
      event.player.getFoodData().addExhaustion(lifeRegeneration / 5);
    }
  }

  @SubscribeEvent
  public static void applyExperiencePerMinuteBonus(PlayerTickEvent event) {
    Player player = event.player;
    if (player.level().isClientSide) return;
    float bonus = (float) (player.getAttributeValue(PSTAttributes.EXP_PER_MINUTE.get()));
    int frequency = Math.max((int) (1200 / bonus), 1);
    if (player.tickCount % frequency == 0) {
      ExperienceOrb expOrb =
          new ExperienceOrb(player.level(), player.getX(), player.getY(), player.getZ(), 1);
      player.level().addFreshEntity(expOrb);
    }
  }

  @SubscribeEvent
  public static void applyGrindstoneExpPenalty(GrindstoneEvent.OnTakeItem event) {
    event.setXp((int) (event.getXp() * ServerConfig.grindstone_exp_multiplier));
  }

  // TODO: replace with separate bonus
  @SubscribeEvent
  public static void applyRangedWeaponAttackSpeedBonus(LivingEntityUseItemEvent.Tick event) {
    if (!EquipmentCondition.isRangedWeapon(event.getItem())) return;
    AttributeInstance attribute = event.getEntity().getAttribute(Attributes.ATTACK_SPEED);
    if (attribute == null) return;
    double baseAttackSpeed = attribute.getBaseValue();
    if (baseAttackSpeed == 0) return;
    double attackSpeedBonus = attribute.getValue() / baseAttackSpeed - 1;
    if (attackSpeedBonus == 0) return;
    int tickBonus = attackSpeedBonus < 0 ? 1 : -1;
    while (attackSpeedBonus > 1) {
      event.setDuration(event.getDuration() + tickBonus);
      attackSpeedBonus--;
    }
    int bonusTickFrequency = (int) (1f / attackSpeedBonus);
    if (event.getEntity().tickCount % bonusTickFrequency == 0) {
      event.setDuration(event.getDuration() + tickBonus);
    }
  }
}
