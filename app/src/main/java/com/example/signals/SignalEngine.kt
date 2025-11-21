package com.example.signals

object SignalEngine {

    fun sma(values: List<Double>, period: Int): List<Double?> {
        val res = MutableList<Double?>(values.size) { null }
        if (period <= 0) return res
        var sum = 0.0
        for (i in values.indices) {
            sum += values[i]
            if (i >= period) sum -= values[i - period]
            if (i >= period - 1) res[i] = sum / period
        }
        return res
    }

    fun ema(values: List<Double>, period: Int): List<Double?> {
        val res = MutableList<Double?>(values.size) { null }
        if (values.isEmpty() || period <= 0) return res
        val k = 2.0 / (period + 1)
        res[0] = values[0]
        for (i in 1 until values.size) {
            res[i] = (values[i] - res[i-1]!!) * k + res[i-1]!!
        }
        return res
    }

    fun rsi(values: List<Double>, period: Int = 14): List<Double?> {
        val res = MutableList<Double?>(values.size) { null }
        if (values.size <= period) return res

        val gains = MutableList(values.size) { 0.0 }
        val losses = MutableList(values.size) { 0.0 }

        for (i in 1 until values.size) {
            val diff = values[i] - values[i-1]
            if (diff >= 0) gains[i] = diff else losses[i] = -diff
        }

        var avgGain = gains.subList(1, period+1).sum() / period
        var avgLoss = losses.subList(1, period+1).sum() / period

        res[period] = if (avgLoss == 0.0) 100.0 else 100 - 100 / (1 + avgGain/avgLoss)

        for (i in period+1 until values.size) {
            avgGain = (avgGain*(period-1) + gains[i]) / period
            avgLoss = (avgLoss*(period-1) + losses[i]) / period
            res[i] = if (avgLoss == 0.0) 100.0 else 100 - 100 / (1 + avgGain/avgLoss)
        }
        return res
    }

    fun generateSignal(
        closes: List<Double>,
        shortPeriod: Int = 9,
        longPeriod: Int = 21,
        rsiPeriod: Int = 14,
        rsiBuy: Double = 40.0,
        rsiSell: Double = 60.0
    ): Pair<String, String> {

        if (closes.size < longPeriod + 2) return "HOLD" to "Not enough data"

        val smaS = sma(closes, shortPeriod)
        val smaL = sma(closes, longPeriod)
        val rsiVal = rsi(closes, rsiPeriod)

        val i = closes.lastIndex
        val pi = i - 1

        val pS = smaS[pi]
        val pL = smaL[pi]
        val cS = smaS[i]
        val cL = smaL[i]
        val r = rsiVal[i] ?: return "HOLD" to "No RSI"

        if (pS!=null && pL!=null && cS!=null && cL!=null) {
            if (pS <= pL && cS > cL && r > rsiBuy)
                return "BUY" to "SMA crossover ↑ + RSI=%.1f".format(r)
            if (pS >= pL && cS < cL && r < rsiSell)
                return "SELL" to "SMA crossover ↓ + RSI=%.1f".format(r)
        }
        return "HOLD" to "RSI=%.1f".format(r)
    }
}
