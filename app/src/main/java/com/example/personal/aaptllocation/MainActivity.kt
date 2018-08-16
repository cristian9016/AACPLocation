package com.example.personal.aaptllocation

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.toast

class MainActivity : AppCompatActivity(),GetAddressTask.OnTaskCompleted {

    lateinit var permissions :RxPermissions
    lateinit var location: Location
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        permissions = RxPermissions(this)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        button_location.setOnClickListener {
            permissions.request(Manifest.permission.ACCESS_FINE_LOCATION)
                    .subscribe {
                        if (it) {
                            getLocation()
                        } else {
                            toast("acceso denegado")
                        }
                    }
        }

    }

    @SuppressLint("MissingPermission")
    fun getLocation() {
        fusedLocationProviderClient.lastLocation.addOnSuccessListener {
            if (it != null) {
                GetAddressTask(this,this).execute(it)
                textview_location.text = "Adquiriendo Direccion..."
            }

        }
    }

    override fun OnTaskCompleted(result: String) {
        textview_location.text = getString(R.string.direccion,result,System.currentTimeMillis())
    }
}
