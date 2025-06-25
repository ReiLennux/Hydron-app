package com.undefined.hydron.presentation.shared

import android.content.Context
import android.net.Uri
import android.util.Patterns
import com.undefined.hydron.R
import com.undefined.hydron.domain.models.SexType
import com.undefined.hydron.domain.models.ValidationResult
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDate
import javax.inject.Inject

class Validations @Inject constructor(
    @ApplicationContext private val context: Context
) {
    // Regex
    private val STRONG_PASSWORD = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@\$!%*?&]{8,}$".toRegex()
    private val BASIC_TEXT = "^[^\\s].{2,}$".toRegex()
    private val POSTAL_CODE = "^\\d{4,6}$".toRegex()
    private val SEX = "^[HM]$".toRegex()
    private val ELECTOR_KEY = "[A-Z]{6}[0-9]{8}[A-Z]{1}[0-9]{3}".toRegex()
    private val CURP_REGEX = "^[A-Z]{1}[AEIOU]{1}[A-Z]{2}[0-9]{2}(0[1-9]|1[0-2])(0[1-9]|1[0-9]|2[0-9]|3[0-1])[HM]{1}(AS|BC|BS|CC|CL|CM|CS|CH|DF|DG|GT|GR|HG|JC|MC|MN|MS|NT|NL|OC|PL|QT|QR|SP|SL|SR|TC|TS|TL|VZ|YN|ZS|NE)[B-DF-HJ-NP-TV-Z]{3}[0-9A-Z]{1}[0-9]{1}$".toRegex()

    //Validación email
    fun validateEmail(email: String): ValidationResult{
        if (email.isBlank()){
            return ValidationResult(
                successful = false,
                errorMessage = context.getString(R.string.val_email_empty)
            )
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            return ValidationResult(
                successful = false,
                errorMessage = context.getString(R.string.val_email_invalid)
            )
        }
        return ValidationResult(
            successful = true
        )
    }

    //Validación contrseña segura
    fun validateStrongPassword(password: String): ValidationResult{
        if (password.isBlank()){
            return ValidationResult(
                successful = false,
                errorMessage = context.getString(R.string.val_password_empty)
            )
        }
        if (!STRONG_PASSWORD.matches(password)){
            return ValidationResult(
                successful = false,
                errorMessage = context.getString(R.string.val_password_invalid)
            )
        }
        return ValidationResult(
            successful = true
        )
    }

    //Validación contraseña repetida
    fun validateRepeatedPassword(password: String, repeatedPassword: String): ValidationResult{
        if (password != repeatedPassword){
            return ValidationResult(
                successful = false,
                errorMessage = context.getString(R.string.val_password_match)
            )
        }
        return ValidationResult(
            successful = true
        )
    }

    //Validación texto basico
    fun validateBasicText(text: String): ValidationResult{
        if (text.isBlank()){
            return ValidationResult(
                successful = false,
                errorMessage = context.getString(R.string.val_text_empty)
            )
        }
        if (!BASIC_TEXT.matches(text)){
            return ValidationResult(
                successful = false,
                errorMessage = context.getString(R.string.val_text_invalid)
            )
        }
        return ValidationResult(
            successful = true
        )
    }

    //Validación código postal
    fun validatePostalCode(postalCode: String): ValidationResult{
        if (postalCode.isBlank()){
            return ValidationResult(
                successful = false,
                errorMessage = "El campo no puede estar vacio."
            )
        }
        if (!POSTAL_CODE.matches(postalCode)){
            return ValidationResult(
                successful = false,
                errorMessage = "Solo se permiten de 4 a 6 digitos."
            )
        }
        return ValidationResult(
            successful = true
        )
    }

    //Validación sexo
    fun validateSex(sex: SexType): ValidationResult{
        return if (sex.toString().isBlank()) {
            ValidationResult(
                successful = false,
                errorMessage = "El campo no puede estar vacio."
            )
        }else {
            ValidationResult(
                successful = true
            )
        }
    }

    //Validación terminos y condiciones
    fun validateTerms(terms: Boolean): ValidationResult{
        if (!terms){
            return ValidationResult(
                successful = false,
                errorMessage = "Por favor acepta los terminos"
            )
        }
        return ValidationResult(
            successful = true
        )
    }

    // Validación cumpleaños
    fun validateBirthdate(birthdate: LocalDate): ValidationResult{
        if (birthdate == LocalDate.now()){
            return ValidationResult(
                successful = false,
                errorMessage = "Fecha invalida, la fecha introducida es hoy!"
            )
        }
        return ValidationResult(
            successful = true
        )
    }

    // Validación Image
    fun  validateImagesList(images: List<Uri>): ValidationResult {
        return if (images.isEmpty()) {
            ValidationResult(
                successful = false,
                errorMessage = "Debe subir al menos una imagen."
            )
        }else {
            ValidationResult(
                successful = true
            )
        }
    }

    fun validateList(list: List<String>): ValidationResult {
        return if (list.isEmpty()) {
            ValidationResult(
                successful = false,
                errorMessage = "Debe tener al menos 3 elementos en la lista."
            )
        } else {
            ValidationResult(
                successful = true
            )
        }
    }

    fun validateComboBox(selected : String): ValidationResult {
        return if (selected.isEmpty()) {
            ValidationResult(
                successful = false,
                errorMessage = "Debe seleccionar una opción."
            )
        }else {
            ValidationResult(
                successful = true
            )
        }
    }

    fun validateLocation(longitude: Double, latitude: Double): ValidationResult {
        return when {
            longitude == 0.0 && latitude == 0.0 -> {
                ValidationResult(
                    successful = false,
                    errorMessage = "Debe seleccionar una ubicación."
                )
            }
            else -> {
                ValidationResult(successful = true)
            }
        }
    }




    fun validateHeight(height: Double): ValidationResult {
        return if (height == 0.0 || height < 0.0) {
            ValidationResult(
                successful = false,
                errorMessage = "Debe ingresar una altura válida."
            )
            } else {
                ValidationResult(successful = true)
            }
    }

    fun validateWeight(weight: Double): ValidationResult {
        return if (weight == 0.0 || weight < 0.0) {
            ValidationResult(
                successful = false,
                errorMessage = "Debe ingresar un peso válido."
            )
            } else {
                ValidationResult(successful = true)
            }

    }



}