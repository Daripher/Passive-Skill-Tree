package daripher.skilltree.client.widget.editor;

import daripher.skilltree.attribute.AttributesHelper;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.client.widget.*;
import daripher.skilltree.client.widget.editor.menu.EditorMenu;
import daripher.skilltree.client.widget.editor.menu.MainEditorMenu;
import daripher.skilltree.client.widget.editor.menu.selection.SelectionList;
import daripher.skilltree.client.widget.editor.menu.selection.SelectionMenuButton;
import daripher.skilltree.client.widget.editor.menu.selection.TextSelectionList;
import daripher.skilltree.client.widget.editor.menu.selection.TextureSelectionMenuButton;
import daripher.skilltree.client.widget.group.WidgetGroup;
import daripher.skilltree.client.widget.skill.SkillButton;
import daripher.skilltree.client.widget.skill.SkillButtons;
import daripher.skilltree.data.client.SkillTexturesData;
import daripher.skilltree.data.client.SkillTreeEditorData;
import daripher.skilltree.init.*;
import daripher.skilltree.init.predicate.*;
import daripher.skilltree.skill.PassiveSkill;
import daripher.skilltree.skill.PassiveSkillTree;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.event.SkillEventListener;
import daripher.skilltree.skill.bonus.function.FloatFunction;
import daripher.skilltree.skill.bonus.item.ItemBonus;
import daripher.skilltree.skill.bonus.multiplier.LivingMultiplier;
import daripher.skilltree.skill.bonus.predicate.damage.DamageCondition;
import daripher.skilltree.skill.bonus.predicate.effect.MobEffectPredicate;
import daripher.skilltree.skill.bonus.predicate.enchantment.EnchantmentCondition;
import daripher.skilltree.skill.bonus.predicate.item.ItemStackPredicate;
import daripher.skilltree.skill.bonus.predicate.living.LivingEntityPredicate;
import daripher.skilltree.skill.requirement.SkillRequirement;
import daripher.skilltree.skill.requirement.StatRequirement;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.StatType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

public class SkillTreeEditor extends WidgetGroup<AbstractWidget> {
    private final SkillButtons skillButtons;
    private final SkillSelector skillSelector;
    private final SkillMirrorer skillMirrorer;
    private final SkillDragger skillDragger;
    private @NotNull EditorMenu selectedMenu = new MainEditorMenu(this);

    public SkillTreeEditor(SkillButtons skillButtons) {
        super(0, 0, 0, 0);
        this.skillButtons = skillButtons;
        this.skillSelector = new SkillSelector(this, skillButtons);
        this.skillMirrorer = new SkillMirrorer(this);
        this.skillDragger = new SkillDragger(this);
    }

    public void init() {
        clearWidgets();
        addWidget(selectedMenu);
        addWidget(skillSelector);
        addWidget(skillDragger);
        addWidget(skillMirrorer);
        selectedMenu.init();
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        skillMirrorer.render(graphics, mouseX, mouseY, partialTick);
        skillDragger.render(graphics, mouseX, mouseY, partialTick);
        if (height > 0) {
            graphics.fill(getX(), getY(), getX() + width, getY() + height, 0xDD000000);
        }
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode != GLFW.GLFW_KEY_ESCAPE) {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
        if (selectedMenu.previousMenu != null) {
            selectMenu(selectedMenu.previousMenu);
            return true;
        }
        if (!skillSelector.getSelectedSkills().isEmpty()) {
            skillSelector.clearSelection();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    public void selectMenu(EditorMenu menu) {
        if (menu != null) {
            selectedMenu = menu;
            rebuildWidgets();
        }
    }

    public Button addButton(int x, int y, int width, int height, String message) {
        return addButton(x, y, width, height, Component.literal(message));
    }

    public Button addButton(int x, int y, int width, int height, Component message) {
        return addWidget(new Button(getWidgetsX(x), getWidgetsY(y), width, height, message));
    }

    public ConfirmationButton addConfirmationButton(int x, int y, int width, int height, String message, String confirmationMessage) {
        ConfirmationButton button = new ConfirmationButton(getWidgetsX(x), getWidgetsY(y), width, height, Component.literal(message));
        button.setConfirmationMessage(Component.literal(confirmationMessage));
        return addWidget(button);
    }

    public TextField addTextField(int x, int y, int width, int height, String defaultValue) {
        return addWidget(new TextField(getWidgetsX(x), getWidgetsY(y), width, height, defaultValue));
    }

    public NumericTextField addNumericTextField(int x, int y, int width, int height, double defaultValue) {
        return addWidget(new NumericTextField(getWidgetsX(x), getWidgetsY(y), width, height, defaultValue));
    }

    public TextArea addTextArea(int x, int y, int width, int height, String defaultValue) {
        return addWidget(new TextArea(getWidgetsX(x), getWidgetsY(y), width, height, defaultValue));
    }

    public Label addLabel(int x, int y, String text, ChatFormatting... styles) {
        MutableComponent message = Component.literal(text);
        for (ChatFormatting style : styles) {
            message.withStyle(style);
        }
        return addWidget(new Label(getWidgetsX(x), getWidgetsY(y), message));
    }

    public CheckBox addCheckBox(int x, int y, boolean value) {
        return addWidget(new CheckBox(getWidgetsX(x), getWidgetsY(y), value));
    }

    public TextureSelectionMenuButton addTextureSelectionMenu(int x, int y, int width, ResourceLocation currentValue, String folder) {
        Collection<ResourceLocation> values = SkillTexturesData.getTexturesInFolder(folder);
        x = getWidgetsX(x);
        y = getWidgetsY(y);
        String message = currentValue.toString();
        TextureSelectionMenuButton button = (TextureSelectionMenuButton) new TextureSelectionMenuButton(this, x, y, width, message, folder, values).setValue(currentValue)
                .setElementNameGetter(TooltipHelper::getTextureName);
        return addWidget(button);
    }

    @SuppressWarnings("rawtypes")
    public SelectionMenuButton<SkillBonus> addSelectionMenu(int x, int y, int width, SkillBonus defaultValue) {
        Collection<SkillBonus> values = PSTSkillBonuses.defaultInstances();
        return addSelectionMenu(x, y, width, values).setValue(defaultValue)
                .setElementNameGetter(b -> Component.literal(PSTSkillBonuses.getName(b)));
    }

    @SuppressWarnings("rawtypes")
    public SelectionMenuButton<SkillRequirement> addSelectionMenu(int x, int y, int width, SkillRequirement defaultValue) {
        Collection<SkillRequirement> values = PSTSkillRequirements.requirementList();
        return addSelectionMenu(x, y, width, values).setValue(defaultValue)
                .setElementNameGetter(b -> Component.literal(PSTSkillRequirements.getName(b)));
    }

    public SelectionMenuButton<StatRequirement> addSelectionMenu(int x, int y, int width, StatRequirement defaultValue) {
        Collection<StatRequirement> values = getDefaultRequirementInstances();
        return addSelectionMenu(x, y, width, values).setValue(defaultValue)
                .setElementNameGetter(r -> Component.literal(r.getStatTypeId().getPath()));
    }

    private Collection<StatRequirement> getDefaultRequirementInstances() {
        return ForgeRegistries.STAT_TYPES.getValues().stream().map(SkillTreeEditor::createDefaultRequirement).filter(Objects::nonNull)
                .toList();
    }

    private static @Nullable <T> StatRequirement createDefaultRequirement(StatType<T> statType) {
        ResourceLocation statId = ForgeRegistries.STAT_TYPES.getKey(statType);
        Registry<T> statRegistry = statType.getRegistry();
        T stat = statRegistry.byId(0);
        if (stat == null) {
            return null;
        }
        return new StatRequirement(statId, statRegistry.getKey(stat), 1);
    }

    @SuppressWarnings("rawtypes")
    public SelectionMenuButton<FloatFunction> addSelectionMenu(int x, int y, int width, FloatFunction defaultValue) {
        Collection<FloatFunction> values = PSTFloatFunctions.providerList();
        return addSelectionMenu(x, y, width, values).setValue(defaultValue)
                .setElementNameGetter(p -> Component.literal(PSTFloatFunctions.getName(p)));
    }

    public SelectionMenuButton<Attribute> addSelectionMenu(int x, int y, int width, Attribute defaultValue) {
        Collection<Attribute> values = AttributesHelper.attributeList();
        return addSelectionMenu(x, y, width, values).setValue(defaultValue)
                .setElementNameGetter(a -> Component.literal(AttributesHelper.getName(a)));
    }

    public SelectionMenuButton<LivingEntityPredicate> addSelectionMenu(int x, int y, int width, LivingEntityPredicate defaultValue) {
        Collection<LivingEntityPredicate> values = PSTLivingEntityPredicates.conditionsList();
        return addSelectionMenu(x, y, width, values).setValue(defaultValue)
                .setElementNameGetter(c -> Component.literal(PSTLivingEntityPredicates.getName(c)));
    }

    public SelectionMenuButton<MobEffectPredicate> addSelectionMenu(int x, int y, int width, MobEffectPredicate defaultValue) {
        Collection<MobEffectPredicate> values = PSTMobEffectPredicates.defaultInstances();
        return addSelectionMenu(x, y, width, values).setValue(defaultValue)
                .setElementNameGetter(c -> Component.literal(PSTMobEffectPredicates.getName(c)));
    }

    public SelectionMenuButton<LivingMultiplier> addSelectionMenu(int x, int y, int width, LivingMultiplier defaultValue) {
        Collection<LivingMultiplier> values = PSTLivingMultipliers.multiplierList();
        return addSelectionMenu(x, y, width, values).setValue(defaultValue)
                .setElementNameGetter(m -> Component.literal(PSTLivingMultipliers.getName(m)));
    }

    public SelectionMenuButton<ItemStackPredicate> addSelectionMenu(int x, int y, int width, ItemStackPredicate defaultValue) {
        Collection<ItemStackPredicate> values = PSTItemPredicates.conditionsList();
        return addSelectionMenu(x, y, width, values).setValue(defaultValue)
                .setElementNameGetter(c -> Component.literal(PSTItemPredicates.getName(c)));
    }

    @SuppressWarnings("rawtypes")
    public SelectionMenuButton<ItemBonus> addSelectionMenu(int x, int y, int width, ItemBonus defaultValue) {
        Collection<ItemBonus> values = PSTItemBonuses.bonusList();
        return addSelectionMenu(x, y, width, values).setValue(defaultValue)
                .setElementNameGetter(c -> Component.literal(PSTItemBonuses.getName(c)));
    }

    public SelectionMenuButton<MobEffect> addSelectionMenu(int x, int y, int width, MobEffect defaultValue) {
        Collection<MobEffect> values = ForgeRegistries.MOB_EFFECTS.getValues();
        return addSelectionMenu(x, y, width, values).setValue(defaultValue)
                .setElementNameGetter(e -> Component.literal(e.getDescriptionId()));
    }

    public SelectionMenuButton<DamageCondition> addSelectionMenu(int x, int y, int width, DamageCondition defaultValue) {
        List<DamageCondition> values = PSTDamagePredicates.conditionsList();
        return addSelectionMenu(x, y, width, values).setValue(defaultValue)
                .setElementNameGetter(c -> Component.translatable(PSTDamagePredicates.getName(c)));
    }

    public SelectionMenuButton<SkillEventListener> addSelectionMenu(int x, int y, int width, SkillEventListener defaultValue) {
        List<SkillEventListener> values = PSTEventListeners.eventsList();
        return addSelectionMenu(x, y, width, values).setValue(defaultValue)
                .setElementNameGetter(e -> Component.translatable(PSTEventListeners.getName(e)));
    }

    public SelectionMenuButton<EnchantmentCondition> addSelectionMenu(int x, int y, int width, EnchantmentCondition defaultValue) {
        List<EnchantmentCondition> values = PSTEnchantmentPredicates.conditionsList();
        return addSelectionMenu(x, y, width, values).setValue(defaultValue)
                .setElementNameGetter(c -> Component.translatable(PSTEnchantmentPredicates.getName(c)));
    }

    public <T extends Enum<T>> SelectionMenuButton<T> addSelectionMenu(int x, int y, int width, T defaultValue) {
        List<T> values = getEnumValues(defaultValue);
        return addSelectionMenu(x, y, width, values).setValue(defaultValue);
    }

    public <T> SelectionMenuButton<T> addSelectionMenu(int x, int y, int width, Collection<T> values) {
        return addWidget(new SelectionMenuButton<>(this, getWidgetsX(x), getWidgetsY(y), width, values));
    }

    public <T> SelectionList<T> addSelection(int x, int y, int width, T defaultValue, Collection<T> values, int maxDisplayed) {
        SelectionList<T> widget = new TextSelectionList<>(getWidgetsX(x), getWidgetsY(y), width, 14, values).setRows(maxDisplayed)
                .selectElement(defaultValue);
        return addWidget(widget);
    }

    public SelectionList<AttributeModifier.Operation> addOperationSelection(int x, int y, int width, AttributeModifier.Operation defaultValue) {
        List<AttributeModifier.Operation> values = List.of(AttributeModifier.Operation.values());
        return addSelection(x, y, width, defaultValue, values, 1).setNameGetter(TooltipHelper::getOperationName);
    }

    public <T extends Enum<T>> SelectionList<T> addSelection(int x, int y, int width, int maxDisplayed, T defaultValue) {
        List<T> values = getEnumValues(defaultValue);
        return addSelection(x, y, width, defaultValue, values, maxDisplayed);
    }

    @NotNull
    private static <T extends Enum<T>> List<T> getEnumValues(T defaultValue) {
        Class<T> enumType = defaultValue.getDeclaringClass();
        return List.of(enumType.getEnumConstants());
    }

    public void addMirrorerWidgets() {
        skillMirrorer.init();
    }

    public Set<PassiveSkill> getSelectedSkills() {
        return skillSelector.getSelectedSkills();
    }

    @Nullable
    public PassiveSkill getFirstSelectedSkill() {
        return skillSelector.getFirstSelectedSkill();
    }

    public SkillMirrorer getSkillMirrorer() {
        return skillMirrorer;
    }

    public void saveSelectedSkills() {
        skillSelector.getSelectedSkills().forEach(SkillTreeEditorData::saveEditorSkill);
    }

    public int getWidgetsY(int y) {
        return getHeight() + y;
    }

    public int getWidgetsX(int x) {
        return getX() + 5 + x;
    }

    public float getScrollX() {
        return skillButtons.getScrollX();
    }

    public float getScrollY() {
        return skillButtons.getScrollY();
    }

    public float getZoom() {
        return skillButtons.getZoom();
    }

    public void increaseHeight(int delta) {
        setHeight(getHeight() + delta);
    }

    public PassiveSkillTree getSkillTree() {
        return skillButtons.getSkillTree();
    }

    public List<PassiveSkill> getSkills() {
        return getSkillTree().getSkillIds().stream().map(SkillTreeEditorData::getEditorSkill).toList();
    }

    public Collection<SkillButton> getSkillButtons() {
        return skillButtons.getWidgets();
    }

    public void addSkillButton(PassiveSkill skill) {
        SkillButton button = skillButtons.addSkillButton(skill, () -> 0f);
        button.skillLearned = true;
    }

    public void updateSkillConnections() {
        skillButtons.updateSkillConnections();
    }

    @Override
    public void rebuildWidgets() {
        super.rebuildWidgets();
        updateSkillConnections();
    }

    public boolean canEdit(Function<PassiveSkill, ?> function) {
        return getSelectedSkills().stream().map(function).distinct().count() <= 1;
    }

    public void removeSkillButton(PassiveSkill skill) {
        skillButtons.getWidgets().removeIf(button -> button.skill == skill);
    }

    public SkillButton getSkillButton(ResourceLocation skillId) {
        return skillButtons.getWidgetById(skillId);
    }

    public int getScreenWidth() {
        return skillButtons.getWidth();
    }

    public int getScreenHeight() {
        return skillButtons.getHeight();
    }

    public @NotNull EditorMenu getSelectedMenu() {
        return selectedMenu;
    }

    public boolean selectedMismatchedBonuses() {
        PassiveSkill selectedSkill = getFirstSelectedSkill();
        if (selectedSkill == null) {
            return true;
        }
        for (PassiveSkill otherSkill : getSelectedSkills()) {
            if (otherSkill == selectedSkill) {
                continue;
            }
            List<SkillBonus<?>> bonuses = otherSkill.getBonuses();
            List<SkillBonus<?>> otherBonuses = selectedSkill.getBonuses();
            if (bonuses.size() != otherBonuses.size()) {
                return true;
            }
            for (int i = 0; i < bonuses.size(); i++) {
                if (!bonuses.get(i).sameBonus(otherBonuses.get(i))) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean selectedMismatchingRequirements() {
        PassiveSkill selectedSkill = getFirstSelectedSkill();
        if (selectedSkill == null) {
            return true;
        }
        for (PassiveSkill otherSkill : getSelectedSkills()) {
            if (otherSkill == selectedSkill) {
                continue;
            }
            List<SkillRequirement<?>> requirements = otherSkill.getRequirements();
            List<SkillRequirement<?>> otherRequirements = selectedSkill.getRequirements();
            if (requirements.size() != otherRequirements.size()) {
                return true;
            }
            for (int i = 0; i < requirements.size(); i++) {
                if (!requirements.get(i).equals(otherRequirements.get(i))) {
                    return true;
                }
            }
        }
        return false;
    }

    public SkillDragger getSkillDragger() {
        return skillDragger;
    }
}
