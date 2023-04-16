package daripher.skilltree.data;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import org.slf4j.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.skill.PassiveSkill;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.storage.loot.Deserializers;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class SkillsDataReloader extends SimpleJsonResourceReloadListener {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Gson GSON = Deserializers.createLootTableSerializer().create();
	private static final Map<ResourceLocation, PassiveSkill> SKILLS = new HashMap<>();

	public SkillsDataReloader() {
		super(GSON, "skills");
	}

	@SubscribeEvent
	public static void reloadSkills(AddReloadListenerEvent event) {
		event.addListener(new SkillsDataReloader());
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
		SKILLS.clear();

		map.forEach((id, json) -> {
			try {
				var passiveSkill = PassiveSkill.loadFromJson(id, json.getAsJsonObject());
				SKILLS.put(id, passiveSkill);
				LOGGER.info("Loading passive skill {}", id);
			} catch (Exception exception) {
				LOGGER.error("Couldn't parse passive skill {}", id, exception);
			}
		});
	}

	@Nullable
	public static PassiveSkill getSkillById(ResourceLocation id) {
		return SKILLS.get(id);
	}

	public static Map<ResourceLocation, PassiveSkill> getSkills() {
		return SKILLS;
	}
}
