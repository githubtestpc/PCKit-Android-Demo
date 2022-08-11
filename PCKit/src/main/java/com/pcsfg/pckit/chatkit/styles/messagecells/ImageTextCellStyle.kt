package com.pcsfg.pckit.chatkit.styles.messagecells

import androidx.compose.ui.graphics.Color
import com.pcsfg.pckit.chatkit.styles.messagecells.common.CommonDateTimeStyle
import com.pcsfg.pckit.chatkit.styles.messagecells.common.CommonMessageStatusTextStyle
import com.pcsfg.pckit.chatkit.styles.messagecells.common.CommonTextStyle
import com.pcsfg.pckit.chatkit.styles.messagecells.common.CommonViewStyle

class ImageTextCellStyle(_textStyle: CommonTextStyle = CommonTextStyle(),
                         _dateTimeStyle: CommonDateTimeStyle = CommonDateTimeStyle(),
                         _messageStatusTextStyle: CommonMessageStatusTextStyle = CommonMessageStatusTextStyle(),
                         _imagePadding: Double = 5.0,
                         _dateTimeInImage: Boolean = false,
                         _cellMinWidth: Double = 100.0,
                         _cellMaxWidth: Double = 300.0,
                         _cellPadding: Double = 10.0,
                         _cellSameSenderTopPadding: Double = 4.0,
                         _cellDifferentSenderTopPadding: Double = 20.0,
                         _cellBackgroundColor: Color = Color.Blue,
                         _cellCornerRadius: Double = 10.0,
                         _cellBorderColor: Color = Color.Transparent,
                         _cellBorderWidth: Double = 1.0,
                         _cellElevationRadius: Double = 0.0,
                         _cellElevationColor: Color = Color.LightGray) : CommonViewStyle {

    public val textStyle: CommonTextStyle = _textStyle
    public val dateTimeStyle: CommonDateTimeStyle = _dateTimeStyle
    public val messageStatusTextStyle: CommonMessageStatusTextStyle = _messageStatusTextStyle
    public val imagePadding: Double = _imagePadding
    public val dateTimeInImage: Boolean = _dateTimeInImage

    // CellContainerStyle
    public override var cellMinWidth: Double = _cellMinWidth
    public override var cellMaxWidth: Double = _cellMaxWidth
    public override var cellPadding : Double = _cellPadding
    public override var cellSameSenderTopPadding: Double = _cellSameSenderTopPadding
    public override var cellDifferentSenderTopPadding: Double = _cellDifferentSenderTopPadding
    public override var cellBackgroundColor : Color = _cellBackgroundColor
    public override var cellCornerRadius: Double = _cellCornerRadius
    public override var cellBorderColor: Color = _cellBorderColor
    public override var cellBorderWidth: Double = _cellBorderWidth
    public override var cellElevationRadius: Double = _cellElevationRadius
    public override var cellElevationColor: Color = _cellElevationColor
}