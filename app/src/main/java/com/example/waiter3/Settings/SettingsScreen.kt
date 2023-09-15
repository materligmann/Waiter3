package com.example.waiter3.Settings


import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import centmoinshuitstudio.waiter3.R
import com.example.waiter3.AppBar
import com.example.waiter3.Orders.prepareBottomMenu


@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun SettingsScreen(navController: NavHostController) {

    val bottomMenuItemsList = prepareBottomMenu()


    var selectedItem = remember {
        mutableStateOf("Settings")
    }


    Scaffold(
        topBar = {
            AppBar("Settings", null, null, { }, { })
        },
        bottomBar = {
            BottomNavigation(
                backgroundColor = Color.White
            ) {
                // this is a row scope
                // all items are added horizontally

                bottomMenuItemsList.forEach { menuItem ->
                    // adding each item
                    BottomNavigationItem(
                        selected = (selectedItem.value == menuItem.label),
                        onClick = {
                            selectedItem.value = menuItem.label
                            navController.navigate(menuItem.route)
                        },
                        icon = {
                            Icon(
                                imageVector = if (selectedItem.value == menuItem.label) { menuItem.selectedIcon} else { menuItem.unselectedIcon },
                                contentDescription = menuItem.label
                            )
                        },
                        label = {
                            Text(text = menuItem.label)
                        },
                        enabled = true
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier
            .padding(innerPadding)) {
            Surface() {
                Column(
                    Modifier
                        .background(Color.White)
                        .fillMaxSize()) {
                    SettingsCard(settingsNav = SettingsNav("Menu", R.drawable.menu, { navController.navigate("menu") }))
                    SettingsCard(settingsNav = SettingsNav("Currency", R.drawable.euro, { navController.navigate("currency") }))
                    SettingsCard(settingsNav = SettingsNav("Waiter Pro", R.drawable.cart, { navController.navigate("subscription") }))
                }
            }
        }
    }
}

data class SettingsNav(val title: String, val iconId: Int, val action: () -> Unit)
@Composable
fun SettingContent(settingsNav: SettingsNav) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = painterResource(id = settingsNav.iconId),
            contentDescription = null,
            modifier = Modifier.height(40.dp)
        )
        Spacer(modifier = Modifier.width(20.dp))
        Text(text = settingsNav.title, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun SettingsCard(settingsNav: SettingsNav) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .padding(top = 8.dp, bottom = 4.dp, start = 16.dp, end = 16.dp)
            .wrapContentHeight(Alignment.Top)
            .clickable { settingsNav.action.invoke() },
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        )
    ) {
        SettingContent(settingsNav = settingsNav)
    }
}