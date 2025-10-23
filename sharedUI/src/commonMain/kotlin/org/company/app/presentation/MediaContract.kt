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
    val isLoading: Boolean = true // Mặc định là true khi VM khởi tạo
)


sealed interface MediaIntent {
    data object OnPlayPauseClick : MediaIntent
    data object OnNextClick : MediaIntent
    data object OnPreviousClick : MediaIntent
    data class OnSeek(val positionMs: Long) : MediaIntent
    data object OnFavoriteClick : MediaIntent
    data object OnPlaylistClick : MediaIntent // Nút menu bên trái (theo yêu cầu)
}

sealed interface MediaEvent {
    data class ShowError(val message: String) : MediaEvent
}