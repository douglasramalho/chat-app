package com.example.chatapp.ui.component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import com.example.chatapp.navigation.TopLevelDestination
import com.example.chatapp.ui.isTopLevelInHierarchy

@Composable
fun ChatNavigationRail(
    destinations: List<TopLevelDestination>,
    currentDestination: NavDestination?,
    onDestinationSelected: (destination: TopLevelDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavigationRail(
        modifier = modifier,
    ) {
        destinations.sortedByDescending {
            it == TopLevelDestination.PLUS_BUTTON
        }.forEach { destination ->
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
                
                Spacer(modifier = Modifier.height(30.dp))
            } else {
                val selected = currentDestination.isTopLevelInHierarchy(destination)

                NavigationRailItem(
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
                    }
                )
            }
        }
    }
}