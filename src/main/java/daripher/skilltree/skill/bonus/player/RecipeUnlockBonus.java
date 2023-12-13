package daripher.skilltree.skill.bonus.player;

import com.google.gson.*;
import daripher.skilltree.client.screen.SkillTreeEditorScreen;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.init.PSTSkillBonuses;
import daripher.skilltree.skill.bonus.SkillBonus;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

public final class RecipeUnlockBonus implements SkillBonus<RecipeUnlockBonus> {
  private ResourceLocation recipeId;

  public RecipeUnlockBonus(ResourceLocation recipeId) {
    this.recipeId = recipeId;
  }

  @Override
  public SkillBonus.Serializer getSerializer() {
    return PSTSkillBonuses.RECIPE_UNLOCK.get();
  }

  @Override
  public RecipeUnlockBonus copy() {
    return new RecipeUnlockBonus(recipeId);
  }

  @Override
  public RecipeUnlockBonus multiply(double multiplier) {
    return this;
  }

  @Override
  public boolean canMerge(SkillBonus<?> other) {
    return false;
  }

  @Override
  public boolean sameBonus(SkillBonus<?> other) {
    if (!(other instanceof RecipeUnlockBonus otherBonus)) return false;
    return otherBonus.recipeId.equals(this.recipeId);
  }

  @Override
  public SkillBonus<RecipeUnlockBonus> merge(SkillBonus<?> other) {
    throw new UnsupportedOperationException();
  }

  @Override
  public MutableComponent getTooltip() {
    String recipeDescriptionId =
        "recipe.%s.%s".formatted(recipeId.getNamespace(), recipeId.getPath());
    Component recipeDescription =
        Component.translatable(recipeDescriptionId)
            .withStyle(TooltipHelper.getItemBonusStyle(isPositive()));
    return Component.translatable(getDescriptionId(), recipeDescription)
        .withStyle(TooltipHelper.getSkillBonusStyle(isPositive()));
  }

  @Override
  public boolean isPositive() {
    return true;
  }

  @Override
  public void addEditorWidgets(
      SkillTreeEditorScreen editor, int row, Consumer<RecipeUnlockBonus> consumer) {
    editor.addLabel(0, 0, "Recipe ID", ChatFormatting.GOLD);
    editor.shiftWidgets(0, 19);
    editor
        .addTextField(0, 0, 200, 14, recipeId.toString())
        .setSoftFilter(ResourceLocation::isValidResourceLocation)
        .setResponder(
            s -> {
              setRecipeId(new ResourceLocation(s));
              consumer.accept(this.copy());
            });
  }

  public void setRecipeId(ResourceLocation recipeId) {
    this.recipeId = recipeId;
  }

  public ResourceLocation getRecipeId() {
    return recipeId;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) return true;
    if (obj == null || obj.getClass() != this.getClass()) return false;
    RecipeUnlockBonus that = (RecipeUnlockBonus) obj;
    return Objects.equals(this.recipeId, that.recipeId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(recipeId);
  }

  public static class Serializer implements SkillBonus.Serializer {
    @Override
    public RecipeUnlockBonus deserialize(JsonObject json) throws JsonParseException {
      String recipeId = json.get("recipe_id").getAsString();
      return new RecipeUnlockBonus(new ResourceLocation(recipeId));
    }

    @Override
    public void serialize(JsonObject json, SkillBonus<?> bonus) {
      if (!(bonus instanceof RecipeUnlockBonus commandBonus)) {
        throw new IllegalArgumentException();
      }
      json.addProperty("recipe_id", commandBonus.recipeId.toString());
    }

    @Override
    public RecipeUnlockBonus deserialize(CompoundTag tag) {
      String recipeId = tag.getString("recipe_id");
      return new RecipeUnlockBonus(new ResourceLocation(recipeId));
    }

    @Override
    public CompoundTag serialize(SkillBonus<?> bonus) {
      if (!(bonus instanceof RecipeUnlockBonus commandBonus)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      tag.putString("recipe_id", commandBonus.recipeId.toString());
      return tag;
    }

    @Override
    public RecipeUnlockBonus deserialize(FriendlyByteBuf buf) {
      return new RecipeUnlockBonus(new ResourceLocation(buf.readUtf()));
    }

    @Override
    public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
      if (!(bonus instanceof RecipeUnlockBonus commandBonus)) {
        throw new IllegalArgumentException();
      }
      buf.writeUtf(commandBonus.recipeId.toString());
    }

    @Override
    public SkillBonus<?> createDefaultInstance() {
      return new RecipeUnlockBonus(new ResourceLocation("skilltree:weapon_poisoning"));
    }
  }
}
