package vsukharew.vkclient.auth.navigation

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.github.terrakok.cicerone.androidx.ActivityScreen

class BrowserAuthScreen(private val url: String) : ActivityScreen {
    override fun createIntent(context: Context): Intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
}