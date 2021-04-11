package vsukharew.vkclient.features.di

import org.koin.dsl.module
import vsukharew.vkclient.auth.navigation.AuthNavigator
import vsukharew.vkclient.features.presentation.FeaturesFlowFragment

val featuresScreenModule = module {
    scope<FeaturesFlowFragment> {
        scoped { AuthNavigator() }
    }
}