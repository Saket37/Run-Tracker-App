package com.example.run.data

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.await
import com.example.core.database.dao.RunPendingSyncDao
import com.example.core.database.entity.DeletedRunSyncEntity
import com.example.core.database.entity.RunPendingSyncEntity
import com.example.core.database.mappers.toRunEntity
import com.example.core.domain.SessionStorage
import com.example.core.domain.run.Run
import com.example.core.domain.run.RunId
import com.example.core.domain.run.SyncRunScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import kotlin.time.Duration
import kotlin.time.toJavaDuration

class SyncRunWorkerScheduler(
    private val context: Context,
    private val pendingSyncDao: RunPendingSyncDao,
    private val sessionStorage: SessionStorage,
    private val applicationScope: CoroutineScope
) : SyncRunScheduler {

    private val workManager = WorkManager.getInstance(context)

    override suspend fun scheduleSync(type: SyncRunScheduler.SyncType) {
        when (type) {
            is SyncRunScheduler.SyncType.FetchRuns -> scheduleFetchRunWorker(type.interval)

            is SyncRunScheduler.SyncType.CreateRun -> scheduleCreateRunWorker(
                run = type.run,
                mapPictureBytes = type.mapPictureBytes
            )

            is SyncRunScheduler.SyncType.DeleteRun -> scheduleDeleteRunWorker(type.runId)

        }
    }

    private suspend fun scheduleDeleteRunWorker(runId: RunId) {
        val userId = sessionStorage.get()?.userId ?: return
        val entity = DeletedRunSyncEntity(
            userId = userId,
            runId = runId
        )
        pendingSyncDao.upsertDeletedRunSyncEntity(entity)
        val workRequest = OneTimeWorkRequestBuilder<DeleteRunWorker>()
            .addTag("delete_work")
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .setBackoffCriteria(
                backoffPolicy = BackoffPolicy.EXPONENTIAL,
                backoffDelay = 2000L,
                timeUnit = TimeUnit.MILLISECONDS
            )
            .setInputData(
                Data.Builder()
                    .putString(DeleteRunWorker.RUN_ID, entity.runId)
                    .build()
            )

        applicationScope.launch {
            workManager.enqueue(workRequest.build()).await()
        }.join()

    }

    private suspend fun scheduleCreateRunWorker(run: Run, mapPictureBytes: ByteArray) {
        val userId = sessionStorage.get()?.userId ?: return
        val pendingRun = RunPendingSyncEntity(
            userId = userId,
            run = run.toRunEntity(),
            mapPictureBytes = mapPictureBytes
        )
        pendingSyncDao.upsertRunPendingSyncEntity(pendingRun)

        val workRequest = OneTimeWorkRequestBuilder<CreateRunWorker>()
            .addTag("create_work")
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .setBackoffCriteria(
                backoffPolicy = BackoffPolicy.EXPONENTIAL,
                backoffDelay = 2000L,
                timeUnit = TimeUnit.MILLISECONDS
            )
            .setInputData(
                Data.Builder()
                    .putString(CreateRunWorker.RUN_ID, pendingRun.runId)
                    .build()
            )
        applicationScope.launch {
            workManager.enqueue(workRequest.build()).await()
        }.join()

    }

    private suspend fun scheduleFetchRunWorker(interval: Duration) {
        val isSyncScheduled = withContext(Dispatchers.IO) {
            workManager
                .getWorkInfosByTag("sync_work")
                .get()
                .isNotEmpty()
        }
        if (isSyncScheduled) {
            return
        }
        val workRequest = PeriodicWorkRequestBuilder<FetchRunWorkers>(
            repeatInterval = interval.toJavaDuration()
        )
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .setBackoffCriteria(
                backoffPolicy = BackoffPolicy.EXPONENTIAL,
                backoffDelay = 2000L,
                timeUnit = TimeUnit.MILLISECONDS
            )
            .setInitialDelay(
                duration = 30,
                timeUnit = TimeUnit.MINUTES
            )
            .addTag("sync_work")
            .build()

        workManager.enqueue(workRequest).await()
    }

    override suspend fun cancelAllSyncs() {
        WorkManager.getInstance(context).cancelAllWork().await()
    }


}