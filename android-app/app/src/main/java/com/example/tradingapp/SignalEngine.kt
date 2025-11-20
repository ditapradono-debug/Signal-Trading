package com.example.tradingapp

object SignalEngine {
    fun movingAverage(pr: List<Double>, p: Int)= if(pr.size<p)null else pr.takeLast(p).average()
}
