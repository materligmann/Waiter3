package com.example.waiter3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import centmoinshuitstudio.waiter3.MainActivity.OrderScreen
import centmoinshuitstudio.waiter3.ModelPreferencesManager
import com.example.waiter3.Currency.CurrencyScreen
import com.example.waiter3.Menu.MenuScreen
import com.example.waiter3.NewOrder.NewOrderScreen
import com.example.waiter3.Orders.OrdersScreen
import com.example.waiter3.Settings.SettingsScreen
import com.example.waiter3.Subscription.SubscriptionScreen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ModelPreferencesManager.with(application)
        setContent {
            UserApplication()
        }
    }
}

@Composable
fun UserApplication() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            OrdersScreen(navController)
        }
        composable(
            route = "order/{orderId}",
            arguments =  listOf(navArgument("orderId") {
                type = NavType.StringType
            })) { navStackBackEntry ->
            navStackBackEntry.arguments!!.getString("orderId")?.let { OrderScreen(navController, it) }
        }
        composable("settings") {
            SettingsScreen(navController = navController)
        }
        composable("menu") {
            MenuScreen(navController = navController)
        }
        composable("newOrder") {
            NewOrderScreen(navController = navController)
        }
        composable("currency") {
            CurrencyScreen(navController = navController)
        }
        composable("subscription") {
            SubscriptionScreen(navController = navController)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(title: String, leftIcon: ImageVector?, rightIcon: ImageVector?, leftIconClick: () -> Unit, rightIconClick: (() -> Unit)?) {
    TopAppBar(
        navigationIcon = {
            if (leftIcon != null) {
                Icon(
                    imageVector = leftIcon,
                    contentDescription = "",
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .clickable { leftIconClick.invoke() },
                )
            }
        },
        actions = {
            if (rightIcon != null) {
                IconButton(onClick = { rightIconClick?.invoke()}) {
                    Icon(imageVector = rightIcon, contentDescription = "Localized description")
                }
            }
        },
        colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color.White),
        title = { Text(title) }
    )
}