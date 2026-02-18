package daripher.skilltree.compat.curios;

import daripher.skilltree.compat.curios.skill.bonus.CurioSlotsBonus;
import daripher.skilltree.init.PSTSkillBonuses;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.SkillBonusHandler;
import daripher.skilltree.skill.bonus.player.CantUseItemBonus;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.registries.RegistryObject;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.event.CurioEquipEvent;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public enum CuriosCompatibility {
    INSTANCE;

    public static final RegistryObject<SkillBonus.Serializer> CURIO_SLOTS_BONUS =
            PSTSkillBonuses.REGISTRY.register("curio_slots", CurioSlotsBonus.Serializer::new);

    public void register() {
        MinecraftForge.EVENT_BUS.addListener(INSTANCE::applyCantUseItemBonus);
    }

    public Stream<ItemStack> getCurios(LivingEntity living) {
        List<ItemStack> curios = new ArrayList<>();
        CuriosApi.getCuriosInventory(living)
                .map(ICuriosItemHandler::getEquippedCurios)
                .ifPresent(
                        inv -> {
                            for (int i = 0; i < inv.getSlots(); i++) {
                                curios.add(inv.getStackInSlot(i));
                            }
                        });
        return curios.stream();
    }


    private void applyCantUseItemBonus(CurioEquipEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        for (CantUseItemBonus bonus : SkillBonusHandler.getSkillBonuses(player, CantUseItemBonus.class)) {
            if (bonus.getItemCondition().test(event.getStack())) {
                event.setResult(Event.Result.DENY);
                return;
            }
        }
    }
}
