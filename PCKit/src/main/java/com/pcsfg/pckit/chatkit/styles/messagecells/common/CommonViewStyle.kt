package com.pcsfg.pckit.chatkit.styles.messagecells.common

import androidx.compose.ui.graphics.Color

interface CommonViewStyle {

    //Cell Minimum Width
    var cellMinWidth : Double

    //Cell Maximum Width
    var cellMaxWidth : Double

    //Cell Padding
    var cellPadding: Double

    //Cell Same Sender Top Padding
    var cellSameSenderTopPadding: Double

    //Cell Different Sender Top Padding
    var cellDifferentSenderTopPadding: Double

    //Cell Background Color
    var cellBackgroundColor: Color

    //Cell Corner Radius
    var cellCornerRadius: Double

    //Cell Border Color
    var cellBorderColor: Color

    //Cell Border Width
    var cellBorderWidth: Double

    //Cell Elevation Radius
    var cellElevationRadius: Double

    //Cell Elevation Color
    var cellElevationColor: Color

//    //Cached Cell Alpha
//    var cachedCellAlpha: Double
}