package com.example.auth.presentation.register

import com.example.core.presentation.ui.ui.UiText

sealed interface RegisterEvent {
    data object OnSuccessfulRegistration : RegisterEvent
    data class Error(val error: UiText): RegisterEvent

}