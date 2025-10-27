package org.company.app.presentation

import org.company.app.domain.model.Song

data class MediaState(
    // Trạng thái bài hát
    val currentSong: Song? = null,
    val isPlaying: Boolean = false,
    val currentPositionMs: Long = 0L,
    val durationMs: Long = 0L,

    // Trạng thái điều khiển
    val hasNext: Boolean = false,
    val hasPrevious: Boolean = false,
    val isFavorite: Boolean = false, // Trạng thái favorite tạm thời (theo yêu cầu)

    // Trạng thái tải
    val isLoading: Boolean = true, // Mặc định là true khi VM khởi tạo

    // Trạng thái seek cho UI dumb
    val isSeeking: Boolean = false,
    val seekingPositionMs: Long = 0L
)


sealed interface MediaIntent {
    data object OnPlayPauseClick : MediaIntent
    data object OnNextClick : MediaIntent
    data object OnPreviousClick : MediaIntent
    // UI dumb: báo vị trí khi đang kéo và khi kết thúc
    data class OnSeeking(val positionMs: Long) : MediaIntent
    data class OnSeekEnd(val positionMs: Long) : MediaIntent
    data object OnFavoriteClick : MediaIntent
    data object OnPlaylistClick : MediaIntent
}

sealed interface MediaEvent {
    data class ShowError(val message: String) : MediaEvent
}