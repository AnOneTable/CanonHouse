package com.example.canting.ui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.canting.data.model.ConnectionState
import com.example.canting.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                ) { MainScreen(viewModel) }
            }
        }
    }
}

@Composable
fun MainScreen(viewModel: MainViewModel) {
    val connectionState by viewModel.connectionState.collectAsState()
    val vehicleInfo by viewModel.vehicleInfo.collectAsState()
    val recorderInfo by viewModel.recorderInfo.collectAsState()
    val confirmedFaults by viewModel.confirmedFaults.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
                text = "Connect State: ${formatState(connectionState)}",
                style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { viewModel.connect() }) { Text("Connect") }
        Button(onClick = { viewModel.disconnect() }) { Text("Disconnect") }

        Spacer(modifier = Modifier.height(24.dp))

        // --- 核心展示区域：超过6秒的真实故障 ---
        Text(text = "Active Faults (>6s):", style = MaterialTheme.typography.titleLarge)

        if (confirmedFaults.isEmpty()) {
            Card(
                    colors =
                            CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            ) {
                Text(
                        text = "System Nominal (No Faults)",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            Card(
                    colors =
                            CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer
                            ),
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                            text = "CRITICAL ALERTS (${confirmedFaults.size}):",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                            text = confirmedFaults.toString(),
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(text = "Vehicle Status:", style = MaterialTheme.typography.titleMedium)
        Text(text = vehicleInfo)

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "Recorder State:", style = MaterialTheme.typography.titleMedium)
        Text(text = recorderInfo)
    }
}

private fun formatState(state: ConnectionState): String {
    return when (state) {
        is ConnectionState.Connected -> "Connected"
        is ConnectionState.Connecting -> "Connecting..."
        is ConnectionState.Disconnected -> "Disconnected"
        is ConnectionState.Error -> "Error: $state"
    }
}
