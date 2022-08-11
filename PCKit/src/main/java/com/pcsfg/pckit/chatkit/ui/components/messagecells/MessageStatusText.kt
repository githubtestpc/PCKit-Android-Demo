package com.pcsfg.pckit.chatkit.ui.components.messagecells

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pcsfg.pckit.R
import com.pcsfg.pckit.chatkit.styles.messagecells.common.CommonMessageStatusTextStyle

@Composable
fun MessageStatusText(countRead:Boolean , statusTextStyle: CommonMessageStatusTextStyle ) {

    val statusText =
       if (countRead) stringResource(R.string.ChatView_Unread) else stringResource(R.string.ChatView_Read)

    Row()
    {
        Text(
            text = statusText,
            fontSize = statusTextStyle.textSize.sp,
            color = statusTextStyle.textColor,
            modifier = Modifier.padding(
                start = statusTextStyle.textStartPadding.dp,
                top = statusTextStyle.textTopPadding.dp,
                end = statusTextStyle.textEndPadding.dp,
                bottom = statusTextStyle.textBottomPadding.dp)
        )
    }
}