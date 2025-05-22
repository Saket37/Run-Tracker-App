package com.example.core.database.mappers

import com.example.core.database.entity.RunEntity
import com.example.core.domain.location.Location
import com.example.core.domain.run.Run
import org.bson.types.ObjectId
import java.time.Instant
import java.time.ZoneId
import kotlin.time.Duration.Companion.milliseconds

fun RunEntity.toRun(): Run {
    return Run(
        id = id,
        duration = durationMillis.milliseconds,
        dateTimeUTC = Instant.parse(dateTimeUTC).atZone(ZoneId.of("UTC")),
        distanceInMeters = distanceMeters,
        location = Location(
            lat = latitude,
            long = longitude
        ),
        maxSpeedKmH = maxSpeedKmh,
        totalElevationInMeters = totalElevationMeters,
        mapPictureUrl = mapPictureUrl
    )
}

fun Run.toRunEntity(): RunEntity {
    return RunEntity(
        id = id ?: ObjectId().toHexString(),
        durationMillis = duration.inWholeMilliseconds,
        maxSpeedKmh = maxSpeedKmH,
        dateTimeUTC = dateTimeUTC.toInstant().toString(),
        latitude = location.lat,
        longitude = location.long,
        totalElevationMeters = totalElevationInMeters,
        mapPictureUrl = mapPictureUrl,
        distanceMeters = distanceInMeters,
        avgSpeedKmh = avgSpeedKmH
    )
}