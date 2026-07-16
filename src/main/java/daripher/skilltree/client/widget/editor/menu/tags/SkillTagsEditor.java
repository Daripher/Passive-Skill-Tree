package daripher.skilltree.client.widget.editor.menu.tags;

import daripher.skilltree.data.client.SkillTreeEditorData;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.client.widget.editor.menu.EditorMenu;
import daripher.skilltree.skill.PassiveSkill;
import daripher.skilltree.skill.PassiveSkillTree;
import net.minecraft.ChatFormatting;

import java.util.List;
import java.util.Map;

public class SkillTagsEditor extends EditorMenu {
    public SkillTagsEditor(SkillTreeEditor editor, EditorMenu previousMenu) {
        super(editor, previousMenu);
    }

    @Override
    public void init() {
        editor.addButton(0, 0, 90, 14, "Back").setPressFunc(b -> editor.selectMenu(previousMenu));
        editor.increaseHeight(29);
        if (editor.getSelectedSkills().isEmpty()) {
            return;
        }
        PassiveSkill selectedSkill = editor.getFirstSelectedSkill();
        if (selectedSkill == null) {
            return;
        }
        if (!canEditTags()) {
            return;
        }
        PassiveSkillTree skillTree = editor.getSkillTree();
        Map<String, Integer> limitations = skillTree.getSkillLimitations();
        editor.addLabel(0, 0, "Tag List", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        List<String> tags = selectedSkill.getTags();
        for (int i = 0; i < tags.size(); i++) {
            int index = i;
            editor.addTextField(0, 0, 200, 14, tags.get(i)).setResponder(v -> {
                editor.getSelectedSkills().forEach(s -> s.getTags().set(index, v));
                editor.saveSelectedSkills();
            });
            editor.increaseHeight(19);
        }
        editor.increaseHeight(10);
        editor.addButton(0, 0, 90, 14, "Add").setPressFunc(b -> {
            String name = "New Tag";
            while (selectedSkill.getTags().contains(name)) {
                name += "1";
            }
            String finalName = name;
            editor.getSelectedSkills().forEach(s -> s.getTags().add(finalName));
            editor.saveSelectedSkills();
            editor.rebuildWidgets();
        });
        if (!tags.isEmpty()) {
            editor.addButton(110, 0, 90, 14, "Remove").setPressFunc(b -> {
                editor.getSelectedSkills().forEach(s -> s.getTags().remove(tags.size() - 1));
                editor.saveSelectedSkills();
                editor.rebuildWidgets();
            });
        }
        editor.increaseHeight(19);
        editor.addButton(0, 0, 200, 14, "Tree Limitations").setPressFunc(b -> editor.selectMenu(new SkillTagLimitsEditor(editor, this)));
        editor.increaseHeight(19);
        editor.increaseHeight(10);
        editor.addButton(0, 0, 90, 14, "Add").setPressFunc(b -> {
            String name = "New Tag";
            while (limitations.containsKey(name)) {
                name += "1";
            }
            limitations.put(name, 1);
            editor.rebuildWidgets();
            SkillTreeEditorData.saveEditorSkillTree(skillTree);
        });
        editor.increaseHeight(19);
    }

    protected boolean canEditTags() {
        PassiveSkill selectedSkill = editor.getFirstSelectedSkill();
        if (selectedSkill == null) {
            return false;
        }
        if (editor.getSelectedSkills().size() < 2) {
            return true;
        }
        for (PassiveSkill otherSkill : editor.getSelectedSkills()) {
            if (selectedSkill == otherSkill) {
                continue;
            }
            List<String> tags = selectedSkill.getTags();
            List<String> otherTags = otherSkill.getTags();
            if (tags.size() != otherTags.size()) {
                return false;
            }
            for (int i = 0; i < tags.size(); i++) {
                if (!tags.get(i).equals(otherTags.get(i))) {
                    return false;
                }
            }
        }
        return true;
    }
}
