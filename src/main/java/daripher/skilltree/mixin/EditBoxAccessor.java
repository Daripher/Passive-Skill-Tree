package daripher.skilltree.mixin;

import java.util.function.BiFunction;
import javax.annotation.Nullable;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EditBox.class)
public interface EditBoxAccessor {
  @Accessor
  @Nullable
  String getSuggestion();

  @Accessor
  int getDisplayPos();

  @Accessor
  int getHighlightPos();

  @Accessor
  int getFrame();

  @Accessor
  int getMaxLength();

  @Accessor
  BiFunction<String, Integer, FormattedCharSequence> getFormatter();

  @Invoker
  void invokeRenderHighlight(GuiGraphics graphics, int startX, int startY, int endX, int endY);
}
