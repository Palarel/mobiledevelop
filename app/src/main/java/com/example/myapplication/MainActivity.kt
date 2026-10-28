package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.ui.AppViewModelProvider
import com.example.myapplication.ui.analysis.AnalysisViewModel
import com.example.myapplication.ui.home.HomeViewModel
import com.example.myapplication.ui.profile.ProfileViewModel
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.timer.TimerViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : ComponentActivity() {

    private var currentWebView: WebView? = null
    private var activeSelectedGoal: String = "Драники"
    private var analysisViewModelRef: AnalysisViewModel? = null

    // Нативная съемка камерой
    private val takePicturePreviewLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            currentWebView?.post {
                currentWebView?.evaluateJavascript("window.openScreen('analysis');", null)
            }
            analysisViewModelRef?.startAnalysis(activeSelectedGoal)
        }
    }

    // Выбор изображения из галереи
    private val openGalleryLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            currentWebView?.post {
                currentWebView?.evaluateJavascript("window.openScreen('analysis');", null)
            }
            analysisViewModelRef?.startAnalysis(activeSelectedGoal)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val homeViewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
                    val analysisViewModel: AnalysisViewModel = viewModel(factory = AppViewModelProvider.Factory)
                    val timerViewModel: TimerViewModel = viewModel(factory = AppViewModelProvider.Factory)
                    val profileViewModel: ProfileViewModel = viewModel(factory = AppViewModelProvider.Factory)

                    analysisViewModelRef = analysisViewModel

                    ObserveViewModelStates(
                        homeViewModel = homeViewModel,
                        analysisViewModel = analysisViewModel,
                        timerViewModel = timerViewModel,
                        profileViewModel = profileViewModel
                    )

                    BulbaScanWebView(
                        homeViewModel = homeViewModel,
                        analysisViewModel = analysisViewModel,
                        timerViewModel = timerViewModel,
                        profileViewModel = profileViewModel,
                        onCameraRequested = {
                            takePicturePreviewLauncher.launch(null)
                        },
                        onGalleryRequested = {
                            openGalleryLauncher.launch("image/*")
                        },
                        onHapticRequested = { type ->
                            performHapticFeedback(type)
                        },
                        onWebViewCreated = { webView ->
                            currentWebView = webView
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    )
                }
            }
        }
    }

    private fun performHapticFeedback(type: String) {
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vibratorManager.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }

            if (!vibrator.hasVibrator()) return

            val effect = when (type) {
                "click" -> VibrationEffect.createOneShot(12, VibrationEffect.DEFAULT_AMPLITUDE)
                "scan_tick" -> VibrationEffect.createOneShot(22, 120)
                "success" -> VibrationEffect.createWaveform(longArrayOf(0, 30, 50, 60), -1)
                "error" -> VibrationEffect.createWaveform(longArrayOf(0, 80, 40, 80), -1)
                else -> VibrationEffect.createOneShot(15, VibrationEffect.DEFAULT_AMPLITUDE)
            }
            vibrator.vibrate(effect)
        } catch (e: Exception) {
            // Ignore if vibration fails
        }
    }

    @Composable
    private fun ObserveViewModelStates(
        homeViewModel: HomeViewModel,
        analysisViewModel: AnalysisViewModel,
        timerViewModel: TimerViewModel,
        profileViewModel: ProfileViewModel
    ) {
        LaunchedEffect(Unit) {
            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    launch {
                        homeViewModel.uiState.collectLatest { state ->
                            if (state is com.example.myapplication.ui.state.UiState.Success) {
                                activeSelectedGoal = state.data.selectedRecipe
                                timerViewModel.setGoalTimer(state.data.selectedRecipe)
                                val json = JSONObject().apply {
                                    put("type", "home")
                                    put("selectedRecipe", state.data.selectedRecipe)
                                    put("isAiCalibrated", state.data.isAiCalibrated)
                                }.toString()
                                sendStateToWebView(json)
                            }
                        }
                    }

                    launch {
                        analysisViewModel.uiState.collectLatest { state ->
                            val json = JSONObject().apply {
                                put("type", "analysis")
                                put("progressPercentage", state.progressPercentage)
                                put("isComplete", state.isComplete)
                                put("statusText", state.statusText)
                                state.scanVerdict?.let { verdict ->
                                    val verdictObj = JSONObject().apply {
                                        put("varietyName", verdict.varietyName)
                                        put("culinaryType", verdict.culinaryType)
                                        put("starchPercentage", verdict.starchPercentage)
                                        put("fleshColor", verdict.fleshColor)
                                        put("accuracyPercentage", verdict.accuracyPercentage)
                                        put("score", verdict.score)
                                        put("verdictTitle", verdict.verdictTitle)
                                        put("verdictText", verdict.verdictText)
                                        put("warningMessage", verdict.warningMessage ?: "")
                                    }
                                    put("verdict", verdictObj)
                                }
                            }.toString()
                            sendStateToWebView(json)
                        }
                    }

                    launch {
                        timerViewModel.uiState.collectLatest { state ->
                            val json = JSONObject().apply {
                                put("type", "timer")
                                put("remainingSeconds", state.remainingSeconds)
                                put("isRunning", state.isRunning)
                                put("isFinished", state.isFinished)
                                put("formattedTime", state.formattedTime)
                                put("title", state.title)
                            }.toString()
                            sendStateToWebView(json)
                        }
                    }

                    launch {
                        profileViewModel.uiState.collectLatest { state ->
                            if (state is com.example.myapplication.ui.state.UiState.Success) {
                                val json = JSONObject().apply {
                                    put("type", "profile")
                                    put("name", state.data.userProfile.name)
                                    put("email", state.data.userProfile.email)
                                    put("totalScans", state.data.userProfile.totalScansCount)
                                    put("isDarkTheme", state.data.isDarkTheme)

                                    val historyArray = JSONArray()
                                    state.data.scanHistory.forEach { scan ->
                                        historyArray.put(JSONObject().apply {
                                            put("id", scan.id)
                                            put("varietyName", scan.varietyName)
                                            put("culinaryType", scan.culinaryType)
                                            put("score", scan.score)
                                            put("verdictTitle", scan.verdictTitle)
                                            put("timestamp", scan.timestamp)
                                        })
                                    }
                                    put("scanHistory", historyArray)
                                }.toString()
                                sendStateToWebView(json)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun sendStateToWebView(jsonString: String) {
        val escapedJson = jsonString.replace("'", "\\'").replace("\n", "\\n")
        currentWebView?.post {
            currentWebView?.evaluateJavascript(
                "if (window.onStateUpdate) { window.onStateUpdate('$escapedJson'); }",
                null
            )
        }
    }
}

class AndroidBridge(
    private val homeViewModel: HomeViewModel,
    private val analysisViewModel: AnalysisViewModel,
    private val timerViewModel: TimerViewModel,
    private val profileViewModel: ProfileViewModel,
    private val onCameraRequested: () -> Unit,
    private val onGalleryRequested: () -> Unit,
    private val onHapticRequested: (String) -> Unit
) {
    @JavascriptInterface
    fun selectRecipe(recipe: String) {
        homeViewModel.selectRecipe(recipe)
        timerViewModel.setGoalTimer(recipe)
    }

    @JavascriptInterface
    fun startAnalysis() {
        analysisViewModel.startAnalysis()
    }

    @JavascriptInterface
    fun cancelAnalysis() {
        analysisViewModel.cancelAnalysis()
    }

    @JavascriptInterface
    fun toggleTimer() {
        timerViewModel.toggleTimer()
    }

    @JavascriptInterface
    fun resetTimer() {
        timerViewModel.resetTimer()
    }

    @JavascriptInterface
    fun updateProfileName(newName: String) {
        profileViewModel.updateName(newName)
    }

    @JavascriptInterface
    fun deleteScan(id: String) {
        profileViewModel.deleteScanHistoryItem(id)
    }

    @JavascriptInterface
    fun setDarkTheme(isDark: Boolean) {
        profileViewModel.toggleTheme(isDark)
    }

    @JavascriptInterface
    fun openCamera() {
        onCameraRequested()
    }

    @JavascriptInterface
    fun openGallery() {
        onGalleryRequested()
    }

    @JavascriptInterface
    fun triggerHaptic(type: String) {
        onHapticRequested(type)
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun BulbaScanWebView(
    homeViewModel: HomeViewModel,
    analysisViewModel: AnalysisViewModel,
    timerViewModel: TimerViewModel,
    profileViewModel: ProfileViewModel,
    onCameraRequested: () -> Unit,
    onGalleryRequested: () -> Unit,
    onHapticRequested: (String) -> Unit,
    onWebViewCreated: (WebView) -> Unit,
    modifier: Modifier = Modifier
) {
    var webView: WebView? by remember { mutableStateOf(null) }
    var canGoBack by remember { mutableStateOf(false) }

    BackHandler(enabled = canGoBack) {
        webView?.goBack()
    }

    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )

                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    allowFileAccess = true
                    allowContentAccess = true
                    useWideViewPort = true
                    loadWithOverviewMode = true
                    mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                }

                addJavascriptInterface(
                    AndroidBridge(
                        homeViewModel = homeViewModel,
                        analysisViewModel = analysisViewModel,
                        timerViewModel = timerViewModel,
                        profileViewModel = profileViewModel,
                        onCameraRequested = onCameraRequested,
                        onGalleryRequested = onGalleryRequested,
                        onHapticRequested = onHapticRequested
                    ),
                    "AndroidNative"
                )

                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        canGoBack = view?.canGoBack() == true
                    }
                }

                webChromeClient = object : WebChromeClient() {
                    override fun onPermissionRequest(request: PermissionRequest?) {
                        request?.grant(request.resources)
                    }
                }

                loadUrl("file:///android_asset/index.html")
                webView = this
                onWebViewCreated(this)
            }
        },
        update = {
            webView = it
        }
    )
}
