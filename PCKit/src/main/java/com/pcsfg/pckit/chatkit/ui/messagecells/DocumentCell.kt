package com.pcsfg.pckit.chatkit.ui.messagecells

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import com.pcsfg.pckit.R
import com.pcsfg.pckit.chatkit.common.view.DateTimeText
import com.pcsfg.pckit.chatkit.styles.messagecells.DocumentCellStyle
import com.pcsfg.pckit.chatkit.ui.chatview.GlobalCellStyle
import com.pcsfg.pckit.chatkit.ui.components.messagecells.AvatarView
import com.pcsfg.pckit.chatkit.ui.components.messagecells.MessageStatusText
import com.pcsfg.pckit.chatkit.ui.messagecells.common.SenderTextCell


@Composable
fun DocumentCell(
    message: com.pcsfg.pckit.chatkit.model.ChatMessage,
    documentURL: String,
    documentName: String
) {
    var cellStyle: DocumentCellStyle =
        if (message.isSender == true) GlobalCellStyle.current.outgoingDocumentCellStyle else GlobalCellStyle.current.incomingDocumentCellStyle

    var cellTopPadding =
        if (message.isSameSender == true) cellStyle.cellSameSenderTopPadding else cellStyle.cellDifferentSenderTopPadding

    val context = LocalContext.current

    var columnAlignment =
        if (message.isSender == true) Alignment.End else Alignment.Start

    val documentIcon =
        when (message.files?.get(0)?.type) {
            "application/pdf" -> R.drawable.ic_pdfcell
            "application/msword" -> R.drawable.ic_wordcell
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document" -> R.drawable.ic_wordcell
            "application/vnd.ms-excel" -> R.drawable.ic_excelcell
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" -> R.drawable.ic_excelcell
            "application/vnd.ms-powerpoint" -> R.drawable.ic_pptcell
            "application/vnd.openxmlformats-officedocument.presentationml.presentation" -> R.drawable.ic_pptcell
            else -> R.drawable.ic_pdfcell
        }

    val messageAlpha = if (message.cachedMessage) GlobalCellStyle.current.cachedMessageAlpha else GlobalCellStyle.current.synchronizedMessageAlpha

    Box() {
        Column(
            horizontalAlignment = columnAlignment,
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
                    if (message.isSender == true) {
                        Column(horizontalAlignment = Alignment.End) {
                            if (!message.cachedMessage)
                            {
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
                        border = BorderStroke(
                            cellStyle.cellBorderWidth.dp,
                            cellStyle.cellBorderColor
                        ),
                        modifier = Modifier.weight(1f)
                            .alpha(messageAlpha)

                    ) {
                        Column(modifier = Modifier.clickable(
                            onClick = {
                                documentOnClick(context = context, documentURL = documentURL)
                            }
                        )) {
                            if (!message.isSender!! && message.isSameSender == false) {
                                SenderTextCell(
                                    senderName = message.senderName,
                                    textPadding = cellStyle.textStyle.textPadding,
                                    senderNameTextColor = cellStyle.textStyle.senderNameTextColor,
                                    fontWeight = cellStyle.textStyle.fontWeight
                                )
                            }

                            Row(modifier = Modifier.padding(cellStyle.cellPadding.dp))
                            {
                                Image(
//                    modifier = Modifier.fillMaxSize(),
                                    painter = painterResource(id = documentIcon),
                                    contentDescription = "Document Icon",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.size(40.dp)
                                )

                                Text(
                                    text = documentName,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.padding(cellStyle.textStyle.textPadding.dp)
                                )
                            }
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
}

fun documentOnClick(documentURL: String , context: Context)
{
    Log.d(
        "Document Cell",
        "Cell Click"
    )

        // Show PDF to External App
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(documentURL))
    startActivity(context, intent, null)


}

@Preview(showBackground = true)
@Composable
fun DocumentCellPreview() {
//    ComposeTheme {
//        val navController = rememberNavController()
//        val screen1ViewModel = Screen1ViewModel()
//
//        Screen1(navController, screen1ViewModel)
//    }
    val context = LocalContext.current
    val documentURL = "https://pcsfgmkthk.oss-accelerate.aliyuncs.com/UAT/room/RM00000039/145_CICN300599221_895591.pdf"
//    DocumentCell(displayText = "Document Cell Preview" , documentURL = documentURL)
//    val navController = rememberNavController()
//    DocumentCell(displayText = "Document Cell Preview" , navController = navController)
}