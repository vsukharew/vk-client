package vsukharew.vkclient.features.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import vsukharew.vkclient.account.data.AccountRepo
import vsukharew.vkclient.common.livedata.SingleLiveEvent

class FeaturesViewModel(accountRepo: AccountRepo) : ViewModel() {
    val profileInfo = liveData {
        isLoading.value = true
        val info = accountRepo.getProfileInfo()
        isLoading.value = false
        emit(SingleLiveEvent(info))
    }

    val isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
}