package org.company.app.domain.player

import kotlinx.coroutines.flow.Flow

actual interface IPlayerService {
    actual suspend fun prepare(url: String)
    actual fun play()
    actual fun pause()
    actual fun seekTo(positionMs: Long)
    actual fun stopAndRelease()
    actual fun playbackState(): Flow<Boolean>
    actual fun currentPositionMs(): Flow<Long>
    actual fun onSongCompleted(): Flow<Unit>
}