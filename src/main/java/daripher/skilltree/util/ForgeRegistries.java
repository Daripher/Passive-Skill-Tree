package daripher.skilltree.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import net.neoforged.neoforge.registries.callback.RegistryCallback;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.neoforged.neoforge.registries.IRegistryExtension;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.registries.BuiltInRegistries;
import com.mojang.serialization.Lifecycle;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.StatType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;

public final class ForgeRegistries {
  public static final ForgeRegistry<Item> ITEMS = new ForgeRegistry<>(BuiltInRegistries.ITEM);
  public static final ForgeRegistry<Block> BLOCKS = new ForgeRegistry<>(BuiltInRegistries.BLOCK);
  public static final ForgeRegistry<MobEffect> MOB_EFFECTS =
      new ForgeRegistry<>(BuiltInRegistries.MOB_EFFECT);
  public static final ForgeRegistry<Potion> POTIONS = new ForgeRegistry<>(BuiltInRegistries.POTION);
  public static final ForgeRegistry<MenuType<?>> MENU_TYPES =
      new ForgeRegistry<>(BuiltInRegistries.MENU);
  public static final ForgeRegistry<RecipeSerializer<?>> RECIPE_SERIALIZERS =
      new ForgeRegistry<>(BuiltInRegistries.RECIPE_SERIALIZER);
  public static final ForgeRegistry<RecipeType<?>> RECIPE_TYPES =
      new ForgeRegistry<>(BuiltInRegistries.RECIPE_TYPE);
  public static final ForgeRegistry<Attribute> ATTRIBUTES =
      new ForgeRegistry<>(BuiltInRegistries.ATTRIBUTE);
  public static final ForgeRegistry<EntityType<?>> ENTITY_TYPES =
      new ForgeRegistry<>(BuiltInRegistries.ENTITY_TYPE);
  public static final ForgeRegistry<StatType<?>> STAT_TYPES =
      new ForgeRegistry<>(BuiltInRegistries.STAT_TYPE);

  private ForgeRegistries() {}

  public static final class ForgeRegistry<T> implements Registry<T> {
    private final Registry<T> registry;

    public ForgeRegistry(Registry<T> registry) {
      this.registry = registry;
    }

    public T getValue(ResourceLocation id) {
      return registry.get(id);
    }

    public Collection<T> getValues() {
      return registry.stream().toList();
    }

    public Set<Map.Entry<ResourceKey<T>, T>> getEntries() {
      return registry.entrySet();
    }

    public Set<ResourceLocation> getKeys() {
      return registry.keySet();
    }

    public T register(String id, T value) {
      return Registry.register(registry, id, value);
    }

    public T register(ResourceLocation id, T value) {
      return Registry.register(registry, id, value);
    }

    @Override
    public ResourceKey<? extends Registry<T>> key() {
      return registry.key();
    }

    private IRegistryExtension<T> extension() {
      return registry;
    }

    @Override
    public ResourceLocation getKey(T value) {
      return registry.getKey(value);
    }

    @Override
    public Optional<ResourceKey<T>> getResourceKey(T value) {
      return registry.getResourceKey(value);
    }

    @Override
    public int getId(T value) {
      return registry.getId(value);
    }

    @Override
    public T byId(int id) {
      return registry.byId(id);
    }

    @Override
    public int size() {
      return registry.size();
    }

    @Override
    public Iterator<T> iterator() {
      return registry.iterator();
    }

    @Override
    public T get(ResourceKey<T> key) {
      return registry.get(key);
    }

    @Override
    public T get(ResourceLocation id) {
      return registry.get(id);
    }

    @Override
    public Optional<RegistrationInfo> registrationInfo(ResourceKey<T> key) {
      return registry.registrationInfo(key);
    }

    @Override
    public Lifecycle registryLifecycle() {
      return registry.registryLifecycle();
    }

    @Override
    public Optional<Holder.Reference<T>> getAny() {
      return registry.getAny();
    }

    @Override
    public Set<ResourceLocation> keySet() {
      return registry.keySet();
    }

    @Override
    public Set<Map.Entry<ResourceKey<T>, T>> entrySet() {
      return registry.entrySet();
    }

    @Override
    public Set<ResourceKey<T>> registryKeySet() {
      return registry.registryKeySet();
    }

    @Override
    public Optional<Holder.Reference<T>> getRandom(net.minecraft.util.RandomSource random) {
      return registry.getRandom(random);
    }

    @Override
    public boolean containsKey(ResourceLocation id) {
      return registry.containsKey(id);
    }

    @Override
    public boolean containsKey(ResourceKey<T> key) {
      return registry.containsKey(key);
    }

    @Override
    public Registry<T> freeze() {
      return registry.freeze();
    }

    @Override
    public Holder.Reference<T> createIntrusiveHolder(T value) {
      return registry.createIntrusiveHolder(value);
    }

    @Override
    public Optional<Holder.Reference<T>> getHolder(int id) {
      return registry.getHolder(id);
    }

    @Override
    public Optional<Holder.Reference<T>> getHolder(ResourceLocation id) {
      return registry.getHolder(id);
    }

    @Override
    public Optional<Holder.Reference<T>> getHolder(ResourceKey<T> key) {
      return registry.getHolder(key);
    }

    @Override
    public Holder<T> wrapAsHolder(T value) {
      return registry.wrapAsHolder(value);
    }

    @Override
    public Stream<Holder.Reference<T>> holders() {
      return registry.holders();
    }

    @Override
    public Optional<HolderSet.Named<T>> getTag(net.minecraft.tags.TagKey<T> tag) {
      return registry.getTag(tag);
    }

    @Override
    public HolderSet.Named<T> getOrCreateTag(net.minecraft.tags.TagKey<T> tag) {
      return registry.getOrCreateTag(tag);
    }

    @Override
    public Stream<com.mojang.datafixers.util.Pair<net.minecraft.tags.TagKey<T>, HolderSet.Named<T>>>
        getTags() {
      return registry.getTags();
    }

    @Override
    public Stream<net.minecraft.tags.TagKey<T>> getTagNames() {
      return registry.getTagNames();
    }

    @Override
    public void resetTags() {
      registry.resetTags();
    }

    @Override
    public void bindTags(Map<net.minecraft.tags.TagKey<T>, java.util.List<Holder<T>>> tags) {
      registry.bindTags(tags);
    }

    @Override
    public HolderOwner<T> holderOwner() {
      return registry.holderOwner();
    }

    @Override
    public HolderLookup.RegistryLookup<T> asLookup() {
      return registry.asLookup();
    }

    @Override
    public HolderLookup.RegistryLookup<T> asTagAddingLookup() {
      return registry.asTagAddingLookup();
    }

    @Override
    public boolean doesSync() {
      return extension().doesSync();
    }

    @Override
    public int getMaxId() {
      return extension().getMaxId();
    }

    @Override
    public void addCallback(RegistryCallback<T> callback) {
      extension().addCallback(callback);
    }

    @Override
    public void addAlias(ResourceLocation from, ResourceLocation to) {
      extension().addAlias(from, to);
    }

    @Override
    public ResourceLocation resolve(ResourceLocation id) {
      return extension().resolve(id);
    }

    @Override
    public ResourceKey<T> resolve(ResourceKey<T> key) {
      return extension().resolve(key);
    }

    @Override
    public int getId(ResourceKey<T> key) {
      return extension().getId(key);
    }

    @Override
    public int getId(ResourceLocation id) {
      return extension().getId(id);
    }

    @Override
    public boolean containsValue(T value) {
      return extension().containsValue(value);
    }

    @Override
    public <A> A getData(DataMapType<T, A> type, ResourceKey<T> key) {
      return extension().getData(type, key);
    }

    @Override
    public <A> Map<ResourceKey<T>, A> getDataMap(DataMapType<T, A> type) {
      return extension().getDataMap(type);
    }
  }
}
