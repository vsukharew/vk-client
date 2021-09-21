package vsukharew.vkclient.auth.navigation

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.github.terrakok.cicerone.androidx.FragmentScreen
import vsukharew.vkclient.auth.presentation.AuthFragment

class AuthScreen : FragmentScreen {
    override fun createFragment(factory: FragmentFactory): Fragment {
        return AuthFragment()
    }
}