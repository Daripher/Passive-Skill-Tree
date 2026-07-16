package daripher.skilltree.client.widget.editor.menu;

import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.client.widget.skill.SkillConnection;
import daripher.skilltree.skill.PassiveSkill;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class SkillConnectionsEditor extends EditorMenu {
    public SkillConnectionsEditor(SkillTreeEditor editor, EditorMenu previousMenu) {
        super(editor, previousMenu);
    }

    @Override
    public void init() {
        editor.addButton(0, 0, 90, 14, "Back").setPressFunc(b -> editor.selectMenu(previousMenu));
        editor.increaseHeight(29);
        if (editor.getSelectedSkills().size() < 2) {
            return;
        }
        if (selectedSkillsConnected()) {
            editor.addButton(0, 0, 100, 14, "Disconnect").setPressFunc(b -> disconnectSelectedSkills());
        } else {
            editor.addLabel(0, 0, "Connect");
            editor.increaseHeight(19);
            editor.addButton(0, 0, 100, 14, "Direct").setPressFunc(b -> connectSelectedSkills(SkillConnection.Type.DIRECT));
            editor.increaseHeight(19);
            editor.addButton(0, 0, 100, 14, "Long").setPressFunc(b -> connectSelectedSkills(SkillConnection.Type.LONG));
            editor.increaseHeight(19);
            editor.addButton(0, 0, 100, 14, "One Way").setPressFunc(b -> connectSelectedSkills(SkillConnection.Type.ONE_WAY));
        }
        editor.increaseHeight(19);
    }

    private boolean selectedSkillsConnected() {
        PassiveSkill[] selectedSkills = editor.getSelectedSkills().toArray(new PassiveSkill[0]);
        for (int i = 0; i < selectedSkills.length - 1; i++) {
            PassiveSkill skill1 = selectedSkills[i];
            PassiveSkill skill2 = selectedSkills[i + 1];
            if (!skillsConnected(skill1, skill2)) {
                return false;
            }
        }
        return true;
    }

    private boolean skillsConnected(PassiveSkill first, PassiveSkill second) {
        return first.getDirectConnections().contains(second.getId()) || second.getDirectConnections()
                .contains(first.getId()) || first.getLongConnections().contains(second.getId()) || second.getLongConnections()
                .contains(first.getId()) || first.getOneWayConnections().contains(second.getId()) || second.getOneWayConnections()
                .contains(first.getId());
    }

    private void disconnectSelectedSkills() {
        PassiveSkill[] selectedSkills = editor.getSelectedSkills().toArray(new PassiveSkill[0]);
        for (int i = 0; i < selectedSkills.length - 1; i++) {
            PassiveSkill skill1 = selectedSkills[i];
            PassiveSkill skill2 = selectedSkills[i + 1];
            skill1.getDirectConnections().remove(skill2.getId());
            skill2.getDirectConnections().remove(skill1.getId());
            skill1.getLongConnections().remove(skill2.getId());
            skill2.getLongConnections().remove(skill1.getId());
            skill1.getOneWayConnections().remove(skill2.getId());
            skill2.getOneWayConnections().remove(skill1.getId());
        }
        editor.saveSelectedSkills();
        editor.rebuildWidgets();
    }

    private void connectSelectedSkills(SkillConnection.Type connectionType) {
        PassiveSkill[] selectedSkills = editor.getSelectedSkills().toArray(new PassiveSkill[0]);
        for (int i = 0; i < selectedSkills.length - 1; i++) {
            PassiveSkill skill = selectedSkills[i];
            List<ResourceLocation> connections = switch (connectionType) {
                case DIRECT -> skill.getDirectConnections();
                case LONG -> skill.getLongConnections();
                case ONE_WAY -> skill.getOneWayConnections();
            };
            connections.add(selectedSkills[i + 1].getId());
        }
        editor.saveSelectedSkills();
        editor.rebuildWidgets();
    }
}
