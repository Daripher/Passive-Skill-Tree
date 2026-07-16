package daripher.skilltree.client.widget.editor.menu.tags;

import daripher.skilltree.data.client.SkillTreeEditorData;
import daripher.skilltree.client.widget.NumericTextField;
import daripher.skilltree.client.widget.TextField;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.client.widget.editor.menu.EditorMenu;
import daripher.skilltree.skill.PassiveSkillTree;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SkillTagLimitsEditor extends EditorMenu {
    private final List<Pair<TextField, NumericTextField>> widgetPairs = new ArrayList<>();

    public SkillTagLimitsEditor(SkillTreeEditor editor, EditorMenu previousMenu) {
        super(editor, previousMenu);
    }

    @Override
    public void init() {
        editor.addButton(0, 0, 90, 14, "Back").setPressFunc(b -> editor.selectMenu(previousMenu));
        editor.increaseHeight(29);
        PassiveSkillTree skillTree = editor.getSkillTree();
        Map<String, Integer> limitations = skillTree.getSkillLimitations();
        List<String> tags = limitations.keySet().stream().toList();
        Runnable saveFunc = () -> {
            limitations.clear();
            for (Pair<TextField, NumericTextField> pair : widgetPairs) {
                int limit = (int) pair.getValue().getNumericValue();
                if (limit == 0) {
                    continue;
                }
                String tag = pair.getKey().getValue();
                limitations.put(tag, limit);
            }
            SkillTreeEditorData.saveEditorSkillTree(skillTree);
        };
        for (int i = 0; i < limitations.size(); i++) {
            TextField tagEditor = editor.addTextField(0, 0, 155, 14, tags.get(i));
            NumericTextField limitEditor = editor.addNumericTextField(160, 0, 40, 14, limitations.get(tags.get(i)));
            tagEditor.setResponder(v -> saveFunc.run());
            widgetPairs.add(Pair.of(tagEditor, limitEditor));
            limitEditor.setNumericFilter(d -> d >= 0).setNumericResponder(v -> {
                saveFunc.run();
                if (v == 0) {
                    editor.rebuildWidgets();
                }
            });
            editor.increaseHeight(19);
        }
    }
}
