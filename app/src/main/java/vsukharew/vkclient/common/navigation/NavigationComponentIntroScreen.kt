package vsukharew.vkclient.common.navigation

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.navigation.fragment.NavHostFragment
import com.github.terrakok.cicerone.androidx.FragmentScreen
import vsukharew.vkclient.R

/**
 * Acts as a starting point for that part of app where navigation is implemented through
 * Android Navigation Component
 */
class NavigationComponentIntroScreen : FragmentScreen {
    override fun createFragment(factory: FragmentFactory): Fragment =
        NavHostFragment.create(R.navigation.navigation_graph)
}