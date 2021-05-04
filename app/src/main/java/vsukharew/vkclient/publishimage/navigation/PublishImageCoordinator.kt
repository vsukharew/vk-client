package vsukharew.vkclient.publishimage.navigation

import androidx.annotation.IdRes
import androidx.navigation.NavController
import vsukharew.vkclient.R
import vsukharew.vkclient.common.navigation.BackStackEntryObserver
import vsukharew.vkclient.publishimage.attach.presentation.dialog.ImageSourceBottomSheetDialog.ImageSource
import vsukharew.vkclient.publishimage.attach.presentation.dialog.ImageSourceBottomSheetDialog.ImageSourceListener
import vsukharew.vkclient.publishimage.navigation.PublishImageFlowStage.AttachImageStage
import vsukharew.vkclient.publishimage.navigation.PublishImageFlowStage.CaptionStage

class PublishImageCoordinator(private val navigator: PublishImageNavigator) : ImageSourceListener {
    val attachImageStage = AttachImageStage(this)
    val captionStage = CaptionStage(this)
    var currentStage: PublishImageFlowStage = attachImageStage
    var rootNavController: NavController? = null
        set(value) {
            field = value
            navigator.rootNavController = value
        }

    var flowNavController: NavController? = null
        set(value) {
            field = value
            navigator.flowNavController = value
        }

    override fun onSourceChoose(source: ImageSource) {
        navigator.onSourceChoose(source)
    }

    fun addObserverToBackStackEntry(@IdRes id: Int, onResumeBlock: () -> Unit = {}) {
        flowNavController?.let { BackStackEntryObserver.addObserver(it, id, onResumeBlock) }
    }

    fun <T> doIfKeyExists(key: String, block: (T?) -> Unit) {
        val stateHandle = flowNavController?.currentBackStackEntry?.savedStateHandle
        if (stateHandle?.contains(key) == true) {
            val value = stateHandle.get<T>(key)
            block.invoke(value)
        }
    }

    fun <T> removeKey(key: String) {
        flowNavController?.currentBackStackEntry?.savedStateHandle?.remove<T>(key)
    }

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
        navigator.openImageSourceScreen()
    }
}