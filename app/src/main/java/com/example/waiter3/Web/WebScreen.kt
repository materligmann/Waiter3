package com.example.waiter3.Web

import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import centmoinshuitstudio.waiter3.R
import com.example.waiter3.AppBar
import com.example.waiter3.Subscription.Argument
import com.example.waiter3.Subscription.ArgumentCard
import com.example.waiter3.Subscription.SubscriptionViewModel

@Composable
fun WebScreen(navController: NavController, url: String) {
    Scaffold(
        topBar = {
            AppBar("", Icons.Default.ArrowBack, null, { navController.navigateUp()}, { })
        },
        bottomBar = {
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            Surface() {
                loadWebUrl(passedUrl = url)
            }
        }
    }
}
@Composable
fun loadWebUrl(passedUrl: String) {
    var context = LocalContext.current
    AndroidView(
        update = {
            it.loadUrl(passedUrl)
        },
        factory = {
        WebView(context).apply {
            webViewClient = WebViewClient()
            webViewClient = object : WebViewClient() {

                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    if (url != null) {
                        if(url!!.contains(passedUrl)){
                            return false
                        }
                    }
                    return true;
                }
            }
            loadUrl(passedUrl)
        }
    })
}

private fun WebViewClient.shouldOverrideUrlLoading() {
    TODO("Not yet implemented")
}
