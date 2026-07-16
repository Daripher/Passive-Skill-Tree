package daripher.skilltree.client.widget.editor.menu.selection;

import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;

public class TextureSelectionMenuButton extends SelectionMenuButton<ResourceLocation> {
    private final String texturesFolder;

    public TextureSelectionMenuButton(SkillTreeEditor editor, int x, int y, int width, String message, String texturesFolder, Collection<ResourceLocation> values) {
        super(editor, x, y, width, message, values);
        this.texturesFolder = texturesFolder;
        this.selectionList = new TextureSelectionList(0, 0, 19, 19, 16, 16, values).setColumns(10).setRows(10);
    }

    public TextureSelectionMenuButton setElementSize(int width, int height) {
        selectionList.setElementSize(width, height);
        return this;
    }

    public TextureSelectionMenuButton setElementTextureSize(int width, int height) {
        ((TextureSelectionList) selectionList).setElementTextureSize(width, height);
        return this;
    }

    @Override
    protected void selectMenu(SkillTreeEditor editor) {
        TextureSelectionMenu menu = new TextureSelectionMenu(editor, editor.getSelectedMenu(), (TextureSelectionList) selectionList, texturesFolder, onMenuInit).setSelectionListGridSize(selectionListRows, selectionListColumns)
                .setResponder(responder);
        editor.selectMenu(menu);
    }
}
