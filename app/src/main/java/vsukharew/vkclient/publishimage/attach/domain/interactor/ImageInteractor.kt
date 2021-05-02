package vsukharew.vkclient.publishimage.attach.domain.interactor

import kotlinx.coroutines.flow.Flow
import vsukharew.vkclient.common.domain.model.Result
import vsukharew.vkclient.publishimage.attach.domain.model.Image
import vsukharew.vkclient.publishimage.attach.domain.model.SavedWallImage

interface ImageInteractor {
    suspend fun uploadImage(
        image: Image,
        isRetryLoading: Boolean,
        onProgressUpdated: (Double) -> Unit
    ): Result<SavedWallImage>
    fun removeUploadedImage(image: Image)
    suspend fun postImagesOnWall(message: String): Result<Int>
    fun observePublishingReadiness(): Flow<Boolean>
    fun observePublishedPosts(): Flow<Int?>
}