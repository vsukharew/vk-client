package vsukharew.vkclient.publishimage.flow

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import vsukharew.vkclient.common.presentation.BaseViewModel
import vsukharew.vkclient.publishimage.flow.PublishImageViewModel.PublishImageStage.ATTACH_IMAGE

class PublishImageViewModel(
    private val savedState: SavedStateHandle
) : BaseViewModel() {
    val currentStageLiveData = MutableLiveData<PublishImageStage>().apply {
        savedState.get<PublishImageStage>(KEY_CURRENT_STAGE) ?: ATTACH_IMAGE
    }

    fun onStageChanged(stage: PublishImageStage) {
        currentStageLiveData.value = stage.also { savedState[KEY_CURRENT_STAGE] = it }
    }

    enum class PublishImageStage {
        ATTACH_IMAGE,
        CAPTION,
    }

    private companion object {
        private const val KEY_CURRENT_STAGE = "current_stage"
    }
}