package com.airetv.app.ui.browse

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.airetv.app.data.model.Channel
import com.airetv.app.ui.theme.GoldAccent
import com.airetv.app.ui.theme.TextSecondary

@OptIn(ExperimentalTvMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun FeaturedChannel(
    channel: Channel,
    modifier: Modifier = Modifier
) {
    AnimatedContent(
        targetState = channel,
        transitionSpec = {
            fadeIn() togetherWith fadeOut()
        },
        label = "featuredChannel",
        modifier = modifier
    ) { currentChannel ->
        Column(
            modifier = Modifier
                .padding(horizontal = 48.dp)
                .widthIn(max = 500.dp)
        ) {
            Text(
                text = currentChannel.category.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = GoldAccent,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = currentChannel.name,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = currentChannel.description,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                maxLines = 3
            )
        }
    }
}
