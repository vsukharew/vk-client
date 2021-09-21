package vsukharew.vkclient.splash.navigation

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.github.terrakok.cicerone.androidx.FragmentScreen
import vsukharew.vkclient.splash.presentation.SplashFragment

class SplashScreen : FragmentScreen {
    override fun createFragment(factory: FragmentFactory): Fragment = SplashFragment()
}