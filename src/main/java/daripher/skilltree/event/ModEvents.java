package daripher.skilltree.event;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.config.Config;
import daripher.skilltree.init.PSTItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class ModEvents {
	@SubscribeEvent
	public static void dropAmnesiaScroll(LivingDropsEvent event) {
		if (!Config.dragon_drops_amnesia_scroll) return;
		LivingEntity entity = event.getEntity();
		if (entity.getType() == EntityType.ENDER_DRAGON) {
			ItemStack scroll = new ItemStack(PSTItems.AMNESIA_SCROLL.get());
			event.getDrops().add(new ItemEntity(entity.level(), entity.getX(), entity.getY(), entity.getZ(), scroll));
		}
	}
}
