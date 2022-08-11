package com.pcsfg.pckit.chatkit.ui.messagecells

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.pcsfg.pckit.R
import com.pcsfg.pckit.chatkit.common.view.DateTimeText
import com.pcsfg.pckit.chatkit.styles.messagecells.VideoCellStyle
import com.pcsfg.pckit.chatkit.ui.chatview.GlobalCellStyle
import com.pcsfg.pckit.chatkit.ui.chatview.NavRoutes
import com.pcsfg.pckit.chatkit.ui.components.messagecells.AvatarView
import com.pcsfg.pckit.chatkit.ui.components.messagecells.MessageStatusText
import com.pcsfg.pckit.chatkit.ui.messagecells.common.SenderTextCell
import java.net.URLEncoder

@Composable
fun VideoCell(message: com.pcsfg.pckit.chatkit.model.ChatMessage, imageURL: String, videoURL:String, navController: NavController)
{
    var cellStyle: VideoCellStyle =
        if(message.isSender == true) GlobalCellStyle.current.outgoingVideoCellStyle else GlobalCellStyle.current.incomingVideoCellStyle

    var cellTopPadding =
        if (message.isSameSender == true) cellStyle.cellSameSenderTopPadding else cellStyle.cellDifferentSenderTopPadding

    var columnAlignment =
        if (message.isSender == true) Alignment.End else Alignment.Start

    val messageAlpha = if (message.cachedMessage) GlobalCellStyle.current.cachedMessageAlpha else GlobalCellStyle.current.synchronizedMessageAlpha

    Column(horizontalAlignment = columnAlignment ,
        modifier = Modifier
            .padding(horizontal = cellStyle.cellPadding.dp)
            .padding(top = cellTopPadding.dp)
            .widthIn(100.dp, 300.dp)
    )
    {
        Row() {

            if (message.isSameSender == false) {
                AvatarView(message = message)
            } else {
                Spacer(modifier = Modifier.width(35.dp))
            }

            Row(verticalAlignment = Alignment.Bottom)
            {

                if(message.isSender == true) {
                    Column(horizontalAlignment = Alignment.End) {
                        if (!message.cachedMessage) {
                            MessageStatusText(
                                countRead = message.countread,
                                statusTextStyle = cellStyle.messageStatusTextStyle
                            )
                        }
                        DateTimeText(
                            dateTimeText = message.timestamp,
                            dateTimeStyle = cellStyle.dateTimeStyle
                        )
                    }
                }

                Card(
                    backgroundColor = cellStyle.cellBackgroundColor,
                    elevation = cellStyle.cellElevationRadius.dp,
                    shape = RoundedCornerShape(cellStyle.cellCornerRadius.dp),
                    border = BorderStroke(cellStyle.cellBorderWidth.dp, cellStyle.cellBorderColor),
                    modifier = Modifier.alpha(messageAlpha)
//                    modifier = Modifier.weight(1f)
                ) {
                    Column {
                        if (!message.isSender!! && message.isSameSender == false) {
                            SenderTextCell(
                                senderName = message.senderName,
                                textPadding = cellStyle.textStyle.textPadding,
                                senderNameTextColor = cellStyle.textStyle.senderNameTextColor,
                                fontWeight = cellStyle.textStyle.fontWeight
                            )
                        }

                        VideoView(
                            imageURL = imageURL,
                            videoURL = videoURL,
                            navController = navController,
                            cellStyle = cellStyle,
                            dateTime = message.timestamp
                        )
                    }
                }

                if (message.isSender == false) {
                    DateTimeText(
                        dateTimeText = message.timestamp,
                        dateTimeStyle = cellStyle.dateTimeStyle
                    )
                }
            }
        }
    }

}

@Composable
fun VideoView(imageURL:String,videoURL: String, navController:NavController, cellStyle: VideoCellStyle, dateTime: String?){

    Box(contentAlignment = Alignment.BottomEnd) {
        Box(contentAlignment = Alignment.Center) {
            AsyncImage(
//                    model = "https://picsum.photos/250/400",
//        model = "https://pcsfgmkthk.oss-accelerate.aliyuncs.com/UAT/room/RM00000041/285_IMG_9568.PNG",
                model = imageURL,
                contentDescription = null,
                contentScale = ContentScale.None,
                modifier = Modifier
                    .padding(cellStyle.imagePadding.dp)
                    .clip(RoundedCornerShape(cellStyle.cellCornerRadius.dp))
//                        .width(300.dp)
                    .fillMaxHeight()
                    .clickable(
                        onClick = {
                            videoOnClick(
                                videoURL = videoURL,
                                navController = navController
                            )
                        }
                    )
            )

            Image(
                painter = painterResource(id = R.drawable.circled_play),
                contentDescription = null,
                modifier = Modifier.size(50.dp)
            )

        }

//        if (dateTime != null) {
//            DateTimeText(dateTimeText = dateTime,dateTimeStyle = cellStyle.dateTimeStyle)
//        }
    }
}

fun videoOnClick(videoURL: String, navController: NavController)
{
    Log.d(
        "Document Cell",
        "Cell Click"
    )

    val encodedUrl = URLEncoder.encode(videoURL , "UTF-8")
//    navController.navigate(NavRoutes.Image.createRoute(imageURL = encodedUrl))
    navController.navigate(NavRoutes.Video.createRoute(videoURL = encodedUrl))
//   navController.navigate("Video")


}

@Preview(showBackground = true)
@Composable
fun VideoCellPreview() {
//    ComposeTheme {
//        val navController = rememberNavController()
//        val screen1ViewModel = Screen1ViewModel()
//
//        Screen1(navController, screen1ViewModel)
//    }
    val navController = rememberNavController()
    val imageURL = "https://pcsfgmkthk.oss-accelerate.aliyuncs.com/UAT/room/RM00000041/285_IMG_9568.PNG"
//    VideoCell(displayText = "Image Cell Preview", imageURL = imageURL , navController =  navController)
}