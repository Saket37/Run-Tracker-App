package com.example.run.presentation.maps

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.example.core.domain.location.LocationTimestamp
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Polyline

@Composable
fun RunTrackerPolylines(locations: List<List<LocationTimestamp>>) {
    val polylines = remember(locations) {
        locations.map {
            it.zipWithNext { timeStamp1, timeStamp2 ->
                PolylineUI(
                    location1 = timeStamp1.location.location,
                    location2 = timeStamp2.location.location,
                    color = PolyLineColorCalculator.locationToColor(
                        location1 = timeStamp1,
                        location2 = timeStamp2
                    )
                )
            }
        }
    }
    polylines.forEach { polyline ->
        polyline.forEach { polylineUI ->
            Polyline(
                points = listOf(
                    LatLng(polylineUI.location1.lat, polylineUI.location1.long),
                    LatLng(polylineUI.location2.lat, polylineUI.location2.long)
                ),
                color = polylineUI.color,
                jointType = JointType.BEVEL
            )
        }
    }

}