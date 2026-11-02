package com.example.myapplication.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.model.Achievement
import com.example.myapplication.data.model.ScanResult
import com.example.myapplication.ui.analysis.AnalysisUiState
import com.example.myapplication.ui.analysis.AnalysisViewModel
import com.example.myapplication.ui.home.HomeUiState
import com.example.myapplication.ui.home.HomeViewModel
import com.example.myapplication.ui.profile.ProfileUiState
import com.example.myapplication.ui.profile.ProfileViewModel
import com.example.myapplication.ui.state.UiState
import com.example.myapplication.ui.timer.TimerUiState
import com.example.myapplication.ui.timer.TimerViewModel

@Composable
fun BulbaNativeApp(
    homeViewModel: HomeViewModel,
    analysisViewModel: AnalysisViewModel,
    timerViewModel: TimerViewModel,
    profileViewModel: ProfileViewModel,
    onCameraRequested: () -> Unit,
    onGalleryRequested: () -> Unit,
    onShareRequested: (String) -> Unit,
    onHapticRequested: (String) -> Unit
) {
    var currentTab by remember { mutableIntStateOf(0) }
    var currentScreen by remember { mutableStateOf("home") }

    val homeState by homeViewModel.uiState.collectAsState()
    val analysisState by analysisViewModel.uiState.collectAsState()
    val timerState by timerViewModel.uiState.collectAsState()
    val profileState by profileViewModel.uiState.collectAsState()

    LaunchedEffect(analysisState.isComplete) {
        if (analysisState.isComplete && analysisState.scanVerdict != null) {
            currentScreen = "verdict"
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surfaceVariant) {
                NavigationBarItem(
                    selected = currentTab == 0 && currentScreen in listOf("home", "camera"),
                    onClick = {
                        onHapticRequested("click")
                        currentTab = 0
                        currentScreen = "home"
                    },
                    icon = { Text("⛶", fontSize = 20.sp) },
                    label = { Text("Сканер") }
                )
                NavigationBarItem(
                    selected = currentTab == 1 || currentScreen == "verdict",
                    onClick = {
                        onHapticRequested("click")
                        currentTab = 1
                        currentScreen = "verdict"
                    },
                    icon = { Text("▤", fontSize = 20.sp) },
                    label = { Text("Сорта") }
                )
                NavigationBarItem(
                    selected = currentTab == 2 || currentScreen == "timer",
                    onClick = {
                        onHapticRequested("click")
                        currentTab = 2
                        currentScreen = "timer"
                    },
                    icon = { Text("⏱", fontSize = 20.sp) },
                    label = { Text("Таймер") }
                )
                NavigationBarItem(
                    selected = currentTab == 3 || currentScreen == "profile",
                    onClick = {
                        onHapticRequested("click")
                        currentTab = 3
                        currentScreen = "profile"
                    },
                    icon = { Text("📁", fontSize = 20.sp) },
                    label = { Text("История") }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (currentScreen) {
                "home" -> HomeScreen(
                    homeState = homeState,
                    onRecipeSelected = { recipe ->
                        onHapticRequested("click")
                        homeViewModel.selectRecipe(recipe)
                        timerViewModel.setGoalTimer(recipe)
                    },
                    onScanClicked = {
                        onHapticRequested("click")
                        onCameraRequested()
                    },
                    onGalleryClicked = {
                        onHapticRequested("click")
                        onGalleryRequested()
                    },
                    onProfileClicked = {
                        onHapticRequested("click")
                        currentScreen = "profile"
                        currentTab = 3
                    }
                )

                "analysis" -> AnalysisScreen(
                    analysisState = analysisState,
                    onCancel = {
                        onHapticRequested("click")
                        analysisViewModel.cancelAnalysis()
                        currentScreen = "home"
                    }
                )

                "verdict" -> VerdictScreen(
                    scanResult = analysisState.scanVerdict,
                    onStartTimer = {
                        onHapticRequested("click")
                        currentScreen = "timer"
                        currentTab = 2
                    },
                    onShare = { text ->
                        onHapticRequested("click")
                        onShareRequested(text)
                    },
                    onScanNew = {
                        onHapticRequested("click")
                        onCameraRequested()
                    }
                )

                "timer" -> TimerScreen(
                    timerState = timerState,
                    onToggleTimer = {
                        onHapticRequested("click")
                        timerViewModel.toggleTimer()
                    },
                    onResetTimer = {
                        onHapticRequested("click")
                        timerViewModel.resetTimer()
                    }
                )

                "profile" -> ProfileScreen(
                    profileState = profileState,
                    onUpdateName = { name ->
                        onHapticRequested("click")
                        profileViewModel.updateName(name)
                    },
                    onDeleteScan = { id ->
                        onHapticRequested("click")
                        profileViewModel.deleteScanHistoryItem(id)
                    },
                    onToggleTheme = { isDark ->
                        onHapticRequested("click")
                        profileViewModel.toggleTheme(isDark)
                    },
                    onLogin = { email, pass ->
                        profileViewModel.login(email, pass)
                    },
                    onLogout = {
                        profileViewModel.logout()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    homeState: UiState<HomeUiState>,
    onRecipeSelected: (String) -> Unit,
    onScanClicked: () -> Unit,
    onGalleryClicked: () -> Unit,
    onProfileClicked: () -> Unit
) {
    var isDropdownExpanded by remember { mutableStateOf(false) }

    val data = (homeState as? UiState.Success)?.data ?: HomeUiState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🥔", fontSize = 24.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text("BulbaScan", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
            }
            IconButton(onClick = onProfileClicked) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE2E8F0)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("👤", fontSize = 18.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Goal Title
        Text(
            "КУЛИНАРНАЯ ЦЕЛЬ",
            fontSize = 12.sp,
            color = Color(0xFFE07A2B),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            "Что будем готовить?",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Recipe Dropdown
        Box(modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = { isDropdownExpanded = !isDropdownExpanded },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE07A2B)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("🥔 ${data.selectedRecipe} ⌄", fontWeight = FontWeight.Bold)
            }

            DropdownMenu(
                expanded = isDropdownExpanded,
                onDismissRequest = { isDropdownExpanded = false }
            ) {
                listOf("Драники", "Пюре", "Жарка", "Запекание").forEach { recipe ->
                    DropdownMenuItem(
                        text = { Text(recipe) },
                        onClick = {
                            onRecipeSelected(recipe)
                            isDropdownExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Scanner Card Area
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFEDD5)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🥔", fontSize = 48.sp)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text("✨ AI калибровка активна", fontSize = 12.sp, color = Color(0xFF64748B))
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Action Buttons
        Button(
            onClick = onScanClicked,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE07A2B)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("📷 Сканировать клубень", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onGalleryClicked,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("🖼️ Выбрать из галереи", fontSize = 14.sp)
        }
    }
}

@Composable
fun AnalysisScreen(
    analysisState: AnalysisUiState,
    onCancel: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Анализ клубня", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text("Сверка с эталонными профилями сортов", fontSize = 14.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A))
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("🥔", fontSize = 64.sp)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(analysisState.statusText, fontSize = 14.sp)
            Text("${analysisState.progressPercentage}%", fontWeight = FontWeight.Bold, color = Color(0xFFE07A2B))
        }

        Spacer(modifier = Modifier.height(8.dp))

        LinearProgressIndicator(
            progress = { analysisState.progressPercentage / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(RoundedCornerShape(6.dp)),
            color = Color(0xFFE07A2B)
        )

        Spacer(modifier = Modifier.height(32.dp))

        TextButton(onClick = onCancel) {
            Text("Отмена", color = Color.Red)
        }
    }
}

@Composable
fun VerdictScreen(
    scanResult: ScanResult?,
    onStartTimer: () -> Unit,
    onShare: (String) -> Unit,
    onScanNew: () -> Unit
) {
    val result = scanResult ?: ScanResult(
        id = "demo",
        timestamp = System.currentTimeMillis(),
        varietyName = "МАНИФЕСТ",
        culinaryType = "C (высококрахмалистый)",
        starchPercentage = 17,
        fleshColor = "Светло-желтая",
        accuracyPercentage = 96,
        score = 10,
        verdictTitle = "МАНИФЕСТ — 10/10 ДЛЯ ДРАНИКОВ",
        verdictText = "Идеальный выбор! Содержание крахмала 17% обеспечит плотное тесто. Оладьи не расползутся и получат фирменную золотистую корочку."
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Вердикт сканера", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Surface(
                    color = Color(0xFF22C55E),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        "★ ${result.score}/10 — ПОДХОДИТ",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text("“${result.varietyName}”", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                Text("Кулинарный тип: ${result.culinaryType}", fontSize = 14.sp, color = Color.LightGray)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Оценка качества", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(result.verdictText, fontSize = 14.sp, color = Color(0xFF334155))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("КРАХМАЛ", fontSize = 10.sp, color = Color.Gray)
                    Text("~${result.starchPercentage}%", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE07A2B))
                }
            }

            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("МЯКОТЬ", fontSize = 10.sp, color = Color.Gray)
                    Text(result.fleshColor, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onStartTimer,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE07A2B)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("⏱ Запустить таймер идеальной жарки", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                val shareText = "🥔 Результат BulbaScan:\nСорт: ${result.varietyName}\nОценка: ${result.score}/10\n${result.verdictText}"
                onShare(shareText)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("📤 Поделиться вердиктом", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = onScanNew,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Сканировать другой клубень")
        }
    }
}

@Composable
fun TimerScreen(
    timerState: TimerUiState,
    onToggleTimer: () -> Unit,
    onResetTimer: () -> Unit
) {
    var servings by remember { mutableFloatStateOf(2f) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Таймер и Рецепт", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(timerState.title, fontSize = 14.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .size(200.dp)
                .clip(CircleShape)
                .background(Color(0xFFFFF7ED))
                .border(6.dp, Color(0xFFE07A2B), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    timerState.formattedTime,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (timerState.remainingSeconds <= 10 && timerState.remainingSeconds > 0) Color.Red else Color(0xFF1E293B)
                )
                Text("Жарка 1 стороны", fontSize = 12.sp, color = Color.Gray)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(
                onClick = onToggleTimer,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE07A2B)),
                modifier = Modifier.size(64.dp),
                shape = CircleShape
            ) {
                Text(if (timerState.isRunning) "❚❚" else "▶", fontSize = 22.sp)
            }

            Button(
                onClick = onResetTimer,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF64748B)),
                modifier = Modifier.size(64.dp),
                shape = CircleShape
            ) {
                Text("■", fontSize = 22.sp)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Калькулятор порций: ${servings.toInt()} чел.", fontWeight = FontWeight.Bold)

                Slider(
                    value = servings,
                    onValueChange = { servings = it },
                    valueRange = 1f..10f,
                    steps = 8
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("🥔 Картофель: ${(400 * servings.toInt())} г", fontSize = 13.sp)
                    Text("🧅 Лук: ${(75 * servings.toInt())} г", fontSize = 13.sp)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("🥚 Яйцо: ${(0.5 * servings.toInt()).toInt()} шт", fontSize = 13.sp)
                    Text("🌾 Мука: ${(10 * servings.toInt())} г", fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
fun ProfileScreen(
    profileState: UiState<ProfileUiState>,
    onUpdateName: (String) -> Unit,
    onDeleteScan: (String) -> Unit,
    onToggleTheme: (Boolean) -> Unit,
    onLogin: (String, String) -> Unit,
    onLogout: () -> Unit
) {
    val data = (profileState as? UiState.Success)?.data ?: ProfileUiState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Text("Профиль", fontSize = 24.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE2E8F0)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("👤", fontSize = 32.sp)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(data.userProfile.name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text(data.userProfile.email, fontSize = 13.sp, color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Аналитика сканирований", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Card(modifier = Modifier.weight(1f)) {
                    Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Всего", fontSize = 10.sp)
                        Text("${data.analytics.totalScans}", fontWeight = FontWeight.Bold, color = Color(0xFFE07A2B))
                    }
                }
                Card(modifier = Modifier.weight(1f)) {
                    Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Ср. Крахмал", fontSize = 10.sp)
                        Text("${String.format("%.1f", data.analytics.averageStarch)}%", fontWeight = FontWeight.Bold, color = Color(0xFFE07A2B))
                    }
                }
                Card(modifier = Modifier.weight(1f)) {
                    Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Сорт #1", fontSize = 10.sp)
                        Text(data.analytics.topVariety, fontWeight = FontWeight.Bold, color = Color(0xFFE07A2B))
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Ачивки", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(data.achievements) { ach ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (ach.isUnlocked) Color(0xFFFFF7ED) else Color(0xFFF1F5F9)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(ach.icon, fontSize = 24.sp)
                            Text(ach.title, fontSize = 10.sp, textAlign = TextAlign.Center)
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
            Text("История сканирований (Room DB)", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (data.scanHistory.isEmpty()) {
            item {
                Text("История сканирований пуста", fontSize = 13.sp, color = Color.Gray)
            }
        } else {
            items(data.scanHistory) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("${item.varietyName} (${item.culinaryType})", fontWeight = FontWeight.Bold)
                            Text("★ ${item.score}/10", fontSize = 12.sp, color = Color(0xFFE07A2B))
                        }
                        IconButton(onClick = { onDeleteScan(item.id) }) {
                            Text("🗑️")
                        }
                    }
                }
            }
        }
    }
}
