@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.run.presentation.active_run

import android.Manifest
import android.content.Context
import android.os.Build
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
import com.example.core.presentation.designsystem.components.OutlineActionButton
import com.example.core.presentation.designsystem.components.RunTrackerDialog
import com.example.core.presentation.designsystem.components.RunTrackerFloatingActionButton
import com.example.core.presentation.designsystem.components.RunTrackerScaffold
import com.example.core.presentation.designsystem.components.RunTrackerToolbar
import com.example.run.presentation.R
import com.example.run.presentation.active_run.components.RunDataCard
import com.example.run.presentation.active_run.util.hasLocationPermission
import com.example.run.presentation.active_run.util.hasNotificationPermission
import com.example.run.presentation.active_run.util.shouldShowLocationPermissionRationale
import com.example.run.presentation.active_run.util.shouldShowNotificationPermissionRationale
import org.koin.androidx.compose.koinViewModel

@Composable
fun ActiveRunScreenRoot(viewModel: ActiveRunViewModel = koinViewModel()) {
    ActiveRunScreen(
        state = viewModel.state,
        onAction = viewModel::onAction
    )
}

@Composable
fun ActiveRunScreen(
    state: ActiveRunState,
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
            RunDataCard(
                elapsedTime = state.elapsedTime,
                runData = state.runData,
                modifier = Modifier
                    .padding(16.dp)
                    .padding(paddingValues)
                    .fillMaxWidth()
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
                        }
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
        ActiveRunScreen(state = ActiveRunState(), {})
    }

}