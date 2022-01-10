package vsukharew.vkclient.features.di

import androidx.navigation.NavController
import kotlinx.coroutines.FlowPreview
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import vsukharew.vkclient.features.navigation.FeaturesCoordinator
import vsukharew.vkclient.features.presentation.FeaturesFragment
import vsukharew.vkclient.features.presentation.FeaturesViewModel

@FlowPreview
val featuresScreenModule = module {
    scope<FeaturesFragment> {
        scoped { (navController: NavController) -> FeaturesCoordinator(navController) }
        viewModel { FeaturesViewModel(get(), get(), get(), get(), get()) }
    }
}