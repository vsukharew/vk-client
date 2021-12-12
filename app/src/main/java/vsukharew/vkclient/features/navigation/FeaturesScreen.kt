package vsukharew.vkclient.features.navigation

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.github.terrakok.cicerone.androidx.FragmentScreen
import vsukharew.vkclient.features.presentation.FeaturesFragment

class FeaturesScreen : FragmentScreen {
    override fun createFragment(factory: FragmentFactory): Fragment {
        return FeaturesFragment()
    }
}