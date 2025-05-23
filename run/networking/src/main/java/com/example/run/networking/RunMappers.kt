package com.example.run.networking

import com.example.core.domain.location.Location
import com.example.core.domain.run.Run
import java.time.Instant
import java.time.ZoneId
import kotlin.time.Duration.Companion.milliseconds

fun RunDto.toRun(): Run {
    return Run(
        id = id,
        duration = durationMillis.milliseconds,
        dateTimeUTC = Instant.parse(dateTimeUtc).atZone(ZoneId.of("UTC")),
        distanceInMeters = distanceMeters,
        location = Location(lat = lat, long = long),
        maxSpeedKmH = maxSpeedKmh,
        totalElevationInMeters = totalElevationMeters,
        mapPictureUrl = mapPictureUrl
    )
}

fun Run.toRunRequest(): CreateRunRequest {
    return CreateRunRequest(
        id = id!!,
        durationMillis = duration.inWholeMilliseconds,
        distanceMeters = distanceInMeters,
        lat = location.lat,
        long = location.long,
        avgSpeedKmh = avgSpeedKmH,
        maxSpeedKmh = maxSpeedKmH,
        totalElevationMeters = totalElevationInMeters,
        epocMillis = dateTimeUTC.toEpochSecond() * 1000L
    )
}