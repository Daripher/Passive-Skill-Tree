package daripher.skilltree.skill.bonus.handler;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.skill.SkillBonusProvider;
import daripher.skilltree.skill.bonus.player.ProjectileDuplicationBonus;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class ProjectileDuplicationBonusHandler {
    public static final String IS_DUPLICATED_TAG_NAME = "IS_DUPLICATED";

    @SubscribeEvent
    public static void duplicateProjectiles(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof Projectile projectile)) {
            return;
        }
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }
        if (!(projectile.getOwner() instanceof Player player)) {
            return;
        }
        if (event.loadedFromDisk()) {
            return;
        }
        CompoundTag projectileTag = projectile.getPersistentData();
        if (projectileTag.getBoolean(IS_DUPLICATED_TAG_NAME)) {
            return;
        }
        List<ProjectileDuplicationBonus> skillBonuses = SkillBonusProvider.getSkillBonuses(player, ProjectileDuplicationBonus.class);
        if (skillBonuses.isEmpty()) {
            return;
        }
        float duplicationChance = 0f;
        for (ProjectileDuplicationBonus skillBonus : skillBonuses) {
            duplicationChance += skillBonus.getDuplicationChance(player);
        }
        if (duplicationChance == 0) {
            return;
        }
        projectileTag.putBoolean(IS_DUPLICATED_TAG_NAME, true);
        int projectileAmount = (int) duplicationChance;
        duplicationChance -= projectileAmount;
        RandomSource random = player.getRandom();
        if (random.nextFloat() < duplicationChance) {
            projectileAmount++;
        }
        spawnDuplicateProjectiles(projectile, level, player, projectileAmount);
    }

    private static void spawnDuplicateProjectiles(Projectile originalProjectile, ServerLevel level, Player player, int projectileAmount) {
        float spreadAngle = 5f;
        for (int i = 0; i < projectileAmount; i++) {
            int side = (i % 2 == 0 ? 1 : -1);
            int projectileNumber = i / 2 + 1;
            float angleOffset = projectileNumber * side * spreadAngle;
            spawnDuplicateProjectileWithOffset(originalProjectile, player, level, angleOffset);
        }
    }

    private static void spawnDuplicateProjectileWithOffset(Projectile original, Player player, ServerLevel level, float angleOffset) {
        EntityType<?> projectileType = original.getType();
        Projectile duplicate = (Projectile) projectileType.create(level);
        if (duplicate == null) {
            return;
        }
        duplicate.getPersistentData().merge(original.getPersistentData());
        Vec3 movementVector = original.getDeltaMovement();
        Vec3 rotatedDirection = rotateVector(movementVector, angleOffset);
        Vec3 originalPos = original.position();
        Vec3 duplicatePos = originalPos.add(rotatedDirection.normalize());
        duplicate.setPos(duplicatePos.x, duplicatePos.y, duplicatePos.z);
        duplicate.setDeltaMovement(rotatedDirection);
        duplicate.setOwner(player);
        CompoundTag projectileTag = duplicate.getPersistentData();
        projectileTag.putBoolean(IS_DUPLICATED_TAG_NAME, true);
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

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void removeInvulnerabilityTicksForDupedProjectiles(LivingHurtEvent event) {
        DamageSource damageSource = event.getSource();
        if (!(damageSource.getDirectEntity() instanceof Projectile projectile)) {
            return;
        }
        if (!(projectile.getOwner() instanceof Player)) {
            return;
        }
        CompoundTag projectileTag = projectile.getPersistentData();
        if (!(projectileTag.getBoolean(IS_DUPLICATED_TAG_NAME))) {
            return;
        }
        LivingEntity target = event.getEntity();
        target.invulnerableTime = 0;
    }
}
