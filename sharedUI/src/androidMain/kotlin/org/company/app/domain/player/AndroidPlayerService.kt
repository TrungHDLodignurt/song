package org.company.app.domain.player

import kotlinx.coroutines.flow.Flow
import android.content.Context
import android.media.MediaPlayer
import androidx.core.net.toUri
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds
class AndroidPlayerService(
    private val context: Context
) : IPlayerService {
    private var mediaPlayer: MediaPlayer? = null
    private var positionJob: Job? = null

    private val _playbackState = MutableStateFlow(false)
    override fun playbackState(): Flow<Boolean> = _playbackState.asStateFlow()

    private val _onSongCompleted = MutableSharedFlow<Unit>()
    override fun onSongCompleted(): Flow<Unit> = _onSongCompleted

    override suspend fun prepare(url: String) {
        stopAndRelease() // Dọn dẹp player cũ (nếu có)

        mediaPlayer = MediaPlayer().apply {
            setDataSource(context, url.toUri())
            setOnPreparedListener {
                _playbackState.value = false
            }
            setOnCompletionListener {
                _playbackState.value = false
                positionJob?.cancel()
                GlobalScope.launch { _onSongCompleted.emit(Unit) }
            }
            prepareAsync()
        }
    }

    // Phát nhạc
    override fun play() {
        mediaPlayer?.start()
        _playbackState.value = true
        startPositionUpdates() // Bắt đầu cập nhật thanh tua
    }

    // Tạm dừng
    override fun pause() {
        mediaPlayer?.pause()
        _playbackState.value = false
        positionJob?.cancel() // Dừng cập nhật thanh tua
    }

    // Tua nhạc
    override fun seekTo(positionMs: Long) {
        mediaPlayer?.seekTo(positionMs.toInt())
    }

    // Dọn dẹp
    override fun stopAndRelease() {
        positionJob?.cancel()
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        _playbackState.value = false
    }

    // Trả về vị trí hiện tại (dùng Flow)
    override fun currentPositionMs(): Flow<Long> = flow {
        // Vòng lặp này sẽ tự động bị hủy khi coroutine (positionJob) bị cancel
        while (true) {
            emit(mediaPlayer?.currentPosition?.toLong() ?: 0L)
            delay(100.milliseconds) // Cập nhật 10 lần mỗi giây
        }
    }

    // Hàm nội bộ để bắt đầu/dừng Flow cập nhật vị trí
    @OptIn(DelicateCoroutinesApi::class)
    private fun startPositionUpdates() {
        positionJob?.cancel()
        positionJob = GlobalScope.launch(Dispatchers.Main) {
            // Khởi chạy flow `currentPositionMs`
            // ViewModel sẽ thu thập (collect) từ flow này
            currentPositionMs().collect {
                // Chúng ta không cần làm gì ở đây,
                // chỉ cần đảm bảo coroutine này chạy
                if (!isActive) return@collect
            }
        }
    }
}