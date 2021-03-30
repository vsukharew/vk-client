package vsukharew.vkclient

import android.content.Intent
import androidx.navigation.NavOptions
import vsukharew.vkclient.auth.presentation.ChromeTabsResponseListener
import vsukharew.vkclient.common.delegation.activityViewBinding
import vsukharew.vkclient.common.presentation.BaseActivity
import vsukharew.vkclient.databinding.ActivityMainBinding

class MainActivity : BaseActivity() {
    override val binding by activityViewBinding(ActivityMainBinding::inflate)

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        supportFragmentManager.primaryNavigationFragment
            ?.childFragmentManager
            ?.fragments
            ?.firstOrNull()
            ?.let { fragment ->
                if (fragment is ChromeTabsResponseListener) {
                    intent?.let { fragment.onResponse(it) }
                }
            }
    }
}