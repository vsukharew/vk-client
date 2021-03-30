package vsukharew.vkclient.auth.presentation

import android.content.Intent

/**
 * Interface tracking the data that come from Chrome Tabs
 */
interface ChromeTabsResponseListener {
    fun onResponse(intent: Intent)
}