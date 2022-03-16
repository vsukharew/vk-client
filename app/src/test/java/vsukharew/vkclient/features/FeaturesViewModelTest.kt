package vsukharew.vkclient.features

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.flow.flow
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.*
import vsukharew.vkclient.account.domain.interactor.AccountInteractor
import vsukharew.vkclient.account.domain.model.ProfileInfo
import vsukharew.vkclient.auth.domain.interactor.AuthInteractor
import vsukharew.vkclient.common.domain.interactor.SessionInteractor
import vsukharew.vkclient.common.domain.model.Either
import vsukharew.vkclient.common.extension.findAndCast
import vsukharew.vkclient.common.presentation.loadstate.ProfileInfoUiState
import vsukharew.vkclient.features.presentation.FeaturesViewModel
import vsukharew.vkclient.publishimage.attach.domain.interactor.ImageInteractor

class FeaturesViewModelTest {

    @get:Rule
    val rule = CoroutineDispatcherRule()

    // Run tasks synchronously
    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    private val accountInteractor = mock<AccountInteractor>().apply {
        stub {
            onBlocking { getProfileInfo() }
                .doReturn(
                    Either.Left(
                        ProfileInfo("Vadim", "Sukharev", "vsukharew")
                    )
                )
        }
    }
    private val authInteractor = mock<AuthInteractor>()
    private val sessionInteractor = mock<SessionInteractor>()
    private val imageInteractor = mock<ImageInteractor>().apply {
        whenever(observePublishedPosts()).thenReturn(flow { })
    }

    private val viewModel = FeaturesViewModel(
        accountInteractor,
        authInteractor,
        sessionInteractor,
        SavedStateHandle(mapOf()),
        rule.testDispatcherProvider,
        imageInteractor
    )

    @Test
    fun `profileUiState-initial loading action-should emit loading state and success state`() {
        val captor = argumentCaptor<ProfileInfoUiState>()
        val observer = mock<Observer<ProfileInfoUiState>>()
        viewModel.profileUiState.observeForTesting(observer) {
            verify(observer, times(2)).onChanged(captor.capture())
            assert(captor.allValues.size == 2)
            assert(captor.allValues.count { it is ProfileInfoUiState.LoadingProgress } == 1)
            assert(captor.allValues.count { it is ProfileInfoUiState.Success } == 1)
        }
    }

    @Test
    fun `profileUiState-initial loading action-assert expected loading state equal to actual one`() {
        val captor = argumentCaptor<ProfileInfoUiState>()
        val observer = mock<Observer<ProfileInfoUiState>>()
        viewModel.profileUiState.observeForTesting(observer) {
            verify(observer, times(2)).onChanged(captor.capture())
            val expectedState = ProfileInfoUiState.LoadingProgress
            val actualState = captor.allValues.find { it is ProfileInfoUiState.LoadingProgress }
            assert(expectedState == actualState)
        }
    }

    @Test
    fun `profileUiState-initial loading action-assert expected success state equal to actual one`() {
        val captor = argumentCaptor<ProfileInfoUiState>()
        val observer = mock<Observer<ProfileInfoUiState>>()
        viewModel.profileUiState.observeForTesting(observer) {
            verify(observer, times(2)).onChanged(captor.capture())
            val expectedState = ProfileInfoUiState.Success(
                ProfileInfo("Vadim", "Sukharev", "vsukharew")
            )
            val actualState = captor.allValues.find { it is ProfileInfoUiState.Success }
            assert(expectedState == actualState)
        }
    }

    @Test
    fun `profileUiState-initial loading action-check loading state properties`() {
        val captor = argumentCaptor<ProfileInfoUiState>()
        val observer = mock<Observer<ProfileInfoUiState>>()
        viewModel.profileUiState.observeForTesting(observer) {
            verify(observer, times(2)).onChanged(captor.capture())
            val state = captor.allValues.find { it is ProfileInfoUiState.LoadingProgress }!!
            assert(!state.isPublishImageVisible)
            assert(!state.isRetryVisible)
            assert(!state.isShortNameHintVisible)
            assert(!state.isSignOutVisible)
            assert(!state.swipeRefreshState.isEnabled)
            assert(!state.swipeRefreshState.isRefreshing)
        }
    }

    @Test
    fun `profileUiState-initial loading action-check success state properties`() {
        val captor = argumentCaptor<ProfileInfoUiState>()
        val observer = mock<Observer<ProfileInfoUiState>>()
        viewModel.profileUiState.observeForTesting(observer) {
            verify(observer, times(2)).onChanged(captor.capture())
            captor.allValues.findAndCast<ProfileInfoUiState, ProfileInfoUiState.Success>()?.apply {
                assert(isPublishImageVisible)
                assert(!isRetryVisible)
                assert(isShortNameHintVisible)
                assert(isSignOutVisible)
                assert(swipeRefreshState.isEnabled)
                assert(!swipeRefreshState.isRefreshing)
                assert(data == ProfileInfo("Vadim", "Sukharev", "vsukharew"))
            }
        }
    }
}