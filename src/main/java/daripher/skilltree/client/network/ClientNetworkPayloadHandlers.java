package daripher.skilltree.client.network;

import daripher.skilltree.client.screen.SkillTreeScreen;
import net.minecraft.client.Minecraft;

public class ClientNetworkPayloadHandlers {
  public static void refreshSkillTreeScreen(int skillPoints) {
    Minecraft minecraft = Minecraft.getInstance();
    if (minecraft.screen instanceof SkillTreeScreen screen) {
      screen.updateSkillPoints(skillPoints);
      screen.init();
    }
  }
}
