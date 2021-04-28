package vsukharew.vkclient.publishimage.attach.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import org.koin.experimental.builder.scopedBy
import vsukharew.vkclient.publishimage.attach.domain.infrastructure.AndroidUriProvider
import vsukharew.vkclient.publishimage.attach.domain.infrastructure.UriProvider
import vsukharew.vkclient.publishimage.attach.presentation.AttachImageFragment
import vsukharew.vkclient.publishimage.attach.presentation.AttachImageViewModel

val attachImageScreenModule = module {
    scope<AttachImageFragment> {
        scopedBy<UriProvider, AndroidUriProvider>()
        viewModel { AttachImageViewModel(get(), get()) }
    }
}