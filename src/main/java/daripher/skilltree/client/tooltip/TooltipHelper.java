package daripher.skilltree.client.tooltip;

import daripher.skilltree.data.reloader.SkillsReloader;
import daripher.skilltree.effect.SkillBonusEffect;
import daripher.skilltree.init.PSTRecipeTypes;
import daripher.skilltree.recipe.workbench.AbstractWorkbenchRecipe;
import daripher.skilltree.skill.PassiveSkill;
import daripher.skilltree.skill.bonus.SkillBonus;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;

public class TooltipHelper {
    private static final Style SKILL_BONUS_STYLE = Style.EMPTY.withColor(0x7B7BE5);
    private static final Style SKILL_BONUS_STYLE_NEGATIVE = Style.EMPTY.withColor(0xE25A5A);
    private static final Style SKILL_BONUS_SECOND_STYLE = Style.EMPTY.withColor(0x7AB3E2);
    private static final Style SKILL_BONUS_SECOND_STYLE_NEGATIVE = Style.EMPTY.withColor(0xDB9792);
    private static final Style SKILL_REQUIREMENT_STYLE = Style.EMPTY.withColor(0x83E27A);
    private static final Style SKILL_REQUIREMENT_STYLE_UNFINISHED = Style.EMPTY.withColor(0xE25A5A);
    private static final Style ITEM_UPGRADE_STYLE = Style.EMPTY.withColor(0xECBE46);
    private static final Style LESSER_TITLE_STYLE = Style.EMPTY.withColor(0xEAA169);
    private static final Style NOTABLE_TITLE_STYLE = Style.EMPTY.withColor(0x9B66D8);
    private static final Style CLASS_TITLE_STYLE = Style.EMPTY.withColor(0xFFD75F);
    private static final Style KEYSTONE_TITLE_STYLE = Style.EMPTY.withColor(0xEB7530);
    private static final Style GATEWAY_TITLE_STYLE = Style.EMPTY.withColor(0x849696);

    public static Component getEffectTooltip(MobEffectInstance effect) {
        Component effectDescription;
        if (effect.getEffect() instanceof SkillBonusEffect skillEffect) {
            effectDescription = skillEffect.getBonus().copy().multiply(effect.getAmplifier() + 1).getSimpleTooltip().setStyle(Style.EMPTY);
        } else {
            effectDescription = effect.getEffect().getDisplayName();
            if (effect.getAmplifier() == 0) {
                return effectDescription;
            }
            Component amplifier = Component.translatable("potion.potency." + effect.getAmplifier());
            effectDescription = Component.translatable("potion.withAmplifier", effectDescription, amplifier);
        }
        return effectDescription;
    }

    public static Component getOperationName(AttributeModifier.Operation operation) {
        return Component.literal(switch (operation) {
            case ADDITION -> "Addition";
            case MULTIPLY_BASE -> "Multiply Base";
            case MULTIPLY_TOTAL -> "Multiply Total";
        });
    }

    public static MutableComponent getOptionalTooltip(String descriptionId, String subtype, Object... args) {
        String key = "%s.%s".formatted(descriptionId, subtype);
        MutableComponent tooltip = Component.translatable(key, args);
        if (!tooltip.getString().equals(key)) {
            return tooltip;
        }
        return Component.translatable(descriptionId, args);
    }

    public static void consumeTranslated(String descriptionId, Consumer<MutableComponent> consumer) {
        MutableComponent tooltip = Component.translatable(descriptionId);
        if (!tooltip.getString().equals(descriptionId)) {
            consumer.accept(tooltip);
        }
    }

    public static MutableComponent getSkillBonusTooltip(Component bonusDescription, double amount, AttributeModifier.Operation operation) {
        float multiplier = 1;
        if (operation != AttributeModifier.Operation.ADDITION) {
            multiplier = 100;
        }
        double visibleAmount = amount * multiplier;
        if (amount < 0) {
            visibleAmount *= -1;
        }
        String operationDescription = amount > 0 ? "plus" : "take";
        operationDescription = "attribute.modifier." + operationDescription + "." + operation.ordinal();
        String multiplierDescription = formatNumber(visibleAmount);
        return Component.translatable(operationDescription, multiplierDescription, bonusDescription);
    }

    public static String formatNumber(double number) {
        String formatted = ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(number);
        if (formatted.endsWith(".0")) {
            formatted = formatted.substring(0, formatted.length() - 2);
        }
        return formatted;
    }

    public static MutableComponent getSkillBonusTooltip(String bonus, double amount, AttributeModifier.Operation operation) {
        return getSkillBonusTooltip(Component.translatable(bonus), amount, operation);
    }

    public static Style getSkillBonusStyle(boolean positive) {
        return positive ? SKILL_BONUS_STYLE : SKILL_BONUS_STYLE_NEGATIVE;
    }

    public static Style getSkillRequirementStyle(boolean finished) {
        return finished ? SKILL_REQUIREMENT_STYLE : SKILL_REQUIREMENT_STYLE_UNFINISHED;
    }

    public static Style getSkillBonusSecondStyle(boolean positive) {
        return positive ? SKILL_BONUS_SECOND_STYLE : SKILL_BONUS_SECOND_STYLE_NEGATIVE;
    }

    public static Style getItemUpgradeStyle() {
        return ITEM_UPGRADE_STYLE;
    }

    public static MutableComponent getTextureName(ResourceLocation location) {
        String texture = location.getPath();
        texture = texture.substring(texture.lastIndexOf("/") + 1);
        texture = texture.replace(".png", "");
        texture = TooltipHelper.idToName(texture);
        return Component.literal(texture);
    }

    public static MutableComponent getTargetName(SkillBonus.Target target) {
        return Component.literal(TooltipHelper.idToName(target.name().toLowerCase(Locale.ROOT)));
    }

    @NotNull
    public static String idToName(String path) {
        if (path.isEmpty()) {
            return path;
        }
        String[] words = path.split("_");
        StringBuilder name = new StringBuilder();
        for (String word : words) {
            String string;
            if (word.isEmpty()) {
                string = word;
            } else {
                string = word.substring(0, 1).toUpperCase(Locale.ROOT) + word.substring(1);
            }
            name.append(" ");
            name.append(string);
        }
        return name.substring(1);
    }

    public static List<MutableComponent> split(MutableComponent component, Font font, int maxWidth) {
        String[] split = component.getString().split(" ");
        if (split.length < 2) {
            return List.of(component);
        }
        String line = split[0];
        List<MutableComponent> components = new ArrayList<>();
        for (int i = 1; i < split.length; i++) {
            String next = line + " " + split[i];
            if (font.width(next) > maxWidth) {
                components.add(Component.translatable(line).withStyle(component.getStyle()));
                line = "  " + split[i];
                continue;
            }
            line = next;
        }
        components.add(Component.translatable(line).withStyle(component.getStyle()));
        return components;
    }

    @NotNull
    public static String getTrimmedString(Font font, String message, int maxWidth) {
        if (font.width(message) > maxWidth) {
            while (font.width(message + "...") > maxWidth) {
                message = message.substring(0, message.length() - 1);
            }
            message += "...";
        }
        return message;
    }

    @NotNull
    public static String getTrimmedString(String message, int maxWidth) {
        return getTrimmedString(Minecraft.getInstance().font, message, maxWidth);
    }

    public static Component getSlotTooltip(String slotName, String type) {
        return Component.translatable("curio.slot.%s.%s".formatted(slotName, type));
    }

    public static Component getSlotTooltip(String slotName) {
        return Component.translatable("curio.slot.%s".formatted(slotName));
    }

    public static MutableComponent getSkillTitle(@NotNull PassiveSkill skill) {
        MutableComponent title;
        if (skill.getTitle().isEmpty()) {
            ResourceLocation skillId = skill.getId();
            String descriptionId = "skill." + skillId.getNamespace() + "." + skillId.getPath() + ".name";
            title = Component.translatable(descriptionId);
        } else {
            title = Component.literal(skill.getTitle());
        }
        return title.withStyle(getSkillTitleStyle(skill));
    }

    public static MutableComponent getSkillTitle(ResourceLocation skillId) {
        PassiveSkill skill = SkillsReloader.getSkillById(skillId);
        if (skill == null) {
            return Component.literal("Unknown Skill: " + skillId.toString()).withStyle(ChatFormatting.RED);
        }
        return getSkillTitle(skill);
    }

    public static Style getSkillTitleStyle(PassiveSkill skill) {
        String titleColor = skill.getTitleColor();
        if (titleColor.isEmpty()) {
            return switch (skill.getSkillSize()) {
                case 30 -> GATEWAY_TITLE_STYLE;
                case 24 -> CLASS_TITLE_STYLE;
                case 20 -> NOTABLE_TITLE_STYLE;
                case 32 -> KEYSTONE_TITLE_STYLE;
                default -> LESSER_TITLE_STYLE;
            };
        } else {
            try {
                return Style.EMPTY.withColor(Integer.parseInt(titleColor, 16));
            } catch (NumberFormatException e) {
                return Style.EMPTY;
            }
        }
    }

    public static Component getRecipeTooltip(@NotNull AbstractWorkbenchRecipe recipe) {
        return recipe.getShortDescription();
    }

    public static Component getRecipeTooltip(ResourceLocation recipeId) {
        ClientLevel level = Minecraft.getInstance().level;
        Objects.requireNonNull(level);
        RecipeManager recipeManager = level.getRecipeManager();
        List<AbstractWorkbenchRecipe> recipes = recipeManager.getAllRecipesFor(PSTRecipeTypes.WORKBENCH);
        AbstractWorkbenchRecipe recipe = recipes.stream().filter(r -> r.getId().equals(recipeId)).findAny().orElse(null);
        if (recipe == null) {
            return Component.literal("Unknown Recipe: " + recipeId.toString()).withStyle(ChatFormatting.RED);
        }
        return getRecipeTooltip(recipe);
    }

    public static String getRecipeDescriptionId(ResourceLocation recipeId) {
        ClientLevel level = Minecraft.getInstance().level;
        Objects.requireNonNull(level);
        RecipeManager recipeManager = level.getRecipeManager();
        List<AbstractWorkbenchRecipe> recipes = recipeManager.getAllRecipesFor(PSTRecipeTypes.WORKBENCH);
        AbstractWorkbenchRecipe recipe = recipes.stream().filter(r -> r.getId().equals(recipeId)).findAny().orElse(null);
        if (recipe == null) {
            return "Unknown Recipe: " + recipeId.toString();
        }
        return recipe.getDescriptionId();
    }
}
