package com.example.myapplication.data.repository

import com.example.myapplication.data.local.datastore.UserPreferencesRepository
import com.example.myapplication.data.model.Achievement
import com.example.myapplication.data.model.CookingGoal
import com.example.myapplication.data.model.PotatoVariety
import com.example.myapplication.data.model.ScanResult
import com.example.myapplication.data.model.UserProfile
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class PotatoRepositoryImpl(
    private val userPreferences: UserPreferencesRepository
) : PotatoRepository {

    private val sampleVarieties = listOf(
        PotatoVariety(
            id = "v1",
            name = "МАНИФЕСТ",
            culinaryType = "C (высококрахмалистый)",
            starchPercentage = 17,
            fleshColor = "Светло-желтая",
            dranikScore = 10,
            description = "Отличный выбор! Высокое содержание крахмала даст плотное тесто и идеальную золотистую корочку."
        ),
        PotatoVariety(
            id = "v2",
            name = "КОРОЛЕВА АННА",
            culinaryType = "A-B (салатный / жарка)",
            starchPercentage = 13,
            fleshColor = "Желтая",
            dranikScore = 6,
            description = "Идеальна для жарки ломтиками и запекания. Сохраняет форму при термической обработке."
        ),
        PotatoVariety(
            id = "v3",
            name = "ЖУРАВИНКА",
            culinaryType = "C-D (рассыпчатый)",
            starchPercentage = 18,
            fleshColor = "Желтая",
            dranikScore = 9,
            description = "Прекрасный сорт для пюре и запеканок."
        )
    )

    private val historyState = MutableStateFlow(
        listOf(
            ScanResult(
                id = "scan_101",
                timestamp = System.currentTimeMillis() - 86400000,
                varietyName = "МАНИФЕСТ",
                culinaryType = "C (универсальный)",
                starchPercentage = 17,
                fleshColor = "Светло-желтая",
                accuracyPercentage = 96,
                score = 10,
                verdictTitle = "ИДЕАЛЬНО ДЛЯ ДРАНИКОВ",
                verdictText = "Густое тесто, хрустящая корочка"
            )
        )
    )

    override fun getCookingGoals(): List<CookingGoal> = listOf(
        CookingGoal("g1", "Драники", "🥔", "Классические белорусские драники"),
        CookingGoal("g2", "Пюре", "🥣", "Нежное воздушное пюре"),
        CookingGoal("g3", "Жарка", "🔥", "Хрустящая корочка со специями"),
        CookingGoal("g4", "Запекание", "🥔", "Ароматный запечённый картофель")
    )

    override fun getPotatoVarieties(): List<PotatoVariety> = sampleVarieties

    override fun getScanHistory(): Flow<List<ScanResult>> = historyState

    override suspend fun saveScanResult(result: ScanResult) {
        val currentList = historyState.value.toMutableList()
        currentList.add(0, result)
        historyState.value = currentList
    }

    override fun getUserProfile(): Flow<UserProfile> {
        return userPreferences.userName.map { name ->
            UserProfile(
                name = name,
                email = "expert@bulbascan.by",
                achievementsCount = 6,
                totalScansCount = historyState.value.size
            )
        }
    }

    override suspend fun updateUserProfile(name: String, email: String) {
        userPreferences.saveUserProfile(name, email)
    }

    override fun getAchievements(): List<Achievement> = listOf(
        Achievement("a1", "Первый клубень", "🥔", true),
        Achievement("a2", "Мастер драников", "🥞", true),
        Achievement("a3", "Жаркая полночь", "🔥", true),
        Achievement("a4", "Знаток сортов", "⭐", true),
        Achievement("a5", "Агроном 80 лвл", "🚜", true),
        Achievement("a6", "Король картошки", "👑", true)
    )

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
                // По умолчанию или "Драники"
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
}
