package org.company.app

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import org.company.app.presentation.MediaController
import org.company.app.presentation.MediaViewModel

class AppActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Giữ lại logic đổi theme của bạn
            // (Bạn có thể thay `false` bằng logic thực tế)
            ThemeChanged(isDark = false)

            // --- BẮT ĐẦU TÍCH HỢP MEDIA CONTROLLER ---

            // 1. Lấy Context
            val context = LocalContext.current

            // 2. Tạo và nhớ Factory
            val factory = remember(context) {
                AndroidMediaViewModelFactory(context.applicationContext)
            }

            // 3. Khởi tạo ViewModel bằng Factory
            val viewModel: MediaViewModel = viewModel(factory = factory)

            // 4. Lắng nghe State từ ViewModel
            val state by viewModel.state.collectAsState()

            // 5. Xử lý Lifecycle (Quan trọng!)
            // Gọi onClear() khi Activity bị hủy để giải phóng MediaPlayer
            val lifecycleOwner = LocalLifecycleOwner.current
            DisposableEffect(lifecycleOwner) {
                val observer = LifecycleEventObserver { _, event ->
                    // (Bạn có thể muốn dùng ON_PAUSE hoặc ON_STOP tùy logic)
                    if (event == Lifecycle.Event.ON_DESTROY) {
                        viewModel.onClear()
                    }
                }
                lifecycleOwner.lifecycle.addObserver(observer)

                // Tự động gỡ observer khi Composable bị hủy
                onDispose {
                    lifecycleOwner.lifecycle.removeObserver(observer)
                }
            }

            // 6. Hiển thị UI
            // Đặt Controller ở cuối màn hình
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 24.dp), // Đệm
                contentAlignment = Alignment.BottomCenter // Căn chỉnh xuống dưới
            ) {
                MediaController(
                    state = state,
                    onIntent = viewModel::processIntent // Kết nối Intent
                )
            }
        }
    }
}

@Composable
private fun ThemeChanged(isDark: Boolean) {
    val view = LocalView.current
    LaunchedEffect(isDark) {
        val window = (view.context as Activity).window
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = !isDark // Nền sáng -> icon tối
            isAppearanceLightNavigationBars = !isDark
        }
    }
}