package org.company.app

import android.app.Application
import org.company.app.di.commonModule
import org.company.app.di.platformModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

/**
 * File này chạy đầu tiên khi ứng dụng khởi động.
 * Nhiệm vụ của nó là "Bật" Koin lên.
 */
class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Khởi động Koin
        startKoin {
            // 1. Cung cấp Context của Android cho Koin
            // (Đây là nơi `get<Context>()` trong PlatformModule sẽ tìm thấy)
            androidContext(this@MainApplication)

            // 2. Nạp tất cả "công thức" của chúng ta vào Koin
            modules(
                commonModule,   // Công thức chung (tạo ViewModel, Repo...)
                platformModule  // Công thức Android (tạo AndroidPlayerService)
            )
        }
    }
}