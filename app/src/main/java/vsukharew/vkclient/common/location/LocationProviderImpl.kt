package vsukharew.vkclient.common.location

import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.location.Location
import android.location.LocationManager
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.Task

class LocationProviderImpl(private val applicationContext: Context) : LocationProvider {
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(applicationContext)
    private val locationManager =
        applicationContext.getSystemService(LOCATION_SERVICE) as LocationManager
    private var cancellationTokenSource = CancellationTokenSource()

    override fun isGpsEnabled(): Boolean =
        locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

    override fun areGooglePlayServicesEnabled(): Boolean {
        val instance = GoogleApiAvailability.getInstance()
        val connectionResult = instance.isGooglePlayServicesAvailable(applicationContext)
        val isAvailable = connectionResult == ConnectionResult.SUCCESS
        return isAvailable || instance.isUserResolvableError(connectionResult)
    }

    override fun requestCurrentLocation(
        onSuccessListener: (DomainLocation) -> Unit,
        onFailureListener: (Exception) -> Unit,
    ) {
        val currentLocationTask: Task<Location> = fusedLocationClient.getCurrentLocation(
            LocationRequest.PRIORITY_HIGH_ACCURACY,
            cancellationTokenSource.token
        )
        currentLocationTask.addOnCompleteListener { task ->
            with(task) {
                if (isSuccessful && result != null) {
                    onSuccessListener.invoke(DomainLocation(result.latitude, result.longitude))
                } else {
                    onFailureListener.invoke(exception ?: LocationNotReceivedException())
                }
            }
        }
    }

    override fun cancelRequest() {
        cancellationTokenSource.cancel()
    }
}