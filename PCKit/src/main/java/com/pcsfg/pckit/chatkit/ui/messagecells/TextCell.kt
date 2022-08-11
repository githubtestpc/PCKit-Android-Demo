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
import com.pcsfg.pckit.chatkit.common.extension.LinkifyText
import com.pcsfg.pckit.chatkit.common.view.DateTimeText
import com.pcsfg.pckit.chatkit.model.ChatMessage
import com.pcsfg.pckit.chatkit.styles.messagecells.TextCellStyle
import com.pcsfg.pckit.chatkit.ui.chatview.GlobalCellStyle
import com.pcsfg.pckit.chatkit.ui.components.messagecells.AvatarView
import com.pcsfg.pckit.chatkit.ui.components.messagecells.MessageStatusText
import com.pcsfg.pckit.chatkit.ui.messagecells.common.SenderTextCell

@Composable
fun TextCell(message: ChatMessage) {

    var cellStyle: TextCellStyle =
        if (message.isSender == true) GlobalCellStyle.current.outgoingTextCellStyle else GlobalCellStyle.current.incomingTextCellStyle

    var cellTopPadding =
        if (message.isSameSender == true) cellStyle.cellSameSenderTopPadding else cellStyle.cellDifferentSenderTopPadding

    var columnAlignment =
        if (message.isSender == true) Alignment.End else Alignment.Start

    val messageAlpha = if (message.cachedMessage) GlobalCellStyle.current.cachedMessageAlpha else GlobalCellStyle.current.synchronizedMessageAlpha

//    Column(horizontalAlignment = Alignment.Start , modifier = Modifier.background(Color.Transparent).padding(10.dp)) {
    Column(
        horizontalAlignment = columnAlignment, modifier = Modifier
            .padding(horizontal = cellStyle.cellPadding.dp)
            .padding(top = cellTopPadding.dp)
            .widthIn(cellStyle.cellMinWidth.dp, cellStyle.cellMaxWidth.dp)
    ) {
        Row() {
            if(message.isSameSender == false) {
                AvatarView(message = message)
            }
            else
            {
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
                )
                {
                    Column() {
                        if (!message.isSender!! && message.isSameSender == false) {
                            SenderTextCell(
                                senderName = message.senderName,
                                textPadding = cellStyle.textStyle.textPadding,
                                senderNameTextColor = cellStyle.textStyle.senderNameTextColor,
                                fontWeight = cellStyle.textStyle.fontWeight
                            )
                        }
                        /// Emoji For Older device
//                AndroidView(
//                    factory = { context ->
//                        AppCompatTextView(context).apply {
//                            text = message.message
//                            textSize = 14F
//                            textAlignment = View.TEXT_ALIGNMENT_CENTER
//                        }
//                    },
//                )
                        LinkifyText(
                            text = message.message.trimIndent(),
                            modifier = Modifier.padding(
                                start = cellStyle.textStyle.textPadding.dp,
                                top = cellStyle.textStyle.textPadding.dp,
                                bottom = cellStyle.textStyle.textPadding.dp,
                                end = cellStyle.textStyle.textPadding.dp,
                            ),
                            color = cellStyle.textStyle.textColor,
                            linkColor = Color.Blue,
                            linkEntire = false,
                            clickable = true,
                            onClickLink = null,
                            fontWeight = cellStyle.textStyle.fontWeight,
                        )
//                        Column(modifier = Modifier.align(Alignment.End))
//                        {
//                            DateTimeText(
//                                dateTimeText = message.timestamp,
//                                dateTimeStyle = cellStyle.dateTimeStyle
//                            )
//                        }
                    }
                }
//                Text(text = message.messageStatus.toString())

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
fun TextCellPreview() {
//    ComposeTheme {
//        val navController = rememberNavController()
//        val screen1ViewModel = Screen1ViewModel()
//
//        Screen1(navController, screen1ViewModel)
//    }
//    TextCell(displayText = "Text Cell Preview")
}