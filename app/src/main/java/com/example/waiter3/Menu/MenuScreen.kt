package com.example.waiter3.Menu

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import com.example.waiter3.Models.Item
import com.example.waiter3.Models.Menu
import com.example.waiter3.Models.MenuSection
import java.text.DecimalFormat
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.res.painterResource
import centmoinshuitstudio.waiter3.R
import com.example.waiter3.AppBar
import com.example.waiter3.Orders.prepareBottomMenu


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun MenuScreen(navController: NavHostController){
    val bottomMenuItemsList = prepareBottomMenu()
    var menu = ModelPreferencesManager.get<Menu>("KEY_MENU")
    val rememberedMenu = remember { mutableStateOf(menu) }

    var sectionText  = remember { mutableStateOf("") }
    var itemNameText = remember { mutableStateOf("") }
    var itemPriceText = remember { mutableStateOf("") }


    val dialogSectionOpen = remember { mutableStateOf(false) }
    val dialogItemOpen = remember { mutableStateOf(false) }

    var selectedSectionIndex = -1
    var selectedItemIndex = -1

    val state = androidx.compose.material3.rememberModalBottomSheetState()
    var isSheetOpen = remember { mutableStateOf(false) }


    Scaffold(
        topBar = {
            AppBar("Menu", Icons.Default.ArrowBack, null, { navController.navigateUp()}, { })
        },
        bottomBar = {
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            Surface {
                if (isSheetOpen.value) {
                    ModalBottomSheet(
                        sheetState = state,
                        onDismissRequest = {
                            isSheetOpen.value = false
                        }, containerColor = Color.White
                    ) {
                        BottomSheetCard(title = "Delete", color = Red) {
                            if (rememberedMenu.value != null) {
                                Log.d("mytag", selectedSectionIndex.toString())
                                Log.d("mytag", selectedItemIndex.toString())
                                var section = rememberedMenu.value!!.menuSections[selectedSectionIndex]
                                var menu = rememberedMenu.value
                                if (menu != null) {
                                    menu.menuSections[selectedSectionIndex].items.removeAt(selectedItemIndex)
                                    rememberedMenu.value = menu
                                }
                                if (section.items.isEmpty()) {
                                    rememberedMenu.value!!.menuSections.removeAt(selectedSectionIndex)
                                }
                                ModelPreferencesManager.put(rememberedMenu.value, "KEY_MENU")
                            }
                            isSheetOpen.value = false
                        }
                    }
                }
                if (dialogItemOpen.value) {
                    AlertDialogBasic(
                        title = "Enter an item",
                        firstText = itemNameText ,
                        firstPlaceholder = "Name",
                        firstKeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        shouldOpen = dialogItemOpen,
                        onConfirm = {
                            if (!itemNameText.value.isEmpty()) {
                                val price = itemPriceText.value.toDouble()
                                val item = Item(itemNameText.value, price)
                                if (menu != null) {
                                    if (selectedSectionIndex != null) {
                                        menu.menuSections[selectedSectionIndex].items.add(item)
                                        ModelPreferencesManager.put(menu, "KEY_MENU")
                                        rememberedMenu.value = menu
                                    }
                                }
                                itemPriceText.value = ""
                                itemNameText.value = ""
                            }
                            dialogItemOpen.value = false
                        },
                        secondText = itemPriceText,
                        secondPlaceholder = "Price",
                        secondKeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
                if (dialogSectionOpen.value) {
                    AlertDialogBasic(
                        title = "Enter a section title",
                        firstText = sectionText,
                        firstPlaceholder = "Title",
                        firstKeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        shouldOpen = dialogSectionOpen,
                        onConfirm = {
                            if (menu == null) {
                                var menu = Menu(
                                    menuSections = arrayListOf(
                                        MenuSection(
                                            sectionText.value,
                                            ArrayList<Item>()
                                        )
                                    )
                                )
                                ModelPreferencesManager.put(menu, "KEY_MENU")
                                rememberedMenu.value = menu
                            } else {
                                menu.menuSections.add(
                                    MenuSection(
                                        sectionText.value,
                                        ArrayList<Item>()
                                    )
                                )
                                ModelPreferencesManager.put(menu, "KEY_MENU")
                                rememberedMenu.value = menu
                            }
                            dialogSectionOpen.value = false
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
                        .verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Top) {
                    if (rememberedMenu.value != null) {
                        rememberedMenu.value!!.menuSections.forEachIndexed { sectionIndex, section ->
                            Text(text = section.title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Spacer(modifier = Modifier.height(30.dp))
                            section.items.forEachIndexed { itemIndex, item ->
                                ItemCard(item = item) {
                                    selectedSectionIndex = sectionIndex
                                    selectedItemIndex = itemIndex
                                    isSheetOpen.value = true
                                }
                            }
                            for (item in section.items) {

                            }
                            OutlinedButton(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    selectedSectionIndex = sectionIndex
                                    dialogItemOpen.value = true
                                },
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = Color.White,
                                    contentColor = Color.Black
                                ),
                                shape = RoundedCornerShape(20)
                            ) {
                                Text(text = "Add Item", color = Color.Black)
                            }
                            Spacer(modifier = Modifier.height(30.dp))
                        }
                    }
                    Button(onClick = { dialogSectionOpen.value = true}, modifier = Modifier.fillMaxWidth() ,colors = ButtonDefaults.buttonColors(containerColor = Color.Black), shape = RoundedCornerShape(20)) {
                        Text(text = "Add Section")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertDialogBasic(title: String,
                     firstText: MutableState<String>,
                     firstPlaceholder: String,
                     firstKeyboardOptions: KeyboardOptions,
                     shouldOpen: MutableState<Boolean>,
                     onConfirm: () -> Unit,
                     secondText: MutableState<String>?,
                     secondPlaceholder: String?,
                     secondKeyboardOptions: KeyboardOptions) {
    AlertDialog(
        title = {
            Text(text = title)
        },
        text = {
            Column(verticalArrangement = Arrangement.Center) {
                TextField(
                    value = firstText.value,
                    keyboardOptions = firstKeyboardOptions,
                    placeholder = { Text(text = firstPlaceholder) },
                    onValueChange = {
                        firstText.value = it
                    },
                    colors = androidx.compose.material3.TextFieldDefaults.textFieldColors(containerColor = Color.White, focusedIndicatorColor = Color.Black, cursorColor = Color.Black)
                )
                if (secondText != null && secondPlaceholder != null) {
                    TextField(
                        value = secondText.value,
                        keyboardOptions = secondKeyboardOptions,
                        placeholder = { Text(text = secondPlaceholder) },
                        onValueChange = {
                            secondText.value = it
                        },
                        colors = androidx.compose.material3.TextFieldDefaults.textFieldColors(containerColor = Color.White, focusedIndicatorColor = Color.Black, cursorColor = Color.Black)
                    )
                }
            }
        },
        onDismissRequest = {
            shouldOpen.value = false
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    shouldOpen.value = false
                }
            ) {
                Text("Dismiss")
            }
        }
    )
}

@Composable
fun BottomSheetCard(title: String, color: Color, clickAction: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .padding(top = 8.dp, bottom = 4.dp, start = 0.dp, end = 0.dp)
            .clickable { clickAction.invoke() },
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        )
    ) {
        BottomSheetItemContent(title, color)
    }
}

@Composable
fun BottomSheetItemContent(title: String, color: Color){
    val dec = DecimalFormat("#,###.00")
    Row(
        Modifier
            .fillMaxSize()
            .padding(10.dp, 0.dp, 10.dp, 0.dp), horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = painterResource(id = R.drawable.bin),
            contentDescription = null,
            modifier = Modifier.height(40.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = title, fontWeight = FontWeight.Bold, color = color)
    }
}
@Composable
fun ItemCard(item: Item, clickAction: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .padding(top = 8.dp, bottom = 4.dp, start = 0.dp, end = 0.dp)
            .clickable { clickAction.invoke() },
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        ),
        border = BorderStroke(1.dp, Color.Black),
    ) {
        ItemContent(item)
    }
}

@Composable
fun ItemContent(item: Item){
    val dec = DecimalFormat("#,###.00")
    var currency = ModelPreferencesManager.get<String>("KEY_CURRENCY")
    if (currency == null) {
        currency = "euro"
        ModelPreferencesManager.put(currency, "KEY_CURRENCY")
    }
    var symbol = getSymbol(currency)
    Row(
        Modifier
            .fillMaxSize()
            .padding(10.dp, 0.dp, 10.dp, 0.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(text = item.name)
        Text(text = dec.format(item.price) + " "+ symbol)
    }
}

fun getSymbol(currency: String): String {
    var symbol = ""
    when (currency) {
        "euro" -> symbol = "€"
        "dollar"-> symbol = "$"
        "pound" -> symbol = "£"
        "yen" -> symbol = "¥"
    }
    return symbol
}