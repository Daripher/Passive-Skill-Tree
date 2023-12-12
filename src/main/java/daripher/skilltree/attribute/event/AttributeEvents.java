package daripher.skilltree.attribute.event;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.config.Config;
import daripher.skilltree.init.PSTAttributes;
import daripher.skilltree.item.ItemHelper;
import daripher.skilltree.mixin.AbstractArrowAccessor;
import daripher.skilltree.mixin.LivingEntityAccessor;
import daripher.skilltree.entity.player.PlayerHelper;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.GrindstoneEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class AttributeEvents {
  @SubscribeEvent
  public static void applyLifeRegenerationBonus(PlayerTickEvent event) {
    if (event.phase == Phase.END || event.player.level.isClientSide) return;
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
  public static void applyEvasionBonus(LivingAttackEvent event) {
    if (!(event.getEntity() instanceof Player player)) return;
    double evasion = player.getAttributeValue(PSTAttributes.EVASION.get());
    if (evasion == 0) return;
    if (!(event.getSource() instanceof EntityDamageSource damageSource)) return;
    if (!(damageSource.getEntity() instanceof LivingEntity)) return;
    if (!(player.getRandom().nextFloat() < evasion / 100)) return;
    player.level.playSound(
        null, player, SoundEvents.ENDER_DRAGON_FLAP, SoundSource.PLAYERS, 0.5F, 1.5F);
    event.setCanceled(true);
  }

  @SubscribeEvent
  public static void applyBlockingBonus(LivingAttackEvent event) {
    if (!(event.getEntity() instanceof Player player)) return;
    float damage = event.getAmount();
    if (damage <= 0) return;
    DamageSource damageSource = event.getSource();
    if (damageSource.isBypassArmor()) return;
    Entity attacker = damageSource.getDirectEntity();
    if (attacker instanceof AbstractArrow arrow && arrow.getPierceLevel() > 0) return;
    ItemStack shield = player.getOffhandItem();
    if (!ItemHelper.isShield(shield)) return;
    double blockChance = player.getAttributeValue(PSTAttributes.BLOCKING.get()) / 100d;
    if (player.getRandom().nextFloat() >= blockChance) return;
    ShieldBlockEvent blockEvent = ForgeHooks.onShieldBlock(player, damageSource, damage);
    if (blockEvent.isCanceled()) return;
    event.setCanceled(true);
    player.level.broadcastEntityEvent(player, (byte) 29);
    if (blockEvent.shieldTakesDamage()) {
      PlayerHelper.hurtShield(player, shield, damage);
    }
    if (damageSource.isProjectile()) return;
    if (!(attacker instanceof LivingEntity livingAttacker)) return;
    LivingEntityAccessor entityAccessor = (LivingEntityAccessor) player;
    entityAccessor.invokeBlockUsingShield(livingAttacker);
  }

  @SubscribeEvent
  public static void applyLifeOnBlockBonus(ShieldBlockEvent event) {
    if (!(event.getEntity() instanceof Player player)) return;
    if (player.getFoodData().getFoodLevel() == 0) return;
    float lifeOnBlock = (float) player.getAttributeValue(PSTAttributes.LIFE_ON_BLOCK.get());
    if (lifeOnBlock == 0) return;
    player.getFoodData().addExhaustion(lifeOnBlock / 5F);
    player.heal(lifeOnBlock);
  }

  @SubscribeEvent
  public static void applyPoisonedWeaponEffects(LivingHurtEvent event) {
    if (!(event.getSource().getDirectEntity() instanceof Player player)) return;
    ItemStack weapon = player.getMainHandItem();
    if (!ItemHelper.hasPoisons(weapon)) return;
    List<MobEffectInstance> poisons = ItemHelper.getPoisons(weapon);
    poisons.forEach(event.getEntity()::addEffect);
  }

  @SubscribeEvent
  public static void applyPoisonedThrownTridentEffects(LivingHurtEvent event) {
    DamageSource damageSource = event.getSource();
    if (!(damageSource.getDirectEntity() instanceof ThrownTrident trident)) return;
    AbstractArrowAccessor arrowAccessor = (AbstractArrowAccessor) trident;
    ItemStack weapon = arrowAccessor.invokeGetPickupItem();
    if (!ItemHelper.hasPoisons(weapon)) return;
    List<MobEffectInstance> poisons = ItemHelper.getPoisons(weapon);
    LivingEntity target = event.getEntity();
    poisons.stream().map(MobEffectInstance::new).forEach(target::addEffect);
  }

  @SubscribeEvent(priority = EventPriority.HIGH)
  public static void addPoisonedWeaponTooltips(ItemTooltipEvent event) {
    ItemStack weapon = event.getItemStack();
    if (!ItemHelper.hasPoisons(weapon)) return;
    event.getToolTip().add(Component.empty());
    event
        .getToolTip()
        .add(Component.translatable("weapon.poisoned").withStyle(ChatFormatting.DARK_PURPLE));
    ItemHelper.getPoisons(weapon).stream()
        .map(TooltipHelper::getEffectInstanceTooltip)
        .forEach(event.getToolTip()::add);
  }

  @SubscribeEvent
  public static void applyLifePerHitBonus(LivingHurtEvent event) {
    Entity directAttacker = event.getSource().getDirectEntity();
    Entity indirectAttacker = event.getSource().getEntity();
    Player player = null;
    if (directAttacker instanceof Player) {
      player = (Player) directAttacker;
    } else if (directAttacker instanceof AbstractArrow && indirectAttacker instanceof Player) {
      player = (Player) indirectAttacker;
    }
    if (player == null) return;
    if (player.getFoodData().getFoodLevel() == 0) return;
    double lifePerHit = player.getAttributeValue(PSTAttributes.LIFE_PER_HIT.get());
    player.getFoodData().addExhaustion((float) (lifePerHit / 5));
    player.heal((float) lifePerHit);
  }

  @SubscribeEvent
  public static void applyExperiencePerMinuteBonus(PlayerTickEvent event) {
    Player player = event.player;
    if (player.level.isClientSide) return;
    float bonus = (float) (player.getAttributeValue(PSTAttributes.EXP_PER_MINUTE.get()));
    int frequency = Math.max((int) (1200 / bonus), 1);
    if (player.tickCount % frequency == 0) {
      ExperienceOrb expOrb =
          new ExperienceOrb(player.level, player.getX(), player.getY(), player.getZ(), 1);
      player.level.addFreshEntity(expOrb);
    }
  }

  @SubscribeEvent
  public static void applyStealthBonus(LivingChangeTargetEvent event) {
    if (!(event.getNewTarget() instanceof Player player)) return;
    double stealth = player.getAttributeValue(PSTAttributes.STEALTH.get()) / 100d;
    if (stealth == 0) return;
    LivingEntity attacker = event.getEntity();
    if (attacker.distanceTo(player)
        > attacker.getAttributeValue(Attributes.FOLLOW_RANGE) * (1 - stealth)) {
      event.setCanceled(true);
    }
  }

  @SubscribeEvent
  public static void applyGrindstoneExpPenalty(GrindstoneEvent.OnTakeItem event) {
    event.setXp((int) (event.getXp() * Config.grindstone_exp_multiplier));
  }

  @SubscribeEvent
  public static void applyRangedWeaponAttackSpeedBonus(LivingEntityUseItemEvent.Tick event) {
    if (!ItemHelper.isRangedWeapon(event.getItem())) return;
    AttributeInstance attribute = event.getEntity().getAttribute(Attributes.ATTACK_SPEED);
    if (attribute == null) return;
    double attackSpeedBonus = attribute.getValue() / attribute.getBaseValue() - 1;
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
