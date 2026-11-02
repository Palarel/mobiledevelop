package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.ui.AppViewModelProvider
import com.example.myapplication.ui.analysis.AnalysisViewModel
import com.example.myapplication.ui.home.HomeViewModel
import com.example.myapplication.ui.profile.ProfileViewModel
import com.example.myapplication.ui.screens.BulbaNativeApp
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.timer.TimerViewModel

class MainActivity : ComponentActivity() {

    private var activeSelectedGoal: String = "Драники"
    private var analysisViewModelRef: AnalysisViewModel? = null

    // Нативная съемка камерой
    private val takePicturePreviewLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            analysisViewModelRef?.startAnalysis(activeSelectedGoal)
        }
    }

    // Выбор изображения из галереи
    private val openGalleryLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            analysisViewModelRef?.startAnalysis(activeSelectedGoal)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                val homeViewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
                val analysisViewModel: AnalysisViewModel = viewModel(factory = AppViewModelProvider.Factory)
                val timerViewModel: TimerViewModel = viewModel(factory = AppViewModelProvider.Factory)
                val profileViewModel: ProfileViewModel = viewModel(factory = AppViewModelProvider.Factory)

                analysisViewModelRef = analysisViewModel

                BulbaNativeApp(
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
                    onShareRequested = { shareText ->
                        shareVerdict(shareText)
                    },
                    onHapticRequested = { type ->
                        performHapticFeedback(type)
                    }
                )
            }
        }
    }

    private fun shareVerdict(text: String) {
        try {
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, text)
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, "Поделиться вердиктом BulbaScan")
            startActivity(shareIntent)
        } catch (e: Exception) {
            // Safe fallback
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
            // Safe fallback
        }
    }
}
