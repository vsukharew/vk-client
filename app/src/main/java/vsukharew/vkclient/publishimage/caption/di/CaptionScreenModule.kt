package vsukharew.vkclient.publishimage.caption.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import org.koin.experimental.builder.scopedBy
import vsukharew.vkclient.common.location.LocationProvider
import vsukharew.vkclient.common.location.LocationProviderImpl
import vsukharew.vkclient.publishimage.caption.presentation.CaptionFragment
import vsukharew.vkclient.publishimage.caption.presentation.CaptionViewModel
import vsukharew.vkclient.publishimage.navigation.PublishImageCoordinator

val captionScreenModule = module {
    scope<CaptionFragment> {
        scopedBy<LocationProvider, LocationProviderImpl>()
        viewModel { CaptionViewModel(get(), get(), (get() as PublishImageCoordinator).captionStage) }
    }
}