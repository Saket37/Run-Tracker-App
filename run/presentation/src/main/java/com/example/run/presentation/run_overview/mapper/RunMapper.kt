package com.example.run.presentation.run_overview.mapper

import com.example.core.domain.run.Run
import com.example.core.presentation.ui.ui.formatted
import com.example.core.presentation.ui.ui.formattedMeters
import com.example.core.presentation.ui.ui.toFormatedKm
import com.example.core.presentation.ui.ui.toFormatedKmh
import com.example.core.presentation.ui.ui.toFormatedPace
import com.example.run.presentation.run_overview.model.RunUI
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun Run.toRunUI(): RunUI {
    val localDateTime = dateTimeUTC.withZoneSameInstant(ZoneId.systemDefault())
    val formattedDateTime =
        DateTimeFormatter.ofPattern("MMM dd, yyyy - hh:mma").format(localDateTime)

    val distanceKm = distanceInMeters / 1000.0
    return RunUI(
        id = id ?: "",
        duration = duration.formatted(),
        dateTime = formattedDateTime,
        distance = distanceKm.toFormatedKm(),
        avgSpeed = avgSpeedKmH.toFormatedKmh(),
        maxSpeed = maxSpeedKmH.toFormatedKmh(),
        pace = duration.toFormatedPace(distanceKm),
        totalElevation = totalElevationInMeters.formattedMeters(),
        mapPictureUrl = mapPictureUrl
    )
}