package com.emperor.myrechargeng


import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.emperor.myrechargeng.ui.theme.MyRechargeNgTheme
import com.emperor.myrechargeng.utils.LoadBrowser
import com.emperor.myrechargeng.utils.isInternetAvailable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private var webView: WebView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition{true}
        CoroutineScope(Dispatchers.Main).launch {
            delay(200L)
            splashScreen.setKeepOnScreenCondition{false}
        }
        setContent {
            MyRechargeNgTheme {
                Scaffold(
                   modifier = Modifier.fillMaxSize(),) { innerPadding ->
                    Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                        val devUrl: String = "https://my-recharge-web-services-git-feature-mobil-44ddd0-myrechargedev.vercel.app"
                        val prodUrl: String = "https://myrecharge.ng"
                        InternetAwareWebView(url = devUrl,
                            onWebViewCreated = { webViewInstance ->
                                webView = webViewInstance // Capture WebView instance for back navigation
                            })
                        //LoadBrowser(url = "https://myrecharge.ng")
                    }
                }
            }
        }
    }
    override fun onBackPressed() {
        webView?.let {
            if (it.canGoBack()) {
                it.goBack() // Go back in WebView history
            } else {
                super.onBackPressed() // Exit the app
            }
        } ?: super.onBackPressed()
    }
}

@Composable
fun DisplayWebView(url: String, onWebViewCreated: (WebView) -> Unit) {
    val context = LocalContext.current
    val webViewState = remember { mutableStateOf<WebView?>(null) }

    AndroidView(modifier = Modifier.fillMaxSize(), factory = { ctx ->
        WebView(ctx).apply {
            onWebViewCreated(this)
            webViewClient = WebViewClient()
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                loadWithOverviewMode = true
                useWideViewPort = true
                cacheMode = WebSettings.LOAD_NO_CACHE // Prevent cached content issues
                allowFileAccess = true // Allow access to local resources
                javaScriptCanOpenWindowsAutomatically = true // Handle JavaScript popups
                setSupportZoom(true) // Enable zoom controls
                builtInZoomControls = true // Add built-in zoom controls
                displayZoomControls = false // Hide default zoom buttons
            }
            clearCache(true)
            clearHistory()
            loadUrl(url)
        }
    }, update = { webView ->
        webViewState.value = webView
        webView.loadUrl(url)
    })
}

// Composable function for the WebView with internet check
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InternetAwareWebView(url: String, onWebViewCreated: (WebView) -> Unit) {
    val context = LocalContext.current
    var isInternetAvailable by remember { mutableStateOf(isInternetAvailable(context)) }
    var showDialog by remember { mutableStateOf(false) } // State to control BottomSheet visibility

    if (isInternetAvailable) {
        // Show the WebView when internet is available
        DisplayWebView(url = url, onWebViewCreated = onWebViewCreated)
    } else {
        // Show BottomSheet dialog when internet is not available
        showDialog = true
    }
    // BottomSheet Dialog
    if (showDialog) {
        ModalBottomSheet(onDismissRequest = { showDialog = false }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    text = "No Internet Connection",
                    style = MaterialTheme.typography.bodyLarge,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    text = "Please turn on your internet connection and try again.",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp), onClick = {
                    // Check internet again and dismiss dialog if connected
                    isInternetAvailable = isInternetAvailable(context)
                    if (isInternetAvailable) {
                        showDialog = false
                    }
                }) {
                    Text(text = "Retry")
                }
            }
        }
    }
}

@Composable
fun DialogWise(){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            text = "No Internet Connection",
            style = MaterialTheme.typography.bodyLarge

        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            text = "Please turn on your internet connection and try again.",
            style = MaterialTheme.typography.bodyMedium,

        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp), onClick = {
            // Check internet again and dismiss dialog if connected
        }) {
            Text(text = "Retry")
        }
    }
}



@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyRechargeNgTheme {
        Box(modifier = Modifier.fillMaxSize()) { // Ensure the content fills the available space
            DialogWise()
        }
    }
}