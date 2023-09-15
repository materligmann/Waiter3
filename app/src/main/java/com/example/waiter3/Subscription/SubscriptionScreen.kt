package com.example.waiter3.Subscription


import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import centmoinshuitstudio.waiter3.R
import com.example.waiter3.AppBar


@Composable
fun SubscriptionScreen(navController: NavController) {
    var priceText = rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current
    var viewModel = SubscriptionViewModel(context)
    viewModel.getPrice { price ->
        priceText.value = price
    }
    var arguments = arrayListOf<Argument>(
        Argument(
            R.drawable.infinity,
            "Unlimited Orders",
            "Unlimited number of orders at the same time.\nBasic version is limited to 5."
        ),
        Argument(
            R.drawable.notes,
            "Add Instructions",
            "With Waiter Pro you have the possibility to add instructions to an order. e.g. Well done or medium rare"
        ),
        Argument(
            R.drawable.checkplain,
            "Check your order",
            "With Waiter Pro you can checkmark your orders when the customer did pay, or when you are done with it."
        )
    )
    Scaffold(
        topBar = {
            AppBar("Order", Icons.Default.Close, null, { navController.navigateUp()}, { })
        },
        bottomBar = {
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            Surface() {
                Column(modifier = Modifier.padding(16.dp)) {
                    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(painter = painterResource(id = R.drawable.logowaiter), contentDescription = "")
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(text = "Waiter Pro", fontWeight = FontWeight.Bold, fontSize = 22.sp)
                    }
                    Spacer(modifier = Modifier.height(30.dp))
                    Column(modifier = Modifier.fillMaxSize()) {
                        for (argument in arguments) {
                            ArgumentCard(argument = argument)
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = {
                                viewModel.buy {
                                    navController.navigateUp()
                                }
                            },
                            shape = RoundedCornerShape(20),
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                        ) {
                            Text(text = "Subscribe for " + priceText.value + " / Month", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

data class Argument(val iconId: Int, val title: String, val argument: String)

@Composable
fun ArgumentContent(argument: Argument) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = painterResource(id = argument.iconId),
            contentDescription = null,
            modifier = Modifier.height(40.dp)
        )
        Spacer(modifier = Modifier.width(20.dp))
        Column {
            Text(text = argument.title, fontWeight = FontWeight.Bold)
            Text(text = argument.argument)
        }
    }
}

@Composable
fun ArgumentCard(argument: Argument) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(top = 8.dp, bottom = 4.dp, start = 16.dp, end = 16.dp)
            .wrapContentHeight(Alignment.Top),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        )
    ) {
        ArgumentContent(argument = argument)
    }
}