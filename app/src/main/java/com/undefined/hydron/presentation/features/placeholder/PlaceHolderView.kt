package com.undefined.hydron.presentation.features.placeholder

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.hilt.navigation.compose.hiltViewModel


@Composable
fun PlaceHolderView (
    modifier: Modifier,
    navController: NavController,
    viewModel: PlaceHolderViewModel = hiltViewModel()
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopStart
    ) {

        CircularProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
        )

    }
}