package vsukharew.vkclient

import android.content.Intent
import android.os.Bundle
import org.koin.android.scope.AndroidScopeComponent
import org.koin.androidx.scope.activityRetainedScope
import org.koin.core.scope.Scope
import vsukharew.vkclient.common.delegation.activityViewBinding
import vsukharew.vkclient.common.presentation.BaseActivity
import vsukharew.vkclient.databinding.ActivityMainBinding

class MainActivity : BaseActivity(), AndroidScopeComponent {
    override val binding by activityViewBinding(ActivityMainBinding::inflate)
    override val scope: Scope by activityRetainedScope()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            title = getString(
                when (destination.id) {
                    R.id.authFragment -> R.string.auth_fragment_title
                    else -> R.string.app_name
                }
            )
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        navController.handleDeepLink(intent)
    }
}