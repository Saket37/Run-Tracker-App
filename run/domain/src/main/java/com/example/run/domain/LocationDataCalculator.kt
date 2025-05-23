package com.example.run.domain

import com.example.core.domain.location.LocationTimestamp
import kotlin.math.roundToInt
import kotlin.time.DurationUnit

object LocationDataCalculator {
    fun getTotalDistanceMeters(locations: List<List<LocationTimestamp>>): Int {
        return locations.sumOf { timestampsPerLine ->
            timestampsPerLine.zipWithNext { locationA, locationB ->
                locationA.location.location.distanceTo(locationB.location.location)
            }.sum().roundToInt()

        }
    }

    fun getMaxSpeed(locations: List<List<LocationTimestamp>>): Double {
        return locations.maxOf { locationSet ->
            locationSet.zipWithNext { location1, location2 ->
                val distance = location1.location.location.distanceTo(location2.location.location)
                val hoursDiff =
                    (location2.durationTimestamp - location1.durationTimestamp).toDouble(
                        DurationUnit.HOURS
                    )

                if (hoursDiff == 0.0) {
                    0.0
                } else {
                    (distance / 1000.0) / hoursDiff
                }
            }.maxOrNull() ?: 0.0
        }
    }

    fun getTotalElevationMeters(locations: List<List<LocationTimestamp>>): Int {
        return locations.sumOf { location ->
            location.zipWithNext { loc1, loc2 ->
                val altitude1 = loc1.location.altitude
                val altitude2 = loc2.location.altitude
                (altitude2 - altitude1).coerceAtLeast(0.0)
            }.sum().roundToInt()
        }
    }
}