package vsukharew.vkclient.publishimage.navigation

import androidx.annotation.IdRes
import androidx.navigation.NavController
import vsukharew.vkclient.R
import vsukharew.vkclient.common.navigation.BackStackEntryObserver
import vsukharew.vkclient.publishimage.attach.domain.model.ImageSource
import vsukharew.vkclient.publishimage.attach.presentation.dialog.ImageSourceBottomSheetDialog
import vsukharew.vkclient.publishimage.attach.presentation.dialog.ImageSourceBottomSheetDialog.ImageSourceListener
import vsukharew.vkclient.publishimage.flow.PublishImageViewModel.PublishImageStage
import vsukharew.vkclient.publishimage.flow.PublishImageViewModel.PublishImageStage.ATTACH_IMAGE
import vsukharew.vkclient.publishimage.flow.PublishImageViewModel.PublishImageStage.CAPTION
import vsukharew.vkclient.publishimage.navigation.PublishImageFlowStage.AttachImageStage
import vsukharew.vkclient.publishimage.navigation.PublishImageFlowStage.CaptionStage

class PublishImageCoordinator(
    private val rootNavController: NavController,
    private val flowNavController: NavController,
) : ImageSourceListener {
    val attachImageStage = AttachImageStage(this)
    val captionStage = CaptionStage(this)
    var currentStage: PublishImageFlowStage = attachImageStage

    override fun onSourceChoose(source: ImageSource) {
        flowNavController.getBackStackEntry(R.id.attachImageFragment)
            .savedStateHandle
            .set(ImageSourceBottomSheetDialog.KEY_IMAGE_SOURCE, source)
    }

    fun setCurrentStage(stage: PublishImageStage) {
        currentStage = when (stage) {
            ATTACH_IMAGE -> attachImageStage
            CAPTION -> captionStage
        }
    }

    fun addObserverToBackStackEntry(@IdRes id: Int, onResumeBlock: () -> Unit = {}) {
        flowNavController.let { BackStackEntryObserver(onResumeBlock).addObserver(it, id) }
    }

    fun <T> doIfKeyExists(key: String, block: (T?) -> Unit) {
        val stateHandle = flowNavController.currentBackStackEntry?.savedStateHandle
        if (stateHandle?.contains(key) == true) {
            val value = stateHandle.get<T>(key)
            block.invoke(value)
        }
    }

    fun <T> removeKey(key: String) {
        flowNavController.currentBackStackEntry?.savedStateHandle?.remove<T>(key)
    }

    fun exitFlow() {
        rootNavController.popBackStack(R.id.features_fragment, false)
    }

    fun openCaptionScreen() {
        flowNavController.navigate(R.id.action_attachImageFragment_to_captionFragment)
    }

    fun goBackToImageAttachStage() {
        flowNavController.popBackStack(R.id.attachImageFragment, false)
    }

    fun openImageSourceScreen() {
        flowNavController.navigate(R.id.imageSourceBottomSheetDialog)
    }
}