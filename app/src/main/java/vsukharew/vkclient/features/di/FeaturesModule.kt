package vsukharew.vkclient.features.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import vsukharew.vkclient.features.navigation.FeaturesCoordinator
import vsukharew.vkclient.features.navigation.FeaturesNavigator
import vsukharew.vkclient.features.presentation.FeaturesFlowFragment
import vsukharew.vkclient.features.presentation.FeaturesViewModel

val featuresScreenModule = module {
    scope<FeaturesFlowFragment> {
        scoped { FeaturesNavigator() }
        scoped { FeaturesCoordinator(get()) }
        viewModel { FeaturesViewModel(get(), get(), get()) }
    }
}