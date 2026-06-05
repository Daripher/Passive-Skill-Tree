package daripher.skilltree.client.widget.editor;

import daripher.skilltree.skill.PassiveSkill;
import org.apache.logging.log4j.util.TriConsumer;

@FunctionalInterface
public interface SkillFactory extends TriConsumer<Float, Float, PassiveSkill> {
}
