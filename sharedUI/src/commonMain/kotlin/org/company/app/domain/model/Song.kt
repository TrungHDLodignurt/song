package org.company.app.domain.model


data class Song(
    val title: String,
    val artist: String,
    val durationMs: Long,
    val path: String
)