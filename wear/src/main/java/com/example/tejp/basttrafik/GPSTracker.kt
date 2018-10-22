package com.example.tejp.basttrafik

import android.content.Intent
import android.os.IBinder
import android.os.Bundle
import android.R.string.cancel
import android.app.AlertDialog
import android.app.Service
import android.content.Context
import android.content.DialogInterface
import android.support.v4.content.ContextCompat.startActivity
import android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS
import android.location.LocationManager
import android.content.Context.LOCATION_SERVICE
import android.location.Location
import android.support.v4.content.ContextCompat.getSystemService
import android.location.LocationListener
import android.provider.Settings
import android.util.Log


class GPSTracker(private val mContext: Context) : Service(), LocationListener {
    // flag for GPS status
    private var isGPSEnabled = false
    // flag for network status
    private var isNetworkEnabled = false
    // flag for GPS status
    private var canGetLocation = false
    private var location: Location? = null // location
    private var latitude: Double = 0.toDouble() // latitude
    private var longitude: Double = 0.toDouble() // longitude
    // Declaring a Location Manager
    protected var locationManager: LocationManager? = null

    init {
        getLocation()
    }

    fun getLocation(): Location? {
        try {
            locationManager = mContext
                    .getSystemService(LOCATION_SERVICE) as LocationManager?

            // getting GPS status
            isGPSEnabled = locationManager!!
                    .isProviderEnabled(LocationManager.GPS_PROVIDER)
            // getting network status
            isNetworkEnabled = locationManager!!
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {
                this.canGetLocation = true
                if (isNetworkEnabled) {
                    locationManager!!.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), this)
                    Log.d("Network", "Network")
                    if (locationManager != null) {
                        location = locationManager!!.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                        if (location != null) {
                            latitude = location!!.getLatitude()
                            longitude = location!!.getLongitude()
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager!!.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), this)
                        Log.d(BASTTRAFIK_TAG, "GPS Enabled")
                        if (locationManager != null) {
                            location = locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                            if (location != null) {
                                latitude = location!!.getLatitude()
                                longitude = location!!.getLongitude()
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } catch (e: SecurityException) {
            e.printStackTrace()
        }

        return location
    }

    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     */
    fun stopUsingGPS() {
        if (locationManager != null) {
            locationManager!!.removeUpdates(this@GPSTracker)
        }
    }

    /**
     * Function to get latitude
     */
    fun getLatitude(): Double {
        if (location != null) {
            latitude = location!!.getLatitude()
        }
        // return latitude
        return latitude
    }

    /**
     * Function to get longitude
     */
    fun getLongitude(): Double {
        if (location != null) {
            longitude = location!!.getLongitude()
        }
        // return longitude
        return longitude
    }

    /**
     * Function to check GPS/wifi enabled
     * @return boolean
     */
    fun canGetLocation(): Boolean {
        return this.canGetLocation
    }

    /**
     * Function to show settings alert dialog
     * On pressing Settings button will lauch Settings Options
     */
    fun showSettingsAlert() {
        val alertDialog = AlertDialog.Builder(mContext)
        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings")
        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?")
        // On pressing Settings button
        alertDialog.setPositiveButton("Settings") { dialog, which ->
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            mContext.startActivity(intent)
        }
        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
        // Showing Alert Message
        alertDialog.show()
    }

    override fun onLocationChanged(location: Location) {
        // TODO Auto-generated method stub
    }

    override fun onProviderDisabled(provider: String) {
        // TODO Auto-generated method stub
    }

    override fun onProviderEnabled(provider: String) {
        // TODO Auto-generated method stub

    }

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
        // TODO Auto-generated method stub
    }

    override fun onBind(intent: Intent): IBinder? {
        // TODO Auto-generated method stub
        return null
    }

    companion object {
        // The minimum distance to change Updates in meters
        private val MIN_DISTANCE_CHANGE_FOR_UPDATES: Long = 10 // 10 meters
        // The minimum time between updates in milliseconds
        private val MIN_TIME_BW_UPDATES = (1000 * 60 * 1).toLong() // 1 minute
    }
}