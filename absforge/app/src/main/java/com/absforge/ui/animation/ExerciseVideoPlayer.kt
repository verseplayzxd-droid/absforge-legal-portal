package com.absforge.ui.animation

import android.net.Uri
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView

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

    val exoPlayer = remember(videoUriStr) {
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
            })
        }
    }

    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            exoPlayer.play()
        } else {
            exoPlayer.pause()
        }
    }

    DisposableEffect(exoPlayer) {
        onDispose {
            exoPlayer.release()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF090B0A)),
        contentAlignment = Alignment.Center
    ) {
        // Thumbnail shown as smooth background / preview until video starts
        ExerciseThumbnail(
            animationId = animationId,
            modifier = Modifier.fillMaxSize()
        )

        // Looping video view
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    useController = false
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                    setBackgroundColor(android.graphics.Color.TRANSPARENT)
                    this.player = exoPlayer
                }
            },
            update = { playerView ->
                playerView.player = exoPlayer
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}
