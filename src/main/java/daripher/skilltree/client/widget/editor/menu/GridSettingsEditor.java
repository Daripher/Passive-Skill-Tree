package daripher.skilltree.client.widget.editor.menu;

import daripher.skilltree.client.widget.editor.SkillDragger;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import net.minecraft.ChatFormatting;

public class GridSettingsEditor extends EditorMenu {

    public GridSettingsEditor(SkillTreeEditor editor, EditorMenu previousMenu) {
        super(editor, previousMenu);
    }

    @Override
    public void init() {
        SkillDragger skillDragger = editor.getSkillDragger();
        editor.addButton(0, 0, 90, 14, "Back").setPressFunc(b -> editor.selectMenu(previousMenu));
        editor.increaseHeight(29);
        editor.addLabel(0, 0, "Grid Size", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addNumericTextField(0, 0, 50, 14, skillDragger.getGridSizeX()).setNumericFilter(v -> v > 0 && v.intValue() == v)
                .setNumericResponder(v -> skillDragger.setGridSizeX(v.intValue()));
        editor.addNumericTextField(100, 0, 50, 14, skillDragger.getGridSizeY()).setNumericFilter(v -> v > 0 && v.intValue() == v)
                .setNumericResponder(v -> skillDragger.setGridSizeY(v.intValue()));
        editor.increaseHeight(19);
        editor.addLabel(0, 0, "Grid Snap", ChatFormatting.GOLD);
        editor.addLabel(100, 0, "Show Grid", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addCheckBox(0, 0, skillDragger.isGridSnapEnabled()).setResponder(skillDragger::setGridSnapEnabled);
        editor.addCheckBox(100, 0, skillDragger.isShowGrid()).setResponder(skillDragger::setShowGrid);
        editor.increaseHeight(19);
    }
}
