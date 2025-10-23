package org.company.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// --- CÁC IMPORT CỦA BẠN TỪ SHAREDUI ---
import org.company.app.presentation.MediaController
import org.company.app.presentation.MediaViewModel

// --- IMPORT CHÍNH XÁC NẰM Ở ĐÂY ---
// (Mặc kệ nó nếu nó vẫn báo đỏ)
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

class AppActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Đổi tên thành "MusicPlayerApp" để TRÁNH LỖI "TYPHOON"
            MusicPlayerApp()
        }
    }
}

@Composable
fun MusicPlayerApp() { // <-- Đã đổi tên
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {

            val coroutineScope = rememberCoroutineScope()

            // --- ĐÂY LÀ DÒNG SỬA LỖI CRASH ---
            val viewModel: MediaViewModel = koinViewModel(

                parameters = { parametersOf(coroutineScope) }

            )

            val state = viewModel.state.collectAsState()

            DisposableEffect(viewModel) {
                onDispose {
                    viewModel.onClear()
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 24.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                MediaController(
                    state = state.value,
                    onIntent = viewModel::processIntent
                )
            }
        }
    }
}