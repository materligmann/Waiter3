package com.example.waiter3.Orders


import android.annotation.SuppressLint
import android.util.Log
import android.view.Display.Mode
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.ImageVector
import centmoinshuitstudio.waiter3.ModelPreferencesManager
import androidx.compose.material.Surface
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.runtime.MutableState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.waiter3.Models.Orders
import centmoinshuitstudio.waiter3.R
import com.example.waiter3.AppBar
import java.text.SimpleDateFormat


data class BottomMenuItem(val label: String, val selectedIcon: ImageVector, val unselectedIcon: ImageVector, val route: String)


fun prepareBottomMenu(): List<BottomMenuItem> {
    val bottomMenuItemsList = arrayListOf<BottomMenuItem>()

    // add menu items
    bottomMenuItemsList.add(BottomMenuItem(label = "Orders", selectedIcon = Icons.Filled.Home, unselectedIcon = Icons.Outlined.Home,"home"))
    bottomMenuItemsList.add(BottomMenuItem(label = "Settings", selectedIcon = Icons.Filled.Settings, unselectedIcon = Icons.Outlined.Settings, "settings"))

    return bottomMenuItemsList
}

@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun OrdersScreen(navController: NavHostController) {
    val context = LocalContext.current
    var viewModel = OrdersViewModel(context)
    viewModel.connectToBilling {
        viewModel.checkPurchase()
    }
    var unlockPro = ModelPreferencesManager.get<Boolean>("KEY_PRO")
    if (unlockPro== null) {
        unlockPro = false
        ModelPreferencesManager.put(false, "KEY_PRO")
    }
    var orders = ModelPreferencesManager.get<Orders>("KEY_ORDERS")
    if (orders != null) {
        orders.entries.sortByDescending { it.date }
    }
    var rememberedOrders = remember { mutableStateOf(orders) }
    val bottomMenuItemsList = prepareBottomMenu()


    var selectedItem = remember {
        mutableStateOf("Orders")
    }

    Scaffold(
        topBar = {
            AppBar("Orders", null, Icons.Default.Add, { }, { onNewOrder(navController = navController, orders = rememberedOrders) })
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
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    if (orders != null) {
                        var orderDay = ""
                        val formatter = SimpleDateFormat("dd/MM/yyyy")
                        rememberedOrders.value?.entries?.forEachIndexed { index, order ->
                            if (orderDay == "") {
                                orderDay = formatter.format(order.date)
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(text = orderDay, fontWeight = FontWeight.Bold)
                            } else {
                                if (orderDay != formatter.format(order.date)) {
                                    orderDay = formatter.format(order.date)
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(text = orderDay, fontWeight = FontWeight.Bold)
                                }
                            }
                            var rememberedOrder = remember { mutableStateOf(order) }
                            OrderCard(orders = rememberedOrders, index = index) {
                                navController.navigate("order/${order.id}")
                            }
                        }
                    }
                }
            }
        }
    }
}

fun onNewOrder(navController: NavController, orders: MutableState<Orders?>) {
    var unlockPro = ModelPreferencesManager.get<Boolean>("KEY_PRO")
    if (unlockPro == true) {
        Log.d("myTag", "unlock")
            navController.navigate("newOrder")
    }  else {
        if (orders.value?.entries?.count() ?: 0 <= 5) {
            navController.navigate("newOrder")
        } else {
            navController.navigate("subscription")
        }
    }
}

fun LazyListState.isScrolledToTheEnd() = layoutInfo.visibleItemsInfo.lastOrNull()?.index == layoutInfo.totalItemsCount - 1

@Composable
fun OrderCard(orders: MutableState<Orders?>, index: Int, clickAction: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .padding(top = 0.dp, bottom = 0.dp, start = 0.dp, end = 0.dp)
            .wrapContentHeight(Alignment.Top)
            .clickable { clickAction.invoke() },
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        )
    ) {
        OrderContent(orders, index)
    }
    Divider()
}

@Composable
fun OrderContent(orders: MutableState<Orders?>, index: Int){
    val formatter = SimpleDateFormat("hh:mm")
    if (orders.value != null) {
        val time = formatter.format(orders.value!!.entries[index].date)
        var checked = remember { mutableStateOf(orders.value!!.entries[index].check) }
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxSize()) {
            ImageButton(checked = checked, onCheck = {
                orders.value!!.entries[index].check = true
                checked.value = true
                ModelPreferencesManager.put(orders.value, "KEY_ORDERS")
            }, onUnchecked = {
                orders.value!!.entries[index].check = false
                checked.value = false
                ModelPreferencesManager.put(orders.value, "KEY_ORDERS")
            })
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = time)
            Spacer(Modifier.weight(1f))
            Text(text = "Table " + orders.value!!.entries[index].table.toString(), fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ImageButton(checked: MutableState<Boolean>, onCheck: () -> Unit, onUnchecked: () -> Unit) {
    Box() {
        if (checked.value) {
            Image(painter = painterResource(id = R.drawable.check), contentDescription = null,
                modifier = Modifier.clickable { onUnchecked.invoke() })
        } else {
            Image(painter = painterResource(id = R.drawable.dryclean), contentDescription = null,
                modifier = Modifier.clickable { onCheck.invoke() })
        }
    }
}
