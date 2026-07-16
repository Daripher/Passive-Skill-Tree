package daripher.skilltree.client.widget;

import daripher.skilltree.client.screen.SkillTreeScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class SkillTreeSelectionButton extends Button {
    private final ResourceLocation skillTreeId;

    public SkillTreeSelectionButton(int x, int y, int width, int height, ResourceLocation skillTreeId) {
        super(x, y, width, height, Component.translatable(skillTreeId.toString()));
        setPressFunc(b -> onPress(skillTreeId));
        this.skillTreeId = skillTreeId;
    }

    private static void onPress(ResourceLocation skillTreeId) {
        getMinecraft().setScreen(new SkillTreeScreen(skillTreeId));
    }

    protected void renderBackground(@NotNull GuiGraphics graphics) {
        String texturesFolder = "textures/icons/skill_tree/";
        ResourceLocation texture = skillTreeId.withPrefix(texturesFolder).withSuffix(".png");
        int v = getTextureVariant() * 19;
        graphics.blit(texture, getX(), getY(), 0, v, width, height, 19, 57);
    }

    protected void renderText(@NotNull GuiGraphics graphics) {
    }

    private static Minecraft getMinecraft() {
        return Minecraft.getInstance();
    }
}
