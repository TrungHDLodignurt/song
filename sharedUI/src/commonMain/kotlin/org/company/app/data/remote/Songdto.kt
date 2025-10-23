package org.company.app.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class Songdto(
    val title: String,
    val artist: String,
    val kind: String,
    val duration: String,
    val path: String
)