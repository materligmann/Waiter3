package com.example.waiter3


import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import centmoinshuitstudio.waiter3.ModelPreferencesManager
import centmoinshuitstudio.waiter.Models.Menu
import centmoinshuitstudio.waiter.Models.Order
import centmoinshuitstudio.waiter.Models.Orders
import centmoinshuitstudio.waiter.Models.QuantityItem
import centmoinshuitstudio.waiter.Models.QuantitySection
import centmoinshuitstudio.waiter.Models.QuantityTable
import java.util.Date
import java.util.UUID


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewOrderScreen(navController: NavHostController) {
    var menu = ModelPreferencesManager.get<Menu>("KEY_MENU")
    var canSave = remember { mutableStateOf(false) }
    val quantityTable = QuantityTable(ArrayList<QuantitySection>())
    var tableNumber = remember { mutableStateOf("") }
    var rememberedQuantityTable = remember { mutableStateOf(quantityTable) }
    if (menu != null) {
        for (section in menu.menuSections) {
            var quantitySection = QuantitySection(ArrayList<QuantityItem>(), section.title)
            for (item in section.items) {
                quantitySection.entries.add(QuantityItem(item, 0,null))
            }
            quantityTable.sections.add(quantitySection)
        }
        rememberedQuantityTable.value = quantityTable
    }
    val tableDialogOpen = remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            AppBar("Order", Icons.Default.ArrowBack, null, { navController.navigateUp()}, { })
        },
        bottomBar = {
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            Surface() {
                if (tableDialogOpen.value) {
                    AlertDialogBasic(
                        title = "Enter a table number",
                        firstText = tableNumber,
                        firstPlaceholder = "Number",
                        firstKeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shouldOpen = tableDialogOpen,
                        onConfirm = {
                            var orders = ModelPreferencesManager.get<Orders>("KEY_ORDERS")

                            val myUuid = UUID.randomUUID()
                            val myUuidAsString = myUuid.toString()
                            val current = Date()
                            var canSave = false

                            if (orders != null) {
                                var order = Order(myUuidAsString, current, ArrayList<QuantityItem>(), tableNumber.value.toInt(), false)
                                for (section in rememberedQuantityTable.value.sections) {
                                    for (item in section.entries) {
                                        if (item.quantity != 0) {
                                            order.quantityItems.add(item)
                                            canSave = true
                                        }
                                    }
                                }
                                if (canSave) {
                                    orders.entries.add(order)
                                    ModelPreferencesManager.put(orders, "KEY_ORDERS")
                                    navController.navigateUp()
                                }
                            } else {
                                orders = Orders(java.util.ArrayList<Order>())
                                var order = Order(myUuidAsString, current, ArrayList<QuantityItem>(), tableNumber.value.toInt(), false)
                                for (section in rememberedQuantityTable.value.sections) {
                                    for (item in section.entries) {
                                        if (item.quantity != 0) {
                                            order.quantityItems.add(item)
                                            canSave = true
                                        }
                                    }
                                }
                                if (canSave) {
                                    orders.entries.add(order)
                                    ModelPreferencesManager.put(orders, "KEY_ORDERS")
                                    navController.navigateUp()
                                }
                            }
                        },
                        secondText = null,
                        secondPlaceholder = null,
                        secondKeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                    )
                }
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    rememberedQuantityTable.value.sections.forEachIndexed { sectionIndex, quantitySection ->
                        Text(text = quantitySection.title)
                        quantitySection.entries.forEachIndexed { itemIndex, quantityItem ->
                            var quantity = remember { mutableStateOf(quantityItem.quantity) }
                            QuantityItemCard(
                                quantityItem = rememberedQuantityTable.value.sections[sectionIndex].entries[itemIndex],
                                quantity = quantity,
                                onMinus = {
                                    if (quantity.value >= 1) {
                                        --rememberedQuantityTable.value.sections[sectionIndex].entries[itemIndex].quantity
                                        --quantity.value
                                        check(canSave, rememberedQuantityTable.value)
                                    }
                                },
                                onPlus = {
                                    ++rememberedQuantityTable.value.sections[sectionIndex].entries[itemIndex].quantity
                                    ++quantity.value
                                    check(canSave, rememberedQuantityTable.value)
                                }) {

                            }
                        }
                        Spacer(modifier = Modifier.height(30.dp))
                    }
                    if (canSave.value) {
                        Button(
                            onClick = {
                                tableDialogOpen.value = true
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                        ) {
                            Text(text = "Save", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Button(onClick = { /*TODO*/ }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20), colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)) {
                            Text(text = "Save", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

fun check(canSave: MutableState<Boolean>, quantityTable: QuantityTable) {
    var shouldSave = false
    for (section in quantityTable.sections) {
        for (quantityItem in section.entries) {
            if (quantityItem.quantity != 0) {
                shouldSave = true
            }
        }
    }
    canSave.value = shouldSave
}

@Composable
fun QuantityItemCard(quantityItem: QuantityItem, quantity: MutableState<Int>, onMinus: () -> Unit, onPlus: () -> Unit, clickAction: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(top = 8.dp, bottom = 4.dp, start = 0.dp, end = 0.dp)
            .clickable { clickAction.invoke() },
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        ),
        border = BorderStroke(1.dp, Color.Black),
    ) {
        QuantityItemContent(quantityItem, quantity, onMinus, onPlus)
    }
}

@Composable
fun QuantityItemContent(quantityItem: QuantityItem, quantity: MutableState<Int>, onMinus: () -> Unit, onPlus: () -> Unit){
    Row(
        Modifier
            .fillMaxSize()
            .padding(10.dp, 10.dp, 10.dp, 10.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(text = quantityItem.item.name, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.weight(1f))
        OutlinedButton(
            onClick = onMinus,
            shape = RoundedCornerShape(20),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.LightGray,
                contentColor = Color.Black
            ),
            contentPadding = PaddingValues(0.dp)
        ) {
            Text(text = "-", color = Color.Black)
        }
        OutlinedButton(
            onClick = onPlus,
            shape = RoundedCornerShape(20),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.LightGray,
                contentColor = Color.Black
            ),
            contentPadding = PaddingValues(0.dp)
        ) {
            Text(text = "+", color = Color.Black)
        }
        Spacer(modifier = Modifier.width(20.dp))
        Text(text = quantity.value.toString(), fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}