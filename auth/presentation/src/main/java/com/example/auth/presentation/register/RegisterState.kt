package com.example.auth.presentation.register

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.text2.input.TextFieldState
import com.example.auth.domain.PasswordValidationState

@OptIn(ExperimentalFoundationApi::class)
data class RegisterState(
    val email: TextFieldState = TextFieldState(),
    val isEmailValid: Boolean = true,
    val password: TextFieldState = TextFieldState(),
    val isPasswordVisible: Boolean = true,
    val passwordValidationState: PasswordValidationState = PasswordValidationState(),
    val isRegistering: Boolean = false,
    val canRegister: Boolean = false
)
