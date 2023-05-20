package vsukharew.vkclient.publishimage.flow

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import vsukharew.vkclient.common.domain.interactor.SessionInteractor
import vsukharew.vkclient.common.presentation.BaseViewModel

class PublishImageViewModel(
    private val savedState: SavedStateHandle,
    sessionInteractor: SessionInteractor
) : BaseViewModel(sessionInteractor) {
    val currentStageLiveData = savedState.getLiveData<PublishImageStage>(KEY_CURRENT_STAGE)

    fun onStageChanged(stage: PublishImageStage) {
        savedState[KEY_CURRENT_STAGE] = stage
    }

    enum class PublishImageStage {
        ATTACH_IMAGE,
        CAPTION,
    }

    private companion object {
        private const val KEY_CURRENT_STAGE = "current_stage"
    }
}