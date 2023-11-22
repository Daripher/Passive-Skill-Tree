package daripher.skilltree.attribute.event;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.api.EquipmentContainer;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.config.Config;
import daripher.skilltree.init.PSTAttributes;
import daripher.skilltree.item.ItemHelper;
import daripher.skilltree.util.PlayerHelper;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion.BlockInteraction;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.GrindstoneEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.ShieldBlockEvent;
import net.minecraftforge.event.entity.player.ItemFishedEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.level.BlockEvent.BreakEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

@EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class AttributeEvents {
  @SubscribeEvent
  public static void applyLifeRegenerationBonus(PlayerTickEvent event) {
    if (event.phase == Phase.END || event.player.level.isClientSide) return;
    if (event.player.getFoodData().getFoodLevel() == 0) return;
    float lifeRegeneration =
        (float) event.player.getAttributeValue(PSTAttributes.LIFE_REGENERATION.get());
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

  @SubscribeEvent(priority = EventPriority.LOWEST)
  public static void applyLootDuplicationChanceBonus(LivingDropsEvent event) {
    // shouldn't multiply player's loot
    if (event.getEntity() instanceof Player) return;
    if (!(event.getSource().getEntity() instanceof Player player)) return;
    double doubleLootChance = player.getAttributeValue(PSTAttributes.DOUBLE_LOOT_CHANCE.get()) - 1;
    double tripleLootChance = player.getAttributeValue(PSTAttributes.TRIPLE_LOOT_CHANCE.get()) - 1;
    if (player.getRandom().nextFloat() < doubleLootChance) {
      List<ItemEntity> drops = getDrops(event);
      event.getDrops().addAll(drops);
    }
    if (player.getRandom().nextFloat() < tripleLootChance) {
      List<ItemEntity> drops = getDrops(event);
      event.getDrops().addAll(drops);
      event.getDrops().addAll(drops);
    }
  }

  protected static List<ItemEntity> getDrops(LivingDropsEvent event) {
    List<ItemEntity> drops = new ArrayList<>();
    event.getDrops().stream().map(ItemEntity::copy).forEach(drops::add);
    if (event.getEntity() instanceof EquipmentContainer entity) drops.removeIf(entity::equipped);
    return drops;
  }

  @SubscribeEvent
  public static void applyBlockingBonus(LivingAttackEvent event) {
    if (!(event.getEntity() instanceof Player player)) return;
    if (event.getAmount() <= 0) return;
    if (event.getSource().isBypassArmor()) return;
    if (event.getSource().getDirectEntity() instanceof AbstractArrow arrow
        && arrow.getPierceLevel() > 0) return;
    ItemStack offhandItem = player.getOffhandItem();
    if (!ItemHelper.isShield(offhandItem)) return;
    double blockChance = player.getAttributeValue(PSTAttributes.BLOCKING.get()) / 100;
    if (player.getRandom().nextFloat() >= blockChance) return;
    ShieldBlockEvent shieldBlockEvent =
        ForgeHooks.onShieldBlock(player, event.getSource(), event.getAmount());
    if (shieldBlockEvent.isCanceled()) return;
    event.setCanceled(true);
    player.level.broadcastEntityEvent(player, (byte) 29);
    if (shieldBlockEvent.shieldTakesDamage())
      PlayerHelper.hurtShield(player, offhandItem, event.getAmount());
    if (event.getSource().isProjectile()) return;
    Entity attacker = event.getSource().getDirectEntity();
    if (attacker instanceof LivingEntity livingAttacker) {
      Method blockUsingShieldMethod =
          ObfuscationReflectionHelper.findMethod(LivingEntity.class, "m_6728_", LivingEntity.class);
      try {
        blockUsingShieldMethod.invoke(player, livingAttacker);
      } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
        e.printStackTrace();
      }
    }
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
    Method getPickupItemMethod =
        ObfuscationReflectionHelper.findMethod(AbstractArrow.class, "m_7941_");
    ItemStack weapon = null;
    try {
      weapon = (ItemStack) getPickupItemMethod.invoke(trident);
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      e.printStackTrace();
    }
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
        .map(TooltipHelper::getEffectTooltip)
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
  public static void applyChanceToRetrieveArrowsBonus(LivingHurtEvent event) {
    if (!(event.getSource().getDirectEntity() instanceof AbstractArrow arrow)) return;
    if (!(event.getSource().getEntity() instanceof Player player)) return;
    double chance = player.getAttributeValue(PSTAttributes.CHANCE_TO_RETRIEVE_ARROWS.get()) - 1;
    if (player.getRandom().nextFloat() >= chance) return;
    LivingEntity target = event.getEntity();
    CompoundTag targetData = target.getPersistentData();
    ListTag stuckArrowsTag = targetData.getList("StuckArrows", new CompoundTag().getId());
    stuckArrowsTag.add(getArrowStack(arrow).save(new CompoundTag()));
    targetData.put("StuckArrows", stuckArrowsTag);
  }

  private static ItemStack getArrowStack(AbstractArrow arrow) {
    try {
      // AbstractArrow.getPickupItem
      return (ItemStack)
          ObfuscationReflectionHelper.findMethod(AbstractArrow.class, "m_7941_").invoke(arrow);
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      e.printStackTrace();
    }
    return ItemStack.EMPTY;
  }

  @SubscribeEvent
  public static void retrieveArrows(LivingDeathEvent event) {
    LivingEntity entity = event.getEntity();
    ListTag stuckArrowsTag =
        entity.getPersistentData().getList("StuckArrows", new CompoundTag().getId());
    if (stuckArrowsTag.isEmpty()) return;
    stuckArrowsTag.stream()
        .map(CompoundTag.class::cast)
        .map(ItemStack::of)
        .forEach(entity::spawnAtLocation);
  }

  @SubscribeEvent
  public static void applyIncomingHealingBonus(LivingHealEvent event) {
    if (!(event.getEntity() instanceof Player player)) return;
    float multiplier = (float) (player.getAttributeValue(PSTAttributes.INCOMING_HEALING.get()));
    event.setAmount(event.getAmount() * multiplier);
  }

  @SubscribeEvent
  public static void applyExperiencePerMinuteBonus(PlayerTickEvent event) {
    Player player = event.player;
    if (player.level.isClientSide) return;
    float bonus = (float) (player.getAttributeValue(PSTAttributes.EXPERIENCE_PER_MINUTE.get()));
    int frequency = Math.max((int) (1200 / bonus), 1);
    if (player.tickCount % frequency == 0) {
      ExperienceOrb expOrb =
          new ExperienceOrb(player.level, player.getX(), player.getY(), player.getZ(), 1);
      player.level.addFreshEntity(expOrb);
    }
  }

  @SubscribeEvent
  public static void applyExperienceFromMobsBonus(LivingExperienceDropEvent event) {
    if (event.getAttackingPlayer() == null) return;
    float bonus =
        (float)
            (event
                .getAttackingPlayer()
                .getAttributeValue(PSTAttributes.EXPERIENCE_FROM_MOBS.get()));
    event.setDroppedExperience((int) (event.getDroppedExperience() * bonus));
  }

  @SubscribeEvent
  public static void applyExperienceFromOreBonus(BreakEvent event) {
    if (!event.getState().is(Tags.Blocks.ORES)) return;
    float bonus =
        (float) (event.getPlayer().getAttributeValue(PSTAttributes.EXPERIENCE_FROM_ORE.get()));
    event.setExpToDrop((int) (event.getExpToDrop() * bonus));
  }

  @SubscribeEvent
  public static void applyStealthBonus(LivingChangeTargetEvent event) {
    if (!(event.getNewTarget() instanceof Player player)) return;
    double stealth = player.getAttributeValue(PSTAttributes.STEALTH.get()) - 1;
    if (stealth == 0) return;
    if (event.getEntity().distanceTo(player)
        > event.getEntity().getAttributeValue(Attributes.FOLLOW_RANGE) * (1 - stealth)) {
      event.setCanceled(true);
    }
  }

  @SubscribeEvent
  public static void applyFishingExperienceBonus(ItemFishedEvent event) {
    Player player = event.getEntity();
    double expBonus = player.getAttributeValue(PSTAttributes.EXPERIENCE_FROM_FISHING.get()) - 1;
    if (expBonus == 0) return;
    int exp = (int) ((player.getRandom().nextInt(6) + 1) * expBonus);
    if (exp == 0) return;
    ExperienceOrb expOrb =
        new ExperienceOrb(
            player.level, player.getX(), player.getY() + 0.5D, player.getZ() + 0.5D, exp);
    player.level.addFreshEntity(expOrb);
  }

  @SubscribeEvent
  public static void applyChanceToIgnite(LivingHurtEvent event) {
    if (!(event.getSource().getEntity() instanceof Player player)) return;
    double chance = player.getAttributeValue(PSTAttributes.CHANCE_TO_IGNITE.get()) - 1;
    if (player.getRandom().nextFloat() >= chance) return;
    event.getEntity().setSecondsOnFire(5);
  }

  @SubscribeEvent
  public static void applyChanceToExplodeEnemy(LivingHurtEvent event) {
    if (!(event.getSource().getEntity() instanceof Player player)) return;
    double chance = player.getAttributeValue(PSTAttributes.CHANCE_TO_EXPLODE_ENEMY.get()) - 1;
    if (player.getRandom().nextFloat() >= chance) return;
    LivingEntity target = event.getEntity();
    target.level.explode(
        player, target.getX(), target.getEyeY(), target.getZ(), 2F, BlockInteraction.NONE);
  }

  @SubscribeEvent
  public static void applyGrindstoneExpPenalty(GrindstoneEvent.OnTakeItem event) {
    event.setXp((int) (event.getXp() * Config.grindstone_exp_multiplier));
  }
}
