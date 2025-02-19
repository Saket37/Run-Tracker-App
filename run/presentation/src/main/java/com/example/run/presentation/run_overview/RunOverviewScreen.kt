@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.run.presentation.run_overview

import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.core.presentation.designsystem.AnalyticsIcon
import com.example.core.presentation.designsystem.LogoIcon
import com.example.core.presentation.designsystem.LogoutIcon
import com.example.core.presentation.designsystem.RunIcon
import com.example.core.presentation.designsystem.components.RunTrackerFloatingActionButton
import com.example.core.presentation.designsystem.components.RunTrackerScaffold
import com.example.core.presentation.designsystem.components.RunTrackerToolbar
import com.example.core.presentation.designsystem.components.util.DropDownItem
import com.example.run.presentation.R
import org.koin.androidx.compose.koinViewModel

@Composable
fun RunOverviewScreenRoot(
    onStartRunClick: () -> Unit,
    viewModel: RunOverviewModel = koinViewModel()
) {
    RunOverviewScreen(onAction = { action ->
        when (action) {
            RunOverviewAction.OnStartRun -> onStartRunClick()
            else -> Unit
        }
        viewModel.onAction(action)
    })
}

@Composable
fun RunOverviewScreen(
    onAction: (RunOverviewAction) -> Unit
) {
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(state = topAppBarState)
    RunTrackerScaffold(
        topAppBar = {
            RunTrackerToolbar(
                showBackButton = false,
                title = stringResource(id = R.string.runtracker),
                scrollBehaviour = scrollBehavior,
                startContent = {
                    Icon(
                        imageVector = LogoIcon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(30.dp)
                    )
                },
                menuItems = listOf(
                    DropDownItem(
                        icon = AnalyticsIcon,
                        title = stringResource(id = R.string.analytics)
                    ), DropDownItem(icon = LogoutIcon, title = stringResource(R.string.log_out))
                ), onMenuItemClicked = { index ->
                    when (index) {
                        0 -> onAction(RunOverviewAction.OnAnalyticsClick)
                        1 -> onAction(RunOverviewAction.OnLogOutClicked)
                    }
                }
            )
        },
        floatingActionButton = {
            RunTrackerFloatingActionButton(
                icon = RunIcon,
                onClick = { onAction(RunOverviewAction.OnStartRun) })
        }
    ) { paddingValues ->

    }

}

@Preview
@Composable
private fun RunOverviewScreenPreview() {
    RunOverviewScreen() {}
}