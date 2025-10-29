package daripher.skilltree.skill.bonus;

import com.mojang.datafixers.util.Either;
import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.effect.SkillBonusEffect;
import daripher.skilltree.entity.player.PlayerHelper;
import daripher.skilltree.mixin.AbstractArrowAccessor;
import daripher.skilltree.mixin.MobEffectInstanceAccessor;
import daripher.skilltree.skill.PassiveSkill;
import daripher.skilltree.skill.bonus.condition.damage.DamageCondition;
import daripher.skilltree.skill.bonus.event.*;
import daripher.skilltree.skill.bonus.item.ItemBonus;
import daripher.skilltree.skill.bonus.item.ItemBonusHandler;
import daripher.skilltree.skill.bonus.item.ItemSkillBonus;
import daripher.skilltree.skill.bonus.player.*;
import java.util.*;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.Input;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.MovementInputUpdateEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.event.CurioEquipEvent;

@Mod.EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class SkillBonusHandler {
  @SubscribeEvent
  public static void applyBreakSpeedMultiplier(PlayerEvent.BreakSpeed event) {
    Player player = event.getEntity();
    float multiplier = 1f;
    multiplier +=
        getSkillBonuses(player, BlockBreakSpeedBonus.class).stream()
            .map(b -> b.getMultiplier(player))
            .reduce(Float::sum)
            .orElse(0f);
    event.setNewSpeed(event.getNewSpeed() * multiplier);
  }

  @SubscribeEvent
  public static void applyFallReductionMultiplier(LivingFallEvent event) {
    if (!(event.getEntity() instanceof Player player)) return;
    float multiplier = getJumpHeightMultiplier(player);
    if (multiplier <= 1) return;
    event.setDistance(event.getDistance() / multiplier);
  }

  @SubscribeEvent
  public static void applyRepairEfficiency(AnvilUpdateEvent event) {
    Player player = event.getPlayer();
    ItemStack stack = event.getLeft();
    float efficiency = getRepairEfficiency(player, stack);
    if (efficiency == 1) return;
    if (!stack.isDamageableItem() || !stack.isDamaged()) return;
    ItemStack material = event.getRight();
    if (!stack.getItem().isValidRepairItem(stack, material)) {
      return;
    }
    ItemStack result = stack.copy();
    int durabilityPerMaterial = (int) (result.getMaxDamage() * 12 * (1 + efficiency) / 100);
    int durabilityRestored = durabilityPerMaterial;
    int materialsUsed = 0;
    int cost = 0;
    while (durabilityRestored > 0 && materialsUsed < material.getCount()) {
      result.setDamageValue(result.getDamageValue() - durabilityRestored);
      cost++;
      durabilityRestored = Math.min(result.getDamageValue(), durabilityPerMaterial);
      materialsUsed++;
    }
    if (event.getName() != null && !StringUtils.isBlank(event.getName())) {
      if (!event.getName().equals(stack.getHoverName().getString())) {
        cost++;
        result.setHoverName(Component.literal(event.getName()));
      }
    } else if (stack.hasCustomHoverName()) {
      cost++;
      result.resetHoverName();
    }
    event.setMaterialCost(materialsUsed);
    event.setCost(cost);
    event.setOutput(result);
  }

  private static float getRepairEfficiency(Player player, ItemStack stack) {
    float efficiency = 1f;
    for (RepairEfficiencyBonus bonus : getSkillBonuses(player, RepairEfficiencyBonus.class)) {
      if (bonus.getItemCondition().met(stack)) {
        efficiency += bonus.getMultiplier();
      }
    }
    return efficiency;
  }

  @SubscribeEvent
  public static void tickSkillBonuses(TickEvent.PlayerTickEvent event) {
    if (event.player.isDeadOrDying()) return;
    if (!(event.player instanceof ServerPlayer player)) return;
    if (event.phase == TickEvent.Phase.END) return;
    getSkillBonuses(player, TickingSkillBonus.class).forEach(bonus -> bonus.tick(player));
  }

  @SubscribeEvent(priority = EventPriority.HIGH)
  public static void applyFlatDamageBonus(LivingHurtEvent event) {
    Player attacker = getPlayerAttacker(event);
    if (attacker == null) return;
    LivingEntity target = event.getEntity();
    setLastTarget(attacker, target);
    float bonus =
        getDamageBonus(attacker, event.getSource(), target, AttributeModifier.Operation.ADDITION);
    event.setAmount(event.getAmount() + bonus);
  }

  private static void setLastTarget(Player attacker, LivingEntity target) {
    CompoundTag dataTag = attacker.getPersistentData();
    dataTag.putInt("LastAttackTarget", target.getId());
  }

  @SubscribeEvent
  public static void applyBaseDamageMultipliers(LivingHurtEvent event) {
    Player attacker = getPlayerAttacker(event);
    if (attacker == null) return;
    float bonus =
        getDamageBonus(
            attacker,
            event.getSource(),
            event.getEntity(),
            AttributeModifier.Operation.MULTIPLY_BASE);
    event.setAmount(event.getAmount() * (1 + bonus));
  }

  @SubscribeEvent(priority = EventPriority.LOW)
  public static void applyTotalDamageMultipliers(LivingHurtEvent event) {
    Player attacker = getPlayerAttacker(event);
    if (attacker == null) return;
    float bonus =
        getDamageBonus(
            attacker,
            event.getSource(),
            event.getEntity(),
            AttributeModifier.Operation.MULTIPLY_TOTAL);
    event.setAmount(event.getAmount() * (1 + bonus));
  }

  private static @Nullable Player getPlayerAttacker(LivingHurtEvent event) {
    Player attacker = null;
    if (event.getSource().getEntity() instanceof Player player) {
      attacker = player;
    } else if (event.getSource().getDirectEntity() instanceof Player player) {
      attacker = player;
    }
    return attacker;
  }

  private static float getDamageBonus(
      Player player,
      DamageSource damageSource,
      LivingEntity target,
      AttributeModifier.Operation operation) {
    float amount = 0;
    for (DamageBonus bonus : getSkillBonuses(player, DamageBonus.class)) {
      amount += bonus.getDamageBonus(operation, damageSource, player, target);
    }
    return amount;
  }

  @SubscribeEvent
  public static void applyCritBonuses(CriticalHitEvent event) {
    if (!(event.getEntity() instanceof ServerPlayer player)) return;
    if (!(event.getTarget() instanceof LivingEntity target)) return;
    DamageSource damageSource = player.level().damageSources().playerAttack(player);
    float critChance = getCritChance(player, damageSource, event.getEntity());
    if (player.getRandom().nextFloat() >= critChance) {
      return;
    }
    float critMultiplier = event.getDamageModifier();
    critMultiplier += getCritDamageMultiplier(player, damageSource, target);
    if (!event.isVanillaCritical()) {
      critMultiplier += 0.5f;
      event.setResult(Event.Result.ALLOW);
    }
    event.setDamageModifier(critMultiplier);
  }

  @SubscribeEvent(priority = EventPriority.LOW)
  public static void applyCritBonuses(LivingHurtEvent event) {
    // direct damage, ignoring
    if (event.getSource().getDirectEntity() instanceof Player) {
      return;
    }
    if (!(event.getSource().getEntity() instanceof ServerPlayer player)) {
      return;
    }
    float critChance = getCritChance(player, event.getSource(), event.getEntity());
    if (player.getRandom().nextFloat() >= critChance) {
      return;
    }
    float critMultiplier = 1.5f;
    critMultiplier += getCritDamageMultiplier(player, event.getSource(), event.getEntity());
    event.setAmount(event.getAmount() * critMultiplier);
  }

  private static float getCritDamageMultiplier(
      ServerPlayer player, DamageSource source, LivingEntity target) {
    float multiplier = 0f;
    for (CritDamageBonus bonus : getSkillBonuses(player, CritDamageBonus.class)) {
      multiplier += bonus.getDamageBonus(source, player, target);
    }
    return multiplier;
  }

  private static float getCritChance(
      ServerPlayer player, DamageSource source, LivingEntity target) {
    float critChance = 0f;
    for (CritChanceBonus bonus : getSkillBonuses(player, CritChanceBonus.class)) {
      critChance += bonus.getChanceBonus(source, player, target);
    }
    return critChance;
  }

  @SubscribeEvent
  public static void applyIncomingHealingBonus(LivingHealEvent event) {
    if (!(event.getEntity() instanceof Player player)) return;
    float multiplier = 1f;
    for (IncomingHealingBonus bonus : getSkillBonuses(player, IncomingHealingBonus.class)) {
      multiplier += bonus.getHealingMultiplier(player);
    }
    event.setAmount(event.getAmount() * multiplier);
  }

  @SubscribeEvent
  public static void applyExperienceFromMobsBonus(LivingExperienceDropEvent event) {
    Player player = event.getAttackingPlayer();
    if (player == null) return;
    float multiplier = 1f;
    multiplier += getExperienceMultiplier(player, GainedExperienceBonus.ExperienceSource.MOBS);
    event.setDroppedExperience((int) (event.getDroppedExperience() * multiplier));
  }

  @SubscribeEvent
  public static void applyExperienceFromOreBonus(BlockEvent.BreakEvent event) {
    if (!event.getState().is(Tags.Blocks.ORES)) {
      return;
    }
    float multiplier = 1f;
    multiplier +=
        getExperienceMultiplier(event.getPlayer(), GainedExperienceBonus.ExperienceSource.ORE);
    event.setExpToDrop((int) (event.getExpToDrop() * multiplier));
  }

  @SubscribeEvent
  public static void applyFishingExperienceBonus(ItemFishedEvent event) {
    Player player = event.getEntity();
    float multiplier =
        getExperienceMultiplier(player, GainedExperienceBonus.ExperienceSource.FISHING);
    if (multiplier == 0) return;
    int exp = (int) ((player.getRandom().nextInt(6) + 1) * multiplier);
    if (exp == 0) return;
    ExperienceOrb expOrb =
        new ExperienceOrb(
            player.level(), player.getX(), player.getY() + 0.5D, player.getZ() + 0.5D, exp);
    player.level().addFreshEntity(expOrb);
  }

  private static float getExperienceMultiplier(
      Player player, GainedExperienceBonus.ExperienceSource source) {
    float multiplier = 0f;
    for (GainedExperienceBonus bonus : getSkillBonuses(player, GainedExperienceBonus.class)) {
      if (bonus.getSource() == source) {
        multiplier += bonus.getMultiplier();
      }
    }
    return multiplier;
  }

  @SubscribeEvent
  public static void applyEventListenerEffect(LivingHurtEvent event) {
    Entity sourceEntity = event.getSource().getEntity();
    if (sourceEntity instanceof Player player) {
      for (EventListenerBonus<?> bonus : getSkillBonuses(player, EventListenerBonus.class)) {
        if (!(bonus.getEventListener() instanceof AttackEventListener listener)) continue;
        SkillBonus<? extends EventListenerBonus<?>> copy = bonus.copy();
        listener.onEvent(
            player, event.getEntity(), event.getSource(), (EventListenerBonus<?>) copy);
      }
    }
    if (event.getEntity() instanceof Player player) {
      for (EventListenerBonus<?> bonus : getSkillBonuses(player, EventListenerBonus.class)) {
        if (!(bonus.getEventListener() instanceof DamageTakenEventListener listener)) continue;
        SkillBonus<? extends EventListenerBonus<?>> copy = bonus.copy();
        LivingEntity attacker =
            sourceEntity instanceof LivingEntity ? (LivingEntity) sourceEntity : null;
        listener.onEvent(player, attacker, event.getSource(), (EventListenerBonus<?>) copy);
      }
    }
  }

  @SubscribeEvent
  public static void applyEventListenerEffect(ShieldBlockEvent event) {
    if (!(event.getEntity() instanceof Player player)) return;
    for (EventListenerBonus<?> bonus : getSkillBonuses(player, EventListenerBonus.class)) {
      if (!(bonus.getEventListener() instanceof BlockEventListener listener)) continue;
      SkillBonus<? extends EventListenerBonus<?>> copy = bonus.copy();
      DamageSource source = event.getDamageSource();
      Entity sourceEntity = source.getEntity();
      LivingEntity attacker =
          sourceEntity instanceof LivingEntity ? (LivingEntity) sourceEntity : null;
      listener.onEvent(player, attacker, source, (EventListenerBonus<?>) copy);
    }
  }

  @SubscribeEvent(priority = EventPriority.LOWEST)
  public static void applyEventListenerEffect(LivingEntityUseItemEvent.Finish event) {
    if (!(event.getEntity() instanceof Player player)) return;
    for (EventListenerBonus<?> bonus : getSkillBonuses(player, EventListenerBonus.class)) {
      if (!(bonus.getEventListener() instanceof ItemUsedEventListener listener)) continue;
      SkillBonus<? extends EventListenerBonus<?>> copy = bonus.copy();
      listener.onEvent(player, event.getItem(), (EventListenerBonus<?>) copy);
    }
  }

  @SubscribeEvent
  public static void applyEventListenerEffect(LivingDeathEvent event) {
    if (!(event.getSource().getEntity() instanceof Player player)) {
      return;
    }
    for (EventListenerBonus<?> bonus : getSkillBonuses(player, EventListenerBonus.class)) {
      if (!(bonus.getEventListener() instanceof KillEventListener listener)) continue;
      SkillBonus<? extends EventListenerBonus<?>> copy = bonus.copy();
      DamageSource source = event.getSource();
      listener.onEvent(player, player, source, (EventListenerBonus<?>) copy);
    }
  }

  @SubscribeEvent
  public static void applyArrowRetrievalBonus(LivingHurtEvent event) {
    if (!(event.getSource().getDirectEntity() instanceof AbstractArrow arrow)) {
      return;
    }
    if (!(event.getSource().getEntity() instanceof Player player)) {
      return;
    }
    AbstractArrowAccessor arrowAccessor = (AbstractArrowAccessor) arrow;
    ItemStack arrowStack = arrowAccessor.invokeGetPickupItem();
    if (arrowStack == null) return;
    float retrievalChance = 0f;
    for (ArrowRetrievalBonus bonus : getSkillBonuses(player, ArrowRetrievalBonus.class)) {
      retrievalChance += bonus.getChance();
    }
    if (player.getRandom().nextFloat() >= retrievalChance) {
      return;
    }
    LivingEntity target = event.getEntity();
    CompoundTag targetData = target.getPersistentData();
    ListTag stuckArrowsTag = targetData.getList("StuckArrows", new CompoundTag().getId());
    stuckArrowsTag.add(arrowStack.save(new CompoundTag()));
    targetData.put("StuckArrows", stuckArrowsTag);
  }

  @SubscribeEvent
  public static void retrieveArrows(LivingDeathEvent event) {
    LivingEntity entity = event.getEntity();
    ListTag arrowsTag =
        entity.getPersistentData().getList("StuckArrows", new CompoundTag().getId());
    if (arrowsTag.isEmpty()) return;
    for (Tag tag : arrowsTag) {
      ItemStack arrowStack = ItemStack.of((CompoundTag) tag);
      entity.spawnAtLocation(arrowStack);
    }
  }

  @SubscribeEvent
  public static void applyHealthReservationEffect(TickEvent.PlayerTickEvent event) {
    if (event.phase == TickEvent.Phase.END || event.side == LogicalSide.CLIENT) return;
    float reservation = getHealthReservation(event.player);
    if (reservation == 0) return;
    if (event.player.getHealth() / event.player.getMaxHealth() > 1 - reservation) {
      event.player.setHealth(event.player.getMaxHealth() * (1 - reservation));
    }
  }

  @SubscribeEvent(priority = EventPriority.LOWEST)
  public static void applyHealthReservationEffect(LivingHealEvent event) {
    if (!(event.getEntity() instanceof Player player)) return;
    float reservation = getHealthReservation(player);
    if (reservation == 0) return;
    float healthAfterHealing = player.getHealth() + event.getAmount();
    if (healthAfterHealing / player.getMaxHealth() > 1 - reservation) {
      event.setCanceled(true);
    }
  }

  private static float getHealthReservation(Player player) {
    float reservation = 0f;
    for (HealthReservationBonus bonus : getSkillBonuses(player, HealthReservationBonus.class)) {
      reservation += bonus.getAmount(player);
    }
    return reservation;
  }

  @SubscribeEvent
  public static void applyCantUseItemBonus(AttackEntityEvent event) {
    for (CantUseItemBonus bonus : getSkillBonuses(event.getEntity(), CantUseItemBonus.class)) {
      if (bonus.getItemCondition().met(event.getEntity().getMainHandItem())) {
        event.setCanceled(true);
        return;
      }
    }
  }

  @SubscribeEvent
  public static void applyCantUseItemBonus(PlayerInteractEvent event) {
    for (CantUseItemBonus bonus : getSkillBonuses(event.getEntity(), CantUseItemBonus.class)) {
      if (bonus.getItemCondition().met(event.getItemStack())) {
        event.setCancellationResult(InteractionResult.FAIL);
        if (event.isCancelable()) {
          event.setCanceled(true);
        }
        return;
      }
    }
  }

  @SubscribeEvent
  public static void applyCantUseItemBonus(CurioEquipEvent event) {
    if (!(event.getEntity() instanceof Player player)) return;
    for (CantUseItemBonus bonus : getSkillBonuses(player, CantUseItemBonus.class)) {
      if (bonus.getItemCondition().met(event.getStack())) {
        event.setResult(Event.Result.DENY);
        return;
      }
    }
  }

  @OnlyIn(Dist.CLIENT)
  @SubscribeEvent(priority = EventPriority.LOWEST)
  public static void addCantUseItemTooltip(RenderTooltipEvent.GatherComponents event) {
    Player player = Minecraft.getInstance().player;
    if (player == null) return;
    for (CantUseItemBonus bonus : getSkillBonuses(player, CantUseItemBonus.class)) {
      if (bonus.getItemCondition().met(event.getItemStack())) {
        Component tooltip =
            Component.translatable("item.cant_use.info").withStyle(ChatFormatting.RED);
        event.getTooltipElements().add(Either.left(tooltip));
        return;
      }
    }
  }

  @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
  public static void inflictPoisonForcefully(MobEffectEvent.Applicable event) {
    if (event.getEffectInstance().getEffect() != MobEffects.POISON) {
      return;
    }
    if (!(event.getEntity().getKillCredit() instanceof Player player)) {
      return;
    }
    if (getSkillBonuses(player, CanPoisonAnyoneBonus.class).isEmpty()) return;
    event.setResult(Event.Result.ALLOW);
  }

  @SubscribeEvent(priority = EventPriority.LOWEST)
  public static void applyDamageTakenBonuses(LivingHurtEvent event) {
    if (!(event.getEntity() instanceof Player player)) return;
    DamageSource damageSource = event.getSource();
    if (!(damageSource.getEntity() instanceof LivingEntity attacker)) return;
    float damageTaken = event.getAmount();
    float addition =
        getDamageTaken(player, attacker, damageSource, AttributeModifier.Operation.ADDITION);
    damageTaken += addition;
    float multiplier =
        getDamageTaken(player, attacker, damageSource, AttributeModifier.Operation.MULTIPLY_BASE);
    damageTaken *= 1 + multiplier;
    float multiplierTotal =
        getDamageTaken(player, attacker, damageSource, AttributeModifier.Operation.MULTIPLY_TOTAL);
    damageTaken *= 1 + multiplierTotal;
    event.setAmount(damageTaken);
  }

  @SubscribeEvent(priority = EventPriority.LOWEST)
  public static void applyDamageAvoidanceBonuses(LivingAttackEvent event) {
    if (!(event.getEntity() instanceof Player player)) return;
    DamageSource damageSource = event.getSource();
    if (!(damageSource.getEntity() instanceof LivingEntity attacker)) return;
    float avoidance =
        getSkillBonuses(player, DamageAvoidanceBonus.class).stream()
            .map(b -> b.getAvoidanceChance(damageSource, player, attacker))
            .reduce(Float::sum)
            .orElse(0f);
    if (player.getRandom().nextFloat() < avoidance) {
      event.setCanceled(true);
    }
  }

  @SubscribeEvent(priority = EventPriority.LOWEST)
  public static void applyDamageConversionBonuses(LivingHurtEvent event) {
    DamageSource originalDamageSource = event.getSource();
    if (!(originalDamageSource.getEntity() instanceof Player player)) return;
    if (getDamageConversionBonuses(player, originalDamageSource).findAny().isEmpty()) {
      return;
    }
    LivingEntity target = event.getEntity();
    float originalDamageAmount = event.getAmount();
    getDamageConversionMap(event, player, originalDamageSource)
        .forEach(
            (damageCondition, amount) -> {
              DamageSource damageSource = damageCondition.createDamageSource(player);
              forcefullyInflictDamage(damageSource, amount * originalDamageAmount, target);
            });
    float convertedDamage = getConvertedDamagePercentage(player, originalDamageSource, target);
    event.setAmount(originalDamageAmount * (1 - convertedDamage));
  }

  @SubscribeEvent
  public static void applyEffectDurationBonuses(MobEffectEvent.Added event) {
    Player source = null;
    if (event.getEffectSource() instanceof Player player) {
      source = player;
    }
    if (event.getEffectSource() instanceof Projectile projectile
        && projectile.getOwner() instanceof Player player) {
      source = player;
    }
    final Player playerSource = source;
    float durationMultiplier = 1;
    if (source != null) {
      durationMultiplier +=
          getSkillBonuses(playerSource, EffectDurationBonus.class).stream()
              .filter(b -> b.getTarget() == SkillBonus.Target.ENEMY)
              .map(b -> b.getDuration(playerSource, event.getEntity()))
              .reduce(Float::sum)
              .orElse(0f);
    }
    if (event.getEntity() instanceof Player player) {
      durationMultiplier +=
          getSkillBonuses(player, EffectDurationBonus.class).stream()
              .filter(b -> b.getTarget() == SkillBonus.Target.PLAYER)
              .map(b -> b.getDuration(playerSource, player))
              .reduce(Float::sum)
              .orElse(0f);
    }
    if (durationMultiplier == 1) return;
    MobEffectInstance effectInstance = event.getEffectInstance();
    int newDuration = (int) (effectInstance.getDuration() * durationMultiplier);
    ((MobEffectInstanceAccessor) effectInstance).setDuration(newDuration);
  }

  @SubscribeEvent
  public static void applyProjectileDuplicationBonuses(EntityJoinLevelEvent event) {
    if (!(event.getEntity() instanceof Projectile projectile)) return;
    if (!(event.getLevel() instanceof ServerLevel level)) return;
    if (!(projectile.getOwner() instanceof Player player)) return;
    CompoundTag projectileTag = projectile.getPersistentData();
    if (projectileTag.getBoolean("duplicated")) return;
    float duplicationChance =
        getSkillBonuses(player, ProjectileDuplicationBonus.class).stream()
            .map(b -> b.getChance(player))
            .reduce(Float::sum)
            .orElse(0f);
    if (duplicationChance == 0) return;
    projectileTag.putBoolean("duplicated", true);
    int projectileAmount = (int) duplicationChance;
    duplicationChance -= projectileAmount;
    RandomSource random = player.getRandom();
    if (random.nextFloat() < duplicationChance) {
      projectileAmount++;
    }
    fireDuplicateProjectiles(projectile, level, player, projectileAmount);
  }

  @SubscribeEvent(priority = EventPriority.HIGH)
  public static void forcefullyInflictDuplicatedProjectileDamage(LivingAttackEvent event) {
    DamageSource damageSource = event.getSource();
    if (!(damageSource.getDirectEntity() instanceof Projectile projectile
        && projectile.getOwner() instanceof Player)) return;
    CompoundTag projectileTag = projectile.getPersistentData();
    if (!(projectileTag.getBoolean("duplicated"))) return;
    LivingEntity target = event.getEntity();
    target.invulnerableTime = 0;
    target.setInvulnerable(false);
  }

  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public static void applyProjectileSpeedBonus(EntityJoinLevelEvent event) {
    if (!(event.getEntity() instanceof Projectile projectile)) return;
    if (!(event.getLevel() instanceof ServerLevel)) return;
    if (!(projectile.getOwner() instanceof Player player)) return;
    CompoundTag projectileTag = projectile.getPersistentData();
    if (projectileTag.getBoolean("speed_applied")) return;
    float speedBonus = 1f;
    speedBonus +=
        getSkillBonuses(player, ProjectileSpeedBonus.class).stream()
            .map(b -> b.getMultiplier(player))
            .reduce(Float::sum)
            .orElse(0f);
    if (speedBonus == 1) return;
    projectileTag.putBoolean("speed_applied", true);
    Vec3 speedBonusVec = new Vec3(speedBonus, speedBonus, speedBonus);
    Vec3 projectileMovement = projectile.getDeltaMovement();
    projectile.setDeltaMovement(projectileMovement.multiply(speedBonusVec));
  }

  @SubscribeEvent
  public static void applyItemUsageSpeed(LivingEntityUseItemEvent.Tick event) {
    if (!(event.getEntity() instanceof Player player)) {
      return;
    }
    float additionalSpeed =
        getSkillBonuses(player, ItemUsageSpeedBonus.class).stream()
            .map(bonus -> bonus.getMultiplier(player, event.getItem()))
            .reduce(Float::sum)
            .orElse(0f);
    if (additionalSpeed == 0) {
      return;
    }
    int useTimeOffset = -1;
    if (additionalSpeed < 0) {
      useTimeOffset = 1;
      additionalSpeed *= -1;
    }
    while (additionalSpeed > 1) {
      event.setDuration(event.getDuration() + useTimeOffset);
      additionalSpeed--;
    }
    if (additionalSpeed > 0.5F) {
      if (event.getEntity().tickCount % 2 == 0) {
        event.setDuration(event.getDuration() + useTimeOffset);
      }
      additionalSpeed -= 0.5F;
    }
    int mod = (int) Math.floor(1 / Math.min(1, additionalSpeed));
    if (event.getEntity().tickCount % mod == 0) {
      event.setDuration(event.getDuration() + useTimeOffset);
    }
  }

  @OnlyIn(Dist.CLIENT)
  @SubscribeEvent
  public static void applyItemUseMovementSpeedBonus(MovementInputUpdateEvent event) {
    Player player = event.getEntity();
    Input input = event.getInput();
    if (player.isUsingItem() && !player.isPassenger()) {
      float defaultPenalty = 0.8f;
      float penaltyReduction =
          getSkillBonuses(player, ItemUseMovementSpeedBonus.class).stream()
              .map(bonus -> bonus.getMultiplier(player, player.getUseItem()))
              .reduce(Float::sum)
              .orElse(0f);
      defaultPenalty += defaultPenalty * penaltyReduction;
      float reductionFactor = 1 - defaultPenalty;
      input.leftImpulse *= reductionFactor;
      input.forwardImpulse *= reductionFactor;
      // counteracts default slow
      input.leftImpulse *= 5;
      input.forwardImpulse *= 5;
    }
  }

  private static void fireDuplicateProjectiles(
      Projectile projectile, ServerLevel level, Player player, int projectileAmount) {
    float spreadAngle = 5f;
    for (int i = 0; i < projectileAmount; i++) {
      int side = (i % 2 == 0 ? 1 : -1);
      int projectileNumber = i / 2 + 1;
      float angleOffset = projectileNumber * side * spreadAngle;
      duplicateProjectileWithOffset(projectile, player, level, angleOffset);
    }
  }

  private static void duplicateProjectileWithOffset(
      Projectile original, Player player, ServerLevel level, float angleOffset) {
    EntityType<?> projectileType = original.getType();
    Projectile duplicate = (Projectile) projectileType.create(level);
    if (duplicate == null) return;
    Vec3 movementVector = original.getDeltaMovement();
    Vec3 rotatedDirection = rotateVector(movementVector, angleOffset);
    Vec3 originalPos = original.position();
    Vec3 duplicatePos = originalPos.add(rotatedDirection.normalize());
    duplicate.setPos(duplicatePos.x, duplicatePos.y, duplicatePos.z);
    duplicate.setDeltaMovement(rotatedDirection);
    duplicate.setOwner(player);
    CompoundTag projectileTag = duplicate.getPersistentData();
    projectileTag.putBoolean("duplicated", true);
    if (duplicate instanceof AbstractArrow duplicateArrow) {
      AbstractArrow originalArrow = (AbstractArrow) original;
      duplicateArrow.pickup = AbstractArrow.Pickup.DISALLOWED;
      float velocity = (float) movementVector.length();
      duplicateArrow.setEnchantmentEffectsFromEntity(player, velocity);
      duplicateArrow.setBaseDamage(originalArrow.getBaseDamage());
    } else if (duplicate instanceof ThrownPotion potion) {
      ThrownPotion originalPotion = (ThrownPotion) original;
      potion.setItem(originalPotion.getItem());
    }
    level.addFreshEntity(duplicate);
  }

  private static Vec3 rotateVector(Vec3 vector, double angleDegrees) {
    double angleRadians = Math.toRadians(angleDegrees);
    double cos = Math.cos(angleRadians);
    double sin = Math.sin(angleRadians);
    double x = vector.x * cos - vector.z * sin;
    double z = vector.x * sin + vector.z * cos;
    return new Vec3(x, vector.y, z);
  }

  private static float getConvertedDamagePercentage(
      Player player, DamageSource originalDamageSource, LivingEntity target) {
    return getDamageConversionBonuses(player, originalDamageSource)
        .map(b -> b.getConversionRate(originalDamageSource, player, target))
        .reduce(Float::sum)
        .orElse(0f);
  }

  @NotNull
  private static Map<DamageCondition, Float> getDamageConversionMap(
      LivingHurtEvent event, Player player, DamageSource originalDamageSource) {
    Map<DamageCondition, Float> conversions = new HashMap<>();
    getDamageConversionBonuses(player, originalDamageSource)
        .forEach(
            bonus -> {
              DamageCondition resultDamageSource = bonus.getResultDamageCondition();
              conversions.put(
                  resultDamageSource,
                  conversions.getOrDefault(resultDamageSource, 0f)
                      + bonus.getConversionRate(originalDamageSource, player, event.getEntity()));
            });
    return conversions;
  }

  @NotNull
  private static Stream<DamageConversionBonus> getDamageConversionBonuses(
      Player player, DamageSource damageSource) {
    return getSkillBonuses(player, DamageConversionBonus.class).stream()
        .filter(b -> b.getOriginalDamageCondition().met(damageSource))
        .filter(b -> !b.getResultDamageCondition().met(damageSource));
  }

  public static void forcefullyInflictDamage(DamageSource source, float amount, Entity entity) {
    MinecraftServer server = entity.getServer();
    if (server == null) return;
    server.tell(
        new TickTask(
            server.getTickCount() + 1,
            () -> {
              entity.invulnerableTime = 0;
              entity.hurt(source, amount);
            }));
  }

  private static float getDamageTaken(
      Player player,
      LivingEntity attacker,
      DamageSource damageSource,
      AttributeModifier.Operation operation) {
    List<DamageTakenBonus> damageTakenBonuses = getSkillBonuses(player, DamageTakenBonus.class);
    return damageTakenBonuses.stream()
        .map(b -> b.getDamageBonus(operation, damageSource, player, attacker))
        .reduce(Float::sum)
        .orElse(0f);
  }

  public static float getJumpHeightMultiplier(Player player) {
    float multiplier = 1f;
    for (JumpHeightBonus bonus : getSkillBonuses(player, JumpHeightBonus.class)) {
      multiplier += bonus.getJumpHeightMultiplier(player);
    }
    return multiplier;
  }

  public static float getFreeEnchantmentChance(@Nonnull Player player) {
    float chance = 0f;
    for (FreeEnchantmentBonus bonus :
        SkillBonusHandler.getSkillBonuses(player, FreeEnchantmentBonus.class)) {
      chance += bonus.getChance();
    }
    return chance;
  }

  public static <T> List<T> getSkillBonuses(@Nonnull Player player, Class<T> type) {
    if (!PlayerSkillsProvider.hasSkills(player)) return List.of();
    List<T> bonuses = new ArrayList<>();
    bonuses.addAll(getPlayerBonuses(player, type));
    bonuses.addAll(getEffectBonuses(player, type));
    bonuses.addAll(getEquipmentBonuses(player, type));
    return bonuses;
  }

  private static <T> List<T> getPlayerBonuses(Player player, Class<T> type) {
    List<T> list = new ArrayList<>();
    for (PassiveSkill skill : PlayerSkillsProvider.get(player).getPlayerSkills()) {
      List<SkillBonus<?>> bonuses = skill.getBonuses();
      for (SkillBonus<?> skillBonus : bonuses) {
        if (type.isInstance(skillBonus)) {
          list.add(type.cast(skillBonus));
        }
      }
    }
    return list;
  }

  private static <T> List<T> getEffectBonuses(Player player, Class<T> type) {
    List<T> bonuses = new ArrayList<>();
    for (MobEffectInstance e : player.getActiveEffects()) {
      if (e.getEffect() instanceof SkillBonusEffect skillEffect) {
        SkillBonus<?> bonus = skillEffect.getBonus().copy();
        if (type.isInstance(bonus)) {
          bonus = bonus.copy().multiply(e.getAmplifier());
          bonuses.add(type.cast(bonus));
        }
      }
    }
    return bonuses;
  }

  private static <T> List<T> getEquipmentBonuses(Player player, Class<T> type) {
    return PlayerHelper.getAllEquipment(player)
        .map(s -> getItemBonuses(s, type))
        .flatMap(List::stream)
        .toList();
  }

  private static <T> List<T> getItemBonuses(ItemStack stack, Class<T> type) {
    List<ItemBonus<?>> itemBonuses = new ArrayList<>(ItemBonusHandler.getItemBonuses(stack));
    List<T> bonuses = new ArrayList<>();
    for (ItemBonus<?> itemBonus : itemBonuses) {
      if (itemBonus instanceof ItemSkillBonus itemSkillBonus) {
        SkillBonus<?> skillBonus = itemSkillBonus.skillBonus();
        if (type.isInstance(skillBonus)) {
          bonuses.add(type.cast(skillBonus));
        }
      }
    }
    return bonuses;
  }
}
