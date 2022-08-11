package com.pcsfg.pckit.chatkit.ui.messagecells

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.pcsfg.pckit.chatkit.common.extension.LinkifyText
import com.pcsfg.pckit.chatkit.common.view.DateTimeText
import com.pcsfg.pckit.chatkit.styles.messagecells.ImageCellStyle
import com.pcsfg.pckit.chatkit.styles.messagecells.ImageTextCellStyle
import com.pcsfg.pckit.chatkit.ui.chatview.GlobalCellStyle
import com.pcsfg.pckit.chatkit.ui.components.messagecells.AvatarView
import com.pcsfg.pckit.chatkit.ui.components.messagecells.MessageStatusText
import com.pcsfg.pckit.chatkit.ui.messagecells.common.SenderTextCell

@Composable
fun ImageTextCell(message: com.pcsfg.pckit.chatkit.model.ChatMessage, imageURL: String, navController: NavController)
{
    var cellStyle: ImageTextCellStyle =
        if(message.isSender == true) GlobalCellStyle.current.outgoingImageTextCellStyle else GlobalCellStyle.current.incomingImageTextCellStyle

    var imageCellStyle: ImageCellStyle =
        if(message.isSender == true) GlobalCellStyle.current.outgoingImageCellStyle else GlobalCellStyle.current.incomingImageCellStyle

    var cellTopPadding =
        if (message.isSameSender == true) cellStyle.cellSameSenderTopPadding else cellStyle.cellDifferentSenderTopPadding

    var columnAlignment =
        if (message.isSender == true) Alignment.End else Alignment.Start

    val messageAlpha = if (message.cachedMessage) GlobalCellStyle.current.cachedMessageAlpha else GlobalCellStyle.current.synchronizedMessageAlpha

    Column(horizontalAlignment = columnAlignment ,
        modifier = Modifier
            .padding(horizontal = cellStyle.cellPadding.dp)
            .padding(top = cellTopPadding.dp)
            .widthIn(cellStyle.cellMinWidth.dp, cellStyle.cellMaxWidth.dp)
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
                    modifier = Modifier.weight(1f)
                        .alpha(messageAlpha)
                ) {
                    Column() {
                        if (!message.isSender!! && message.isSameSender == false) {
                            SenderTextCell(
                                senderName = message.senderName,
                                textPadding = cellStyle.textStyle.textPadding,
                                senderNameTextColor = cellStyle.textStyle.senderNameTextColor,
                                fontWeight = cellStyle.textStyle.fontWeight
                            )
                        }
                        ImageView(
                            imageURL = imageURL,
                            navController = navController,
                            cellStyle = imageCellStyle,
                            dateTimeInImage = cellStyle.dateTimeInImage,
                            dateTime = null,
                            cachedMessage = message.cachedMessage
                        )

                        LinkifyText(
                            text = message.message.trimIndent(),
                            modifier = Modifier.padding(cellStyle.cellPadding.dp),
                            linkColor = Color.Blue,
                            linkEntire = false,
                            clickable = true,
                            onClickLink = null,
                            color = Color.Black,
                            fontWeight = cellStyle.textStyle.fontWeight
                        )
                    }
                }

                if(message.isSender == false) {
                    DateTimeText(
                        dateTimeText = message.timestamp,
                        dateTimeStyle = cellStyle.dateTimeStyle
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ImageTextCellPreview() {
//    ComposeTheme {
//        val navController = rememberNavController()
//        val screen1ViewModel = Screen1ViewModel()
//
//        Screen1(navController, screen1ViewModel)
//    }
    val navController = rememberNavController()
    val imageURL = "https://pcsfgmkthk.oss-accelerate.aliyuncs.com/UAT/room/RM00000041/285_IMG_9568.PNG"
//    ImageTextCell(displayText = "Image Text Cell Preview" , imageURL = imageURL , navController = navController)
}
