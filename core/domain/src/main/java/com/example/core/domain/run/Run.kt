package com.example.core.domain.run

import com.example.core.domain.location.Location
import java.time.ZonedDateTime
import kotlin.time.Duration
import kotlin.time.DurationUnit

data class Run(
    val id: String?, // if its new run, it will be null
    val duration: Duration,
    val dateTimeUTC: ZonedDateTime,
    val distanceInMeters: Int,
    val location: Location,
    val maxSpeedKmH: Double,
    val totalElevationInMeters: Int,
    val mapPictureUrl: String?
) {
    val avgSpeedKmH: Double
        get() = (distanceInMeters / 1000.0) / duration.toDouble(DurationUnit.HOURS)
}
