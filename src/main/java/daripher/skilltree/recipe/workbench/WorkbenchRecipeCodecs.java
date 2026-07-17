package daripher.skilltree.recipe.workbench;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.crafting.Ingredient;

final class WorkbenchRecipeCodecs {
  static final Codec<JsonObject> JSON_OBJECT =
      Codec.PASSTHROUGH.flatXmap(
          dynamic -> {
            JsonElement element = dynamic.convert(JsonOps.INSTANCE).getValue();
            if (element.isJsonObject()) {
              return DataResult.success(element.getAsJsonObject());
            }
            return DataResult.error(() -> "Expected JSON object");
          },
          jsonObject -> DataResult.success(new Dynamic<>(JsonOps.INSTANCE, jsonObject)));

  private WorkbenchRecipeCodecs() {}

  static List<IngredientEntry> toEntries(Map<Ingredient, Integer> ingredients) {
    return ingredients.entrySet().stream().map(IngredientEntry::from).toList();
  }

  static Map<Ingredient, Integer> toMap(List<IngredientEntry> ingredients) {
    Map<Ingredient, Integer> map = new HashMap<>();
    ingredients.forEach(entry -> map.put(entry.ingredient(), entry.requiredAmount()));
    return map;
  }

  static void writeIngredientMap(RegistryFriendlyByteBuf buf, Map<Ingredient, Integer> ingredients) {
    buf.writeInt(ingredients.size());
    ingredients.forEach(
        (ingredient, amount) -> {
          Ingredient.CONTENTS_STREAM_CODEC.encode(buf, ingredient);
          buf.writeInt(amount);
        });
  }

  static Map<Ingredient, Integer> readIngredientMap(RegistryFriendlyByteBuf buf) {
    Map<Ingredient, Integer> ingredients = new HashMap<>();
    int ingredientsCount = buf.readInt();
    for (int i = 0; i < ingredientsCount; i++) {
      ingredients.put(Ingredient.CONTENTS_STREAM_CODEC.decode(buf), buf.readInt());
    }
    return ingredients;
  }

  record IngredientEntry(Ingredient ingredient, int requiredAmount) {
    static final Codec<IngredientEntry> CODEC =
        RecordCodecBuilder.create(
            instance ->
                instance
                    .group(
                        Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(IngredientEntry::ingredient),
                        Codec.INT.fieldOf("required_amount").forGetter(IngredientEntry::requiredAmount))
                    .apply(instance, IngredientEntry::new));

    static IngredientEntry from(Map.Entry<Ingredient, Integer> entry) {
      return new IngredientEntry(entry.getKey(), entry.getValue());
    }
  }
}
