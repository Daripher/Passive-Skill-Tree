package daripher.skilltree.client.widget.editor.menu.selection;

import daripher.skilltree.client.widget.Button;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import net.minecraft.network.chat.Component;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;

public class SelectionMenuButton<T> extends Button {
    protected SelectionList<T> selectionList;
    protected Runnable onMenuInit = () -> {
    };
    protected Consumer<T> responder = t -> {
    };
    protected int selectionListRows = 10;
    protected int selectionListColumns = 10;
    private boolean requiresSearch = true;

    public SelectionMenuButton(SkillTreeEditor editor, int x, int y, int width, String message, Collection<T> values) {
        super(x, y, width, 14, Component.literal(message));
        this.selectionList = new TextSelectionList<>(0, 0, 190, 14, values).setRows(8);
        setPressFunc(b -> selectMenu(editor));
    }

    public SelectionMenuButton(SkillTreeEditor editor, int x, int y, int width, Collection<T> values) {
        this(editor, x, y, width, "", values);
    }

    public SelectionMenuButton<T> setResponder(Consumer<T> responder) {
        this.responder = responder;
        return this;
    }

    public SelectionMenuButton<T> setValue(T value) {
        selectionList.selectElement(value);
        return this;
    }

    public SelectionMenuButton<T> setElementNameGetter(Function<T, Component> nameGetter) {
        selectionList.setNameGetter(nameGetter);
        T value = selectionList.getSelectedElement();
        if (value != null) {
            setMessage(selectionList.getNameGetter().apply(value));
        }
        return this;
    }

    public SelectionMenuButton<T> setElementSize(int width, int height) {
        selectionList.setElementSize(width, height);
        return this;
    }

    public SelectionMenuButton<T> setSelectionListGridSize(int rows, int columns) {
        this.selectionListRows = rows;
        this.selectionListColumns = columns;
        return this;
    }

    public SelectionMenuButton<T> setRequiresSearch(boolean requiresSearch) {
        this.requiresSearch = requiresSearch;
        return this;
    }

    public void setMenuInitFunc(Runnable onMenuInit) {
        this.onMenuInit = onMenuInit;
    }

    protected void selectMenu(SkillTreeEditor editor) {
        SelectionMenu<T> menu = new SelectionMenu<>(editor, editor.getSelectedMenu(), selectionList, onMenuInit).setResponder(responder)
                .setRequiresSearch(requiresSearch);
        editor.selectMenu(menu);
    }
}
