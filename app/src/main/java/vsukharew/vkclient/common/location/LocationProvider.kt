package vsukharew.vkclient.common.location

interface LocationProvider {
    fun isGpsEnabled(): Boolean
    fun areGooglePlayServicesEnabled(): Boolean
    fun requestCurrentLocation(
        onSuccessListener: (DomainLocation) -> Unit,
        onFailureListener: (Exception) -> Unit
    )
    fun cancelRequest()
}