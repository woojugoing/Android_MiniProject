package com.woojugoing.mini01_lbs02

import android.Manifest
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.woojugoing.mini01_lbs02.databinding.ActivityMainBinding
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

  lateinit var activityMainBinding: ActivityMainBinding
  lateinit var map: GoogleMap
  lateinit var userLocation: Location

  var myMarker: Marker? = null
  var myLocationListener: LocationListener? = null

  val latitudeList = mutableListOf<Double>()
  val longitudeList = mutableListOf<Double>()
  val nameList = mutableListOf<String>()
  val vicinityList = mutableListOf<String>()
  val markerList = mutableListOf<Marker>()

  var permissionList =
      arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)

  val dialogData =
      arrayOf(
          "accounting",
          "airport",
          "amusement_park",
          "aquarium",
          "art_gallery",
          "atm",
          "bakery",
          "bank",
          "bar",
          "beauty_salon",
          "bicycle_store",
          "book_store",
          "bowling_alley",
          "bus_station",
          "cafe",
          "campground",
          "car_dealer",
          "car_rental",
          "car_repair",
          "car_wash",
          "casino",
          "cemetery",
          "church",
          "city_hall",
          "clothing_store",
          "convenience_store",
          "courthouse",
          "dentist",
          "department_store",
          "doctor",
          "drugstore",
          "electrician",
          "electronics_store",
          "embassy",
          "fire_station",
          "florist",
          "funeral_home",
          "furniture_store",
          "gas_station",
          "gym",
          "hair_care",
          "hardware_store",
          "hindu_temple",
          "home_goods_store",
          "hospital",
          "insurance_agency",
          "jewelry_store",
          "laundry",
          "lawyer",
          "library",
          "light_rail_station",
          "liquor_store",
          "local_government_office",
          "locksmith",
          "lodging",
          "meal_delivery",
          "meal_takeaway",
          "mosque",
          "movie_rental",
          "movie_theater",
          "moving_company",
          "museum",
          "night_club",
          "painter",
          "park",
          "parking",
          "pet_store",
          "pharmacy",
          "physiotherapist",
          "plumber",
          "police",
          "post_office",
          "primary_school",
          "real_estate_agency",
          "restaurant",
          "roofing_contractor",
          "rv_park",
          "school",
          "secondary_school",
          "shoe_store",
          "shopping_mall",
          "spa",
          "stadium",
          "storage",
          "store",
          "subway_station",
          "supermarket",
          "synagogue",
          "taxi_stand",
          "tourist_attraction",
          "train_station",
          "transit_station",
          "travel_agency",
          "university",
          "eterinary_care",
          "zoo")

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    installSplashScreen()
    MapsInitializer.initialize(this, MapsInitializer.Renderer.LATEST, null)
    activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(activityMainBinding.root)
    requestPermissions(permissionList, 0)

    activityMainBinding.run {
      toolbarMain.run {
        title = "LBS Project"
        inflateMenu(R.menu.menu_main)
        setOnMenuItemClickListener {
          when (it.itemId) {
            R.id.menu_main_location -> {
              getMyLocation()
            }
            R.id.menu_main_select -> {
              val builder = AlertDialog.Builder(this@MainActivity)
              builder.run {
                setTitle("장소 종류 선택")
                setNegativeButton("취소", null)
                setNeutralButton("초기화") { dialogInterface: DialogInterface, i: Int ->
                  runOnUiThread {
                    latitudeList.clear()
                    longitudeList.clear()
                    nameList.clear()
                    vicinityList.clear()
                    for (marker in markerList) {
                      marker.remove()
                    }
                    markerList.clear()
                  }
                }
                setItems(dialogData) { dialogInterface: DialogInterface, i: Int ->
                  thread {
                    val location = "${userLocation.latitude},${userLocation.longitude}"
                    val radius = 50000
                    val language = "ko"
                    val type = dialogData[i]
                    val key = "API key"
                    val site =
                        "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=${location}&radius=${radius}&language=${language}&type=${type}&key=${key}"

                    runOnUiThread {
                      latitudeList.clear()
                      longitudeList.clear()
                      nameList.clear()
                      vicinityList.clear()
                      for (marker in markerList) {
                        marker.remove()
                      }
                      markerList.clear()
                    }

                    var nextToken: String? = null

                    do {
                      val site2 =
                          if (nextToken != null) {
                            "${site}&pagetoken=${nextToken}"
                          } else {
                            site
                          }

                      val url = URL(site2)
                      val httpURLConnection = url.openConnection() as HttpURLConnection
                      val inputStreamReaddr = InputStreamReader(httpURLConnection.inputStream)
                      val bufferedReader = BufferedReader(inputStreamReaddr)

                      var str: String? = null
                      val stringBuffer = StringBuffer()

                      do {
                        str = bufferedReader.readLine()
                        if (str != null) {
                          stringBuffer.append(str)
                        }
                      } while (str != null)

                      val data = stringBuffer.toString()
                      val root = JSONObject(data)
                      val status = root.getString("status")
                      if (status == "OK") {
                        val resultsArray = root.getJSONArray("results")

                        for (idx in 0 until resultsArray.length()) {
                          val resultObject = resultsArray.getJSONObject(idx)
                          val geometryObject = resultObject.getJSONObject("geometry")
                          val locationObject = geometryObject.getJSONObject("location")
                          val lat = locationObject.getDouble("lat")
                          val lng = locationObject.getDouble("lng")

                          val name = resultObject.getString("name")
                          val vicinity = resultObject.getString("vicinity")

                          latitudeList.add(lat)
                          longitudeList.add(lng)
                          nameList.add(name)
                          vicinityList.add(vicinity)
                        }
                      }

                      if (root.has("next_page_token")) {
                        nextToken = root.getString("next_page_token")
                      } else {
                        nextToken = null
                      }
                    } while (nextToken != null)

                    runOnUiThread {
                      for (idx in 0 until nameList.size) {
                        val markerOptions = MarkerOptions()
                        val loc = LatLng(latitudeList[idx], longitudeList[idx])
                        markerOptions.position(loc)
                        markerOptions.title(nameList[idx])
                        markerOptions.snippet(vicinityList[idx])

                        val marker = map.addMarker(markerOptions)
                        markerList.add(marker!!)
                      }
                    }
                  }
                }
                show()
              }
            }
          }
          false
        }
      }
    }

    val supportMapFragment =
        supportFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment

    supportMapFragment.getMapAsync {
      map = it

      it.uiSettings.isZoomControlsEnabled = true
      it.uiSettings.isMyLocationButtonEnabled = true
      it.isMyLocationEnabled = true // 현재 위치 표시
      it.mapType = GoogleMap.MAP_TYPE_NORMAL // Map Type

      val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

      val per1 =
          ActivityCompat.checkSelfPermission(
              this@MainActivity, Manifest.permission.ACCESS_COARSE_LOCATION)
      val per2 =
          ActivityCompat.checkSelfPermission(
              this@MainActivity, Manifest.permission.ACCESS_FINE_LOCATION)
      if (per1 == PackageManager.PERMISSION_GRANTED && per2 == PackageManager.PERMISSION_GRANTED) {
        val loc1 = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        if (loc1 != null) {
          setLoc(loc1)
        }
        getMyLocation()
      }
    }
  }

  fun setLoc(location: Location) {
    userLocation = location
    if (myLocationListener != null) {
      val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
      locationManager.removeUpdates(myLocationListener!!)
      myLocationListener = null
    }

    val latLng = LatLng(location.latitude, location.longitude)
    val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 16F)
    map.animateCamera(cameraUpdate)
  }

  fun getMyLocation() {
    val per1 =
        ActivityCompat.checkSelfPermission(
            this@MainActivity, Manifest.permission.ACCESS_COARSE_LOCATION)
    val per2 =
        ActivityCompat.checkSelfPermission(
            this@MainActivity, Manifest.permission.ACCESS_FINE_LOCATION)
    if (per1 == PackageManager.PERMISSION_GRANTED && per2 == PackageManager.PERMISSION_GRANTED) {
      val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
      myLocationListener =
          object : LocationListener {
            override fun onLocationChanged(location: Location) {
              setLoc(location)
            }
          }

      if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
        locationManager.requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER, 0, 0F, myLocationListener!!)
      }
    }
  }
}
