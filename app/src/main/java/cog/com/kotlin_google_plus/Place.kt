package cog.com.kotlin_google_plus

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.gms.location.places.AutocompleteFilter
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.ui.PlaceAutocomplete
import java.util.*

class Place : AppCompatActivity() {

    var search: LinearLayout? = null
    var address: TextView? = null
    var city: EditText? = null
    var state: EditText? = null
    var country: EditText? = null
    var postalCode: EditText? = null
    var submit: Button? = null
    private val ORIGIN_REQUEST_CODE_AUTOCOMPLETE = 1
    var sharedPreferences: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        search = findViewById<LinearLayout>(R.id.layout_address)
        address = findViewById<TextView>(R.id.tv_address)
        city = findViewById<EditText>(R.id.et_city)
        state = findViewById<EditText>(R.id.et_state)
        country = findViewById<EditText>(R.id.et_country)
        postalCode = findViewById<EditText>(R.id.et_postalCode)
        submit = findViewById<Button>(R.id.submit)

        search?.setOnClickListener {
            openAutocompleteActivity(ORIGIN_REQUEST_CODE_AUTOCOMPLETE)
        }
        submit?.setOnClickListener {
            startActivity(Intent((applicationContext), Map::class.java))
        }

    }

    private fun openAutocompleteActivity(REQUEST_CODE_AUTOCOMPLETE: Int) {

        val locale = this.resources.configuration.locale.country
        println("Current Country==>$locale")

        val autocompleteFilter = AutocompleteFilter.Builder()
                .setTypeFilter(Place.TYPE_LOCALITY)
                .build()

        val intent = PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                .setFilter(autocompleteFilter)
                .build(this)
        startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == ORIGIN_REQUEST_CODE_AUTOCOMPLETE)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                val place = PlaceAutocomplete.getPlace(this,data)
                var search_address = place.name.toString()
                search_address += "\n" + place.address.toString()
                println("search_address $search_address")
                println("search_address ${place.latLng}")

                val lat = place.latLng.latitude
                val lng = place.latLng.longitude
                println("latlngggg $lat and $lng")
                println("latlnggggto ${lat.toString()} and ${lng.toString()}")

                sharedPreferences?.edit()?.putString("latitude",lat.toString())?.apply()
                sharedPreferences?.edit()?.putString("longitude",lng.toString())?.apply()

                val geocoder = Geocoder(this@Place, Locale.getDefault())
                val array: List<Address>?
                array = geocoder.getFromLocation(lat, lng, 1)
                val addresss = array.get(0).getAddressLine(0)
                val cityy = array.get(0).locality
                val statee = array.get(0).adminArea
                val countryy = array.get(0).countryName
                val postalCodee = array.get(0).postalCode
                val knownNamee = array.get(0).featureName

                println("addreeeeees 1 $addresss")
                println("addreeeeees 2 $cityy")
                println("addreeeeees 3 $statee")
                println("addreeeeees 4 $countryy")
                println("addreeeeees 5 $postalCodee")
                println("addreeeeees 6 $knownNamee")

                address?.setText(addresss)
                city?.setText(cityy)
                state?.setText(statee)
                country?.setText(countryy)
                postalCode?.setText(postalCodee)



            }
        }
    }
}
