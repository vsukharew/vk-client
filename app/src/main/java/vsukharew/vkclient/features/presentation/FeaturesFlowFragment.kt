package vsukharew.vkclient.features.presentation

import org.koin.java.KoinJavaComponent.getKoin
import android.os.Bundle
import android.view.View
import org.koin.androidx.viewmodel.ext.android.viewModel
import vsukharew.vkclient.R
import vsukharew.vkclient.account.domain.model.ProfileInfo
import vsukharew.vkclient.common.delegation.fragmentViewBinding
import vsukharew.vkclient.common.di.ScopeCreator
import vsukharew.vkclient.common.domain.model.Result
import vsukharew.vkclient.common.extension.EMPTY
import vsukharew.vkclient.common.extension.toast
import vsukharew.vkclient.common.livedata.SingleLiveEvent
import vsukharew.vkclient.common.presentation.BaseFragment
import vsukharew.vkclient.databinding.FragmentFeaturesBinding
import vsukharew.vkclient.features.di.FeaturesScopeCreator

class FeaturesFlowFragment : BaseFragment<FragmentFeaturesBinding>(R.layout.fragment_features) {
    private val viewModel: FeaturesViewModel by viewModel()

    override val binding by fragmentViewBinding(FragmentFeaturesBinding::bind)
    override val scopeCreator: ScopeCreator = FeaturesScopeCreator(this, getKoin())

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.apply {
            profileInfo.observe(viewLifecycleOwner, ::observeProfileInfo)
            isLoading.observe(viewLifecycleOwner, ::observeLoading)
        }
    }

    private fun observeProfileInfo(event: SingleLiveEvent<Result<ProfileInfo>>) {
        with(event) {
            when (peekContent) {
                is Result.Success -> {
                    binding.userName.text =
                        getString(
                            R.string.features_fragment_user_name_text,
                            "${peekContent.data.firstName} ${peekContent.data.lastName}"
                        )
                }
                else -> {
                    if (!isHandled) {
                        toast("error").also { isHandled = true }
                    }
                }
            }
        }
    }

    private fun observeLoading(isLoading: Boolean) {
        binding.userName.apply {
            text = if (isLoading) {
                getString(R.string.features_fragment_user_name_loading_text)
            } else {
                String.EMPTY
            }
        }
    }
}