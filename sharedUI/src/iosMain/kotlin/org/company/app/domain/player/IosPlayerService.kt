package org.company.app.domain.player

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

actual class IosPlayerService : IPlayerService {
    override suspend fun prepare(url: String) { /* Không làm gì */ }
    override fun play() { /* Không làm gì */ }
    override fun pause() { /* Không làm gì */ }
    override fun seekTo(positionMs: Long) { /* Không làm gì */ }
    override fun stopAndRelease() { /* Không làm gì */ }
    override fun playbackState(): Flow<Boolean> = flowOf(false)
    override fun currentPositionMs(): Flow<Long> = flowOf(0L)
    override fun onSongCompleted(): Flow<Unit> = flowOf(Unit)
}