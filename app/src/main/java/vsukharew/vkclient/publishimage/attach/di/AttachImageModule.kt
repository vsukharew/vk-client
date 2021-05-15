package vsukharew.vkclient.publishimage.attach.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import vsukharew.vkclient.publishimage.attach.presentation.AttachImageFragment
import vsukharew.vkclient.publishimage.attach.presentation.AttachImageViewModel
import vsukharew.vkclient.publishimage.navigation.PublishImageCoordinator

val attachImageScreenModule = module {
    scope<AttachImageFragment> {
        viewModel {
            AttachImageViewModel(
                get(),
                get(),
                (get() as PublishImageCoordinator).attachImageStage,
                get()
            )
        }
    }
}