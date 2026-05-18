package com.airetv.app

import airetvgo.Airetvgo
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Button
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.airetv.app.ui.ChannelViewModel
import com.airetv.app.ui.components.LoadingIndicator
import com.airetv.app.ui.player.PlayerScreen
import com.airetv.app.ui.theme.AireTvTheme
import com.airetv.app.ui.theme.RedPrimary
import com.airetv.app.ui.theme.TextSecondary

class MainActivity : ComponentActivity() {

    private val viewModel: ChannelViewModel by viewModels()
    private var backPressedOnce = false

    @OptIn(ExperimentalTvMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (backPressedOnce) {
                    finish()
                    return
                }
                backPressedOnce = true
                Toast.makeText(this@MainActivity, "Presione atrás de nuevo para salir", Toast.LENGTH_SHORT).show()
                window.decorView.postDelayed({ backPressedOnce = false }, 2000)
            }
        })
        setContent {
            AireTvTheme {
                val channels by viewModel.channels.collectAsState()
                val isLoading by viewModel.isLoading.collectAsState()
                val error by viewModel.error.collectAsState()

                when {
                    isLoading && channels.channels.isEmpty() -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                LoadingIndicator()
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Cargando canales...",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = TextSecondary
                                )
                            }
                        }
                    }

                    error != null && channels.channels.isEmpty() -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = "Error al cargar canales",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = RedPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = error ?: "",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(onClick = { viewModel.loadChannels() }) {
                                    Text("Reintentar")
                                }
                            }
                        }
                    }

                    channels.channels.isNotEmpty() -> {
                        PlayerScreen(
                            context = this@MainActivity,
                            channelId = channels.channels.first().id,
                            channels = channels.channels,
                            onRefresh = { viewModel.refreshChannels() }
                        )
                    }
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        Airetvgo.cleanCache()
    }
}
