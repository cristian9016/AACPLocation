package com.example.personal.aaptllocation

import android.Manifest
import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.annotation.SuppressLint
import android.location.Location
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.support.annotation.RequiresApi
import com.google.android.gms.location.*
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.toast

class MainActivity : AppCompatActivity(), GetAddressTask.OnTaskCompleted {

    lateinit var permissions: RxPermissions
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var anim: AnimatorSet
    var trackingLocation = false
    lateinit var locationCallback: LocationCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState != null) trackingLocation = savedInstanceState.getBoolean(TRACKING_LOCATION_KEY)

        permissions = RxPermissions(this)
        anim = AnimatorInflater.loadAnimator(this, R.animator.rotate) as AnimatorSet
        anim.setTarget(imageview_android)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        button_location.setOnClickListener {
            permissions.request(Manifest.permission.ACCESS_FINE_LOCATION)
                    .subscribe {
                        if (it) {
                            if (!trackingLocation) startTrackingLocation()
                            else stopTrackingLocation()
                        } else toast("acceso denegado")
                    }
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult?) {
                super.onLocationResult(p0)
                if (trackingLocation) {
                    GetAddressTask(this@MainActivity, this@MainActivity).execute(p0!!.lastLocation)
                }
            }
        }

        if(trackingLocation) startTrackingLocation()
        else stopTrackingLocation()

    }

    @SuppressLint("MissingPermission")
    fun startTrackingLocation() {
        fusedLocationProviderClient.requestLocationUpdates(getLocationRequest(), locationCallback, null)
        textview_location.text = "Adquiriendo Direccion..."
        anim.start()
        trackingLocation = true
        button_location.text = getString(R.string.stop_location)
    }

    fun stopTrackingLocation() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        trackingLocation = false
        button_location.text = getString(R.string.get_location)
        textview_location.text = getString(R.string.no_location)
        anim.end()

    }

    fun getLocationRequest(): LocationRequest =
            LocationRequest()
                    .setInterval(10000)
                    .setFastestInterval(5000)
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)


    override fun OnTaskCompleted(result: String) {
        if (trackingLocation) textview_location.text = getString(R.string.direccion, result, System.currentTimeMillis())
    }


    override fun onSaveInstanceState(outState: Bundle?) {
        outState!!.putBoolean(TRACKING_LOCATION_KEY, trackingLocation)
        super.onSaveInstanceState(outState)
    }

    companion object {
        const val TRACKING_LOCATION_KEY = "tracking_location_key"
    }
}
