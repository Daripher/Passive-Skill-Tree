package daripher.skilltree.client.widget.editor.menu.bonuses;

import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.client.widget.editor.menu.EditorMenu;
import daripher.skilltree.skill.bonus.item.ItemBonus;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ItemBonusEditor extends EditorMenu {
    private final Consumer<ItemBonus<?>> changesListener;
    private final Supplier<ItemBonus<?>> selectedValueProvider;

    public ItemBonusEditor(SkillTreeEditor editor, EditorMenu previousMenu, Consumer<ItemBonus<?>> changesListener, Supplier<ItemBonus<?>> selectedValueProvider) {
        super(editor, previousMenu);
        this.changesListener = changesListener;
        this.selectedValueProvider = selectedValueProvider;
    }

    @Override
    public void init() {
        editor.addButton(0, 0, 90, 14, "Back").setPressFunc(b -> editor.selectMenu(previousMenu));
        editor.addConfirmationButton(110, 0, 90, 14, "Remove", "Confirm").setPressFunc(b -> {
            changesListener.accept(null);
            editor.selectMenu(previousMenu);
        });
        editor.increaseHeight(29);
        ItemBonus<?> itemBonus = selectedValueProvider.get();
        if (itemBonus == null) {
            editor.selectMenu(previousMenu);
            return;
        }
        itemBonus.addEditorWidgets(editor, changesListener::accept);
    }
}
