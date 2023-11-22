package daripher.skilltree.data.generation.translation;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.init.*;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.effect.MobEffects;
import top.theillusivec4.curios.common.CuriosHelper;

public class PSTRussianTranslationProvider extends PSTTranslationProvider {
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
    addSkill(
        "blacksmith_gateway", "Пространственные врата", "Соединяются с пространственными вратами");
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
    // skill bonuses
    add(PSTSkillBonuses.DAMAGE.get(), "Урон");
    add(PSTSkillBonuses.CRIT_DAMAGE.get(), "Множитель критического урона");
    add(PSTSkillBonuses.CRIT_CHANCE.get(), "Шанс критического удара");
    add(PSTSkillBonuses.CRAFTED_ITEM_BONUS.get(), "Создаваемые %s: %s");
    add(PSTSkillBonuses.GEM_POWER.get(), "Самоцветов вставляемые в %s: %s");
    add(PSTSkillBonuses.GEM_POWER.get(), "bonus", "Сила Эффектов");
    add(PSTSkillBonuses.PLAYER_SOCKETS.get(), "Гнёзда Самоцветов в %s");
    add(PSTSkillBonuses.BLOCK_BREAK_SPEED.get(), "Скорость добычи Блоков");
    add(PSTSkillBonuses.REPAIR_EFFICIENCY.get(), "Ремонтируемые %s: %s");
    add(PSTSkillBonuses.REPAIR_EFFICIENCY.get(), "bonus", "Прочности восстановлено");
    add(PSTSkillBonuses.ENCHANTMENT_AMPLIFICATION.get(), "%s: %s");
    add(PSTSkillBonuses.ENCHANTMENT_AMPLIFICATION.get(), "bonus", "Шанс усиления");
    add(PSTSkillBonuses.ENCHANTMENT_REQUIREMENT.get(), "Зачарование: %s");
    add(PSTSkillBonuses.ENCHANTMENT_REQUIREMENT.get(), "bonus", "Требование к уровню");
    add(PSTSkillBonuses.FREE_ENCHANTMENT.get(), "Зачарование: %s");
    add(PSTSkillBonuses.FREE_ENCHANTMENT.get(), "bonus", "Шанс бесплатного зачарование");
    add(PSTSkillBonuses.RECIPE_UNLOCK.get(), "Открывает рецепт: %s");
    // item bonuses
    add(PSTItemBonuses.SOCKETS.get(), "+%d Гнезда для Самоцветов");
    add(PSTItemBonuses.DURABILITY.get(), "Прочность");
    add(PSTItemBonuses.QUIVER_CAPACITY.get(), "Вместимость");
    add(PSTItemBonuses.POTION_AMPLIFICATION.get(), "Шанс Усиления");
    add(PSTItemBonuses.POTION_DURATION.get(), "Длительность");
    add(PSTItemBonuses.FOOD_EFFECT.get(), "%s на %s");
    add(PSTItemBonuses.FOOD_SATURATION.get(), "Насыщение");
    add(PSTItemBonuses.FOOD_HEALING.get(), "Восстанавливает %s Здоровья");
    // living conditions
    add(PSTLivingConditions.EFFECT_AMOUNT.get(), "target.you", "Вас есть");
    add(PSTLivingConditions.EFFECT_AMOUNT.get(), "target.target", "Цели");
    add(PSTLivingConditions.EFFECT_AMOUNT.get(), "min.1", "%s если на %s есть эффекты");
    add(PSTLivingConditions.EFFECT_AMOUNT.get(), "min", "%s %s есть минимум %d есть эффектов");
    add(PSTLivingConditions.EFFECT_AMOUNT.get(), "max", "%s %s есть максимум %d эффектов");
    add(PSTLivingConditions.EFFECT_AMOUNT.get(), "range", "%s %s есть от %d до %d эффектов");
    add(PSTLivingConditions.HEALTH_PERCENTAGE.get(), "target.you", "если у Вас");
    add(PSTLivingConditions.HEALTH_PERCENTAGE.get(), "target.target", "если у Цели");
    add(PSTLivingConditions.HEALTH_PERCENTAGE.get(), "min", "%s %s минимум %s%% здоровья");
    add(PSTLivingConditions.HEALTH_PERCENTAGE.get(), "max", "%s %s максимум %s%% здоровья");
    add(PSTLivingConditions.HEALTH_PERCENTAGE.get(), "range", "%s %s от %s%% до %s%% здоровья");
    add(PSTLivingConditions.HAS_ENCHANTED_ITEM.get(), "target.you", "Вас");
    add(PSTLivingConditions.HAS_ENCHANTED_ITEM.get(), "target.target", "Цели");
    add(PSTLivingConditions.HAS_ENCHANTED_ITEM.get(), "%s если на %s экипирован зачарованный %s");
    add(PSTLivingConditions.HAS_ITEM_EQUIPPED.get(), "target.you", "Вас");
    add(PSTLivingConditions.HAS_ITEM_EQUIPPED.get(), "target.target", "Цели");
    add(PSTLivingConditions.HAS_ITEM_EQUIPPED.get(), "%s если на %s экипирован %s");
    add(PSTLivingConditions.HAS_GEMS.get(), "target.you", "если у Вас есть");
    add(PSTLivingConditions.HAS_GEMS.get(), "target.target", "если у Цели есть");
    add(PSTLivingConditions.HAS_GEMS.get(), "min.1", "%s %s самоцветы в %s");
    add(PSTLivingConditions.HAS_GEMS.get(), "min", "%s %s минимум %d самоцветов в %s");
    add(PSTLivingConditions.HAS_GEMS.get(), "max", "%s %s максимум %d самоцветов в %s");
    add(PSTLivingConditions.HAS_GEMS.get(), "range", "%s %s от %d до %d самоцветов в %s");
    add(PSTLivingConditions.HAS_EFFECT.get(), "target.you", "Вас");
    add(PSTLivingConditions.HAS_EFFECT.get(), "target.target", "Цель");
    add(PSTLivingConditions.HAS_EFFECT.get(), "%s если на %s действует эффект %s");
    add(PSTLivingConditions.IS_BURNING.get(), "target.you", "Вы горите");
    add(PSTLivingConditions.IS_BURNING.get(), "target.target", "Цель горит");
    add(PSTLivingConditions.IS_BURNING.get(), "%s если %s");
    add(PSTLivingConditions.ATTRIBUTE_VALUE.get(), "target.you", "у Вас");
    add(PSTLivingConditions.ATTRIBUTE_VALUE.get(), "target.target", "у Цели");
    add(PSTLivingConditions.ATTRIBUTE_VALUE.get(), "min", "%s если %s минимум %s %s");
    add(PSTLivingConditions.ATTRIBUTE_VALUE.get(), "max", "%s если %s максимум %s %s");
    add(PSTLivingConditions.ATTRIBUTE_VALUE.get(), "range", "%s если %s от %s%% до %s %s");
    add(PSTLivingConditions.FOOD_LEVEL.get(), "target.you", "у Вас");
    add(PSTLivingConditions.FOOD_LEVEL.get(), "target.target", "у Цели");
    add(PSTLivingConditions.FOOD_LEVEL.get(), "min", "%s если %s минимум %s очков Голода");
    add(PSTLivingConditions.FOOD_LEVEL.get(), "max", "%s если %s максимум %s очков Голода");
    add(PSTLivingConditions.FOOD_LEVEL.get(), "range", "%s если %s от %s%% до %s очков Голода");
    // damage conditions
    add(PSTDamageConditions.IS_PROJECTILE.get(), "%s снарядов");
    add(PSTDamageConditions.IS_MELEE.get(), "%s в ближнем бою");
    // enchantment conditions
    add(PSTEnchantmentConditions.WEAPON.get(), "Зачарование оружия");
    add(PSTEnchantmentConditions.ARMOR.get(), "Зачарование брони");
    add(PSTEnchantmentConditions.ANY.get(), "Зачарование");
    // item conditions
    add(PSTItemConditions.WEAPON.get(), "any", "Оружие");
    add(PSTItemConditions.WEAPON.get(), "ranged", "Оружие дальнего боя");
    add(PSTItemConditions.WEAPON.get(), "melee", "Оружие ближнего боя");
    add(PSTItemConditions.CURIO.get(), "ring", "Кольца");
    add(PSTItemConditions.CURIO.get(), "necklace", "Ожерелья");
    add(PSTItemConditions.CURIO.get(), "quiver", "Колчаны");
    add(PSTItemConditions.ARMOR.get(), "any", "Броня");
    add(PSTItemConditions.ARMOR.get(), "head", "Шлемы");
    add(PSTItemConditions.ARMOR.get(), "chest", "Нагрудники");
    add(PSTItemConditions.ARMOR.get(), "legs", "Штаны");
    add(PSTItemConditions.ARMOR.get(), "feet", "Ботинки");
    add(PSTItemConditions.ARMOR.get(), "offhand", "Щиты");
    add(PSTItemConditions.EQUIPMENT.get(), "Экипировка");
    add(PSTItemConditions.AXE.get(), "Топоры");
    add(PSTItemConditions.POTION.get(), "any", "Зелья");
    add(PSTItemConditions.POTION.get(), "beneficial", "Благотворные Зелья");
    add(PSTItemConditions.POTION.get(), "harmful", "Вредящие Зелья");
    add(PSTItemConditions.POTION.get(), "neutral", "Нейтральные Зелья");
    add(PSTItemConditions.FOOD.get(), "Еда");
    add(PSTItemConditions.JEWELRY.get(), "Бижутерия");
    add(PSTItemConditions.PICKAXE.get(), "Кирка");
    // skill multipliers
    add(PSTSkillBonusMultipliers.EFFECT_AMOUNT.get(), "%s за каждый эффект на вас");
    add(PSTSkillBonusMultipliers.ATTRIBUTE_VALUE.get(), "%s за каждую единицу %s");
    add(PSTSkillBonusMultipliers.ENCHANTS_AMOUNT.get(), "%s за каждое зачарование на вашем %s");
    add(PSTSkillBonusMultipliers.ENCHANTS_LEVELS.get(), "%s for each enchantment level on your %s");
    add(PSTSkillBonusMultipliers.GEMS_AMOUNT.get(), "%s за каждый самоцвет в вашем %s");
    add(PSTSkillBonusMultipliers.FOOD_LEVEL.get(), "%s за каждую единицу Голода");
    // recipes
    addRecipe("skilltree:weapon_poisoning", "Отравление Оружия");
    addRecipe("skilltree:potion_mixing", "Смешивание Зелий");
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
    add(PSTAttributes.EVASION.get(), "Уклонение");
    add(PSTAttributes.BLOCKING.get(), "Блокирование");
    add(PSTAttributes.LIFE_ON_BLOCK.get(), "Здоровье при блоке");
    add(PSTAttributes.DOUBLE_LOOT_CHANCE.get(), "Шанс удвоенной добычи");
    add(PSTAttributes.TRIPLE_LOOT_CHANCE.get(), "Шанс утроенной добычи");
    add(PSTAttributes.DAMAGE_PER_DISTANCE_TO_ENEMY.get(), "Урон за расстояние до цели");
    add(PSTAttributes.CHANCE_TO_RETRIEVE_ARROWS.get(), "Шанс вернуть стрелы");
    add(PSTAttributes.GEM_DROP_CHANCE.get(), "Шанс найти самоцвет в руде");
    add(PSTAttributes.INCOMING_HEALING.get(), "Получаемое лечение");
    add(PSTAttributes.EXPERIENCE_PER_MINUTE.get(), "Опыт в минуту");
    add(CuriosHelper.getOrCreateSlotAttribute("ring"), "Слоты колец");
    add(PSTAttributes.EXPERIENCE_FROM_MOBS.get(), "Опыт от убитых существ");
    add(PSTAttributes.EXPERIENCE_FROM_ORE.get(), "Опыт от добытой руды");
    add(
        PSTAttributes.DAMAGE_PER_DISTANCE_TO_SPAWN.get(),
        "Урон за расстояние до точки возрождения (максимум 50%)");
    add(PSTAttributes.CHANCE_TO_IGNITE.get(), "Шанс поджога");
    add(PSTAttributes.STEALTH.get(), "Скрытность");
    add(PSTAttributes.DOUBLE_FISHING_LOOT_CHANCE.get(), "Шанс удвоенной добычи от рыбалки");
    add(PSTAttributes.EXPERIENCE_FROM_FISHING.get(), "Опыт от рыбалки");
    add(PSTAttributes.CHANCE_TO_EXPLODE_ENEMY.get(), "Шанс взорвать врага");
    // effects
    add(PSTEffects.CRIT_DAMAGE_BONUS.get(), "Критический урон");
    add(PSTEffects.DAMAGE_BONUS.get(), "Урон");
    add(PSTEffects.LIFE_REGENERATION_BONUS.get(), "Регенерация здоровья");
    // system messages
    add(
        "skilltree.message.reset",
        "Древо пассивных умений изменилось. Ваши очки умений были восстановлены.");
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
    add(
        "skilltree.jei.gem_info",
        "Самоцветы можно вставлять в предметы с гнёздами на кузнечном столе. Выпадают из любой руды с небольшим шансом (требуется инструмент без шёлкового касания).");
    // curios info
    add("curios.identifier.quiver", "Колчан");
    add("curios.modifiers.quiver", "Когда надет:");
    // tabs
    add("itemGroup.skilltree", "Passive Skill Tree");
    // misc
    add("item.modifiers.both_hands", "Когда в руке:");
    // affix names
    add("affix.skilltree:jewelry/dmg_reduction/tempered", "Закалённый");
    add("affix.skilltree:jewelry/dmg_reduction/tempered.suffix", "Закалки");
    add("affix.skilltree:jewelry/attribute/immortal", "Бессмертный");
    add("affix.skilltree:jewelry/attribute/immortal.suffix", "Бессмертия");
    add("affix.skilltree:jewelry/attribute/experienced", "Опытный");
    add("affix.skilltree:jewelry/attribute/experienced.suffix", "Опыта");
    add("affix.skilltree:jewelry/attribute/lucky", "Удачливый");
    add("affix.skilltree:jewelry/attribute/lucky.suffix", "Удачи");
    add("affix.skilltree:jewelry/attribute/hasty", "Спешащий");
    add("affix.skilltree:jewelry/attribute/hasty.suffix", "Спешки");
    add("affix.skilltree:jewelry/attribute/greedy", "Жадный");
    add("affix.skilltree:jewelry/attribute/greedy.suffix", "Жадности");
    add("affix.skilltree:jewelry/attribute/healthy", "Здоровый");
    add("affix.skilltree:jewelry/attribute/healthy.suffix", "Здоровья");
  }
}
