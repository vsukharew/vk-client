package vsukharew.vkclient.publishimage.navigation

sealed class PublishImageFlowStage {
    abstract fun onBackClick()
    abstract fun onForwardClick()

    data class AttachImageStage(
        private val coordinator: PublishImageCoordinator
    ) : PublishImageFlowStage() {
        override fun onBackClick() {
            coordinator.exitFlow()
        }

        override fun onForwardClick() {
            coordinator.apply {
                currentStage = captionStage
                openCaptionScreen()
            }
        }
    }

    data class CaptionStage(
        private val coordinator: PublishImageCoordinator
    ) : PublishImageFlowStage() {
        override fun onBackClick() {
            coordinator.apply {
                currentStage = attachImageStage
                goBackToImageAttachStage()
            }
        }

        override fun onForwardClick() {
            coordinator.exitFlow()
        }
    }
}