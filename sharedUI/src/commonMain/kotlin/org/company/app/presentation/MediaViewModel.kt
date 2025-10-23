package org.company.app.presentation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.company.app.domain.model.Song
import org.company.app.domain.player.IPlayerService
import org.company.app.domain.repository.MusicRepository
// koin
class MediaViewModel(
    private val musicRepository: MusicRepository, // Phụ thuộc vào Repository
    private val playerService: IPlayerService,   // Phụ thuộc vào Player Service
    private val viewModelScope: CoroutineScope    // Cần 1 scope để chạy coroutines
) {

    private val _state = MutableStateFlow(MediaState())
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<MediaEvent>()
    val event = _event.asSharedFlow()

    private var songList: List<Song> = emptyList()
    private var currentIndex: Int = -1
    private var playerJobs: List<Job> = emptyList()

    init {
        // Bắt đầu tải danh sách bài hát ngay khi VM được tạo
        fetchSongs()
    }

    /**
     * Hàm công khai duy nhất để nhận Intent từ View.
     */
    fun processIntent(intent: MediaIntent) {
        when (intent) {
            is MediaIntent.OnPlayPauseClick -> togglePlayPause()
            is MediaIntent.OnNextClick -> loadSongAtIndex(currentIndex + 1)
            is MediaIntent.OnPreviousClick -> loadSongAtIndex(currentIndex - 1)
            is MediaIntent.OnFavoriteClick -> toggleFavorite()
            is MediaIntent.OnPlaylistClick -> {
                // Tạm thời chưa làm gì (theo yêu cầu)
                println("Playlist clicked")
            }
            is MediaIntent.OnSeek -> playerService.seekTo(intent.positionMs)
        }
    }

    /**
     * Dọn dẹp tài nguyên (hủy coroutines, giải phóng player)
     */
    fun onClear() {
        playerService.stopAndRelease()
        playerJobs.forEach { it.cancel() }
    }

    // --- Các hàm logic nội bộ ---

    /**
     * Tải danh sách bài hát từ API
     */
    private fun fetchSongs() {
        viewModelScope.launch(Dispatchers.Default) {
            _state.update { it.copy(isLoading = true) }

            val songs = musicRepository.getSongs()
            songList = songs

            if (songs.isNotEmpty()) {
                // Tải bài hát đầu tiên
                loadSongAtIndex(0)
            } else {
                _state.update { it.copy(isLoading = false) }
                // (Có thể emit Event lỗi ở đây)
            }
        }
    }

    /**
     * Tải (chuẩn bị) một bài hát tại một chỉ số (index) cụ thể.
     * Đây là hàm cốt lõi xử lý Next/Previous.
     */
    private fun loadSongAtIndex(index: Int) {
        // Kiểm tra xem index có hợp lệ không
        if (index !in songList.indices) return

        currentIndex = index
        val song = songList[index]

        viewModelScope.launch(Dispatchers.Main) {
            // 1. Chuẩn bị trình phát nhạc
            playerService.prepare(song.path)

            // 2. Cập nhật State
            _state.update {
                it.copy(
                    isLoading = false,
                    currentSong = song,
                    currentPositionMs = 0L,
                    durationMs = song.durationMs,
                    isPlaying = false, // Luôn bắt đầu ở trạng thái pause
                    isFavorite = false, // Reset favorite (theo yêu cầu)
                    hasPrevious = index > 0,
                    hasNext = index < songList.size - 1
                )
            }

            // 3. Hủy các trình lắng nghe (listener) cũ
            playerJobs.forEach { it.cancel() }

            // 4. Bắt đầu lắng nghe các cập nhật mới từ PlayerService
            listenToPlayerUpdates()
        }
    }

    /**
     * Lắng nghe các Flow từ IPlayerService để cập nhật UI
     */
    private fun listenToPlayerUpdates() {
        playerJobs = listOf(
            // Lắng nghe trạng thái Play/Pause
            playerService.playbackState()
                .onEach { isPlaying ->
                    _state.update { it.copy(isPlaying = isPlaying) }
                }
                .launchIn(viewModelScope),

            // Lắng nghe tiến độ (vị trí hiện tại)
            playerService.currentPositionMs()
                .onEach { position ->
                    _state.update { it.copy(currentPositionMs = position) }
                }
                .launchIn(viewModelScope),

            // Lắng nghe khi hết bài -> tự động Next
            playerService.onSongCompleted()
                .onEach {
                    loadSongAtIndex(currentIndex + 1)
                }
                .launchIn(viewModelScope)
        )
    }

    private fun togglePlayPause() {
        if (state.value.isPlaying) {
            playerService.pause()
        } else {
            playerService.play()
        }
    }

    private fun toggleFavorite() {
        // Chỉ lật trạng thái isFavorite trong state (theo yêu cầu)
        _state.update { it.copy(isFavorite = !it.isFavorite) }
    }
}