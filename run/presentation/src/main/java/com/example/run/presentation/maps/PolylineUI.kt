package com.example.run.presentation.maps

import androidx.compose.ui.graphics.Color
import com.example.core.domain.location.Location

data class PolylineUI(
    val location1: Location,
    val location2: Location,
    val color: Color
)
