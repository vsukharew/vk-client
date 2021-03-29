package vsukharew.vkclient.auth.domain.model

enum class Scope {
    PHOTOS,
    WALL;

    companion object {
        fun namesLowerCase(): List<String> = values().map { it.name }
    }
}