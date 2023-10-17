package com.example.chatapp.ui.component

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import com.example.chatapp.navigation.TopLevelDestination

@Composable
fun ChatNavigationBar(
    destinations: List<TopLevelDestination>,
    currentDestination: NavDestination?,
    onDestinationSelected: (destination: TopLevelDestination) -> Unit,
    modifier: Modifier,
) {
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp
    ) {
        destinations.forEach { destination ->
            if (destination == TopLevelDestination.PLUS_BUTTON) {
                FloatingActionButton(
                    onClick = {
                        onDestinationSelected(destination)
                    },
                    modifier = Modifier.padding(top = 6.dp),
                    shape = CircleShape,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    elevation = FloatingActionButtonDefaults.elevation(
                        defaultElevation = 0.dp,
                        pressedElevation = 0.dp,
                        focusedElevation = 0.dp,
                        hoveredElevation = 0.dp
                    )
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = destination.title)
                }
            } else {
                val selected = currentDestination.isTopLevelInHierarchy(destination)
                NavigationBarItem(
                    selected = selected,
                    onClick = {
                        onDestinationSelected(destination)
                    },
                    icon = {
                        Icon(
                            painter = painterResource(id = destination.icon),
                            contentDescription = destination.title
                        )
                    },
                    label = {
                        Text(text = destination.title)
                    },
                    alwaysShowLabel = true,
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = MaterialTheme.colorScheme.surface,
                    )
                )
            }
        }
    }
}

private fun NavDestination?.isTopLevelInHierarchy(destination: TopLevelDestination) =
    this?.hierarchy?.any {
        it.route?.contains(destination.name, true) ?: false
    } ?: false