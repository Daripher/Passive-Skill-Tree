package daripher.skilltree.compat.ironsspellbooks;

import daripher.skilltree.compat.ironsspellbooks.skill.bonus.GrantSpellSkillBonus;
import daripher.skilltree.compat.ironsspellbooks.skill.bonus.SpellLevelSkillBonus;
import daripher.skilltree.compat.ironsspellbooks.skill.bonus.function.ManaLevelFunction;
import daripher.skilltree.init.PSTFloatFunctions;
import daripher.skilltree.init.PSTSkillBonuses;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.SkillBonusHandler;
import daripher.skilltree.skill.bonus.function.FloatFunction;
import io.redspace.ironsspellbooks.api.events.ModifySpellLevelEvent;
import io.redspace.ironsspellbooks.api.magic.SpellSelectionManager;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.registries.RegistryObject;

import java.util.*;

public enum IronsSpellbooksCompat {
    INSTANCE;

    private static final Map<Player, List<UUID>> PLAYER_SPELLS_MAP = new HashMap<>();
    public static final RegistryObject<SkillBonus.Serializer> GRANT_SPELL_BONUS =
            PSTSkillBonuses.REGISTRY.register("grant_spell", GrantSpellSkillBonus.Serializer::new);
    public static final RegistryObject<SkillBonus.Serializer> SPELL_LEVEL_BONUS =
            PSTSkillBonuses.REGISTRY.register("spell_level", SpellLevelSkillBonus.Serializer::new);
    public static final RegistryObject<FloatFunction.Serializer> MANA_LEVEL_FUNCTION =
            PSTFloatFunctions.REGISTRY.register("mana_level", ManaLevelFunction.Serializer::new);

    public void register() {
        MinecraftForge.EVENT_BUS.addListener(INSTANCE::applyGrantSpellBonus);
        MinecraftForge.EVENT_BUS.addListener(INSTANCE::applySpellLevelBonus);
    }

    public void addPlayerSpell(Player player, UUID uuid) {
        List<UUID> playerSpells = PLAYER_SPELLS_MAP.getOrDefault(player, new ArrayList<>());
        playerSpells.add(uuid);
        PLAYER_SPELLS_MAP.put(player, playerSpells);
    }

    public void removePlayerSpell(Player player, UUID uuid) {
        List<UUID> playerSpells = PLAYER_SPELLS_MAP.getOrDefault(player, new ArrayList<>());
        playerSpells.remove(uuid);
        PLAYER_SPELLS_MAP.put(player, playerSpells);
    }

    public boolean hasPlayerSpell(Player player, UUID uuid) {
        return PLAYER_SPELLS_MAP.getOrDefault(player, List.of()).contains(uuid);
    }

    private void applyGrantSpellBonus(SpellSelectionManager.SpellSelectionEvent event) {
        Player player = event.getEntity();
        List<GrantSpellSkillBonus> skillBonuses = SkillBonusHandler.getSkillBonuses(player, GrantSpellSkillBonus.class);
        for (GrantSpellSkillBonus bonus : skillBonuses) {
            if (!bonus.getPlayerCondition().test(player)) {
                continue;
            }
            int spellCount = event.getManager().getSpellCount();
            event.addSelectionOption(bonus.getSpellData(), "pst_fake_slot", spellCount);
        }
    }

    private void applySpellLevelBonus(ModifySpellLevelEvent event) {
        if (!(event.getEntity() instanceof Player player))  {
            return;
        }
        List<SpellLevelSkillBonus> skillBonuses = SkillBonusHandler.getSkillBonuses(player, SpellLevelSkillBonus.class);
        for (SpellLevelSkillBonus bonus : skillBonuses) {
            if (!bonus.getSpellId().toString().equals(event.getSpell().getSpellId())) {
                continue;
            }
            event.addLevels(bonus.getBonusLevels(player));
        }
    }
}
