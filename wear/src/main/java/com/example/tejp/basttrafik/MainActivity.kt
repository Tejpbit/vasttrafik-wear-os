package com.example.tejp.basttrafik

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.provider.Contacts
import android.provider.Contacts.Intents.UI
import android.support.wearable.activity.WearableActivity
import android.util.Log
import android.widget.ListView
import khttp.responses.Response
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import android.location.LocationManager
import android.content.Context.LOCATION_SERVICE
import android.support.v4.content.ContextCompat.getSystemService
import android.Manifest.permission
import android.Manifest.permission.READ_CONTACTS
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.pm.PackageManager
import android.location.Location
import android.support.v4.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import android.support.v4.app.ActivityCompat




val BASTTRAFIK_TAG = "basttrafik"

class MainActivity : WearableActivity() {

    private val INITIAL_PERMS = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
    private val INITIAL_PERMS_CODE = 1337
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val view: ListView = findViewById(R.id.recycler_launcher_view)

        val listOfDepartures = mutableListOf(
                Departure(123L, "16", "Eketrägatan", Date()),
                Departure(123L, "8", "Angered", Date())
        )


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        //requestPermissions(INITIAL_PERMS, INITIAL_PERMS_CODE)

        val lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(BASTTRAFIK_TAG, "NOT allowed to access location.")
            //this.finishAffinity()
        } else {
            Log.d(BASTTRAFIK_TAG, "Allowed to access location.")
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 10)

            fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        Log.d(BASTTRAFIK_TAG, "My current location: ${location.toString()}")
                    }
        }




        val adapter = ListViewAdapter(this, listOfDepartures)

        launch {
            val vasttrafikService = VasttrafikService()
            vasttrafikService.getAccessToken()
            val nearbyStops = vasttrafikService.getNearbyStops(57.669950, 11.937100)
            Log.d(BASTTRAFIK_TAG, nearbyStops.toString())

            var stops = mutableListOf<Departure>()
            for (i in 0 until nearbyStops.length()) {
                val stop = nearbyStops.getJSONObject(i)
                stops.add(Departure(123L, stop["name"].toString(), stop["name"].toString(), Date()))

            }


            launch(UI) {
                listOfDepartures.clear()
                listOfDepartures.addAll(stops)
                adapter.notifyDataSetChanged()
            }
        }








        view.adapter = adapter


        // Enables Always-on
        //setAmbientEnabled()
    }

}

class VasttrafikService() {

    var accessToken: String? = null
    var tokenExpiresIn: Int? = null

    fun getAccessToken() {

        val resp: Response = khttp.get(
                url = "https://api.vasttrafik.se:443/token",
                data = mapOf("grant_type" to "client_credentials"),
                headers = mapOf("Authorization" to "Basic Yzc4T1FQREhOZHZXZUt3Rk9RY0V4WUFSRDFVYTo2V2pkTWtMWjJ0TkpUQm9oT051RmFxdVdCWjhh")
        )
        Log.d(BASTTRAFIK_TAG, "Get access token: ${resp.statusCode}")
        Log.d(BASTTRAFIK_TAG, resp.text)
        this.accessToken = resp.jsonObject["access_token"] as String
        this.tokenExpiresIn = resp.jsonObject["expires_in"] as Int
    }

    fun getNearbyStops(lat: Double, long: Double): JSONArray {
        val locationResp = khttp.get(
                url = "https://api.vasttrafik.se/bin/rest.exe/v2/location.nearbystops",
                params = mapOf(
                        "format" to "json",
                        "originCoordLat" to lat.toString(),
                        "originCoordLong" to long.toString()),
                headers = mapOf("Authorization" to "Bearer $accessToken")
        )



        Log.d(BASTTRAFIK_TAG, "Nearest station ${locationResp.text}")

        val locList = locationResp.jsonObject.get("LocationList") as JSONObject
        return locList.getJSONArray("StopLocation")
    }
}

