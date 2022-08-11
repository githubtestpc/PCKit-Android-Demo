package com.pcsfg.pckit.chatkit.common.extension

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.pcsfg.pckit.R
import com.pcsfg.pckit.BuildConfig
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class CameraFileProvider : FileProvider(
    R.xml.filepaths
) {
    companion object {
        fun getImageUri(context: Context): Uri {

            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
//            val directory = File(context.cacheDir, BuildConfig.LIBRARY_PACKAGE_NAME + ".images")
            val directory = File(context.cacheDir, BuildConfig.LIBRARY_PACKAGE_NAME)
            if(!directory.exists())
            {
                directory.mkdirs()
            }

            //Create a file in Cache Folder for save image from Camera
            val file = File.createTempFile(
                "camera_image_",
                ".jpg",
                directory
            )

            //Return the content path of the image file.
//            val authority =  context.packageName + BuildConfig.LIBRARY_PACKAGE_NAME
            val authority = context.packageName + ".com.pcsfg.pckit"
            return getUriForFile(
                context,
                authority,
                file,
            )
        }


    }

}

