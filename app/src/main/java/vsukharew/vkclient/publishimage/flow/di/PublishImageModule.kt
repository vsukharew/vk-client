package vsukharew.vkclient.publishimage.flow.di

import org.koin.androidx.experimental.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.experimental.builder.scopedBy
import retrofit2.Retrofit
import vsukharew.vkclient.common.di.DIScopes
import vsukharew.vkclient.publishimage.attach.data.ImageRepo
import vsukharew.vkclient.publishimage.attach.data.ImageRepository
import vsukharew.vkclient.publishimage.attach.data.network.ImageApi
import vsukharew.vkclient.publishimage.attach.data.network.WallApi
import vsukharew.vkclient.publishimage.attach.domain.entity.CheckUploadedImageResolution
import vsukharew.vkclient.publishimage.attach.domain.entity.CheckUploadedImageSize
import vsukharew.vkclient.publishimage.attach.domain.infrastructure.ContentResolverImpl
import vsukharew.vkclient.publishimage.attach.domain.infrastructure.DomainContentResolver
import vsukharew.vkclient.publishimage.attach.domain.interactor.ImageInteractor
import vsukharew.vkclient.publishimage.attach.domain.interactor.ImageInteractorImpl
import vsukharew.vkclient.publishimage.flow.PublishImageFragment
import vsukharew.vkclient.publishimage.flow.PublishImageViewModel
import vsukharew.vkclient.publishimage.navigation.PublishImageCoordinator
import vsukharew.vkclient.publishimage.navigation.PublishImageNavigator

private fun provideImageApi(retrofit: Retrofit): ImageApi {
    return retrofit.create(ImageApi::class.java)
}

private fun provideWallApi(retrofit: Retrofit): WallApi {
    return retrofit.create(WallApi::class.java)
}

val publishImageFlowModule = module {
    scope(named(DIScopes.PUBLISHING_POST_DATA)) {
        scoped { provideImageApi(get()) }
        scoped { provideWallApi(get()) }
        scoped { CheckUploadedImageSize(get()) }
        scoped { CheckUploadedImageResolution(get()) }
        scopedBy<DomainContentResolver, ContentResolverImpl>()
        scopedBy<ImageRepo, ImageRepository>()
        scopedBy<ImageInteractor, ImageInteractorImpl>()
    }

    scope<PublishImageFragment> {
        scoped { PublishImageNavigator() }
        scoped { PublishImageCoordinator(get()) }
        viewModel<PublishImageViewModel>()
    }
}