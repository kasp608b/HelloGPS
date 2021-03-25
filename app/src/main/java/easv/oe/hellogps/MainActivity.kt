package easv.oe.hellogps

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.location.LocationManager.*
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    companion object {
        val TAG = "xyz"
    }

    private val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                                      Manifest.permission.ACCESS_COARSE_LOCATION)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestPermissions()
    }

    private fun requestPermissions() {
        if (!isPermissionGiven()) {
            Log.d(TAG, "permission denied to USE GPS - requesting it")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                requestPermissions(permissions, 1)
            return
        } else
            Log.d(TAG, "permission to USE GPS granted!")
    }

    private fun isPermissionGiven(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return permissions.all { p -> checkSelfPermission(p) == PackageManager.PERMISSION_GRANTED}
        }
        return true
    }

    @SuppressLint("MissingPermission")
    fun onClickGetLocation(view: View) {
        if (!isPermissionGiven()) {
            tvCurrentLocation.text = "No permission given"
            return
        }
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val location = locationManager.getLastKnownLocation(GPS_PROVIDER)
        if (location != null) {
            tvCurrentLocation.text = "Location = ${location.latitude}, ${location.longitude}"
        } else
            tvCurrentLocation.text = "Location = null"
    }

    var myLocationListener: LocationListener? = null

    fun onClickListeningSwitch(view: View) {
        if (swListening.isChecked())
            startListening()
        else
            stopListening()
    }

    @SuppressLint("MissingPermission")
    private fun startListening() {
        if (!isPermissionGiven())
            return

        if (myLocationListener == null)
            myLocationListener = object : LocationListener {
                var count: Int = 0

                override fun onLocationChanged(location: Location) {
                    count++
                    Log.d(TAG, "Location changed")
                    tvCurrentLocation.text = "Location = ${location.latitude}, ${location.longitude}"
                    counter.text = "Count = $count"
                }
            }

        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.requestLocationUpdates(GPS_PROVIDER,
                1000,
                5.0F,
                myLocationListener!!)

    }

    private fun stopListening() {

        if (myLocationListener == null) return

        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.removeUpdates(myLocationListener!!)
    }

    override fun onStop(){
        stopListening()
        super.onStop()
    }


}