package com.example.core.domain.run

import kotlin.time.Duration

interface SyncRunScheduler {

    suspend fun scheduleSync(type: SyncType)

    suspend fun cancelAllSyncs()

    sealed interface SyncType {
        data class FetchRuns(val interval: Duration) : SyncType
        data class CreateRun(val run: Run, val mapPictureBytes: ByteArray) : SyncType
        data class DeleteRun(val runId: String) : SyncType

    }
}