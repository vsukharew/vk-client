package vsukharew.vkclient.features.di

import kotlinx.coroutines.FlowPreview
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import vsukharew.vkclient.features.FeaturesFlowFragment
import vsukharew.vkclient.features.FeaturesFlowViewModel
import vsukharew.vkclient.features.navigation.FeaturesCoordinator
import vsukharew.vkclient.features.navigation.FeaturesNavigator
import vsukharew.vkclient.features.presentation.FeaturesFragment
import vsukharew.vkclient.features.presentation.FeaturesViewModel

val featuresFlowModule = module {
    scope<FeaturesFlowFragment> {
        scoped { FeaturesFlowViewModel() }
    }
}

@FlowPreview
val featuresScreenModule = module {
    scope<FeaturesFragment> {
        scoped { FeaturesNavigator() }
        scoped { FeaturesCoordinator(get()) }
        viewModel { FeaturesViewModel(get(), get(), get(), get(), get()) }
    }
}