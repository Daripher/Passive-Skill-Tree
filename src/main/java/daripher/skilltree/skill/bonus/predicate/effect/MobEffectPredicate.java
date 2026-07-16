package daripher.skilltree.skill.bonus.predicate.effect;

import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.init.PSTRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

public interface MobEffectPredicate extends Predicate<MobEffect> {
    default String getDescriptionId() {
        ResourceLocation id = PSTRegistries.MOB_EFFECT_PREDICATES.get().getKey(getSerializer());
        Objects.requireNonNull(id);
        return "mob_effect_predicate.%s.%s".formatted(id.getNamespace(), id.getPath());
    }

    default Component getTooltip() {
        return Component.translatable(getDescriptionId());
    }

    default Component getTooltip(String type) {
        return TooltipHelper.getOptionalTooltip(getDescriptionId(), type);
    }

    MobEffectPredicate.Serializer getSerializer();

    default void addEditorWidgets(SkillTreeEditor editor, Consumer<MobEffectPredicate> consumer) {
    }

    boolean testsForHarmfulEffects();

    interface Serializer extends daripher.skilltree.data.serializers.Serializer<MobEffectPredicate> {
        MobEffectPredicate createDefaultInstance();
    }
}
