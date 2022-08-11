package com.pcsfg.pckit.chatkit.styles.messagecells

import androidx.compose.ui.graphics.Color
import com.pcsfg.pckit.chatkit.styles.messagecells.common.CommonTextStyle
import com.pcsfg.pckit.chatkit.ui.theme.chatCellOrange
import com.pcsfg.pckit.chatkit.ui.theme.incomingCellBackgroundColor
import com.pcsfg.pckit.chatkit.ui.theme.outgoingCellBackgroundColor

class ChatMessageCellStyle {

    private val inCellBackgroundColor = incomingCellBackgroundColor.copy(alpha = 0.5f)
    private val outCellBackgroundColor = outgoingCellBackgroundColor

    val cachedMessageAlpha = 0.5f
    val synchronizedMessageAlpha = 1.0f

    /// Incoming Text Cell Style
    val incomingTextCellStyle : TextCellStyle = TextCellStyle(
          _textStyle = CommonTextStyle(textColor= Color.Black , senderNameTextColor = chatCellOrange),
        _cellBackgroundColor = inCellBackgroundColor
    )

    /// Outgoing Text Cell Style
    val outgoingTextCellStyle : TextCellStyle = TextCellStyle(
        _textStyle = CommonTextStyle(textColor= Color.Black),
        _cellBackgroundColor = outCellBackgroundColor
    )

    /// Incoming Image Cell Style
    val incomingImageCellStyle : ImageCellStyle = ImageCellStyle(
        _textStyle = CommonTextStyle(textColor= Color.Black , senderNameTextColor = chatCellOrange),
        _cellBackgroundColor =  inCellBackgroundColor
    )

    /// Outgoing Image Cell Style
    val outgoingImageCellStyle : ImageCellStyle = ImageCellStyle(
        _textStyle = CommonTextStyle(textColor= Color.Black),
        _cellBackgroundColor =  outCellBackgroundColor
    )

    /// Incoming Image Text Cell Style
    val incomingImageTextCellStyle : ImageTextCellStyle = ImageTextCellStyle(
        _textStyle = CommonTextStyle(textColor= Color.Black , senderNameTextColor = chatCellOrange),
        _cellBackgroundColor =  inCellBackgroundColor
    )

    /// Outgoing Image Text Cell Style
    val outgoingImageTextCellStyle : ImageTextCellStyle = ImageTextCellStyle(
        _textStyle = CommonTextStyle(textColor= Color.Black),
        _cellBackgroundColor =  outCellBackgroundColor
    )

    /// Incoming Document Cell Style
    val incomingDocumentCellStyle : DocumentCellStyle = DocumentCellStyle(
        _textStyle = CommonTextStyle(textColor= Color.Black , senderNameTextColor = chatCellOrange),
        _cellBackgroundColor =  inCellBackgroundColor
    )

    /// Outgoing Document Cell Style
    val outgoingDocumentCellStyle : DocumentCellStyle = DocumentCellStyle(
        _textStyle = CommonTextStyle(textColor= Color.Black),
        _cellBackgroundColor =  outCellBackgroundColor
    )

    /// Incoming Video Cell Style
    val incomingVideoCellStyle : VideoCellStyle = VideoCellStyle(
        _textStyle = CommonTextStyle(textColor= Color.Black , senderNameTextColor = chatCellOrange),
        _cellBackgroundColor =  inCellBackgroundColor
    )

    /// Outgoing Video Cell Style
    val outgoingVideoCellStyle : VideoCellStyle = VideoCellStyle(
        _textStyle = CommonTextStyle(textColor= Color.Black),
        _cellBackgroundColor =  outCellBackgroundColor
    )

    /// Date Time Cell Style
    val dateTimeCellStyle : DateTimeCellStyle = DateTimeCellStyle(
        _textStyle = CommonTextStyle(textColor= Color.Black),
    )


}