package vsukharew.vkclient.publishimage.caption.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import vsukharew.vkclient.publishimage.caption.presentation.CaptionFragment
import vsukharew.vkclient.publishimage.caption.presentation.CaptionViewModel
import vsukharew.vkclient.publishimage.navigation.PublishImageCoordinator

val captionScreenModule = module {
    scope<CaptionFragment> {
        viewModel { CaptionViewModel(get(), (get() as PublishImageCoordinator).captionStage) }
    }
}