package com.pcsfg.pckit.chatkit.ui.messagecells

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pcsfg.pckit.chatkit.model.ChatMessage

@Composable
fun SystemMessageCell(message: ChatMessage)
{
    Column(horizontalAlignment = Alignment.CenterHorizontally,
       modifier = Modifier.padding(20.dp)){
        Text(text = message.message ,
            fontSize = 14.sp,
            color = Color.Gray,
            fontWeight = FontWeight.Bold )
    }

}