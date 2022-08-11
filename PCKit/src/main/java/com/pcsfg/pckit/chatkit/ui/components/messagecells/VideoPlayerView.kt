package com.pcsfg.pckit.chatkit.common.view

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.pcsfg.pckit.chatkit.common.extension.ComposableLifecycle

@Composable
fun VideoPlayerView(videoURL:String , navController: NavController)
{
    val context = LocalContext.current

    var playWhenReady by remember { mutableStateOf(true) }

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
//            setMediaItem(MediaItem.fromUri("https://pcsfgmkthk.oss-accelerate.aliyuncs.com/PRD/card/CR00009004/18381_TheRichLife（final压.mp4"))
            setMediaItem(MediaItem.fromUri(videoURL))
            repeatMode = ExoPlayer.REPEAT_MODE_ALL
            playWhenReady = playWhenReady
            prepare()
            play()


        }
    }

    val systemUiController = rememberSystemUiController()
    val configuration = LocalConfiguration.current

    when (configuration.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> systemUiController.isStatusBarVisible = false
        Configuration.ORIENTATION_PORTRAIT -> systemUiController.isStatusBarVisible = true
    }

    ComposableLifecycle { source, event ->
        if (event == Lifecycle.Event.ON_PAUSE) {
            exoPlayer.pause()
        }
    }

    //Component Layout
    Column() {

        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.background(Color.Black)
        ) {
            Spacer(Modifier.weight(1f).background(Color.Black))
            Icon(Icons.Rounded.Close,
                contentDescription = "Localized description",
                tint = Color.White,
                modifier = Modifier
                    .background(Color.Black)
                    .padding(top = 10.dp , end = 10.dp)
                    .clickable(
                        onClick = {
                            context
                                .findActivity()
                                ?.onBackPressed()
                        }
                    ))
        }


        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            factory = { context ->

                StyledPlayerView(context).apply {
                    player = exoPlayer
                    useController = true
                    this.setControllerOnFullScreenModeChangedListener {
                        if (it) {
                            println("Full Screen")
                            systemUiController.isStatusBarVisible = false
//                        this.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT
                            context.findActivity()?.requestedOrientation =
                                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

                        } else {
                            println("Not Full Screen")
                            systemUiController.isStatusBarVisible = true
                            context.findActivity()?.requestedOrientation =
                                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                        }
                    }
                }
            }
        )
        BackHandler() {
            systemUiController.isStatusBarVisible = true
            context.findActivity()?.requestedOrientation =
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            navController.navigateUp()
            exoPlayer.release()
        }
    }
}

fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}