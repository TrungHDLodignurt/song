package org.company.app.di

import org.koin.dsl.module
import kotlinx.coroutines.CoroutineScope
import org.company.app.data.remote.MusicApiService
import org.company.app.domain.repository.MusicRepository
import org.company.app.domain.repository.MusicRepositoryImpl
import org.company.app.presentation.MediaViewModel
val commonModule = module() {
    // tạo ApiService
    // Dùng `single` để Koin chỉ tạo 1 đối tượng duy nhất
    single { MusicApiService() }

    // MusicRepository
    // Dùng `factory` (tạo mới mỗi khi cần)
    // `get()` -> Koin sẽ tự động tìm `ApiService` từ công thức 1
    factory<MusicRepository> { MusicRepositoryImpl(get()) }

    // Công thức 3: Cách tạo MediaViewModel
    // Dùng `factory` (ViewModel luôn nên được tạo mới)
    // `get()` -> Koin tự tìm MusicRepository (công thức 2)
    // `get()` -> Koin tự tìm IPlayerService (sẽ được định nghĩa ở platformModule)
    // `(scope: CoroutineScope)` -> Báo Koin rằng UI (AppActivity) sẽ truyền vào 1 CoroutineScope khi yêu cầu
    factory { (scope: CoroutineScope) ->
        MediaViewModel(
            musicRepository = get(),
            playerService = get(),
            viewModelScope = scope
        )
    }
}
