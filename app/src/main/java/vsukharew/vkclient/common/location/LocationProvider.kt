package vsukharew.vkclient.common.location

interface LocationProvider {
    fun isGpsEnabled(): Boolean
    fun requestCurrentLocation(
        onSuccessListener: (DomainLocation) -> Unit,
        onFailureListener: (Exception) -> Unit
    )
    fun cancelRequest()
}