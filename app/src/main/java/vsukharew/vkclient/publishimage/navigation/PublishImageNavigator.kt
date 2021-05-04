package vsukharew.vkclient.publishimage.navigation

import androidx.navigation.NavController
import vsukharew.vkclient.R
import vsukharew.vkclient.common.navigation.BaseNavigator
import vsukharew.vkclient.publishimage.attach.domain.model.ImageSource
import vsukharew.vkclient.publishimage.attach.presentation.dialog.ImageSourceBottomSheetDialog.Companion.KEY_IMAGE_SOURCE

class PublishImageNavigator : BaseNavigator() {
    var rootNavController: NavController? = null
    var flowNavController: NavController? = null

    fun exitFlow() {
        rootNavController?.popBackStack(R.id.features_fragment, false)
    }

    fun openCaptionScreen() {
        flowNavController?.navigate(R.id.action_attachImageFragment_to_captionFragment)
    }

    fun goBackToImageAttachStage() {
        flowNavController?.popBackStack(R.id.attachImageFragment, false)
    }

    fun openImageSourceScreen() {
        flowNavController?.navigate(R.id.imageSourceBottomSheetDialog)
    }

    fun onSourceChoose(source: ImageSource) {
        flowNavController?.getBackStackEntry(R.id.attachImageFragment)
            ?.savedStateHandle
            ?.set(KEY_IMAGE_SOURCE, source)
    }
}