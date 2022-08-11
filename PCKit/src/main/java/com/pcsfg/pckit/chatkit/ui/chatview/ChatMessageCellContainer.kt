package com.pcsfg.pckit.chatkit.ui.chatview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.pcsfg.pckit.chatkit.model.ChatMessage
import com.pcsfg.pckit.chatkit.model.ChatMessageKind
import com.pcsfg.pckit.chatkit.ui.messagecells.*
import com.pcsfg.pckit.chatkit.ui.messagecells.common.DateTimeCell
import java.text.SimpleDateFormat

@Composable
fun ChatMessageCellContainer (message: ChatMessage, navController:NavController)
{
    val rowArrangement =
    if(message.messageKind == ChatMessageKind.DateTime || message.messageKind == ChatMessageKind.SystemMessage) Arrangement.Center else {
        if (!message.isSender!!) Arrangement.Start else Arrangement.End
    }

//    println ("GLOBAL CELL STYLE Border Width : ${GlobalCellStyle.current.incomingTextCellStyle.cellBorderWidth}")
//    println ("Global Cell Sender Color : ${GlobalCellStyle.current.incomingTextCellStyle.textStyle.senderNameTextColor}")
    Row(modifier = Modifier.fillMaxWidth() , horizontalArrangement = rowArrangement) {
//        if (message.isSender) {
//            Spacer(modifier = Modifier.size(80.dp))
//        }

        when (message.messageKind) {
            ChatMessageKind.Text -> {
                TextCell(message = message)
            }

            ChatMessageKind.ImageText -> {
                message.files?.get(0)?.let {
                    ImageTextCell(
                        message = message,
                        imageURL = it.url,
                        navController = navController
                    )
                }
            }

            ChatMessageKind.Image -> {
                message.files?.get(0)?.let {
                    ImageCell(
                        message = message,
                        imageURL = it.url,
                        navController = navController
                    )
                }
            }

            ChatMessageKind.Document -> {
                message.files?.get(0)?.let { DocumentCell(message = message, documentURL = it.url, documentName = it.name) }
            }

            ChatMessageKind.Video -> {
                message.files?.get(0)?.let {
                    VideoCell(
                        message = message,
                        imageURL = it.url,
                        videoURL = it.url,
                        navController = navController
                    )
                }
            }

            ChatMessageKind.DateTime -> {
                val pattern = "yyyy-MM-dd"
                val simpleDateFormat = SimpleDateFormat(pattern)
//
//                val dayBefore = Date(message.date.time - 24 * 60 * 60 * 1000)
//                val dayAfter = Date(message.date.time + 24 * 60 * 60 * 1000)
//
//                var dateToDisplay = Date()
//
//                when ((0..1).random())
//                {
//                    0 -> dateToDisplay = dayBefore
//                    1 -> dateToDisplay = dayAfter
//                }

//                DateTimeCell(displayDateTime = simpleDateFormat.format(dateToDisplay))
                DateTimeCell(displayDateTime = message.timestampDisplay)
            }
            
            ChatMessageKind.SystemMessage -> {
                SystemMessageCell(message = message)
            }

            else -> {}
        }

//        if (!(message.isSender)) {
//            Spacer(modifier = Modifier.size(80.dp))
//        }
    }
}