package org.company.app

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import org.company.app.data.remote.MusicApiService
import org.company.app.domain.player.AndroidPlayerService
import org.company.app.domain.repository.MusicRepositoryImpl
import org.company.app.presentation.MediaViewModel

/**
 * Factory này sẽ tự tay "lắp ráp" tất cả các dependency
 * và tạo ra MediaViewModel cho chúng ta.
 */
class AndroidMediaViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MediaViewModel::class.java)) {

            // 1. Tạo ApiService (không cần gì)
            val apiService = MusicApiService()

            // 2. Tạo Repository (cần ApiService)
            val repository = MusicRepositoryImpl(apiService)

            // 3. Tạo PlayerService (cần Context)
            val playerService = AndroidPlayerService(context.applicationContext)

            // 4. Tạo ViewModel (cần Repository, PlayerService, và CoroutineScope)
            // Chúng ta sẽ dùng "FakeViewModel" để lấy viewModelScope
            val fakeViewModel = object : ViewModel() {}

            @Suppress("UNCHECKED_CAST")
            return MediaViewModel(
                musicRepository = repository,
                playerService = playerService,
                viewModelScope = fakeViewModel.viewModelScope // Lấy scope từ 1 VM thật
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}