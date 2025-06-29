package com.undefined.hydron.presentation.features.auth.register

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.undefined.hydron.R
import com.undefined.hydron.presentation.shared.components.textfields.GenericTextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.font.FontWeight
import com.undefined.hydron.domain.models.GenericCatalogModel
import com.undefined.hydron.domain.models.SexType
import com.undefined.hydron.presentation.shared.components.GenericCardWithCheck
import com.undefined.hydron.presentation.shared.components.GenericDropDownMenu
import com.undefined.hydron.presentation.shared.components.textfields.GenericDateField
import com.undefined.hydron.presentation.shared.components.textfields.GenericDoubleField
import com.undefined.hydron.presentation.shared.components.textfields.GenericTextArea
import com.undefined.hydron.presentation.shared.navigation.enums.Routes
import kotlinx.coroutines.launch


@Composable
fun RegisterContent(
    paddingValues: PaddingValues,
    navController: NavController,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .padding(paddingValues),
    ) {

        RegisterTitle()
        RegisterSection(viewModel, navController)
    }
}

@Composable
fun RegisterTitle() {
    Text(
        text = "Crea una cuenta",
        style = MaterialTheme.typography.displayLarge,
        modifier = Modifier.padding(bottom = 8.dp),
        color = MaterialTheme.colorScheme.secondary,
        fontWeight = FontWeight.Bold
    )
}


@Composable
fun RegisterSection(
    viewModel: RegisterViewModel,
    navController: NavController
) {

    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { 4 })
    HorizontalPager(
        state = pagerState,
        modifier = Modifier,
        userScrollEnabled = false
    ) { page ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .animateContentSize() // <= aquí también funciona
        ) {
            when (page) {
                0 -> AuthForm(
                    viewModel = viewModel,
                    navController = navController,
                    next = {
                        scope.launch {
                            pagerState.animateScrollToPage(1)
                        }
                    }
                )

                1 -> PersonalInfoForm(
                    viewModel = viewModel,
                    navController = navController,
                    prev = {
                        scope.launch {
                            pagerState.animateScrollToPage(0)
                        }
                    },
                    next = {
                        scope.launch {
                            pagerState.animateScrollToPage(2)
                        }
                    }
                )

                2 -> ChronialForm(
                    viewModel = viewModel,
                    navController = navController,
                    prev = {
                        scope.launch {
                            pagerState.animateScrollToPage(1)
                        }
                    }
                )
            }

        }
    }

}

@Composable
fun AuthForm(
    viewModel: RegisterViewModel,
    navController: NavController,
    next: () -> Unit,
) {

    val email by viewModel.email.observeAsState("")
    val password by viewModel.password.observeAsState("")
    val repeatedPassword by viewModel.repeatedPassword.observeAsState("")

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(text = "Crea una cuenta para empezar a usar la app.")
        GenericTextField(
            value = email,
            onValueChange = {
                viewModel.onEvent(
                    RegisterFormEvent.EmailChanged(it)
                )
            },
            labelRes = R.string.val_email_label,
            errorMessage = viewModel.emailError.value,
        )
        GenericTextField(
            value = password,
            onValueChange = {
                viewModel.onEvent(
                    RegisterFormEvent.PasswordChanged(it)
                )
            },
            labelRes = R.string.val_password_label,
            errorMessage = viewModel.passwordError.value,
        )
        GenericTextField(
            value = repeatedPassword,
            onValueChange = {
                viewModel.onEvent(
                    RegisterFormEvent.RepeatedPasswordChanged(it)
                )
            },
            labelRes = R.string.val_repeated_password_label,
            errorMessage = viewModel.repeatedPasswordError.value,
        )

        ActionsSection(
            viewModel = viewModel,
            navController = navController,
            next = next
        )

    }


}


@Composable
fun PersonalInfoForm(
    viewModel: RegisterViewModel,
    navController: NavController,
    next: () -> Unit,
    prev: () -> Unit,
) {

    val name by viewModel.name.observeAsState("")
    val sex by viewModel.sex.observeAsState("")
    val birthDate by viewModel.birthDate.observeAsState("")
    val height by viewModel.height.observeAsState()
    val weight by viewModel.weight.observeAsState()

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(text = "Ajustaremos nuestros parametros de salud con estos datos.")
        GenericTextField(
            value = name,
            onValueChange = {
                viewModel.onEvent(
                    RegisterFormEvent.NameChanged(it)
                )
            },
            labelRes = R.string.val_name_label,
            errorMessage = viewModel.nameError.value,
        )
        GenericDropDownMenu(
            label = R.string.val_sex_label,
            selectedText = sex.toString(),
            items = SexType.entries.map { it -> GenericCatalogModel(it.toString(), it.toString()) },
            onSelectedItem = {
                viewModel.onEvent(
                    RegisterFormEvent.SexChanged(SexType.valueOf(it.value))
                )
            },
            errorMessage = viewModel.sexError.value,
        )
        GenericDateField(
            dateValue = birthDate,
            onDateChange = { newDate ->
                viewModel.onEvent(RegisterFormEvent.BirthDateChanged(newDate))
            },
            labelRes = R.string.val_birth_date_label,
            errorMessage = viewModel.birthDateError.value,
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {

            GenericDoubleField(
                value = height?: "",
                onValueChange = { viewModel.onEvent(RegisterFormEvent.HeightChanged(it)) },
                labelRes = R.string.val_height_label,
                errorMessage = viewModel.heightError.value,
                intValue = 1,
                decValue = 2,
                modifier = Modifier.weight(0.5f)
            )

            Spacer(modifier = Modifier.width(16.dp))

            GenericDoubleField(
                value = weight?: "",
                onValueChange = { viewModel.onEvent(RegisterFormEvent.WeightChanged(it)) },
                labelRes = R.string.val_weight_label,
                errorMessage = viewModel.weightError.value,
                intValue = 3,
                decValue = 2,
                modifier = Modifier.weight(0.5f)
            )

        }

        ActionsSection(
            viewModel = viewModel,
            navController = navController,
            next = next,
            prev = prev
        )
    }

}

@Composable
fun ChronialForm(
    viewModel: RegisterViewModel,
    navController: NavController,
    prev: () -> Unit,
) {
    val hasHypertension by viewModel.hasHypertension.observeAsState(false)
    val hasDiabetes by viewModel.hasDiabetes.observeAsState(false)
    val hasHeartDisease by viewModel.hasHeartDisease.observeAsState(false)
    val cDDetails by viewModel.cDDetails.observeAsState("")
    var noDisease by remember { mutableStateOf(false) }


    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(text = "Tenemos que saber un poco mas sobre ti. Dinos, ¿Padeces alguna de estas enfermedades?")
        GenericCardWithCheck(
            title = R.string.val_hypertension_title,
            description = R.string.val_hypertension_description,
            icon = Icons.Filled.MonitorHeart,
            isChecked = hasHypertension,
            onSelect = {
                viewModel.onEvent(
                    RegisterFormEvent.HasHypertensionChanged(!hasHypertension)
                )
                noDisease = false
            }
        )
        GenericCardWithCheck(
            title = R.string.val_diabetes_title,
            description = R.string.val_diabetes_description,
            icon = Icons.Filled.MonitorHeart,
            isChecked = hasDiabetes,
            onSelect = {
                viewModel.onEvent(
                    RegisterFormEvent.HasDiabetesChanged(!hasDiabetes)
                )
                noDisease = false
            }
        )
        GenericCardWithCheck(
            title = R.string.val_heart_disease_title,
            description = R.string.val_heart_disease_description,
            icon = Icons.Filled.MonitorHeart,
            isChecked = hasHeartDisease,
            onSelect = {
                viewModel.onEvent(
                    RegisterFormEvent.HasHeartDiseaseChanged(!hasHeartDisease)
                )
                noDisease = false
            }
        )
        GenericCardWithCheck(
            title = R.string.val_no_disease_title,
            description = R.string.val_no_disease_description,
            icon = Icons.Filled.MonitorHeart,
            isChecked = noDisease,
            onSelect = {
                viewModel.onEvent(
                    RegisterFormEvent.HasDiabetesChanged(false)
                )
                viewModel.onEvent(
                    RegisterFormEvent.HasHeartDiseaseChanged(false)
                )
                viewModel.onEvent(
                    RegisterFormEvent.HasHypertensionChanged(false)
                )
                viewModel.onEvent(
                    RegisterFormEvent.ChronicDiseaseDetailsChanged("")
                )
                noDisease = true
            }
        )
        AnimatedVisibility(
            visible = hasHypertension || hasDiabetes || hasHeartDisease,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Column {

                GenericTextArea(
                    value = cDDetails.toString(),
                    onValueChange = {
                        viewModel.onEvent(
                            RegisterFormEvent.ChronicDiseaseDetailsChanged(it)
                        )
                    },
                    labelRes = R.string.val_moreabout_label,
                )
                Text(
                    text = "Para mejorar la presision de nuestros servicio es recomendable compartirnos mas informacion acerca de tu diagnostico",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

        }


        ActionsSection(
            viewModel = viewModel,
            navController = navController,
            prev = prev
        )
    }


}

@Composable
fun ActionsSection(
    viewModel: RegisterViewModel,
    navController: NavController,
    prev: (() -> Unit)? = null,
    next: (() -> Unit)? = null
) {

    val prevAction = prev ?: { navController.navigate(Routes.DASHBOARD.name) }
    val nextAction = next ?: { viewModel.onEvent(RegisterFormEvent.Submit) }
    val prevTitle = if (prev == null) "Ya tengo cuenta!" else "Anterior"
    val nextTitle = if (next == null) "Registrarse" else "Siguiente"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Button(
            onClick = { prevAction() },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onBackground)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "prev"
            )
            Text(text = prevTitle, modifier = Modifier.padding(start = 8.dp))
        }

        Button(
            onClick = { nextAction() },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "next"
            )
            Text(text = nextTitle, modifier = Modifier.padding(start = 8.dp))
        }

    }
}
