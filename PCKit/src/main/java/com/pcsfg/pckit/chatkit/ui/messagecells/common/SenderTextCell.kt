package com.pcsfg.pckit.chatkit.ui.messagecells.common

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun SenderTextCell(senderName: String, textPadding:Double, senderNameTextColor: Color, fontWeight: FontWeight)
{
    Text(text = senderName, modifier = Modifier.padding(textPadding.dp), color = senderNameTextColor , fontWeight = fontWeight)
//    Text(text = AnnotatedString(text = senderName , ) senderName, modifier = androidx.compose.ui.Modifier.padding(textPadding.dp), color = senderNameTextColor , fontWeight = fontWeight)

}