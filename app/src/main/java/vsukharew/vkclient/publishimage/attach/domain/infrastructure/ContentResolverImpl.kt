package vsukharew.vkclient.publishimage.attach.domain.infrastructure

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.core.content.FileProvider
import vsukharew.vkclient.common.utils.AppDirectories
import vsukharew.vkclient.common.utils.DatePatterns
import java.io.File
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*

class ContentResolverImpl(private val context: Context) : DomainContentResolver {

    private val authority = "${context.packageName}.fileprovider"

    override fun createFileForWallImage(): String {
        val timeStamp = SimpleDateFormat(
            DatePatterns.YEAR_MONTH_NUMBER_DAY_HOURS_MINUTES_SECONDS,
            Locale.getDefault()
        ).format(Date())
        val storageDir = context.cacheDir
        val imagesDirectoryPath = "$storageDir/${AppDirectories.WALL_IMAGES}"
        val wallImagesDirectory = with(File(imagesDirectoryPath)) {
            if (!exists()) {
                mkdir()
                this
            } else {
                this
            }
        }
        val file = File.createTempFile(
            "photo_$timeStamp",
            ".jpg",
            wallImagesDirectory
        )
        return FileProvider.getUriForFile(context, authority, file).toString()
    }

    override fun getExtensionFromContentUri(uri: String): String? {
        return context.contentResolver.getType(Uri.parse(uri))
            ?.split("/")
            ?.last()
    }

    override fun openInputStream(uri: String): InputStream? {
        return context.contentResolver.openInputStream(Uri.parse(uri))
    }

    override fun deleteCacheFiles(subdirectoryName: String) {
        val destinationDirectory = File("${context.cacheDir}/$subdirectoryName")
        destinationDirectory.listFiles()?.forEach { it.delete() }
    }

    override fun getFileSize(uri: String): Long? {
        return context.contentResolver.query(Uri.parse(uri), null, null, null, null)
            ?.use {
                val sizeColumnIndex = it.getColumnIndex(OpenableColumns.SIZE)
                it.moveToFirst()
                it.getLong(sizeColumnIndex)
            }
    }
}