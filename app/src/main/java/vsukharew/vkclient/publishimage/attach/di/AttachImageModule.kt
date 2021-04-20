package vsukharew.vkclient.publishimage.attach.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import org.koin.experimental.builder.scopedBy
import retrofit2.Retrofit
import vsukharew.vkclient.publishimage.attach.data.ImageRepo
import vsukharew.vkclient.publishimage.attach.data.ImageRepository
import vsukharew.vkclient.publishimage.attach.data.network.ImageApi
import vsukharew.vkclient.publishimage.attach.domain.interactor.ImageInteractor
import vsukharew.vkclient.publishimage.attach.domain.interactor.ImageInteractorImpl
import vsukharew.vkclient.publishimage.attach.presentation.AttachImageFragment
import vsukharew.vkclient.publishimage.attach.presentation.AttachImageViewModel

private fun provideImageApi(retrofit: Retrofit): ImageApi {
    return retrofit.create(ImageApi::class.java)
}

val attachImageScreenModule = module {
    scope<AttachImageFragment> {
        scoped { provideImageApi(get()) }
        scopedBy<ImageRepo, ImageRepository>()
        scopedBy<ImageInteractor, ImageInteractorImpl>()
        viewModel { AttachImageViewModel(get()) }
    }
}