package com.example.run.presentation.run_overview

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.domain.SessionStorage
import com.example.core.domain.run.RunRepository
import com.example.core.domain.run.SyncRunScheduler
import com.example.run.presentation.run_overview.mapper.toRunUI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class RunOverviewModel(
    private val runRepository: RunRepository,
    private val syncRunScheduler: SyncRunScheduler,
    private val applicationScope: CoroutineScope,
    private val sessionStorage: SessionStorage
) : ViewModel() {

    var state by mutableStateOf(RunOverviewState())
        private set

    init {

//        viewModelScope.launch {
//            syncRunScheduler.scheduleSync(
//                type = SyncRunScheduler.SyncType.FetchRuns(30.minutes)
//            )
//        }

        runRepository.getRuns().onEach { runs ->
            val runUi = runs.map { it.toRunUI() }
            state = state.copy(runs = runUi)
        }.launchIn(viewModelScope)

//        viewModelScope.launch {
//            runRepository.syncPendingRuns()
//            runRepository.fetchRuns()
//        }
    }

    fun onAction(action: RunOverviewAction) {
        when (action) {
            is RunOverviewAction.OnDeleteRun -> {
                viewModelScope.launch {
                    runRepository.deleteRun(action.runUi.id)
                }
            }

            RunOverviewAction.OnLogOutClicked -> logout()
            RunOverviewAction.OnStartRun -> Unit
            else -> Unit
        }
    }

    private fun logout() {
        applicationScope.launch {
            //syncRunScheduler.cancelAllSyncs()
            //runRepository.deleteAllRuns()
            //runRepository.logout()
            sessionStorage.set(null)
        }
    }
}