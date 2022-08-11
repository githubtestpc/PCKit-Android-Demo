package com.pcsfg.pckit.chatkit.ui.messagecells.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pcsfg.pckit.chatkit.styles.messagecells.DateTimeCellStyle
import com.pcsfg.pckit.chatkit.ui.chatview.GlobalCellStyle
import com.pcsfg.pckit.chatkit.ui.theme.dateTimeCellBackgroundColor

@Composable
fun DateTimeCell(displayDateTime:String)
{
    var cellStyle: DateTimeCellStyle = GlobalCellStyle.current.dateTimeCellStyle

//    Text(text = senderName, modifier = Modifier.padding(textPadding.dp), color = senderNameTextColor , fontWeight = fontWeight)
    Column(
        horizontalAlignment = Alignment.Start, modifier = Modifier
//            .padding(cellStyle.cellPadding.dp)
//            .padding(50.dp)
            .padding(
                start = 0.dp,
                top = cellStyle.cellTopPadding.dp,
                end = 0.dp,
                bottom = 0.dp
            )
            .widthIn(cellStyle.cellMinWidth.dp, cellStyle.cellMaxWidth.dp)
    ) {
        Card(
//            backgroundColor = cellStyle.cellBackgroundColor,
            backgroundColor = dateTimeCellBackgroundColor,
            elevation = cellStyle.cellElevationRadius.dp,
            shape = RoundedCornerShape(cellStyle.cellCornerRadius.dp),
            border = BorderStroke(cellStyle.cellBorderWidth.dp, cellStyle.cellBorderColor)
        )
        {
            Text(displayDateTime , modifier = Modifier.padding(cellStyle.cellPadding.dp))
        }
    }

}