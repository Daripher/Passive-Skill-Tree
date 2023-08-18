package daripher.skilltree.datagen.translation;

import java.util.Arrays;

import javax.annotation.Nullable;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.init.PSTAttributes;
import daripher.skilltree.init.PSTEffects;
import daripher.skilltree.init.PSTItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.data.LanguageProvider;
import top.theillusivec4.curios.common.CuriosHelper;

public class PSTRussianTranslationProvider extends LanguageProvider {
	public PSTRussianTranslationProvider(DataGenerator gen) {
		super(gen, SkillTreeMod.MOD_ID, "ru_ru");
	}

	@Override
	protected void addTranslations() {
		addSkill("void", "Пустота", "Ничего не даёт");
		// hunter skills
		addSkill("hunter_class", "Охотник");
		addSkill("hunter_crafting_notable_1", "Бережливость");
		addSkill("hunter_defensive_notable_1", "Желание выжить");
		addSkill("hunter_offensive_notable_1", "Искусный стрелок");
		addSkill("hunter_life_notable_1", "Движение - жизнь");
		addSkill("hunter_speed_notable_1", "Скоростная стрельба");
		addSkill("hunter_healing_notable_1", "Кровожадные стрелы");
		addSkill("hunter_crit_notable_1", "Точность");
		addSkill("hunter_defensive_crafting_keystone_1", "Шляпник");
		addSkill("hunter_offensive_crafting_keystone_1", "Декоративные луки");
		addSkill("hunter_defensive_keystone_1", "Подогнанная броня");
		addSkill("hunter_offensive_keystone_1", "Снайпер");
		addSkill("hunter_mastery", "Охотник за сокровищами");
		addSkill("hunter_gateway", "Пространственные врата", "Соединяются с пространственными вратами");
		// ranger skills
		addSkill("hunter_subclass_1", "Рейнджер");
		addSkill("hunter_subclass_1_mastery", "Неуловимость");
		addSkill("hunter_subclass_1_crafting_notable_1", "Мягкие подошвы");
		addSkill("hunter_subclass_1_offensive_notable_1", "Без следа");
		addSkill("hunter_subclass_special", "Кровожадный клинок");
		// fletcher skills
		addSkill("hunter_subclass_2", "Стрельник");
		addSkill("hunter_subclass_2_mastery", "Бездонный колчан");
		addSkill("hunter_subclass_2_crafting_notable_1", "Облегчённые стрелы");
		addSkill("hunter_subclass_2_life_notable_1", "Уверенность");
		// cook skills
		addSkill("cook_class", "Повар");
		addSkill("cook_crafting_notable_1", "Фрукт амброзии");
		addSkill("cook_defensive_notable_1", "Толстые руки");
		addSkill("cook_offensive_notable_1", "Тяжелый удар");
		addSkill("cook_life_notable_1", "Здоровая диета");
		addSkill("cook_speed_notable_1", "Запас энергии");
		addSkill("cook_healing_notable_1", "Перекус");
		addSkill("cook_crit_notable_1", "Острое блюдо");
		addSkill("cook_defensive_crafting_keystone_1", "Вегетерианство");
		addSkill("cook_offensive_crafting_keystone_1", "Горячая еда");
		addSkill("cook_defensive_keystone_1", "Лишний вес");
		addSkill("cook_offensive_keystone_1", "Жирное тело");
		addSkill("cook_mastery", "Большие порции");
		addSkill("cook_gateway", "Духовные врата", "Соединяются с духовными вратами");
		// berserker skills
		addSkill("cook_subclass_1", "Берсерк");
		addSkill("cook_subclass_1_mastery", "Кровавая пелена");
		addSkill("cook_subclass_1_crafting_notable_1", "Топор палача");
		addSkill("cook_subclass_1_offensive_notable_1", "Грань смерти");
		addSkill("cook_subclass_special", "Изучение останков");
		// fisherman skills
		addSkill("cook_subclass_2", "Рыбак");
		addSkill("cook_subclass_2_mastery", "Дар моря");
		addSkill("cook_subclass_2_crafting_notable_1", "Опытный рыбак");
		addSkill("cook_subclass_2_life_notable_1", "Везучий рыбак");
		// alchemist skills
		addSkill("alchemist_class", "Алхимик");
		addSkill("alchemist_crafting_notable_1", "Эксперимент");
		addSkill("alchemist_defensive_notable_1", "Улучшенные рефлексы");
		addSkill("alchemist_offensive_notable_1", "Жестокость");
		addSkill("alchemist_life_notable_1", "Зависимость");
		addSkill("alchemist_speed_notable_1", "Адреналин");
		addSkill("alchemist_healing_notable_1", "Зелье крови");
		addSkill("alchemist_crit_notable_1", "Интоксикация");
		addSkill("alchemist_defensive_crafting_keystone_1", "Чистота");
		addSkill("alchemist_offensive_crafting_keystone_1", "Стойкий токсин");
		addSkill("alchemist_defensive_keystone_1", "Мутация");
		addSkill("alchemist_offensive_keystone_1", "Передозировка");
		addSkill("alchemist_mastery", "Секретный ингредиент");
		addSkill("alchemist_gateway", "Духовные врата", "Соединяются с духовными вратами");
		// assassin skills
		addSkill("alchemist_subclass_1", "Ассасин");
		addSkill("alchemist_subclass_1_mastery", "Потрошение");
		addSkill("alchemist_subclass_1_crafting_notable_1", "Отравитель");
		addSkill("alchemist_subclass_1_offensive_notable_1", "Удар в спину");
		addSkill("alchemist_subclass_special", "Шипастые кольца");
		// healer skills
		addSkill("alchemist_subclass_2", "Лекарь");
		addSkill("alchemist_subclass_2_mastery", "Самолечение");
		addSkill("alchemist_subclass_2_crafting_notable_1", "Панацея");
		addSkill("alchemist_subclass_2_life_notable_1", "Крепкое здоровье");
		// enchanter skills
		addSkill("enchanter_class", "Зачарователь");
		addSkill("enchanter_crafting_notable_1", "Магический поток");
		addSkill("enchanter_defensive_notable_1", "Рунический барьер");
		addSkill("enchanter_offensive_notable_1", "Руническое лезвие");
		addSkill("enchanter_life_notable_1", "Жизнь из магии");
		addSkill("enchanter_speed_notable_1", "Оживленное оружие");
		addSkill("enchanter_healing_notable_1", "Энергетическая призма");
		addSkill("enchanter_crit_notable_1", "Жнец");
		addSkill("enchanter_defensive_crafting_keystone_1", "Руна защиты");
		addSkill("enchanter_offensive_crafting_keystone_1", "Руна разрушения");
		addSkill("enchanter_defensive_keystone_1", "Эгида");
		addSkill("enchanter_offensive_keystone_1", "Экскалибур");
		addSkill("enchanter_mastery", "Сокрытое знание");
		addSkill("enchanter_gateway", "Астральные врата", "Соединяются с астральными вратами");
		// arsonist skills
		addSkill("enchanter_subclass_1", "Поджигатель");
		addSkill("enchanter_subclass_1_mastery", "Испепеление");
		addSkill("enchanter_subclass_1_crafting_notable_1", "Пламенный клинок");
		addSkill("enchanter_subclass_1_offensive_notable_1", "Опалённая плоть");
		addSkill("enchanter_subclass_special", "Адские колчаны");
		// scholar skills
		addSkill("enchanter_subclass_2", "Учёный");
		addSkill("enchanter_subclass_2_mastery", "Изучение звёзд");
		addSkill("enchanter_subclass_2_crafting_notable_1", "Изучение минералов");
		addSkill("enchanter_subclass_2_life_notable_1", "Медитация");
		// blacksmith skills
		addSkill("blacksmith_class", "Кузнец");
		addSkill("blacksmith_crafting_notable_1", "Создатель щитов");
		addSkill("blacksmith_defensive_notable_1", "Железный панцирь");
		addSkill("blacksmith_offensive_notable_1", "Противовес");
		addSkill("blacksmith_life_notable_1", "Уверенная стойка");
		addSkill("blacksmith_speed_notable_1", "Амбидекстр");
		addSkill("blacksmith_healing_notable_1", "Укрытие");
		addSkill("blacksmith_crit_notable_1", "Столкновение");
		addSkill("blacksmith_defensive_crafting_keystone_1", "Крепкий металл");
		addSkill("blacksmith_offensive_crafting_keystone_1", "Лёгкий сплав");
		addSkill("blacksmith_defensive_keystone_1", "Живая крепость");
		addSkill("blacksmith_offensive_keystone_1", "Колосс");
		addSkill("blacksmith_mastery", "Чёрная сталь");
		addSkill("blacksmith_gateway", "Пространственные врата", "Соединяются с пространственными вратами");
		// soldier skills
		addSkill("blacksmith_subclass_1", "Солдат");
		addSkill("blacksmith_subclass_1_mastery", "Военная подготовки");
		addSkill("blacksmith_subclass_1_crafting_notable_1", "Заточка");
		addSkill("blacksmith_subclass_1_offensive_notable_1", "Опытный боец");
		addSkill("blacksmith_subclass_special", "Жадные клинки");
		// artisan skills
		addSkill("blacksmith_subclass_2", "Ремесленник");
		addSkill("blacksmith_subclass_2_mastery", "Мастер на все руки");
		addSkill("blacksmith_subclass_2_crafting_notable_1", "Облегчённые щиты");
		addSkill("blacksmith_subclass_2_life_notable_1", "Закалено в крови");
		// miner skills
		addSkill("miner_class", "Шахтёр");
		addSkill("miner_crafting_notable_1", "Раскопки");
		addSkill("miner_defensive_notable_1", "Защитная каска");
		addSkill("miner_offensive_notable_1", "Полировка");
		addSkill("miner_life_notable_1", "Кристалл жизни");
		addSkill("miner_speed_notable_1", "Лёгкие кристаллы");
		addSkill("miner_healing_notable_1", "Исцеляющий кристалл");
		addSkill("miner_crit_notable_1", "Проклятый камень");
		addSkill("miner_defensive_crafting_keystone_1", "Куллинан");
		addSkill("miner_offensive_crafting_keystone_1", "Звезда Фуры");
		addSkill("miner_defensive_keystone_1", "Каменное сердце");
		addSkill("miner_offensive_keystone_1", "Драгоценное оружие");
		addSkill("miner_mastery", "Алчность");
		addSkill("miner_gateway", "Астральные врата", "Соединяются с астральными вратами");
		// explorer skills
		addSkill("miner_subclass_1", "Исследователь");
		addSkill("miner_subclass_1_mastery", "Первооткрыватель");
		addSkill("miner_subclass_1_crafting_notable_1", "Сапоги скороходы");
		addSkill("miner_subclass_1_offensive_notable_1", "Спешка");
		addSkill("miner_subclass_special", "Декоративные ботинки");
		// jeweler skills
		addSkill("miner_subclass_2", "Ювелир");
		addSkill("miner_subclass_2_mastery", "Аристократ");
		addSkill("miner_subclass_2_crafting_notable_1", "Осколки звёзд");
		addSkill("miner_subclass_2_life_notable_1", "Оберег");
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
		add("gem_class_format", "• %s: ");
		add("gem_class.helmet", "Шлемы");
		add("gem_class.chestplate", "Нагрудники");
		add("gem_class.boots", "Ботинки");
		add("gem_class.other_armor", "Другая броня");
		add("gem_class.weapon", "Оружие");
		add("gem_class.shield", "Щиты");
		add("gem_class.bow", "Луки");
		add("gem_class.melee_weapon", "Оружие ближнего боя");
		add("gem_class.armor_and_shield", "Броню и щиты");
		add("gem_class.anything", "Что угодно");
		add("gem_class.armor", "Броню");
		add("gem_class.pickaxe", "Кирки");
		add("gem_class.ring", "Кольца");
		add("gem_class.necklace", "Ожерелья");
		add("gem_class.jewelry", "Бижутерию");
		add("gem.tooltip", "• Можно вставить в предметы с гнёздами");
		addTooltip(PSTItems.VACUCITE.get(), "Уничтожает самоцветы в предмете");
		addTooltip(PSTItems.IRISCITE.get(), "Результат непредсказуем");
		// food info
		add("food.bonus.healing", "• Восстанавливает %s здоровья");
		add("food.bonus.damage", "• %s урона");
		add("food.bonus.crit_damage", "• %s критического урона");
		add("food.bonus.life_regeneration", "• %s регенерации здоровья");
		// weapon info
		add("weapon.poisoned", "Отравлено:");
		// quiver info
		add("quiver.capacity", "• Вмещает до %s стрел");
		add("quiver.contents", "• Внутри: %s");
		// items
		add(PSTItems.ADAMITE.get(), "Адамит");
		add(PSTItems.CITRINE.get(), "Цитрин");
		add(PSTItems.IRISCITE.get(), "Ирисцит");
		add(PSTItems.MOONSTONE.get(), "Лунный камень");
		add(PSTItems.ONYX.get(), "Оникс");
		add(PSTItems.RUBY.get(), "Рубин");
		add(PSTItems.VACUCITE.get(), "Вакуцит");
		add(PSTItems.JADE.get(), "Нефрит");
		add(PSTItems.SAPPHIRE.get(), "Сапфир");
		add(PSTItems.TOURMALINE.get(), "Турмалин");
		add(PSTItems.TURQUOISE.get(), "Бирюза");
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
		add(PSTItems.QUIVER.get(), "Колчан");
		add(PSTItems.ARMORED_QUIVER.get(), "Бронированный колчан");
		add(PSTItems.DIAMOND_QUIVER.get(), "Алмазный колчан");
		add(PSTItems.EXPLOSIVE_QUIVER.get(), "Взрывной колчан");
		add(PSTItems.FIERY_QUIVER.get(), "Огненный колчан");
		add(PSTItems.GILDED_QUIVER.get(), "Позолоченный колчан");
		add(PSTItems.HEALING_QUIVER.get(), "Исцеляющий колчан");
		add(PSTItems.TOXIC_QUIVER.get(), "Токсичный колчан");
		add(PSTItems.SILENT_QUIVER.get(), "Бесшумный колчан");
		add(PSTItems.BONE_QUIVER.get(), "Костяной колчан");
		addTooltip(PSTItems.WISDOM_SCROLL.get(), "Дарует одно очко пассивных умений");
		addTooltip(PSTItems.AMNESIA_SCROLL.get(), "Сбрасывает ваше древо пассивных умений");
		addWarning(PSTItems.AMNESIA_SCROLL.get(), "%d%% очков умений будут потеряны");
		// attributes
		add(PSTAttributes.LIFE_REGENERATION.get(), "Регенерация здоровья");
		add(PSTAttributes.LIFE_PER_HIT.get(), "Здоровье за удар");
		add(PSTAttributes.PROJECTILE_CRIT_DAMAGE.get(), "Критический урон снарядов");
		add(PSTAttributes.PROJECTILE_DAMAGE.get(), "Урон снарядов");
		add(PSTAttributes.EVASION.get(), "Уклонение");
		add(PSTAttributes.BLOCK_CHANCE.get(), "Шанс блока");
		add(PSTAttributes.LIFE_ON_BLOCK.get(), "Здоровье при блоке");
		add(PSTAttributes.CRIT_CHANCE.get(), "Шанс критического удара");
		add(PSTAttributes.DOUBLE_LOOT_CHANCE.get(), "Шанс удвоенной добычи");
		add(PSTAttributes.TRIPLE_LOOT_CHANCE.get(), "Шанс утроенной добычи");
		add(PSTAttributes.CRAFTED_ARMOR_EVASION.get(), "Уклонение создаваемой брони");
		add(PSTAttributes.CRAFTED_RANGED_WEAPON_SOCKETS.get(), "Гнёзда создаваемого оружия дальнего боя");
		add(PSTAttributes.CRAFTED_HELMETS_SOCKETS.get(), "Гнёзда создаваемых шлемов");
		add(PSTAttributes.CRAFTED_RANGED_WEAPON_ATTACK_SPEED.get(), "Скорость атаки создаваемого оружия дальнего боя");
		add(PSTAttributes.EVASION_CHANCE_WHEN_WOUNDED.get(), "Уклонение если вы искалечены");
		add(PSTAttributes.ARMOR_PER_EVASION.get(), "Броня за шанс уклонения");
		add(PSTAttributes.DAMAGE_PER_DISTANCE_TO_ENEMY.get(), "Урон за расстояние до цели");
		add(PSTAttributes.LIFE_PER_PROJECTILE_HIT.get(), "Здоровье за попадание снарядом");
		add(PSTAttributes.MAXIMUM_LIFE_PER_EVASION.get(), "Максимум здоровья за уклонение");
		add(PSTAttributes.ATTACK_SPEED_WITH_RANGED_WEAPON.get(), "Скорость атаки с оружием дальнего боя");
		add(PSTAttributes.CHANCE_TO_RETRIEVE_ARROWS.get(), "Шанс вернуть стрелы");
		add(PSTAttributes.PROJECTILE_CRIT_CHANCE.get(), "Шанс критического удара снарядов");
		add(PSTAttributes.MAXIMUM_LIFE_UNDER_POTION_EFFECT.get(), "Максимум здоровя под эффектом зелья");
		add(PSTAttributes.ATTACK_SPEED_UNDER_POTION_EFFECT.get(), "Скорость атаки под эффектом зелья");
		add(PSTAttributes.BREWED_POTIONS_DURATION.get(), "Длительность создаваемых зелий");
		add(PSTAttributes.BREWED_POTIONS_STRENGTH.get(), "Шанс создать усиленное зелье");
		add(PSTAttributes.BREWED_BENEFICIAL_POTIONS_STRENGTH.get(), "Шанс создать усиленное благотворное зелье");
		add(PSTAttributes.BREWED_HARMFUL_POTIONS_STRENGTH.get(), "Шанс создать усиленное пагубное зелье");
		add(PSTAttributes.DAMAGE_AGAINST_POISONED.get(), "Урон по отравленным врагам");
		add(PSTAttributes.CRIT_CHANCE_AGAINST_POISONED.get(), "Шанс критического удара по отравленным");
		add(PSTAttributes.EVASION_UNDER_POTION_EFFECT.get(), "Шанс уклонения под эффектом зелья");
		add(PSTAttributes.CAN_POISON_WEAPONS.get(), "Вы можете отравлять оружие ближнего боя");
		add(PSTAttributes.CAN_MIX_POTIONS.get(), "Вы можете смешивать зелья");
		add(PSTAttributes.EVASION_PER_POTION_EFFECT.get(), "Шанс уклонения за эффект зелья");
		add(PSTAttributes.DAMAGE_PER_POTION_EFFECT.get(), "Урон за эффект зелья");
		add(PSTAttributes.LIFE_PER_HIT_UNDER_POTION_EFFECT.get(), "Здоровье за удар под эффектом зелья");
		add(PSTAttributes.CRIT_DAMAGE_AGAINST_POISONED.get(), "Критический урон по отравленным врагам");
		add(PSTAttributes.GEM_POWER_IN_ARMOR.get(), "Сила самоцветов в броне");
		add(PSTAttributes.GEM_POWER_IN_WEAPON.get(), "Сила самоцветов в оружии");
		add(PSTAttributes.MAXIMUM_WEAPON_SOCKETS.get(), "Гнёзда в оружии");
		add(PSTAttributes.MAXIMUM_CHESTPLATE_SOCKETS.get(), "Гнёзда в нагрудниках");
		add(PSTAttributes.MAXIMUM_EQUIPMENT_SOCKETS.get(), "Гнёзда в экипировке");
		add(PSTAttributes.ARMOR_PER_GEM_IN_HELMET.get(), "Броня за самоцвет в шлеме");
		add(PSTAttributes.ARMOR_PER_GEM_IN_CHESTPLATE.get(), "Броня за самоцвет в нагруднике");
		add(PSTAttributes.CRIT_CHANCE_PER_GEM_IN_WEAPON.get(), "Шанс критического удара за самоцвет в оружии");
		add(PSTAttributes.MINING_SPEED.get(), "Скорость добычи с киркой");
		add(PSTAttributes.MAXIMUM_LIFE_PER_GEM_IN_HELMET.get(), "Максимум здоровья за самоцвет в шлеме");
		add(PSTAttributes.MAXIMUM_LIFE_PER_GEM_IN_ARMOR.get(), "Максимум здоровья за самоцвет в броне");
		add(PSTAttributes.CRIT_DAMAGE_PER_GEM_IN_WEAPON.get(), "Критический урон за самоцвет в оружии");
		add(PSTAttributes.GEM_DROP_CHANCE.get(), "Шанс найти самоцвет в руде");
		add(PSTAttributes.LIFE_REGENERATION_PER_GEM_IN_HELMET.get(), "Регенерация здоровья за самоцвет в шлеме");
		add(PSTAttributes.CRAFTED_ARMOR_DEFENCE.get(), "Защита создаваемой брони");
		add(PSTAttributes.CRAFTED_SHIELDS_ARMOR.get(), "Броня создаваемых щитов");
		add(PSTAttributes.LIFE_REGENERATION_WITH_SHIELD.get(), "Регенерация здоровья со щитом");
		add(PSTAttributes.MAXIMUM_LIFE_PER_BOOTS_ARMOR.get(), "Максимум здоровья за броню на ботинках");
		add(PSTAttributes.ATTACK_SPEED_WITH_SHIELD.get(), "Скорость атаки со щитом");
		add(PSTAttributes.CRAFTED_MELEE_WEAPON_DAMAGE_BONUS.get(), "Урон создаваемого оружия ближнего боя");
		add(PSTAttributes.CRAFTED_MELEE_WEAPON_ATTACK_SPEED.get(), "Скорость атаки создаваемого оружия ближнего боя");
		add(PSTAttributes.CRIT_DAMAGE_WITH_SHIELD.get(), "Критический урон со щитом");
		add(PSTAttributes.CRIT_CHANCE_WITH_SHIELD.get(), "Шанс критического удара со щитом");
		add(PSTAttributes.DAMAGE_WITH_SHIELD.get(), "Урон со щитом");
		add(PSTAttributes.CHANCE_TO_CRAFT_TOUGHER_ARMOR.get(), "Шанс создать более твёрдую броню");
		add(PSTAttributes.ATTACK_DAMAGE_PER_ARMOR.get(), "Урон в ближнем бою за единицу брони");
		add(PSTAttributes.CHESTPLATE_ARMOR.get(), "Броня нагрудника");
		add(PSTAttributes.ENCHANTMENT_LEVEL_REQUIREMENT.get(), "Требование к уровню для зачарования");
		add(PSTAttributes.ARMOR_ENCHANTMENT_POWER.get(), "Сила накладываемых зачарований брони");
		add(PSTAttributes.WEAPON_ENCHANTMENT_POWER.get(), "Сила накладываемых зачарований оружия");
		add(PSTAttributes.DAMAGE_WITH_ENCHANTED_WEAPON.get(), "Урон с зачарованным оружием");
		add(PSTAttributes.MAXIMUM_LIFE_WITH_ENCHANTED_ITEM.get(), "Максимум здоровья с зачарованным предметом");
		add(PSTAttributes.MAXIMUM_LIFE_PER_ARMOR_ENCHANTMENT.get(), "Максимум здоровья за каждое зачарование брони");
		add(PSTAttributes.ATTACK_SPEED_WITH_ENCHANTED_WEAPON.get(), "Скорость атаки с зачарованным оружием");
		add(PSTAttributes.ENCHANTMENT_POWER.get(), "Сила накладываемых зачарований");
		add(PSTAttributes.CRIT_CHANCE_WITH_ENCHANTED_WEAPON.get(), "Шанс критического удара с зачарованным оружием");
		add(PSTAttributes.CRIT_DAMAGE_PER_WEAPON_ENCHANTMENT.get(), "Критический урон за каждое зачарование на оружии");
		add(PSTAttributes.LIFE_ON_BLOCK_PER_SHIELD_ENCHANTMENT.get(), "Здоровье при блоке за каждое зачарование на щите");
		add(PSTAttributes.BLOCK_CHANCE_PER_SHIELD_ENCHANTMENT.get(), "Шанс блока за каждое зачарование на щите");
		add(PSTAttributes.DAMAGE_PER_WEAPON_ENCHANTMENT_LEVEL.get(), "Урон за уровень зачарования на оружии");
		add(PSTAttributes.BLOCK_CHANCE_WITH_ENCHANTED_SHIELD.get(), "Шанс блока с зачарованным щитом");
		add(PSTAttributes.FREE_ENCHANTMENT_CHANCE.get(), "Шанс бесплатного зачарования");
		add(PSTAttributes.COOKED_FOOD_SATURATION.get(), "Насыщение создаваемой еды");
		add(PSTAttributes.COOKED_FOOD_LIFE_REGENERATION.get(), "Регенерация здоровья создаваемой еды");
		add(PSTAttributes.COOKED_FOOD_HEALING_PER_SATURATION.get(), "Лечение создаваемой еды за единицу насыщения");
		add(PSTAttributes.COOKED_FOOD_DAMAGE_PER_SATURATION.get(), "Урон создаваемой еды за единицу насыщения");
		add(PSTAttributes.DAMAGE_IF_NOT_HUNGRY.get(), "Урон если вы не голодны");
		add(PSTAttributes.BLOCK_CHANCE_IF_NOT_HUNGRY.get(), "Шанс блока если вы не голодны");
		add(PSTAttributes.LIFE_ON_BLOCK_IF_NOT_HUNGRY.get(), "Здоровье при блоке если вы не голодны");
		add(PSTAttributes.CRIT_CHANCE_IF_NOT_HUNGRY.get(), "Шанс критического удара если вы не голодны");
		add(PSTAttributes.MAXIMUM_LIFE_IF_NOT_HUNGRY.get(), "Максимум здоровья если вы не голодны");
		add(PSTAttributes.MAXIMUM_LIFE_PER_SATISFIED_HUNGER.get(), "Максимум здоровья за единицу утолённого голода");
		add(PSTAttributes.ATTACK_SPEED_IF_NOT_HUNGRY.get(), "Скорость атаки если вы не голодны");
		add(PSTAttributes.BLOCK_CHANCE_PER_SATISFIED_HUNGER.get(), "Шанс блока за единицу утолённого голода");
		add(PSTAttributes.DAMAGE_PER_SATISFIED_HUNGER.get(), "Урон за единицу утолённого голода");
		add(PSTAttributes.COOKED_FOOD_CRIT_DAMAGE_PER_SATURATION.get(), "Критический урон создаваемой еды за единицу насыщения");
		add(PSTAttributes.CRIT_DAMAGE_PER_SATISFIED_HUNGER.get(), "Критический урон за единицу утолённого голода");
		add(PSTAttributes.CRAFTED_EQUIPMENT_DURABILITY.get(), "Прочность создаваемых предметов");
		add(PSTAttributes.DAMAGE_WITH_GEM_IN_WEAPON.get(), "Урон с самоцветом в оружии");
		add(PSTAttributes.DAMAGE_PER_GEM_IN_WEAPON.get(), "Урон за самоцвет в оружии");
		add(PSTAttributes.ATTACK_SPEED_WITH_GEM_IN_WEAPON.get(), "Скорость атаки с самоцветом в оружии");
		add(PSTAttributes.ATTACK_SPEED_PER_GEM_IN_WEAPON.get(), "Скорость атаки за самоцвет в оружии");
		add(PSTAttributes.DAMAGE_UNDER_POTION_EFFECT.get(), "Урон под эффектом зелья");
		add(PSTAttributes.BREWED_POISONS_STRENGTH.get(), "Шанс создать усиленный яд");
		add(PSTAttributes.BREWED_HEALING_POTIONS_STRENGTH.get(), "Шанс создать усиленное лечебное зелье");
		add(PSTAttributes.BREWED_HARMFUL_POTIONS_DURATION.get(), "Длительность создаваемых пагубных зелий");
		add(PSTAttributes.BREWED_BENEFICIAL_POTIONS_DURATION.get(), "Длительность создаваемых благотворных зелий");
		add(PSTAttributes.INCOMING_HEALING.get(), "Получаемое лечение");
		add(PSTAttributes.CRIT_DAMAGE.get(), "Критический урон");
		add(PSTAttributes.EXPERIENCE_PER_HOUR.get(), "Опыт в час");
		add(PSTAttributes.CRAFTED_BOOTS_SOCKETS.get(), "Гнезда в создаваемых ботинках");
		add(PSTAttributes.MAXIMUM_RING_SOCKETS.get(), "Гнезда в кольцах");
		add(PSTAttributes.GEM_POWER_IN_JEWELRY.get(), "Сила самоцветов в бижутерии");
		add(PSTAttributes.MAXIMUM_LIFE_PER_EQUIPPED_JEWELRY.get(), "Максимальное здоровье за надетую бижутерию");
		add(PSTAttributes.CRAFTED_RINGS_CRITICAL_DAMAGE.get(), "Критический урон создаваемых колец");
		add(PSTAttributes.CRAFTED_NECKLACES_MAXIMUM_LIFE.get(), "Максимальное здоровье создаваемых ожерелий");
		add(CuriosHelper.getOrCreateSlotAttribute("ring"), "Слоты колец");
		add(PSTAttributes.EXPERIENCE_FROM_MOBS.get(), "Опыт от убитых существ");
		add(PSTAttributes.EXPERIENCE_FROM_ORE.get(), "Опыт от добытой руды");
		add(PSTAttributes.DAMAGE_IF_DAMAGED.get(), "Урон если вы ранены");
		add(PSTAttributes.DAMAGE_IF_WOUNDED.get(), "Урон если вы искалечены");
		add(PSTAttributes.CRAFTED_AXES_CRIT_CHANCE.get(), "Шанс критического удара создаваемых топоров");
		add(PSTAttributes.CRIT_CHANCE_IF_WOUNDED.get(), "Шанс критического удара если вы искалечены");
		add(PSTAttributes.ATTACK_SPEED_IF_WOUNDED.get(), "Скорость атаки если вы искалечены");
		add(PSTAttributes.LIFE_PER_HIT_IF_WOUNDED.get(), "Здоровье за удар если вы искалечены");
		add(PSTAttributes.CRAFTED_BOOTS_MOVEMENT_SPEED.get(), "Скорость передвижения создаваемых ботинок");
		add(PSTAttributes.DAMAGE_PER_DISTANCE_TO_SPAWN.get(), "Урон за расстояние до точки возрождения (максимум 50%)");
		add(PSTAttributes.CRAFTED_ARMOR_MAXIMUM_LIFE.get(), "Максимальное здоровье создаваемой брони");
		add(PSTAttributes.CRAFTED_SHIELDS_MAXIMUM_LIFE.get(), "Максимальное здоровье создаваемых щитов");
		add(PSTAttributes.CRAFTED_WEAPON_ATTACK_SPEED.get(), "Скорость атаки создаваемого оружия");
		add(PSTAttributes.CRAFTED_SHIELDS_BLOCK_CHANCE.get(), "Шанс блока создаваемых щитов");
		add(PSTAttributes.DAMAGE_AGAINST_BURNING.get(), "Урон по горящим врагам");
		add(PSTAttributes.CHANCE_TO_IGNITE.get(), "Шанс поджога");
		add(PSTAttributes.CRAFTED_WEAPON_CHANCE_TO_IGNITE.get(), "Шанс поджога создаваемого оружия");
		add(PSTAttributes.CRIT_CHANCE_AGAINST_BURNING.get(), "Шанс критического удара по горящим врагам");
		add(PSTAttributes.CRAFTED_QUIVERS_CHANCE_TO_IGNITE.get(), "Шанс поджога создаваемых колчанов");
		add(PSTAttributes.CRAFTED_QUIVERS_DAMAGE_AGAINST_BURNING.get(), "Урон по горящим врагам создаваемых колчанов");
		add(PSTAttributes.MAXIMUM_LIFE_PER_ARROW_IN_QUIVER.get(), "Максимальное здоровье за стрелу в колчане");
		add(PSTAttributes.CRAFTED_QUIVERS_CAPACITY.get(), "Вместимость создаваемых колчанов");
		add(PSTAttributes.CRAFTED_QUIVERS_MAXIMUM_LIFE.get(), "Максимальное здоровье создаваемых колчанов");
		add(PSTAttributes.STEALTH.get(), "Скрытность");
		add(PSTAttributes.CRAFTED_ARMOR_STEALTH.get(), "Скрытность создаваемой брони");
		add(PSTAttributes.JUMP_HEIGHT.get(), "Высота прыжка");
		add(PSTAttributes.CRAFTED_BOOTS_STEALTH.get(), "Скрытность создаваемых ботинок");
		add(PSTAttributes.MELEE_DAMAGE.get(), "Урон в ближнем бою");
		add(PSTAttributes.MELEE_CRIT_DAMAGE.get(), "Критический урон в ближнем бою");
		add(PSTAttributes.CRAFTED_MELEE_WEAPON_CRIT_CHANCE.get(), "Шанс критического удара создаваемого оружия ближнего боя");
		add(PSTAttributes.CRAFTED_WEAPON_DOUBLE_LOOT_CHANCE.get(), "Шанс удвоенной добычи создаваемого оружия");
		add(PSTAttributes.DOUBLE_FISHING_LOOT_CHANCE.get(), "Шанс удвоенной добычи от рыбалки");
		add(PSTAttributes.EXPERIENCE_FROM_FISHING.get(), "Опыт от рыбалки");
		add(PSTAttributes.LUCK_WHILE_FISHING.get(), "Удача во время рыбалки");
		add(PSTAttributes.CRAFTED_WEAPON_LIFE_PER_HIT.get(), "Здоровье за удар создаваемого оружия");
		add(PSTAttributes.CRAFTED_WEAPON_DAMAGE_AGAINST_BURNING.get(), "Урон по горящим врагам создаваемого оружия");
		add(PSTAttributes.CHANCE_TO_EXPLODE_ENEMY.get(), "Шанс взорвать врага");
		add(PSTAttributes.CRAFTED_QUIVERS_CHANCE_TO_RETRIEVE_ARROWS.get(), "Шанс вернуть стрелы создаваемых колчанов");
		add(PSTAttributes.EQUIPMENT_REPAIR_EFFICIENCY.get(), "Эффективность починки экипировки");
		// effects
		add(PSTEffects.CRIT_DAMAGE_BONUS.get(), "Критический урон");
		add(PSTEffects.DAMAGE_BONUS.get(), "Урон");
		add(PSTEffects.LIFE_REGENERATION_BONUS.get(), "Регенерация здоровья");
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
		// apotheosis compatibility
		add("item.apotheosis.gem.skilltree:ruby", "Рубин");
		add("item.apotheosis.gem.skilltree:onyx", "Оникс");
		add("item.apotheosis.gem.skilltree:moonstone", "Лунный камень");
		add("item.apotheosis.gem.skilltree:opal", "Опал");
		add("item.apotheosis.gem.skilltree:adamite", "Адамит");
		add("item.apotheosis.gem.skilltree:citrine", "Цитрин");
		add("item.apotheosis.gem.skilltree:jade", "Нефрит");
		add("item.apotheosis.gem.skilltree:sapphire", "Сапфир");
		add("item.apotheosis.gem.skilltree:tourmaline", "Турмалин");
		add("item.apotheosis.gem.skilltree:turquoise", "Бирюза");
		add("text.apotheosis.category.ring.plural", "Колца");
		add("text.apotheosis.category.necklace.plural", "Ожерелья");
		// jei info
		add("skilltree.jei.gem_info",
				"Самоцветы можно вставлять в предметы с гнёздами на кузнечном столе. Выпадают из любой руды с небольшим шансом (требуется инструмент без шёлкового касания).");
		// curios info
		add("curios.identifier.quiver", "Колчан");
		add("curios.modifiers.quiver", "Когда надет:");
		// tabs
		add("itemGroup.skilltree", "Passive Skill Tree");
	}

	private void addTooltip(Item item, String tooltip) {
		add(item.getDescriptionId() + ".tooltip", tooltip);
	}

	private void addWarning(Item item, String tooltip) {
		add(item.getDescriptionId() + ".warning", tooltip);
	}

	private void add(Attribute attribute, String name) {
		add(attribute.getDescriptionId(), name);
	}

	private void addSkill(String skillName, String name, @Nullable String description) {
		var skillId = "skill." + SkillTreeMod.MOD_ID + "." + skillName;
		add(skillId + ".name", name);
		if (description != null) {
			add(skillId + ".description", description);
		}
	}

	private void addSkill(String skillName, String name) {
		addSkill(skillName, name, null);
	}

	private void addMixture(String name, MobEffect... effects) {
		addMixture("Микстура " + name, "potion", effects);
		addMixture("Взрывная микстура " + name, "splash_potion", effects);
		addMixture("Туманная микстура " + name, "lingering_potion", effects);
	}

	protected void addMixture(String name, String potionType, MobEffect... effects) {
		var potionName = new StringBuilder("item.minecraft." + potionType + ".mixture");
		Arrays.asList(effects).stream().map(MobEffect::getDescriptionId).map(id -> id.replaceAll("effect.", ""))
				.forEach(id -> potionName.append("." + id));
		add(potionName.toString(), name);
	}
}
