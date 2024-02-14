package daripher.skilltree.skill.bonus;

import com.mojang.datafixers.util.Either;
import daripher.itemproduction.event.ItemProducedEvent;
import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.effect.SkillBonusEffect;
import daripher.skilltree.entity.EquippedEntity;
import daripher.skilltree.entity.player.PlayerHelper;
import daripher.skilltree.item.ItemBonusProvider;
import daripher.skilltree.item.ItemHelper;
import daripher.skilltree.mixin.AbstractArrowAccessor;
import daripher.skilltree.skill.PassiveSkill;
import daripher.skilltree.skill.bonus.event.AttackEventListener;
import daripher.skilltree.skill.bonus.event.BlockEventListener;
import daripher.skilltree.skill.bonus.event.DamageTakenEventListener;
import daripher.skilltree.skill.bonus.event.ItemUsedEventListener;
import daripher.skilltree.skill.bonus.item.FoodHealingBonus;
import daripher.skilltree.skill.bonus.item.ItemBonus;
import daripher.skilltree.skill.bonus.item.ItemSkillBonus;
import daripher.skilltree.skill.bonus.player.*;
import java.util.*;
import java.util.function.BiConsumer;
import javax.annotation.Nonnull;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.StringUtils;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.event.CurioAttributeModifierEvent;
import top.theillusivec4.curios.api.event.CurioEquipEvent;

@Mod.EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class SkillBonusHandler {
  @SubscribeEvent
  public static void applyBreakSpeedMultiplier(PlayerEvent.BreakSpeed event) {
    Player player = event.getEntity();
    float multiplier = 1f;
    for (BlockBreakSpeedBonus bonus : getSkillBonuses(player, BlockBreakSpeedBonus.class)) {
      if (bonus.getPlayerCondition().met(player)) {
        multiplier += bonus.getMultiplier();
      }
    }
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
    if (!stack.getItem().isValidRepairItem(stack, material)) return;
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
    getSkillBonuses(player, SkillBonus.Ticking.class).forEach(bonus -> bonus.tick(player));
  }

  @SubscribeEvent(priority = EventPriority.HIGH)
  public static void applyFlatDamageBonus(LivingHurtEvent event) {
    if (!(event.getSource().getEntity() instanceof Player player)) return;
    player.getPersistentData().putInt("LastAttackTarget", event.getEntity().getId());
    float bonus =
        getDamageBonus(
            player, event.getSource(), event.getEntity(), AttributeModifier.Operation.ADDITION);
    event.setAmount(event.getAmount() + bonus);
  }

  @SubscribeEvent
  public static void applyBaseDamageMultipliers(LivingHurtEvent event) {
    if (!(event.getSource().getEntity() instanceof Player player)) return;
    float bonus =
        getDamageBonus(
            player,
            event.getSource(),
            event.getEntity(),
            AttributeModifier.Operation.MULTIPLY_BASE);
    event.setAmount(event.getAmount() * (1 + bonus));
  }

  @SubscribeEvent(priority = EventPriority.LOW)
  public static void applyTotalDamageMultipliers(LivingHurtEvent event) {
    if (!(event.getSource().getEntity() instanceof Player player)) return;
    float bonus =
        getDamageBonus(
            player,
            event.getSource(),
            event.getEntity(),
            AttributeModifier.Operation.MULTIPLY_TOTAL);
    event.setAmount(event.getAmount() * (1 + bonus));
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
    DamageSource damageSource = DamageSource.playerAttack(player);
    float critChance = getCritChance(player, damageSource, event.getEntity());
    if (player.getRandom().nextFloat() >= critChance) return;
    float critMultiplier = getCritDamageMultiplier(player, damageSource, target);
    event.setDamageModifier(event.getDamageModifier() + critMultiplier);
    if (!event.isVanillaCritical()) {
      event.setDamageModifier(event.getDamageModifier() + 0.5F);
      event.setResult(Event.Result.ALLOW);
    }
  }

  @SubscribeEvent(priority = EventPriority.LOW)
  public static void applyCritBonuses(LivingHurtEvent event) {
    // direct damage, ignoring
    if (event.getSource().getClass() == EntityDamageSource.class
        && event.getSource().msgId.equals("player")) return;
    if (!(event.getSource().getEntity() instanceof ServerPlayer player)) return;
    float critChance = getCritChance(player, event.getSource(), event.getEntity());
    if (player.getRandom().nextFloat() >= critChance) return;
    float critMultiplier = getCritDamageMultiplier(player, event.getSource(), event.getEntity());
    event.setAmount(event.getAmount() * critMultiplier);
  }

  private static float getCritDamageMultiplier(
      ServerPlayer player, DamageSource source, LivingEntity target) {
    float multiplier = 1.5f;
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
  public static void addAdditionalSocketTooltip(ItemTooltipEvent event) {
    ItemStack stack = event.getItemStack();
    int sockets = ItemHelper.getAdditionalSockets(stack);
    if (sockets > 0) {
      String key = "gem.additional_socket_" + sockets;
      Component socketTooltip = Component.translatable(key).withStyle(ChatFormatting.YELLOW);
      event.getToolTip().add(1, socketTooltip);
    }
  }

  @SubscribeEvent
  public static void addCraftedItemSkillBonusTooltips(ItemTooltipEvent event) {
    List<Component> components = event.getToolTip();
    for (ItemBonus<?> itemBonus : ItemHelper.getItemBonusesExcludingGems(event.getItemStack())) {
      if (itemBonus instanceof ItemSkillBonus skillBonus) {
        SkillBonus<?> bonus = skillBonus.getBonus();
        if (bonus instanceof AttributeBonus) {
          MutableComponent tooltip = bonus.getTooltip();
          components.add(tooltip);
        }
      }
    }
  }

  @SubscribeEvent
  public static void setCraftedItemBonus(ItemProducedEvent event) {
    ItemStack stack = event.getStack();
    Player player = event.getPlayer();
    ItemHelper.removeItemBonuses(stack);
    getSkillBonuses(player, CraftedItemBonus.class).forEach(bonus -> bonus.itemCrafted(stack));
    ItemHelper.getItemBonuses(stack, ItemBonus.class).forEach(bonus -> bonus.itemCrafted(stack));
    ItemHelper.refreshDurabilityBonuses(stack);
  }

  @SubscribeEvent
  public static void applyCraftedItemAttributeBonuses(ItemAttributeModifierEvent event) {
    ItemStack stack = event.getItemStack();
    if (event.getSlotType() != Player.getEquipmentSlotForItem(stack)) return;
    addAttributeModifiers(event::addModifier, stack);
  }

  @SubscribeEvent
  public static void applyCraftedCurioAttributeBonuses(CurioAttributeModifierEvent event) {
    ItemStack stack = event.getItemStack();
    if (!CuriosApi.getCuriosHelper().isStackValid(event.getSlotContext(), stack)) return;
    addAttributeModifiers(event::addModifier, stack);
  }

  @SubscribeEvent
  public static void applyFoodHealing(LivingEntityUseItemEvent.Finish event) {
    ItemStack stack = event.getItem();
    if (stack.getFoodProperties(event.getEntity()) == null) return;
    float healing = 0f;
    for (FoodHealingBonus bonus : ItemHelper.getItemBonuses(stack, FoodHealingBonus.class)) {
      healing += bonus.getAmount();
    }
    event.getEntity().heal(healing);
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

  @SubscribeEvent(priority = EventPriority.LOWEST)
  public static void applyLootDuplicationChanceBonus(LivingDropsEvent event) {
    // shouldn't multiply player's loot
    if (event.getEntity() instanceof Player) return;
    if (!(event.getSource().getEntity() instanceof Player player)) return;
    float multiplier = getLootMultiplier(player, LootDuplicationBonus.LootType.MOBS);
    while (multiplier > 1) {
      event.getDrops().addAll(getDrops(event));
      multiplier--;
    }
    if (player.getRandom().nextFloat() < multiplier) {
      event.getDrops().addAll(getDrops(event));
    }
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
    if (!event.getState().is(Tags.Blocks.ORES)) return;
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
            player.level, player.getX(), player.getY() + 0.5D, player.getZ() + 0.5D, exp);
    player.level.addFreshEntity(expOrb);
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
      for (EventListenerBonus<?> bonus : getSkillBonuses(player, EventListenerBonus.class, true)) {
        if (!(bonus.getEventListener() instanceof AttackEventListener listener)) continue;
        SkillBonus<? extends EventListenerBonus<?>> copy = bonus.copy();
        listener.onEvent(
            player, event.getEntity(), event.getSource(), (EventListenerBonus<?>) copy);
      }
    }
    if (event.getEntity() instanceof Player player) {
      for (EventListenerBonus<?> bonus : getSkillBonuses(player, EventListenerBonus.class, true)) {
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
    for (EventListenerBonus<?> bonus : getSkillBonuses(player, EventListenerBonus.class, true)) {
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
    for (EventListenerBonus<?> bonus : getSkillBonuses(player, EventListenerBonus.class, true)) {
      if (!(bonus.getEventListener() instanceof ItemUsedEventListener listener)) continue;
      SkillBonus<? extends EventListenerBonus<?>> copy = bonus.copy();
      listener.onEvent(player, event.getItem(), (EventListenerBonus<?>) copy);
    }
  }

  @SubscribeEvent
  public static void applyArrowRetrievalBonus(LivingHurtEvent event) {
    if (!(event.getSource().getDirectEntity() instanceof AbstractArrow arrow)) return;
    if (!(event.getSource().getEntity() instanceof Player player)) return;
    AbstractArrowAccessor arrowAccessor = (AbstractArrowAccessor) arrow;
    ItemStack arrowStack = arrowAccessor.invokeGetPickupItem();
    if (arrowStack == null) return;
    float retrievalChance = 0f;
    for (ArrowRetrievalBonus bonus : getSkillBonuses(player, ArrowRetrievalBonus.class)) {
      retrievalChance += bonus.getChance();
    }
    if (player.getRandom().nextFloat() >= retrievalChance) return;
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
    float reservation = 0f;
    for (HealthReservationBonus bonus :
        getSkillBonuses(event.player, HealthReservationBonus.class)) {
      reservation += bonus.getAmount(event.player);
    }
    if (reservation == 0) return;
    if (event.player.getHealth() / event.player.getMaxHealth() > 1 - reservation) {
      event.player.setHealth(event.player.getMaxHealth() * (1 - reservation));
    }
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
        MutableComponent tooltip =
            Component.translatable("item.cant_use.info").withStyle(ChatFormatting.RED);
        event.getTooltipElements().add(Either.left(tooltip));
        return;
      }
    }
  }

  public static float getLootMultiplier(Player player, LootDuplicationBonus.LootType lootType) {
    Map<Float, Float> multipliers = getLootMultipliers(player, lootType);
    float multiplier = 0f;
    for (Map.Entry<Float, Float> entry : multipliers.entrySet()) {
      float chance = entry.getValue();
      while (chance > 1) {
        multiplier += entry.getKey();
        chance--;
      }
      if (player.getRandom().nextFloat() < chance) {
        multiplier += entry.getKey();
      }
    }
    return multiplier;
  }

  @Nonnull
  private static Map<Float, Float> getLootMultipliers(
      Player player, LootDuplicationBonus.LootType lootType) {
    Map<Float, Float> multipliers = new HashMap<>();
    for (LootDuplicationBonus b : getSkillBonuses(player, LootDuplicationBonus.class)) {
      if (b.getLootType() != lootType) continue;
      float chance = b.getChance() + multipliers.getOrDefault(b.getMultiplier(), 0f);
      multipliers.put(b.getMultiplier(), chance);
    }
    return multipliers;
  }

  protected static List<ItemEntity> getDrops(LivingDropsEvent event) {
    List<ItemEntity> drops = new ArrayList<>();
    for (ItemEntity itemEntity : event.getDrops()) {
      ItemEntity copy = itemEntity.copy();
      drops.add(copy);
    }
    if (event.getEntity() instanceof EquippedEntity entity) drops.removeIf(entity::hasItemEquipped);
    return drops;
  }

  private static void addAttributeModifiers(
      BiConsumer<Attribute, AttributeModifier> addFunction, ItemStack stack) {
    for (ItemBonus<?> itemBonus : ItemHelper.getItemBonuses(stack)) {
      if (itemBonus instanceof ItemSkillBonus itemSkillBonus) {
        SkillBonus<?> bonus = itemSkillBonus.getBonus();
        if (bonus instanceof AttributeBonus attributeBonus) {
          if (!attributeBonus.hasMultiplier() && !attributeBonus.hasCondition()) {
            addFunction.accept(attributeBonus.getAttribute(), attributeBonus.getModifier());
          }
        }
      }
    }
  }

  public static float getJumpHeightMultiplier(Player player) {
    float multiplier = 1f;
    for (JumpHeightBonus bonus : getSkillBonuses(player, JumpHeightBonus.class)) {
      multiplier += bonus.getJumpHeightMultiplier(player);
    }
    return multiplier;
  }

  public static void amplifyEnchantments(
      List<EnchantmentInstance> enchantments, RandomSource random, Player player) {
    enchantments.replaceAll(
        enchantmentInstance -> amplifyEnchantment(enchantmentInstance, random, player));
  }

  private static EnchantmentInstance amplifyEnchantment(
      EnchantmentInstance enchantment, RandomSource random, Player player) {
    if (enchantment.enchantment.getMaxLevel() == 1) {
      return enchantment;
    }
    float amplificationChance = getAmplificationChance(enchantment, player);
    if (amplificationChance == 0) return enchantment;
    int levelBonus = (int) amplificationChance;
    amplificationChance -= levelBonus;
    int enchantmentLevel = enchantment.level + levelBonus;
    if (random.nextFloat() < amplificationChance) enchantmentLevel++;
    return new EnchantmentInstance(enchantment.enchantment, enchantmentLevel);
  }

  public static int adjustEnchantmentCost(int cost, Player player) {
    return (int) Math.max(1, cost * getEnchantmentCostMultiplier(player));
  }

  public static float getFreeEnchantmentChance(Player player) {
    float chance = 0f;
    for (FreeEnchantmentBonus bonus :
        SkillBonusHandler.getSkillBonuses(player, FreeEnchantmentBonus.class)) {
      chance += bonus.getChance();
    }
    return chance;
  }

  private static double getEnchantmentCostMultiplier(Player player) {
    float multiplier = 1f;
    for (EnchantmentRequirementBonus bonus :
        SkillBonusHandler.getSkillBonuses(player, EnchantmentRequirementBonus.class)) {
      multiplier += bonus.getMultiplier();
    }
    return multiplier;
  }

  private static float getAmplificationChance(EnchantmentInstance enchantment, Player player) {
    float chance = 0f;
    for (EnchantmentAmplificationBonus bonus :
        SkillBonusHandler.getSkillBonuses(player, EnchantmentAmplificationBonus.class)) {
      if (bonus.getCondition().met(enchantment.enchantment.category)) {
        chance += bonus.getChance();
      }
    }
    return chance;
  }

  public static <T> List<T> getSkillBonuses(@Nonnull Player player, Class<T> type) {
    return getSkillBonuses(player, type, false);
  }

  public static <T> List<T> getSkillBonuses(@Nonnull Player player, Class<T> type, boolean merge) {
    if (!PlayerSkillsProvider.hasSkills(player)) return List.of();
    List<T> bonuses = new ArrayList<>();
    bonuses.addAll(getPlayerBonuses(player, type));
    bonuses.addAll(getEffectBonuses(player, type));
    bonuses.addAll(getEquipmentBonuses(player, type));
    return merge ? mergeSkillBonuses(bonuses) : bonuses;
  }

  private static <T> List<T> mergeSkillBonuses(List<T> bonuses) {
    List<T> mergedBonuses = new ArrayList<>();
    for (T bonus : bonuses) {
      List<T> mergedCopy = new ArrayList<>(mergedBonuses);
      for (int i = 0; i < mergedCopy.size(); i++) {
        SkillBonus<?> merged = (SkillBonus<?>) mergedCopy.get(i);
        if (merged.canMerge((SkillBonus<?>) bonus)) {
          mergedBonuses.set(i, (T) merged.merge((SkillBonus<?>) bonus));
          break;
        }
      }
      mergedBonuses.add(bonus);
    }
    return mergedBonuses;
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
    List<ItemBonus<?>> itemBonuses = new ArrayList<>();
    if (stack.getItem() instanceof ItemBonusProvider provider) {
      itemBonuses.addAll(provider.getItemBonuses());
    }
    itemBonuses.addAll(ItemHelper.getItemBonuses(stack));
    List<T> bonuses = new ArrayList<>();
    for (ItemBonus<?> itemBonus : itemBonuses) {
      if (itemBonus instanceof ItemSkillBonus skillBonus) {
        SkillBonus<?> bonus = skillBonus.getBonus();
        if (type.isInstance(bonus)) {
          bonuses.add(type.cast(bonus));
        }
      }
    }
    return bonuses;
  }
}
