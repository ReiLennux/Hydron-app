package com.undefined.hydron.presentation.features.dashboard

import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeviceThermostat
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WindPower
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.undefined.hydron.R
import com.undefined.hydron.domain.models.entities.WeatherModel
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.models.AnimationMode
import ir.ehsannarmani.compose_charts.models.DrawStyle
import ir.ehsannarmani.compose_charts.models.Line


@Composable
fun DashboardContent(
    viewModel: DashboardViewModel = hiltViewModel(),
) {
    val registers by viewModel.registers.observeAsState(emptyList())
    val weather by viewModel.weather.observeAsState(null)
    val bpmList = registers.map { it.value.toDouble() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        weather?.let {
            WeatherCard(it)
        }

        if (bpmList.isNotEmpty()) {
            BpmCard(bpmList)
        } else {
            NoDataMessage()
        }
    }
}

@Composable
fun NoDataMessage() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.dashboard_no_data),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
        )
    }
}


@Composable
fun BpmCard(bpmList: List<Double>) {
    val primary = MaterialTheme.colorScheme.primary
    val surface = MaterialTheme.colorScheme.surface

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = surface),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Frecuencia Cardíaca", style = MaterialTheme.typography.titleMedium)
                Text(
                    "${bpmList.last().toInt()} BPM",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = primary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            LineChart(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .background(surface, MaterialTheme.shapes.medium)
                    .padding(12.dp),
                data = listOf(
                    Line(
                        label = "BPM",
                        values = bpmList,
                        color =SolidColor(primary),
                        firstGradientFillColor = primary.copy(alpha = 0.2f),
                        secondGradientFillColor = Color.Transparent,
                        strokeAnimationSpec = tween(0, easing = EaseInOutCubic),
                        drawStyle = DrawStyle.Stroke(width = 2.dp),
                    )
                ),
                animationMode = AnimationMode.Together(delayBuilder = { 0 }),
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Rango normal", style = MaterialTheme.typography.labelSmall)
                if (bpmList.last() > 130 || bpmList.last() < 60) {
                    Text(
                        text = if (bpmList.last() > 130)
                            "¡BPM alto!" else "¡BPM bajo!",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.error
                        )
                    )
                }
            }
        }
    }
}



@Composable
fun WeatherCard(weather: WeatherModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "${weather.location.name}, ${weather.location.country}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Uncomment if using Coil or Glide-Compose
                // AsyncImage(
                //     model = "https:${weather.current.condition.icon}",
                //     contentDescription = weather.current.condition.text,
                //     modifier = Modifier.size(64.dp)
                // )

                Column {
                    Text(
                        text = "${weather.current.temp_c}°C",
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = weather.current.condition.text,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            WeatherDetailItem(
                icon = Icons.Default.WaterDrop,
                "Humedad",
                "${weather.current.humidity}%"
            )
            WeatherDetailItem(
                icon = Icons.Default.WindPower,
                "Viento",
                "${weather.current.wind_kph} km/h"
            )
            WeatherDetailItem(
                icon = Icons.Default.DeviceThermostat,
                "Sensación",
                "${weather.current.feelslike_c}°C"
            )
        }
    }
}

@Composable
fun WeatherDetailItem(
    icon: ImageVector,
    title: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(title, style = MaterialTheme.typography.bodyMedium)
        }

        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}
