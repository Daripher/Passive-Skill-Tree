package daripher.skilltree.event;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.skill.bonus.predicate.item.EquipmentPredicate;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class PoisonedWeaponEvents {
    public static final String WEAPON_EFFECTS_TAG_NAME = "poisoned_weapon_effects";
    public static final String POISON_USES_LEFT_TAG_NAME = "poisoned_weapon_uses_left";

    @SubscribeEvent
    public static void applyPoisonedWeaponEffect(LivingIncomingDamageEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) {
            return;
        }
        ItemStack mainHandItem = player.getMainHandItem();
        if (!EquipmentPredicate.isMeleeWeapon(mainHandItem)) {
            return;
        }
        if (!hasPoison(mainHandItem)) {
            return;
        }
        getPoisonedWeaponEffects(mainHandItem).forEach(pEffectInstance -> event.getEntity().addEffect(pEffectInstance, player));
        if (!hasInfinitePoisonUses(mainHandItem)) {
            consumePoisonStack(mainHandItem);
        }
    }

    public static void setPoisonedWeaponEffects(ItemStack itemStack, ItemStack potionStack, int maxUses) {
        PotionContents potionContents = potionStack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
        ListTag effectsTagList = new ListTag();
        potionContents.getAllEffects().forEach(effect -> effectsTagList.add(effect.save()));
        CustomData.update(DataComponents.CUSTOM_DATA, itemStack, itemTag -> {
            itemTag.put(WEAPON_EFFECTS_TAG_NAME, effectsTagList);
            itemTag.putInt(POISON_USES_LEFT_TAG_NAME, maxUses);
        });
    }

    public static boolean hasPoison(ItemStack itemStack) {
        CompoundTag itemTag = getCustomData(itemStack);
        if (!itemTag.contains(WEAPON_EFFECTS_TAG_NAME, Tag.TAG_LIST)) {
            return false;
        }
        if (itemTag.getList(WEAPON_EFFECTS_TAG_NAME, Tag.TAG_COMPOUND).isEmpty()) {
            return false;
        }
        return hasInfinitePoisonUses(itemStack) || getPoisonUses(itemStack) > 0;
    }

    public static boolean hasInfinitePoisonUses(ItemStack itemStack) {
        int usesLeft = getPoisonUses(itemStack);
        return usesLeft == -1;
    }

    public static int getPoisonUses(ItemStack itemStack) {
        CompoundTag itemTag = getCustomData(itemStack);
        return itemTag.getInt(POISON_USES_LEFT_TAG_NAME);
    }

    public static List<MobEffectInstance> getPoisonedWeaponEffects(ItemStack itemStack) {
        if (!hasPoison(itemStack)) {
            return List.of();
        }
        CompoundTag itemTag = getCustomData(itemStack);
        List<MobEffectInstance> effects = new ArrayList<>();
        ListTag effectsListTag = itemTag.getList(WEAPON_EFFECTS_TAG_NAME, Tag.TAG_COMPOUND);
        effectsListTag.stream().map(CompoundTag.class::cast).map(MobEffectInstance::load).forEach(effects::add);
        return effects;
    }

    private static void consumePoisonStack(ItemStack itemStack) {
        if (!hasPoison(itemStack)) {
            return;
        }
        int usesLeft = getPoisonUses(itemStack) - 1;
        if (usesLeft == 0) {
            clearWeaponEffects(itemStack);
            return;
        }
        CustomData.update(DataComponents.CUSTOM_DATA, itemStack, itemTag -> itemTag.putInt(POISON_USES_LEFT_TAG_NAME, usesLeft));
    }

    private static void clearWeaponEffects(ItemStack itemStack) {
        CustomData.update(DataComponents.CUSTOM_DATA, itemStack, itemTag -> {
            itemTag.remove(WEAPON_EFFECTS_TAG_NAME);
            itemTag.remove(POISON_USES_LEFT_TAG_NAME);
        });
    }

    private static CompoundTag getCustomData(ItemStack itemStack) {
        return itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
    }
}
