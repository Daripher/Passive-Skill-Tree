package daripher.skilltree.skill;

import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.effect.SkillBonusEffect;
import daripher.skilltree.entity.player.PlayerHelper;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.item.EquipmentBonus;
import daripher.skilltree.skill.bonus.item.ItemBonus;
import daripher.skilltree.skill.bonus.item.ItemBonusHandler;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SkillBonusProvider {
    public static <T> List<T> getSkillBonuses(@Nonnull Player player, Class<T> type) {
        if (!PlayerSkillsProvider.hasSkills(player)) {
            return List.of();
        }
        List<T> bonuses = new ArrayList<>();
        bonuses.addAll(getSkillTreeBonuses(player, type));
        bonuses.addAll(getEffectBonuses(player, type));
        bonuses.addAll(getEquipmentBonuses(player, type));
        return bonuses;
    }

    public static <T> List<T> getMergedSkillBonuses(@Nonnull Player player, Class<T> type) {
        return mergeSkillBonuses(getSkillBonuses(player, type));
    }

    @NotNull
    @SuppressWarnings({"rawtypes", "unchecked", "SuspiciousMethodCalls"})
    private static <T> List<T> mergeSkillBonuses(List<T> bonuses) {
        List<T> mergedBonuses = new ArrayList<>();
        for (T bonus : bonuses) {
            SkillBonus skillBonus = (SkillBonus) bonus;
            Optional<SkillBonus> mergeTarget = mergedBonuses.stream().map(SkillBonus.class::cast).filter(skillBonus::canMerge).findAny();
            if (mergeTarget.isPresent()) {
                mergedBonuses.remove(mergeTarget.get());
                mergedBonuses.add((T) mergeTarget.get().copy().merge(skillBonus));
            } else {
                mergedBonuses.add((T) skillBonus);
            }
        }
        return mergedBonuses;
    }

    private static <T> List<T> getSkillTreeBonuses(Player player, Class<T> type) {
        List<T> list = new ArrayList<>();
        for (PassiveSkill skill : PlayerSkillsProvider.get(player).getPlayerSkills()) {
            List<SkillBonus<?>> bonuses = skill.getBonuses();
            for (SkillBonus<?> skillBonus : bonuses) {
                if (type.isInstance(skillBonus)) {
                    list.add(type.cast(skillBonus));
                }
            }
        }
        return list;
    }

    private static <T> List<T> getEffectBonuses(Player player, Class<T> type) {
        List<T> bonuses = new ArrayList<>();
        for (MobEffectInstance e : player.getActiveEffects()) {
            if (e.getEffect() instanceof SkillBonusEffect skillEffect) {
                SkillBonus<?> bonus = skillEffect.getBonus().copy();
                if (type.isInstance(bonus)) {
                    bonus = bonus.copy().multiply(e.getAmplifier());
                    bonuses.add(type.cast(bonus));
                }
            }
        }
        return bonuses;
    }

    private static <T> List<T> getEquipmentBonuses(Player player, Class<T> type) {
        return PlayerHelper.getAllEquipment(player).map(s -> getItemBonuses(s, type)).flatMap(List::stream).toList();
    }

    private static <T> List<T> getItemBonuses(ItemStack stack, Class<T> type) {
        List<ItemBonus<?>> itemBonuses = new ArrayList<>(ItemBonusHandler.getItemBonuses(stack));
        List<T> bonuses = new ArrayList<>();
        for (ItemBonus<?> itemBonus : itemBonuses) {
            if (itemBonus instanceof EquipmentBonus bonus) {
                SkillBonus<?> skillBonus = bonus.getSkillBonus();
                if (type.isInstance(skillBonus)) {
                    bonuses.add(type.cast(skillBonus));
                }
            }
        }
        return bonuses;
    }
}
