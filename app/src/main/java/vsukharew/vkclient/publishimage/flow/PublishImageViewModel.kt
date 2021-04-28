package vsukharew.vkclient.publishimage.flow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import vsukharew.vkclient.publishimage.attach.domain.interactor.ImageInteractor

class PublishImageViewModel(
    imageInteractor: ImageInteractor
) : ViewModel() {

    val isNextButtonAvailable = imageInteractor.observePublishingReadiness().asLiveData()
}