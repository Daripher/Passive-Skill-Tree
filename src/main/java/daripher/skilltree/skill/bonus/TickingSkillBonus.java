package daripher.skilltree.skill.bonus;

import net.minecraft.server.level.ServerPlayer;

public interface TickingSkillBonus {
    void tick(ServerPlayer player);
}
