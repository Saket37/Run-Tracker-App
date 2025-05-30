package com.example.auth.presentation.login

sealed interface LoginAction {
    data object OnTogglePasswordVisibility : LoginAction
    data object OnLoginClicked : LoginAction
    data object OnRegisterClicked : LoginAction
}