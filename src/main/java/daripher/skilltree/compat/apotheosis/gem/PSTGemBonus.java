package daripher.skilltree.compat.apotheosis.gem;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.data.serializers.SerializationHelper;
import daripher.skilltree.skill.bonus.item.ItemBonus;
import daripher.skilltree.skill.bonus.item.ItemSkillBonus;
import daripher.skilltree.skill.bonus.player.AttributeBonus;
import dev.shadowsoffire.apotheosis.adventure.affix.socket.gem.GemClass;
import dev.shadowsoffire.apotheosis.adventure.affix.socket.gem.GemItem;
import dev.shadowsoffire.apotheosis.adventure.affix.socket.gem.bonus.GemBonus;
import dev.shadowsoffire.apotheosis.adventure.loot.LootRarity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class PSTGemBonus extends GemBonus {
  public static Codec<ItemBonus<?>> BONUS_CODEC =
      new Codec<>() {
        @Override
        public <T> DataResult<Pair<ItemBonus<?>, T>> decode(DynamicOps<T> ops, T input) {
          JsonObject json = ops.convertTo(JsonOps.INSTANCE, input).getAsJsonObject();
          ItemBonus<?> bonus = SerializationHelper.deserializeItemBonus(json);
          return DataResult.success(Pair.of(bonus, input));
        }

        @Override
        public <T> DataResult<T> encode(ItemBonus<?> input, DynamicOps<T> ops, T prefix) {
          JsonObject json = new JsonObject();
          SerializationHelper.serializeItemBonus(json, input);
          return DataResult.success(JsonOps.INSTANCE.convertTo(ops, json));
        }
      };
  public static Codec<PSTGemBonus> CODEC =
      RecordCodecBuilder.create(
          i ->
              i.group(gemClass(), BONUS_CODEC.fieldOf("bonus").forGetter(b -> b.bonus))
                  .apply(i, PSTGemBonus::new));

  private ItemBonus<?> bonus;

  public PSTGemBonus(GemClass gemClass, ItemBonus<?> bonus) {
    super(new ResourceLocation(SkillTreeMod.MOD_ID, "gem_bonus"), gemClass);
    this.bonus = bonus;
  }

  public PSTGemBonus copy() {
    return new PSTGemBonus(gemClass, bonus.copy());
  }

  public PSTGemBonus multiply(float multiplier) {
    bonus = bonus.copy().multiply(multiplier);
    return this;
  }

  public ItemBonus<?> getBonus(ItemStack gemStack) {
    float gemPower = 1f;
    CompoundTag tag = gemStack.getOrCreateTag();
    if (tag.contains("gem_power")) {
      gemPower = tag.getFloat("gem_power");
    }
    ItemBonus<?> bonus = this.bonus.copy().multiply(gemPower);
    if (bonus instanceof ItemSkillBonus aBonus
        && aBonus.getBonus() instanceof AttributeBonus atBonus) {
      atBonus.setUUID(GemItem.getUUIDs(gemStack).get(0));
    }
    return bonus;
  }

  @Override
  public GemBonus validate() {
    Preconditions.checkNotNull(bonus, "Invalid PSTGemBonus with null bonus");
    return this;
  }

  @Override
  public boolean supports(LootRarity lootRarity) {
    return true;
  }

  @Override
  public int getNumberOfUUIDs() {
    return 1;
  }

  @Override
  public Component getSocketBonusTooltip(ItemStack gemStack, LootRarity lootRarity) {
    float gemPower = 1f;
    CompoundTag tag = gemStack.getOrCreateTag();
    if (tag.contains("gem_power")) {
      gemPower = tag.getFloat("gem_power");
    }
    ItemBonus<?> bonus = this.bonus.copy().multiply(gemPower);
    return bonus.getTooltip().withStyle(TooltipHelper.getSkillBonusStyle(bonus.isPositive()));
  }

  @Override
  public Codec<? extends GemBonus> getCodec() {
    return CODEC;
  }
}
