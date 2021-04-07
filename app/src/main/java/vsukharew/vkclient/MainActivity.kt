package vsukharew.vkclient

import android.content.Intent
import org.koin.android.scope.AndroidScopeComponent
import org.koin.androidx.scope.activityRetainedScope
import org.koin.core.scope.Scope
import vsukharew.vkclient.common.delegation.activityViewBinding
import vsukharew.vkclient.common.presentation.BaseActivity
import vsukharew.vkclient.databinding.ActivityMainBinding

class MainActivity : BaseActivity(), AndroidScopeComponent {
    override val binding by activityViewBinding(ActivityMainBinding::inflate)
    override val scope: Scope by activityRetainedScope()

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        navController.handleDeepLink(intent)
    }
}