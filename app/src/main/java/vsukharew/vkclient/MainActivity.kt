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
import vsukharew.vkclient.auth.navigation.AuthScreen
import vsukharew.vkclient.common.delegation.activityViewBinding
import vsukharew.vkclient.common.navigation.AuthScreenDeepLinkEndPoint
import vsukharew.vkclient.common.navigation.NavigationComponentIntroScreen
import vsukharew.vkclient.common.presentation.BaseActivity
import vsukharew.vkclient.databinding.ActivityMainBinding
import vsukharew.vkclient.common.navigation.TitleLifecycleCallback
import vsukharew.vkclient.splash.navigation.SplashScreen

class MainActivity : BaseActivity(), AndroidScopeComponent {
    private val navigator = object : AppNavigator(this, R.id.fragment_container_view) {
        override fun applyCommands(commands: Array<out Command>) {
            // as soon as NavHostFragment is in stack, one have to pass navigation management to
            // Navigation Component and stop applying Cicerone commands
            val isNavHostNotInStack = fragmentManager.fragments.firstOrNull() !is NavHostFragment

            // dirty workaround for applying Cicerone commands during return from external activity
            // where the authorization goes
            val isGoingToFeaturesScreen =
                commands.firstOrNull()?.isGoingTo<NavigationComponentIntroScreen>()

            // dirty workaround for applying Cicerone commands during log out and returning
            // to the sign in screen
            val isNewRootChain = commands.lastOrNull()?.isGoingTo<AuthScreen>()
            if (isNavHostNotInStack || isGoingToFeaturesScreen == true || isNewRootChain == true) {
                super.applyCommands(commands)
            }
        }
    }
    private val navigatorHolder = getKoin().get<NavigatorHolder>()

    override val binding by activityViewBinding(ActivityMainBinding::inflate)
    override val scope: Scope by activityRetainedScope()

    override fun onCreate(savedInstanceState: Bundle?) {
        supportFragmentManager.registerFragmentLifecycleCallbacks(
            TitleLifecycleCallback(this), true
        )
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            deepLinkEndPoints.add(
                AuthScreenDeepLinkEndPoint(supportFragmentManager, AuthScreen())
            )
        }
        navigator.applyCommands(arrayOf(Replace(SplashScreen())))
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        this.intent = intent
        intent?.let {
            deepLinkEndPoints.find { point ->
                intent.dataString?.let { point.pattern.matches(it) } ?: false
            }?.blockToRunIfPatternMatches?.invoke()
        }
    }

    override fun onResume() {
        super.onResume()
        navigatorHolder.setNavigator(navigator)
    }

    override fun onPause() {
        navigatorHolder.removeNavigator()
        super.onPause()
    }

    private inline fun <reified T> Command.isGoingTo(): Boolean =
        this is Replace && this.screen is T
}