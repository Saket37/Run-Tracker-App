package com.example.core.presentation.ui.ui

import java.util.Locale
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.roundToInt
import kotlin.time.Duration

fun Duration.formatted(): String {
    val totalSeconds = this.inWholeSeconds
    val hours = String.format(Locale.getDefault(), "%02d", totalSeconds / 3600)
    val minute = String.format(Locale.getDefault(), "%02d", (totalSeconds % 3600) / 60)
    val seconds = String.format(Locale.getDefault(), "%02d", totalSeconds % 60)

    return "$hours:$minute:$seconds"
}

fun Double.toFormatedKm(): String {
    return "${this.roundToDecimals(1)} km"
}

fun Duration.toFormatedPace(distanceIKm: Double): String {
    if ((this == Duration.ZERO || distanceIKm <= 0.0)) {
        return "-"
    }
    val secondsPerKm = (this.inWholeSeconds / distanceIKm).roundToInt()
    val avgPaceInMinutes = secondsPerKm / 60
    val avgPaceInSeconds = String.format(Locale.getDefault(), "%02d", secondsPerKm % 60)
    return "$avgPaceInMinutes:$avgPaceInSeconds / km"
}

private fun Double.roundToDecimals(decimalCount: Int): Double {
    val factor = 10f.pow(decimalCount)
    return round(this * factor) / factor
}