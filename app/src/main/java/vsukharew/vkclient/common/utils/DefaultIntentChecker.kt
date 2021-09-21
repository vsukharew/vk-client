package vsukharew.vkclient.common.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri

class DefaultIntentChecker(private val context: Context) : IntentChecker {
    override fun isIntentAvailable(packageName: String, action: String, data: Uri?): Boolean {
        val resolveInfos = context.packageManager?.queryIntentActivities(
            Intent(action, data),
            PackageManager.MATCH_DEFAULT_ONLY
        ) ?: return false
        return resolveInfos.any { it.activityInfo.packageName == packageName }
    }
}