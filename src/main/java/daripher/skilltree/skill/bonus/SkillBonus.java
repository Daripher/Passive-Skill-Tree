package daripher.skilltree.skill.bonus;

import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.init.PSTRegistries;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;

public interface SkillBonus<T extends SkillBonus<T>> extends Comparable<SkillBonus<?>> {
    default void onSkillLearned(ServerPlayer player, boolean firstTime) {
    }

    default void onSkillRemoved(ServerPlayer player) {
    }

    boolean canMerge(SkillBonus<?> other);

    default boolean sameBonus(SkillBonus<?> other) {
        return canMerge(other);
    }

    SkillBonus<T> merge(SkillBonus<?> other);

    SkillBonus<T> copy();

    T multiply(double multiplier);

    Serializer getSerializer();

    default String getDescriptionId() {
        ResourceLocation id = PSTRegistries.SKILL_BONUSES.get().getKey(getSerializer());
        Objects.requireNonNull(id);
        return "skill_bonus.%s.%s".formatted(id.getNamespace(), id.getPath());
    }

    default MutableComponent getSimpleTooltip() {
        return Component.literal("Description is missing for: " + this).withStyle(ChatFormatting.RED);
    }

    default List<MutableComponent> getFullTooltip() {
        return List.of(getSimpleTooltip());
    }
    
    default void gatherInfo(Consumer<MutableComponent> consumer) {
        TooltipHelper.consumeTranslated(getDescriptionId() + ".info", consumer);
    }

    boolean isPositive();

    void addEditorWidgets(SkillTreeEditor editor, Consumer<T> consumer);

    @Override
    default int compareTo(@NotNull SkillBonus<?> o) {
        if (isPositive() != o.isPositive()) {
            return isPositive() ? -1 : 1;
        }
        String regex = "\\+?-?[0-9]+\\.?[0-9]?%? ";
        String as = getSimpleTooltip().getString().replaceAll(regex, "");
        String bs = o.getSimpleTooltip().getString().replaceAll(regex, "");
        return as.compareTo(bs);
    }

    enum Target {
        PLAYER, ENEMY;

        public String getName() {
            return name().toLowerCase(Locale.ROOT);
        }

        public static Target fromName(String name) {
            return valueOf(name.toUpperCase(Locale.ROOT));
        }
    }

    interface Serializer extends daripher.skilltree.data.serializers.Serializer<SkillBonus<?>> {
        @Nullable SkillBonus<?> createDefaultInstance();
    }
}
