package vsukharew.vkclient.common.utils

import android.net.Uri

interface IntentChecker {
    fun isIntentAvailable(packageName: String, action: String, data: Uri? = null): Boolean
}