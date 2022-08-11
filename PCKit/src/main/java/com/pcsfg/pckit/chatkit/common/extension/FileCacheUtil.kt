package com.pcsfg.pckit.chatkit.common.extension

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Environment
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import androidx.core.content.ContextCompat
import com.pcsfg.pckit.BuildConfig
import com.pcsfg.pckit.chatkit.model.CachedFileReturn
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors

class FileCacheUtil(
    private val mContext: Context
) {
    // content resolver
    private val contentResolver = mContext.contentResolver

    // to get the type of file
    private val mimeTypeMap = MimeTypeMap.getSingleton()

    private val mCacheLocation = mContext.cacheDir.toString() + "/" + BuildConfig.LIBRARY_PACKAGE_NAME //+ ".images"

    var fileNameOutput = ""

    fun cacheImage(uris: List<Uri>) {
        executor.submit {
            uris.forEach { uri -> copyImageToCache(uri) }
        }
    }

    fun cacheImage(uri: Uri) :CachedFileReturn{
//        executor.submit {
////            uris.forEach { uri -> copyFromSource(uri) }
//           fileNameOutput = copyFromSource(uri)
//        }
        return copyImageToCache(uri)
    }

    fun cacheDocument(uri: Uri) : CachedFileReturn {
        return copyDocumentToCache(uri)
    }

    fun getFile(uri:Uri) : File {
//        executor.submit {
//            return@submit getFileFromCache(uri)
//        }
        return getFileFromCache(uri = uri)
    }


    @Synchronized
    private fun copyDocumentToCache(uri: Uri) :CachedFileReturn {

        val directory = File(mCacheLocation)
        if(!directory.exists())
        {
            directory.mkdirs()
        }

        val fileExtension: String = getFileExtension(uri) ?: kotlin.run {
            throw RuntimeException("Extension is null for $uri")
        }
        val fileName = queryName(uri) ?: getFileName(fileExtension)

        val inputStream = contentResolver.openInputStream(uri) ?: kotlin.run {
            throw RuntimeException("Cannot open for reading $uri")
        }
        val bufferedInputStream = BufferedInputStream(inputStream)

        // the file which will be the new cached file
        val outputFile = File(mCacheLocation, fileName)
        val bufferedOutputStream = BufferedOutputStream(FileOutputStream(outputFile))
        val bos = ByteArrayOutputStream()

        // this will hold the content for each iteration
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)

        var readBytes = 0 // will be -1 if reached the end of file

        while (true) {
            readBytes = bufferedInputStream.read(buffer)

            // check if the read was failure
            if (readBytes == -1) {
                bufferedOutputStream.flush()
                break
            }

            bufferedOutputStream.write(buffer)
            bufferedOutputStream.flush()
        }

        // close everything
        println ("Copy Document To Cache Folder Success")
        inputStream.close()
        bufferedInputStream.close()
        bufferedOutputStream.close()

//        var rotatedImageFileName =  rotateImage(fileName)
//        return "$mCacheLocation/$fileName"
//        return rotatedImageFileName
        val cacheFileReturn = CachedFileReturn()
        cacheFileReturn.fileName = fileName
        cacheFileReturn.fileType = fileExtension
        return cacheFileReturn
//        return fileName
    }

    /**
     * Copies the actual data from provided content provider.
     */
    @Synchronized
    private fun copyImageToCache(uri: Uri) :CachedFileReturn {

        val directory = File(mCacheLocation)
        if(!directory.exists())
        {
            directory.mkdirs()
        }

        val fileExtension: String = getFileExtension(uri) ?: kotlin.run {
            throw RuntimeException("Extension is null for $uri")
        }
        val fileName = queryName(uri) ?: getFileName(fileExtension)

        val inputStream = contentResolver.openInputStream(uri) ?: kotlin.run {
            throw RuntimeException("Cannot open for reading $uri")
        }
        val bufferedInputStream = BufferedInputStream(inputStream)

        // the file which will be the new cached file
        val outputFile = File(mCacheLocation, fileName)
        val bufferedOutputStream = BufferedOutputStream(FileOutputStream(outputFile))
        val bos = ByteArrayOutputStream()

        // this will hold the content for each iteration
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)

        var readBytes = 0 // will be -1 if reached the end of file

        while (true) {
            readBytes = bufferedInputStream.read(buffer)

            // check if the read was failure
            if (readBytes == -1) {
                bufferedOutputStream.flush()
                break
            }

            bufferedOutputStream.write(buffer)
            bufferedOutputStream.flush()
        }

        // close everything
        println ("Copy Image To Cache Folder Success")
        inputStream.close()
        bufferedInputStream.close()
        bufferedOutputStream.close()

        var rotatedImageFileName =  rotateImage(fileName)
//        return "$mCacheLocation/$fileName"

        val cacheFileReturn = CachedFileReturn()
        cacheFileReturn.fileName = rotatedImageFileName
//        cacheFileReturn.fileName = fileName
        cacheFileReturn.fileType = fileExtension
        return cacheFileReturn
//        return rotatedImageFileName
    }

    private fun getFileExtension(uri: Uri): String? {
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri))
    }

    /**
     * Tries to get actual name of the file being copied.
     * This might be required in some of the cases where you might want to know the file name too.
     *
     * @param uri
     *
     */
    @SuppressLint("Recycle")
    private fun queryName(uri: Uri): String? {
        val returnCursor: Cursor = contentResolver.query(uri, null, null, null, null) ?: return null
        val nameIndex: Int = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        val name: String = returnCursor.getString(nameIndex)
        returnCursor.close()
        return name
    }

    private fun getFileName(fileExtension: String): String {
        return "${System.currentTimeMillis().toString()}.$fileExtension"
    }


    private fun getFileFromCache(uri: Uri):File {

        val fileExtension: String = getFileExtension(uri) ?: kotlin.run {
            throw RuntimeException("Extension is null for $uri")
        }
        val fileName = queryName(uri) ?: getFileName(fileExtension)


        // the file which will be the new cached file
        val outputFile = File(uri.toString())
//        val outputFile = File(mCacheLocation, fileName)


        // close everything
        println ("Get File from Cache Folder Success")
        return  outputFile
    }

    fun rotateImage(photoFileName: String?): String {

        val filePath = "$mCacheLocation/$photoFileName"

        // Create and configure BitmapFactory
        val bounds: BitmapFactory.Options = BitmapFactory.Options()
        bounds.inJustDecodeBounds = true
        BitmapFactory.decodeFile(filePath, bounds)
        val opts: BitmapFactory.Options = BitmapFactory.Options()
        val bm: Bitmap = BitmapFactory.decodeFile(filePath, opts)
        // Read EXIF Data
        var exif: ExifInterface? = null
        try {
            exif = filePath?.let { ExifInterface(it) }
        } catch (e: IOException) {
            // Log.d("LOWQIMAGEDEBUG", "e: ${e.toString()}")
            e.printStackTrace()
        }
        val orientString = exif?.getAttribute(ExifInterface.TAG_ORIENTATION)
        val orientation = orientString?.toInt() ?: ExifInterface.ORIENTATION_NORMAL
        var rotationAngle = 0f
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90f
        if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180f
        if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270f
        if (orientation == ExifInterface.ORIENTATION_TRANSVERSE) rotationAngle = -90f
        if (orientation == ExifInterface.ORIENTATION_FLIP_VERTICAL) rotationAngle = 180f
        // Rotate Bitmap
        val matrix = Matrix()
        matrix.setRotate(rotationAngle, bm.width.toFloat() / 2, bm.height.toFloat() / 2)
        // Return result
        val bitmap = Bitmap.createBitmap(bm, 0, 0, bounds.outWidth, bounds.outHeight, matrix, true)
        val byteArray = imageToByteArray(bitmap)
        val photoFile = createImageFile()
        photoFile.writeBytes(byteArray)
        return photoFile.name
    }

    private fun imageToByteArray(bitmap: Bitmap): ByteArray {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 75, baos)
        return baos.toByteArray()
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir = ContextCompat.getExternalFilesDirs(mContext, Environment.DIRECTORY_PICTURES)
        val directory = File(mContext.cacheDir, BuildConfig.LIBRARY_PACKAGE_NAME)
        /*Log.d("LOWQIMAGEDEBUG", "storageDir: ${storageDir.size}")
        for(sDir in storageDir) {
            Log.d("LOWQIMAGEDEBUG", "sdir: ${sDir.name}, ${sDir.absolutePath}, ${sDir.canonicalPath}, ${sDir.path}")
        }*/
        return File.createTempFile(
            "Rotated_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
//            storageDir[0] /* directory */
                    directory
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            // currentPhotoPath = absolutePath
        }
    }


    /**
     * Remove everything that we have cached.
     * You might want to invoke this method before quiting the application.
     */
    fun removeAll() {
        mContext.cacheDir.deleteRecursively()
    }

    companion object {

        // base buffer size
        private const val BASE_BUFFER_SIZE = 1024

        // if you want to modify size use binary multiplier 2, 4, 6, 8
        private const val DEFAULT_BUFFER_SIZE = BASE_BUFFER_SIZE * 4

        val executor = Executors.newSingleThreadExecutor()

    }

}