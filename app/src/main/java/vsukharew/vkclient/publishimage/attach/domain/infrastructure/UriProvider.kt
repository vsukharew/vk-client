package vsukharew.vkclient.publishimage.attach.domain.infrastructure

interface UriProvider {
    /**
     * Creates a file for storing image taken from camera to post on wall
     *
     * @return string representation of file's Uri
     */
    fun createFileForWallImage(): String
}