package com.example.run.domain

import com.example.core.domain.location.LocationTimestamp
import kotlin.math.roundToInt

object LocationDataCalculator {
    fun getTotalDistanceMeters(locations: List<List<LocationTimestamp>>): Int {
        return locations.sumOf { timestampsPerLine ->
            timestampsPerLine.zipWithNext { locationA, locationB ->
                locationA.location.location.distanceTo(locationB.location.location)
            }.sum().roundToInt()

        }
    }
}