package daripher.skilltree.data.generation.translation;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.compat.ironsspellbooks.IronsSpellbooksCompat;
import daripher.skilltree.init.*;
import daripher.skilltree.skill.bonus.player.GainedExperienceBonus;
import daripher.skilltree.skill.bonus.player.LootDuplicationBonus;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;

public class PSTRussianTranslationProvider extends PSTTranslationProvider {
    public PSTRussianTranslationProvider(DataGenerator gen) {
        super(gen, SkillTreeMod.MOD_ID, "ru_ru");
    }

    @Override
    protected void addTranslations() {
        // skill trees
        add("skilltree:hunter", "Охотник");
        add("skilltree:alchemist", "Алхимик");
        add("skilltree:cook", "Повар");
        // skills
        addSkill("alchemist", 1, "Алхимик");
        addSkills("alchemist", 2, 26, 29, "Шанс отравления");
        addSkill("alchemist", 32, "Обход иммунитета");
        addSkill("alchemist", 49, "Шанс слабости");
        addSkill("alchemist", 52, "Шанс иссушения");
        addSkill("alchemist", 55, "Шанс тошноты");
        addSkill("alchemist", 58, "Распространяющаяся чума");
        addSkills("alchemist", 5, 8, 11, "Длительность негативных эффектов");
        addSkill("alchemist", 14, "Грубокая инфекция");
        addSkills("alchemist", 22, 25, 34, "Шанс замедления");
        addSkill("alchemist", 37, "Жестокость");
        addSkills("alchemist", 38, 41, 44, "Урон ядом");
        addSkill("alchemist", 60, "Тающая плоть");
        addSkills("alchemist", 3, 27, 30, "Длительность положительных эффектов");
        addSkill("alchemist", 33, "Передозировка");
        addSkills("alchemist", 47, 50, 53, "Лечение зельями");
        addSkill("alchemist", 56, "Регенерация конечностей");
        addSkills("alchemist", 6, 9, 12, "Получаемое лечение");
        addSkill("alchemist", 15, "Имунный ответ");
        addSkills("alchemist", 20, 23, 28, "Максимальное здоровье");
        addSkill("alchemist", 35, "Мутация");
        addSkills("alchemist", 39, 42, 45, "Скорость передвижения");
        addSkill("alchemist", 61, "Адреналин");
        addSkills("alchemist", 4, 17, 18, "Урон снарядами");
        addSkill("alchemist", 19, "Больше пороха!");
        addSkills("alchemist", 48, 51, 54, "Шанс дополнительного снаряда");
        addSkill("alchemist", 57, "Сдвоенные склянки");
        addSkills("alchemist", 7, 10, 13, "Макический урон");
        addSkill("alchemist", 16, "Антимагическое поле");
        addSkills("alchemist", 21, 24, 31, "Скорость снарядов");
        addSkill("alchemist", 36, "Мощный бросок");
        addSkills("alchemist", 40, 43, 46, "Урон огнем");
        addSkill("alchemist", 59, "Испепеление");

        addSkill("cook", 1, "Повар");
        addSkills("cook", 2, 26, 29, "Урон без оружия");
        addSkill("cook", 32, "Тяжелый удар");
        addSkills("cook", 49, 52, 55, "Скорость атаки без оружия");
        addSkill("cook", 58, "Железный кулак");
        addSkills("cook", 5, 8, 11, "Урон атак");
        addSkill("cook", 14, "Остро!");
        addSkills("cook", 22, 25, 34, "Урон в ближнем бою");
        addSkill("cook", 37, "Здоровое тело");
        addSkills("cook", 38, 41, 44, "Шанс критического удара без оружия");
        addSkill("cook", 60, "Живое оружие");
        addSkills("cook", 4, 17, 18, "Максимальное здоровье");
        addSkill("cook", 19, "Здоровая диета");
        addSkills("cook", 48, 51, 54, "Лечение едой");
        addSkill("cook", 57, "Груда мышц");
        addSkills("cook", 7, 10, 13, "Получаемое лечение");
        addSkill("cook", 16, "Ускоренный метаболизм");
        addSkills("cook", 21, 24, 31, "Лечение при ударе");
        addSkill("cook", 36, "Похищение жизни");
        addSkills("cook", 40, 43, 46, "Шанс насыщения");
        addSkill("cook", 59, "Комфорт");
        addSkills("cook", 3, 27, 30, "Скорость перемещения во время еды");
        addSkill("cook", 33, "Перекус");
        addSkills("cook", 47, 50, 53, "Скорость применения еды");
        addSkill("cook", 56, "Амбидекстрия");
        addSkills("cook", 6, 9, 12, "Шанс двойных наград с рыбалки");
        addSkill("cook", 15, "Благословление моря");
        addSkills("cook", 20, 23, 28, "Опыт с рыбалки");
        addSkill("cook", 35, "Опытный рыбак");
        addSkills("cook", 39, 42, 45, "Получаемый урон во время рыбалки");
        addSkill("cook", 61, "Незримый рыбак");

        addSkill("hunter", 1, "Охотник");
        addSkills("hunter", 2, 26, 29, "Прочность кожаной брони");
        addSkill("hunter", 32, "Подгонка брони");
        addSkills("hunter", 49, 52, 55, "Измегание урона");
        addSkill("hunter", 58, "Акробатика");
        addSkills("hunter", 5, 8, 11, "Броня");
        addSkill("hunter", 14, "Дублёная кожа");
        addSkills("hunter", 22, 25, 34, "Скорость перемещения");
        addSkill("hunter", 37, "Неуловимость");
        addSkills("hunter", 38, 41, 44, "Избегание урона снарядов");
        addSkill("hunter", 60, "Двухслойная броня");
        addSkills("hunter", 4, 17, 18, "Скорость снарядов");
        addSkill("hunter", 19, "Пробивающий выстрел");
        addSkills("hunter", 48, 51, 54, "Скорость стрельбы из лука");
        addSkill("hunter", 57, "Скоростная стрельба");
        addSkills("hunter", 7, 10, 13, "Скорость передвижения во время стрельбы");
        addSkill("hunter", 16, "Тренированный лучник");
        addSkills("hunter", 21, 24, 31, "Критический урон снарядов");
        addSkill("hunter", 36, "Утяжеленные стрелы");
        addSkills("hunter", 40, 43, 46, "Урон снарядов");
        addSkill("hunter", 59, "Баллистический эффект");
        addSkills("hunter", 3, 27, 30, "Удвоение наград с существ");
        addSkill("hunter", 33, "Охотничий трофей");
        addSkills("hunter", 47, 50, 53, "Удвоение наград с сундуков");
        addSkill("hunter", 56, "Охота за сокровищами");
        addSkills("hunter", 6, 9, 12, "Шанс вернуть стрелы");
        addSkill("hunter", 15, "Осторожный выстрел");
        addSkills("hunter", 20, 23, 28, "Опыт с существ");
        addSkill("hunter", 35, "Опытный охотник");
        addSkills("hunter", 39, 42, 45, "Удача");
        addSkill("hunter", 61, "Счастливчик");
        // skill bonuses
        add(PSTSkillBonuses.DAMAGE.get(), "Урон");
        add(PSTSkillBonuses.CRIT_DAMAGE.get(), "Множитель критического урона");
        add(PSTSkillBonuses.CRIT_DAMAGE.get(), "damage", "Множитель критического урона %s");
        add(PSTSkillBonuses.CRIT_CHANCE.get(), "Шанс критического удара");
        add(PSTSkillBonuses.CRIT_CHANCE.get(), "damage", "Шанс критического удара %s");
        add(PSTSkillBonuses.BLOCK_BREAK_SPEED.get(), "Скорость добычи блоков");
        add(PSTSkillBonuses.REPAIR_EFFICIENCY.get(), "Ремонтируем%s: %s");
        add(PSTSkillBonuses.REPAIR_EFFICIENCY.get(), "bonus", "Прочности восстановлено");
        add(PSTSkillBonuses.FREE_ENCHANTMENT.get(), "chance", "Шанс зачаровать %s бесплатно");
        add(PSTSkillBonuses.FREE_ENCHANTMENT.get(), "Вы зачаровываете %s бесплатно");
        add(PSTSkillBonuses.JUMP_HEIGHT.get(), "Высота прыжка");
        add(PSTSkillBonuses.INCOMING_HEALING.get(), "Получаемое лечение");
        add(PSTSkillBonuses.LOOT_DUPLICATION.get(), "Шанс получить %s %s");
        add(PSTSkillBonuses.LOOT_DUPLICATION.get(), "multiplier", "+%s%%");
        add(PSTSkillBonuses.LOOT_DUPLICATION.get(), "guaranteed", "Вы всегда получаете %s %s");
        add(PSTSkillBonuses.LOOT_DUPLICATION.get(), "double", "двойные");
        add(PSTSkillBonuses.LOOT_DUPLICATION.get(), "triple", "тройные");
        add(PSTSkillBonuses.GAINED_EXPERIENCE.get(), "Опыт %s");
        add(PSTSkillBonuses.INFLICT_IGNITE.get(), "player", "Вы загораетесь на %s");
        add(PSTSkillBonuses.INFLICT_IGNITE.get(), "player.chance", "Шанс загореться на %s");
        add(PSTSkillBonuses.INFLICT_IGNITE.get(), "enemy", "Вы поджигаете врагов на %s");
        add(PSTSkillBonuses.INFLICT_IGNITE.get(), "enemy.chance", "Шанс поджечь врагов на %s");
        add(PSTSkillBonuses.ARROW_RETRIEVAL.get(), "Шанс вернуть стрелы");
        add(PSTSkillBonuses.HEALTH_RESERVATION.get(), "Удержание здоровья");
        add(PSTSkillBonuses.ALL_ATTRIBUTES.get(), "Все характеристики");
        add(PSTSkillBonuses.INFLICT_EFFECT.get(), "player", "Вы получаете %s%s");
        add(PSTSkillBonuses.INFLICT_EFFECT.get(), "player.chance", "Шанс получить %s%s");
        add(PSTSkillBonuses.INFLICT_EFFECT.get(), "enemy", "Вы накладываете %s%s");
        add(PSTSkillBonuses.INFLICT_EFFECT.get(), "enemy.chance", "Шанс наложить %s%s");
        add(PSTSkillBonuses.INFLICT_EFFECT.get(), "seconds", " на %s секунд");
        add(PSTSkillBonuses.INFLICT_EFFECT.get(), "minutes", " на %s минут");
        add(PSTSkillBonuses.INFLICT_EFFECT.get(), "stacks", "%s, суммируется до %s раз");
        add(PSTSkillBonuses.CANT_USE_ITEM.get(), "Нельзя использовать %s");
        add(PSTSkillBonuses.HEALING.get(), "player", "Вы восстанавливаете %s здоровья");
        add(PSTSkillBonuses.HEALING.get(), "player.chance", "Шанс восстановить %s здоровья");
        add(PSTSkillBonuses.HEALING.get(), "enemy", "Враги восстанавливают %s здоровья");
        add(PSTSkillBonuses.HEALING.get(), "enemy.chance", "Шанс для врагов восстановить %s здоровья");
        add(PSTSkillBonuses.INFLICT_DAMAGE.get(), "player", "Вы получаете %s урона");
        add(PSTSkillBonuses.INFLICT_DAMAGE.get(), "player.chance", "Шанс получить %s урона");
        add(PSTSkillBonuses.INFLICT_DAMAGE.get(), "enemy", "+%s урона наносится %s");
        add(PSTSkillBonuses.INFLICT_DAMAGE.get(), "enemy.chance", "Шанс нанести %s урона %s");
        add(PSTSkillBonuses.CAN_POISON_ANYONE.get(), "Ваши яды действуют на любых врагов");
        add(PSTSkillBonuses.LETHAL_POISON.get(), "Ваши яды летальны");
        add(PSTSkillBonuses.DAMAGE_TAKEN.get(), "Получаемый %s");
        add(PSTSkillBonuses.DAMAGE_AVOIDANCE.get(), "Шанс избежать %s");
        add(PSTSkillBonuses.DAMAGE_CONVERSION.get(), "%s%% всего %s конвертируется в %s");
        add(PSTSkillBonuses.GRANT_ITEM.get(), "Дарует %s при изучении");
        add(PSTSkillBonuses.GRANT_ITEM.get(), "amount", "Дарует %sx %s при изучении");
        add(PSTSkillBonuses.EFFECT_DURATION.get(), "player", "Длительность %s на вас");
        add(PSTSkillBonuses.EFFECT_DURATION.get(), "enemy", "Длительность накладываемых %s");
        add(PSTSkillBonuses.PROJECTILE_DUPLICATION.get(), "chance", "Шанс удвоить выпущенный снаряд");
        add(PSTSkillBonuses.PROJECTILE_DUPLICATION.get(), "amount", "Вы выпускаете %s дополнительных снарядов");
        add(PSTSkillBonuses.PROJECTILE_DUPLICATION.get(), "Вы выпускаете дополнительный снаряд");
        add(PSTSkillBonuses.SELF_SPLASH_IMMUNE.get(), "Ваши взрывные зелья не действуют на вас");
        add(PSTSkillBonuses.PROJECTILE_SPEED.get(), "Скорость снарядов");
        add(PSTSkillBonuses.ITEM_DURABILITY_LOSS_AVOIDANCE.get(), "chance", "Шанс что %s не потеряет прочность при использовании");
        add(PSTSkillBonuses.ITEM_DURABILITY_LOSS_AVOIDANCE.get(), "%s не теряет прочности при использовании");
        add(PSTSkillBonuses.ITEM_USAGE_SPEED.get(), "positive", "Вы используете %s %s%% быстрее");
        add(PSTSkillBonuses.ITEM_USAGE_SPEED.get(), "negative", "Вы используете %s %s%% медленнее");
        add(PSTSkillBonuses.ITEM_USE_MOVEMENT_SPEED.get(), "positive", "Используя %s вы замедляетесь на %s%% слабее");
        add(PSTSkillBonuses.ITEM_USE_MOVEMENT_SPEED.get(), "remove", "Используя %s вы не замедляетесь");
        add(PSTSkillBonuses.ITEM_USE_MOVEMENT_SPEED.get(), "negative", "Используя %s вы замедляетесь на %s%% сильнее");
        add(PSTSkillBonuses.MORE_ITEM_BONUSES.get(), "one", "Вы можете улучшить %s дополнительный раз используя продвинутый рабочий стол");
        add(PSTSkillBonuses.MORE_ITEM_BONUSES.get(), "Вы можете улучшить %s %s дополнительных раза используя продвинутый рабочий стол");
        add(PSTSkillBonuses.RECIPE_UNLOCK.get(), "Открывает рецепт: %s");
        add(PSTSkillBonuses.GAIN_EXPERIENCE.get(), "Вы получаете %s опыта");
        add(PSTSkillBonuses.GAIN_EXPERIENCE.get(), "chance", "Шанс получить %s опыта");
        add(PSTSkillBonuses.VANILLA_RECIPE_UNLOCK.get(), "Вы можете создавать %s на продвинутом рабочем столе");
        add(IronsSpellbooksCompat.GRANT_SPELL_BONUS.get(), "Дарует заклинание %s %s");
        add(IronsSpellbooksCompat.SPELL_LEVEL_BONUS.get(), "к уровню заклинания %s");
        // experience sources
        add(GainedExperienceBonus.ExperienceSource.MOBS.getDescriptionId(), "с Существ");
        add(GainedExperienceBonus.ExperienceSource.ORE.getDescriptionId(), "из Руды");
        add(GainedExperienceBonus.ExperienceSource.FISHING.getDescriptionId(), "за Рыбалку");
        // loot conditions
        add(LootDuplicationBonus.LootType.MOBS.getDescriptionId(), "награды с существ");
        add(LootDuplicationBonus.LootType.FISHING.getDescriptionId(), "награды с рыбалки");
        add(LootDuplicationBonus.LootType.GEMS.getDescriptionId(), "самоцветы из руды");
        add(LootDuplicationBonus.LootType.CHESTS.getDescriptionId(), "награды в сундуках");
        add(LootDuplicationBonus.LootType.ORE.getDescriptionId(), "награды из руды");
        add(LootDuplicationBonus.LootType.ARCHAEOLOGY.getDescriptionId(), "награды от археологии");
        // living conditions
        add(PSTLivingEntityPredicates.HAS_ITEM_EQUIPPED.get(), "target.you", "вас");
        add(PSTLivingEntityPredicates.HAS_ITEM_EQUIPPED.get(), "target.target", "цели");
        add(PSTLivingEntityPredicates.HAS_ITEM_EQUIPPED.get(), "%s если на %s %s");
        add(PSTLivingEntityPredicates.HAS_EFFECT.get(), "target.you", "вас");
        add(PSTLivingEntityPredicates.HAS_EFFECT.get(), "target.target", "цель");
        add(PSTLivingEntityPredicates.HAS_EFFECT.get(), "%s если на %s действует %s");
        add(PSTLivingEntityPredicates.HAS_EFFECT.get(), "amplifier", "%s если на %s действует %s или выше");
        add(PSTLivingEntityPredicates.BURNING.get(), "target.you", "вы горите");
        add(PSTLivingEntityPredicates.BURNING.get(), "target.target", "цель горит");
        add(PSTLivingEntityPredicates.BURNING.get(), "%s если %s");
        add(PSTLivingEntityPredicates.FISHING.get(), "target.player", "вы рыбачите");
        add(PSTLivingEntityPredicates.FISHING.get(), "target.target", "цель рыбачит");
        add(PSTLivingEntityPredicates.FISHING.get(), "%s если %s");
        add(PSTLivingEntityPredicates.UNDERWATER.get(), "target.you", "вы");
        add(PSTLivingEntityPredicates.UNDERWATER.get(), "target.target", "цель");
        add(PSTLivingEntityPredicates.UNDERWATER.get(), "%s если %s под водой");
        add(PSTLivingEntityPredicates.DUAL_WIELDING.get(), "target.you", "вы держите");
        add(PSTLivingEntityPredicates.DUAL_WIELDING.get(), "target.target", "цель держит");
        add(PSTLivingEntityPredicates.DUAL_WIELDING.get(), "%s если %s %s в обеих руках");
        add(PSTLivingEntityPredicates.HAS_ITEM_IN_HAND.get(), "target.you", "вас");
        add(PSTLivingEntityPredicates.HAS_ITEM_IN_HAND.get(), "target.target", "цели");
        add(PSTLivingEntityPredicates.HAS_ITEM_IN_HAND.get(), "%s если у %s в руке %s");
        add(PSTLivingEntityPredicates.CROUCHING.get(), "target.you", "вы крадётесь");
        add(PSTLivingEntityPredicates.CROUCHING.get(), "target.target", "цель крадется");
        add(PSTLivingEntityPredicates.CROUCHING.get(), "%s если %s");
        add(PSTLivingEntityPredicates.UNARMED.get(), "target.you", "без оружия");
        add(PSTLivingEntityPredicates.UNARMED.get(), "target.target", "если цель безоружна");
        add(PSTLivingEntityPredicates.UNARMED.get(), "%s %s");
        add(PSTLivingEntityPredicates.NUMERIC_VALUE.get(), "more", "больше чем %s");
        add(PSTLivingEntityPredicates.NUMERIC_VALUE.get(), "less", "меньше чем %s");
        add(PSTLivingEntityPredicates.NUMERIC_VALUE.get(), "equal", "равно %s");
        add(PSTLivingEntityPredicates.NUMERIC_VALUE.get(), "at_least", "минимум %s");
        add(PSTLivingEntityPredicates.NUMERIC_VALUE.get(), "at_most", "максимум %s");
        add(PSTLivingEntityPredicates.ALL_ARMOR.get(), "target.player", "если вся ваша броня - ");
        add(PSTLivingEntityPredicates.ALL_ARMOR.get(), "target.enemy", "если вся броня цели - ");
        add(PSTLivingEntityPredicates.ALL_ARMOR.get(), "%s %s %s");
        // event listeners
        add(PSTEventListeners.ATTACK.get(), "%s при атаке");
        add(PSTEventListeners.ATTACK.get(), "damage", "%s при атаке %s");
        add(PSTEventListeners.BLOCK.get(), "%s при блоке");
        add(PSTEventListeners.BLOCK.get(), "damage", "%s при блоке %s");
        add(PSTEventListeners.EVASION.get(), "%s при уклонении");
        add(PSTEventListeners.ITEM_USED.get(), "%s когда вы используете %s");
        add(PSTEventListeners.DAMAGE_TAKEN.get(), "%s когда вы получаете %s");
        add(PSTEventListeners.ON_KILL.get(), "%s при убийстве");
        add(PSTEventListeners.ON_KILL.get(), "damage", "%s при убийстве %s");
        add(PSTEventListeners.SKILL_LEARNED.get(), "%s при изучении этого умения");
        add(PSTEventListeners.SKILL_REMOVED.get(), "%s когда вы забудете это умение");
        add(PSTEventListeners.TICKING.get(), "second", "%s каждую секунду");
        add(PSTEventListeners.TICKING.get(), "seconds", "%s каждые %s секунд");
        add(PSTEventListeners.TICKING.get(), "minute", "%s каждую минуту");
        add(PSTEventListeners.TICKING.get(), "minutes", "%s каждые %s минуты");
        add(PSTEventListeners.CRITICAL_HIT.get(), "%s при критическом ударе");
        // damage conditions
        add(PSTDamageConditions.PROJECTILE.get(), "Урон снарядами");
        add(PSTDamageConditions.PROJECTILE.get(), "type", "снарядами");
        add(PSTDamageConditions.PROJECTILE.get(), "type.blocked", "снаряда");
        add(PSTDamageConditions.MELEE.get(), "Урон в ближнем бою");
        add(PSTDamageConditions.MELEE.get(), "type", "в ближнем бою");
        add(PSTDamageConditions.MAGIC.get(), "Урон магией");
        add(PSTDamageConditions.MAGIC.get(), "type", "магией");
        add(PSTDamageConditions.MAGIC.get(), "type.blocked", "магии");
        add(PSTDamageConditions.NONE.get(), "Урон");
        add(PSTDamageConditions.FALL.get(), "Урон от падения");
        add(PSTDamageConditions.FALL.get(), "type", "от падения");
        add(PSTDamageConditions.FIRE.get(), "Урон огнём");
        add(PSTDamageConditions.FIRE.get(), "type", "огнём");
        add(PSTDamageConditions.THORNS.get(), "Урон шипами");
        add(PSTDamageConditions.THORNS.get(), "type", "шипами");
        // enchantment conditions
        add(PSTEnchantmentConditions.WEAPON.get(), "Зачарование оружия");
        add(PSTEnchantmentConditions.ARMOR.get(), "Зачарование брони");
        add(PSTEnchantmentConditions.NONE.get(), "Зачарование");
        // item conditions
        add(PSTItemConditions.NONE.get(), "Предмет");
        add(PSTItemConditions.NONE.get(), "where", "Предмете");
        add(PSTItemConditions.NONE.get(), "type", "ый Предмет");
        add(PSTItemConditions.NONE.get(), "plural.adjective", "ые Предметы");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "weapon", "Оружие");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "weapon.prepositional", "Оружии");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "weapon.adjective", "ое Оружие");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "weapon.plural.adjective", "ое Оружие");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "weapon.plural", "Оружие");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "ranged_weapon", "Оружие дальнего боя");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "ranged_weapon.prepositional", "Оружии дальнего боя");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "ranged_weapon.adjective", "ое Оружие дальнего боя");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "ranged_weapon.plural.adjective", "ое Оружие дальнего боя");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "ranged_weapon.plural", "Оружие дальнего боя");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "bow", "Лук");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "bow.prepositional", "Луке");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "bow.adjective", "ый Лук");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "bow.plural.adjective", "ые Луки");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "bow.plural", "Луки");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "crossbow", "Арбалет");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "crossbow.prepositional", "Арбалете");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "crossbow.adjective", "ый Арбалет");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "crossbow.plural.adjective", "ые Арбалеты");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "crossbow.plural", "Арбалеты");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "melee_weapon", "Оружие ближнего боя");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "melee_weapon.prepositional", "Оружии ближнего боя");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "melee_weapon.adjective", "ое Оружие ближнего боя");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "melee_weapon.plural.adjective", "ое Оружие ближнего боя");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "melee_weapon.plural", "Оружие ближнего боя");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "sword", "Меч");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "sword.prepositional", "Мече");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "sword.adjective", "ый Меч");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "sword.plural.adjective", "ые Мечи");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "sword.plural", "Мечи");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "trident", "Трезубец");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "trident.prepositional", "Трезубце");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "trident.adjective", "ый Трезубец");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "trident.plural.adjective", "ые Трезубцы");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "trident.plural", "Трезубцы");
        add(PSTTags.Items.RINGS, "Кольцо");
        add(PSTTags.Items.RINGS, "where", "Кольцах");
        add(PSTTags.Items.RINGS, "type", "ое Кольцо");
        add(PSTTags.Items.RINGS, "plural.adjective", "ые Кольца");
        add(PSTTags.Items.RINGS, "plural", "Кольца");
        add(PSTTags.Items.NECKLACES, "Ожерелье");
        add(PSTTags.Items.NECKLACES, "type", "ое Ожерелье");
        add(PSTTags.Items.NECKLACES, "plural.adjective", "ые Ожерелья");
        add(PSTTags.Items.NECKLACES, "plural", "Ожерелья");
        add(PSTTags.Items.LEATHER_ARMOR, "Кожаная броня");
        add(PSTTags.Items.LEATHER_ARMOR, "plural", "Кожаная броня");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "armor", "Броня");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "armor.prepositional", "Броне");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "armor.adjective", "ая Броня");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "armor.plural.adjective", "ая Броня");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "armor.plural", "Броня");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "helmet", "Шлем");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "helmet.prepositional", "Шлеме");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "helmet.adjective", "ый Шлем");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "helmet.plural.adjective", "ые Шлемы");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "helmet.plural", "Шлемы");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "chestplate", "Нагрудник");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "chestplate.prepositional", "Нагруднике");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "chestplate.adjective", "ый Нагрудник");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "chestplate.plural.adjective", "ые Нагрудники");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "chestplate.plural", "Нагрудники");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "leggings", "Штаны");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "leggings.prepositional", "Штанах");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "leggings.adjective", "ые Штаны");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "leggings.plural.adjective", "ые Штаны");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "leggings.plural", "Штаны");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "boots", "Ботинки");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "boots.prepositional", "Ботинках");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "boots.adjective", "ые Ботинки");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "boots.plural.adjective", "ые Ботинки");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "boots.plural", "Ботинки");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "shield", "Щит");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "shield.prepositional", "Щите");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "shield.adjective", "ый Щит");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "shield.plural.adjective", "ые Щиты");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "shield.plural", "Щиты");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "any", "Экипировка");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "any.prepositional", "Экипировке");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "any.adjective", "ая Экипировка");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "any.plural.adjective", "ая Экипировка");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "any.plural", "Экипировка");
        add(PSTItemConditions.POTIONS.get(), "any", "Зелье");
        add(PSTItemConditions.POTIONS.get(), "any.adjective", "ое Зелье");
        add(PSTItemConditions.POTIONS.get(), "any.plural.adjective", "ые Зелья");
        add(PSTItemConditions.POTIONS.get(), "any.plural", "Зелья");
        add(PSTItemConditions.POTIONS.get(), "beneficial", "Благотворное Зелье");
        add(PSTItemConditions.POTIONS.get(), "beneficial.adjective", "ое Благотворное Зелье");
        add(PSTItemConditions.POTIONS.get(), "beneficial.plural.adjective", "ые Благотворные Зелья");
        add(PSTItemConditions.POTIONS.get(), "beneficial.plural", "Благотворные Зелья");
        add(PSTItemConditions.POTIONS.get(), "harmful", "Вредящее Зелье");
        add(PSTItemConditions.POTIONS.get(), "harmful.adjective", "ое Вредящее Зелье");
        add(PSTItemConditions.POTIONS.get(), "harmful.plural.adjective", "ые Вредящие Зелья");
        add(PSTItemConditions.POTIONS.get(), "harmful.plural", "Вредящие Зелья");
        add(PSTItemConditions.POTIONS.get(), "neutral", "Нейтральное Зелье");
        add(PSTItemConditions.POTIONS.get(), "neutral.adjective", "ое Нейтральное Зелье");
        add(PSTItemConditions.POTIONS.get(), "neutral.plural.adjective", "ые Нейтральные Зелья");
        add(PSTItemConditions.POTIONS.get(), "neutral.plural", "Нейтральные Зелья");
        add(PSTItemConditions.FOOD.get(), "Еда");
        add(PSTItemConditions.FOOD.get(), "type", "ая Еда");
        add(PSTItemConditions.FOOD.get(), "plural.adjective", "ая Еда");
        add(PSTItemConditions.FOOD.get(), "plural", "Еда");
        add(PSTTags.Items.JEWELRY, "Бижутерия");
        add(PSTTags.Items.JEWELRY, "where", "Бижутерии");
        add(PSTTags.Items.JEWELRY, "type", "ая Бижутерия");
        add(PSTTags.Items.JEWELRY, "plural.adjective", "ая Бижутерия");
        add(PSTTags.Items.JEWELRY, "plural", "Бижутерия");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "tool", "Инструмент");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "tool.prepositional", "Инструменте");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "tool.adjective", "ый Инструмент");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "tool.plural.adjective", "ые Инструменты");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "tool.plural", "Инструменты");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "axe", "Топор");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "axe.prepositional", "Топоре");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "axe.adjective", "ый Топор");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "axe.plural.adjective", "ые Топоры");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "axe.plural", "Топоры");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "hoe", "Мотыга");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "hoe.prepositional", "Мотыге");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "hoe.adjective", "ая Мотыга");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "hoe.plural.adjective", "ые Мотыги");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "hoe.plural", "Мотыги");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "pickaxe", "Кирка");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "pickaxe.prepositional", "Кирке");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "pickaxe.adjective", "ая Кирка");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "pickaxe.plural.adjective", "ые Кирки");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "pickaxe.plural", "Кирки");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "shovel", "Лопата");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "shovel.prepositional", "Лопате");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "shovel.adjective", "ая Лопата");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "shovel.plural.adjective", "ые Лопаты");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "shovel.plural", "Лопаты");
        add(PSTItemConditions.ENCHANTED.get(), "Зачарованн%s");
        // skill multipliers
        add(PSTLivingMultipliers.NUMERIC_VALUE.get(), "plural", "%s per %s %s");
        add(PSTLivingMultipliers.NUMERIC_VALUE.get(), "%s per %s");
        // value providers
        add(PSTFloatFunctions.ATTRIBUTE_VALUE.get(), "multiplier.player.plural", "%s за каждые %s %s");
        add(PSTFloatFunctions.ATTRIBUTE_VALUE.get(), "multiplier.player", "%s за каждое очко %s");
        add(PSTFloatFunctions.ATTRIBUTE_VALUE.get(), "multiplier.enemy.plural", "%s за каждые %s %s цели");
        add(PSTFloatFunctions.ATTRIBUTE_VALUE.get(), "multiplier.enemy", "%s за каждое очко %s цели");

        add(PSTFloatFunctions.ATTRIBUTE_VALUE.get(), "condition.player", "%s если %s %s");
        add(PSTFloatFunctions.ATTRIBUTE_VALUE.get(), "condition.enemy", "%s если %s цели %s");

        add(PSTFloatFunctions.ATTRIBUTE_VALUE.get(), "requirement", "Достигните %s %s");

        add(PSTFloatFunctions.DISTANCE_TO_TARGET.get(), "multiplier.player.plural", "%s за каждые %s блока между вами и целью");
        add(PSTFloatFunctions.DISTANCE_TO_TARGET.get(), "multiplier.player", "%s за каждый блок между вами и целью");

        add(PSTFloatFunctions.DISTANCE_TO_TARGET.get(), "condition.player", "%s если расстояние между вами и целью %s");

        add("effect_type.beneficial", "положительный эффект");
        add("effect_type.beneficial.plural", "положительные эффекты");
        add("effect_type.harmful", "негативный эффект");
        add("effect_type.harmful.plural", "негативные эффекты");
        add("effect_type.neutral", "нейтральный эффект");
        add("effect_type.neutral.plural", "нейтральные эффект");
        add("effect_type.any", "эффект");
        add("effect_type.any.plural", "эффекты");

        add(PSTFloatFunctions.EFFECT_AMOUNT.get(), "multiplier.player.plural", "%s за каждые %s %s на вас");
        add(PSTFloatFunctions.EFFECT_AMOUNT.get(), "multiplier.player", "%s за каждый %s на вас");
        add(PSTFloatFunctions.EFFECT_AMOUNT.get(), "multiplier.enemy.plural", "%s за каждые %s %s цели");
        add(PSTFloatFunctions.EFFECT_AMOUNT.get(), "multiplier.enemy", "%s за каждый %s цели");

        add(PSTFloatFunctions.EFFECT_AMOUNT.get(), "condition.player", "%s если на вас действуют %s %s");
        add(PSTFloatFunctions.EFFECT_AMOUNT.get(), "condition.player.any", "%s если на вас действует любой %s");
        add(PSTFloatFunctions.EFFECT_AMOUNT.get(), "condition.player.none", "%s если на вас не действует %s");
        add(PSTFloatFunctions.EFFECT_AMOUNT.get(), "condition.enemy", "%s если на цель действуют %s %s");
        add(PSTFloatFunctions.EFFECT_AMOUNT.get(), "condition.enemy.any", "%s если на цель действует любой %s");
        add(PSTFloatFunctions.EFFECT_AMOUNT.get(), "condition.enemy.none", "%s если на цель не действуют %s");

        add(PSTFloatFunctions.EFFECT_AMOUNT.get(), "requirement", "На вас %s %s");

        add(PSTLivingEntityPredicates.NUMERIC_VALUE.get(), "equal.effect_amount", "ровно %s");

        add(PSTFloatFunctions.ENCHANTMENT_AMOUNT.get(), "multiplier.player.plural", "%s за каждое %s зачарования на %s");
        add(PSTFloatFunctions.ENCHANTMENT_AMOUNT.get(), "multiplier.player", "%s за каждое зачарование на %s");
        add(PSTFloatFunctions.ENCHANTMENT_AMOUNT.get(), "multiplier.enemy.plural", "%s за каждые %s зачарования на %s цели");
        add(PSTFloatFunctions.ENCHANTMENT_AMOUNT.get(), "multiplier.enemy", "%s за каждое зачарование на %s цели");

        add(PSTFloatFunctions.ENCHANTMENT_AMOUNT.get(), "enchantment", "зачарование");
        add(PSTFloatFunctions.ENCHANTMENT_AMOUNT.get(), "enchantment.plural", "зачарования");

        add(PSTFloatFunctions.ENCHANTMENT_AMOUNT.get(), "condition.player", "%s если у вас %s %s на %s");
        add(PSTFloatFunctions.ENCHANTMENT_AMOUNT.get(), "condition.player.any", "%s если ваш %s имеет зачарования");
        add(PSTFloatFunctions.ENCHANTMENT_AMOUNT.get(), "condition.player.none", "%s если ваш %s не имеет зачарования");
        add(PSTFloatFunctions.ENCHANTMENT_AMOUNT.get(), "condition.enemy", "%s если у цели есть %s %s на %s");
        add(PSTFloatFunctions.ENCHANTMENT_AMOUNT.get(), "condition.enemy.any", "%s если %s цели имеет зачарования");
        add(PSTFloatFunctions.ENCHANTMENT_AMOUNT.get(), "condition.enemy.none", "%s если %s цели не имеет зачарования");

        add(PSTFloatFunctions.ENCHANTMENT_AMOUNT.get(), "requirement", "На вашем %3$s %1$s %2$s");

        add(PSTLivingEntityPredicates.NUMERIC_VALUE.get(), "equal.enchantment_amount", "ровно %s");

        add(PSTFloatFunctions.ENCHANTMENT_LEVELS.get(), "multiplier.player.plural", "%s за %s уровня зачарований на %s");
        add(PSTFloatFunctions.ENCHANTMENT_LEVELS.get(), "multiplier.player", "%s за каждый уровень зачарований на %s");
        add(PSTFloatFunctions.ENCHANTMENT_LEVELS.get(), "multiplier.enemy.plural", "%s за %s уровня зачарований на %s цели");
        add(PSTFloatFunctions.ENCHANTMENT_LEVELS.get(), "multiplier.enemy", "%s за каждый уровень зачарований на %s цели");

        add(PSTFloatFunctions.ENCHANTMENT_LEVELS.get(), "level", "уровень");
        add(PSTFloatFunctions.ENCHANTMENT_LEVELS.get(), "level.plural", "уровней");

        add(PSTFloatFunctions.ENCHANTMENT_LEVELS.get(), "condition.player", "%s если у вас %s %s зачарований на %s");
        add(PSTFloatFunctions.ENCHANTMENT_LEVELS.get(), "condition.player.any", "%s если ваш %s имеет зачарования");
        add(PSTFloatFunctions.ENCHANTMENT_LEVELS.get(), "condition.player.none", "%s если ваш %s не имеет зачарований");
        add(PSTFloatFunctions.ENCHANTMENT_LEVELS.get(), "condition.enemy", "%s если у цели %s %s зачарований на %s");
        add(PSTFloatFunctions.ENCHANTMENT_LEVELS.get(), "condition.enemy.any", "%s если %s цели имеет зачарования");
        add(PSTFloatFunctions.ENCHANTMENT_LEVELS.get(), "condition.enemy.none", "%s если %s цели не имеет зачарований");

        add(PSTFloatFunctions.ENCHANTMENT_LEVELS.get(), "requirement", "На вашем %3$s %1$s %2$s");

        add(PSTFloatFunctions.EQUIPMENT_DURABILITY.get(), "multiplier.player.plural", "%s за каждые %s прочности вашего %s");
        add(PSTFloatFunctions.EQUIPMENT_DURABILITY.get(), "multiplier.player", "%s за единицу прочности вашего %s");
        add(PSTFloatFunctions.EQUIPMENT_DURABILITY.get(), "multiplier.enemy.plural", "%s за каждые %s прочности %s цели");
        add(PSTFloatFunctions.EQUIPMENT_DURABILITY.get(), "multiplier.enemy", "%s за единицу прочности %s цели");

        add(PSTFloatFunctions.EQUIPMENT_DURABILITY.get(), "condition.player", "%s если ваш %s имеет %s прочности");
        add(PSTFloatFunctions.EQUIPMENT_DURABILITY.get(), "condition.enemy", "%s если %s цели имеет %s durability");

        add(PSTFloatFunctions.EQUIPMENT_DURABILITY.get(), "requirement", "Ваш %2$s имеет %1$s прочности");

        add(PSTLivingEntityPredicates.NUMERIC_VALUE.get(), "equal.equipment_durability", "ровно %s");

        add(PSTFloatFunctions.FOOD_LEVEL.get(), "point", "очко сытости");
        add(PSTFloatFunctions.FOOD_LEVEL.get(), "point.plural", "очка сытости");
        add(PSTFloatFunctions.FOOD_LEVEL.get(), "multiplier.player", "%s за каждое текущее %s");
        add(PSTFloatFunctions.FOOD_LEVEL.get(), "multiplier.player.plural", "%s за каждые %s текущих %s");
        add(PSTFloatFunctions.FOOD_LEVEL.get(), "multiplier.player.missing", "%s за каждое недостающее %s");
        add(PSTFloatFunctions.FOOD_LEVEL.get(), "multiplier.player.plural.missing", "%s за каждые %s недостающие %s");
        add(PSTFloatFunctions.FOOD_LEVEL.get(), "multiplier.enemy", "%s за каждое текущее %s цели");
        add(PSTFloatFunctions.FOOD_LEVEL.get(), "multiplier.enemy.plural", "%s за каждые %s текущих %s цели");
        add(PSTFloatFunctions.FOOD_LEVEL.get(), "multiplier.enemy.missing", "%s за каждое недостающее %s цели");
        add(PSTFloatFunctions.FOOD_LEVEL.get(), "multiplier.enemy.plural.missing", "%s за каждые %s недостающие %s цели");
        add(PSTFloatFunctions.FOOD_LEVEL.get(), "condition.player", "%s если у вас %s %s");
        add(PSTFloatFunctions.FOOD_LEVEL.get(), "condition.enemy", "%s если у цели %s %s");
        add(PSTFloatFunctions.FOOD_LEVEL.get(), "condition.player.missing", "%s если у вас недостает %s %s");
        add(PSTFloatFunctions.FOOD_LEVEL.get(), "condition.enemy.missing", "%s если у цели недостает %s %s");
        add(PSTFloatFunctions.FOOD_LEVEL.get(), "condition.player.full", "%s если вы не голодны");
        add(PSTFloatFunctions.FOOD_LEVEL.get(), "condition.enemy.full", "%s если цель не голодна");
        add(PSTFloatFunctions.FOOD_LEVEL.get(), "condition.player.not_full", "%s если вы голодны");
        add(PSTFloatFunctions.FOOD_LEVEL.get(), "condition.enemy.not_full", "%s если цель голодна");
        add(PSTFloatFunctions.FOOD_LEVEL.get(), "requirement", "У вас %s %s");
        add(PSTLivingEntityPredicates.NUMERIC_VALUE.get(), "equal.food_level", "ровно %s");

        add(PSTFloatFunctions.HEALTH_LEVEL.get(), "point", "очко здоровья");
        add(PSTFloatFunctions.HEALTH_LEVEL.get(), "point.plural", "очка здоровья");
        add(PSTFloatFunctions.HEALTH_LEVEL.get(), "percentage", "здоровья");
        add(PSTFloatFunctions.HEALTH_LEVEL.get(), "percentage.plural", "здоровья");
        add(PSTFloatFunctions.HEALTH_LEVEL.get(), "multiplier.player", "%s за каждое текущее %s");
        add(PSTFloatFunctions.HEALTH_LEVEL.get(), "multiplier.player.plural", "%s за каждые %s текущих %s");
        add(PSTFloatFunctions.HEALTH_LEVEL.get(), "multiplier.player.missing", "%s за каждое недостающее %s");
        add(PSTFloatFunctions.HEALTH_LEVEL.get(), "multiplier.player.plural.missing", "%s за каждые %s недостающих %s");
        add(PSTFloatFunctions.HEALTH_LEVEL.get(), "multiplier.enemy", "%s за каждое текущее %s цели");
        add(PSTFloatFunctions.HEALTH_LEVEL.get(), "multiplier.enemy.plural", "%s за каждые %s текущих %s цели");
        add(PSTFloatFunctions.HEALTH_LEVEL.get(), "multiplier.enemy.missing", "%s за каждое недостающее %s цели");
        add(PSTFloatFunctions.HEALTH_LEVEL.get(), "multiplier.enemy.plural.missing", "%s за каждые %s недостающих %s цели");
        add(PSTFloatFunctions.HEALTH_LEVEL.get(), "condition.player", "%s если у вас %s %s");
        add(PSTFloatFunctions.HEALTH_LEVEL.get(), "condition.enemy", "%s если у цели %s %s");
        add(PSTFloatFunctions.HEALTH_LEVEL.get(), "condition.player.missing", "%s если у вас недостает %s %s");
        add(PSTFloatFunctions.HEALTH_LEVEL.get(), "condition.enemy.missing", "%s если у цели недостает %s %s");
        add(PSTFloatFunctions.HEALTH_LEVEL.get(), "condition.player.full", "%s если у вас полное здоровье");
        add(PSTFloatFunctions.HEALTH_LEVEL.get(), "condition.enemy.full", "%s если у цели полное здоровье");
        add(PSTFloatFunctions.HEALTH_LEVEL.get(), "condition.player.not_full", "%s если вы ранены");
        add(PSTFloatFunctions.HEALTH_LEVEL.get(), "condition.enemy.not_full", "%s если цель ранена");
        add(PSTFloatFunctions.HEALTH_LEVEL.get(), "requirement", "У вас %s %s");
        add(PSTLivingEntityPredicates.NUMERIC_VALUE.get(), "equal.health_level", "ровно %s");

        add(IronsSpellbooksCompat.MANA_LEVEL_FUNCTION.get(), "point", "очко маны");
        add(IronsSpellbooksCompat.MANA_LEVEL_FUNCTION.get(), "point.plural", "очка маны");
        add(IronsSpellbooksCompat.MANA_LEVEL_FUNCTION.get(), "percentage", "маны");
        add(IronsSpellbooksCompat.MANA_LEVEL_FUNCTION.get(), "percentage.plural", "маны");
        add(IronsSpellbooksCompat.MANA_LEVEL_FUNCTION.get(), "multiplier.player", "%s за каждое текущее %s");
        add(IronsSpellbooksCompat.MANA_LEVEL_FUNCTION.get(), "multiplier.player.plural", "%s за каждые %s текущих %s");
        add(IronsSpellbooksCompat.MANA_LEVEL_FUNCTION.get(), "multiplier.player.missing", "%s за каждое недостающее %s");
        add(IronsSpellbooksCompat.MANA_LEVEL_FUNCTION.get(), "multiplier.player.plural.missing", "%s за каждые %s недостающих %s");
        add(IronsSpellbooksCompat.MANA_LEVEL_FUNCTION.get(), "multiplier.enemy", "%s за каждое текущее %s цели");
        add(IronsSpellbooksCompat.MANA_LEVEL_FUNCTION.get(), "multiplier.enemy.plural", "%s за каждые %s текущих %s цели");
        add(IronsSpellbooksCompat.MANA_LEVEL_FUNCTION.get(), "multiplier.enemy.missing", "%s за каждое недостающее %s цели");
        add(IronsSpellbooksCompat.MANA_LEVEL_FUNCTION.get(), "multiplier.enemy.plural.missing", "%s за каждые %s недостающих %s цели");
        add(IronsSpellbooksCompat.MANA_LEVEL_FUNCTION.get(), "condition.player", "%s если у вас %s %s");
        add(IronsSpellbooksCompat.MANA_LEVEL_FUNCTION.get(), "condition.enemy", "%s если у цели %s %s");
        add(IronsSpellbooksCompat.MANA_LEVEL_FUNCTION.get(), "condition.player.missing", "%s если у вас недостает %s %s");
        add(IronsSpellbooksCompat.MANA_LEVEL_FUNCTION.get(), "condition.enemy.missing", "%s если у цели недостает %s %s");
        add(IronsSpellbooksCompat.MANA_LEVEL_FUNCTION.get(), "condition.player.full", "%s если у вас полная мана");
        add(IronsSpellbooksCompat.MANA_LEVEL_FUNCTION.get(), "condition.enemy.full", "%s если у цели полная мана");
        add(IronsSpellbooksCompat.MANA_LEVEL_FUNCTION.get(), "condition.player.not_full", "%s если у вас неполная мана");
        add(IronsSpellbooksCompat.MANA_LEVEL_FUNCTION.get(), "condition.enemy.not_full", "%s если у цели неполная мана");
        add(IronsSpellbooksCompat.MANA_LEVEL_FUNCTION.get(), "requirement", "У вас %s %s");
        add(PSTLivingEntityPredicates.NUMERIC_VALUE.get(), "equal.mana_level", "ровно %s");

        // skill requirements
        add(PSTSkillRequirements.ADVANCEMENT.get(), "Получите достижение %s");
        add(PSTSkillRequirements.LEARNED_SKILL.get(), "Изучите умение %s");
        // items
        add("item.cant_use.info", "Вы не можете это использовать");
        add(PSTItems.WISDOM_SCROLL.get(), "Свиток мудрости");
        add(PSTItems.AMNESIA_SCROLL.get(), "Свиток амнезии");
        add(PSTItems.WORKBENCH.get(), "Продвинутый рабочий стол");
        addTooltip(PSTItems.WISDOM_SCROLL.get(), "Дарует одно очко пассивных умений");
        addTooltip(PSTItems.AMNESIA_SCROLL.get(), "Сбрасывает ваше древо пассивных умений");
        addWarning(PSTItems.AMNESIA_SCROLL.get(), "%d%% очков умений будут потеряны");
        // effects
        add(PSTMobEffects.LIQUID_FIRE.get(), "Жидкий огонь");
        // potions
        add(PSTPotions.LIQUID_FIRE_1.get(), "жидкого огня");
        add(PSTPotions.LIQUID_FIRE_2.get(), "жидкого огня");
        // system messages
        add("skilltree.message.reset", "Древо пассивных умений изменилось. Ваши очки умений были восстановлены.");
        add("skilltree.message.reset_command", "Ваше древо пассивных умений было сброшено.");
        add("skilltree.message.point_command", "Получено очко пассивных умений.");
        add("skilltree.message.grant_skill_command", "Вам было даровано умение %s.");
        // screen info
        add("widget.skill_points_left", "Очков осталось: %s");
        add("widget.skill_button.not_learned", "Умение не изучено");
        add("widget.skill_button.multiple_bonuses", "%s и %s");
        add("widget.buy_skill_button", "Купить очко умений");
        add("widget.confirm_button", "Подтвердить");
        add("widget.cancel_button", "Отмена");
        add("widget.show_stats", "Список бонусов");
        add("key.categories.skilltree", "Древо пассивных умений");
        add("key.display_skill_tree", "Открыть древо пассивных умений");
        add("skill.limitation", "Ограничение: %s");
        add("skill.requirements", "Требования:");
        // tabs
        add("itemGroup.skilltree", "Passive Skill Tree");
        // recipes
        add(PSTRecipeSerializers.WORKBENCH_ITEM_BONUS.get(), "%s [%s]");
    }

    protected void add(Potion potion, String name) {
        add(potion.getName(Items.POTION.getDescriptionId() + ".effect."), "Зелье " + name);
        add(potion.getName(Items.SPLASH_POTION.getDescriptionId() + ".effect."), "Взрывное зелье " + name);
        add(potion.getName(Items.LINGERING_POTION.getDescriptionId() + ".effect."), "Туманное зелье " + name);
    }
}
