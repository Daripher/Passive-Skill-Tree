package daripher.skilltree.skill.bonus.player;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.data.serializers.SerializationHelper;
import daripher.skilltree.init.PSTSkillBonuses;
import daripher.skilltree.network.NetworkHelper;
import daripher.skilltree.recipe.workbench.WorkbenchVanillaCraftingRecipe;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.predicate.item.ItemStackPredicate;
import daripher.skilltree.skill.bonus.predicate.item.NoneItemStackPredicate;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.function.Consumer;

public class VanillaRecipeUnlockBonus implements SkillBonus<VanillaRecipeUnlockBonus> {
    private @Nonnull ItemStackPredicate itemStackPredicate;

    public VanillaRecipeUnlockBonus(@Nonnull ItemStackPredicate itemStackPredicate) {
        this.itemStackPredicate = itemStackPredicate;
    }

    public boolean canUnlockRecipe(WorkbenchVanillaCraftingRecipe recipe) {
        return itemStackPredicate.test(recipe.getResult());
    }

    @Override
    public SkillBonus.Serializer getSerializer() {
        return PSTSkillBonuses.VANILLA_RECIPE_UNLOCK.get();
    }

    @Override
    public VanillaRecipeUnlockBonus copy() {
        return new VanillaRecipeUnlockBonus(itemStackPredicate);
    }

    @Override
    public VanillaRecipeUnlockBonus multiply(double multiplier) {
        return this;
    }

    @Override
    public boolean canMerge(SkillBonus<?> other) {
        return false;
    }

    @Override
    public SkillBonus<VanillaRecipeUnlockBonus> merge(SkillBonus<?> other) {
        return this;
    }

    @Override
    public MutableComponent getSimpleTooltip() {
        Component itemStackPredicateTooltip = itemStackPredicate.getTooltip();
        Style recipeTooltipStyle = TooltipHelper.getItemUpgradeStyle();
        MutableComponent tooltip = Component.translatable(getDescriptionId(), itemStackPredicateTooltip);
        return tooltip.withStyle(TooltipHelper.getSkillBonusStyle(isPositive()));
    }

    @Override
    public boolean isPositive() {
        return true;
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, Consumer<VanillaRecipeUnlockBonus> consumer) {
        editor.addLabel(0, 0, "Item Stack Predicate", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addSelectionMenu(0, 0, 200, itemStackPredicate).setResponder(condition -> selectItemCondition(editor, consumer, condition))
                .setMenuInitFunc(() -> addItemConditionWidgets(editor, consumer));
        editor.increaseHeight(19);
    }

    private void addItemConditionWidgets(SkillTreeEditor editor, Consumer<VanillaRecipeUnlockBonus> consumer) {
        itemStackPredicate.addEditorWidgets(editor, c -> {
            setItemCondition(c);
            consumer.accept(this.copy());
        });
    }

    private void selectItemCondition(SkillTreeEditor editor, Consumer<VanillaRecipeUnlockBonus> consumer, ItemStackPredicate condition) {
        setItemCondition(condition);
        consumer.accept(this.copy());
        editor.rebuildWidgets();
    }

    public SkillBonus<?> setItemCondition(ItemStackPredicate condition) {
        this.itemStackPredicate = condition;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        VanillaRecipeUnlockBonus that = (VanillaRecipeUnlockBonus) o;
        return Objects.equals(itemStackPredicate, that.itemStackPredicate);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(itemStackPredicate);
    }

    public static class Serializer implements SkillBonus.Serializer {
        @Override
        public VanillaRecipeUnlockBonus deserialize(JsonObject json) throws JsonParseException {
            ItemStackPredicate itemStackPredicate = SerializationHelper.deserializeItemPredicate(json);
            return new VanillaRecipeUnlockBonus(itemStackPredicate);
        }

        @Override
        public void serialize(JsonObject json, SkillBonus<?> bonus) {
            if (!(bonus instanceof VanillaRecipeUnlockBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            SerializationHelper.serializeItemPredicate(json, aBonus.itemStackPredicate);
        }

        @Override
        public VanillaRecipeUnlockBonus deserialize(CompoundTag tag) {
            ItemStackPredicate itemStackPredicate = SerializationHelper.deserializeItemPredicate(tag);
            return new VanillaRecipeUnlockBonus(itemStackPredicate);
        }

        @Override
        public CompoundTag serialize(SkillBonus<?> bonus) {
            if (!(bonus instanceof VanillaRecipeUnlockBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            CompoundTag tag = new CompoundTag();
            SerializationHelper.serializeItemPredicate(tag, aBonus.itemStackPredicate);
            return tag;
        }

        @Override
        public VanillaRecipeUnlockBonus deserialize(FriendlyByteBuf buf) {
            ItemStackPredicate itemStackPredicate = NetworkHelper.readItemPredicate(buf);
            return new VanillaRecipeUnlockBonus(itemStackPredicate);
        }

        @Override
        public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
            if (!(bonus instanceof VanillaRecipeUnlockBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            NetworkHelper.writeItemPredicate(buf, aBonus.itemStackPredicate);
        }

        @Override
        public SkillBonus<?> createDefaultInstance() {
            return new VanillaRecipeUnlockBonus(NoneItemStackPredicate.INSTANCE);
        }
    }
}
