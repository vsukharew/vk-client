package vsukharew.vkclient.publishimage.attach.domain.infrastructure

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
}