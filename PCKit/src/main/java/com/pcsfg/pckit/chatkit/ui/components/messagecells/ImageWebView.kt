package com.pcsfg.pckit.chatkit.common.view

import android.graphics.Bitmap
import android.util.Log
import android.webkit.WebView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.google.accompanist.web.AccompanistWebViewClient
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewNavigator
import com.google.accompanist.web.rememberWebViewState

@Composable
fun ImageWebView(imageURL:String)
{

//    val state = rememberWebViewState("https://pcsfgmkthk.oss-accelerate.aliyuncs.com/UAT/room/RM00000039/145_CICN300599221_895591.pdf")
//    val state = rememberWebViewState(url = "https://picsum.photos/200/300")
//    val state = rememberWebViewState(url = "https://pcsfgmkthk.oss-accelerate.aliyuncs.com/PRD/room/RM00000039/135_image.jpg")
//    val state = rememberWebViewState(url = "https://images8.alphacoders.com/712/712496.jpg")
//    val state = rememberWebViewState(url = "https://browsecat.net/sites/default/files/4k-vertical-wallpapers-98596-690003-9958206.png")
//    val state = rememberWebViewState(url = "file:///android_asset/index.html?https://www.gjtool.cn/pdfh5/git.pdf")
//    val state = rememberWebViewState(url = "file:///android_asset/index.html?https://pcsfgmkthk.oss-accelerate.aliyuncs.com/UAT/room/RM00000039/145_CICN300599221_895591.pdf")
//    val state = rememberWebViewState(url = "file:///android_asset/index.html?https://www.td.gov.hk/filemanager/en/content_4833/name_list_of_group_1_pdi_contact_list_rev_mar_2022.pdf")
//    webView?.loadUrl("file:///android_asset/index.html?https://www.gjtool.cn/pdfh5/git.pdf");
//    val state = rememberWebViewState("https://www.google.com")
//    val state = rememberWebViewState("https://www.pcsfg.com")
//    val state = rememberWebViewState("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4")
    val state = rememberWebViewState(url = imageURL)
    val navigator = rememberWebViewNavigator()

    val webClient = remember {
        object : AccompanistWebViewClient() {
            override fun onPageStarted(
                view: WebView?,
                url: String?,
                favicon: Bitmap?
            ) {
                super.onPageStarted(view, url, favicon)
                Log.d("Accompanist WebView", "Page started loading for $url")
            }
        }
    }

    WebView(
        state,
//                modifier = Modifier.wei
        navigator = navigator,
        onCreated = { webView ->
            webView.settings.javaScriptEnabled = true
            webView.settings.allowFileAccess = true
            webView.settings.builtInZoomControls = true
            webView.settings.setSupportZoom(true)
            webView.settings.displayZoomControls = true
            webView.settings.loadWithOverviewMode = true
            webView.settings.useWideViewPort = true
            webView.settings.setSupportMultipleWindows(true)

        },
        client = webClient
    )
}