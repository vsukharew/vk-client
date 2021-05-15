package vsukharew.vkclient.common.navigation

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri

abstract class BaseNavigator {

    protected fun isIntentAvailable(
        context: Context,
        packageName: String,
        action: String,
        data: Uri?
    ): Boolean {
        val resolveInfos = context.packageManager?.queryIntentActivities(
            Intent(action, data),
            PackageManager.MATCH_DEFAULT_ONLY
        ) ?: return false
        return resolveInfos.any { it.activityInfo.packageName == packageName }
    }
}