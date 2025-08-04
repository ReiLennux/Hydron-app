package com.undefined.hydron.presentation.shared.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.undefined.hydron.R
import kotlinx.coroutines.delay

import com.undefined.hydron.presentation.shared.viewmodels.SharedStateViewModel

@Composable
fun AppLogo(
    sharedViewModel: SharedStateViewModel = hiltViewModel()
) {
    val isMonitoring by sharedViewModel.isMonitoring.collectAsState()

    val carouselMessages = listOf(
        stringResource(R.string.val_carrousel_message_1),
        stringResource(R.string.val_carrousel_message_2)
    )

    var currentMessageIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(isMonitoring) {
        if (!isMonitoring) {
            while (!isMonitoring) {
                delay(4000)
                currentMessageIndex = (currentMessageIndex + 1) % carouselMessages.size
            }
        }
    }

    Row(
        modifier = Modifier
            .padding(10.dp)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                sharedViewModel.toggleMonitoring()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.WaterDrop,
            tint = MaterialTheme.colorScheme.secondary,
            contentDescription = "app-icon",
            modifier = Modifier.size(40.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = stringResource(R.string.app_name),
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 35.sp,
                fontWeight = FontWeight.Black
            )

            AnimatedContent(
                targetState = if (isMonitoring) stringResource(R.string.val_monitoring_state) else carouselMessages[currentMessageIndex],
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
            ) { message ->
                Text(
                    text = message,
                    color = if (isMonitoring)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSecondaryContainer,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
