package daripher.skilltree.exp;

import net.minecraft.world.entity.player.Player;

public class ExpHelper {
    public static long getPlayerExp(Player player) {
        return levelsToXP(player.experienceLevel) + Math.round(player.experienceProgress * player.getXpNeededForNextLevel());
    }

    private static int levelsToXP(int levels) {
        if (levels <= 16) {
            return (int) (Math.pow(levels, 2) + 6 * levels);
        } else if (levels <= 31) {
            return (int) (2.5 * Math.pow(levels, 2) - 40.5 * levels + 360);
        } else {
            return (int) (4.5 * Math.pow(levels, 2) - 162.5 * levels + 2220);
        }
    }
}
