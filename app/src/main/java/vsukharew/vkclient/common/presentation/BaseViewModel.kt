package vsukharew.vkclient.common.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import vsukharew.vkclient.common.domain.model.AppError
import vsukharew.vkclient.common.domain.model.Left
import vsukharew.vkclient.common.livedata.SingleLiveEvent

abstract class BaseViewModel : ViewModel() {
    val errorLiveData = MutableLiveData<SingleLiveEvent<Left<AppError>>>()
}