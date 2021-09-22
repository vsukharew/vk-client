package vsukharew.vkclient

import android.content.Intent
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import com.github.terrakok.cicerone.Command
import com.github.terrakok.cicerone.NavigatorHolder
import com.github.terrakok.cicerone.Replace
import com.github.terrakok.cicerone.androidx.AppNavigator
import org.koin.android.ext.android.getKoin
import org.koin.android.scope.AndroidScopeComponent
import org.koin.androidx.scope.activityRetainedScope
import org.koin.core.scope.Scope
import vsukharew.vkclient.common.delegation.activityViewBinding
import vsukharew.vkclient.common.presentation.BaseActivity
import vsukharew.vkclient.databinding.ActivityMainBinding
import vsukharew.vkclient.common.navigation.NavigationComponentIntroScreen
import vsukharew.vkclient.splash.navigation.SplashScreen

class MainActivity : BaseActivity(), AndroidScopeComponent {
    override val binding by activityViewBinding(ActivityMainBinding::inflate)
    override val scope: Scope by activityRetainedScope()
    private val navigator = object : AppNavigator(this, R.id.fragment_container_view) {
        override fun applyCommands(commands: Array<out Command>) {
            val isNavHostNotInStack = fragmentManager.fragments.firstOrNull() !is NavHostFragment
            val isGoingToFeaturesScreen = commands.firstOrNull()?.let {
                it is Replace && it.screen is NavigationComponentIntroScreen
            }
            if (isNavHostNotInStack || isGoingToFeaturesScreen == true) {
                super.applyCommands(commands)
            }
        }
    }
    private val navigatorHolder = getKoin().get<NavigatorHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navigator.applyCommands(arrayOf(Replace(SplashScreen())))
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
    }

    override fun onResume() {
        super.onResume()
        navigatorHolder.setNavigator(navigator)
    }

    override fun onPause() {
        navigatorHolder.removeNavigator()
        super.onPause()
    }
}