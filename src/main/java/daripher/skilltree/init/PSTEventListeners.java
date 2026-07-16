package daripher.skilltree.init;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.skill.bonus.event.*;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.List;
import java.util.Objects;

public class PSTEventListeners {
    public static final ResourceLocation REGISTRY_ID = ResourceLocation.fromNamespaceAndPath(SkillTreeMod.MOD_ID, "event_listeners");
    public static final DeferredRegister<SkillEventListener.Serializer> REGISTRY = DeferredRegister.create(REGISTRY_ID, SkillTreeMod.MOD_ID);

    public static final DeferredHolder<SkillEventListener.Serializer, ? extends SkillEventListener.Serializer> ATTACK = REGISTRY.register("attack", OutgoingDamageEventListener.Serializer::new);
    public static final DeferredHolder<SkillEventListener.Serializer, ? extends SkillEventListener.Serializer> BLOCK = REGISTRY.register("block", ShieldBlockEventListener.Serializer::new);
    public static final DeferredHolder<SkillEventListener.Serializer, ? extends SkillEventListener.Serializer> EVASION = REGISTRY.register("evasion", EvasionEventListener.Serializer::new);
    public static final DeferredHolder<SkillEventListener.Serializer, ? extends SkillEventListener.Serializer> ITEM_USED = REGISTRY.register("item_used", ItemUseEventListener.Serializer::new);
    public static final DeferredHolder<SkillEventListener.Serializer, ? extends SkillEventListener.Serializer> DAMAGE_TAKEN = REGISTRY.register("damage_taken", IncomingDamageEventListener.Serializer::new);
    public static final DeferredHolder<SkillEventListener.Serializer, ? extends SkillEventListener.Serializer> ON_KILL = REGISTRY.register("on_kill", KillEventListener.Serializer::new);
    public static final DeferredHolder<SkillEventListener.Serializer, ? extends SkillEventListener.Serializer> SKILL_LEARNED = REGISTRY.register("skill_learned", SkillLearnedEventListener.Serializer::new);
    public static final DeferredHolder<SkillEventListener.Serializer, ? extends SkillEventListener.Serializer> SKILL_REMOVED = REGISTRY.register("skill_removed", SkillRemovedEventListener.Serializer::new);
    public static final DeferredHolder<SkillEventListener.Serializer, ? extends SkillEventListener.Serializer> TICKING = REGISTRY.register("ticking", TickingEventListener.Serializer::new);
    public static final DeferredHolder<SkillEventListener.Serializer, ? extends SkillEventListener.Serializer> CRITICAL_HIT = REGISTRY.register("critical_hit", CriticalHitEventListener.Serializer::new);

    public static List<SkillEventListener> eventsList() {
        return PSTRegistries.EVENT_LISTENERS.get().getValues().stream().map(SkillEventListener.Serializer::createDefaultInstance).toList();
    }

    public static String getName(SkillEventListener eventType) {
        ResourceLocation id = PSTRegistries.EVENT_LISTENERS.get().getKey(eventType.getSerializer());
        return TooltipHelper.idToName(Objects.requireNonNull(id).getPath());
    }
}
