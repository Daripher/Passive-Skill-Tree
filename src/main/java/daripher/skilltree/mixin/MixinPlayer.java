package daripher.skilltree.mixin;

import java.lang.reflect.InvocationTargetException;

import org.spongepowered.asm.mixin.Mixin;

import daripher.skilltree.init.SkillTreeAttributes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

@Mixin(Player.class)
public abstract class MixinPlayer extends LivingEntity {
	protected MixinPlayer() {
		super(null, null);
	}

	@Override
	public int getUseItemRemainingTicks() {
		if (getUseItem().getItem() instanceof BowItem) {
			var bowChargeSpeed = this.getAttributeValue(SkillTreeAttributes.BOW_CHARGE_SPEED_MULTIPLIER.get());

			if (bowChargeSpeed > 1) {
				var bowChargeSpeedBonus = bowChargeSpeed - 1;
				var maxUseDuration = getUseItem().getUseDuration();
				var useDuration = maxUseDuration - useItemRemaining;
				return (int) (useItemRemaining - useDuration * bowChargeSpeedBonus);
			}
		}

		return super.getUseItemRemainingTicks();
	}

	@Override
	protected void dropExperience() {
		var shouldDropExp = isAlwaysExperienceDropper() || lastHurtByPlayerTime > 0 && shouldDropExperience() && level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT);

		if (!level.isClientSide && !wasExperienceConsumed() && shouldDropExp) {
			var expReward = net.minecraftforge.event.ForgeEventFactory.getExperienceDrop(this, lastHurtByPlayer, getExperienceReward());
			var level = (ServerLevel) this.level;

			while (expReward > 0) {
				var droppedExp = ExperienceOrb.getExperienceValue(expReward);
				expReward -= droppedExp;
				var tryMergeToExistingMethod = ObfuscationReflectionHelper.findMethod(ExperienceOrb.class, "m_147096_", ServerLevel.class, Vec3.class, int.class);
				var tryMergeToExisting = false;

				try {
					tryMergeToExisting = (boolean) tryMergeToExistingMethod.invoke(null, level, position(), droppedExp);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					e.printStackTrace();
				}

				if (!tryMergeToExisting) {
					var expOrb = new ExperienceOrb(level, getX(), getY(), getZ(), droppedExp);
					expOrb.getTags().add("FromPlayer");
					level.addFreshEntity(expOrb);
				}
			}
		}
	}
}
