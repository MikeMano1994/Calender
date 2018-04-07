package cog.com.kotlin_google_plus

import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import cog.com.kotlin_google_plus.R.drawable.circle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.activity_map_2.*
import java.util.*

class Map : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnCameraChangeListener {

    private lateinit var map: GoogleMap
    internal var circleOptions: CircleOptions? = null
    internal var circle: Circle? = null
    var sharedPreferences: SharedPreferences? = null
    internal var latLng: LatLng? = null
    var locationnn: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_2)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        locationnn = findViewById<TextView>(R.id.locationnn)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_view) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    override fun onMapReady(googleMap: GoogleMap) {

        map = googleMap
        map.uiSettings.isZoomControlsEnabled = false
        map.uiSettings.isMyLocationButtonEnabled = false
        map.isMyLocationEnabled = false
        map.setOnCameraChangeListener(this)
        getLatLng()

    }

    companion object {

        private val radius = 10.0
        private val stroke = /*0xFF00FF00*/Color.parseColor("#008489")
        private val shade = /*0x44ff0000*/ Color.parseColor("#99cdcf")

    }

    override fun onCameraChange(cameraPosition: CameraPosition?) {

        latLng = cameraPosition?.target

        try {

            val geocoder = Geocoder(this@Map, Locale.getDefault())
            val array: List<Address>?
            array = geocoder.getFromLocation(latLng!!.latitude, latLng!!.longitude, 1)
            val address = array.get(0).getAddressLine(0)
            locationnn?.setText(address)
            placeMarkerOnMap(latLng!!)

        }
        catch (exception:Exception)
        {
            println("Error: $exception")
        }
    }


    private fun getLatLng()
    {

        val lati = sharedPreferences?.getString("latitude","")
        val longi = sharedPreferences?.getString("longitude","")

        println("vallllll ${lati!!.toDouble()} and  ${longi!!.toDouble()}")

        val latLng = LatLng(lati!!.toDouble(), longi!!.toDouble())
        placeMarkerOnMap(latLng)
    }

    private fun placeMarkerOnMap(location: LatLng) {

        val markerOptions = MarkerOptions().position(location)
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 20.0f))
        circleOptions = CircleOptions().center(location).radius(Map.radius).fillColor(Map.shade).strokeColor(Map.stroke.toInt()).strokeWidth(8F)

        if (circle != null)
        {
            map.clear()
        }

        circle = map.addCircle(circleOptions)
        markerOptions.draggable(false)
        /*markerOptions.isDraggable*/
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(resources, R.drawable.marker)))
        map.addMarker(markerOptions)

    }
}
