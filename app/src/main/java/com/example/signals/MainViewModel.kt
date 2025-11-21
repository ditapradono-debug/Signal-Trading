package com.example.signals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(private val apiKey: String) : ViewModel() {

    private val svc = ApiClient.create()
    private val _state = MutableStateFlow(AppState())
    val state: StateFlow<AppState> = _state

    fun loadSymbol(symbol: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val resp = svc.getDaily(symbol = symbol, apikey = apiKey)
                val prices = parseDailyTimeSeries(resp)
                val closes = prices.map { it.close }
                val (signal, reason) = SignalEngine.generateSignal(closes)
                _state.value = _state.value.copy(
                    isLoading = false,
                    prices = prices,
                    symbol = symbol,
                    signal = signal,
                    signalReason = reason
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.localizedMessage ?: "Error"
                )
            }
        }
    }
}

class MainViewModelFactory(private val apiKey: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(cls: Class<T>): T {
        if (cls.isAssignableFrom(MainViewModel::class.java))
            return MainViewModel(apiKey) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

data class AppState(
    val isLoading: Boolean = false,
    val prices: List<PricePoint> = emptyList(),
    val symbol: String = "",
    val signal: String = "HOLD",
    val signalReason: String = "",
    val error: String? = null
)

fun parseDailyTimeSeries(resp: Map<String, Any>): List<PricePoint> {
    val key = resp.keys.firstOrNull { it.contains("Time Series") } ?: return emptyList()
    val ts = resp[key] as? Map<*, *> ?: return emptyList()
    return ts.mapNotNull {
        val date = it.key as? String ?: return@mapNotNull null
        val m = it.value as? Map<*, *> ?: return@mapNotNull null
        val closeStr = m["4. close"] as? String ?: return@mapNotNull null
        PricePoint(date, closeStr.toDouble())
    }.sortedBy { it.date }
}
