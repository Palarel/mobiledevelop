package com.example.myapplication.data.repository

import com.example.myapplication.data.local.db.PotatoScanDao
import com.example.myapplication.data.local.db.toDomainModel
import com.example.myapplication.data.local.db.toEntity
import com.example.myapplication.data.local.datastore.UserPreferencesRepository
import com.example.myapplication.data.model.Achievement
import com.example.myapplication.data.model.CookingGoal
import com.example.myapplication.data.model.Ingredient
import com.example.myapplication.data.model.PotatoVariety
import com.example.myapplication.data.model.Recipe
import com.example.myapplication.data.model.RecipeStep
import com.example.myapplication.data.model.ScanAnalytics
import com.example.myapplication.data.model.ScanResult
import com.example.myapplication.data.model.UserProfile
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class PotatoRepositoryImpl(
    private val scanDao: PotatoScanDao,
    private val userPreferences: UserPreferencesRepository
) : PotatoRepository {

    override val isDarkTheme: Flow<Boolean> = userPreferences.isDarkTheme

    override suspend fun setDarkTheme(isDark: Boolean) {
        userPreferences.setDarkTheme(isDark)
    }

    private val sampleVarieties = listOf(
        PotatoVariety(
            id = "v1",
            name = "МАНИФЕСТ",
            culinaryType = "C (высококрахмалистый)",
            starchPercentage = 17,
            fleshColor = "Светло-желтая",
            dranikScore = 10,
            description = "Белорусский флагманский сорт. Высокое содержание крахмала даст густое тесто и идеальную хрустящую корочку.",
            country = "Беларусь",
            yield = "до 55 т/га",
            boilability = "Среднеразвариваемый",
            diseaseResistance = "Высокая (устойчив к фитофторозу)",
            bestDishes = "Драники, Пюре, Клецки"
        ),
        PotatoVariety(
            id = "v2",
            name = "КОРОЛЕВА АННА",
            culinaryType = "A-B (салатный / жарка)",
            starchPercentage = 13,
            fleshColor = "Желтая",
            dranikScore = 6,
            description = "Немецкий гладкий сорт. Сохраняет форму ломтиков при жарке, запекании и в салатах.",
            country = "Германия",
            yield = "до 50 т/га",
            boilability = "Низкая (слаборассыпчатый)",
            diseaseResistance = "Высокая",
            bestDishes = "Жарка, Салаты, Фри"
        ),
        PotatoVariety(
            id = "v3",
            name = "ЖУРАВИНКА",
            culinaryType = "C-D (рассыпчатый)",
            starchPercentage = 18,
            fleshColor = "Желтая",
            dranikScore = 9,
            description = "Популярный белорусский поздний сорт. Идеален для воздушного пюре и запеканок.",
            country = "Беларусь",
            yield = "до 60 т/га",
            boilability = "Сильноразвариваемый",
            diseaseResistance = "Очень высокая",
            bestDishes = "Пюре, Запеканки, Чипсы"
        ),
        PotatoVariety(
            id = "v4",
            name = "ГАЛА",
            culinaryType = "B (универсальный)",
            starchPercentage = 14,
            fleshColor = "Темно-желтая",
            dranikScore = 8,
            description = "Универсальный столовый сорт с прекрасным вкусом и устойчивостью к потемнению мякоти.",
            country = "Германия",
            yield = "до 48 т/га",
            boilability = "Умеренная",
            diseaseResistance = "Высокая",
            bestDishes = "Супы, Жарка, Запекание"
        ),
        PotatoVariety(
            id = "v5",
            name = "БРИЗ",
            culinaryType = "B (слаборассыпчатый)",
            starchPercentage = 15,
            fleshColor = "Желтая",
            dranikScore = 8,
            description = "Белорусский среднеранний сорт с отличной развариваемостью для большинства блюд.",
            country = "Беларусь",
            yield = "до 52 т/га",
            boilability = "Средняя",
            diseaseResistance = "Высокая",
            bestDishes = "Драники, Вареный картофель"
        ),
        PotatoVariety(
            id = "v6",
            name = "ВЕКТОР",
            culinaryType = "C (высококрахмалистый)",
            starchPercentage = 19,
            fleshColor = "Желтая",
            dranikScore = 10,
            description = "Рекордсмен по содержанию крахмала. Отлично подходит для крахмала, драников и фри.",
            country = "Беларусь",
            yield = "до 58 т/га",
            boilability = "Высокая",
            diseaseResistance = "Иммунитет к раку картофеля",
            bestDishes = "Драники, Фри, Крахмал"
        )
    )

    override fun getCookingGoals(): List<CookingGoal> = listOf(
        CookingGoal("g1", "Драники", "🥔", "Классические белорусские драники"),
        CookingGoal("g2", "Пюре", "🥣", "Нежное воздушное пюре"),
        CookingGoal("g3", "Жарка", "🔥", "Хрустящая корочка со специями"),
        CookingGoal("g4", "Запекание", "🥔", "Ароматный запечённый картофель")
    )

    override fun getPotatoVarieties(): List<PotatoVariety> = sampleVarieties

    override fun getScanHistory(): Flow<List<ScanResult>> {
        return scanDao.getAllScans().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun saveScanResult(result: ScanResult) {
        scanDao.insertScan(result.toEntity())
    }

    override suspend fun updateScanNote(id: String, note: String) {
        scanDao.updateScanNote(id, note)
    }

    override suspend fun toggleScanFavorite(id: String, isFav: Boolean) {
        scanDao.updateScanFavorite(id, isFav)
    }

    override suspend fun deleteScanResult(id: String) {
        scanDao.deleteScanById(id)
    }

    override fun getUserProfile(): Flow<UserProfile> {
        return combine(
            userPreferences.userName,
            userPreferences.userEmail,
            userPreferences.isLoggedIn,
            getScanHistory()
        ) { name, email, isLoggedIn, history ->
            UserProfile(
                name = name,
                email = email,
                isLoggedIn = isLoggedIn,
                achievementsCount = 6,
                totalScansCount = history.size
            )
        }
    }

    override suspend fun updateUserProfile(name: String, email: String) {
        userPreferences.saveUserProfile(name, email)
    }

    override suspend fun login(email: String, password: String): Result<UserProfile> {
        if (!email.contains("@") || password.length < 6) {
            return Result.failure(IllegalArgumentException("Неверный email или пароль (минимум 6 символов)"))
        }
        val name = email.substringBefore("@").replaceFirstChar { it.uppercase() }
        val token = "token_${System.currentTimeMillis()}"
        userPreferences.saveAuthSession(name, email, token)
        return Result.success(UserProfile(name = name, email = email, isLoggedIn = true))
    }

    override suspend fun register(name: String, email: String, password: String): Result<UserProfile> {
        if (name.isBlank() || !email.contains("@") || password.length < 6) {
            return Result.failure(IllegalArgumentException("Проверьте правильность введенных данных"))
        }
        val token = "token_${System.currentTimeMillis()}"
        userPreferences.saveAuthSession(name.trim(), email.trim(), token)
        return Result.success(UserProfile(name = name.trim(), email = email.trim(), isLoggedIn = true))
    }

    override suspend fun logout() {
        userPreferences.clearAuthSession()
    }

    override fun getAchievements(): Flow<List<Achievement>> {
        return combine(getScanHistory(), userPreferences.isLoggedIn) { history, isLoggedIn ->
            val totalScans = history.size
            val has10Score = history.any { it.score >= 10 }
            val distinctVarieties = history.map { it.varietyName }.distinct().size

            listOf(
                Achievement("a1", "Первый клубень", "Сделайте первое сканирование", "🥔", totalScans >= 1, totalScans.coerceAtMost(1), 1),
                Achievement("a2", "Мастер драников", "Найдите сорт с высшим баллом 10/10", "🥞", has10Score, if (has10Score) 1 else 0, 1),
                Achievement("a3", "Знаток сортов", "Отсканируйте 2 разных сорта", "⭐", distinctVarieties >= 2, distinctVarieties.coerceAtMost(2), 2),
                Achievement("a4", "Агроном 80 лвл", "Сделайте минимум 3 сканирования", "🚜", totalScans >= 3, totalScans.coerceAtMost(3), 3),
                Achievement("a5", "Авторизован", "Зарегистрируйтесь в приложении", "🔥", isLoggedIn, if (isLoggedIn) 1 else 0, 1),
                Achievement("a6", "Король картошки", "Выполните 5 сканирований", "👑", totalScans >= 5, totalScans.coerceAtMost(5), 5)
            )
        }
    }

    override fun getScanAnalytics(): Flow<ScanAnalytics> {
        return getScanHistory().map { history ->
            if (history.isEmpty()) {
                ScanAnalytics(0, 0, 0, 0, 0.0, "Манифест")
            } else {
                val total = history.size
                val dranikiCount = history.count { it.verdictTitle.contains("ДРАНИК", ignoreCase = true) }
                val pureeCount = history.count { it.verdictTitle.contains("ПЮРЕ", ignoreCase = true) }
                val fryCount = history.count { it.verdictTitle.contains("ЖАРК", ignoreCase = true) }
                val avgStarch = history.map { it.starchPercentage }.average()
                val topVar = history.groupingBy { it.varietyName }.eachCount().maxByOrNull { it.value }?.key ?: "Манифест"

                ScanAnalytics(
                    totalScans = total,
                    dranikiCount = dranikiCount,
                    pureeCount = pureeCount,
                    fryCount = fryCount,
                    averageStarch = avgStarch,
                    topVariety = topVar
                )
            }
        }
    }

    override fun simulateAnalysis(selectedGoal: String): Flow<Int> = flow {
        emit(20)
        delay(250)
        emit(40)
        delay(250)
        emit(60)
        delay(250)
        emit(80)
        delay(250)
        emit(100)
    }

    override fun generateScanVerdict(selectedGoal: String): ScanResult {
        val normalizedGoal = selectedGoal.trim()
        val now = System.currentTimeMillis()

        return when {
            normalizedGoal.contains("Пюре", ignoreCase = true) -> {
                ScanResult(
                    id = "scan_$now",
                    timestamp = now,
                    varietyName = "ЖУРАВИНКА",
                    culinaryType = "C-D (рассыпчатый)",
                    starchPercentage = 18,
                    fleshColor = "Желтая",
                    accuracyPercentage = 95,
                    score = 9,
                    verdictTitle = "ОТЛИЧНО ДЛЯ ПЮРЕ",
                    verdictText = "Высокое содержание крахмала (18%). Пюре получится пышным, без комочков и с нежной текстурой.",
                    warningMessage = null
                )
            }
            normalizedGoal.contains("Жарка", ignoreCase = true) -> {
                ScanResult(
                    id = "scan_$now",
                    timestamp = now,
                    varietyName = "КОРОЛЕВА АННА",
                    culinaryType = "A-B (жарка / салат)",
                    starchPercentage = 13,
                    fleshColor = "Ярко-желтая",
                    accuracyPercentage = 94,
                    score = 9,
                    verdictTitle = "ОТЛИЧНО ДЛЯ ЖАРКИ",
                    verdictText = "Умеренный крахмал (13%). Ломтики отлично держат форму при жарке и дают румяную корочку.",
                    warningMessage = null
                )
            }
            else -> {
                ScanResult(
                    id = "scan_$now",
                    timestamp = now,
                    varietyName = "МАНИФЕСТ",
                    culinaryType = "C (высококрахмалистый)",
                    starchPercentage = 17,
                    fleshColor = "Светло-желтая",
                    accuracyPercentage = 96,
                    score = 10,
                    verdictTitle = "МАНИФЕСТ — 10/10 ДЛЯ ДРАНИКОВ",
                    verdictText = "Идеальный выбор! Содержание крахмала 17% обеспечит плотное тесто. Оладьи не расползутся и получат фирменную золотистую корочку.",
                    warningMessage = null
                )
            }
        }
    }

    override fun getRecipes(): List<Recipe> = listOf(
        Recipe(
            id = "r1",
            title = "Классические белорусские драники",
            description = "Хрустящие снаружи и сочные внутри драники без добавления лишней муки.",
            prepTimeMinutes = 25,
            difficulty = "Средняя",
            baseServings = 2,
            ingredients = listOf(
                Ingredient("Картофель (сорт Манифест)", 800.0, "г"),
                Ingredient("Лук репчатый", 150.0, "г"),
                Ingredient("Яйцо куриное", 1.0, "шт"),
                Ingredient("Мука пшеничная", 20.0, "г"),
                Ingredient("Соль и свежемолотый перец", 1.0, "по вкусу"),
                Ingredient("Масло растительное для жарки", 50.0, "мл")
            ),
            steps = listOf(
                RecipeStep(1, "Подготовка", "Очистить картофель и лук. Натереть на мелкой картофельной тёрке («звёздочки»), чередуя картофель с луком, чтобы тесто не темнело."),
                RecipeStep(2, "Отжатие сока", "Откинуть натёртую массу на сито и дать стечь лишнему соку в течение 2 минут.", 120),
                RecipeStep(3, "Замес теста", "Добавить яйцо, муку, соль, перец и аккуратно перемешать ложкой."),
                RecipeStep(4, "Разогрев сковороды", "Разогреть сковороду с растительным маслом на среднем огне.", 180),
                RecipeStep(5, "Жарка", "Выкладывать тесто ложкой в виде оладий и жарить по 3-4 минуты с каждой стороны до золотистой корочки.", 240)
            )
        ),
        Recipe(
            id = "r2",
            title = "Нежное картофельное пюре",
            description = "Воздушное пюре без комочков с добавлением сливочного масла и тёплого молока.",
            prepTimeMinutes = 30,
            difficulty = "Легкая",
            baseServings = 2,
            ingredients = listOf(
                Ingredient("Картофель (сорт Журавинка)", 1000.0, "г"),
                Ingredient("Молоко 3.2%", 150.0, "мл"),
                Ingredient("Масло сливочное 82.5%", 60.0, "г"),
                Ingredient("Соль", 10.0, "г")
            ),
            steps = listOf(
                RecipeStep(1, "Варка", "Очистить картофель, нарезать крупными кусками, залить холодной водой, посолить и варить 20 минут до мягкости.", 1200),
                RecipeStep(2, "Прогрев молока", "Пока варится картофель, подогреть молоко со сливочным маслом."),
                RecipeStep(3, "Пюрирование", "Слить воду, тщательно размять картофель толкушкой и постепенно влить горячее молоко с маслом до пышности.")
            )
        )
    )
}
