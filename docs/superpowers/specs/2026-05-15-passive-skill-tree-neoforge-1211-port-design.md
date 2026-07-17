# Passive Skill Tree NeoForge 1.21.1 Port Design

## Summary
Port Passive Skill Tree from Minecraft 1.20.1 Forge to Minecraft 1.21.1 NeoForge while preserving full feature parity.

The mod should keep the current gameplay surface intact: skill trees, point progression, player skill persistence, skill UI, editor UI, data-driven skills/recipes/loot modifiers, commands, and optional compat integrations.

## Goals
- Build and run on Minecraft 1.21.1 NeoForge.
- Preserve core gameplay and save data behavior.
- Preserve optional compat support for the modded ecosystem already used by the project.
- Keep existing JSON/data-driven content usable with minimal disruption.

## Non-Goals
- No gameplay redesign.
- No content rebalance unless a 1.21.1 API break forces a targeted fix.
- No multi-loader abstraction work.

## Migration Strategy
Use a staged full-parity port:
1. Update the build and NeoForge toolchain.
2. Migrate Forge platform APIs to NeoForge equivalents.
3. Replace player capability storage with NeoForge attachment-based storage.
4. Replace SimpleChannel networking with NeoForge payload networking.
5. Update vanilla 1.21.1 API breakpoints and mixin targets.
6. Restore optional compat modules and verify they still load.

This keeps the scope full-featured, but avoids mixing every classpath and runtime break into one unreadable failure.

## Platform Changes
- Move from ForgeGradle to the NeoForge userdev/NeoGradle setup used by 1.21.1 MDKs.
- Target Java 21.
- Update the wrapper to a NeoForge-compatible Gradle version.
- Replace `META-INF/mods.toml` with `META-INF/neoforge.mods.toml`.
- Update metadata properties and dependency declarations for NeoForge 1.21.1.

## Core Runtime Changes

### Player skill state
Replace the Forge player capability provider with a NeoForge entity attachment for `PlayerSkills`.

Why:
- `PlayerSkills` already serializes to NBT.
- The data is persistent per-player state and maps cleanly to an attachment.
- Player respawn handling can stay behaviorally identical by copying or restoring the saved skill state on clone.

Required behavior:
- Persist learned skills, skill points, and tree reset state.
- Preserve the current tree-version invalidation behavior.
- Preserve the current “refund points and mark reset” fallback when the tree version or skill IDs are invalid.

### Networking
Replace the current `SimpleChannel` message stack with NeoForge payload registration.

Required payloads:
- server sync data
- player skill sync
- learn skill request
- gain skill point request

Rules:
- server validates every client-originated request
- payload handlers stay side-safe
- client UI still receives the same sync information it expects today

### Registries and events
Keep the existing `PST*` registration classes where possible, but swap their Forge-specific APIs to NeoForge equivalents.

The following areas need attention:
- mod bootstrap and event bus wiring
- deferred registry usage
- config registration
- reload listeners
- client keybind registration
- loot modifier registration
- recipe, menu, block/item, effect, and tag registration

## 1.21.1 API Breakpoints
Update the code where 1.21.1 vanilla or NeoForge signatures changed:
- `ResourceLocation` construction and parsing usage
- attribute modifier handling
- recipe and menu APIs
- item stack and component/data handling
- event classes and event package names
- mixin targets and accessor names
- client screen/widget bindings

The migration should preserve the current data formats where possible, with adapters only where the version change requires them.

## Compat Modules
Preserve the current optional integrations, but swap them to the 1.21.1 NeoForge versions already available in the ecosystem.

Current compat surface to keep:
- Curios
- AttributeFix / max health handling
- Apothic Attributes
- Iron’s Spells and Spellbooks
- JEI
- AppleSkin
- the current development/runtime support dependencies already present in the build, including Selene, mmmmmmmmmmmm, Placebo, Geckolib, and PlayerAnimator

Dependency policy should mirror the current mod:
- required integrations stay required when the existing metadata says they are required
- optional compat stays optional
- dev/runtime support libs stay present for testing and local launch, but do not become hard dependencies unless the code truly needs them

## Data Flow
1. Server loads skill trees, skills, recipes, loot modifiers, tags, and configs.
2. Player joins and receives persistent skill state from the attachment-backed storage.
3. Server syncs trees and player data to the client.
4. Client sends learn/reset/editor actions through payloads.
5. Server validates the request, mutates saved skill state, applies or removes bonuses, then resyncs.
6. Gameplay listeners and compat hooks apply the active bonuses during combat, crafting, movement, potion, damage, and loot events.

## Error Handling
- Missing optional compat mods should not crash the base mod.
- Missing required mods should fail through mod metadata, not late during gameplay.
- Invalid skill IDs or malformed data should log clearly and fail safe on the server.
- Dedicated server startup must not touch client-only classes.

## Verification
Acceptance criteria:
- project compiles on the NeoForge 1.21.1 toolchain
- client launches and opens the skill tree UI
- dedicated server launches without classloading failures
- learned skills persist across relog and death
- learn/reset/sync requests work over the new payload channel
- optional compat modules compile and load with their 1.21.1 NeoForge dependencies
- datagen outputs remain valid for the existing data model

Recommended check order:
1. `compileJava`
2. client smoke run
3. dedicated server smoke run
4. data generation / resource validation
5. targeted gameplay checks for learn skill, reset tree, and compat-triggered bonuses

## References
- NeoForge getting started: https://docs.neoforged.net/docs/1.21.1/gettingstarted/
- NeoForge mod files: https://docs.neoforged.net/docs/1.21.4/gettingstarted/modfiles/
- NeoForge data attachments: https://docs.neoforged.net/docs/1.21.1/datastorage/attachments/
- NeoForge networking payloads: https://docs.neoforged.net/docs/1.21.1/networking/payload/
- Curios 1.21.1 NeoForge files: https://www.curseforge.com/minecraft/mc-mods/curios/files/all?page=1&version=1.21.1
- AttributeFix 1.21.1 NeoForge files: https://www.curseforge.com/minecraft/mc-mods/attributefix/files/all?page=1&version=1.21.1
- Apothic Attributes 1.21.1 NeoForge files: https://www.curseforge.com/minecraft/mc-mods/apothic-attributes/files/all?page=1&pageSize=20&version=1.21.1
