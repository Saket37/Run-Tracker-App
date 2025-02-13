package com.example.auth.presentation.login

import com.example.core.presentation.ui.ui.UiText

sealed interface LoginEvents {
    data class Error(val error: UiText) : LoginEvents
    data object LoginSuccess : LoginEvents
}