package vsukharew.vkclient.common.network

object ServerUrls {
    const val BASE_URL = "https://api.vk.com/method/"

    object Auth {
        const val BASE_URL = "https://oauth.vk.com/authorize"
        const val REDIRECT_URL = "https://oauth.vk.com/blank.html"
    }

    object Account {
        const val GET_PROFILE = "account.getProfileInfo"
    }
}