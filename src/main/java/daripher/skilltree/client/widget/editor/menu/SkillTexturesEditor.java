package daripher.skilltree.client.widget.editor.menu;

import daripher.skilltree.data.client.SkillTexturesData;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.skill.PassiveSkill;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;
import java.util.function.Function;

public class SkillTexturesEditor extends EditorMenu {
    public SkillTexturesEditor(SkillTreeEditor editor, EditorMenu previousMenu) {
        super(editor, previousMenu);
    }

    @Override
    public void init() {
        editor.addButton(0, 0, 90, 14, "Back").setPressFunc(b -> editor.selectMenu(previousMenu));
        editor.increaseHeight(29);
        PassiveSkill selectedSkill = editor.getFirstSelectedSkill();
        if (selectedSkill == null) {
            return;
        }
        addTextureEditorButton("Frame Texture", this::setFrameTextures, PassiveSkill::getFrameTexture, 3, 2, 95, 19, 48, 16);
        addTextureEditorButton("Tooltip Frame", this::setTooltipFrameTextures, PassiveSkill::getTooltipFrameTexture, 4, 1, 190, 19, 88, 16);
        addTextureEditorButton("Icon Texture", this::setIconTextures, PassiveSkill::getIconTexture, 10, 10, 19, 19, 16, 16);
    }

    private void addTextureEditorButton(String label, Consumer<ResourceLocation> setTextureFunction, Function<PassiveSkill, ResourceLocation> textureProvider, int rows, int columns, int elementWidth, int elementHeight, int elementTextureWidth, int elementTextureHeight) {
        if (editor.canEdit(textureProvider)) {
            PassiveSkill selectedSkill = editor.getFirstSelectedSkill();
            ResourceLocation texture = textureProvider.apply(selectedSkill);
            editor.addLabel(0, 0, label, ChatFormatting.GOLD);
            editor.increaseHeight(19);
            String textureFolder = SkillTexturesData.getTextureFolder(texture);
            editor.addTextureSelectionMenu(0, 0, 200, texture, textureFolder)
                    .setElementTextureSize(elementTextureWidth, elementTextureHeight).setSelectionListGridSize(rows, columns)
                    .setElementNameGetter(TooltipHelper::getTextureName).setElementSize(elementWidth, elementHeight)
                    .setResponder(setTextureFunction);
            editor.increaseHeight(19);
        }
    }

    private void setFrameTextures(ResourceLocation value) {
        editor.getSelectedSkills().forEach(s -> s.setBackgroundTexture(value));
        editor.saveSelectedSkills();
    }

    private void setTooltipFrameTextures(ResourceLocation value) {
        editor.getSelectedSkills().forEach(s -> s.setBorderTexture(value));
        editor.saveSelectedSkills();
    }

    private void setIconTextures(ResourceLocation value) {
        editor.getSelectedSkills().forEach(s -> s.setIconTexture(value));
        editor.saveSelectedSkills();
    }
}
