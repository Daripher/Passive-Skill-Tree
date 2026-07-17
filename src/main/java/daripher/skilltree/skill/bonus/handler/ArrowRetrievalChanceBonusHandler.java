package daripher.skilltree.skill.bonus.handler;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.mixin.AbstractArrowAccessor;
import daripher.skilltree.skill.SkillBonusProvider;
import daripher.skilltree.skill.bonus.player.ArrowRetrievalBonus;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import java.util.List;

@EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class ArrowRetrievalChanceBonusHandler {
    public static final String STUCK_ARROWS_TAG_NAME = "StuckArrows";

    @SubscribeEvent
    public static void saveStuckArrows(LivingIncomingDamageEvent event) {
        DamageSource damageSource = event.getSource();
        if (!(damageSource.getDirectEntity() instanceof AbstractArrow arrow)) {
            return;
        }
        if (arrow.pickup != AbstractArrow.Pickup.ALLOWED) {
            return;
        }
        if (!(arrow.getOwner() instanceof Player player)) {
            return;
        }
        AbstractArrowAccessor arrowAccessor = (AbstractArrowAccessor) arrow;
        ItemStack arrowStack = arrowAccessor.invokeGetPickupItem();
        if (arrowStack == null) {
            return;
        }
        List<ArrowRetrievalBonus> skillBonuses = SkillBonusProvider.getSkillBonuses(player, ArrowRetrievalBonus.class);
        if (skillBonuses.isEmpty()) {
            return;
        }
        float retrievalChance = 0f;
        for (ArrowRetrievalBonus bonus : skillBonuses) {
            retrievalChance += bonus.getChance();
        }
        if (player.getRandom().nextFloat() >= retrievalChance) {
            return;
        }
        LivingEntity target = event.getEntity();
        CompoundTag targetPersistentData = target.getPersistentData();
        ListTag stuckArrowsTag = targetPersistentData.getList(STUCK_ARROWS_TAG_NAME, Tag.TAG_COMPOUND);
        stuckArrowsTag.add(arrowStack.save(target.registryAccess(), new CompoundTag()));
        targetPersistentData.put(STUCK_ARROWS_TAG_NAME, stuckArrowsTag);
    }

    @SubscribeEvent
    public static void retrieveArrows(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        CompoundTag entityPersistentData = entity.getPersistentData();
        ListTag arrowsTag = entityPersistentData.getList(STUCK_ARROWS_TAG_NAME, Tag.TAG_COMPOUND);
        if (arrowsTag.isEmpty()) {
            return;
        }
        for (Tag tag : arrowsTag) {
            ItemStack arrowStack = ItemStack.parseOptional(entity.registryAccess(), (CompoundTag) tag);
            entity.spawnAtLocation(arrowStack);
        }
    }
}
