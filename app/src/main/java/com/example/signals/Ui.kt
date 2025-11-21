package com.example.signals

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MainScreen(vm: MainViewModel) {
    val state by vm.state.collectAsState()

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        var text by remember { mutableStateOf(state.symbol) }

        Row {
            TextField(text, { text = it }, Modifier.weight(1f))
            Spacer(Modifier.width(8.dp))
            Button(onClick = { vm.loadSymbol(if (text.isBlank()) "TLKM.JK" else text) }) {
                Text("Load")
            }
        }

        Spacer(Modifier.height(16.dp))

        Text("Signal: ${state.signal}", style = MaterialTheme.typography.h5)
        Text("Reason: ${state.signalReason}")

        if (state.prices.isNotEmpty()) {
            Spacer(Modifier.height(12.dp))
            Text("Latest close: ${state.prices.last().close}")
        }

        state.error?.let { Text("Error: $it", color = MaterialTheme.colors.error) }
    }
}
