package com.example.run.presentation.run_overview

import com.example.run.presentation.run_overview.model.RunUI

sealed interface RunOverviewAction {
    data object OnStartRun : RunOverviewAction
    data object OnLogOutClicked : RunOverviewAction
    data object OnAnalyticsClick : RunOverviewAction
    data class OnDeleteRun(val runUi: RunUI) : RunOverviewAction
}