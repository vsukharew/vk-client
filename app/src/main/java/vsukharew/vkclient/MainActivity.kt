package vsukharew.vkclient

import android.content.Intent
import androidx.navigation.NavOptions
import vsukharew.vkclient.common.delegation.activityViewBinding
import vsukharew.vkclient.common.presentation.BaseActivity
import vsukharew.vkclient.databinding.ActivityMainBinding

class MainActivity : BaseActivity() {
    override val binding by activityViewBinding(ActivityMainBinding::inflate)

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        navController.navigate(
            R.id.features_graph, null,
            NavOptions.Builder()
                .setPopUpTo(R.id.authFragment, true)
                .build()
        )
    }
}