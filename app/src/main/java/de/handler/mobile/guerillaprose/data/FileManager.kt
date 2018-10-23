package de.handler.mobile.guerillaprose.data

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.provider.MediaStore
import android.util.Log
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

object FileManager {
    fun createFile(context: Context, suffix: String): File? {
        return try {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val imageFileName = "${timeStamp}_"
            val storageDir = context.filesDir
            val image: File? = try {
                File.createTempFile(
                        imageFileName, /* prefix */
                        suffix, /* suffix */
                        storageDir)   /* directory */
            } catch (e: Exception) {
                null
            }
            image
        } catch (e: IOException) {
            Log.e(javaClass.name, e.message)
            null
        }
    }

    fun resolveFileFromIntent(contentResolver: ContentResolver, intent: Intent?): File? {
        val uri = intent?.data
        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = uri?.let { contentResolver.query(it, filePathColumn, null, null, null) }
        cursor?.moveToFirst()
        val columnIndex = cursor?.getColumnIndex(filePathColumn[0]) ?: 0
        val filePath = cursor?.getString(columnIndex)
        cursor?.close()

        return filePath?.let { File(filePath) }
    }
}