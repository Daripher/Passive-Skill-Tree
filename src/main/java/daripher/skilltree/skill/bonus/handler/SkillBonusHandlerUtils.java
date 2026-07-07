package daripher.skilltree.skill.bonus.handler;

import daripher.skilltree.SkillTreeMod;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class SkillBonusHandlerUtils {
    public static final String LAST_ATTACK_TARGET_TAG_NAME = "LastAttackTarget";

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void setLastHurtEntity(LivingHurtEvent event) {
        Player attacker = null;
        if (event.getSource().getEntity() instanceof Player player) {
            attacker = player;
        } else if (event.getSource().getDirectEntity() instanceof Player player) {
            attacker = player;
        }
        if (attacker == null) {
            return;
        }
        setLastPlayerAttackTarget(attacker, event.getEntity());
    }

    private static void setLastPlayerAttackTarget(Player player, LivingEntity target) {
        CompoundTag dataTag = player.getPersistentData();
        dataTag.putUUID(LAST_ATTACK_TARGET_TAG_NAME, target.getUUID());
    }

    public static @Nullable Entity getLastPlayerAttackTarget(Player player) {
        CompoundTag playerPersistentData = player.getPersistentData();
        if (!playerPersistentData.hasUUID(LAST_ATTACK_TARGET_TAG_NAME)) {
            return null;
        }
        UUID lastTargetUUID = playerPersistentData.getUUID(LAST_ATTACK_TARGET_TAG_NAME);
        MinecraftServer minecraftServer = player.getServer();
        if (minecraftServer == null) {
            return null;
        }
        ResourceKey<Level> dimension = player.level().dimension();
        ServerLevel serverLevel = minecraftServer.getLevel(dimension);
        if (serverLevel == null) {
            return null;
        }
        return serverLevel.getEntity(lastTargetUUID);
    }

    public static void hurtIgnoringInvulnerabilityTime(LivingEntity livingEntity, DamageSource damageSource, float amount) {
        MinecraftServer minecraftServer = livingEntity.getServer();
        if (minecraftServer == null) {
            return;
        }
        TickTask delayedDamageTask = new TickTask(minecraftServer.getTickCount() + 1, () -> {
            if (livingEntity.isDeadOrDying()) {
                return;
            }
            livingEntity.invulnerableTime = 0;
            livingEntity.hurt(damageSource, amount);
        });
        minecraftServer.tell(delayedDamageTask);
    }
}
