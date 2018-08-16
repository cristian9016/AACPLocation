package com.example.personal.aaptllocation

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.AsyncTask
import android.text.TextUtils
import android.util.Log
import java.io.IOException
import java.util.*

class GetAddressTask(var context: Context,var listener:OnTaskCompleted):AsyncTask<Location,Unit,String>() {

    override fun doInBackground(vararg p0: Location?): String {
        val geocoder = Geocoder(context, Locale.getDefault())
        val location = p0[0]
        var list = mutableListOf<Address>()
        var resultAddress = ""
        var addressParts = arrayListOf<String>()
        try {
            list = geocoder.getFromLocation(location!!.latitude,location!!.longitude,1)
            resultAddress = if(list.isEmpty() || list==null) "No se encontraron Direcciones"
            else{
                val address = list[0]
                for(i in 0..address.maxAddressLineIndex){
                    addressParts.add(address.getAddressLine(i))
                }
                TextUtils.join("\n",addressParts)
            }
        }catch (ioException:IOException){
            resultAddress = "Coordenadas Invalidas"
            Log.e("Servicio no Disponible",resultAddress)
        }catch (illegalArgument:IllegalArgumentException){
            Log.e("Servicio no Disponible", resultAddress+ ". " +
                    "Latitude = " + location!!.latitude +
                    ", Longitude = " +
                    location.longitude, illegalArgument);
        }
        return resultAddress

    }

    interface OnTaskCompleted{
        fun OnTaskCompleted(result:String)
    }

    override fun onPostExecute(result: String?) {
        listener.OnTaskCompleted(result!!)
        super.onPostExecute(result)
    }
}