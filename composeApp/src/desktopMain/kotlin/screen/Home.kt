package screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import screen.table_test.TableTestTab
import screen.table_test_analytics.TableAnalyticsTab

class HomeScreen : Screen {
    @Composable
    override fun Content() {
        TabNavigator(TableTestTab) {
            HomeScreenContent()
        }
    }
}

@Composable
fun HomeScreenContent() {
    MaterialTheme {
        Scaffold(
            content = {
                Box(modifier = Modifier.fillMaxSize().padding(it)) {
                    CurrentTab()
                }
            },
            bottomBar = {
                NavigationBar {
                    TabNavigationItem(TableTestTab)
                    TabNavigationItem(TableAnalyticsTab)
                }
            }
        )
    }
}

@Composable
private fun RowScope.TabNavigationItem(tab: Tab) {
    val tabNavigator = LocalTabNavigator.current

    NavigationBarItem(
        selected = tabNavigator.current == tab,
        onClick = { tabNavigator.current = tab },
        icon = { Icon(painter = tab.options.icon!!, contentDescription = tab.options.title) },
        label = { Text(tab.options.title) }
    )
}