package vsukharew.vkclient.publishimage.attach.presentation.dialog

import android.os.Bundle
import android.view.View
import org.koin.android.ext.android.inject
import vsukharew.vkclient.R
import vsukharew.vkclient.common.delegation.fragmentViewBinding
import vsukharew.vkclient.common.presentation.BaseBottomSheetDialog
import vsukharew.vkclient.databinding.DialogChooseImageSourceBinding
import vsukharew.vkclient.publishimage.attach.di.ImageSourceDialogScopeCreator
import vsukharew.vkclient.publishimage.attach.domain.model.ImageSource
import vsukharew.vkclient.publishimage.attach.domain.model.ImageSource.CAMERA
import vsukharew.vkclient.publishimage.attach.domain.model.ImageSource.GALLERY

class ImageSourceBottomSheetDialog : BaseBottomSheetDialog() {

    override val layoutResId: Int = R.layout.dialog_choose_image_source
    override val binding by fragmentViewBinding(DialogChooseImageSourceBinding::bind)
    override val scopeCreator by lazy {
        ImageSourceDialogScopeCreator(requireParentFragment().requireParentFragment())
    }
    private val imageSourceListener: ImageSourceListener by inject()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            camera.setOnClickListener {
                imageSourceListener.onSourceChoose(CAMERA)
                dismissAllowingStateLoss()
            }
            gallery.setOnClickListener {
                imageSourceListener.onSourceChoose(GALLERY)
                dismissAllowingStateLoss()
            }
        }
    }

    interface ImageSourceListener {
        fun onSourceChoose(source: ImageSource)
    }

    companion object {
        const val KEY_IMAGE_SOURCE = "image_source"
    }
}