package centmoinshuitstudio.waiter3.MainActivity


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import centmoinshuitstudio.waiter3.ModelPreferencesManager
import com.example.waiter3.Models.Orders
import com.example.waiter3.Models.QuantityItem
import com.example.waiter3.AppBar
import com.example.waiter3.Orders.ImageButton
import com.example.waiter3.Menu.getSymbol
import com.example.waiter3.NewOrder.NoteCard
import java.text.DecimalFormat
import java.text.SimpleDateFormat


@Composable
fun OrderScreen(navController: NavController, orderId: String) {
    var orders = ModelPreferencesManager.get<Orders>("KEY_ORDERS")
    var index: Int = -1
    if (orders != null) {
        var rememberedSOrders = remember { mutableStateOf(orders) }
        orders.entries.forEachIndexed { orderIndex, order ->
            if (order.id == orderId){
                index = orderIndex
            }
        }
        Scaffold(
            topBar = {
                AppBar("Order", Icons.Default.ArrowBack, Icons.Outlined.Delete, { navController.navigateUp()}, { onDelete(index, rememberedSOrders, navController) })
            },
            bottomBar = {
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                Surface() {
                    Column(modifier = Modifier
                        .padding(16.dp)
                        .fillMaxSize()) {
                        if (index != -1 && rememberedSOrders.value != null) {
                            DateCheckCard(orders = rememberedSOrders, index, navController)
                            TableCard(orders = rememberedSOrders, index = index)
                            var currency = ModelPreferencesManager.get<String>("KEY_CURRENCY")
                            if (currency == null) {
                                currency = "euro"
                                ModelPreferencesManager.put(currency, "KEY_CURRENCY")
                            }
                            var symbol = getSymbol(currency)
                            var order = orders.entries[index]
                            order.quantityItems.forEachIndexed { index, quantityItem ->
                                QuantityItemCard(quantityItem = quantityItem, symbol)
                                if (quantityItem.notes != null) {
                                    for (note in quantityItem.notes!!) {
                                        NoteItemCard(note = note)
                                    }   
                                }
                            }
                            var total: Double = 0.0
                            for (quantityItem in order.quantityItems) {
                                var plus = quantityItem.quantity * quantityItem.item.price
                                total += plus
                            }
                            TotalCard(total = total, symbol)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NoteItemCard(note: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .padding(top = 8.dp, bottom = 8.dp, start = 0.dp, end = 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        ),
        shape = RoundedCornerShape(0)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            Text(text = "Note: " + note)
        }
    }
}

fun onDelete(index: Int, orders: MutableState<Orders>, navController: NavController) {
    if (index != -1) {
        orders.value.entries.removeAt(index)
        ModelPreferencesManager.put(orders.value, "KEY_ORDERS")
        navController.navigateUp()
    }
}

@Composable
fun QuantityItemCard(quantityItem: QuantityItem, symbol: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .padding(top = 8.dp, bottom = 8.dp, start = 0.dp, end = 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        ),
        shape = RoundedCornerShape(0)
    ) {
        QuantityItemContentBis(quantityItem, symbol)
    }
}

@Composable
fun QuantityItemContentBis(quantityItem: QuantityItem, symbol: String) {
    Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom, modifier = Modifier
        .fillMaxSize(),) {
        Column() {
            Text(
                text = quantityItem.item.name,
                fontWeight = FontWeight.Bold
            )
            Text(text = "Quant: " + quantityItem.quantity.toString())
            Text(text = "Price: " + quantityItem.item.price + " " + symbol)
            Text(text = "Subtotal:")
        }
        var subtotal = quantityItem.quantity * quantityItem.item.price
        val dec = DecimalFormat("#,###.00")
        Text(text = dec.format(subtotal) + " " + symbol)
    }
}
@Composable
fun TableCard(orders: MutableState<Orders>, index: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(30.dp)
            .padding(top = 8.dp, bottom = 4.dp, start = 0.dp, end = 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        ),
    ) {
        TableContent(orders = orders, index = index)
    }
    Divider()
}

@Composable
fun TotalCard(total: Double, symbol: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(30.dp)
            .padding(top = 8.dp, bottom = 4.dp, start = 0.dp, end = 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        ), shape = RoundedCornerShape(0),
    ) {
        TotalContent(total, symbol)
    }
}

@Composable
fun TotalContent(total: Double, symbol: String) {
    val dec = DecimalFormat("#,###.00")
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Total",
            fontWeight = FontWeight.Bold
        )
        Text(text = dec.format(total) + " " + symbol, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun DateCheckCard(orders: MutableState<Orders>, index: Int, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(30.dp)
            .padding(top = 8.dp, bottom = 4.dp, start = 0.dp, end = 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        ),
    ) {
        DateCheckContent(orders = orders, index, navController)
    }
    Divider()
}

@Composable
fun TableContent(orders: MutableState<Orders>, index: Int) {
    if (orders.value != null) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxSize()) {
            Text(text = "Table " + orders.value.entries[index].table.toString(), fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun DateCheckContent(orders: MutableState<Orders>, index: Int, navController: NavController) {
    val formatter = SimpleDateFormat("MMM dd yyyy hh:mm")
    var checked = remember { mutableStateOf(orders.value.entries[index].check) }
    if (orders.value != null) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = formatter.format(orders.value.entries[index].date), fontWeight = FontWeight.Bold)
            ImageButton(checked = checked, onCheck = {
                var unlockPro = ModelPreferencesManager.get<Boolean>("KEY_PRO")
                if (unlockPro == true) {
                    orders.value.entries[index].check = true
                    checked.value = true
                    ModelPreferencesManager.put(orders.value, "KEY_ORDERS")
                } else {
                    navController.navigate("subscription")
                }
            }, onUnchecked = {
                var unlockPro = ModelPreferencesManager.get<Boolean>("KEY_PRO")
                if (unlockPro == true) {
                    orders.value.entries[index].check = false
                    checked.value = false
                    ModelPreferencesManager.put(orders.value, "KEY_ORDERS")
                } else {
                    navController.navigate("subscription")
                }
            })
        }
    }
}