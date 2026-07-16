package daripher.skilltree.client.widget.editor.menu.bonuses;

import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.client.widget.editor.menu.EditorMenu;
import daripher.skilltree.skill.bonus.SkillBonus;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class SkillBonusEditor extends EditorMenu {
    private final Consumer<SkillBonus<?>> changesListener;
    private final Supplier<SkillBonus<?>> selectedValueProvider;

    public SkillBonusEditor(SkillTreeEditor editor, EditorMenu previousMenu, Consumer<SkillBonus<?>> changesListener, Supplier<SkillBonus<?>> selectedValueProvider) {
        super(editor, previousMenu);
        this.changesListener = changesListener;
        this.selectedValueProvider = selectedValueProvider;
    }

    @Override
    public void init() {
        editor.addButton(0, 0, 90, 14, "Back").setPressFunc(b -> editor.selectMenu(previousMenu));
        editor.addConfirmationButton(110, 0, 90, 14, "Remove", "Confirm").setPressFunc(button -> changesListener.accept(null));
        editor.increaseHeight(29);
        SkillBonus<?> skillBonus = selectedValueProvider.get();
        if (skillBonus == null) {
            editor.selectMenu(previousMenu);
            return;
        }
        skillBonus.addEditorWidgets(editor, changesListener::accept);
    }
}
