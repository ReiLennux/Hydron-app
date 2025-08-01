package com.undefined.hydron.presentation

import androidx.lifecycle.ViewModel
import com.undefined.hydron.presentation.shared.navigation.enums.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
): ViewModel() {

    fun verifyRouteTop(currentRoute: String?): Boolean {
        return !(currentRoute == Routes.LOGIN.name || currentRoute == Routes.SIGN_UP.name)
    }

    fun verifyRouteBottom(currentRoute: String?): Boolean {
        return !(currentRoute == Routes.LOGIN.name || currentRoute == Routes.SIGN_UP.name)
    }
}