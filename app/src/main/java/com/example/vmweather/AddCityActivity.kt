package com.example.vmweather

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.example.vmweather.databinding.ActivityAddCityBinding
import java.util.*

class AddCityActivity : AppCompatActivity() {
    lateinit var binding: ActivityAddCityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_city)

        val cities = resources.getStringArray(R.array.city)
        val adapter = ArrayAdapter(this, R.layout.spinner_item, cities)
        binding.spinner.adapter = adapter


        if(ActivityCompat.checkSelfPermission(this@AddCityActivity,Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this@AddCityActivity,Manifest.permission.ACCESS_COARSE_LOCATION)!=
                PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION),
            111)
        }
        binding.addCityBtn.setOnClickListener {

            if(binding.spinner.selectedItem.toString()=="Select City"){
                Toast.makeText(this,"Select atleast one choice",Toast.LENGTH_SHORT).show()
            }else{
                var gc = Geocoder(this, Locale.getDefault())
                var addressess = gc.getFromLocationName(binding.spinner.selectedItem.toString(),2)
                var address:Address = addressess!!.get(0)

                var lat = address.latitude
                var long = address.longitude
                var city = address.locality

                val bundle = Bundle()
                bundle.putString("city",city)
                bundle.putDouble("latitude", lat)
                bundle.putDouble("longitude",long)
                val intent = Intent(this@AddCityActivity, MainActivity::class.java)
                intent.putExtras(bundle)
                startActivity(intent)



            }


        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 111 &&  grantResults[0] ==PackageManager.PERMISSION_GRANTED){

        }
    }

}