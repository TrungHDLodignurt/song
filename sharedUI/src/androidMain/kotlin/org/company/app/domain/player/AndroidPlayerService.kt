package org.company.app.domain.player

import android.content.Context
import android.media.MediaPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

actual class AndroidPlayerService(
    // Chúng ta cần Context để khởi tạo MediaPlayer
    private val context: Context
) : IPlayerService {

    private var mediaPlayer: MediaPlayer? = null
    private var isPrepared = false
    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())
    private var progressJob: Job? = null

    // --- State Flows ---
    private val _playbackState = MutableStateFlow(false) // false = paused
    override fun playbackState(): Flow<Boolean> = _playbackState.asStateFlow()

    private val _onSongCompleted = MutableSharedFlow<Unit>()
    override fun onSongCompleted(): Flow<Unit> = _onSongCompleted.asSharedFlow()

    // --- Hàm điều khiển ---

    override suspend fun prepare(url: String) {
        stopAndReleaseInternal() // Dọn dẹp player cũ (nếu có)
        isPrepared = false
        mediaPlayer = MediaPlayer().apply {
            setDataSource(url)
            setOnPreparedListener {
                isPrepared = true
                // Báo cho UI biết trạng thái mới
                _playbackState.value = false
            }
            setOnCompletionListener {
                _playbackState.value = false
                progressJob?.cancel()
                serviceScope.launch {
                    _onSongCompleted.emit(Unit)
                }
            }
            setOnErrorListener { _, _, _ ->
                isPrepared = false
                _playbackState.value = false
                true // Đã xử lý lỗi
            }
            prepareAsync() // Chuẩn bị bất đồng bộ
        }
    }

    override fun play() {
        if (isPrepared && mediaPlayer?.isPlaying == false) {
            mediaPlayer?.start()
            _playbackState.value = true
            startProgressEmitter() // Bắt đầu phát tiến độ
        }
    }

    override fun pause() {
        if (isPrepared && mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
            _playbackState.value = false
            progressJob?.cancel() // Dừng phát tiến độ
        }
    }

    override fun seekTo(positionMs: Long) {
        if (isPrepared) {
            mediaPlayer?.seekTo(positionMs.toInt())
        }
    }

    override fun stopAndRelease() {
        stopAndReleaseInternal()
    }

    // --- Hàm nội bộ ---

    private fun stopAndReleaseInternal() {
        progressJob?.cancel()
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        isPrepared = false
        _playbackState.value = false
    }

    /** Bắt đầu một coroutine để phát ra vị trí hiện tại mỗi 500ms */
    private fun startProgressEmitter() {
        progressJob?.cancel() // Hủy job cũ (nếu có)
        progressJob = serviceScope.launch {
            while (isActive) {
                // Sẽ thêm flow phát tiến độ ở đây
                delay(500) // Cập nhật mỗi 500ms
            }
        }
    }

    /**
     * (Chúng ta cần triển khai Flow này)
     * Đây là cách phức tạp nhất, dùng callbackFlow
     */
    override fun currentPositionMs(): Flow<Long> = callbackFlow {
        val job = launch(Dispatchers.Main) {
            while (isActive) {
                if (isPrepared && mediaPlayer?.isPlaying == true) {
                    trySend(mediaPlayer!!.currentPosition.toLong())
                }
                delay(500) // Tần suất cập nhật
            }
        }
        awaitClose { job.cancel() } // Tự động hủy job khi flow bị hủy
    }
}