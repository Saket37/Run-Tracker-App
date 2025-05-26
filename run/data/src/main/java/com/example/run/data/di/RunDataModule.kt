package com.example.run.data.di

import com.example.core.domain.run.SyncRunScheduler
import com.example.run.data.CreateRunWorker
import com.example.run.data.DeleteRunWorker
import com.example.run.data.FetchRunWorkers
import com.example.run.data.SyncRunWorkerScheduler
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val runDataModule = module {
    workerOf(::CreateRunWorker)
    workerOf(::DeleteRunWorker)
    workerOf(::FetchRunWorkers)

    singleOf(::SyncRunWorkerScheduler).bind<SyncRunScheduler>()
}