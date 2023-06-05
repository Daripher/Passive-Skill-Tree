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
		addSkill("hunter_crit_keystone_1", "Точность");
		addSkill("hunter_defensive_crafting_keystone_1", "Шляпник");
		addSkill("hunter_offensive_crafting_keystone_1", "Декоративные луки");
		addSkill("hunter_defensive_keystone_1", "Подогнанная броня");
		addSkill("hunter_offensive_keystone_1", "Снайпер");
		addSkill("hunter_mastery", "Охотник за сокровищами");
		// cook skills
		addSkill("cook_class", "Повар");
		addSkill("cook_crafting_notable_1", "Фрукт амброзии");
		addSkill("cook_defensive_notable_1", "Толстые руки");
		addSkill("cook_offensive_notable_1", "Тяжелый удар");
		addSkill("cook_life_notable_1", "Здоровая диета");
		addSkill("cook_speed_notable_1", "Запас энергии");
		addSkill("cook_healing_notable_1", "Перекус");
		addSkill("cook_crit_keystone_1", "Острое блюдо");
		addSkill("cook_defensive_crafting_keystone_1", "Вегетерианство");
		addSkill("cook_offensive_crafting_keystone_1", "Горячая еда");
		addSkill("cook_defensive_keystone_1", "Лишний вес");
		addSkill("cook_offensive_keystone_1", "Жирное тело");
		addSkill("cook_mastery", "Большие порции");
		// alchemist skills
		addSkill("alchemist_class", "Алхимик");
		addSkill("alchemist_crafting_notable_1", "Эксперимент");
		addSkill("alchemist_defensive_notable_1", "Улучшенные рефлексы");
		addSkill("alchemist_offensive_notable_1", "Жестокость");
		addSkill("alchemist_life_notable_1", "Зависимость");
		addSkill("alchemist_speed_notable_1", "Адреналин");
		addSkill("alchemist_healing_notable_1", "Зелье крови");
		addSkill("alchemist_crit_keystone_1", "Интоксикация");
		addSkill("alchemist_defensive_crafting_keystone_1", "Чистота");
		addSkill("alchemist_offensive_crafting_keystone_1", "Отравленное лезвие");
		addSkill("alchemist_defensive_keystone_1", "Мутация");
		addSkill("alchemist_offensive_keystone_1", "Передозировка");
		addSkill("alchemist_mastery", "Секретный ингредиент");
		// enchanter skills
		addSkill("enchanter_class", "Зачарователь");
		addSkill("enchanter_crafting_notable_1", "Магический поток");
		addSkill("enchanter_defensive_notable_1", "Рунический барьер");
		addSkill("enchanter_offensive_notable_1", "Руническое лезвие");
		addSkill("enchanter_life_notable_1", "Жизнь из магии");
		addSkill("enchanter_speed_notable_1", "Оживленное оружие");
		addSkill("enchanter_healing_notable_1", "Энергетическая призма");
		addSkill("enchanter_crit_keystone_1", "Жнец");
		addSkill("enchanter_defensive_crafting_keystone_1", "Руна защиты");
		addSkill("enchanter_offensive_crafting_keystone_1", "Руна разрушения");
		addSkill("enchanter_defensive_keystone_1", "Эгида");
		addSkill("enchanter_offensive_keystone_1", "Экскалибур");
		addSkill("enchanter_mastery", "Сокрытое знание");
		// blacksmith skills
		addSkill("blacksmith_class", "Кузнец");
		addSkill("blacksmith_crafting_notable_1", "Создатель щитов");
		addSkill("blacksmith_defensive_notable_1", "Железный панцирь");
		addSkill("blacksmith_offensive_notable_1", "Противовес");
		addSkill("blacksmith_life_notable_1", "Уверенная стойка");
		addSkill("blacksmith_speed_notable_1", "Амбидекстр");
		addSkill("blacksmith_healing_notable_1", "Укрытие");
		addSkill("blacksmith_crit_keystone_1", "Столкновение");
		addSkill("blacksmith_defensive_crafting_keystone_1", "Драгоценный металл");
		addSkill("blacksmith_offensive_crafting_keystone_1", "Лёгкий сплав");
		addSkill("blacksmith_defensive_keystone_1", "Живая крепость");
		addSkill("blacksmith_offensive_keystone_1", "Колосс");
		addSkill("blacksmith_mastery", "Чёрная сталь");
		// miner skills
		addSkill("miner_class", "Шахтёр");
		addSkill("miner_crafting_notable_1", "Раскопки");
		addSkill("miner_defensive_notable_1", "Защиткая каска");
		addSkill("miner_offensive_notable_1", "Надёжный инструмент");
		addSkill("miner_life_notable_1", "Кристалл жизни");
		addSkill("miner_speed_notable_1", "Выносливость");
		addSkill("miner_healing_notable_1", "Исцеляющий кристалл");
		addSkill("miner_crit_keystone_1", "Проклятый камень");
		addSkill("miner_defensive_crafting_keystone_1", "Куллинан");
		addSkill("miner_offensive_crafting_keystone_1", "Звезда Фуры");
		addSkill("miner_defensive_keystone_1", "Каменное сердце");
		addSkill("miner_offensive_keystone_1", "Сильные руки");
		addSkill("miner_mastery", "Ювелир");
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
		// gems info
		add("gemstone.empty", "<Слот для самоцвета>");
		// food info
		add("food.bonus.damage", "+%d%% Урон");
		add("food.bonus.crit_damage", "+%d%% Критический урон");
		add("food.bonus.life_regeneration", "+%s Регенерация здоровья");
		// weapon info
		add("weapon.poisoned", "Отравлено:");
		// items
		add(SkillTreeItems.SOOTHING_GEMSTONE.get(), "Успокаивающий самоцвет");
		addTooltip(SkillTreeItems.SOOTHING_GEMSTONE.get(), "Тёплый на ощупь");
		add(SkillTreeItems.STURDY_GEMSTONE.get(), "Крепкий самоцвет");
		addTooltip(SkillTreeItems.STURDY_GEMSTONE.get(), "Неразрушимый");
		add(SkillTreeItems.LIGHT_GEMSTONE.get(), "Лёгкий самоцвет");
		addTooltip(SkillTreeItems.LIGHT_GEMSTONE.get(), "Почти невесомый");
		add(SkillTreeItems.VOID_GEMSTONE.get(), "Пустотный самоцвет");
		addTooltip(SkillTreeItems.VOID_GEMSTONE.get(), "Кажется пустым");
		add(SkillTreeItems.RAINBOW_GEMSTONE.get(), "Радужный самоцвет");
		addTooltip(SkillTreeItems.RAINBOW_GEMSTONE.get(), "Его магия непостоянна");
		add(SkillTreeItems.WISDOM_SCROLL.get(), "Свиток мудрости");
		// attributes
		add(SkillTreeAttributes.LIFE_REGENERATION.get(), "Регенерация здоровья");
		add(SkillTreeAttributes.LIFE_PER_HIT.get(), "Здоровье за удар");
		add(SkillTreeAttributes.ARROW_CRIT_DAMAGE.get(), "Критический урон стрел");
		add(SkillTreeAttributes.ARROW_DAMAGE.get(), "Урон стрел");
		add(SkillTreeAttributes.EVASION_CHANCE.get(), "Шанс уклонения");
		add(SkillTreeAttributes.ARROW_DAMAGE_BONUS.get(), "Урон стрел");
		add(SkillTreeAttributes.BLOCK_CHANCE.get(), "Шанс блока");
		add(SkillTreeAttributes.LIFE_ON_BLOCK.get(), "Здоровье при блоке");
		add(SkillTreeAttributes.CRIT_CHANCE.get(), "Шанс критического удара");
		add(SkillTreeAttributes.DOUBLE_LOOT_CHANCE.get(), "Шанс удвоенной добычи");
		add(SkillTreeAttributes.TRIPLE_LOOT_CHANCE.get(), "Шанс утроенной добычи");
		add(SkillTreeAttributes.CRAFTED_ARMOR_EVASION.get(), "Уклонение создаваемой брони");
		add(SkillTreeAttributes.CRAFTED_BOWS_ADDITIONAL_GEMSTONE_SLOTS.get(), "Максимум слотов для самоцветов создаваемых луков");
		add(SkillTreeAttributes.CRAFTED_HELMETS_ADDITIONAL_GEMSTONE_SLOTS.get(), "Максимум слотов для самоцветов создаваемых шлемов");
		add(SkillTreeAttributes.CRAFTED_BOWS_ATTACK_SPEED.get(), "Скорость атаки создаваемых луков");
		add(SkillTreeAttributes.EVASION_CHANCE_WHEN_WOUNDED.get(), "Уклонение когда ранен");
		add(SkillTreeAttributes.ARMOR_PER_EVASION.get(), "Броня за шанс уклонения");
		add(SkillTreeAttributes.ARROW_DAMAGE_PER_DISTANCE.get(), "Урон стрел за расстояние до цели");
		add(SkillTreeAttributes.LIFE_PER_ARROW_HIT.get(), "Здоровье за попадание стрелой");
		add(SkillTreeAttributes.MAXIMUM_LIFE_PER_EVASION.get(), "Максимум здоровья за уклонение");
		add(SkillTreeAttributes.ATTACK_SPEED_WITH_BOW.get(), "Скорость атаки с луком");
		add(SkillTreeAttributes.CHANCE_TO_RETRIEVE_ARROWS.get(), "Шанс вернуть стрелу");
		add(SkillTreeAttributes.CRIT_CHANCE_WITH_BOW.get(), "Шанс критического удара с луком");
		add(SkillTreeAttributes.MAXIMUM_LIFE_UNDER_POTION_EFFECT.get(), "Максимум здоровя под эффектом зелья");
		add(SkillTreeAttributes.ATTACK_SPEED_UNDER_POTION_EFFECT.get(), "Скорость атаки под эффектом зелья");
		add(SkillTreeAttributes.BREWED_POTIONS_DURATION.get(), "Длительность создаваемых зелий");
		add(SkillTreeAttributes.CHANCE_TO_BREW_STRONGER_POTION.get(), "Шанс создать усиленное зелье");
		add(SkillTreeAttributes.CHANCE_TO_BREW_STRONGER_BENEFICIAL_POTION.get(), "Шанс создать усиленное благотворное зелье");
		add(SkillTreeAttributes.CHANCE_TO_BREW_STRONGER_HARMFUL_POTION.get(), "Шанс создать усиленное пагубное зелье");
		add(SkillTreeAttributes.DAMAGE_AGAINST_POISONED.get(), "Урон против отравленных");
		add(SkillTreeAttributes.CRIT_CHANCE_AGAINST_POISONED.get(), "Шанс критического удара против отравленных");
		add(SkillTreeAttributes.EVASION_UNDER_POTION_EFFECT.get(), "Шанс уклонения под эффектом зелья");
		add(SkillTreeAttributes.CAN_POISON_WEAPONS.get(), "Вы можете отравлять оружие ближнего боя");
		add(SkillTreeAttributes.CAN_MIX_POTIONS.get(), "Вы можете смешивать зелья");
		add(SkillTreeAttributes.EVASION_PER_POTION_EFFECT.get(), "Шанс уклонения за каждый эффект зелья");
		add(SkillTreeAttributes.DAMAGE_PER_POTION_EFFECT.get(), "Урон за каждый эффект зелья");
		add(SkillTreeAttributes.LIFE_PER_HIT_UNDER_POTION_EFFECT.get(), "Здоровье за удар под эффектом зелья");
		add(SkillTreeAttributes.CRIT_DAMAGE_AGAINST_POISONED.get(), "Критический урон против отравленных");
		add(SkillTreeAttributes.PICKAXE_DAMAGE.get(), "Урон с киркой");
		add(SkillTreeAttributes.PICKAXE_DAMAGE_BONUS.get(), "Урон с киркой");
		add(SkillTreeAttributes.ATTACK_SPEED_WITH_PICKAXE.get(), "Скорость атаки с киркой");
		add(SkillTreeAttributes.GEMSTONES_STRENGTH_IN_ARMOR.get(), "Сила самоцветов в броне");
		add(SkillTreeAttributes.GEMSTONES_STRENGTH_IN_WEAPON.get(), "Сила самоцветов в оружии");
		add(SkillTreeAttributes.MAXIMUM_WEAPON_GEMSTONE_SLOTS_BONUS.get(), "Максимум слотов для самоцветов в оружии");
		add(SkillTreeAttributes.MAXIMUM_CHESTPLATE_GEMSTONE_SLOTS_BONUS.get(), "Максимум слотов для самоцветов в нагрудниках");
		add(SkillTreeAttributes.MAXIMUM_GEMSTONE_SLOTS_BONUS.get(), "Максимум слотов для самоцветов");
		add(SkillTreeAttributes.ARMOR_PER_GEMSTONE_IN_HELMET.get(), "Броня за каждый самоцвет в шлеме");
		add(SkillTreeAttributes.ARMOR_PER_GEMSTONE_IN_CHESTPLATE.get(), "Броня за каждый самоцвет в нагруднике");
		add(SkillTreeAttributes.CRIT_CHANCE_PER_GEMSTONE_IN_WEAPON.get(), "Шанс критического удара за каждый самоцвет в оружии");
		add(SkillTreeAttributes.MINING_SPEED.get(), "Скорость добычи с киркой");
		add(SkillTreeAttributes.MAXIMUM_LIFE_PER_GEMSTONE_IN_HELMET.get(), "Максимум здоровья за каждый самоцвет в шлеме");
		add(SkillTreeAttributes.MAXIMUM_LIFE_PER_GEMSTONE_IN_ARMOR.get(), "Максимум здоровья за каждый самоцвет в броне");
		add(SkillTreeAttributes.CRIT_DAMAGE_PER_GEMSTONE_IN_WEAPON.get(), "Критический урон за каждый самоцвет в оружии");
		add(SkillTreeAttributes.CHANCE_TO_FIND_GEMSTONE.get(), "Шанс нахождения самоцветов");
		add(SkillTreeAttributes.LIFE_REGENERATION_PER_GEMSTONE_IN_HELMET.get(), "Регенерация здоровья за каждый самоцвет в шлеме");
		add(SkillTreeAttributes.CRAFTED_ARMOR_DEFENCE.get(), "Защита создаваемой брони");
		add(SkillTreeAttributes.CRAFTED_SHIELDS_ARMOR_BONUS.get(), "Броня создаваемых щитов");
		add(SkillTreeAttributes.LIFE_REGENERATION_WITH_SHIELD.get(), "Регенерация здоровья со щитом");
		add(SkillTreeAttributes.MAXIMUM_LIFE_PER_BOOTS_ARMOR.get(), "Максимум здоровья за броню на ботинках");
		add(SkillTreeAttributes.ATTACK_SPEED_WITH_SHIELD.get(), "Скорость атаки со щитом");
		add(SkillTreeAttributes.CRAFTED_WEAPON_DAMAGE_BONUS.get(), "Урон создаваемого оружия");
		add(SkillTreeAttributes.CRAFTED_WEAPON_ATTACK_SPEED.get(), "Скорость атаки создаваемого оружия");
		add(SkillTreeAttributes.CRIT_DAMAGE_WITH_SHIELD.get(), "Критический урон со щитом");
		add(SkillTreeAttributes.CRIT_CHANCE_WITH_SHIELD.get(), "Шанс критического удара со щитом");
		add(SkillTreeAttributes.DAMAGE_WITH_SHIELD.get(), "Урон со щитом");
		add(SkillTreeAttributes.CHANCE_TO_CRAFT_TOUGHER_ARMOR.get(), "Шанс создать более твёрдую броню");
		add(SkillTreeAttributes.ATTACK_DAMAGE_PER_ARMOR.get(), "Урон в ближнем бою за единицу брони");
		add(SkillTreeAttributes.CHESTPLATE_ARMOR.get(), "Броня нагрудника");
		add(SkillTreeAttributes.ENCHANTMENT_LEVEL_REQUIREMENT_REDUCTION.get(), "Снижение требования к уровню для зачарования");
		add(SkillTreeAttributes.CHANCE_TO_APPLY_BETTER_ARMOR_ENCHANTMENT.get(), "Шанс наложить усиленное зачарование брони");
		add(SkillTreeAttributes.CHANCE_TO_APPLY_BETTER_WEAPON_ENCHANTMENT.get(), "Шанс наложить усиленное зачарование оружие");
		add(SkillTreeAttributes.DAMAGE_WITH_ENCHANTED_WEAPON.get(), "Урон с зачарованным оружием");
		add(SkillTreeAttributes.MAXIMUM_LIFE_WITH_ENCHANTED_ITEM.get(), "Максимум здоровья с зачарованным предметом");
		add(SkillTreeAttributes.MAXIMUM_LIFE_PER_ARMOR_ENCHANTMENT.get(), "Максимум здоровья за каждое зачарование брони");
		add(SkillTreeAttributes.ATTACK_SPEED_WITH_ENCHANTED_WEAPON.get(), "Скорость атаки с зачарованным оружием");
		add(SkillTreeAttributes.CHANCE_TO_APPLY_BETTER_ENCHANTMENT.get(), "Шанс наложить усиленное зачарование");
		add(SkillTreeAttributes.CRIT_CHANCE_WITH_ENCHANTED_WEAPON.get(), "Шанс критического удара с зачарованным оружием");
		add(SkillTreeAttributes.CRIT_DAMAGE_PER_WEAPON_ENCHANTMENT.get(), "Критический урон за каждое зачарование на оружии");
		add(SkillTreeAttributes.LIFE_ON_BLOCK_PER_SHIELD_ENCHANTMENT.get(), "Здоровье при блоке за каждое зачарование на щите");
		add(SkillTreeAttributes.BLOCK_CHANCE_PER_SHIELD_ENCHANTMENT.get(), "Шанс блока за каждое зачарование на щите");
		add(SkillTreeAttributes.DAMAGE_PER_WEAPON_ENCHANTMENT_LEVEL.get(), "Урон за каждый уровень зачарования на оружии");
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
		// effects
		add(SkillTreeEffects.CRIT_DAMAGE_BONUS.get(), "Критический урон");
		add(SkillTreeEffects.DAMAGE_BONUS.get(), "Урон");
		add(SkillTreeEffects.LIFE_REGENERATION_BONUS.get(), "Регенерация здоровья");
		// system messages
		add("skilltree.message.reset", "Древо пассивных умений изменилось. Ваши очки умений были восстановлены.");
		add("skilltree.message.reset_command", "Ваше древо пассивных умений было сброшено.");
		// screen info
		add("widget.skill_point_progress_bar.text", "Получайте опыт что бы открывать умения");
		add("widget.skill_point_progress_bar.points", "Очков умений осталось: %s");
		add("widget.skill_point_progress_bar.buy", "Нажмите здесь что бы получить очко умений за %s опыта");
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
