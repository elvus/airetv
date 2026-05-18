package com.airetv.app.ui.player

import android.content.Context
import android.os.Build
import android.util.Log
import android.view.KeyEvent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.airetv.app.data.model.Channel
import com.airetv.app.ui.components.LoadingIndicator
import kotlinx.coroutines.delay

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(UnstableApi::class)
@Composable
fun PlayerScreen(
    context: Context,
    channelId: String,
    channels: List<Channel>,
    onRefresh: () -> Unit = {}
) {
    val context = LocalContext.current
    val initialIndex = remember(channels) {
        channels.indexOfFirst { it.id == channelId }.coerceAtLeast(0)
    }

    var selectedIndex by remember { mutableIntStateOf(initialIndex) }
    var isFullscreen by remember { mutableStateOf(false) }
    var showOverlay by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    var inactivityResetKey by remember { mutableIntStateOf(0) }
    val fullscreenFocusRequester = remember { FocusRequester() }

    val currentChannel = channels[selectedIndex]

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(channels[initialIndex].streamUrl))
            playWhenReady = true
            prepare()
        }
    }

    // Player state listener
    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_READY) {
                    isLoading = false
                }
            }
        }
        exoPlayer.addListener(listener)
        onDispose {
            exoPlayer.removeListener(listener)
            exoPlayer.release()
        }
    }

    // Auto-fullscreen after 5 seconds of inactivity when sidebar is showing
    LaunchedEffect(isFullscreen, inactivityResetKey) {
        if (!isFullscreen) {
            delay(7000)
            isFullscreen = true
        }
    }

    // Auto-hide overlay after 5 seconds
    LaunchedEffect(showOverlay) {
        if (showOverlay) {
            delay(5000)
            showOverlay = false
        }
    }

    // Show overlay and request focus when entering fullscreen
    LaunchedEffect(isFullscreen) {
        if (isFullscreen) {
            showOverlay = true
            fullscreenFocusRequester.requestFocus()
        }
    }

    BackHandler(enabled = isFullscreen) {
        isFullscreen = false
        showOverlay = false
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Video player (always fullscreen in background)
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = false
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Sidebar mode (animated in/out)
        AnimatedVisibility(
            visible = !isFullscreen,
            enter = fadeIn(tween(300)) + slideInHorizontally(tween(400)) { -it },
            exit = fadeOut(tween(300)) + slideOutHorizontally(tween(400)) { -it }
        ) {
            Log.d("SelectedIndex", selectedIndex.toString())
            ChannelSidebar(
                channels = channels,
                selectedIndex = selectedIndex,
                initialFocusIndex = selectedIndex,
                onChannelFocused = { index ->
                    inactivityResetKey++
                    if (index != selectedIndex) {
                        selectedIndex = index
                        isLoading = true
                        exoPlayer.stop()
                        exoPlayer.clearMediaItems()
                        exoPlayer.setMediaItem(MediaItem.fromUri(channels[index].streamUrl))
                        exoPlayer.prepare()
                        exoPlayer.playWhenReady = true
                    }
                },
                onChannelSelected = { isFullscreen = true },
                onRefresh = onRefresh
            )
        }

        // Fullscreen mode: key handler + overlay
        if (isFullscreen) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .focusRequester(fullscreenFocusRequester)
                    .focusable()
                    .onKeyEvent { event ->
                        if (event.nativeKeyEvent.action == KeyEvent.ACTION_DOWN) {
                            when (event.nativeKeyEvent.keyCode) {
                                KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER -> {
                                    isFullscreen = false
                                    showOverlay = false
                                    true
                                }
                                KeyEvent.KEYCODE_DPAD_UP, KeyEvent.KEYCODE_DPAD_DOWN -> {
                                    isFullscreen = false
                                    showOverlay = false
                                    true
                                }
                                else -> false
                            }
                        } else false
                    }
            )

            PlayerOverlay(
                channel = currentChannel,
                isVisible = showOverlay,

            )
        }

        // Loading indicator
        if (isLoading) {
            LoadingIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}
