package vsukharew.vkclient.features

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.navigation.fragment.NavHostFragment
import com.github.terrakok.cicerone.androidx.FragmentScreen
import vsukharew.vkclient.R

class FeaturesFlowScreen : FragmentScreen {
    override fun createFragment(factory: FragmentFactory): Fragment = NavHostFragment.create(R.navigation.navigation_graph)
}