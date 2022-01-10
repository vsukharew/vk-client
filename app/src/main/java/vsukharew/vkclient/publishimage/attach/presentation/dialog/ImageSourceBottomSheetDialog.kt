package vsukharew.vkclient.publishimage.attach.presentation.dialog

import android.os.Bundle
import android.view.View
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import org.koin.core.scope.Scope
import vsukharew.vkclient.R
import vsukharew.vkclient.common.delegation.fragmentViewBinding
import vsukharew.vkclient.common.di.ScopeManager
import vsukharew.vkclient.common.presentation.BaseBottomSheetDialog
import vsukharew.vkclient.common.presentation.BaseFlowFragment
import vsukharew.vkclient.common.presentation.BaseFragment
import vsukharew.vkclient.databinding.DialogChooseImageSourceBinding
import vsukharew.vkclient.publishimage.attach.domain.model.ImageSource
import vsukharew.vkclient.publishimage.attach.domain.model.ImageSource.CAMERA
import vsukharew.vkclient.publishimage.attach.domain.model.ImageSource.GALLERY

class ImageSourceBottomSheetDialog : BaseBottomSheetDialog() {

    override val layoutResId: Int = R.layout.dialog_choose_image_source
    override val binding by fragmentViewBinding(DialogChooseImageSourceBinding::bind)
    override val parentScopes: ScopeManager.() -> Array<Scope> = {
        arrayOf((requireParentFragment().requireParentFragment() as BaseFragment<*>).scope)
    }
    private val imageSourceListener: ImageSourceListener by inject {
        val flowFragment = ((requireParentFragment().requireParentFragment()) as BaseFlowFragment<*>)
        parametersOf(
            flowFragment.navController,
            flowFragment.flowNavController
        )
    }

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