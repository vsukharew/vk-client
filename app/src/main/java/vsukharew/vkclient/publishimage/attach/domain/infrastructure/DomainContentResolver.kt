package vsukharew.vkclient.publishimage.attach.domain.infrastructure

import vsukharew.vkclient.common.domain.model.ImageResolution
import java.io.InputStream

interface DomainContentResolver {
    /**
     * Creates a file for storing image taken from camera to post on wall
     *
     * @return string representation of file's Uri
     */
    fun createFileForWallImage(): String
    fun getExtensionFromContentUri(uri: String): String?
    fun openInputStream(uri: String): InputStream?
    fun deleteCacheFiles(subdirectoryName: String)
    fun getFileSize(uri: String): Long?
    fun getImageResolution(uri: String): ImageResolution
}