package vsukharew.vkclient.publishimage.attach.di

import org.koin.dsl.module
import org.koin.experimental.builder.scopedBy
import vsukharew.vkclient.publishimage.attach.presentation.dialog.ImageSourceBottomSheetDialog
import vsukharew.vkclient.publishimage.attach.presentation.dialog.ImageSourceBottomSheetDialog.ImageSourceListener
import vsukharew.vkclient.publishimage.navigation.PublishImageCoordinator

val chooseImageSourceDialogModule = module {
    scope<ImageSourceBottomSheetDialog> {
        scopedBy<ImageSourceListener, PublishImageCoordinator>()
    }
}