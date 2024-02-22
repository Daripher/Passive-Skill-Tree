package daripher.skilltree.client.widget;

public class SkillConnection {
  private final Type type;
  private final SkillButton button1;
  private final SkillButton button2;

  public SkillConnection(Type type, SkillButton button1, SkillButton button2) {
    this.type = type;
    this.button1 = button1;
    this.button2 = button2;
  }

  public void setActive() {
    if (button1.highlighted == button2.highlighted) return;
    if (type != Type.ONE_WAY) {
      if (!button1.highlighted) {
        button1.setAnimated();
        button1.setActive();
      }
    }
    if (!button2.highlighted) {
      button2.setAnimated();
      button2.setActive();
    }
  }

  public SkillButton getFirstButton() {
    return button1;
  }

  public SkillButton getSecondButton() {
    return button2;
  }

  public Type getType() {
    return type;
  }

  public enum Type {
    DIRECT,
    LONG,
    ONE_WAY
  }
}
