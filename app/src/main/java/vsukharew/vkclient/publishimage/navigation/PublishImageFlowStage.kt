package vsukharew.vkclient.publishimage.navigation

sealed class PublishImageFlowStage {
    abstract fun onBackClick()
    abstract fun onForwardClick()

    data class AttachImageStage(val navigator: PublishImageNavigator) : PublishImageFlowStage() {
        override fun onBackClick() {
            navigator.exitFlow()
        }

        override fun onForwardClick() {
            navigator.openCaptionScreen()
        }
    }

    data class CaptionStage(val navigator: PublishImageNavigator) : PublishImageFlowStage() {
        override fun onBackClick() {
            navigator.goBackToImageAttachStage()
        }

        override fun onForwardClick() {

        }
    }
}