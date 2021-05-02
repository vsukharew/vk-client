package vsukharew.vkclient.publishimage.caption.presentation

import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import vsukharew.vkclient.common.domain.model.Result
import vsukharew.vkclient.common.livedata.SingleLiveEvent
import vsukharew.vkclient.publishimage.attach.domain.interactor.ImageInteractor
import vsukharew.vkclient.publishimage.caption.presentation.state.CaptionUIAction
import vsukharew.vkclient.publishimage.caption.presentation.state.CaptionUIState
import vsukharew.vkclient.publishimage.navigation.PublishImageFlowStage

class CaptionViewModel(
    private val imageInteractor: ImageInteractor,
    private val flowStage: PublishImageFlowStage
) : ViewModel() {

    private val publishingAction = MutableLiveData<CaptionUIAction>()
    val publishingState = Transformations.switchMap(publishingAction, ::mapUiAction)

    fun publish(message: String) {
        publishingAction.value = CaptionUIAction.Publish(message)
    }

    private fun mapUiAction(action: CaptionUIAction): LiveData<CaptionUIState> {
        return liveData {
            when (action) {
                is CaptionUIAction.Publish -> {
                    emit(CaptionUIState.LoadingProgress)
                    when (val result =
                        withContext(Dispatchers.IO) {
                            imageInteractor.postImagesOnWall(
                                action.message
                            )
                        }) {
                        is Result.Success -> {
                            emit(CaptionUIState.Success(result.data))
                            flowStage.onForwardClick()
                        }
                        is Result.Error -> {
                            emit(CaptionUIState.Error(SingleLiveEvent(result)))
                        }
                    }
                }
            }
        }
    }
}