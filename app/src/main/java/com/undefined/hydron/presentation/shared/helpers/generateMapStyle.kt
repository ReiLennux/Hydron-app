package com.undefined.hydron.presentation.shared.helpers

import androidx.compose.ui.graphics.Color

private fun Color.toHexString(): String {
    val alpha = (alpha * 255).toInt()
    val red = (red * 255).toInt()
    val green = (green * 255).toInt()
    val blue = (blue * 255).toInt()
    return String.format("#%02X%02X%02X%02X", alpha, red, green, blue)
}

fun generateMapStyle(primaryColor: Color, backgroundColor: Color, textColor: Color): String {
    return """
    [
      {
        "elementType": "geometry",
        "stylers": [
          { "color": "${backgroundColor.toHexString()}" }
        ]
      },
      {
        "elementType": "labels.text.fill",
        "stylers": [
          { "color": "${textColor.toHexString()}" }
        ]
      },
      {
        "elementType": "labels.text.stroke",
        "stylers": [
          { "color": "${backgroundColor.copy(alpha = 0.9f).toHexString()}" },
          { "weight": 2 }
        ]
      },
      {
        "featureType": "road",
        "elementType": "geometry",
        "stylers": [
          { "color": "${primaryColor.copy(alpha = 0.3f).toHexString()}" }
        ]
      },
      {
        "featureType": "road",
        "elementType": "geometry.stroke",
        "stylers": [
          { "color": "${primaryColor.copy(alpha = 0.15f).toHexString()}" },
          { "weight": 1.2 }
        ]
      },
      {
        "featureType": "road",
        "elementType": "labels.text.fill",
        "stylers": [
          { "color": "${textColor.copy(alpha = 0.9f).toHexString()}" }
        ]
      },
      {
        "featureType": "road",
        "elementType": "labels.text.stroke",
        "stylers": [
          { "color": "${backgroundColor.copy(alpha = 0.85f).toHexString()}" },
          { "weight": 2 }
        ]
      },
      {
        "featureType": "administrative",
        "elementType": "geometry.stroke",
        "stylers": [
          { "color": "${primaryColor.copy(alpha = 0.5f).toHexString()}" },
          { "weight": 1.5 }
        ]
      },
      {
        "featureType": "poi",
        "stylers": [
          { "visibility": "off" }
        ]
      },
      {
        "featureType": "transit",
        "stylers": [
          { "visibility": "off" }
        ]
      },
      {
        "featureType": "water",
        "elementType": "geometry",
        "stylers": [
          { "color": "${primaryColor.copy(alpha = 0.2f).toHexString()}" }
        ]
      }
    ]
    """.trimIndent()
}
