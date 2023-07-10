package daripher.skilltree.datagen.translation;

import java.util.Arrays;

import javax.annotation.Nullable;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.init.SkillTreeAttributes;
import daripher.skilltree.init.SkillTreeEffects;
import daripher.skilltree.init.SkillTreeItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.data.LanguageProvider;

public class ModRussianTranslationProvider extends LanguageProvider {
	public ModRussianTranslationProvider(DataGenerator gen) {
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
		addSkill("hunter_offensive_notable_2", "Стрельба с луком");
		addSkill("hunter_offensive_notable_3", "Волшебные стрелы");
		addSkill("hunter_offensive_notable_4", "Хозяин моря");
		addSkill("hunter_offensive_notable_5", "Зазубренные наконечники");
		addSkill("hunter_offensive_notable_6", "Солдатская подготовка");
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
		addSkill("cook_offensive_notable_2", "Здоровые мышцы");
		addSkill("cook_offensive_notable_3", "Мясник");
		addSkill("cook_offensive_notable_4", "Рыбалка с копьём");
		addSkill("cook_offensive_notable_5", "Рубка мяса");
		addSkill("cook_offensive_notable_6", "Здоровое зрение");
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
		addSkill("alchemist_offensive_crafting_keystone_1", "Отравленное лезвие");
		addSkill("alchemist_defensive_keystone_1", "Мутация");
		addSkill("alchemist_offensive_keystone_1", "Передозировка");
		addSkill("alchemist_mastery", "Секретный ингредиент");
		addSkill("alchemist_offensive_notable_2", "Ассасин");
		addSkill("alchemist_offensive_notable_3", "Рейнджер");
		addSkill("alchemist_offensive_notable_4", "Ядовитый газ");
		addSkill("alchemist_offensive_notable_5", "Зелье Берсерка");
		addSkill("alchemist_offensive_notable_6", "Токсичное покрытие");
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
		addSkill("enchanter_offensive_notable_2", "Рунические мечи");
		addSkill("enchanter_offensive_notable_3", "Рунические луки");
		addSkill("enchanter_offensive_notable_4", "Рунические трезубцы");
		addSkill("enchanter_offensive_notable_5", "Рунические арбалеты");
		addSkill("enchanter_offensive_notable_6", "Рунические топоры");
		// blacksmith skills
		addSkill("blacksmith_class", "Кузнец");
		addSkill("blacksmith_crafting_notable_1", "Создатель щитов");
		addSkill("blacksmith_defensive_notable_1", "Железный панцирь");
		addSkill("blacksmith_offensive_notable_1", "Противовес");
		addSkill("blacksmith_life_notable_1", "Уверенная стойка");
		addSkill("blacksmith_speed_notable_1", "Амбидекстр");
		addSkill("blacksmith_healing_notable_1", "Укрытие");
		addSkill("blacksmith_crit_notable_1", "Столкновение");
		addSkill("blacksmith_defensive_crafting_keystone_1", "Драгоценный металл");
		addSkill("blacksmith_offensive_crafting_keystone_1", "Лёгкий сплав");
		addSkill("blacksmith_defensive_keystone_1", "Живая крепость");
		addSkill("blacksmith_offensive_keystone_1", "Колосс");
		addSkill("blacksmith_mastery", "Чёрная сталь");
		addSkill("blacksmith_offensive_notable_2", "Солдат");
		addSkill("blacksmith_offensive_notable_3", "Страж");
		addSkill("blacksmith_offensive_notable_4", "Воин моря");
		addSkill("blacksmith_offensive_notable_5", "Пращник");
		addSkill("blacksmith_offensive_notable_6", "Викинг");
		// miner skills
		addSkill("miner_class", "Шахтёр");
		addSkill("miner_crafting_notable_1", "Раскопки");
		addSkill("miner_defensive_notable_1", "Защиткая каска");
		addSkill("miner_offensive_notable_1", "Полировка");
		addSkill("miner_life_notable_1", "Кристалл жизни");
		addSkill("miner_speed_notable_1", "Лёгкие кристаллы");
		addSkill("miner_healing_notable_1", "Исцеляющий кристалл");
		addSkill("miner_crit_notable_1", "Проклятый камень");
		addSkill("miner_defensive_crafting_keystone_1", "Куллинан");
		addSkill("miner_offensive_crafting_keystone_1", "Звезда Фуры");
		addSkill("miner_defensive_keystone_1", "Каменное сердце");
		addSkill("miner_offensive_keystone_1", "Драгоценное оружие");
		addSkill("miner_mastery", "Ювелир");
		addSkill("miner_offensive_notable_2", "Разнообразие");
		addSkill("miner_offensive_notable_3", "Драгоценные лезвия");
		addSkill("miner_offensive_notable_4", "Непостоянство");
		addSkill("miner_offensive_notable_5", "Драгоценные стрелы");
		addSkill("miner_offensive_notable_6", "Однообразие");
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
		add("gem.socket", "[Пустое гнездо]");
		add("gem.additional_socket", "• Имеет дополнительное гнездо.");
		add("gem.disabled", "Отключено с модулем приключений Apotheosis.");
		add("gem.slot.helmet", "• Шлемы: ");
		add("gem.slot.chestplate", "• Нагрудники: ");
		add("gem.slot.boots", "• Ботинки: ");
		add("gem.slot.other_armor", "• Другая броня: ");
		add("gem.slot.weapon", "• Оружие: ");
		add("gem.slot.shield", "• Щиты: ");
		add("gem.slot.bow", "• Луки: ");
		add("gem.slot.melee_weapon", "• Оружие ближнего боя: ");
		add("gem.slot.armor_and_shield", "• Броню и щиты: ");
		add("gem.slot.anything", "• Что угодно: ");
		add("gem.slot.armor", "• Броню: ");
		add("gem.slot.pickaxe", "• Кирки: ");
		add("gem.slots", "Вставляется в:");
		addTooltip(SkillTreeItems.VACUCITE.get(), "Поглощает самоцветы из предмета");
		addTooltip(SkillTreeItems.IRISCITE.get(), "Результат непредсказуем");
		// food info
		add("food.bonus.damage", "+%d%% Урон");
		add("food.bonus.crit_damage", "+%d%% Критический урон");
		add("food.bonus.life_regeneration", "+%s Регенерация здоровья");
		// weapon info
		add("weapon.poisoned", "Отравлено:");
		// items
		add(SkillTreeItems.ADAMITE.get(), "Адамит");
		add(SkillTreeItems.CITRINE.get(), "Цитрин");
		add(SkillTreeItems.IRISCITE.get(), "Ирисцит");
		add(SkillTreeItems.MOONSTONE.get(), "Лунный камень");
		add(SkillTreeItems.ONYX.get(), "Оникс");
		add(SkillTreeItems.OPAL.get(), "Опал");
		add(SkillTreeItems.RUBY.get(), "Рубин");
		add(SkillTreeItems.VACUCITE.get(), "Вакуцит");
		add(SkillTreeItems.WISDOM_SCROLL.get(), "Свиток мудрости");
		addTooltip(SkillTreeItems.WISDOM_SCROLL.get(), "Дарует одно очко пассивных умений");
		add(SkillTreeItems.AMNESIA_SCROLL.get(), "Свиток амнезии");
		addTooltip(SkillTreeItems.AMNESIA_SCROLL.get(), "Сбрасывает ваше древо пассивных умений");
		// attributes
		add(SkillTreeAttributes.LIFE_REGENERATION.get(), "Регенерация здоровья");
		add(SkillTreeAttributes.LIFE_PER_HIT.get(), "Здоровье за удар");
		add(SkillTreeAttributes.PROJECTILE_CRIT_DAMAGE.get(), "Критический урон снарядов");
		add(SkillTreeAttributes.PROJECTILE_DAMAGE.get(), "Урон снарядов");
		add(SkillTreeAttributes.EVASION_CHANCE.get(), "Шанс уклонения");
		add(SkillTreeAttributes.BLOCK_CHANCE.get(), "Шанс блока");
		add(SkillTreeAttributes.LIFE_ON_BLOCK.get(), "Здоровье при блоке");
		add(SkillTreeAttributes.CRIT_CHANCE.get(), "Шанс критического удара");
		add(SkillTreeAttributes.DOUBLE_LOOT_CHANCE.get(), "Шанс удвоенной добычи");
		add(SkillTreeAttributes.TRIPLE_LOOT_CHANCE.get(), "Шанс утроенной добычи");
		add(SkillTreeAttributes.CRAFTED_ARMOR_EVASION.get(), "Уклонение создаваемой брони");
		add(SkillTreeAttributes.CRAFTED_RANGED_WEAPON_ADDITIONAL_SOCKETS.get(), "Гнёзда создаваемого оружия дальнего боя");
		add(SkillTreeAttributes.CRAFTED_HELMETS_ADDITIONAL_GEMSTONE_SLOTS.get(), "Гнёзда создаваемых шлемов");
		add(SkillTreeAttributes.CRAFTED_RANGED_WEAPON_ATTACK_SPEED.get(), "Скорость атаки создаваемого оружия дальнего боя");
		add(SkillTreeAttributes.EVASION_CHANCE_WHEN_WOUNDED.get(), "Уклонение когда ранен");
		add(SkillTreeAttributes.ARMOR_PER_EVASION.get(), "Броня за шанс уклонения");
		add(SkillTreeAttributes.DAMAGE_PER_DISTANCE.get(), "Урон за расстояние до цели");
		add(SkillTreeAttributes.LIFE_PER_PROJECTILE_HIT.get(), "Здоровье за попадание снарядом");
		add(SkillTreeAttributes.MAXIMUM_LIFE_PER_EVASION.get(), "Максимум здоровья за уклонение");
		add(SkillTreeAttributes.ATTACK_SPEED_WITH_RANGED_WEAPON.get(), "Скорость атаки с оружием дальнего боя");
		add(SkillTreeAttributes.CHANCE_TO_RETRIEVE_ARROWS.get(), "Шанс вернуть стрелу");
		add(SkillTreeAttributes.PROJECTILE_CRIT_CHANCE.get(), "Шанс критического удара снарядов");
		add(SkillTreeAttributes.MAXIMUM_LIFE_UNDER_POTION_EFFECT.get(), "Максимум здоровя под эффектом зелья");
		add(SkillTreeAttributes.ATTACK_SPEED_UNDER_POTION_EFFECT.get(), "Скорость атаки под эффектом зелья");
		add(SkillTreeAttributes.BREWED_POTIONS_DURATION.get(), "Длительность создаваемых зелий");
		add(SkillTreeAttributes.CHANCE_TO_BREW_STRONGER_POTION.get(), "Шанс создать усиленное зелье");
		add(SkillTreeAttributes.CHANCE_TO_BREW_STRONGER_BENEFICIAL_POTION.get(), "Шанс создать усиленное благотворное зелье");
		add(SkillTreeAttributes.CHANCE_TO_BREW_STRONGER_HARMFUL_POTION.get(), "Шанс создать усиленный яд");
		add(SkillTreeAttributes.DAMAGE_AGAINST_POISONED.get(), "Урон по отравленным");
		add(SkillTreeAttributes.CRIT_CHANCE_AGAINST_POISONED.get(), "Шанс критического удара по отравленным");
		add(SkillTreeAttributes.EVASION_UNDER_POTION_EFFECT.get(), "Шанс уклонения под эффектом зелья");
		add(SkillTreeAttributes.CAN_POISON_WEAPONS.get(), "Вы можете отравлять оружие ближнего боя");
		add(SkillTreeAttributes.CAN_MIX_POTIONS.get(), "Вы можете смешивать зелья");
		add(SkillTreeAttributes.EVASION_PER_POTION_EFFECT.get(), "Шанс уклонения за эффект зелья");
		add(SkillTreeAttributes.DAMAGE_PER_POTION_EFFECT.get(), "Урон за эффект зелья");
		add(SkillTreeAttributes.LIFE_PER_HIT_UNDER_POTION_EFFECT.get(), "Здоровье за удар под эффектом зелья");
		add(SkillTreeAttributes.CRIT_DAMAGE_AGAINST_POISONED.get(), "Критический урон по отравленным");
		add(SkillTreeAttributes.GEM_POWER_IN_ARMOR.get(), "Сила самоцветов в броне");
		add(SkillTreeAttributes.GEM_POWER_IN_WEAPON.get(), "Сила самоцветов в оружии");
		add(SkillTreeAttributes.MAXIMUM_WEAPON_SOCKETS.get(), "Гнёзда в оружии");
		add(SkillTreeAttributes.MAXIMUM_CHESTPLATE_SOCKETS.get(), "Гнёзда в нагрудниках");
		add(SkillTreeAttributes.MAXIMUM_SOCKETS.get(), "Гнёзда во всех предметах");
		add(SkillTreeAttributes.ARMOR_PER_GEM_IN_HELMET.get(), "Броня за самоцвет в шлеме");
		add(SkillTreeAttributes.ARMOR_PER_GEM_IN_CHESTPLATE.get(), "Броня за самоцвет в нагруднике");
		add(SkillTreeAttributes.CRIT_CHANCE_PER_GEM_IN_WEAPON.get(), "Шанс критического удара за самоцвет в оружии");
		add(SkillTreeAttributes.MINING_SPEED.get(), "Скорость добычи с киркой");
		add(SkillTreeAttributes.MAXIMUM_LIFE_PER_GEM_IN_HELMET.get(), "Максимум здоровья за самоцвет в шлеме");
		add(SkillTreeAttributes.MAXIMUM_LIFE_PER_GEM_IN_ARMOR.get(), "Максимум здоровья за самоцвет в броне");
		add(SkillTreeAttributes.CRIT_DAMAGE_PER_GEM_IN_WEAPON.get(), "Критический урон за самоцвет в оружии");
		add(SkillTreeAttributes.GEM_DROP_CHANCE.get(), "Шанс найти самоцвет в руде");
		add(SkillTreeAttributes.LIFE_REGENERATION_PER_GEM_IN_HELMET.get(), "Регенерация здоровья за самоцвет в шлеме");
		add(SkillTreeAttributes.CRAFTED_ARMOR_DEFENCE.get(), "Защита создаваемой брони");
		add(SkillTreeAttributes.CRAFTED_SHIELDS_ARMOR_BONUS.get(), "Броня создаваемых щитов");
		add(SkillTreeAttributes.LIFE_REGENERATION_WITH_SHIELD.get(), "Регенерация здоровья со щитом");
		add(SkillTreeAttributes.MAXIMUM_LIFE_PER_BOOTS_ARMOR.get(), "Максимум здоровья за броню на ботинках");
		add(SkillTreeAttributes.ATTACK_SPEED_WITH_SHIELD.get(), "Скорость атаки со щитом");
		add(SkillTreeAttributes.CRAFTED_MELEE_WEAPON_DAMAGE_BONUS.get(), "Урон создаваемого оружия ближнего боя");
		add(SkillTreeAttributes.CRAFTED_MELEE_WEAPON_ATTACK_SPEED.get(), "Скорость атаки создаваемого оружия ближнего боя");
		add(SkillTreeAttributes.CRIT_DAMAGE_WITH_SHIELD.get(), "Критический урон со щитом");
		add(SkillTreeAttributes.CRIT_CHANCE_WITH_SHIELD.get(), "Шанс критического удара со щитом");
		add(SkillTreeAttributes.DAMAGE_WITH_SHIELD.get(), "Урон со щитом");
		add(SkillTreeAttributes.CHANCE_TO_CRAFT_TOUGHER_ARMOR.get(), "Шанс создать более твёрдую броню");
		add(SkillTreeAttributes.ATTACK_DAMAGE_PER_ARMOR.get(), "Урон в ближнем бою за единицу брони");
		add(SkillTreeAttributes.CHESTPLATE_ARMOR.get(), "Броня нагрудника");
		add(SkillTreeAttributes.ENCHANTMENT_LEVEL_REQUIREMENT_REDUCTION.get(), "Снижение требования к уровню для зачарования");
		add(SkillTreeAttributes.CHANCE_TO_APPLY_BETTER_ARMOR_ENCHANTMENT.get(), "Сила накладываемых зачарований брони");
		add(SkillTreeAttributes.CHANCE_TO_APPLY_BETTER_WEAPON_ENCHANTMENT.get(), "Сила накладываемых зачарований оружия");
		add(SkillTreeAttributes.DAMAGE_WITH_ENCHANTED_WEAPON.get(), "Урон с зачарованным оружием");
		add(SkillTreeAttributes.MAXIMUM_LIFE_WITH_ENCHANTED_ITEM.get(), "Максимум здоровья с зачарованным предметом");
		add(SkillTreeAttributes.MAXIMUM_LIFE_PER_ARMOR_ENCHANTMENT.get(), "Максимум здоровья за каждое зачарование брони");
		add(SkillTreeAttributes.ATTACK_SPEED_WITH_ENCHANTED_WEAPON.get(), "Скорость атаки с зачарованным оружием");
		add(SkillTreeAttributes.CHANCE_TO_APPLY_BETTER_ENCHANTMENT.get(), "Сила накладываемых зачарований");
		add(SkillTreeAttributes.CRIT_CHANCE_WITH_ENCHANTED_WEAPON.get(), "Шанс критического удара с зачарованным оружием");
		add(SkillTreeAttributes.CRIT_DAMAGE_PER_WEAPON_ENCHANTMENT.get(), "Критический урон за каждое зачарование на оружии");
		add(SkillTreeAttributes.LIFE_ON_BLOCK_PER_SHIELD_ENCHANTMENT.get(), "Здоровье при блоке за каждое зачарование на щите");
		add(SkillTreeAttributes.BLOCK_CHANCE_PER_SHIELD_ENCHANTMENT.get(), "Шанс блока за каждое зачарование на щите");
		add(SkillTreeAttributes.DAMAGE_PER_WEAPON_ENCHANTMENT_LEVEL.get(), "Урон за уровень зачарования на оружии");
		add(SkillTreeAttributes.BLOCK_CHANCE_WITH_ENCHANTED_SHIELD.get(), "Шанс блока с зачарованным щитом");
		add(SkillTreeAttributes.FREE_ENCHANTMENT_CHANCE.get(), "Шанс бесплатного зачарования");
		add(SkillTreeAttributes.COOKED_FOOD_SATURATION.get(), "Насыщение создаваемой еды");
		add(SkillTreeAttributes.COOKED_FOOD_LIFE_REGENERATION.get(), "Регенерация здоровья создаваемой еды");
		add(SkillTreeAttributes.COOKED_FOOD_HEALING_PER_SATURATION.get(), "Лечение создаваемой еды за единицу насыщения");
		add(SkillTreeAttributes.COOKED_FOOD_DAMAGE_PER_SATURATION.get(), "Урон создаваемой еды за единицу насыщения");
		add(SkillTreeAttributes.DAMAGE_IF_NOT_HUNGRY.get(), "Урон если не голоден");
		add(SkillTreeAttributes.BLOCK_CHANCE_IF_NOT_HUNGRY.get(), "Шанс блока если не голоден");
		add(SkillTreeAttributes.LIFE_ON_BLOCK_IF_NOT_HUNGRY.get(), "Здоровье при блоке если не голоден");
		add(SkillTreeAttributes.CRIT_CHANCE_IF_NOT_HUNGRY.get(), "Шанс критического удара если не голоден");
		add(SkillTreeAttributes.MAXIMUM_LIFE_IF_NOT_HUNGRY.get(), "Максимум здоровья если не голоден");
		add(SkillTreeAttributes.MAXIMUM_LIFE_PER_SATISFIED_HUNGER.get(), "Максимум здоровья за единицу утолённого голода");
		add(SkillTreeAttributes.ATTACK_SPEED_IF_NOT_HUNGRY.get(), "Скорость атаки если не голоден");
		add(SkillTreeAttributes.BLOCK_CHANCE_PER_SATISFIED_HUNGER.get(), "Шанс блока за единицу утолённого голода");
		add(SkillTreeAttributes.DAMAGE_PER_SATISFIED_HUNGER.get(), "Урон за единицу утолённого голода");
		add(SkillTreeAttributes.COOKED_FOOD_CRITICAL_DAMAGE_PER_SATURATION.get(), "Критический урон создаваемой еды за единицу насыщения");
		add(SkillTreeAttributes.CRIT_DAMAGE_PER_SATISFIED_HUNGER.get(), "Критический урон за единицу утолённого голода");
		add(SkillTreeAttributes.CRAFTED_EQUIPMENT_DURABILITY.get(), "Прочность создаваемых предметов");
		add(SkillTreeAttributes.DAMAGE_WITH_GEM_IN_WEAPON.get(), "Урон с самоцветом в оружии");
		add(SkillTreeAttributes.DAMAGE_PER_GEM_IN_WEAPON.get(), "Урон за самоцвет в оружии");
		add(SkillTreeAttributes.ATTACK_SPEED_WITH_GEM_IN_WEAPON.get(), "Скорость атаки с самоцветом в оружии");
		add(SkillTreeAttributes.ATTACK_SPEED_PER_GEM_IN_WEAPON.get(), "Скорость атаки за самоцвет в оружии");
		add(SkillTreeAttributes.DAMAGE_WITH_POISONED_WEAPON.get(), "Урон с отравленным оружием");
		add(SkillTreeAttributes.PROJECTILE_DAMAGE_AGAINST_POISONED.get(), "Урон снарядов по отравленным");
		add(SkillTreeAttributes.SPLASH_POTION_DAMAGE.get(), "Урон взрывающихся зелий");
		add(SkillTreeAttributes.DAMAGE_PER_ADAMITE_IN_WEAPON.get(), "Урон за адамит в оружии");
		add(SkillTreeAttributes.BOW_DAMAGE.get(), "Урон луков");
		add(SkillTreeAttributes.TIPPED_ARROW_DAMAGE.get(), "Урон стрел с эффектом");
		add(SkillTreeAttributes.ARROW_DAMAGE.get(), "Урон стрел");
		add(SkillTreeAttributes.TRIDENT_DAMAGE.get(), "Урон трезубцев");
		add(SkillTreeAttributes.CROSSBOW_DAMAGE.get(), "Урон арбалетов");
		add(SkillTreeAttributes.ENCHANTED_SWORD_DAMAGE.get(), "Урон зачарованных мечей");
		add(SkillTreeAttributes.ENCHANTED_BOW_DAMAGE.get(), "Урон зачарованных луков");
		add(SkillTreeAttributes.ENCHANTED_TRIDENT_DAMAGE.get(), "Урон зачарованных трезубцев");
		add(SkillTreeAttributes.ENCHANTED_CROSSBOW_DAMAGE.get(), "Урон зачарованных арбалетов");
		add(SkillTreeAttributes.ENCHANTED_AXE_DAMAGE.get(), "Урон зачарованных топоров");
		add(SkillTreeAttributes.MELEE_DAMAGE_IF_NOT_HUNGRY.get(), "Урон в ближнем бою если не голоден");
		add(SkillTreeAttributes.AXE_DAMAGE.get(), "Урон топоров");
		add(SkillTreeAttributes.SWORD_DAMAGE.get(), "Урон мечей");
		add(SkillTreeAttributes.PROJECTILE_DAMAGE_IF_NOT_HUNGRY.get(), "Урон снарядов бою если не голоден");
		add(SkillTreeAttributes.MELEE_DAMAGE_WITH_SHIELD.get(), "Урон в ближнем бою со щитом");
		add(SkillTreeAttributes.PROJECTILE_DAMAGE_WITH_SHIELD.get(), "Урон снарядов со щитом");
		add(SkillTreeAttributes.DAMAGE_WITH_DIFFERENT_WEAPON_GEMS.get(), "Урон с разными самоцветами в оружии");
		add(SkillTreeAttributes.DAMAGE_WITH_SAME_WEAPON_GEMS.get(), "Урон с одинаковыми самоцветами в оружии");
		add(SkillTreeAttributes.MELEE_DAMAGE_PER_GEM_IN_WEAPON.get(), "Урон в ближнем бою за самоцвет в оружии");
		add(SkillTreeAttributes.PROJECTILE_DAMAGE_PER_GEM_IN_WEAPON.get(), "Урон снарядов за самоцвет в оружии");
		add(SkillTreeAttributes.DAMAGE_PER_IRISCITE_IN_WEAPON.get(), "Урон за ирисцит в оружии");
		add(SkillTreeAttributes.DAMAGE_UNDER_POTION_EFFECT.get(), "Урон под эффектом зелья");
		// effects
		add(SkillTreeEffects.CRIT_DAMAGE_BONUS.get(), "Критический урон");
		add(SkillTreeEffects.DAMAGE_BONUS.get(), "Урон");
		add(SkillTreeEffects.LIFE_REGENERATION_BONUS.get(), "Регенерация здоровья");
		// system messages
		add("skilltree.message.reset", "Древо пассивных умений изменилось. Ваши очки умений были восстановлены.");
		add("skilltree.message.reset_command", "Ваше древо пассивных умений было сброшено.");
		add("skilltree.message.point_command", "Получено очко пассивных умений.");
		// screen info
		add("widget.skill_points_left", "Очков осталось: %s");
		add("widget.skill_button.not_learned", "Умение не изучено");
		add("widget.buy_skill_button", "Купить очко умений");
		// apotheosis compatibility
		add("item.apotheosis.gem.skilltree:ruby", "Рубин");
		add("item.apotheosis.gem.skilltree:onyx", "Оникс");
		add("item.apotheosis.gem.skilltree:moonstone", "Лунный камень");
		add("item.apotheosis.gem.skilltree:opal", "Опал");
		add("item.apotheosis.gem.skilltree:adamite", "Адамит");
		add("item.apotheosis.gem.skilltree:third_eye", "Третий Глаз");
		add("item.apotheosis.gem.skilltree:citrine", "Цитрин");
		add("gem_class.any_armor", "Любая броня");
		add("gem_class.armor_or_shield", "Броня и щиты");
		add("gem_class.any_weapon", "Любое оружие");
		add("gem_class.other_armor", "Другая броня");
		add("gem_class.other_weapon", "Другое оружие");
		add("gem_class.pickaxe", "Кирки");
		// jei compatibility
		add("skilltree.jei.gem_info", "Самоцветы можно вставлять в предметы с гнёздами на кузнечном столе. Выпадают из любой руды с небольшим шансом (требуется инструмент без шёлкового касания).");
	}

	private void addTooltip(Item item, String tooltip) {
		add(item.getDescriptionId() + ".tooltip", tooltip);
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
		Arrays.asList(effects).stream().map(MobEffect::getDescriptionId).map(id -> id.replaceAll("effect.", "")).forEach(id -> potionName.append("." + id));
		add(potionName.toString(), name);
	}
}
