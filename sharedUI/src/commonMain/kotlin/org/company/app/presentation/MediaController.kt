package org.company.app.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// --- CÁC IMPORT MỚI ĐỂ DÙNG RESOURCES ---
import org.jetbrains.compose.resources.painterResource
import song.sharedui.generated.resources.Res
import song.sharedui.generated.resources.back_svgrepo_com
import song.sharedui.generated.resources.hamburger_menu_svgrepo_com
import song.sharedui.generated.resources.heart_svgrepo_com
import song.sharedui.generated.resources.heartfill
import song.sharedui.generated.resources.next_svgrepo_com
import song.sharedui.generated.resources.pause_svgrepo_com
import song.sharedui.generated.resources.play_svgrepo_com

private val controllerBackgroundColor = Color(0xFF2E204D)
private val iconTintColor = Color.White

@Composable
fun MediaController(
    state: MediaState,
    onIntent: (MediaIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    var isSeeking by remember { mutableStateOf(false) }
    var seekPosition by remember { mutableStateOf(0f) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(controllerBackgroundColor)
            .padding(vertical = 12.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // ... (Phần 1, 2, 3: Text và Slider không thay đổi) ...

        // 1. Thông tin bài hát (Title & Artist)
        Text(
            text = state.currentSong?.title ?: "Loading...",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = state.currentSong?.artist ?: "...",
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(Modifier.height(8.dp))

        // 2. Thanh Slider
        Column(Modifier.fillMaxWidth()) {
            Slider(
                value = if (isSeeking) seekPosition else state.currentPositionMs.toFloat(),
                onValueChange = { newValue ->
                    isSeeking = true
                    seekPosition = newValue
                },
                onValueChangeFinished = {
                    onIntent(MediaIntent.OnSeek(seekPosition.toLong()))
                    isSeeking = false
                },
                valueRange = 0f..(state.durationMs.toFloat().coerceAtLeast(1f)),
                colors = SliderDefaults.colors(
                    thumbColor = Color.White,
                    activeTrackColor = Color.White,
                    inactiveTrackColor = Color.Gray.copy(alpha = 0.5f)
                )
            )

            // 3. Thời gian (Current / Duration)
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val currentPos = if (isSeeking) seekPosition.toLong() else state.currentPositionMs
                Text(
                    text = formatTime(currentPos),
                    color = iconTintColor.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
                Text(
                    text = formatTime(state.durationMs),
                    color = iconTintColor.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
            }
        }
        Spacer(Modifier.height(8.dp))

        // 4. Hàng nút điều khiển
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Nút Playlist (Trái)
            IconButton(onClick = { onIntent(MediaIntent.OnPlaylistClick) }) {
                Icon(
                    // --- ĐÃ THAY ĐỔI ---
                    painter = painterResource(Res.drawable.hamburger_menu_svgrepo_com),
                    contentDescription = "Playlist",
                    tint = iconTintColor,
                    modifier = Modifier.size(28.dp)
                )
            }

            // Nút Previous
            IconButton(
                onClick = { onIntent(MediaIntent.OnPreviousClick) },
                enabled = state.hasPrevious
            ) {
                Icon(
                    // --- ĐÃ THAY ĐỔI ---
                    painter = painterResource(Res.drawable.back_svgrepo_com),
                    contentDescription = "Previous",
                    tint = if (state.hasPrevious) iconTintColor else iconTintColor.copy(alpha = 0.3f),
                    modifier = Modifier.size(36.dp)
                )
            }

            // Nút Play/Pause (Giữa, to)
            IconButton(
                onClick = { onIntent(MediaIntent.OnPlayPauseClick) },
                modifier = Modifier
                    .size(56.dp)
                    .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(50))
            ) {
                Icon(
                    // --- ĐÃ THAY ĐỔI ---
                    painter = painterResource(
                        if (state.isPlaying) Res.drawable.pause_svgrepo_com
                        else Res.drawable.play_svgrepo_com
                    ),
                    contentDescription = if (state.isPlaying) "Pause" else "Play",
                    tint = iconTintColor,
                    modifier = Modifier.size(40.dp)
                )
            }

            // Nút Next
            IconButton(
                onClick = { onIntent(MediaIntent.OnNextClick) },
                enabled = state.hasNext
            ) {
                Icon(
                    // --- ĐÃ THAY ĐỔI ---
                    painter = painterResource(Res.drawable.next_svgrepo_com),
                    contentDescription = "Next",
                    tint = if (state.hasNext) iconTintColor else iconTintColor.copy(alpha = 0.3f),
                    modifier = Modifier.size(36.dp)
                )
            }

            // Nút Favorite (Phải)
            IconButton(onClick = { onIntent(MediaIntent.OnFavoriteClick) }) {
                Icon(
                    // --- ĐÃ THAY ĐỔI ---
                    painter = painterResource(
                        if (state.isFavorite) Res.drawable.heart_svgrepo_com
                        else Res.drawable.heartfill
                    ),
                    contentDescription = "Favorite",
                    tint = if (state.isFavorite) Color.Red else iconTintColor,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

private fun formatTime(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = (totalSeconds / 60).toString()
    val seconds = (totalSeconds % 60).toString()

    return "${minutes.padStart(2, '0')}:${seconds.padStart(2, '0')}"
}