package org.company.app.domain.player

import kotlinx.coroutines.flow.Flow

interface IPlayerService {
    suspend fun prepare(url: String)
    fun play()
    fun pause()
    fun seekTo(positionMs: Long)
    fun stopAndRelease()
    fun playbackState(): Flow<Boolean>
    fun currentPositionMs(): Flow<Long>
    fun onSongCompleted(): Flow<Unit>
}