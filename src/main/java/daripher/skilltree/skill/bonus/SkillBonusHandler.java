package daripher.skilltree.skill.bonus;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.effect.SkillBonusEffect;
import daripher.skilltree.item.ItemHelper;
import daripher.skilltree.skill.PassiveSkill;
import daripher.skilltree.skill.bonus.item.FoodHealingBonus;
import daripher.skilltree.skill.bonus.item.ItemBonus;
import daripher.skilltree.skill.bonus.item.ItemSkillBonus;
import daripher.skilltree.skill.bonus.player.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.StringUtils;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.event.CurioAttributeModifierEvent;

@Mod.EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class SkillBonusHandler {
  @SubscribeEvent
  public static void applyBreakSpeedMultiplier(PlayerEvent.BreakSpeed event) {
    Player player = event.getEntity();
    float multiplier =
        getSkillBonuses(player, BlockBreakSpeedBonus.class).stream()
            .filter(bonus -> bonus.livingCondition() == null || bonus.livingCondition().met(player))
            .map(BlockBreakSpeedBonus::multiplier)
            .reduce(Float::sum)
            .orElse(1f);
    event.setNewSpeed(event.getNewSpeed() * multiplier);
  }

  @SubscribeEvent
  public static void applyFallReductionMultiplier(LivingFallEvent event) {
    if (!(event.getEntity() instanceof Player player)) return;
    float multiplier = 1f + getJumpHeightMultiplier(player);
    if (multiplier <= 1) return;
    event.setDistance(event.getDistance() / multiplier);
  }

  @SubscribeEvent
  public static void applyRepairEfficiency(AnvilUpdateEvent event) {
    Player player = event.getPlayer();
    ItemStack stack = event.getLeft();
    float efficiency =
        getSkillBonuses(player, RepairEfficiencyBonus.class).stream()
            .filter(bonus -> bonus.itemCondition().met(stack))
            .map(RepairEfficiencyBonus::multiplier)
            .reduce(Float::sum)
            .orElse(1f);
    if (efficiency == 1) return;
    if (!stack.isDamageableItem() || !stack.isDamaged()) return;
    ItemStack material = event.getRight();
    if (!stack.getItem().isValidRepairItem(stack, material)) return;
    ItemStack result = stack.copy();
    int durabilityPerMaterial = (int) (result.getMaxDamage() * 12 * (1 + efficiency) / 100);
    int durabilityRestored = durabilityPerMaterial;
    int materialsUsed;
    int cost = 0;
    for (materialsUsed = 0;
        durabilityRestored > 0 && materialsUsed < material.getCount();
        materialsUsed++) {
      result.setDamageValue(result.getDamageValue() - durabilityRestored);
      cost++;
      durabilityRestored = Math.min(result.getDamageValue(), durabilityPerMaterial);
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

  @SubscribeEvent
  public static void tickSkillBonuses(TickEvent.PlayerTickEvent event) {
    if (event.player.isDeadOrDying()) return;
    if (!(event.player instanceof ServerPlayer player)) return;
    if (event.phase == TickEvent.Phase.END) return;
    getSkillBonuses(player, SkillBonus.Ticking.class).forEach(bonus -> bonus.tick(player));
  }

  @SubscribeEvent(priority = EventPriority.HIGH)
  public static void applyFlatDamageBonus(LivingHurtEvent event) {
    DamageSource damageSource = event.getSource();
    if (!(damageSource.getEntity() instanceof Player player)) return;
    List<DamageBonus> bonuses = getSkillBonuses(player, DamageBonus.class);
    float amount = 0;
    for (DamageBonus bonus : bonuses) {
      amount +=
          bonus.getDamageBonus(
              AttributeModifier.Operation.ADDITION, damageSource, player, event.getEntity());
    }
    event.setAmount(event.getAmount() + amount);
  }

  @SubscribeEvent
  public static void applyDamageMultipliers(LivingHurtEvent event) {
    DamageSource damageSource = event.getSource();
    if (!(damageSource.getEntity() instanceof Player player)) return;
    List<DamageBonus> bonuses = getSkillBonuses(player, DamageBonus.class);
    float multiplier = 1f;
    for (DamageBonus bonus : bonuses) {
      multiplier +=
          bonus.getDamageBonus(
              AttributeModifier.Operation.MULTIPLY_BASE, damageSource, player, event.getEntity());
    }
    event.setAmount(event.getAmount() * multiplier);
  }

  @SubscribeEvent(priority = EventPriority.LOW)
  public static void applyLastDamageMultipliers(LivingHurtEvent event) {
    DamageSource damageSource = event.getSource();
    if (!(damageSource.getEntity() instanceof Player player)) return;
    List<DamageBonus> bonuses = getSkillBonuses(player, DamageBonus.class);
    float multiplier = 1f;
    for (DamageBonus bonus : bonuses) {
      multiplier +=
          bonus.getDamageBonus(
              AttributeModifier.Operation.MULTIPLY_TOTAL, damageSource, player, event.getEntity());
    }
    event.setAmount(event.getAmount() * multiplier);
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
    List<CritDamageBonus> damageBonuses = getSkillBonuses(player, CritDamageBonus.class);
    float multiplier = 1.5f;
    for (CritDamageBonus bonus : damageBonuses) {
      multiplier += bonus.getDamageBonus(source, player, target);
    }
    return multiplier;
  }

  private static float getCritChance(
      ServerPlayer player, DamageSource source, LivingEntity target) {
    float critChance = 0f;
    List<CritChanceBonus> chanceBonuses = getSkillBonuses(player, CritChanceBonus.class);
    for (CritChanceBonus bonus : chanceBonuses) {
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
  public static void setCraftedItemBonus(PlayerEvent.ItemCraftedEvent event) {
    itemCrafted(event.getEntity(), event.getCrafting());
  }

  @SubscribeEvent
  public static void setCraftedItemBonus(PlayerEvent.ItemSmeltedEvent event) {
    itemCrafted(event.getEntity(), event.getSmelting());
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
    float healing =
        ItemHelper.getItemBonuses(stack, FoodHealingBonus.class).stream()
            .map(FoodHealingBonus::amount)
            .reduce(Float::sum)
            .orElse(0f);
    event.getEntity().heal(healing);
  }

  private static void addAttributeModifiers(
      BiConsumer<Attribute, AttributeModifier> addFunction, ItemStack stack) {
    ItemHelper.getItemBonuses(stack).stream()
        .filter(ItemSkillBonus.class::isInstance)
        .map(ItemSkillBonus.class::cast)
        .map(ItemSkillBonus::bonus)
        .filter(AttributeBonus.class::isInstance)
        .map(AttributeBonus.class::cast)
        .forEach(bonus -> addFunction.accept(bonus.getAttribute(), bonus.getModifier()));
  }

  public static void itemCrafted(Player player, ItemStack stack) {
    getSkillBonuses(player, CraftedItemBonus.class).forEach(bonus -> bonus.itemCrafted(stack));
    ItemHelper.getItemBonuses(stack, ItemBonus.class).forEach(bonus -> bonus.itemCrafted(stack));
  }

  public static float getJumpHeightMultiplier(Player player) {
    return getSkillBonuses(player, JumpHeightBonus.class).stream()
        .filter(bonus -> bonus.livingCondition() == null || bonus.livingCondition().met(player))
        .map(JumpHeightBonus::multiplier)
        .reduce(Float::sum)
        .orElse(1f);
  }

  public static <T> List<T> getSkillBonuses(Player player, Class<T> type) {
    List<T> bonuses = new ArrayList<>();
    PlayerSkillsProvider.get(player).getPlayerSkills().stream()
        .map(PassiveSkill::getBonuses)
        .forEach(b -> b.stream().filter(type::isInstance).map(type::cast).forEach(bonuses::add));
    bonuses.addAll(getEffectBonuses(player, type));
    return bonuses;
  }

  private static <T> List<T> getEffectBonuses(Player player, Class<T> type) {
    List<T> bonuses = new ArrayList<>();
    player
        .getActiveEffects()
        .forEach(
            effect -> {
              if (!(effect.getEffect() instanceof SkillBonusEffect skillEffect)) return;
              SkillBonus<?> bonus = skillEffect.getBonus();
              if (type.isInstance(bonus)) {
                bonus = bonus.multiply(effect.getAmplifier());
                bonuses.add(type.cast(bonus));
              }
            });
    return bonuses;
  }
}
