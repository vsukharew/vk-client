package vsukharew.vkclient.publishimage.attach.domain.infrastructure

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import vsukharew.vkclient.common.utils.DatePatterns.YEAR_MONTH_NUMBER_DAY_HOURS_MINUTES_SECONDS
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class AndroidUriProvider(private val context: Context) : UriProvider {

    private val authority = "${context.packageName}.fileprovider"

    override fun createFileForWallImage(): String {
        val timeStamp = SimpleDateFormat(
            YEAR_MONTH_NUMBER_DAY_HOURS_MINUTES_SECONDS,
            Locale.getDefault()
        ).format(Date())
        val storageDir = context.filesDir
        val imagesDirectoryPath = "$storageDir/Wall Images"
        val wallImagesDirectory = with(File(imagesDirectoryPath)) {
            if (!exists()) {
                mkdir()
                this
            } else {
                this
            }
        }
        val file = File.createTempFile(
            "photo $timeStamp",
            ".jpg",
            wallImagesDirectory
        )
        return FileProvider.getUriForFile(context, authority, file)
            .toString()
    }

    override fun getExtensionFromContentUri(uri: String): String? {
        return context.contentResolver.getType(Uri.parse(uri))
            ?.split("/")
            ?.last()
    }
}