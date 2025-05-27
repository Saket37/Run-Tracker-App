@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.run.presentation.active_run

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.core.presentation.designsystem.RunTrackerTheme
import com.example.core.presentation.designsystem.StartIcon
import com.example.core.presentation.designsystem.StopIcon
import com.example.core.presentation.designsystem.components.ActionButton
import com.example.core.presentation.designsystem.components.OutlineActionButton
import com.example.core.presentation.designsystem.components.RunTrackerDialog
import com.example.core.presentation.designsystem.components.RunTrackerFloatingActionButton
import com.example.core.presentation.designsystem.components.RunTrackerScaffold
import com.example.core.presentation.designsystem.components.RunTrackerToolbar
import com.example.core.presentation.ui.ui.ObserveAsEvents
import com.example.run.presentation.R
import com.example.run.presentation.active_run.components.RunDataCard
import com.example.run.presentation.active_run.service.ActiveRunService
import com.example.run.presentation.active_run.util.hasLocationPermission
import com.example.run.presentation.active_run.util.hasNotificationPermission
import com.example.run.presentation.active_run.util.shouldShowLocationPermissionRationale
import com.example.run.presentation.active_run.util.shouldShowNotificationPermissionRationale
import com.example.run.presentation.maps.TrackerMap
import org.koin.androidx.compose.koinViewModel
import java.io.ByteArrayOutputStream

@Composable
fun ActiveRunScreenRoot(
    onFinish: () -> Unit,
    onBack: () -> Unit,
    viewModel: ActiveRunViewModel = koinViewModel(),
    onServiceToggle: (isServiceStarted: Boolean) -> Unit
) {
    val context = LocalContext.current
    ObserveAsEvents(flow = viewModel.events) { event ->
        when (event) {
            is ActiveRunEvent.Error -> {
                Toast.makeText(context, event.error.asString(context), Toast.LENGTH_LONG).show()
            }

            ActiveRunEvent.RunSaved -> {
                onFinish()
            }
        }
    }
    ActiveRunScreen(
        state = viewModel.state,
        onServiceToggle = onServiceToggle,
        onAction = { action ->
            when (action) {
                is ActiveRunAction.OnBackClick -> {
                    if (!viewModel.state.hasStartedRunning) {
                        onBack()
                    }
                }

                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}

@Composable
fun ActiveRunScreen(
    state: ActiveRunState,
    onServiceToggle: (isServiceStarted: Boolean) -> Unit,
    onAction: (ActiveRunAction) -> Unit
) {
    val context = LocalContext.current

    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions()
        ) { perms ->
            val hasCoarseLocationPermission =
                perms[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            val hasFineLocationPermission =
                perms[Manifest.permission.ACCESS_FINE_LOCATION] == true
            val hasNotificationPermission = if (Build.VERSION.SDK_INT >= 33) {
                perms[Manifest.permission.POST_NOTIFICATIONS] == true
            } else true
            val activity = context as ComponentActivity

            val showLocationPermissionRationale = activity.shouldShowLocationPermissionRationale()
            val showNotificationPermissionRationale =
                activity.shouldShowNotificationPermissionRationale()

            onAction(
                ActiveRunAction.SubmitLocationPermissionInfo(
                    acceptedLocationPermission = hasCoarseLocationPermission && hasFineLocationPermission,
                    showLocationPermissionRationale = showLocationPermissionRationale
                )
            )
            onAction(
                ActiveRunAction.SubmitNotificationPermissionInfo(
                    acceptedNotificationPermission = hasNotificationPermission,
                    showLocationPermissionRationale = showNotificationPermissionRationale
                )
            )
        }

    LaunchedEffect(key1 = true) {
        val activity = context as ComponentActivity
        val showLocationPermissionRationale = activity.shouldShowLocationPermissionRationale()
        val showNotificationPermissionRationale =
            activity.shouldShowNotificationPermissionRationale()
        onAction(
            ActiveRunAction.SubmitLocationPermissionInfo(
                acceptedLocationPermission = context.hasLocationPermission(),
                showLocationPermissionRationale = showLocationPermissionRationale
            )
        )
        onAction(
            ActiveRunAction.SubmitNotificationPermissionInfo(
                acceptedNotificationPermission = context.hasNotificationPermission(),
                showLocationPermissionRationale = showNotificationPermissionRationale
            )
        )

        if (!showLocationPermissionRationale && !showNotificationPermissionRationale) {
            permissionLauncher.requestRunTrackerPermissions(context)
        }
    }
    LaunchedEffect(state.isRunFinished) {
        if (state.isRunFinished) {
            onServiceToggle(false)
        }
    }
    LaunchedEffect(state.shouldTrack) {
        if (context.hasLocationPermission() && state.shouldTrack && !ActiveRunService.isServiceActive) {
            onServiceToggle(true)
        }
    }

    RunTrackerScaffold(
        withGradient = false,
        topAppBar = {
            RunTrackerToolbar(
                showBackButton = true,
                title = stringResource(id = R.string.active_run),
                onBackClick = { onAction(ActiveRunAction.OnBackClick) })
        },
        floatingActionButton = {
            RunTrackerFloatingActionButton(
                icon = if (state.shouldTrack) StopIcon else StartIcon,
                onClick = { onAction(ActiveRunAction.OnToggleRunClick) },
                iconSize = 20.dp,
                contentDescription = if (state.shouldTrack) stringResource(id = R.string.pause_run) else stringResource(
                    id = R.string.start_run
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            TrackerMap(
                isRunFinished = state.isRunFinished,
                currentLocation = state.currentLocation,
                locations = state.runData.locations,
                onSnapshot = { bmp ->
                    val stream = ByteArrayOutputStream()
                    stream.use {
                        bmp.compress(
                            Bitmap.CompressFormat.JPEG, 30, it
                        )
                    }
                    onAction(ActiveRunAction.OnRunProcessed(stream.toByteArray()))
                },
                modifier = Modifier.fillMaxSize()
            )
            RunDataCard(
                elapsedTime = state.elapsedTime,
                runData = state.runData,
                modifier = Modifier
                    .padding(16.dp)
                    .padding(paddingValues)
                    .fillMaxWidth()
            )
        }
        if (!state.shouldTrack && state.hasStartedRunning) {
            RunTrackerDialog(
                title = stringResource(id = R.string.running_is_paused),
                onDismiss = {
                    onAction(ActiveRunAction.OnResumeRunClick)
                },
                description = stringResource(id = R.string.resume_or_finish_run),
                primaryButton = {
                    ActionButton(
                        text = stringResource(R.string.resume),
                        isLoading = false,
                        onClick = {
                            onAction(ActiveRunAction.OnResumeRunClick)
                        },
                        modifier = Modifier.weight(1f)
                    )
                },
                secondaryButton = {
                    OutlineActionButton(
                        text = stringResource(id = R.string.finish),
                        isLoading = state.isSavingRun,
                        onClick = {
                            onAction(ActiveRunAction.OnFinishRunClick)
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            )
        }

        if (state.showLocationPermissionRationale || state.showNotificationPermissionRationale) {
            RunTrackerDialog(
                title = stringResource(id = R.string.permission_required),
                onDismiss = { /* Normal Dismiss is not allowed for permission*/ },
                description = when {
                    state.showLocationPermissionRationale && state.showNotificationPermissionRationale -> stringResource(
                        id = R.string.location_notification_rationale
                    )

                    state.showLocationPermissionRationale -> stringResource(id = R.string.location_rationale)
                    else -> stringResource(id = R.string.notification_rationale)
                },
                primaryButton = {
                    OutlineActionButton(
                        text = stringResource(id = R.string.okay),
                        isLoading = false,
                        onClick = {
                            onAction(ActiveRunAction.DismissRationaleDialog)
                            permissionLauncher.requestRunTrackerPermissions(context)
                        },
                        modifier = Modifier.weight(1f)

                    )
                }

            )
        }
    }
}

private fun ActivityResultLauncher<Array<String>>.requestRunTrackerPermissions(
    context: Context
) {
    val hasLocationPermissions = context.hasLocationPermission()
    val hasNotificationPermission = context.hasNotificationPermission()

    val locationPermissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    val notificationPermission = if (Build.VERSION.SDK_INT >= 33) {
        arrayOf(Manifest.permission.POST_NOTIFICATIONS)
    } else arrayOf()

    when {
        !hasLocationPermissions && !hasNotificationPermission -> {
            launch(locationPermissions + notificationPermission)
        }

        !hasLocationPermissions -> launch(locationPermissions)
        !hasNotificationPermission -> launch(notificationPermission)
    }
}

@Preview
@Composable
private fun ActiveRunScreenPreview() {
    RunTrackerTheme {
        ActiveRunScreen(state = ActiveRunState(), onServiceToggle = {}, {})
    }

}