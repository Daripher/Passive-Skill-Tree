package daripher.skilltree.skill.bonus.player;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.data.SkillTreeEditorData;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.data.serializers.SerializationHelper;
import daripher.skilltree.init.PSTSkillBonuses;
import daripher.skilltree.skill.bonus.SkillBonus;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public final class GrantItemBonus implements SkillBonus<GrantItemBonus> {
    private ResourceLocation itemId;
    private int amount;

    public GrantItemBonus(ResourceLocation itemId, int amount) {
        this.itemId = itemId;
        this.amount = amount;
    }

    @Override
    public void onSkillLearned(ServerPlayer player, boolean firstTime) {
        if (firstTime) {
            Item item = ForgeRegistries.ITEMS.getValue(itemId);
            if (item == null) {
                SkillTreeEditorData.sendChatMessage("Unknown item: " + itemId, ChatFormatting.DARK_RED);
                return;
            }
            int amountLeft = amount;
            while (amountLeft > 64) {
                amountLeft -= 64;
                player.addItem(new ItemStack(item, 64));
            }
            if (amountLeft > 0) {
                player.addItem(new ItemStack(item, amountLeft));
            }
        }
    }

    @Override
    public SkillBonus.Serializer getSerializer() {
        return PSTSkillBonuses.GRANT_ITEM.get();
    }

    @Override
    public GrantItemBonus copy() {
        return new GrantItemBonus(itemId, amount);
    }

    @Override
    public GrantItemBonus multiply(double multiplier) {
        amount = (int) (amount * multiplier);
        return this;
    }

    @Override
    public boolean canMerge(SkillBonus<?> other) {
        if (!(other instanceof GrantItemBonus otherBonus)) {
            return false;
        }
        return Objects.equals(otherBonus.itemId, this.itemId);
    }

    @Override
    public GrantItemBonus merge(SkillBonus<?> other) {
        if (!(other instanceof GrantItemBonus otherBonus)) {
            throw new IllegalArgumentException();
        }
        return new GrantItemBonus(itemId, amount + otherBonus.amount);
    }

    @Override
    public MutableComponent getTooltip() {
        Item item = ForgeRegistries.ITEMS.getValue(itemId);
        if (item == null) {
            return Component.literal("Unknown item: " + itemId).withStyle(ChatFormatting.DARK_RED);
        }
        Style style = TooltipHelper.getSkillBonusStyle(isPositive());
        Component itemDescription = item.getDescription();
        if (amount > 1) {
            String amountDescription = TooltipHelper.formatNumber(amount);
            return Component.translatable(getDescriptionId() + ".amount", amountDescription, itemDescription).withStyle(style);
        } else {
            return Component.translatable(getDescriptionId(), itemDescription).withStyle(style);
        }
    }

    @Override
    public boolean isPositive() {
        return true;
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, int row, Consumer<GrantItemBonus> consumer) {
        editor.addLabel(0, 0, "Amount", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addNumericTextField(0, 0, 90, 14, amount).setNumericFilter(v -> v > 0 && v % 1 == 0)
                .setNumericResponder(value -> selectAmount(consumer, value));
        editor.increaseHeight(19);
        editor.addLabel(0, 0, "Item", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        List<ResourceLocation> items = ForgeRegistries.ITEMS.getEntries().stream().map(Map.Entry::getKey).map(ResourceKey::location)
                .toList();
        editor.addSelectionMenu(0, 0, 200, items).setValue(itemId).setElementNameGetter(id -> Component.literal(id.toString()))
                .setResponder(id -> selectItemId(id, consumer));
        editor.increaseHeight(19);
    }

    private void selectItemId(ResourceLocation id, Consumer<GrantItemBonus> consumer) {
        setItemId(id);
        consumer.accept(this.copy());
    }

    public void setItemId(ResourceLocation itemId) {
        this.itemId = itemId;
    }

    private void selectAmount(Consumer<GrantItemBonus> consumer, Double value) {
        setAmount(value.intValue());
        consumer.accept(this.copy());
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public static class Serializer implements SkillBonus.Serializer {
        @Override
        public GrantItemBonus deserialize(JsonObject json) throws JsonParseException {
            ResourceLocation itemId = ResourceLocation.parse(json.get("item_id").getAsString());
            int amount = SerializationHelper.getElement(json, "amount").getAsInt();
            return new GrantItemBonus(itemId, amount);
        }

        @Override
        public void serialize(JsonObject json, SkillBonus<?> bonus) {
            if (!(bonus instanceof GrantItemBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            json.addProperty("item_id", aBonus.itemId.toString());
            json.addProperty("amount", aBonus.amount);
        }

        @Override
        public GrantItemBonus deserialize(CompoundTag tag) {
            ResourceLocation itemId = ResourceLocation.parse(tag.getString("item_id"));
            int amount = tag.getInt("amount");
            return new GrantItemBonus(itemId, amount);
        }

        @Override
        public CompoundTag serialize(SkillBonus<?> bonus) {
            if (!(bonus instanceof GrantItemBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            CompoundTag tag = new CompoundTag();
            tag.putString("item_id", aBonus.itemId.toString());
            tag.putInt("amount", aBonus.amount);
            return tag;
        }

        @Override
        public GrantItemBonus deserialize(FriendlyByteBuf buf) {
            ResourceLocation itemId = buf.readResourceLocation();
            int duration = buf.readInt();
            return new GrantItemBonus(itemId, duration);
        }

        @Override
        public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
            if (!(bonus instanceof GrantItemBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            buf.writeResourceLocation(aBonus.itemId);
            buf.writeInt(aBonus.amount);
        }

        @Override
        public SkillBonus<?> createDefaultInstance() {
            return new GrantItemBonus(ForgeRegistries.ITEMS.getKey(Items.DIAMOND), 64);
        }
    }
}
