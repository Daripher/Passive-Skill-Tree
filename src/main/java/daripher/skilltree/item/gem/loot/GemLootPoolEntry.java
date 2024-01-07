package daripher.skilltree.item.gem.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.compat.apotheosis.ApotheosisCompatibility;
import daripher.skilltree.data.reloader.GemTypesReloader;
import java.util.function.Consumer;

import daripher.skilltree.item.gem.GemItem;
import daripher.skilltree.item.gem.GemType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jetbrains.annotations.NotNull;

public class GemLootPoolEntry extends LootPoolSingletonContainer {
  public static final Serializer SERIALIZER = new Serializer();
  public static final LootPoolEntryType TYPE = new LootPoolEntryType(SERIALIZER);
  private final ResourceLocation gemTypeId;

  public GemLootPoolEntry(
      ResourceLocation gemTypeId,
      int weight,
      int quality,
      LootItemCondition[] conditions,
      LootItemFunction[] functions) {
    super(weight, quality, conditions, functions);
    this.gemTypeId = gemTypeId;
  }

  @Override
  protected void createItemStack(
      @NotNull Consumer<ItemStack> consumer, @NotNull LootContext context) {
    if (SkillTreeMod.apotheosisEnabled()) {
      ApotheosisCompatibility.INSTANCE.createGemStack(consumer, context, gemTypeId);
      return;
    }
    GemType gemType = GemTypesReloader.getGemTypeById(gemTypeId);
    consumer.accept(GemItem.getDefaultGemStack(gemType));
  }

  @Override
  public @NotNull LootPoolEntryType getType() {
    return TYPE;
  }

  public static class Builder extends LootPoolSingletonContainer.Builder<GemLootPoolEntry.Builder> {
    private final ResourceLocation gemTypeId;

    public Builder(ResourceLocation gemTypeId) {
      this.gemTypeId = gemTypeId;
    }

    @Override
    protected @NotNull Builder getThis() {
      return this;
    }

    @Override
    public @NotNull LootPoolEntryContainer build() {
      return new GemLootPoolEntry(
          gemTypeId,
          GemLootHandler.getGemLootWeight(gemTypeId),
          GemLootHandler.getGemLootQuality(gemTypeId),
          getConditions(),
          getFunctions());
    }
  }

  public static class Serializer extends LootPoolSingletonContainer.Serializer<GemLootPoolEntry> {

    @Override
    protected @NotNull GemLootPoolEntry deserialize(
        @NotNull JsonObject json,
        @NotNull JsonDeserializationContext ctx,
        int weight,
        int quality,
        LootItemCondition @NotNull [] conditions,
        LootItemFunction @NotNull [] functions) {
      ResourceLocation gemTypeId = new ResourceLocation(json.get("gem").getAsString());
      return new GemLootPoolEntry(gemTypeId, weight, quality, conditions, functions);
    }

    @Override
    public void serializeCustom(
        JsonObject json, GemLootPoolEntry entry, @NotNull JsonSerializationContext ctx) {
      json.addProperty("gem", entry.gemTypeId.toString());
      super.serializeCustom(json, entry, ctx);
    }
  }
}
