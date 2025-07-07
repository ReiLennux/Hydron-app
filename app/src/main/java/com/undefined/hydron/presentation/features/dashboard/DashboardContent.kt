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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.undefined.hydron.R
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.models.AnimationMode
import ir.ehsannarmani.compose_charts.models.DrawStyle
import ir.ehsannarmani.compose_charts.models.Line


@Composable
fun DashboardContent(
    viewModel: DashboardViewModel = hiltViewModel(),
) {
    val registers by viewModel.registers.observeAsState(emptyList())
    val bpmList = registers.map { it.value.toDouble() }

    val primaryColor = MaterialTheme.colorScheme.primary
    val onBackground = MaterialTheme.colorScheme.onBackground
    val surfaceColor = MaterialTheme.colorScheme.surface

    if (bpmList.isNotEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.bpm_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = onBackground
                )

                Text(
                    text = "${bpmList.last().toInt()} BPM",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = primaryColor
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            LineChart(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .background(
                        color = surfaceColor,
                        shape = MaterialTheme.shapes.medium
                    )
                    .padding(16.dp),
                data = listOf(
                    Line(
                        label = "BPM",
                        values = bpmList,
                        color = SolidColor(primaryColor),
                        firstGradientFillColor = primaryColor.copy(alpha = 0.2f),
                        secondGradientFillColor = Color.Transparent,
                        strokeAnimationSpec = tween(2000, easing = EaseInOutCubic),
                        gradientAnimationDelay = 1000,
                        drawStyle = DrawStyle.Stroke(width = 3.dp),

                    )
                ),
                animationMode = AnimationMode.Together(delayBuilder = { it * 500L }),
//                zeroLineProperties = ZeroLineProperties(
//                    enabled = true,
//                    ),
//                minValue = -40.0,
//                maxValue = 130.0
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.normal_range),
                    style = MaterialTheme.typography.labelSmall,
                    color = onBackground.copy(alpha = 0.7f)
                )

                if (bpmList.last() > 130 || bpmList.last() < 60) {
                    Text(
                        text = if (bpmList.last() > 130) stringResource(R.string.bpm_warning_high) else stringResource(R.string.bpm_warning_low),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.error
                        )
                    )
                }
            }
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(22.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.dashboard_no_data),
                style = MaterialTheme.typography.bodyMedium,
                color = onBackground.copy(alpha = 0.6f)
            )
        }
    }
}
