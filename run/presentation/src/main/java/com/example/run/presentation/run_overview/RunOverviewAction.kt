package com.example.run.presentation.run_overview

sealed interface RunOverviewAction {
    data object OnStartRun : RunOverviewAction
    data object OnLogOutClicked : RunOverviewAction
    data object OnAnalyticsClick : RunOverviewAction
}