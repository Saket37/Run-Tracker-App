package com.example.run.data.di

import com.example.run.data.CreateRunWorker
import com.example.run.data.DeleteRunWorker
import com.example.run.data.FetchRunWorkers
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.dsl.module

val runDataModule = module {
    workerOf(::CreateRunWorker)
    workerOf(::DeleteRunWorker)
    workerOf(::FetchRunWorkers)



}