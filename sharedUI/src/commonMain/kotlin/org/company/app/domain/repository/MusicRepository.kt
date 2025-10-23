package org.company.app.domain.repository

import org.company.app.data.remote.MusicApiService
import org.company.app.data.remote.Songdto
import org.company.app.domain.model.Song

interface MusicRepository {
    suspend fun getSongs(): List<Song>
}
class MusicRepositoryImpl(
    private val apiService: MusicApiService // Phụ thuộc (inject) ApiService
) : MusicRepository {

    override suspend fun getSongs(): List<Song> {
        // Gọi API, sau đó dùng .map để chuyển đổi từng item
        return apiService.getSongs().map { dto ->
            dto.toDomainModel() // Gọi hàm chuyển đổi
        }
    }

    // Hàm private giúp chuyển đổi DTO -> Model
    private fun Songdto.toDomainModel(): Song {
        return Song(
            title = this.title,
            artist = this.artist,
            // Chuyển đổi duration String sang Long, nếu lỗi thì mặc định là 0
            durationMs = this.duration.toLongOrNull() ?: 0L,
            path = this.path
        )
    }
}