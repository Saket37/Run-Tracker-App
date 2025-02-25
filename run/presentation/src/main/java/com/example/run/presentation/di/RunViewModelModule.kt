package com.example.run.presentation.di

import com.example.run.presentation.active_run.ActiveRunViewModel
import com.example.run.presentation.run_overview.RunOverviewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val runViewModelModule = module {
    viewModelOf(::ActiveRunViewModel)
    viewModelOf(::RunOverviewModel)
}