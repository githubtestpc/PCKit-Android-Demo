package com.pcsfg.pckit.chatkit.styles.inputview.common

import androidx.compose.ui.graphics.Color
import com.pcsfg.pckit.chatkit.state.inputview.InputViewEvent

interface CommonButtonStyle {

    //Button Image
    var buttonImage : Int

    //Button Text
    var buttonText : String

    //Button Background Color
    var buttonBackgroundColor : Color

    //Button Click Event
    var buttonClickAction : InputViewEvent


}