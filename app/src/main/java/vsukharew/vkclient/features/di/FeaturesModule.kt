package vsukharew.vkclient.features.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import vsukharew.vkclient.auth.navigation.AuthNavigator
import vsukharew.vkclient.features.presentation.FeaturesFlowFragment
import vsukharew.vkclient.features.presentation.FeaturesViewModel

val featuresScreenModule = module {
    scope<FeaturesFlowFragment> {
        viewModel { FeaturesViewModel(get()) }
    }
}