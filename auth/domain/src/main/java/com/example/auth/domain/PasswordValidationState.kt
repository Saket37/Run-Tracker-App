package com.example.auth.domain

data class PasswordValidationState(
    val hasMinLength: Boolean = false,
    val hasUppercaseCharacter: Boolean = false,
    val hasLowercaseCharacter: Boolean = false,
    val hasNumber: Boolean = false,
){
    val isValid: Boolean
        get() = hasMinLength && hasUppercaseCharacter && hasLowercaseCharacter && hasNumber
}
