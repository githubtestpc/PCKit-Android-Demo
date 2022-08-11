package com.pcsfg.pckit.chatkit.styles.inputview

import androidx.compose.ui.graphics.Color
import com.pcsfg.pckit.chatkit.state.inputview.InputViewEvent
import com.pcsfg.pckit.chatkit.styles.inputview.common.CommonButtonStyle

class BottomSheetButtonStyle (_buttonImage : Int,
                              _buttonText: String,
                              _buttonBackgroundColor : Color,
                              _buttonClickAction : InputViewEvent
        ) :CommonButtonStyle
{
    //Button Image
    public override var buttonImage = _buttonImage

    //Button Text
    public override var buttonText = _buttonText

    //Button Background Color
    public override var buttonBackgroundColor = _buttonBackgroundColor

    //Button Click Event
    public override var buttonClickAction = _buttonClickAction
}