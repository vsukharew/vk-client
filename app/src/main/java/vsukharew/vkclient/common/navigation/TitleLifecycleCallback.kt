package vsukharew.vkclient.common.navigation

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.NavHostFragment
import vsukharew.vkclient.R
import vsukharew.vkclient.auth.presentation.AuthFragment
import vsukharew.vkclient.common.presentation.BaseBottomSheetDialog
import vsukharew.vkclient.common.presentation.BaseFlowFragment
import vsukharew.vkclient.publishimage.attach.presentation.AttachImageFragment
import vsukharew.vkclient.publishimage.caption.presentation.CaptionFragment

class TitleLifecycleCallback(
    val activity: Activity
) : FragmentManager.FragmentLifecycleCallbacks() {
    override fun onFragmentViewCreated(
        fm: FragmentManager,
        f: Fragment,
        v: View,
        savedInstanceState: Bundle?
    ) {
        activity.apply {
            title = getString(
                when (f) {
                    is NavHostFragment,
                    is BaseFlowFragment<*>,
                    is BaseBottomSheetDialog -> return
                    is AuthFragment -> R.string.auth_fragment_title
                    is AttachImageFragment -> R.string.attach_image_fragment_title
                    is CaptionFragment -> R.string.caption_fragment_title
                    else -> R.string.app_name
                }
            )
        }
    }
}