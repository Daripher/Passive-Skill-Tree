package daripher.skilltree.skill.bonus.handler;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.skill.SkillBonusProvider;
import daripher.skilltree.skill.bonus.player.ProjectileSpeedBonus;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class ProjectileSpeedBonusHandler {
    public static final String IS_SPED_UP_TAG_NAME = "IS_SPED_UP";

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void applyProjectileSpeedBonus(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof Projectile projectile)) {
            return;
        }
        if (!(event.getLevel() instanceof ServerLevel)) {
            return;
        }
        if (!(projectile.getOwner() instanceof Player player)) {
            return;
        }
        if (event.loadedFromDisk()) {
            return;
        }
        CompoundTag projectileTag = projectile.getPersistentData();
        if (projectileTag.getBoolean(IS_SPED_UP_TAG_NAME)) {
            return;
        }
        float speedMultiplier = 1f;
        List<ProjectileSpeedBonus> skillBonuses = SkillBonusProvider.getSkillBonuses(player, ProjectileSpeedBonus.class);
        if (skillBonuses.isEmpty()) {
            return;
        }
        for (ProjectileSpeedBonus skillBonus : skillBonuses) {
            speedMultiplier += skillBonus.getProjectileSpeedModifier(player);
        }
        if (speedMultiplier == 1f) {
            return;
        }
        speedMultiplier = Math.max(0f, speedMultiplier);
        projectileTag.putBoolean(IS_SPED_UP_TAG_NAME, true);
        Vec3 projectileMovementVec = projectile.getDeltaMovement();
        projectile.setDeltaMovement(projectileMovementVec.scale(speedMultiplier));
    }
}
