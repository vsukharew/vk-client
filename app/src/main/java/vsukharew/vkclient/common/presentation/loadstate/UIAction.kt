package vsukharew.vkclient.common.presentation.loadstate

sealed class UIAction {
    object InitialLoading : UIAction()
    object Click : UIAction()
    object SwipeRefresh : UIAction()
    object Retry : UIAction()
    data class Text(val text: String) : UIAction()
}