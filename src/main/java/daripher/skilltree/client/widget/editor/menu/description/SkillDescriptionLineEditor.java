package daripher.skilltree.client.widget.editor.menu.description;

import daripher.skilltree.data.client.SkillTreeEditorData;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.client.widget.editor.menu.EditorMenu;
import daripher.skilltree.skill.PassiveSkill;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class SkillDescriptionLineEditor extends EditorMenu {
    private final int selectedLine;

    public SkillDescriptionLineEditor(SkillTreeEditor editor, EditorMenu previousMenu, int selectedLine) {
        super(editor, previousMenu);
        this.selectedLine = selectedLine;
    }

    @Override
    public void init() {
        editor.addButton(0, 0, 90, 14, "Back").setPressFunc(b -> editor.selectMenu(previousMenu));
        if (editor.getSelectedSkills().isEmpty()) {
            return;
        }
        PassiveSkill selectedSkill = editor.getFirstSelectedSkill();
        if (selectedSkill == null) {
            return;
        }
        if (!canEditDescription()) {
            return;
        }
        List<MutableComponent> description = selectedSkill.getDescription();
        editor.addConfirmationButton(110, 0, 90, 14, "Remove", "Confirm").setPressFunc(b -> removeDescriptionLine());
        editor.increaseHeight(29);
        if (description == null || selectedLine > description.size()) {
            editor.selectMenu(previousMenu);
            return;
        }
        MutableComponent component = description.get(selectedLine);
        editor.addTextArea(0, 0, 200, 70, component.getString()).setResponder(this::setDescription);
        editor.increaseHeight(75);
        editor.addLabel(0, 0, "Color", ChatFormatting.GOLD);
        Style originalStyle = component.getStyle();
        TextColor textColor = originalStyle.getColor();
        if (textColor == null) {
            textColor = TextColor.fromRgb(0xffffff);
        }
        String color = Integer.toHexString(textColor.getValue());
        editor.addTextField(120, 0, 80, 14, color).setSoftFilter(SkillDescriptionLineEditor::isColorString).setResponder(v -> {
            if (isColorString(v)) {
                int rgb = Integer.parseInt(v, 16);
                setDescriptionStyle(s -> s.withColor(rgb));
            }
        });
        editor.increaseHeight(19);
        editor.addLabel(0, 0, "Bold", ChatFormatting.GOLD);
        editor.addCheckBox(186, 0, originalStyle.isBold()).setResponder(v -> {
            setDescriptionStyle(s -> s.withBold(v));
            editor.rebuildWidgets();
        });
        editor.increaseHeight(19);
        editor.addLabel(0, 0, "Italic", ChatFormatting.GOLD);
        editor.addCheckBox(186, 0, originalStyle.isItalic()).setResponder(v -> {
            setDescriptionStyle(s -> s.withItalic(v));
            editor.rebuildWidgets();
        });
        editor.increaseHeight(19);
        editor.addLabel(0, 0, "Underline", ChatFormatting.GOLD);
        editor.addCheckBox(186, 0, originalStyle.isUnderlined()).setResponder(v -> {
            setDescriptionStyle(s -> s.withUnderlined(v));
            editor.rebuildWidgets();
        });
        editor.increaseHeight(19);
        editor.addLabel(0, 0, "Strikethrough", ChatFormatting.GOLD);
        editor.addCheckBox(186, 0, originalStyle.isStrikethrough()).setResponder(v -> {
            setDescriptionStyle(s -> s.withStrikethrough(v));
            editor.rebuildWidgets();
        });
        editor.increaseHeight(19);
        editor.addLabel(0, 0, "Obfuscated", ChatFormatting.GOLD);
        editor.addCheckBox(186, 0, originalStyle.isObfuscated()).setResponder(v -> {
            setDescriptionStyle(s -> s.withObfuscated(v));
            editor.rebuildWidgets();
        });
        editor.increaseHeight(19);
    }

    private boolean canEditDescription() {
        PassiveSkill selectedSkill = editor.getFirstSelectedSkill();
        if (selectedSkill == null) {
            return false;
        }
        if (editor.getSelectedSkills().size() < 2) {
            return true;
        }
        for (PassiveSkill otherSkill : editor.getSelectedSkills()) {
            List<MutableComponent> description = selectedSkill.getDescription();
            List<MutableComponent> otherDescription = otherSkill.getDescription();
            if (description == null && otherDescription == null) {
                continue;
            }
            if (description == null || otherDescription == null) {
                return false;
            }
            if (description.size() != otherDescription.size()) {
                return false;
            }
            for (int i = 0; i < description.size(); i++) {
                if (!description.get(i).equals(otherDescription.get(i))) {
                    return false;
                }
            }
        }
        return true;
    }

    private void removeDescriptionLine() {
        editor.getSelectedSkills().forEach(skill -> {
            List<MutableComponent> description = skill.getDescription();
            Objects.requireNonNull(description);
            description.remove(selectedLine);
        });
        editor.saveSelectedSkills();
        editor.selectMenu(previousMenu);
        editor.rebuildWidgets();
    }

    private void setDescription(String line) {
        editor.getSelectedSkills().forEach(skill -> {
            List<MutableComponent> description = skill.getDescription();
            Objects.requireNonNull(description);
            MutableComponent component = description.get(selectedLine);
            Style style = component.getStyle();
            description.set(selectedLine, Component.literal(line).withStyle(style));
        });
        editor.saveSelectedSkills();
    }

    private void setDescriptionStyle(Function<Style, Style> styleFunc) {
        editor.getSelectedSkills().forEach(skill -> {
            List<MutableComponent> description = skill.getDescription();
            Objects.requireNonNull(description);
            MutableComponent component = description.get(selectedLine);
            Style style = styleFunc.apply(component.getStyle());
            description.set(selectedLine, component.withStyle(style));
            SkillTreeEditorData.saveEditorSkill(skill);
            SkillTreeEditorData.loadEditorSkill(skill.getId());
        });
    }

    private static boolean isColorString(String v) {
        return v.matches("^[a-fA-F0-9]{6}");
    }
}
