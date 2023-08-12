package vsukharew.vkclient.common.extension

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import vsukharew.vkclient.BuildConfig

fun Intent.systemSettings(): Intent {
    action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
    val uri = Uri.fromParts(
        "package",
        BuildConfig.APPLICATION_ID,
        null
    )
    data = uri
    flags = Intent.FLAG_ACTIVITY_NEW_TASK
    return this
}

fun Intent.locationSettings(): Intent {
    action = Settings.ACTION_LOCATION_SOURCE_SETTINGS
    flags = Intent.FLAG_ACTIVITY_NEW_TASK
    return this
}