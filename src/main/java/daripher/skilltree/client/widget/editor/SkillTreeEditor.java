package daripher.skilltree.client.widget.editor;

import daripher.skilltree.client.data.SkillTreeClientData;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.client.widget.*;
import daripher.skilltree.client.widget.editor.menu.EditorMenu;
import daripher.skilltree.client.widget.editor.menu.MainEditorMenu;
import daripher.skilltree.client.widget.editor.menu.selection.SelectionMenuButton;
import daripher.skilltree.client.widget.group.WidgetGroup;
import daripher.skilltree.client.widget.skill.SkillButton;
import daripher.skilltree.client.widget.skill.SkillButtons;
import daripher.skilltree.init.*;
import daripher.skilltree.skill.PassiveSkill;
import daripher.skilltree.skill.PassiveSkillTree;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.condition.damage.DamageCondition;
import daripher.skilltree.skill.bonus.condition.enchantment.EnchantmentCondition;
import daripher.skilltree.skill.bonus.condition.item.ItemCondition;
import daripher.skilltree.skill.bonus.condition.living.LivingCondition;
import daripher.skilltree.skill.bonus.condition.living.numeric.NumericValueProvider;
import daripher.skilltree.skill.bonus.event.SkillEventListener;
import daripher.skilltree.skill.bonus.multiplier.LivingMultiplier;
import daripher.skilltree.skill.requirement.SkillRequirement;
import daripher.skilltree.skill.requirement.StatRequirement;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
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
    if (!skillSelector.getSelectedSkills().isEmpty()) {
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

  public ConfirmationButton addConfirmationButton(
      int x, int y, int width, int height, String message, String confirmationMessage) {
    ConfirmationButton button =
        new ConfirmationButton(
            getWidgetsX(x), getWidgetsY(y), width, height, Component.literal(message));
    button.setConfirmationMessage(Component.literal(confirmationMessage));
    return addWidget(button);
  }

  public TextField addTextField(int x, int y, int width, int height, String defaultValue) {
    return addWidget(new TextField(getWidgetsX(x), getWidgetsY(y), width, height, defaultValue));
  }

  public NumericTextField addNumericTextField(
      int x, int y, int width, int height, double defaultValue) {
    return addWidget(
        new NumericTextField(getWidgetsX(x), getWidgetsY(y), width, height, defaultValue));
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

  @SuppressWarnings("rawtypes")
  public SelectionMenuButton<SkillBonus> addSelectionMenu(
      int x, int y, int width, SkillBonus defaultValue) {
    Collection<SkillBonus> values = PSTSkillBonuses.bonusList();
    return addSelectionMenu(x, y, width, values)
        .setValue(defaultValue)
        .setElementNameGetter(b -> Component.literal(PSTSkillBonuses.getName(b)));
  }

  @SuppressWarnings("rawtypes")
  public SelectionMenuButton<SkillRequirement> addSelectionMenu(
      int x, int y, int width, SkillRequirement defaultValue) {
    Collection<SkillRequirement> values = PSTSkillRequirements.requirementList();
    return addSelectionMenu(x, y, width, values)
        .setValue(defaultValue)
        .setElementNameGetter(b -> Component.literal(PSTSkillRequirements.getName(b)));
  }

  public SelectionMenuButton<StatRequirement> addSelectionMenu(
      int x, int y, int width, StatRequirement defaultValue) {
    Collection<StatRequirement> values = getDefaultRequirementInstances();
    return addSelectionMenu(x, y, width, values)
        .setValue(defaultValue)
        .setElementNameGetter(r -> Component.literal(r.getStatTypeId().getPath()));
  }

  private Collection<StatRequirement> getDefaultRequirementInstances() {
    return ForgeRegistries.STAT_TYPES.getValues().stream()
        .map(SkillTreeEditor::createDefaultRequirement)
        .filter(Objects::nonNull)
        .toList();
  }

  private static @Nullable StatRequirement createDefaultRequirement(StatType<?> statType) {
    ResourceLocation statId = ForgeRegistries.STAT_TYPES.getKey(statType);
    Registry<Object> statRegistry = (Registry<Object>) statType.getRegistry();
    Object stat = statRegistry.byId(0);
    if (stat == null) {
      return null;
    }
    return new StatRequirement(statId, statRegistry.getKey(stat), 1);
  }

  @SuppressWarnings("rawtypes")
  public SelectionMenuButton<NumericValueProvider> addSelectionMenu(
      int x, int y, int width, NumericValueProvider defaultValue) {
    Collection<NumericValueProvider> values = PSTNumericValueProviders.providerList();
    return addSelectionMenu(x, y, width, values)
        .setValue(defaultValue)
        .setElementNameGetter(p -> Component.literal(PSTNumericValueProviders.getName(p)));
  }

  public SelectionMenuButton<Attribute> addSelectionMenu(
      int x, int y, int width, Attribute defaultValue) {
    Collection<Attribute> values = PSTAttributes.attributeList();
    return addSelectionMenu(x, y, width, values)
        .setValue(defaultValue)
        .setElementNameGetter(a -> Component.literal(PSTAttributes.getName(a)));
  }

  public SelectionMenuButton<LivingCondition> addSelectionMenu(
      int x, int y, int width, LivingCondition defaultValue) {
    Collection<LivingCondition> values = PSTLivingConditions.conditionsList();
    return addSelectionMenu(x, y, width, values)
        .setValue(defaultValue)
        .setElementNameGetter(c -> Component.literal(PSTLivingConditions.getName(c)));
  }

  public SelectionMenuButton<LivingMultiplier> addSelectionMenu(
      int x, int y, int width, LivingMultiplier defaultValue) {
    Collection<LivingMultiplier> values = PSTLivingMultipliers.multiplierList();
    return addSelectionMenu(x, y, width, values)
        .setValue(defaultValue)
        .setElementNameGetter(m -> Component.literal(PSTLivingMultipliers.getName(m)));
  }

  public SelectionMenuButton<ItemCondition> addSelectionMenu(
      int x, int y, int width, ItemCondition defaultValue) {
    Collection<ItemCondition> values = PSTItemConditions.conditionsList();
    return addSelectionMenu(x, y, width, values)
        .setValue(defaultValue)
        .setElementNameGetter(c -> Component.literal(PSTItemConditions.getName(c)));
  }

  public SelectionMenuButton<MobEffect> addSelectionMenu(
      int x, int y, int width, MobEffect defaultValue) {
    Collection<MobEffect> values = ForgeRegistries.MOB_EFFECTS.getValues();
    return addSelectionMenu(x, y, width, values)
        .setValue(defaultValue)
        .setElementNameGetter(e -> Component.literal(e.getDescriptionId()));
  }

  public SelectionMenuButton<DamageCondition> addSelectionMenu(
      int x, int y, int width, DamageCondition defaultValue) {
    List<DamageCondition> values = PSTDamageConditions.conditionsList();
    return addSelectionMenu(x, y, width, values)
        .setValue(defaultValue)
        .setElementNameGetter(c -> Component.translatable(PSTDamageConditions.getName(c)));
  }

  public SelectionMenuButton<SkillEventListener> addSelectionMenu(
      int x, int y, int width, SkillEventListener defaultValue) {
    List<SkillEventListener> values = PSTEventListeners.eventsList();
    return addSelectionMenu(x, y, width, values)
        .setValue(defaultValue)
        .setElementNameGetter(e -> Component.translatable(PSTEventListeners.getName(e)));
  }

  public SelectionMenuButton<EnchantmentCondition> addSelectionMenu(
      int x, int y, int width, EnchantmentCondition defaultValue) {
    List<EnchantmentCondition> values = PSTEnchantmentConditions.conditionsList();
    return addSelectionMenu(x, y, width, values)
        .setValue(defaultValue)
        .setElementNameGetter(c -> Component.translatable(PSTEnchantmentConditions.getName(c)));
  }

  public <T extends Enum<T>> SelectionMenuButton<T> addSelectionMenu(
      int x, int y, int width, T defaultValue) {
    List<T> values = getEnumValues(defaultValue);
    return addSelectionMenu(x, y, width, values).setValue(defaultValue);
  }

  public <T> SelectionMenuButton<T> addSelectionMenu(
      int x, int y, int width, Collection<T> values) {
    return addWidget(
        new SelectionMenuButton<>(this, getWidgetsX(x), getWidgetsY(y), width, values));
  }

  public <T> SelectionList<T> addSelection(
      int x, int y, int width, T defaultValue, Collection<T> values, int maxDisplayed) {
    SelectionList<T> widget =
        new SelectionList<>(getWidgetsX(x), getWidgetsY(y), width, values)
            .setMaxDisplayed(maxDisplayed)
            .setValue(defaultValue);
    return addWidget(widget);
  }

  public SelectionList<AttributeModifier.Operation> addOperationSelection(
      int x, int y, int width, AttributeModifier.Operation defaultValue) {
    List<AttributeModifier.Operation> values = List.of(AttributeModifier.Operation.values());
    return addSelection(x, y, width, defaultValue, values, 1)
        .setNameGetter(TooltipHelper::getOperationName);
  }

  public <T extends Enum<T>> SelectionList<T> addSelection(
      int x, int y, int width, int maxDisplayed, T defaultValue) {
    List<T> values = getEnumValues(defaultValue);
    return addSelection(x, y, width, defaultValue, values, maxDisplayed);
  }

  @NotNull
  private static <T extends Enum<T>> List<T> getEnumValues(T defaultValue) {
    Class<T> enumType = (Class<T>) defaultValue.getClass();
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
    skillSelector.getSelectedSkills().forEach(SkillTreeClientData::saveEditorSkill);
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
    return getSkillTree().getSkillIds().stream().map(SkillTreeClientData::getEditorSkill).toList();
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

  public boolean canEditSkillBonuses() {
    PassiveSkill selectedSkill = getFirstSelectedSkill();
    if (selectedSkill == null) return false;
    for (PassiveSkill otherSkill : getSelectedSkills()) {
      if (otherSkill == selectedSkill) continue;
      List<SkillBonus<?>> bonuses = otherSkill.getBonuses();
      List<SkillBonus<?>> otherBonuses = selectedSkill.getBonuses();
      if (bonuses.size() != otherBonuses.size()) return false;
      for (int i = 0; i < bonuses.size(); i++) {
        if (!bonuses.get(i).sameBonus(otherBonuses.get(i))) return false;
      }
    }
    return true;
  }

  public boolean canEditSkillRequirements() {
    PassiveSkill selectedSkill = getFirstSelectedSkill();
    if (selectedSkill == null) return false;
    for (PassiveSkill otherSkill : getSelectedSkills()) {
      if (otherSkill == selectedSkill) continue;
      List<SkillRequirement<?>> requirements = otherSkill.getRequirements();
      List<SkillRequirement<?>> otherRequirements = selectedSkill.getRequirements();
      if (requirements.size() != otherRequirements.size()) return false;
      for (int i = 0; i < requirements.size(); i++) {
        if (!requirements.get(i).equals(otherRequirements.get(i))) return false;
      }
    }
    return true;
  }
}
