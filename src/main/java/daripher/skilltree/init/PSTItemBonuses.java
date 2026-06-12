package daripher.skilltree.init;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.skill.bonus.item.EquipmentBonus;
import daripher.skilltree.skill.bonus.item.GroupedItemBonus;
import daripher.skilltree.skill.bonus.item.ItemBonus;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.Objects;

public class PSTItemBonuses {
    public static final ResourceLocation REGISTRY_ID = ResourceLocation.fromNamespaceAndPath(SkillTreeMod.MOD_ID, "item_bonuses");
    public static final DeferredRegister<ItemBonus.Serializer> REGISTRY = DeferredRegister.create(REGISTRY_ID, SkillTreeMod.MOD_ID);

    public static final RegistryObject<ItemBonus.Serializer> SKILL_BONUS = REGISTRY.register("skill_bonus", EquipmentBonus.Serializer::new);
    public static final RegistryObject<ItemBonus.Serializer> ITEM_BONUS_LIST = REGISTRY.register("item_bonus_list", GroupedItemBonus.Serializer::new);


    @SuppressWarnings("rawtypes")
    public static List<ItemBonus> bonusList() {
        return PSTRegistries.ITEM_BONUSES.get().getValues().stream().map(ItemBonus.Serializer::createDefaultInstance)
                .map(ItemBonus.class::cast).toList();
    }

    public static String getName(ItemBonus<?> itemBonus) {
        ResourceLocation id = PSTRegistries.ITEM_BONUSES.get().getKey(itemBonus.getSerializer());
        return TooltipHelper.idToName(Objects.requireNonNull(id).getPath());
    }
}
