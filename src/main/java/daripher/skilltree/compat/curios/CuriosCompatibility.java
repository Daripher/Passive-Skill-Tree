package daripher.skilltree.compat.curios;

import daripher.skilltree.compat.curios.skill.bonus.CurioSlotsBonus;
import daripher.skilltree.init.PSTSkillBonuses;
import daripher.skilltree.skill.SkillBonusProvider;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.player.PreventItemUsageBonus;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.registries.DeferredHolder;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.event.CurioCanEquipEvent;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public enum CuriosCompatibility {
    INSTANCE;

    public static final DeferredHolder<SkillBonus.Serializer, ? extends SkillBonus.Serializer> CURIO_SLOTS_BONUS = PSTSkillBonuses.REGISTRY.register("curio_slots", CurioSlotsBonus.Serializer::new);

    public void register() {
        NeoForge.EVENT_BUS.addListener(INSTANCE::applyCantUseItemBonus);
    }

    public Stream<ItemStack> getCurios(LivingEntity living) {
        List<ItemStack> curios = new ArrayList<>();
        CuriosApi.getCuriosInventory(living).map(ICuriosItemHandler::getEquippedCurios).ifPresent(inv -> {
            for (int i = 0; i < inv.getSlots(); i++) {
                curios.add(inv.getStackInSlot(i));
            }
        });
        return curios.stream();
    }


    private void applyCantUseItemBonus(CurioCanEquipEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        for (PreventItemUsageBonus bonus : SkillBonusProvider.getSkillBonuses(player, PreventItemUsageBonus.class)) {
            if (bonus.getItemCondition().test(event.getStack())) {
                event.setEquipResult(TriState.FALSE);
                return;
            }
        }
    }
}
