package com.pcsfg.pckit.chatkit.common.view

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pcsfg.pckit.chatkit.styles.messagecells.common.CommonDateTimeStyle
import java.text.SimpleDateFormat

@Composable
fun DateTimeText(dateTimeText: String, dateTimeStyle: CommonDateTimeStyle) {

    val dateTimePattern = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val tempDateTime = dateTimePattern.parse(dateTimeText)

    val pattern = "HH:mm"
    val simpleDateFormat = SimpleDateFormat(pattern)

    Row()
    {
        Text(
            text = simpleDateFormat.format(tempDateTime),
            fontSize = dateTimeStyle.textSize.sp,
            color = dateTimeStyle.textColor,
            modifier = Modifier.padding(
                start = dateTimeStyle.textStartPadding.dp,
                top = dateTimeStyle.textTopPadding.dp,
                end = dateTimeStyle.textEndPadding.dp,
                bottom = dateTimeStyle.textBottomPadding.dp)
        )
    }
}