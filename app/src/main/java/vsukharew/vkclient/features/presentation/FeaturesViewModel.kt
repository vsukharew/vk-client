package vsukharew.vkclient.features.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import vsukharew.vkclient.account.data.AccountRepo
import vsukharew.vkclient.common.domain.model.Result
import vsukharew.vkclient.common.livedata.SingleLiveEvent

class FeaturesViewModel(accountRepo: AccountRepo) : ViewModel() {
    val profileInfo = liveData {
        isLoading.value = true
        signOutButtonVisible.value = false
        val info = accountRepo.getProfileInfo()
        isLoading.value = false
        if (info is Result.Success) {
            signOutButtonVisible.value = true
        }
        emit(SingleLiveEvent(info))
    }
    val signOutButtonVisible = MutableLiveData<Boolean>()
    val isLoading: MutableLiveData<Boolean> = MutableLiveData(false)

    val signOutEvent = MutableLiveData<SingleLiveEvent<Unit>>()

    fun onSignOutClick() {
        signOutEvent.value = SingleLiveEvent(Unit)
    }
}