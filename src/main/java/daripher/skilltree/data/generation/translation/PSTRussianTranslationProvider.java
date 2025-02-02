package daripher.skilltree.data.generation.translation;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.init.*;
import daripher.skilltree.skill.bonus.player.GainedExperienceBonus;
import daripher.skilltree.skill.bonus.player.LootDuplicationBonus;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;

public class PSTRussianTranslationProvider extends PSTTranslationProvider {
  public PSTRussianTranslationProvider(DataGenerator gen) {
    super(gen, SkillTreeMod.MOD_ID, "ru_ru");
  }

  @Override
  protected void addTranslations() {
    // skill bonuses
    add(PSTSkillBonuses.DAMAGE.get(), "Урон");
    add(PSTSkillBonuses.CRIT_DAMAGE.get(), "Множитель критического урона");
    add(PSTSkillBonuses.CRIT_DAMAGE.get(), "damage", "Множитель критического урона %s");
    add(PSTSkillBonuses.CRIT_CHANCE.get(), "Шанс критического удара");
    add(PSTSkillBonuses.CRIT_CHANCE.get(), "damage", "Шанс критического удара %s");
    add(PSTSkillBonuses.BLOCK_BREAK_SPEED.get(), "Скорость добычи блоков");
    add(PSTSkillBonuses.REPAIR_EFFICIENCY.get(), "Ремонтируем%s: %s");
    add(PSTSkillBonuses.REPAIR_EFFICIENCY.get(), "bonus", "Прочности восстановлено");
    add(PSTSkillBonuses.FREE_ENCHANTMENT.get(), "Шанс зачаровать предмет бесплатно");
    add(PSTSkillBonuses.RECIPE_UNLOCK.get(), "Открывает рецепт: %s");
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
    add(PSTSkillBonuses.CANT_USE_ITEM.get(), "Нельзя использовать %s");
    add(PSTSkillBonuses.HEALING.get(), "player", "Вы восстанавливаете %s здоровья");
    add(PSTSkillBonuses.HEALING.get(), "player.chance", "Шанс восстановить %s здоровья");
    add(PSTSkillBonuses.HEALING.get(), "enemy", "Враги восстанавливают %s здоровья");
    add(PSTSkillBonuses.HEALING.get(), "enemy.chance", "Шанс для врагов восстановить %s здоровья");
    add(PSTSkillBonuses.INFLICT_DAMAGE.get(), "player", "Вы получаете %s урона");
    add(PSTSkillBonuses.INFLICT_DAMAGE.get(), "player.chance", "Шанс получить %s урона");
    add(PSTSkillBonuses.INFLICT_DAMAGE.get(), "enemy", "Вы наносите %s урона");
    add(PSTSkillBonuses.INFLICT_DAMAGE.get(), "enemy.chance", "Шанс нанести %s урона");
    add(PSTSkillBonuses.CAN_POISON_ANYONE.get(), "Ваши яды действуют на любых врагов");
    add(PSTSkillBonuses.LETHAL_POISON.get(), "Ваши яды летальны");
    add(PSTSkillBonuses.DAMAGE_TAKEN.get(), "Получаемый %s");
    add(PSTSkillBonuses.DAMAGE_AVOIDANCE.get(), "Шанс избежать %s");
    add(PSTSkillBonuses.DAMAGE_CONVERSION.get(), "%s%% всего %s конвертируется в %s");
    add(PSTSkillBonuses.GRANT_ITEM.get(), "Дарует %s при изучении");
    add(PSTSkillBonuses.GRANT_ITEM.get(), "amount", "Дарует %sx %s при изучении");
    add(PSTSkillBonuses.EFFECT_DURATION.get(), "player", "Duration of %s on you");
    add(PSTSkillBonuses.EFFECT_DURATION.get(), "enemy", "Duration of inflicted %s");
    add(PSTSkillBonuses.PROJECTILE_DUPLICATION.get(), "chance", "Шанс удвоить выпущенный снаряд");
    add(PSTSkillBonuses.PROJECTILE_DUPLICATION.get(), "amount", "Вы выпускаете %s дополнительных снарядов");
    add(PSTSkillBonuses.PROJECTILE_DUPLICATION.get(), "Вы выпускаете дополнительный снаряд");
    add(PSTSkillBonuses.SELF_SPLASH_IMMUNE.get(), "Ваши взрывные зелья не действуют на вас");
    add(PSTSkillBonuses.PROJECTILE_SPEED.get(), "Скорость снарядов");
    // item bonuses
    add(PSTItemBonuses.SOCKETS.get(), "+%d Гнезда для Самоцветов");
    add(PSTItemBonuses.DURABILITY.get(), "Прочность");
    add(PSTItemBonuses.QUIVER_CAPACITY.get(), "Вместимость");
    add(PSTItemBonuses.POTION_AMPLIFICATION.get(), "Шанс Усиления");
    add(PSTItemBonuses.POTION_DURATION.get(), "Длительность");
    add(PSTItemBonuses.FOOD_EFFECT.get(), "%s на %s");
    add(PSTItemBonuses.FOOD_SATURATION.get(), "Насыщение");
    add(PSTItemBonuses.FOOD_HEALING.get(), "Восстанавливает %s Здоровья");
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
    add(PSTLivingConditions.HAS_ITEM_EQUIPPED.get(), "target.you", "вас");
    add(PSTLivingConditions.HAS_ITEM_EQUIPPED.get(), "target.target", "цели");
    add(PSTLivingConditions.HAS_ITEM_EQUIPPED.get(), "%s если на %s %s");
    add(PSTLivingConditions.HAS_EFFECT.get(), "target.you", "вас");
    add(PSTLivingConditions.HAS_EFFECT.get(), "target.target", "цель");
    add(PSTLivingConditions.HAS_EFFECT.get(), "%s если на %s действует %s");
    add(PSTLivingConditions.HAS_EFFECT.get(), "amplifier", "%s если на %s действует %s или выше");
    add(PSTLivingConditions.BURNING.get(), "target.you", "вы горите");
    add(PSTLivingConditions.BURNING.get(), "target.target", "цель горит");
    add(PSTLivingConditions.BURNING.get(), "%s если %s");
    add(PSTLivingConditions.FISHING.get(), "target.you", "вы рыбачите");
    add(PSTLivingConditions.FISHING.get(), "target.target", "цель рыбачит");
    add(PSTLivingConditions.FISHING.get(), "%s если %s");
    add(PSTLivingConditions.UNDERWATER.get(), "target.you", "вы");
    add(PSTLivingConditions.UNDERWATER.get(), "target.target", "цель");
    add(PSTLivingConditions.UNDERWATER.get(), "%s если %s под водой");
    add(PSTLivingConditions.DUAL_WIELDING.get(), "target.you", "вы держите");
    add(PSTLivingConditions.DUAL_WIELDING.get(), "target.target", "цель держит");
    add(PSTLivingConditions.DUAL_WIELDING.get(), "%s если %s %s в обеих руках");
    add(PSTLivingConditions.HAS_ITEM_IN_HAND.get(), "target.you", "вас");
    add(PSTLivingConditions.HAS_ITEM_IN_HAND.get(), "target.target", "цели");
    add(PSTLivingConditions.HAS_ITEM_IN_HAND.get(), "%s если у %s в руке %s");
    add(PSTLivingConditions.CROUCHING.get(), "target.you", "вы крадётесь");
    add(PSTLivingConditions.CROUCHING.get(), "target.target", "цель крадется");
    add(PSTLivingConditions.CROUCHING.get(), "%s если %s");
    add(PSTLivingConditions.UNARMED.get(), "target.you", "без оружия");
    add(PSTLivingConditions.UNARMED.get(), "target.target", "если цель безоружна");
    add(PSTLivingConditions.UNARMED.get(), "%s %s");
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
    add(PSTTags.Items.QUIVERS, "Колчан");
    add(PSTTags.Items.QUIVERS, "where", "Колчане");
    add(PSTTags.Items.QUIVERS, "type", "ый Колчан");
    add(PSTTags.Items.QUIVERS, "plural.adjective", "ые Колчаны");
    add(PSTTags.Items.QUIVERS, "plural", "Колчаны");
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

    // recipes
    add("recipe.skilltree.weapon_poisoning", "Отравление Оружия");
    add("recipe.skilltree.weapon_poisoning.info", "(Объедините оружие ближнего боя и вредящее зелье на верстаке, чтобы отравить оружие)");
    add("recipe.skilltree.potion_mixing", "Смешивание Зелий");
    add("recipe.skilltree.potion_mixing.info", "(Объедините два разных зелья на верстаке, чтобы создать микстуру)");
    add("upgrade_recipe.chance", "Шанс: %s%%");
    // potions info
    add("potion.superior", "Качественное %s");
    add("item.minecraft.potion.mixture", "Микстура");
    add("item.minecraft.splash_potion.mixture", "Взрывная микстура");
    add("item.minecraft.lingering_potion.mixture", "Туманная микстура");
    addMixture("ныряния", MobEffects.NIGHT_VISION, MobEffects.WATER_BREATHING);
    addMixture("вечной молодости", MobEffects.HEAL, MobEffects.REGENERATION);
    addMixture("болезни", MobEffects.POISON, MobEffects.WEAKNESS);
    addMixture("филина", MobEffects.INVISIBILITY, MobEffects.NIGHT_VISION);
    addMixture("труса", MobEffects.INVISIBILITY, MobEffects.MOVEMENT_SPEED);
    addMixture("драконьей крови", MobEffects.FIRE_RESISTANCE, MobEffects.REGENERATION);
    addMixture("демона", MobEffects.FIRE_RESISTANCE, MobEffects.DAMAGE_BOOST);
    addMixture("убийцы", MobEffects.HARM, MobEffects.POISON);
    addMixture("антигравитации", MobEffects.JUMP, MobEffects.SLOW_FALLING);
    addMixture("старения", MobEffects.MOVEMENT_SLOWDOWN, MobEffects.WEAKNESS);
    addMixture("атлета", MobEffects.JUMP, MobEffects.MOVEMENT_SPEED);
    addMixture("вора", MobEffects.INVISIBILITY, MobEffects.LUCK);
    addMixture("охотника за сокровищами", MobEffects.LUCK, MobEffects.WATER_BREATHING);
    addMixture("рыцаря", MobEffects.REGENERATION, MobEffects.DAMAGE_BOOST);
    addMixture("замедленного времени", MobEffects.SLOW_FALLING, MobEffects.MOVEMENT_SLOWDOWN);
    addMixture("солдата", MobEffects.HEAL, MobEffects.DAMAGE_BOOST);
    addMixture("ниндзя", MobEffects.DAMAGE_BOOST, MobEffects.MOVEMENT_SPEED);
    addMixture("благословения", MobEffects.LUCK, MobEffects.DAMAGE_BOOST);
    addMixture("чумы", MobEffects.POISON, MobEffects.MOVEMENT_SLOWDOWN);
    // gems info
    add("gem.socket", "Пустое гнездо");
    add("gem.additional_socket_1", "• Имеет дополнительное гнездо");
    add("gem.disabled", "Отключено с модулем приключений Apotheosis");
    add("gem_class_format", "• %s: %s");
    add("gem.tooltip", "• Можно вставить в предметы с гнёздами");
    add("gem_bonus.removal", "Уничтожает Самоцветы в предмете");
    add("gem_bonus.random", "Результат непредсказуем");
    // weapon info
    add("weapon.poisoned", "Отравлено:");
    // items
    add("item.cant_use.info", "Вы не можете это использовать");
    addGem("citrine", "цитрин");
    addGem("ruby", "рубин");
    addGem("sapphire", "сапфир");
    addGem("jade", "нефрит");
    addGem("iriscite", "ирисцит");
    addGem("vacucite", "вакуцит");
    add(PSTItems.WISDOM_SCROLL.get(), "Свиток мудрости");
    add(PSTItems.AMNESIA_SCROLL.get(), "Свиток амнезии");
    add(PSTItems.COPPER_RING.get(), "Медное кольцо");
    add(PSTItems.IRON_RING.get(), "Железное кольцо");
    add(PSTItems.GOLDEN_RING.get(), "Золотое кольцо");
    add(PSTItems.COPPER_NUGGET.get(), "Кусочек меди");
    add(PSTItems.ASSASSIN_NECKLACE.get(), "Ожерелье убийцы");
    add(PSTItems.HEALER_NECKLACE.get(), "Ожерелье целителя");
    add(PSTItems.TRAVELER_NECKLACE.get(), "Ожерелье путешественника");
    add(PSTItems.SIMPLE_NECKLACE.get(), "Ожерелье простоты");
    add(PSTItems.SCHOLAR_NECKLACE.get(), "Ожерелье учёного");
    add(PSTItems.ARSONIST_NECKLACE.get(), "Ожерелье поджигателя");
    add(PSTItems.FISHERMAN_NECKLACE.get(), "Ожерелье рыбака");
    add(PSTItems.ANCIENT_ALLOY_GILDED.get(), "Позолоченный древний сплав");
    add(PSTItems.ANCIENT_ALLOY_LIGHTWEIGHT.get(), "Облегченный древний сплав");
    add(PSTItems.ANCIENT_ALLOY_CURATIVE.get(), "Целебный древний сплав");
    add(PSTItems.ANCIENT_ALLOY_TOXIC.get(), "Токсичный древний сплав");
    add(PSTItems.ANCIENT_ALLOY_ENCHANTED.get(), "Зачарованный древний сплав");
    add(PSTItems.ANCIENT_ALLOY_SPATIAL.get(), "Пространственный древний сплав");
    add(PSTItems.ANCIENT_ALLOY_DURABLE.get(), "Прочный древний сплав");
    add(PSTItems.ANCIENT_ALLOY_HOT.get(), "Горячий древний сплав");
    add(PSTItems.ANCIENT_BOOK.get(), "Древняя книга");
    addTooltip(PSTItems.WISDOM_SCROLL.get(), "Дарует одно очко пассивных умений");
    addTooltip(PSTItems.AMNESIA_SCROLL.get(), "Сбрасывает ваше древо пассивных умений");
    addWarning(PSTItems.AMNESIA_SCROLL.get(), "%d%% очков умений будут потеряны");
    addTooltip(PSTItems.ANCIENT_ALLOY_GILDED.get(), "Невозможно налюбоваться");
    addTooltip(PSTItems.ANCIENT_ALLOY_LIGHTWEIGHT.get(), "Гораздо легче чем кажется");
    addTooltip(PSTItems.ANCIENT_ALLOY_CURATIVE.get(), "Вы успокаиваетесь держа его в руках");
    addTooltip(PSTItems.ANCIENT_ALLOY_TOXIC.get(), "Крайне опасен");
    addTooltip(PSTItems.ANCIENT_ALLOY_ENCHANTED.get(), "Излучает магическую энергию");
    addTooltip(PSTItems.ANCIENT_ALLOY_SPATIAL.get(), "Искревляет пространство вокруг себя");
    addTooltip(PSTItems.ANCIENT_ALLOY_DURABLE.get(), "На слитке ни царапины");
    addTooltip(PSTItems.ANCIENT_ALLOY_HOT.get(), "Обжигает ладони");
    addTooltip(PSTItems.ANCIENT_BOOK.get(), "Магия этого предмета была исчерпана");
    add("ancient_material.tooltip", "Требует определенных знаний для использования");
    // slots
    addCurioSlot("ring", "Слот кольца");
    addCurioSlot("ring", "plural", "Слоты колец");
    addCurioSlot("necklace", "Слот ожерелья");
    addCurioSlot("necklace", "plural", "Слоты ожерелий");
    // attributes
    add(PSTAttributes.REGENERATION.get(), "Регенерация здоровья");
    add(PSTAttributes.EXP_PER_MINUTE.get(), "Опыт в минуту");
    add(PSTAttributes.DEXTERITY.get(), "Ловкость");
    addInfo(PSTAttributes.DEXTERITY.get(), "По умолчанию, 1 ловкость дарует +1% к урону снарядами");
    add(PSTAttributes.STRENGTH.get(), "Сила");
    addInfo(PSTAttributes.STRENGTH.get(), "По умолчанию, 1 сила дарует +1% к урону в ближнем бою");
    add(PSTAttributes.INTELLIGENCE.get(), "Интеллект");
    addInfo(PSTAttributes.INTELLIGENCE.get(), "По умолчанию, 1 интеллект дарует +1% к урону магией");
    // effects
    add(PSTMobEffects.LIQUID_FIRE.get(), "Жидкий огонь");
    // potions
    add(PSTPotions.LIQUID_FIRE_1.get(), "жидкого огня");
    add(PSTPotions.LIQUID_FIRE_2.get(), "жидкого огня");
    // system messages
    add("skilltree.message.reset", "Древо пассивных умений изменилось. Ваши очки умений были восстановлены.");
    add("skilltree.message.reset_command", "Ваше древо пассивных умений было сброшено.");
    add("skilltree.message.point_command", "Получено очко пассивных умений.");
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
    // jei info
    add("skilltree.jei.gem_info", "Самоцветы можно вставлять в предметы с гнёздами на кузнечном столе. Выпадают из любой руды с небольшим шансом " +
        "(требуется инструмент без шёлкового касания).");
    // curios info
    add("curios.identifier.quiver", "Колчан");
    add("curios.modifiers.quiver", "Когда надет:");
    // tabs
    add("itemGroup.skilltree", "Passive Skill Tree");
    // misc
    add("item.modifiers.both_hands", "Когда в руке:");
    // apotheosis compatibility
    add("text.apotheosis.category.curios:ring.plural", "Кольца");
    add("text.apotheosis.category.curios:necklace.plural", "Ожерелья");
    add("gem_class.jewelry", "Бижутерия");
    // affix names
    add("affix.skilltree:jewelry/dmg_reduction/tempered", "Закалённое");
    add("affix.skilltree:jewelry/dmg_reduction/tempered.suffix", "Закалки");
    add("affix.skilltree:jewelry/attribute/immortal", "Бессмертное");
    add("affix.skilltree:jewelry/attribute/immortal.suffix", "Бессмертия");
    add("affix.skilltree:jewelry/attribute/experienced", "Опытное");
    add("affix.skilltree:jewelry/attribute/experienced.suffix", "Опыта");
    add("affix.skilltree:jewelry/attribute/lucky", "Удачливое");
    add("affix.skilltree:jewelry/attribute/lucky.suffix", "Удачи");
    add("affix.skilltree:jewelry/attribute/hasty", "Спешащее");
    add("affix.skilltree:jewelry/attribute/hasty.suffix", "Спешки");
    add("affix.skilltree:jewelry/attribute/hidden", "Сокрытое");
    add("affix.skilltree:jewelry/attribute/hidden.suffix", "Скрытности");
    add("affix.skilltree:jewelry/attribute/healthy", "Здоровое");
    add("affix.skilltree:jewelry/attribute/healthy.suffix", "Здоровья");
    // enchantments
    add(PSTEnchantments.STEEL_MIND.get(), "Стальной разум");
    add(PSTEnchantments.MAGIC_FLOW.get(), "Магический поток");
    add(PSTEnchantments.DRAGON_BLOOD.get(), "Кровь дракона");
    add(PSTEnchantments.BOTTOMLESS_FLASK.get(), "Бездонный флакон");
    add(PSTEnchantments.FIRE_WALL.get(), "Стена огня");
  }

  protected void addMixture(String name, MobEffect... effects) {
    addMixture("Микстура " + name, "potion", effects);
    addMixture("Взрывная микстура" + name, "splash_potion", effects);
    addMixture("Туманная микстура " + name, "lingering_potion", effects);
  }

  protected void add(Potion potion, String name) {
    add(potion.getName(Items.POTION.getDescriptionId() + ".effect."), "Зелье " + name);
    add(potion.getName(Items.SPLASH_POTION.getDescriptionId() + ".effect."), "Взрывное зелье " + name);
    add(potion.getName(Items.LINGERING_POTION.getDescriptionId() + ".effect."), "Туманное зелье " + name);
  }

  protected void addGem(String type, String name) {
    super.addGem(type, name, "Раскрошенный", "Сломанный", "Некачественный", "Большой", "Редкий", "Исключительный");
  }
}
