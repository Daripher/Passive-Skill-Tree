package daripher.skilltree.skill.bonus.multiplier;

import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.init.PSTRegistries;
import daripher.skilltree.skill.bonus.SkillBonus;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import java.util.Objects;
import java.util.function.Consumer;

public interface LivingMultiplier {
    float getValue(LivingEntity entity);

    Serializer getSerializer();

    default String getDescriptionId() {
        ResourceLocation id = PSTRegistries.LIVING_MULTIPLIERS.get().getKey(getSerializer());
        Objects.requireNonNull(id);
        return "skill_bonus_multiplier.%s.%s".formatted(id.getNamespace(), id.getPath());
    }

    MutableComponent getTooltip(MutableComponent bonusTooltip, SkillBonus.Target target);

    default void addEditorWidgets(SkillTreeEditor editor, Consumer<LivingMultiplier> consumer) {
    }

    interface Serializer extends daripher.skilltree.data.serializers.Serializer<LivingMultiplier> {
        LivingMultiplier createDefaultInstance();
    }
}
