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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
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
import androidx.compose.runtime.CompositionLocalProvider

// --- IMPORTS BỔ SUNG CẦN THIẾT ---
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.indication // Thêm
import androidx.compose.foundation.shape.CircleShape // Thêm
import androidx.compose.foundation.layout.Box // Thêm
import androidx.compose.foundation.gestures.detectTapGestures
import org.jetbrains.compose.resources.painterResource
import song.sharedui.generated.resources.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned

private val controllerBackgroundColor = Color(0xFF2E204D)
private val iconTintColor = Color.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaController(
    state: MediaState,
    onIntent: (MediaIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(controllerBackgroundColor)
            .padding(vertical = 12.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

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

        CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
            var sliderWidthPx by remember { mutableStateOf(0) }
            var lastDragValue by remember { mutableStateOf(0L) }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coords -> sliderWidthPx = coords.size.width }
                    .pointerInput(state.durationMs) {
                        detectTapGestures { offset ->
                            val dur = state.durationMs
                            if (dur > 0 && sliderWidthPx > 0) {
                                val fraction = (offset.x / sliderWidthPx).coerceIn(0f, 1f)
                                val posMs = (fraction * dur).toLong()
                                lastDragValue = posMs
                                onIntent(MediaIntent.OnSeekEnd(posMs))
                            }
                        }
                    }
            ) {
                Slider(
                    value = (
                        if (state.isSeeking) state.seekingPositionMs
                        else state.currentPositionMs
                    ).toFloat(),
                    onValueChange = { newValue ->
                        val v = newValue.toLong()
                        lastDragValue = v
                        onIntent(MediaIntent.OnSeeking(v))
                    },
                    onValueChangeFinished = {
                        onIntent(MediaIntent.OnSeekEnd(lastDragValue))
                    },
                    valueRange = 0f..(state.durationMs.toFloat().coerceAtLeast(1f)),
                    interactionSource = interactionSource,
                    modifier = Modifier
                        .fillMaxWidth()
                        .indication(interactionSource, null),
                    colors = SliderDefaults.colors(
                        thumbColor = Color.Transparent,
                        activeTrackColor = Color.White,
                        inactiveTrackColor = Color.Gray.copy(alpha = 0.5f)
                    ),
                    thumb = {
                        Box(
                            Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .indication(interactionSource, null)
                        )
                    }
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val displayPos = if (state.isSeeking) state.seekingPositionMs else state.currentPositionMs
            Text(
                text = formatTime(displayPos),
                color = iconTintColor.copy(alpha = 0.7f),
                fontSize = 12.sp
            )
            Text(
                text = formatTime(state.durationMs),
                color = iconTintColor.copy(alpha = 0.7f),
                fontSize = 12.sp
            )
        }

        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Nút Playlist (Trái)
            IconButton(onClick = { onIntent(MediaIntent.OnPlaylistClick) }) {
                Icon(
                    // --- ĐÃ THAY ĐỔI ---
                    painter = painterResource(Res.drawable.ham),
                    contentDescription = "Playlist",
                    tint = iconTintColor,
                    modifier = Modifier.size(28.dp)
                )
            }

            IconButton(
                onClick = { onIntent(MediaIntent.OnPreviousClick) },
                enabled = state.hasPrevious
            ) {
                Icon(
                    // --- ĐÃ THAY ĐỔI ---
                    painter = painterResource(Res.drawable.back),
                    contentDescription = "Previous",
                    tint = if (state.hasPrevious) iconTintColor else iconTintColor.copy(alpha = 0.3f),
                    modifier = Modifier.size(36.dp)
                )
            }
            IconButton(
                onClick = { onIntent(MediaIntent.OnPlayPauseClick) },
                modifier = Modifier
                    .size(56.dp)
                    .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(50))
            ) {
                Icon(
                    // --- ĐÃ THAY ĐỔI ---
                    painter = painterResource(
                        if (state.isPlaying) Res.drawable.pause
                        else Res.drawable.play
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
                    painter = painterResource(Res.drawable.next),
                    contentDescription = "Next",
                    tint = if (state.hasNext) iconTintColor else iconTintColor.copy(alpha = 0.3f),
                    modifier = Modifier.size(28.dp)
                )
            }

            // Nút Favorite (Phải)
            IconButton(onClick = { onIntent(MediaIntent.OnFavoriteClick) }) {
                Icon(
                    // --- ĐÃ THAY ĐỔI ---
                    painter = painterResource(
                        if (state.isFavorite) Res.drawable.heartfill
                        else Res.drawable.heart
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
