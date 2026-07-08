package daripher.skilltree.skill.bonus.item;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.client.widget.editor.menu.EditorMenu;
import daripher.skilltree.client.widget.editor.menu.bonuses.ItemBonusEditor;
import daripher.skilltree.client.widget.editor.menu.selection.SelectionList;
import daripher.skilltree.client.widget.editor.menu.selection.SelectionMenu;
import daripher.skilltree.client.widget.editor.menu.selection.TextSelectionList;
import daripher.skilltree.init.PSTItemBonuses;
import daripher.skilltree.init.PSTRegistries;
import daripher.skilltree.init.PSTSkillBonuses;
import daripher.skilltree.network.NetworkHelper;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.player.AttributeBonus;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public final class GroupedItemBonus implements ItemBonus<GroupedItemBonus> {
    private final ArrayList<ItemBonus<?>> innerBonuses;

    public GroupedItemBonus(ArrayList<ItemBonus<?>> innerBonuses) {
        this.innerBonuses = innerBonuses;
    }

    @Override
    public boolean canMerge(ItemBonus<?> other) {
        return other instanceof GroupedItemBonus;
    }

    @Override
    public GroupedItemBonus merge(ItemBonus<?> other) {
        if (!(other instanceof GroupedItemBonus otherBonus)) {
            throw new IllegalArgumentException();
        }
        return ItemBonusHandler.mergeGroupedItemBonuses(this, otherBonus);
    }

    @Override
    public GroupedItemBonus copy() {
        return new GroupedItemBonus(new ArrayList<>(innerBonuses.stream().map(ItemBonus::copy).toList()));
    }

    @Override
    public GroupedItemBonus multiply(double multiplier) {
        innerBonuses.forEach(bonus -> bonus.multiply(multiplier));
        return this;
    }

    @Override
    public ItemBonus.Serializer getSerializer() {
        return PSTItemBonuses.ITEM_BONUS_LIST.get();
    }

    @Override
    public void addTooltip(Consumer<MutableComponent> consumer) {
        for (ItemBonus<?> itemBonus : innerBonuses) {
            itemBonus.addTooltip(consumer);
        }
    }

    @Override
    public boolean isPositive() {
        return innerBonuses.stream().anyMatch(ItemBonus::isPositive);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GroupedItemBonus that = (GroupedItemBonus) o;
        return Objects.equals(innerBonuses, that.innerBonuses);
    }

    @Override
    public int hashCode() {
        return Objects.hash(innerBonuses);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public void addEditorWidgets(SkillTreeEditor editor, Consumer<GroupedItemBonus> consumer) {
        ItemBonus<?> defaultBonus = PSTItemBonuses.SKILL_BONUS.get().createDefaultInstance();
        editor.addSelectionMenu(0, 0, 90, defaultBonus).setResponder(itemBonus -> {
            addItemBonus(editor, itemBonus);
            consumer.accept(this);
        }).setMessage(Component.literal("Add"));
        editor.increaseHeight(29);
        for (int i = 0; i < getInnerBonuses().size(); i++) {
            final int bonusIndex = i;
            ItemBonus selectedItemBonus = getInnerBonuses().get(i);
            final AtomicReference<MutableComponent> tooltip = new AtomicReference<>();
            selectedItemBonus.addTooltip(component -> {
                if (tooltip.get() == null) {
                    tooltip.set((MutableComponent) component);
                }
            });
            String message = tooltip.get().getString();
            message = TooltipHelper.getTrimmedString(message, 190);
            editor.addButton(0, 0, 200, 14, message).setPressFunc(button -> {
                ItemBonusEditor itemBonusEditor = new ItemBonusEditor(editor, editor.getSelectedMenu(), bonus -> skillBonusChanged(bonus, bonusIndex, consumer), () -> selectedItemBonus);
                editor.selectMenu(itemBonusEditor);
            });
            editor.increaseHeight(19);
        }
    }

    private void skillBonusChanged(@Nullable ItemBonus<?> itemBonus, int selectedBonusIndex, Consumer<GroupedItemBonus> consumer) {
        if (itemBonus == null) {
            deleteSelectedItemBonuses(selectedBonusIndex);
        } else {
            setItemBonuses(itemBonus, selectedBonusIndex);
        }
        consumer.accept(this.copy());
    }

    private void setItemBonuses(ItemBonus<?> bonus, int selectedBonusIndex) {
        innerBonuses.set(selectedBonusIndex, bonus);
    }

    private void deleteSelectedItemBonuses(int selectedBonusIndex) {
        if (getInnerBonuses().size() > selectedBonusIndex) {
            getInnerBonuses().remove(selectedBonusIndex);
        }
    }

    @SuppressWarnings("rawtypes")
    private void addItemBonus(SkillTreeEditor editor, ItemBonus<?> itemBonus) {
        final EditorMenu previousMenu = editor.getSelectedMenu().previousMenu;
        if (itemBonus instanceof EquipmentBonus equipmentBonus) {
            SelectionList<SkillBonus> skillBonusSelectionList = new TextSelectionList<>(0, 0, 190, 14, PSTSkillBonuses.defaultInstances()).setRows(8)
                    .setNameGetter(bonus -> Component.literal(PSTSkillBonuses.getName(bonus)))
                    .selectElement(equipmentBonus.getSkillBonus());
            editor.selectMenu(new SelectionMenu<>(editor, editor.getSelectedMenu(), skillBonusSelectionList, () -> {
            }).setResponder(skillBonus -> {
                innerBonuses.add(new EquipmentBonus(skillBonus));
                editor.selectMenu(previousMenu);
            }));
            return;
        }
        innerBonuses.add(itemBonus);
        editor.selectMenu(previousMenu);
    }

    public List<? extends ItemBonus<?>> getInnerBonuses() {
        return innerBonuses;
    }

    @Override
    public String toString() {
        return "GroupedItemBonus[" + "innerBonuses=" + innerBonuses + ']';
    }


    public static class Serializer implements ItemBonus.Serializer {
        @Override
        public ItemBonus<?> deserialize(JsonObject json) throws JsonParseException {
            JsonArray innerBonusesJson = json.get("inner_bonuses").getAsJsonArray();
            ArrayList<ItemBonus<?>> innerBonuses = new ArrayList<>();
            for (int i = 0; i < innerBonusesJson.size(); i++) {
                JsonObject innerBonusTag = innerBonusesJson.get(i).getAsJsonObject();
                String serializerIdString = innerBonusTag.get("type").getAsString();
                ResourceLocation serializerId = ResourceLocation.parse(serializerIdString);
                ItemBonus.Serializer serializer = PSTRegistries.ITEM_BONUSES.get().getValue(serializerId);
                Objects.requireNonNull(serializer, "Unknown item bonus: " + serializerId);
                ItemBonus<?> innerBonus = serializer.deserialize(innerBonusTag);
                innerBonuses.add(innerBonus);
            }
            return new GroupedItemBonus(innerBonuses);
        }

        @Override
        public void serialize(JsonObject json, ItemBonus<?> bonus) {
            if (!(bonus instanceof GroupedItemBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            JsonArray innerBonusesJson = new JsonArray();
            for (int i = 0; i < aBonus.innerBonuses.size(); i++) {
                ItemBonus<?> innerBonus = aBonus.innerBonuses.get(i);
                ItemBonus.Serializer serializer = innerBonus.getSerializer();
                ResourceLocation serializerId = PSTRegistries.ITEM_BONUSES.get().getKey(serializer);
                Objects.requireNonNull(serializerId);
                JsonObject innerBonusJson = new JsonObject();
                innerBonusJson.addProperty("type", serializerId.toString());
                serializer.serialize(innerBonusJson, innerBonus);
                innerBonusesJson.add(innerBonusJson);
            }
            json.add("inner_bonuses", innerBonusesJson);
        }

        @Override
        public ItemBonus<?> deserialize(CompoundTag tag) {
            ArrayList<ItemBonus<?>> innerBonuses = new ArrayList<>();
            ListTag innerBonusesTag = tag.getList("inner_bonuses", Tag.TAG_COMPOUND);
            for (Tag value : innerBonusesTag) {
                CompoundTag innerBonusTag = (CompoundTag) value;
                String type = innerBonusTag.getString("type");
                ResourceLocation serializerId = ResourceLocation.parse(type);
                ItemBonus.Serializer serializer = PSTRegistries.ITEM_BONUSES.get().getValue(serializerId);
                Objects.requireNonNull(serializer, "Unknown item bonus: " + serializerId);
                innerBonuses.add(serializer.deserialize(innerBonusTag));
            }
            return new GroupedItemBonus(innerBonuses);
        }

        @Override
        public CompoundTag serialize(ItemBonus<?> bonus) {
            if (!(bonus instanceof GroupedItemBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            CompoundTag tag = new CompoundTag();
            ListTag innerBonusesTag = new ListTag();
            for (int i = 0; i < aBonus.innerBonuses.size(); i++) {
                ItemBonus<?> innerBonus = aBonus.innerBonuses.get(i);
                ItemBonus.Serializer serializer = innerBonus.getSerializer();
                ResourceLocation serializerId = PSTRegistries.ITEM_BONUSES.get().getKey(serializer);
                Objects.requireNonNull(serializerId);
                CompoundTag innerBonusTag = serializer.serialize(innerBonus);
                innerBonusTag.putString("type", serializerId.toString());
                innerBonusesTag.add(innerBonusTag);
            }
            tag.put("inner_bonuses", innerBonusesTag);
            return tag;
        }

        @Override
        public ItemBonus<?> deserialize(FriendlyByteBuf buf) {
            ArrayList<ItemBonus<?>> innerBonuses = new ArrayList<>();
            int size = buf.readInt();
            for (int i = 0; i < size; i++) {
                innerBonuses.add(NetworkHelper.readItemBonus(buf));
            }
            return new GroupedItemBonus(innerBonuses);
        }

        @Override
        public void serialize(FriendlyByteBuf buf, ItemBonus<?> bonus) {
            if (!(bonus instanceof GroupedItemBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            buf.writeInt(aBonus.innerBonuses.size());
            for (int i = 0; i < aBonus.innerBonuses.size(); i++) {
                NetworkHelper.writeItemBonus(buf, aBonus.innerBonuses.get(i));
            }
        }

        @Override
        public ItemBonus<?> createDefaultInstance() {
            AttributeModifier defaultModifier = new AttributeModifier("Default Modifier", 1, AttributeModifier.Operation.ADDITION);
            ItemBonus<?> bonus1 = new EquipmentBonus(new AttributeBonus(Attributes.ARMOR, defaultModifier));
            ItemBonus<?> bonus2 = new EquipmentBonus(new AttributeBonus(Attributes.ARMOR_TOUGHNESS, defaultModifier));
            ArrayList<ItemBonus<?>> bonuses = new ArrayList<>();
            bonuses.add(bonus1);
            bonuses.add(bonus2);
            return new GroupedItemBonus(bonuses);
        }
    }
}
