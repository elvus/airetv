package com.airetv.app.ui.player

import android.util.Log
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import com.airetv.app.R
import com.airetv.app.data.model.Channel
import com.airetv.app.ui.theme.GoldAccent
import com.airetv.app.ui.theme.GoldFocus
import com.airetv.app.ui.theme.RedPrimary
import com.airetv.app.ui.theme.SurfaceVariant
import com.airetv.app.ui.theme.TextMuted
import com.airetv.app.ui.theme.TextPrimary
import com.airetv.app.ui.theme.TextSecondary

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun ChannelSidebar(
    channels: List<Channel>,
    selectedIndex: Int,
    initialFocusIndex: Int,
    onChannelFocused: (Int) -> Unit,
    onChannelSelected: (Int) -> Unit,
    onRefresh: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val focusRequesters = remember(channels.size) { List(channels.size) { FocusRequester() } }
    var focusedIndex by remember { mutableIntStateOf(initialFocusIndex) }
    val focusedChannel = channels.getOrNull(focusedIndex) ?: channels.first()
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        // Scroll first so the LazyColumn composes the target item and attaches its FocusRequester
        listState.scrollToItem(initialFocusIndex)
        focusRequesters.getOrNull(initialFocusIndex)?.requestFocus()
    }

    Box(modifier = modifier.fillMaxSize()) {
        // Semi-transparent scrim over video
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f))
        )

        // Sidebar panel
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .fillMaxHeight()
                .width(300.dp)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.95f),
                            Color.Black.copy(alpha = 0.85f),
                            Color.Transparent
                        ),
                        startX = 0f,
                        endX = 900f
                    )
                )
                .padding(top = 24.dp, bottom = 16.dp)
        ) {
            // Title
            Text(
                text = "Canales",
                style = MaterialTheme.typography.titleMedium,
                color = GoldAccent,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp, start = 24.dp)
            )

            // Channel list
            LazyColumn(
                state = listState,
                verticalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            ) {
                itemsIndexed(channels) { index, channel ->
                    SidebarChannelItem(
                        channel = channel,
                        isPlaying = index == selectedIndex,
                        focusRequester = focusRequesters[index],
                        onFocus = {
                            focusedIndex = index
                            onChannelFocused(index)
                        },
                        onClick = { onChannelSelected(index) }
                    )
                }
            }

            // Divider
            Box(
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 8.dp)
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(TextMuted.copy(alpha = 0.3f))
            )

            // Channel preview info with crossfade animation
            Crossfade(
                targetState = focusedChannel,
                label = "previewCrossfade"
            ) { channel ->
                ChannelPreviewInfo(
                    channel = channel,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun SidebarChannelItem(
    channel: Channel,
    isPlaying: Boolean,
    focusRequester: FocusRequester,
    onFocus: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isFocused by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.03f else 1f,
        label = "sidebarItemScale"
    )
    val backgroundColor = when {
        isFocused -> SurfaceVariant.copy(alpha = 0.8f)
        isPlaying -> SurfaceVariant.copy(alpha = 0.4f)
        else -> Color.Transparent
    }
    val borderColor = when {
        isFocused -> GoldFocus
        isPlaying -> RedPrimary.copy(alpha = 0.5f)
        else -> Color.Transparent
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .focusRequester(focusRequester)
            .onFocusChanged { state ->
                isFocused = state.isFocused
                if (state.isFocused) onFocus()
            }
            .clip(RoundedCornerShape(8.dp))
            .border(
                width = if (isFocused || isPlaying) 1.5.dp else 0.dp,
                color = borderColor,
                shape = RoundedCornerShape(8.dp)
            )
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        // Channel logo
        AsyncImage(
            model = channel.logoUrl.ifEmpty { R.drawable.logo_telefuturo },
            contentDescription = channel.name,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(6.dp))
        )

        Spacer(modifier = Modifier.width(12.dp))

        // Channel name
        Text(
            text = channel.name,
            style = MaterialTheme.typography.bodyMedium,
            color = when {
                isFocused -> GoldFocus
                isPlaying -> TextPrimary
                else -> TextSecondary
            },
            fontWeight = if (isFocused || isPlaying) FontWeight.SemiBold else FontWeight.Normal,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )

        // Playing indicator
        if (isPlaying) {
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(RedPrimary)
            )
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun ChannelPreviewInfo(
    channel: Channel,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        // Channel logo
        AsyncImage(
            model = channel.logoUrl.ifEmpty { R.drawable.logo_telefuturo },
            contentDescription = channel.name,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp))
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            // Live indicator
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(RedPrimary)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "EN VIVO",
                    style = MaterialTheme.typography.labelSmall,
                    color = RedPrimary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Channel name
            Text(
                text = channel.name,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Category
            if (channel.category.isNotEmpty()) {
                Text(
                    text = channel.category,
                    style = MaterialTheme.typography.bodySmall,
                    color = GoldAccent
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Description
            if (channel.description.isNotEmpty()) {
                Text(
                    text = channel.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
