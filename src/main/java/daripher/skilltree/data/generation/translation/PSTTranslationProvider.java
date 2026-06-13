package daripher.skilltree.data.generation.translation;

import daripher.skilltree.init.PSTRegistries;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.event.SkillEventListener;
import daripher.skilltree.skill.bonus.function.FloatFunction;
import daripher.skilltree.skill.bonus.multiplier.LivingMultiplier;
import daripher.skilltree.skill.bonus.predicate.damage.DamageCondition;
import daripher.skilltree.skill.bonus.predicate.effect.MobEffectPredicate;
import daripher.skilltree.skill.bonus.predicate.enchantment.EnchantmentCondition;
import daripher.skilltree.skill.bonus.predicate.item.ItemStackPredicate;
import daripher.skilltree.skill.bonus.predicate.living.LivingEntityPredicate;
import daripher.skilltree.skill.requirement.SkillRequirement;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;

public abstract class PSTTranslationProvider extends LanguageProvider {
    public PSTTranslationProvider(DataGenerator dataGenerator, String modId, String locale) {
        super(dataGenerator.getPackOutput(), modId, locale);
    }

    protected void addTooltip(Item item, String tooltip) {
        add(item.getDescriptionId() + ".tooltip", tooltip);
    }

    protected void addWarning(Item item, String tooltip) {
        add(item.getDescriptionId() + ".warning", tooltip);
    }

    protected void add(Attribute attribute, String name) {
        add(attribute.getDescriptionId(), name);
    }

    protected void addSkill(String skillTree, int skillId, String name) {
        add("skill.skilltree.%s_%d.name".formatted(skillTree, skillId), name);
    }

    protected void addSkills(String skillTree, int skillId1, int skillId2, int skillId3, String name) {
        addSkill(skillTree, skillId1, name);
        addSkill(skillTree, skillId2, name);
        addSkill(skillTree, skillId3, name);
    }

    protected void add(LivingEntityPredicate.Serializer condition, String value) {
        ResourceLocation id = PSTRegistries.LIVING_CONDITIONS.get().getKey(condition);
        assert id != null;
        String key = "living_condition.%s.%s".formatted(id.getNamespace(), id.getPath());
        add(key, value);
    }

    protected void add(LivingEntityPredicate.Serializer condition, String type, String value) {
        ResourceLocation id = PSTRegistries.LIVING_CONDITIONS.get().getKey(condition);
        assert id != null;
        String key = "living_condition.%s.%s.%s".formatted(id.getNamespace(), id.getPath(), type);
        add(key, value);
    }

    protected void add(MobEffectPredicate.Serializer condition, String value) {
        ResourceLocation id = PSTRegistries.MOB_EFFECT_PREDICATES.get().getKey(condition);
        assert id != null;
        String key = "mob_effect_predicate.%s.%s".formatted(id.getNamespace(), id.getPath());
        add(key, value);
    }

    protected void add(MobEffectPredicate.Serializer condition, String type, String value) {
        ResourceLocation id = PSTRegistries.MOB_EFFECT_PREDICATES.get().getKey(condition);
        assert id != null;
        String key = "mob_effect_predicate.%s.%s.%s".formatted(id.getNamespace(), id.getPath(), type);
        add(key, value);
    }

    protected void add(SkillRequirement.Serializer requirement, String value) {
        ResourceLocation id = PSTRegistries.SKILL_REQUIREMENTS.get().getKey(requirement);
        assert id != null;
        String key = "skill_requirements.%s.%s".formatted(id.getNamespace(), id.getPath());
        add(key, value);
    }

    protected void add(SkillEventListener.Serializer condition, String value) {
        ResourceLocation id = PSTRegistries.EVENT_LISTENERS.get().getKey(condition);
        assert id != null;
        String key = "event_listener.%s.%s".formatted(id.getNamespace(), id.getPath());
        add(key, value);
    }

    protected void add(SkillEventListener.Serializer condition, String type, String value) {
        ResourceLocation id = PSTRegistries.EVENT_LISTENERS.get().getKey(condition);
        assert id != null;
        String key = "event_listener.%s.%s.%s".formatted(id.getNamespace(), id.getPath(), type);
        add(key, value);
    }

    protected void add(DamageCondition.Serializer condition, String type, String value) {
        ResourceLocation id = PSTRegistries.DAMAGE_CONDITIONS.get().getKey(condition);
        assert id != null;
        String key = "damage_condition.%s.%s.%s".formatted(id.getNamespace(), id.getPath(), type);
        add(key, value);
    }

    protected void add(DamageCondition.Serializer condition, String value) {
        ResourceLocation id = PSTRegistries.DAMAGE_CONDITIONS.get().getKey(condition);
        assert id != null;
        String key = "damage_condition.%s.%s".formatted(id.getNamespace(), id.getPath());
        add(key, value);
    }

    protected void add(LivingMultiplier.Serializer multiplier, String value) {
        ResourceLocation id = PSTRegistries.LIVING_MULTIPLIERS.get().getKey(multiplier);
        assert id != null;
        String key = "skill_bonus_multiplier.%s.%s".formatted(id.getNamespace(), id.getPath());
        add(key, value);
    }

    protected void add(LivingMultiplier.Serializer multiplier, String type, String value) {
        ResourceLocation id = PSTRegistries.LIVING_MULTIPLIERS.get().getKey(multiplier);
        assert id != null;
        String key = "skill_bonus_multiplier.%s.%s.%s".formatted(id.getNamespace(), id.getPath(), type);
        add(key, value);
    }

    protected void add(FloatFunction.Serializer provider, String value) {
        ResourceLocation id = PSTRegistries.FLOAT_FUNCTIONS.get().getKey(provider);
        assert id != null;
        String key = "value_provider.%s.%s".formatted(id.getNamespace(), id.getPath());
        add(key, value);
    }

    protected void add(FloatFunction.Serializer provider, String type, String value) {
        ResourceLocation id = PSTRegistries.FLOAT_FUNCTIONS.get().getKey(provider);
        assert id != null;
        String key = "value_provider.%s.%s.%s".formatted(id.getNamespace(), id.getPath(), type);
        add(key, value);
    }

    protected void add(SkillBonus.Serializer serializer, String value) {
        ResourceLocation id = PSTRegistries.SKILL_BONUSES.get().getKey(serializer);
        assert id != null;
        String key = "skill_bonus.%s.%s".formatted(id.getNamespace(), id.getPath());
        add(key, value);
    }

    protected void add(SkillBonus.Serializer serializer, String type, String value) {
        ResourceLocation id = PSTRegistries.SKILL_BONUSES.get().getKey(serializer);
        assert id != null;
        String key = "skill_bonus.%s.%s.%s".formatted(id.getNamespace(), id.getPath(), type);
        add(key, value);
    }

    protected void add(ItemStackPredicate.Serializer serializer, String type, String value) {
        ResourceLocation id = PSTRegistries.ITEM_CONDITIONS.get().getKey(serializer);
        assert id != null;
        String key = "item_condition.%s.%s.%s".formatted(id.getNamespace(), id.getPath(), type);
        add(key, value);
    }

    protected void add(ItemStackPredicate.Serializer serializer, String value) {
        ResourceLocation id = PSTRegistries.ITEM_CONDITIONS.get().getKey(serializer);
        assert id != null;
        String key = "item_condition.%s.%s".formatted(id.getNamespace(), id.getPath());
        add(key, value);
    }

    protected void add(TagKey<Item> itemTag, String value) {
        ResourceLocation id = itemTag.location();
        String key = "item_tag.%s".formatted(id.toString());
        add(key, value);
    }

    protected void add(TagKey<Item> itemTag, String type, String value) {
        ResourceLocation id = itemTag.location();
        String key = "item_tag.%s.%s".formatted(id.toString(), type);
        add(key, value);
    }

    protected void add(EnchantmentCondition.Serializer serializer, String value) {
        ResourceLocation id = PSTRegistries.ENCHANTMENT_CONDITIONS.get().getKey(serializer);
        assert id != null;
        String key = "enchantment_condition.%s.%s".formatted(id.getNamespace(), id.getPath());
        add(key, value);
    }

    protected void deathMessage(String damageType, String deathMessage) {
        add("death.attack." + damageType, deathMessage);
    }

    protected void add(RecipeSerializer<?> recipeSerializer, String translation) {
        ResourceLocation id = ForgeRegistries.RECIPE_SERIALIZERS.getKey(recipeSerializer);
        Objects.requireNonNull(id);
        add("recipe.%s.%s".formatted(id.getNamespace(), id.getPath()), translation);
    }
}
