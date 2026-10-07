package com.absforge.ui.animation

import android.net.Uri
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.absforge.ui.theme.AbsForgePrimary

@OptIn(UnstableApi::class)
@Composable
fun ExerciseVideoPlayer(
    animationId: String,
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val videoUriStr = remember(animationId) { ExerciseMediaRegistry.getVideoAssetUri(animationId) }

    if (videoUriStr == null) {
        // Fallback to static thumbnail if no video is mapped
        ExerciseThumbnail(
            animationId = animationId,
            modifier = modifier.fillMaxSize()
        )
        return
    }

    var isReady by remember { mutableStateOf(false) }
    var hasError by remember { mutableStateOf(false) }

    if (hasError) {
        ExerciseThumbnail(
            animationId = animationId,
            modifier = modifier.fillMaxSize()
        )
        return
    }

    val exoPlayer = remember(videoUriStr) {
        try {
            ExoPlayer.Builder(context).build().apply {
                repeatMode = Player.REPEAT_MODE_ALL
                volume = 0f // Mute video audio so workout voice coach & sound effects are clear
                val mediaItem = MediaItem.fromUri(Uri.parse(videoUriStr))
                setMediaItem(mediaItem)
                prepare()
                playWhenReady = isPlaying

                addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        if (playbackState == Player.STATE_READY) {
                            isReady = true
                        }
                    }

                    override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                        android.util.Log.e("ExerciseVideoPlayer", "ExoPlayer error: ${error.message}", error)
                        hasError = true
                    }
                })
            }
        } catch (e: Exception) {
            android.util.Log.e("ExerciseVideoPlayer", "Failed to build ExoPlayer: ${e.message}", e)
            hasError = true
            null
        }
    }

    if (exoPlayer == null) {
        ExerciseThumbnail(
            animationId = animationId,
            modifier = modifier.fillMaxSize()
        )
        return
    }

    LaunchedEffect(isPlaying, exoPlayer) {
        try {
            if (isPlaying) {
                exoPlayer.play()
            } else {
                exoPlayer.pause()
            }
        } catch (e: Exception) {
            android.util.Log.e("ExerciseVideoPlayer", "Error controlling playback", e)
        }
    }

    var playerViewRef by remember { mutableStateOf<PlayerView?>(null) }

    DisposableEffect(exoPlayer) {
        onDispose {
            try {
                playerViewRef?.player = null
                exoPlayer.stop()
                exoPlayer.clearMediaItems()
                exoPlayer.release()
            } catch (e: Exception) {
                android.util.Log.e("ExerciseVideoPlayer", "Error releasing ExoPlayer", e)
            }
        }
    }

    // Clean solid dark container — NO stretched/ghost background behind video!
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0A0B0E)),
        contentAlignment = Alignment.Center
    ) {
        // Looping video view (Fitted perfectly with dark background)
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    useController = false
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                    setBackgroundColor(android.graphics.Color.TRANSPARENT)
                    playerViewRef = this
                    this.player = exoPlayer
                }
            },
            update = { playerView ->
                playerViewRef = playerView
                if (playerView.player != exoPlayer) {
                    playerView.player = exoPlayer
                }
            },
            onRelease = { playerView ->
                try {
                    playerView.player = null
                } catch (e: Exception) {
                    // Ignore release errors
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Subtle loading indicator until first video frame is ready
        if (!isReady && !hasError) {
            CircularProgressIndicator(
                color = AbsForgePrimary,
                modifier = Modifier
                    .size(36.dp)
                    .align(Alignment.Center),
                strokeWidth = 3.dp
            )
        }
    }
}
