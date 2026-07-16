package daripher.skilltree.effect;

import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.TickingSkillBonus;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import org.jetbrains.annotations.NotNull;

public abstract class SkillBonusEffect extends MobEffect {
    private final SkillBonus<?> bonus;

    public SkillBonusEffect(MobEffectCategory category, int color, SkillBonus<?> bonus) {
        super(category, color);
        this.bonus = bonus;
    }

    @Override
    public void removeAttributeModifiers(@NotNull AttributeMap attributeMap) {
        super.removeAttributeModifiers(attributeMap);
    }

    @Override
    public void addAttributeModifiers(@NotNull AttributeMap attributeMap, int amplifier) {
        super.addAttributeModifiers(attributeMap, amplifier);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return bonus instanceof TickingSkillBonus;
    }

    @Override
    public boolean applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        if (entity instanceof ServerPlayer player && bonus instanceof TickingSkillBonus ticking) {
            ticking.tick(player);
        }
        return true;
    }

    public SkillBonus<?> getBonus() {
        return bonus;
    }
}
