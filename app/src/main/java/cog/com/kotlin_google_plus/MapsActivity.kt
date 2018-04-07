package cog.com.kotlin_google_plus

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.RelativeLayout
import android.widget.TextView
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.location.places.AutocompleteFilter
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.ui.PlaceAutocomplete
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class MapsActivity: AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnCameraChangeListener {

    override fun onMarkerClick(p0: Marker?) = false

    var search_location: TextView? = null
    var myLocationButton: RelativeLayout? = null
    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private var locationUpdateState = false
    internal var drag_location: Location? = null
    private val ORIGIN_REQUEST_CODE_AUTOCOMPLETE = 1
    private val DEST_REQUEST_CODE_AUTOCOMPLETE = 12
    internal var circle: Circle? = null
    internal var circleOptions: CircleOptions? = null
    internal var latLng: LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)

                lastLocation = p0.lastLocation
                //placeMarkerOnMap(LatLng(lastLocation.latitude, lastLocation.longitude))
            }
        }

        search_location = findViewById<TextView>(R.id.search_location)
        myLocationButton = findViewById<RelativeLayout>(R.id.myLocationButton)
        search_location?.setOnClickListener {
            //loadPlacePicker()
            openAutocompleteActivity(ORIGIN_REQUEST_CODE_AUTOCOMPLETE)
        }
        myLocationButton?.setOnClickListener{

        }

        createLocationRequest()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.uiSettings.isZoomControlsEnabled = false
        map.uiSettings.isMyLocationButtonEnabled = false
        map.isMyLocationEnabled = false
        map.setOnMarkerClickListener(this)
        map.setOnCameraChangeListener(this)
        setUpMap()
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val PLACE_PICKER_REQUEST = 3
        private const val REQUEST_CHECK_SETTINGS = 2
        private val radius = 10.0
        private val stroke = /*0xFF00FF00*/Color.parseColor("#008489")
        private val shade = /*0x44ff0000*/ Color.parseColor("#66B5B8")

    }

    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }

        map.isMyLocationEnabled = false

        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            // Got last known location. In some rare situations this can be null.
            if (location != null) {
                lastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
                placeMarkerOnMap(currentLatLng)

                println("current loction 1 $currentLatLng")
                println("current loction 2 ${getAddress(currentLatLng)}")

                /*map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 20.0f))

                circleOptions = CircleOptions().center(currentLatLng).radius(radius).fillColor(shade).strokeColor(stroke.toInt()).strokeWidth(8F)
                circle = map.addCircle(circleOptions)*/

            }
            myLocationButton?.setOnClickListener{

                val currentLatLng = LatLng(location.latitude, location.longitude)
                placeMarkerOnMap(currentLatLng)
                /*map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 20.0f))
                circleOptions = CircleOptions().center(currentLatLng).radius(radius).fillColor(shade).strokeColor(stroke.toInt()).strokeWidth(8F)
                circle = map.addCircle(circleOptions)*/

            }

        }
    }

    override fun onCameraChange(cameraPosition: CameraPosition?) {

        if (ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }

        latLng = cameraPosition?.target
        /*drag_location?.latitude(latLng?.latitude)
        drag_location?.longitude(latLng?.longitude)*/

        try {

            val geocoder = Geocoder(this@MapsActivity, Locale.getDefault())
            val array: List<Address>?
            array = geocoder.getFromLocation(latLng!!.latitude, latLng!!.longitude, 1)
            val address = array.get(0).getAddressLine(0)
            val city = array.get(0).locality
            val state = array.get(0).adminArea
            val country = array.get(0).countryName
            val postalCode = array.get(0).postalCode
            val knownName = array.get(0).featureName

            println("addreeeeees 1 $address")
            println("addreeeeees 2 $city")
            println("addreeeeees 3 $state")
            println("addreeeeees 4 $country")
            println("addreeeeees 5 $postalCode")
            println("addreeeeees 6 $knownName")

            println("onCameraChange 1 $latLng and ${latLng.toString()}")
            println("onCameraChange 2 $address")
            search_location?.setText(address)
            placeMarkerOnMap(latLng!!)

        }
        catch (exception:Exception)
        {
            println("Error: $exception")
        }


    }

    private fun getAddress(latLng: LatLng): String {
        val geocoder = Geocoder(this)
        val addresses: List<Address>?
        val address: Address?
        var addressText = ""

        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            if (null != addresses && !addresses.isEmpty()) {
                address = addresses[0]
                for (i in 0 until address.maxAddressLineIndex) {
                    addressText += if (i == 0) address.getAddressLine(i) else "\n" + address.getAddressLine(i)

                    println("change address $addressText")
                    /*search_location?.setText(addressText)*/
                }
            }
        } catch (e: IOException) {
            Log.e("MapsActivity", e.localizedMessage)
        }

        return addressText
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

    private fun placeMarkerOnMap(location: LatLng) {

        val markerOptions = MarkerOptions().position(location)

        val titleStr = getAddress(location)  // add these two lines
        markerOptions.title(titleStr)

        /*map.addMarker(markerOptions)*/

        map.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 20.0f))

        /*if (circle != null)
        {
            //map.clear()
            circle?.remove()
        }
        else
        {

        }*/
        circleOptions = CircleOptions().center(location).radius(radius).fillColor(shade).strokeColor(stroke.toInt()).strokeWidth(8F)
        circle = map.addCircle(circleOptions)

        markerOptions.draggable(true)
        markerOptions.isDraggable

        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(
                BitmapFactory.decodeResource(resources, R.drawable.marker)))
        map.addMarker(markerOptions)

        //markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(resources, R.mipmap.ic_user_location)))

        //default marker
        //markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))

        //Customized marker
        /*markerOptions.icon(BitmapDescriptorFactory.fromBitmap(
                BitmapFactory.decodeResource(resources, R.mipmap.ic_user_location)))*/
    }

    private fun startLocationUpdates() {
        //1
        if (ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_PERMISSION_REQUEST_CODE)
            return
        }
        //2
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null /* Looper */)
    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 5000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)

        val client = LocationServices.getSettingsClient(this)
        val task = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            locationUpdateState = true
            startLocationUpdates()
        }
        task.addOnFailureListener { e ->
            if (e is ResolvableApiException) {
                try {
                    e.startResolutionForResult(this@MapsActivity,
                            REQUEST_CHECK_SETTINGS)
                } catch (sendEx: IntentSender.SendIntentException) {
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    public override fun onResume() {
        super.onResume()
        if (!locationUpdateState) {
            startLocationUpdates()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                locationUpdateState = true
                startLocationUpdates()
            }
        }

        if (requestCode == ORIGIN_REQUEST_CODE_AUTOCOMPLETE)
        {
           if (resultCode == Activity.RESULT_OK)
           {
               val place = PlaceAutocomplete.getPlace(this,data)
               var address = place.name.toString()
               address += "\n" + place.address.toString()
               println("search_address $address")
               println("search_address ${place.latLng}")
               search_location?.setText(address)
               placeMarkerOnMap(place.latLng)

           }
        }
    }
}