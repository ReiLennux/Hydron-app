package com.undefined.hydron.presentation.features.home

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.DeviceThermostat
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WindPower
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.undefined.hydron.presentation.shared.helpers.generateMapStyle
import com.undefined.hydron.presentation.shared.viewmodels.SharedStateViewModel
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.models.AnimationMode
import ir.ehsannarmani.compose_charts.models.DrawStyle
import ir.ehsannarmani.compose_charts.models.Line

@Composable
fun HomeContent(
    paddingValues: PaddingValues,
    viewModel: HomeViewModel = hiltViewModel(),
    sharedViewModel: SharedStateViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        item {
            SystemControlCard(
                onToggleSystem = { enabled ->
                    sharedViewModel.toggleMonitoring()
                }
            )
        }

        item {
            HomeHandler(
                viewModel = viewModel,
                context = context
            )
        }

        item {
            SensorStatusCard(
                viewModel = viewModel
            )
        }

        item {
            WeatherInfoCard(
                viewModel = viewModel
            )
        }

        item {
            DataTransferCard(viewModel = viewModel)
        }
    }
}

//region Map
@Composable
fun CompactMapView(viewModel: HomeViewModel) {
    val location by viewModel.location.observeAsState(initial = null)

    val defaultPosition = LatLng(23.6345, -102.5528)
    val userPosition = LatLng(
        location?.latitude ?: defaultPosition.latitude,
        location?.longitude ?: defaultPosition.longitude
    )

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultPosition, 15f)
    }

    val markerState = remember {
        MarkerState(position = userPosition)
    }

    LaunchedEffect(userPosition) {
        markerState.position = userPosition
        cameraPositionState.animate(
            update = CameraUpdateFactory.newLatLngZoom(userPosition, 15f),
            durationMs = 1000
        )
    }

    val primaryColor = MaterialTheme.colorScheme.outline
    val backgroundColor = MaterialTheme.colorScheme.background
    val textColor = MaterialTheme.colorScheme.onBackground

    val mapStyle = remember(primaryColor, backgroundColor) {
        val styleJson = generateMapStyle(primaryColor, backgroundColor, textColor)
        MapStyleOptions(styleJson)
    }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Ubicación",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Mi Ubicación",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            GoogleMap(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp)),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(
                    isMyLocationEnabled = false,
                    mapStyleOptions = mapStyle
                )
            ) {
                Marker(
                    state = markerState,
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                )
            }
        }
    }
}

//endregion

//region Button
@Composable
fun SystemControlCard(
    onToggleSystem: (Boolean) -> Unit,
    sharedViewModel: SharedStateViewModel = hiltViewModel()
) {
    val isMonitoring by sharedViewModel.isMonitoring.collectAsState()

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = if (isMonitoring)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = if (isMonitoring)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.outline,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PowerSettingsNew,
                        contentDescription = "Control del sistema",
                        tint = if (isMonitoring)
                            MaterialTheme.colorScheme.onPrimary
                        else
                            MaterialTheme.colorScheme.surface,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = "Sistema Hydron",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isMonitoring)
                            MaterialTheme.colorScheme.onPrimaryContainer
                        else
                            MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (isMonitoring) "Activo" else "Inactivo",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isMonitoring)
                            MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Switch(
                checked = isMonitoring,
                onCheckedChange = { enabled ->
//                    isMonitoring = enabled
                    onToggleSystem(enabled)
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                    checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                    uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                    uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
    }
}
//endregion

//region Sensor
@Composable
fun SensorStatusCard(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val registers by viewModel.registers.observeAsState(emptyList())
    val bpmList = registers.map { it.value }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Sensors,
                        contentDescription = "Sensores",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Sensores Biométricos",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                if (bpmList.isNotEmpty()) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text(
                            text = "${bpmList.last().toInt()} BPM",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (bpmList.isNotEmpty()) {
                HeartRateChart(bpmList = bpmList)

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Rango normal: 60-100 BPM",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (bpmList.last() > 100 || bpmList.last() < 60) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                                contentColor = MaterialTheme.colorScheme.onErrorContainer
                            )
                        ) {
                            Text(
                                text = if (bpmList.last() > 100) "Elevado" else "Bajo",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            } else {
                NoSensorDataMessage()
            }
        }
    }
}

@Composable
fun HeartRateChart(bpmList: List<Double>) {
    val primary = MaterialTheme.colorScheme.primary

    LineChart(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .background(
                MaterialTheme.colorScheme.surfaceContainer,
                RoundedCornerShape(12.dp)
            )
            .padding(12.dp),
        data = listOf(
            Line(
                label = "BPM",
                values = bpmList,
                color = SolidColor(primary),
                firstGradientFillColor = primary.copy(alpha = 0.3f),
                secondGradientFillColor = Color.Transparent,
                strokeAnimationSpec = tween(1000, easing = EaseInOutCubic),
                drawStyle = DrawStyle.Stroke(width = 3.dp),
            )
        ),
        animationMode = AnimationMode.Together(delayBuilder = { 0 }),
    )
}


//endregion

//region Weather
@Composable
fun WeatherInfoCard(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val weather by viewModel.weather.observeAsState(null)

    weather?.let { weatherData ->
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Condiciones Ambientales",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "${weatherData.location.name}, ${weatherData.location.country}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    ) {
                        Text(
                            text = "${weatherData.current.temp_c}°C",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = weatherData.current.condition.text,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    WeatherMetricItem(
                        icon = Icons.Default.WaterDrop,
                        label = "Humedad",
                        value = "${weatherData.current.humidity}%"
                    )
                    WeatherMetricItem(
                        icon = Icons.Default.WindPower,
                        label = "Viento",
                        value = "${weatherData.current.wind_kph} km/h"
                    )
                    WeatherMetricItem(
                        icon = Icons.Default.DeviceThermostat,
                        label = "Sensación",
                        value = "${weatherData.current.feelslike_c}°C"
                    )
                }
            }
        }
    }
}

@Composable
fun WeatherMetricItem(
    icon: ImageVector,
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
//endregion

//region Handlers
@Composable
fun HomeHandler(
    viewModel: HomeViewModel,
    context: Context
) {
    val hasLocationPermission = locationPermissionHandler(
        context = context,
        viewModel = viewModel,
        onGranted = { viewModel.startLocationUpdates() }
    )

    NotificationPermissionHandler(
        onGranted = {
            Log.d("Permiso", "Notificaciones concedidas")
        }
    )

    if (hasLocationPermission) {
        CompactMapView(viewModel = viewModel)
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.stopLocationUpdates()
        }
    }
}

@Composable
fun NoSensorDataMessage() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .background(
                MaterialTheme.colorScheme.surfaceContainer,
                RoundedCornerShape(12.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Sensors,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Sin datos de sensores",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun locationPermissionHandler(
    context: Context,
    viewModel: HomeViewModel,
    onGranted: () -> Unit
): Boolean {
    var isGranted by remember {
        mutableStateOf(context.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION))
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        isGranted = granted
        if (granted) onGranted()
    }

    LaunchedEffect(Unit) {
        if (isGranted) onGranted()
    }

    if (!isGranted) {
        PermissionRequestCard(
            title = "Permiso de ubicación requerido",
            description = "Necesitamos tu ubicación para mostrarte en el mapa.",
            icon = Icons.Default.LocationOn,
            onRequest = { launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION) }
        )
    }

    return isGranted
}

@Composable
fun NotificationPermissionHandler(
    onGranted: () -> Unit
) {
    val context = LocalContext.current
    val needsPermission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
    val permission = Manifest.permission.POST_NOTIFICATIONS

    var isGranted by remember {
        mutableStateOf(!needsPermission || context.hasPermission(permission))
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        isGranted = granted
        if (granted) onGranted()
    }

    LaunchedEffect(Unit) {
        if (needsPermission && !isGranted) {
            launcher.launch(permission)
        } else {
            onGranted()
        }
    }

    if (!isGranted && needsPermission) {
        PermissionRequestCard(
            title = "Permiso de notificaciones",
            description = "Permítenos enviarte alertas importantes de salud.",
            icon = Icons.Default.Notifications,
            onRequest = { launcher.launch(permission) }
        )
    }
}

@Composable
fun PermissionRequestCard(
    title: String,
    description: String,
    icon: ImageVector,
    onRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(28.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            FilledTonalButton(
                onClick = onRequest,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Permitir acceso",
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}

fun Context.hasPermission(permission: String): Boolean {
    return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
}
//endregion


//region Batch Temporal
@Composable
fun DataTransferCard(
    viewModel: HomeViewModel
) {
    val transferState = viewModel.transferState.observeAsState(HomeViewModel.TransferUiState.Idle).value
    val totalRecords by viewModel.totalRecords.observeAsState(0)

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = when (transferState) {
                is HomeViewModel.TransferUiState.Success -> MaterialTheme.colorScheme.primaryContainer
                is HomeViewModel.TransferUiState.Error -> MaterialTheme.colorScheme.errorContainer
                else -> MaterialTheme.colorScheme.surfaceContainer
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                color = when (transferState) {
                                    is HomeViewModel.TransferUiState.Success -> MaterialTheme.colorScheme.primary
                                    is HomeViewModel.TransferUiState.Error -> MaterialTheme.colorScheme.error
                                    is HomeViewModel.TransferUiState.Transferring -> MaterialTheme.colorScheme.tertiary
                                    else -> MaterialTheme.colorScheme.outline
                                },
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        when (transferState) {
                            is HomeViewModel.TransferUiState.Transferring -> {
                                CircularProgressIndicator(
                                    progress = transferState.progress / 100f,
                                    modifier = Modifier.size(32.dp),
                                    color = MaterialTheme.colorScheme.onTertiary,
                                    strokeWidth = 3.dp
                                )
                            }
                            is HomeViewModel.TransferUiState.Success -> {
                                Icon(
                                    imageVector = Icons.Default.CloudDone,
                                    contentDescription = "Transferencia completada",
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            is HomeViewModel.TransferUiState.Error -> {
                                Icon(
                                    imageVector = Icons.Default.CloudOff,
                                    contentDescription = "Error en transferencia",
                                    tint = MaterialTheme.colorScheme.onError,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            else -> {
                                Icon(
                                    imageVector = Icons.Default.CloudUpload,
                                    contentDescription = "Transferir datos",
                                    tint = MaterialTheme.colorScheme.surface,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = "Sincronización de Datos",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = when (transferState) {
                                is HomeViewModel.TransferUiState.Success -> MaterialTheme.colorScheme.onPrimaryContainer
                                is HomeViewModel.TransferUiState.Error -> MaterialTheme.colorScheme.onErrorContainer
                                else -> MaterialTheme.colorScheme.onSurface
                            }
                        )
                        Text(
                            text = when (transferState) {
                                is HomeViewModel.TransferUiState.Idle -> "$totalRecords registros pendientes"
                                is HomeViewModel.TransferUiState.Transferring -> "Subiendo... ${transferState.progress}%"
                                is HomeViewModel.TransferUiState.Success -> "Sincronización completada"
                                is HomeViewModel.TransferUiState.Error -> "Error en sincronización"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = when (transferState) {
                                is HomeViewModel.TransferUiState.Success -> MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                                is HomeViewModel.TransferUiState.Error -> MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.7f)
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }
                }

                // Indicador de registros
                if (totalRecords > 0 && transferState is HomeViewModel.TransferUiState.Idle) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondary,
                            contentColor = MaterialTheme.colorScheme.onSecondary
                        )
                    ) {
                        Text(
                            text = "$totalRecords",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // Progress bar para transferencia
            if (transferState is HomeViewModel.TransferUiState.Transferring) {
                Spacer(modifier = Modifier.height(16.dp))
                LinearProgressIndicator(
                    progress = transferState.progress / 100f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = MaterialTheme.colorScheme.tertiary,
                    trackColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.3f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${transferState.progress}% completado",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }

            // Mensaje de error
            if (transferState is HomeViewModel.TransferUiState.Error) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = transferState.message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(12.dp)
                )
            }

            // Botones de acción
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = when (transferState) {
                    is HomeViewModel.TransferUiState.Transferring -> Arrangement.Center
                    else -> Arrangement.spacedBy(12.dp)
                }
            ) {
                when (transferState) {
                    is HomeViewModel.TransferUiState.Idle -> {
                        if (totalRecords > 0) {
                            FilledTonalButton(
                                onClick = { viewModel.startDataTransfer() },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CloudUpload,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Sincronizar")
                            }
                        } else {
                            OutlinedButton(
                                onClick = { /* Refresh count */ viewModel.getTotalRecords() },
                                modifier = Modifier.weight(1f),
                                enabled = false
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Todo sincronizado")
                            }
                        }
                    }

                    is HomeViewModel.TransferUiState.Transferring -> {
                        OutlinedButton(
                            onClick = { viewModel.cancelDataTransfer() }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Cancel,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Cancelar")
                        }
                    }

                    is HomeViewModel.TransferUiState.Success -> {
                        OutlinedButton(
                            onClick = { viewModel.resetTransferState() },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Actualizar")
                        }
                    }

                    is HomeViewModel.TransferUiState.Error -> {
                        OutlinedButton(
                            onClick = { viewModel.startDataTransfer() },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Reintentar")
                        }

                        OutlinedButton(
                            onClick = { viewModel.resetTransferState() },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cerrar")
                        }
                    }
                }
            }
        }
    }
}
//endregion
