package org.company.app.domain.player

import kotlinx.coroutines.flow.Flow

expect interface IPlayerService {

    /**
     * Chuẩn bị một bài hát mới từ URL.
     * @param url Đường dẫn (URL) của file mp3.
     */
    suspend fun prepare(url: String)

    /** Bắt đầu phát (hoặc tiếp tục phát nếu đang tạm dừng). */
    fun play()

    /** Tạm dừng phát. */
    fun pause()

    /** Tua đến một vị trí cụ thể.
     * @param positionMs Vị trí tính bằng mili giây.
     */
    fun seekTo(positionMs: Long)

    /** Dừng hẳn và giải phóng tài nguyên (MediaPlayer). */
    fun stopAndRelease()

    /**
     * Một Flow phát ra trạng thái đang phát (true) hoặc tạm dừng (false).
     */
    fun playbackState(): Flow<Boolean> // true = playing, false = paused/stopped

    /**
     * Một Flow phát ra vị trí hiện tại của bài hát (cập nhật mỗi giây).
     */
    fun currentPositionMs(): Flow<Long>

    /**
     * Một Flow phát ra sự kiện khi bài hát kết thúc tự nhiên.
     */
    fun onSongCompleted(): Flow<Unit>
}