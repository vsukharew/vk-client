package vsukharew.vkclient.common.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import vsukharew.vkclient.common.domain.model.Result
import vsukharew.vkclient.common.livedata.SingleLiveEvent

abstract class BaseViewModel : ViewModel() {
    val errorLiveData = MutableLiveData<SingleLiveEvent<Result.Error>>()
}