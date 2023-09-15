package com.example.waiter3.Currency

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import centmoinshuitstudio.waiter3.ModelPreferencesManager
import com.example.waiter3.Models.CurrencyItem
import centmoinshuitstudio.waiter3.R
import com.example.waiter3.AppBar


@Composable
fun CurrencyScreen(navController: NavController) {
    var selectedCurrency = ModelPreferencesManager.get<String>("KEY_CURRENCY")
    if (selectedCurrency == null) {
        selectedCurrency = "euro"
        ModelPreferencesManager.put(selectedCurrency, "KEY_CURRENCY")
    }
    if (selectedCurrency != null) {
        var rememberedSelectedCurrency = remember { mutableStateOf(selectedCurrency) }
        var currencies = arrayListOf<CurrencyItem>(
            CurrencyItem("euro", R.drawable.euro, rememberedSelectedCurrency.value == "euro"),
            CurrencyItem("pound", R.drawable.pound, rememberedSelectedCurrency.value == "pound"),
            CurrencyItem("dollar", R.drawable.coin, rememberedSelectedCurrency.value == "dollar"),
            CurrencyItem("yen", R.drawable.yen, rememberedSelectedCurrency.value == "yen"),
        )
        Scaffold(
            topBar = {
                AppBar("Currency", Icons.Default.ArrowBack, null, { navController.navigateUp()}, { })
            },
            bottomBar = {
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                Surface() {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxSize()
                    ) {
                        for (currency in currencies) {
                            CurrencyCard(currency = currency,
                                action = {
                                    selectedCurrency = currency.string
                                    rememberedSelectedCurrency.value = currency.string
                                    ModelPreferencesManager.put(rememberedSelectedCurrency.value, "KEY_CURRENCY")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CurrencyCard(currency: CurrencyItem, action: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .padding(top = 8.dp, bottom = 8.dp, start = 0.dp, end = 0.dp)
            .clickable {
                action.invoke()
            },
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        ),
        shape = RoundedCornerShape(0)
    ) {
        CurrencyContent(currency = currency)
    }
}

@Composable
fun CurrencyContent(currency: CurrencyItem) {
    Row(modifier = Modifier.fillMaxSize()) {
        Image(painter = painterResource(id = currency.icon), contentDescription = "")
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = currency.string.capitalize())
        Spacer(Modifier.weight(1f))
        if (currency.checked) {
            Image(painter = painterResource(id = R.drawable.check), contentDescription = "")
        }
    }
}