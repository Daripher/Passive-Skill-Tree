package daripher.skilltree.client.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import daripher.skilltree.client.screen.ScreenHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class CheckBox extends net.minecraft.client.gui.components.Button{


    private boolean isOn = false;
    protected net.minecraft.client.gui.components.Button.OnPress pressOnFunc;
    protected net.minecraft.client.gui.components.Button.OnPress pressOffFunc;

    private static final ResourceLocation OnTexture = new ResourceLocation("skilltree:textures/screen/widgets.png");
    private static final ResourceLocation OffTexture = new ResourceLocation("skilltree:textures/screen/widgets.png");

    public CheckBox(int pX, int pY, int pWidth, int pHeight) {
        super(pX, pY, pWidth, pHeight, Component.literal(""), b -> {});
        this.pressOnFunc = b -> {};
        this.pressOffFunc = b -> {};
    }
    public CheckBox(int pX, int pY, int pWidth, int pHeight, boolean isOn) {
        super(pX, pY, pWidth, pHeight, Component.literal(""), b -> {});
        this.pressOnFunc = b -> {};
        this.pressOffFunc = b -> {};
        this.isOn = isOn;
    }

    public void setPressOnFunc(net.minecraft.client.gui.components.Button.OnPress pressFunc){
        this.pressOnFunc = pressFunc;
    }
    public void setPressOffFunc(net.minecraft.client.gui.components.Button.OnPress pressFunc){
        this.pressOffFunc = pressFunc;
    }

    @Override
    public void onPress() {
        if(isOn){
            pressOffFunc.onPress(this);
            setOff();
        } else{
            setOn();
            pressOnFunc.onPress(this);
        }
    }

    @Override
    public void renderButton(
            @NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        if (!isOn){
            ScreenHelper.prepareTextureRendering(OnTexture);
            int v = !isActive() ? 0 : isHoveredOrFocused() ? 56 : 42;
            blit(poseStack, x, y, 0, v, width / 2, height);
            blit(poseStack, x + width / 2, y, -width / 2, v, width / 2, height);
        } else {
            ScreenHelper.prepareTextureRendering(OffTexture);
            int v = !isActive() ? 0 : isHoveredOrFocused() ? 28 : 14;
            blit(poseStack, x, y, 0, v, width / 2, height);
            blit(poseStack, x + width / 2, y, -width / 2, v, width / 2, height);
        }
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        int textColor = getFGColor();
        textColor |= Mth.ceil(alpha * 255F) << 24;
        drawCenteredString(
                poseStack, font, getMessage(), x + width / 2, y + (height - 8) / 2, textColor);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return visible && mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
    }

    public boolean isOn(){
        return this.isOn;
    }
    public void setOn(){
        this.isOn = true;
    }
    public void setOff(){
        this.isOn = false;
    }
}
