package com.undefined.hydron.presentation.features.profile

import android.annotation.SuppressLint
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Height
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.undefined.hydron.domain.models.hasAnyDisease
import com.undefined.hydron.presentation.shared.navigation.enums.Routes
import com.undefined.hydron.R
import kotlinx.coroutines.launch

@SuppressLint("SuspiciousIndentation")
@Composable
fun ProfileContent(
    paddingValues: PaddingValues,
    viewModel: ProfileViewModel = hiltViewModel(),
    navController: NavController
) {
    val user by viewModel.user.observeAsState()
    val email by viewModel.email.observeAsState("")

        user?.let { userData ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ProfileHeader()

                Box(
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .fillMaxWidth(),
                    //shape = MaterialTheme.shapes.large
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {

                        Text(
                            text = userData.name,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        ProfileStatsRow(
                            height = userData.height,
                            weight = userData.weight,
                            birthDate = userData.birthDate,
                            viewModel = viewModel

                        )
                    }
                }

                SectionTitle(title = R.string.profile_title)
                OptionItem(
                    title = R.string.val_email_title,
                    body = email
                )
                OptionItem(
                    title = R.string.val_sex_title,
                    body = userData.sex.toString()
                )
                OptionItem(
                    title = R.string.val_birth_date_title,
                    body = userData.birthDate
                )

                SectionTitle(title = R.string.medical_conditions_title)

                if (!userData.hasAnyDisease()) {
                    OptionItem(
                        title = R.string.val_chronic_disease_title,
                        body = "Ninguna"
                    )
                } else {
                    if (userData.hasHypertension)
                        OptionItem(
                            title = R.string.val_has_hypertension_title,
                            body = "Sí"
                        )
                    if (userData.hasDiabetes)
                        OptionItem(
                            title = R.string.val_has_diabetes_title,
                            body = "Sí"
                        )
                    if (userData.hasHeartDisease)
                        OptionItem(
                            title = R.string.val_has_heart_disease_title,
                            body = "Sí"
                        )
                }

                if (!userData.chronicDiseaseDetails.isNullOrBlank()) {
                    OptionItem(
                        title = R.string.val_chronic_disease_details_title,
                        body = userData.chronicDiseaseDetails
                    )
                }
                LogOut(navController = navController, viewModel = viewModel)

            }
        } ?: run {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
}

@Composable
fun ProfileHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp, bottom = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 3.dp,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier
                .size(120.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "👤",
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}


@Composable
fun SectionTitle(
    @StringRes title: Int,
) {
    Text(
        text = stringResource(title),
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier
            .padding(start = 4.dp, top = 8.dp)
    )
}





@Composable
fun ProfileStatsRow(
    height: Double?,
    weight: Double?,
    birthDate: String?,
    viewModel: ProfileViewModel,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        StatCard(
            label = "Altura",
            value = height?.let { "%.2f".format(it) } ?: "N/D",
            unit = "m",
            icon = Icons.Default.Height,
            modifier = Modifier.weight(1f)
        )

                    StatCard(
                    label = "Peso",
            value = weight?.let { "%.1f".format(it) } ?: "N/D",
            unit = "kg",
            icon = Icons.Default.Scale,
            modifier = Modifier.weight(1f)
                    )

                    StatCard(
                    label = "Edad",
            value = birthDate?.let { viewModel.getAge(it) } ?: "N/D",
            unit = "años",
            icon = Icons.Default.Cake,
            modifier = Modifier.weight(1f))
    }
}

@Composable
fun StatCard(
    label: String,
    value: String,
    unit: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
) {
    Card(
        modifier = modifier
            .height(120.dp),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = label,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (unit.isNotBlank()) {
                    Text(
                        text = unit,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}



@Composable
fun OptionItem(
    @StringRes title: Int,
    body: String,
    option: @Composable (() -> Unit)? = null,
    click: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .clickable(enabled = click != null) { click?.invoke() },
        //shape = MaterialTheme.shapes.medium,
        //elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stringResource(id = title),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant

                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = body,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            option?.invoke()
        }
    }
}



@Composable
fun LogOut(
    navController: NavController,
    viewModel: ProfileViewModel
) {
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        TextButton(
            onClick = {
                coroutineScope.launch {
                    val success = viewModel.logOut()
                    if (success) {
                        navController.navigate(Routes.LOGIN.name) {
                            popUpTo(navController.graph.startDestinationId) {
                                inclusive = true
                            }
                        }
                    }
                }
            }
        ) {
            Text(
                text = "Cerrar sesión",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

