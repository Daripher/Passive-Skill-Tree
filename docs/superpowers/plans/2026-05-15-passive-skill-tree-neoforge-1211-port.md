# Passive Skill Tree NeoForge 1.21.1 Port Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use `superpowers:subagent-driven-development` (recommended) or `superpowers:executing-plans` to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Port Passive Skill Tree from Minecraft 1.20.1 Forge to Minecraft 1.21.1 NeoForge with full feature parity.

**Architecture:** Treat this as a staged platform migration, not a rewrite. First move the build onto the NeoForge 1.21.1 toolchain, then swap Forge persistence/networking/runtime hooks for NeoForge equivalents, then fix the vanilla 1.21.1 API breaks, and finally refresh compat and generated data so the runtime behavior matches the old mod.

**Tech Stack:** Java 21, NeoForge 1.21.1, NeoGradle / Gradle 9.2.1, Sponge Mixin, NeoForge data attachments, NeoForge payload networking, JUnit 5 for regression tests, Curse Maven dependencies.

---

### Task 1: Move the project onto the NeoForge 1.21.1 toolchain

**Files:**
- Modify `build.gradle`
- Modify `settings.gradle`
- Modify `gradle.properties`
- Modify `gradle/wrapper/gradle-wrapper.properties`
- Create `src/main/resources/META-INF/neoforge.mods.toml`
- Delete `src/main/resources/META-INF/mods.toml`
- Modify `src/main/resources/pack.mcmeta`

- [ ] **Step 1: Replace the Forge build bootstrap with the NeoForge MDK shape**

```gradle
plugins {
    id 'java-library'
    id 'maven-publish'
    id 'net.neoforged.gradle.userdev' version '7.1.27'
    id 'org.spongepowered.mixin' version '0.7-SNAPSHOT'
}

java.toolchain.languageVersion = JavaLanguageVersion.of(21)

dependencies {
    implementation "net.neoforged:neoforge:${neo_version}"
}
```

Set `minecraft_version=1.21.1`, `neo_version=21.1.229`, and `mod_file_name=PassiveSkillTree-1.21.1-BETA` in `gradle.properties`. Update `settings.gradle` to use `maven.neoforged.net/releases`, and update the wrapper to Gradle `9.2.1`.

- [ ] **Step 2: Replace `mods.toml` with NeoForge metadata**

```toml
modLoader="javafml"
loaderVersion="${loader_version_range}"
license="${mod_license}"

[[mods]]
modId="${mod_id}"
version="${mod_version}"
displayName="${mod_name}"
authors="${mod_authors}"
description='''${mod_description}'''

[[dependencies.${mod_id}]]
modId="neoforge"
mandatory=true
versionRange="[${neo_version},)"
ordering="NONE"
side="BOTH"
```

Keep the existing required `minecraft` and `attributefix` dependency blocks, and keep `curios` optional.

- [ ] **Step 3: Update the resource pack metadata**

```json
{
  "pack": {
    "description": { "text": "${mod_id} resources" },
    "pack_format": 34
  }
}
```

Keep the same description expansion, just change the 1.20.1 pack format to the 1.21.1 value.

- [ ] **Step 4: Run the build once to prove the failure moved from the toolchain into source-level API errors**

Run: `bash gradlew compileJava`

Expected: the wrapper and dependency resolution should work; any failure should now be actual source migration errors, not the Java 26 / Gradle 8.1.1 cache failure seen before.

- [ ] **Step 5: Commit**

```bash
git add build.gradle settings.gradle gradle.properties gradle/wrapper/gradle-wrapper.properties src/main/resources/META-INF/neoforge.mods.toml src/main/resources/pack.mcmeta src/main/resources/META-INF/mods.toml
git commit -m "build: bootstrap NeoForge 1.21.1"
```

### Task 2: Lock down player-skill persistence with a small regression test

**Files:**
- Modify `build.gradle`
- Create `src/main/java/daripher/skilltree/capability/skill/PlayerSkillsCodec.java`
- Modify `src/main/java/daripher/skilltree/capability/skill/PlayerSkills.java`
- Create `src/test/java/daripher/skilltree/capability/skill/PlayerSkillsCodecTest.java`

- [ ] **Step 1: Add the failing test first**

```java
package daripher.skilltree.capability.skill;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import org.junit.jupiter.api.Test;

class PlayerSkillsCodecTest {
  @Test
  void staleTreeVersion_refundsOnePointPerStoredSkill_andMarksReset() {
    CompoundTag tag = new CompoundTag();
    tag.putUUID("TreeVersion", UUID.fromString("00000000-0000-0000-0000-000000000000"));
    tag.putInt("Points", 3);
    ListTag skills = new ListTag();
    skills.add(StringTag.valueOf("skilltree:hunter_1"));
    skills.add(StringTag.valueOf("skilltree:hunter_2"));
    tag.put("Skills", skills);

    PlayerSkills state = new PlayerSkills();
    PlayerSkillsCodec.read(state, tag);

    assertEquals(5, state.getSkillPoints());
    assertTrue(state.isTreeReset());
    assertTrue(state.getPlayerSkills().isEmpty());
  }
}
```

- [ ] **Step 2: Run the test and watch it fail for the right reason**

Run: `bash gradlew test --tests daripher.skilltree.capability.skill.PlayerSkillsCodecTest -v`

Expected: the test should fail because `PlayerSkillsCodec` does not exist yet, or because the codec methods are not wired yet. Do not accept a green run here.

- [ ] **Step 3: Extract the pure persistence logic into `PlayerSkillsCodec`**

```java
public final class PlayerSkillsCodec {
  private PlayerSkillsCodec() {}

  public static CompoundTag write(PlayerSkills skills) { /* serialize points, reset flag, tree version, and learned skills */ }

  public static void read(PlayerSkills skills, CompoundTag tag) { /* preserve the current tree-version invalidation behavior */ }
}
```

Make `PlayerSkills` delegate to this helper so the persistence rules stay in one place and can be tested without touching NeoForge runtime code.

- [ ] **Step 4: Re-run the test until it passes cleanly**

Run: `bash gradlew test --tests daripher.skilltree.capability.skill.PlayerSkillsCodecTest -v`

Expected: PASS with no new warnings from the test harness.

- [ ] **Step 5: Commit**

```bash
git add build.gradle src/main/java/daripher/skilltree/capability/skill/PlayerSkillsCodec.java src/main/java/daripher/skilltree/capability/skill/PlayerSkills.java src/test/java/daripher/skilltree/capability/skill/PlayerSkillsCodecTest.java
git commit -m "test: lock down player skill persistence"
```

### Task 3: Replace the player capability with a NeoForge attachment

**Files:**
- Create `src/main/java/daripher/skilltree/init/PSTAttachments.java`
- Modify `src/main/java/daripher/skilltree/SkillTreeMod.java`
- Modify `src/main/java/daripher/skilltree/capability/skill/PlayerSkillsProvider.java`
- Modify all call sites that still read through `PlayerSkillsProvider` if the helper method names change

- [ ] **Step 1: Register the attachment type**

```java
public final class PSTAttachments {
  public static final DeferredRegister<AttachmentType<?>> REGISTRY =
      DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, SkillTreeMod.MOD_ID);

  public static final Supplier<AttachmentType<PlayerSkills>> PLAYER_SKILLS =
      REGISTRY.register(
          "player_skills",
          () -> AttachmentType.serializable(PlayerSkills::new).copyOnDeath().build());

  private PSTAttachments() {}
}
```

Register this deferred register from `SkillTreeMod` on the mod event bus next to the existing item, recipe, menu, and loot registries.

- [ ] **Step 2: Rework `PlayerSkillsProvider` into attachment-backed access helpers**

Keep the public `get(Player)` / `hasSkills(Player)` helpers, but make them read from the attachment instead of `CapabilityManager`, `LazyOptional`, or `ICapabilitySerializable`.

```java
public static @NotNull IPlayerSkills get(Player player) {
  return player.getData(PSTAttachments.PLAYER_SKILLS.get());
}
```

Keep the existing login, clone, and join event hooks, but make them use attachment-backed data and preserve the current tree-reset message and bonus reapplication flow.

- [ ] **Step 3: Re-run a build after removing the capability APIs**

Run: `bash gradlew compileJava`

Expected: no remaining references to `CapabilityManager`, `CapabilityToken`, `LazyOptional`, `ICapabilitySerializable`, or `AttachCapabilitiesEvent` in the player-skill storage path.

- [ ] **Step 4: Commit**

```bash
git add src/main/java/daripher/skilltree/init/PSTAttachments.java src/main/java/daripher/skilltree/SkillTreeMod.java src/main/java/daripher/skilltree/capability/skill/PlayerSkillsProvider.java
git commit -m "refactor: move player skills to attachments"
```

### Task 4: Convert the network layer to NeoForge payloads

**Files:**
- Modify `src/main/java/daripher/skilltree/network/NetworkDispatcher.java`
- Modify `src/main/java/daripher/skilltree/network/message/SyncServerDataMessage.java`
- Modify `src/main/java/daripher/skilltree/network/message/SyncPlayerSkillsMessage.java`
- Modify `src/main/java/daripher/skilltree/network/message/LearnSkillMessage.java`
- Modify `src/main/java/daripher/skilltree/network/message/GainSkillPointMessage.java`
- Modify `src/main/java/daripher/skilltree/network/NetworkHelper.java`
- Modify `src/main/java/daripher/skilltree/client/widget/SkillTreeWidgets.java`
- Modify `src/main/java/daripher/skilltree/item/AmnesiaScrollItem.java`
- Modify `src/main/java/daripher/skilltree/item/WisdomScrollItem.java`
- Modify `src/main/java/daripher/skilltree/command/PSTCommands.java`

- [ ] **Step 1: Replace `SimpleChannel` registration with payload registration**

```java
@EventBusSubscriber(modid = SkillTreeMod.MOD_ID, bus = Bus.MOD)
public final class NetworkDispatcher {
  @SubscribeEvent
  public static void register(final RegisterPayloadHandlersEvent event) {
    PayloadRegistrar registrar = event.registrar("1");
    registrar.playToClient(SyncServerDataMessage.TYPE, SyncServerDataMessage.STREAM_CODEC, SyncServerDataMessage::handle);
    registrar.playToClient(SyncPlayerSkillsMessage.TYPE, SyncPlayerSkillsMessage.STREAM_CODEC, SyncPlayerSkillsMessage::handle);
    registrar.playToServer(LearnSkillMessage.TYPE, LearnSkillMessage.STREAM_CODEC, LearnSkillMessage::handle);
    registrar.playToServer(GainSkillPointMessage.TYPE, GainSkillPointMessage.STREAM_CODEC, GainSkillPointMessage::handle);
  }
}
```

Keep the current message names if that makes the call-site migration smaller, but make each message a `CustomPacketPayload`.

- [ ] **Step 2: Convert the four message classes to payload records or payload objects**

Use the existing `NetworkHelper` methods for the custom data fields so the wire format stays stable. A minimal payload shape looks like this:

```java
public record LearnSkillMessage(ResourceLocation skillId) implements CustomPacketPayload {
  public static final Type<LearnSkillMessage> TYPE =
      new Type<>(ResourceLocation.fromNamespaceAndPath(SkillTreeMod.MOD_ID, "learn_skill"));
  public static final StreamCodec<FriendlyByteBuf, LearnSkillMessage> STREAM_CODEC = /* wrap existing encode/decode */;

  @Override
  public Type<? extends CustomPacketPayload> type() { return TYPE; }

  public static void handle(LearnSkillMessage message, IPayloadContext context) { /* server-side validation */ }
}
```

Do the same for the sync payloads and the gain-skill-point request.

- [ ] **Step 3: Update every send site to the NeoForge `PacketDistributor` helpers**

Replace:

```java
NetworkDispatcher.network_channel.sendToServer(new LearnSkillMessage(skill));
```

with the NeoForge payload send helper:

```java
PacketDistributor.sendToServer(new LearnSkillMessage(skill));
```

Replace player-targeted sends with `PacketDistributor.sendToPlayer(player, new SyncPlayerSkillsMessage(player));`.

- [ ] **Step 4: Smoke test the new transport on both sides**

Run:

```bash
bash gradlew runClient
bash gradlew runServer
```

Expected: the client can still open the skill UI and the dedicated server starts without network registration or payload codec errors.

- [ ] **Step 5: Commit**

```bash
git add src/main/java/daripher/skilltree/network/NetworkDispatcher.java src/main/java/daripher/skilltree/network/message/SyncServerDataMessage.java src/main/java/daripher/skilltree/network/message/SyncPlayerSkillsMessage.java src/main/java/daripher/skilltree/network/message/LearnSkillMessage.java src/main/java/daripher/skilltree/network/message/GainSkillPointMessage.java src/main/java/daripher/skilltree/network/NetworkHelper.java src/main/java/daripher/skilltree/client/widget/SkillTreeWidgets.java src/main/java/daripher/skilltree/item/AmnesiaScrollItem.java src/main/java/daripher/skilltree/item/WisdomScrollItem.java src/main/java/daripher/skilltree/command/PSTCommands.java
git commit -m "refactor: move networking to NeoForge payloads"
```

### Task 5: Port the server-side 1.21.1 API breaks

**Files:**
- Modify `src/main/java/daripher/skilltree/data/serializers/SerializationHelper.java`
- Modify `src/main/java/daripher/skilltree/init/PSTTags.java`
- Modify `src/main/java/daripher/skilltree/init/PSTBlocks.java`
- Modify `src/main/java/daripher/skilltree/init/PSTItems.java`
- Modify `src/main/java/daripher/skilltree/init/PSTRecipeSerializers.java`
- Modify `src/main/java/daripher/skilltree/init/PSTRecipeTypes.java`
- Modify `src/main/java/daripher/skilltree/init/PSTLootModifiers.java`
- Modify `src/main/java/daripher/skilltree/init/PSTDamageTypes.java`
- Modify `src/main/java/daripher/skilltree/init/PSTMobEffects.java`
- Modify `src/main/java/daripher/skilltree/init/PSTPotions.java`
- Modify `src/main/java/daripher/skilltree/recipe/**`
- Modify `src/main/java/daripher/skilltree/skill/**` server-side bonus, requirement, and predicate classes
- Modify `src/main/java/daripher/skilltree/data/reloader/**`
- Modify `src/main/java/daripher/skilltree/event/PSTEvents.java`
- Modify `src/main/java/daripher/skilltree/command/PSTCommands.java`
- Modify `src/main/java/daripher/skilltree/entity/player/PlayerHelper.java`

- [ ] **Step 1: Replace old Forge registry and resource-location calls with the 1.21.1 equivalents**

Use `BuiltInRegistries` / NeoForge registries where the code currently hard-depends on `ForgeRegistries`, and convert string-based `new ResourceLocation(...)` calls to `ResourceLocation.fromNamespaceAndPath(...)` where the 1.21.1 API prefers it.

- [ ] **Step 2: Fix the attribute and recipe serialization surfaces**

`SerializationHelper`, the recipe builders, and the skill bonus serializers should preserve the current wire/data format unless 1.21.1 forces a new shape. If the attribute modifier ID format has to change, keep the old UUID/name fields readable so existing skill JSON still loads.

- [ ] **Step 3: Resolve the event/package mismatches that show up during compile**

Fix the imports and method signatures for the event classes touched by:
- grindstone logic
- reload listeners
- anvil logic
- tooltip logic
- movement input logic
- living damage / heal / attack logic

- [ ] **Step 4: Re-run compilation**

Run: `bash gradlew compileJava`

Expected: this should now fail only on remaining client/mixin/compat issues, not on the core server/data layer.

- [ ] **Step 5: Commit**

```bash
git add src/main/java/daripher/skilltree/data/serializers/SerializationHelper.java src/main/java/daripher/skilltree/init/PSTTags.java src/main/java/daripher/skilltree/init/PSTBlocks.java src/main/java/daripher/skilltree/init/PSTItems.java src/main/java/daripher/skilltree/init/PSTRecipeSerializers.java src/main/java/daripher/skilltree/init/PSTRecipeTypes.java src/main/java/daripher/skilltree/init/PSTLootModifiers.java src/main/java/daripher/skilltree/init/PSTDamageTypes.java src/main/java/daripher/skilltree/init/PSTMobEffects.java src/main/java/daripher/skilltree/init/PSTPotions.java src/main/java/daripher/skilltree/recipe src/main/java/daripher/skilltree/skill src/main/java/daripher/skilltree/data/reloader src/main/java/daripher/skilltree/event/PSTEvents.java src/main/java/daripher/skilltree/command/PSTCommands.java src/main/java/daripher/skilltree/entity/player/PlayerHelper.java
git commit -m "refactor: port server gameplay APIs to 1.21.1"
```

### Task 6: Port the client, GUI, and mixin surface

**Files:**
- Modify `src/main/java/daripher/skilltree/client/init/PSTKeybinds.java`
- Modify `src/main/java/daripher/skilltree/init/PSTMenuTypes.java`
- Modify `src/main/java/daripher/skilltree/client/screen/**`
- Modify `src/main/java/daripher/skilltree/client/widget/**`
- Modify `src/main/java/daripher/skilltree/client/tooltip/TooltipHelper.java`
- Modify `src/main/java/daripher/skilltree/mixin/**`
- Modify `src/main/resources/skilltree.mixins.json`

- [ ] **Step 1: Replace the old client menu registration path with `RegisterMenuScreensEvent`**

```java
@SubscribeEvent
public static void registerScreens(RegisterMenuScreensEvent event) {
  event.register(ARTISAN_WORKBENCH.get(), WorkbenchScreen::new);
}
```

Move keybind registration to the NeoForge client event bus and keep the screen/widget classes client-only.

- [ ] **Step 2: Update the mixin config for the Java 21 runtime**

```json
{
  "required": true,
  "minVersion": "0.8",
  "compatibilityLevel": "JAVA_21"
}
```

Then fix the accessor and injector signatures in:
- `AbstractArrowAccessor`
- `ClientAdvancementsAccessor`
- `EditBoxAccessor`
- `MobEffectInstanceAccessor`
- `ArmorSlotMixin`
- `ItemStackMixin`
- `LivingEntityMixin`
- `MobEffectMixin`
- `PlayerMixin`
- `ThrownPotionMixin`

- [ ] **Step 3: Run the client smoke test and fix whatever still fails to render or register**

Run: `bash gradlew runClient`

Expected: the skill tree UI opens, keybinds register, menus/screens open, and the mixins apply without classloading errors.

- [ ] **Step 4: Commit**

```bash
git add src/main/java/daripher/skilltree/client/init/PSTKeybinds.java src/main/java/daripher/skilltree/init/PSTMenuTypes.java src/main/java/daripher/skilltree/client/screen src/main/java/daripher/skilltree/client/widget src/main/java/daripher/skilltree/client/tooltip/TooltipHelper.java src/main/java/daripher/skilltree/mixin src/main/resources/skilltree.mixins.json
git commit -m "refactor: port client ui and mixins to 1.21.1"
```

### Task 7: Refresh datagen, tag namespaces, and optional compat dependencies

**Files:**
- Modify `src/main/java/daripher/skilltree/data/generation/**`
- Modify `src/main/java/daripher/skilltree/init/PSTTags.java`
- Modify `src/main/java/daripher/skilltree/compat/**`
- Modify `build.gradle`
- Modify `src/main/resources/META-INF/neoforge.mods.toml`
- Replace generated resources under `src/generated/resources/**`

- [ ] **Step 1: Move shared tags and damage tags into the NeoForge namespaces**

```java
public class PSTTags {
  public static class DamageTypes {
    public static final TagKey<DamageType> IS_MAGIC =
        TagKey.create(Registries.DAMAGE_TYPE, new ResourceLocation("neoforge", "is_magic"));
  }

  public static class Items {
    public static final TagKey<Item> JEWELRY =
        ItemTags.create(new ResourceLocation("c", "curios/jewelry"));
    public static final TagKey<Item> MELEE_WEAPON = ItemTags.create(new ResourceLocation("skilltree", "melee_weapon"));
    public static final TagKey<Item> RANGED_WEAPON = ItemTags.create(new ResourceLocation("skilltree", "ranged_weapon"));
    public static final TagKey<Item> LEATHER_ARMOR = ItemTags.create(new ResourceLocation("skilltree", "armors/leather"));
  }
}
```

Update the item and damage tags providers so the generated files land in `data/c/...` and `data/neoforge/...` instead of the old Forge paths.

- [ ] **Step 2: Regenerate the data outputs**

Run: `bash gradlew runData`

Replace the generated outputs under `src/generated/resources/` so these files match the NeoForge namespace layout:
- `data/neoforge/loot_modifiers/global_loot_modifiers.json`
- `data/neoforge/tags/damage_type/is_magic.json`
- `data/c/tags/items/tools.json` and any other shared item tags produced by the providers

- [ ] **Step 3: Re-scope the optional dependency declarations**

In `build.gradle`, keep direct API mods available to the compiler, but move purely runtime companions to the NeoForge 1.21.1 runtime setup:
- `compileOnly` / `localRuntime` for API-backed integrations used in source
- `localRuntime` for optional helpers that are only needed to run the mod in a dev instance
- preserve the existing required/optional split in `neoforge.mods.toml`

Refresh the 1.21.1 NeoForge versions for:
- Curios
- AttributeFix
- Max Health Fix
- Apothic Attributes
- Iron’s Spells and Spellbooks
- JEI
- AppleSkin
- Selene, Placebo, Geckolib, and PlayerAnimator where the build still needs them

- [ ] **Step 4: Run the full verification set**

Run:

```bash
bash gradlew compileJava
bash gradlew test
bash gradlew runClient
bash gradlew runServer
bash gradlew runData
```

Expected: the project builds cleanly, the UI opens, the dedicated server starts, and the generated resources match the new NeoForge namespaces.

- [ ] **Step 5: Commit**

```bash
git add src/main/java/daripher/skilltree/data/generation src/main/java/daripher/skilltree/init/PSTTags.java src/main/java/daripher/skilltree/compat build.gradle src/main/resources/META-INF/neoforge.mods.toml src/generated/resources
git commit -m "refactor: refresh datagen and compat for NeoForge"
```

### Task 8: Final pass and branch completion

**Files:**
- All files touched in the tasks above

- [ ] **Step 1: Do one last clean status check**

Run: `git status --short`

Expected: only the intended port files remain changed.

- [ ] **Step 2: Re-run the highest-risk checks after the last merge of changes**

Run:

```bash
bash gradlew compileJava
bash gradlew test
```

Expected: both pass without reintroducing Forge imports, capability leftovers, or broken tests.

- [ ] **Step 3: Prepare the branch for handoff**

Summarize the final changed file groups and note any remaining follow-up work, if any, before calling the port complete.

