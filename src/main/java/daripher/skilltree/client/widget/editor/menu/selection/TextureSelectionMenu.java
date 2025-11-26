package daripher.skilltree.client.widget.editor.menu.selection;

import daripher.skilltree.client.data.SkillTexturesData;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.client.widget.editor.menu.EditorMenu;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TextureSelectionMenu extends EditorMenu {
  private @NotNull Consumer<ResourceLocation> responder = v -> {};
  private final TextureSelectionList selectionList;
  private final Runnable onInit;
  private final String texturesFolder;
  private int selectionListRows = 10;
  private int selectionListColumns = 10;

  public TextureSelectionMenu(
      SkillTreeEditor editor,
      @Nullable EditorMenu previousMenu,
      TextureSelectionList selectionList,
      String texturesFolder,
      Runnable onInit) {
    super(editor, previousMenu);
    this.selectionList = selectionList;
    this.onInit = onInit;
    this.texturesFolder = texturesFolder;
  }

  @Override
  public void init() {
    clearWidgets();
    editor.addButton(0, 0, 90, 14, "Back").setPressFunc(b -> editor.selectMenu(previousMenu));
    editor.increaseHeight(29);
    editor.addLabel(0, 0, "Folder", ChatFormatting.GOLD);
    editor.increaseHeight(19);
    editor
        .addTextField(0, 0, 200, 14, texturesFolder)
        .setSoftFilter(SkillTexturesData::isTextureFolder)
        .setSuggestionProvider(SkillTexturesData::autocompleteFolderName)
        .setResponder(
            v -> {
              selectionList.setElementsList(SkillTexturesData.getTexturesInFolder(v));
              selectionList.setColumns(selectionListColumns);
              selectionList.setRows(selectionListRows);
              editor.setHeight(selectionList.getY() + selectionList.getHeight() + 10);
            });
    editor.increaseHeight(19);
    editor.addLabel(0, 0, "Search", ChatFormatting.GOLD);
    editor.increaseHeight(19);
    editor
        .addTextField(0, 0, 200, 14, "")
        .setFocused()
        .setResponder(selectionList::setSearchString);
    editor.increaseHeight(29);
    selectionList.setX(editor.getWidgetsX(0));
    selectionList.setY(editor.getWidgetsY(0));
    selectionList.setColumns(selectionListColumns);
    selectionList.setRows(selectionListRows);
    editor.increaseHeight(selectionList.getHeight() + 10);
    selectionList.setResponder(responder);
    addWidget(selectionList);
    onInit.run();
  }

  public TextureSelectionMenu setResponder(@NotNull Consumer<ResourceLocation> responder) {
    this.responder = responder;
    return this;
  }

  public TextureSelectionMenu setSelectionListGridSize(int rows, int columns) {
    this.selectionListRows = rows;
    this.selectionListColumns = columns;
    return this;
  }
}
