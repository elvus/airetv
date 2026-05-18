package com.airetv.app.ui.browse

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import com.airetv.app.data.model.Channel
import com.airetv.app.ui.theme.GoldAccent

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun BrowseScreen(
    channels: List<Channel>,
    onChannelSelected: (Channel) -> Unit
) {
    var focusedIndex by remember { mutableIntStateOf(0) }
    val focusedChannel = channels.getOrNull(focusedIndex) ?: return

    Box(modifier = Modifier.fillMaxSize()) {
        // Layer 1: Background image
        if (focusedChannel.backgroundUrl.isNotEmpty()) {
            Crossfade(
                targetState = focusedChannel.backgroundUrl,
                label = "backgroundCrossfade"
            ) { backgroundUrl ->
                AsyncImage(
                    model = backgroundUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Layer 2: Gradient overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.3f),
                            Color.Black.copy(alpha = 0.85f),
                            Color.Black.copy(alpha = 0.95f)
                        )
                    )
                )
        )

        // Layer 3: Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 32.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // App title
            Text(
                text = "AireTV",
                style = MaterialTheme.typography.titleLarge,
                color = GoldAccent,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 48.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            // Featured channel info
            FeaturedChannel(
                channel = focusedChannel,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Channel row
            Text(
                text = "Canales en vivo",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 48.dp, vertical = 8.dp)
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = 36.dp),
                horizontalArrangement = Arrangement.spacedBy(0.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items(channels, key = { it.id }) { channel ->
                    ChannelCard(
                        channel = channel,
                        onFocus = {
                            focusedIndex = channels.indexOf(channel)
                        },
                        onClick = { onChannelSelected(channel) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
