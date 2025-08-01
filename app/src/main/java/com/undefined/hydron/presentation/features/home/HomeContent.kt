package com.undefined.hydron.presentation.features.home

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.undefined.hydron.presentation.shared.helpers.generateMapStyle


@Composable
fun HomeContent(
    paddingValues: PaddingValues,
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        HomeDesign(
            viewModel = viewModel,
            context = context
        )
    }
}


@Composable
fun HomeDesign(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel,
    context: Context,
) {
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        hasLocationPermission = isGranted
        if (isGranted) {
            viewModel.startLocationUpdates()
        } else {
            Log.d("HomeDesign", "Permiso de ubicación denegado")
        }
    }

    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            viewModel.startLocationUpdates()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.stopLocationUpdates()
        }
    }

    RequestNotificationPermission(
        onPermissionGranted = {
            //TODO: manage permissions granted
            },
        onPermissionDenied = {
            Log.d("NotificationPermission", "Permiso de notificaciones denegado")
        },
        contentIfDenied = { launcher ->
            Column {
                Text("Necesitamos permiso para mostrar notificaciones")
                Button(onClick = { launcher.launch(Manifest.permission.POST_NOTIFICATIONS) }) {
                    Text("Pedir permiso")
                }
            }
        }
    )

    Box(modifier = modifier.fillMaxSize()) {
        if (hasLocationPermission) {
            MapView(viewModel = viewModel)
        } else {
            UbicationPermissions(locationPermissionLauncher = locationPermissionLauncher)
        }
    }
}


@Composable
fun RequestNotificationPermission(
    onPermissionGranted: () -> Unit = {},
    onPermissionDenied: () -> Unit = {},
    contentIfDenied: @Composable ((ActivityResultLauncher<String>) -> Unit)? = null,
) {
    val context = LocalContext.current
    val sdkInt = Build.VERSION.SDK_INT
    val permission = Manifest.permission.POST_NOTIFICATIONS

    val needsPermission = sdkInt >= 33

    var hasPermission by remember {
        mutableStateOf(
            !needsPermission ||
                    ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
        if (isGranted) {
            onPermissionGranted()
        } else {
            onPermissionDenied()
        }
    }

    LaunchedEffect(Unit) {
        if (!hasPermission && needsPermission) {
            permissionLauncher.launch(permission)
        } else if (hasPermission) {
            onPermissionGranted()
        }
    }

    if (!hasPermission && needsPermission) {
        contentIfDenied?.invoke(permissionLauncher)
    }
}



@Composable
fun UbicationPermissions(
    locationPermissionLauncher: ManagedActivityResultLauncher<String, Boolean>
) {
    Card{

        Text(text = "necesitamos permisos")
        Text(text = "necesitamos permisos xd")
        Button(
            onClick = {
                locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        ) {
            Text(text = "otorgar permisos")
        }
    }
}



@Composable
fun MapView(viewModel: HomeViewModel) {
    val location by viewModel.location.observeAsState(initial = null)

    val defaultPosition = LatLng(23.6345, -102.5528)
    val userPosition = LatLng(
        location?.latitude ?: defaultPosition.latitude,
        location?.longitude ?: defaultPosition.longitude
    )

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultPosition, 5f)
    }

    val markerState = remember {
        MarkerState(position = userPosition)
    }

    LaunchedEffect(userPosition) {
        markerState.position = userPosition
    }

    val primaryColor = MaterialTheme.colorScheme.outline
    val backgroundColor = MaterialTheme.colorScheme.background
    val textColor = MaterialTheme.colorScheme.onBackground

    val mapStyle = remember(primaryColor, backgroundColor) {
        val styleJson = generateMapStyle(primaryColor, backgroundColor, textColor)
        MapStyleOptions(styleJson)
    }

    var hasZoomedToUser by remember { mutableStateOf(false) }

    LaunchedEffect(location) {
        location?.let {
            val newUserPosition = LatLng(it.latitude, it.longitude)
            if (!hasZoomedToUser) {
                cameraPositionState.animate(
                    update = CameraUpdateFactory.newLatLngZoom(newUserPosition, 15f),
                    durationMs = 1000
                )
                hasZoomedToUser = true
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(22.dp)
    ) {
        GoogleMap(
            modifier = Modifier
                .clip(RoundedCornerShape(22.dp))
                .fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                isMyLocationEnabled = false,
                mapStyleOptions = mapStyle
            )
        ) {
            Marker(
                state = markerState,
                title = "Mi ubicación"
            )
        }
    }
}
