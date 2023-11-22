package daripher.skilltree.skill.bonus.player;

import com.google.gson.*;
import daripher.skilltree.client.screen.SkillTreeEditor;
import daripher.skilltree.init.PSTSkillBonuses;
import daripher.skilltree.skill.bonus.SkillBonus;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;

public record RecipeUnlockBonus(ResourceLocation recipeId)
    implements SkillBonus<RecipeUnlockBonus> {
  @Override
  public SkillBonus.Serializer getSerializer() {
    return PSTSkillBonuses.RECIPE_UNLOCK.get();
  }

  @Override
  public SkillBonus<RecipeUnlockBonus> copy() {
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
        Component.translatable(recipeDescriptionId).withStyle(Style.EMPTY.withColor(0x7AB3E2));
    return Component.translatable(getDescriptionId(), recipeDescription)
        .withStyle(Style.EMPTY.withColor(0x7B7BE5));
  }

  @Override
  public void addEditorWidgets(SkillTreeEditor editor, int row) {
    // TODO
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
  }
}
