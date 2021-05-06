package vsukharew.vkclient.common.network

object ServerUrls {
    const val BASE_URL = "https://api.vk.com/method/"

    object Auth {
        const val BASE_URL = "https://oauth.vk.com/authorize"
        const val REDIRECT_URL = "https://oauth.vk.com/blank.html"
    }

    object Account {
        const val GET_PROFILE = "account.getProfileInfo"
        const val RESOLVE_SCREEN_NAME = "utils.resolveScreenName"
    }

    object Image {
        const val UPLOAD_ADDRESS_WALL = "photos.getWallUploadServer"
        const val SAVE_IMAGE_WALL = "photos.saveWallPhoto"
    }

    object Wall {
        const val POST_TO_USER_WALL = "wall.post"
    }
}