package com.example.gpsmap

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    // 위치 정보를 주기적으로 얻는 데 필요한 객체들을 선언합니다
    // MyLocationCallBack 은 MapsActivity 클래스의 내부 클래스로 생성
    private lateinit var fusedLocationProviderClient : FusedLocationProviderClient
    private lateinit var locationRequest : LocationRequest
    private lateinit var locationCallback : MyLocationCallBack

    // PolyLine 옵션
    // PolyLineOptions() 객체를 생성합니다 선을 이루는 좌표들과 선의 굵기 색상등을 설정 가능
    private val polylineOptions = PolylineOptions().width(5f).color(Color.CYAN)
    
    private val REQUEST_ACCESS_FINE_LOCATION = 1000

    // 이 메서드는 함수 인자를 2개 받습니다 두 함수는 모두 인자가 없고 반환값도 없습니다
    private fun permissionCheck(cancel: () -> Unit, ok: () -> Unit){
        // 위치 권한이 있는지 검사
        if(ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED){
            // 권한이 허용되지 않음
            if(ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )){
                // 이전에 권한을 한 번 거부한 적이 있는 경우에 실행할 함수
                cancel() 
            }else{
                // 권한 요청
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_ACCESS_FINE_LOCATION
                )
            }
        }else{
            // 권한을 수락 했을 때 실행할 함수
            ok()
        }
    }
    
    private fun showPermissionInfoDialog() {
        alert("현재 위치 정보를 얻으려면 위치권한이 필요합니다", "권한이 필요한 이유"){
            yesButton { 
                // 권한 요청
                ActivityCompat.requestPermissions(
                    this@MapsActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_ACCESS_FINE_LOCATION
                )
            }
            noButton {  }
        }.show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 화면 꺼지지 않게 하기
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        //세로모드 화면 고정
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        
        setContentView(R.layout.activity_maps)

        // SupportMapFragment를 가져와서 지도가 준비가 되면 알림을 받습니다\
        // 프래그먼트 매니저로부터 SupportMapFragment 를 얻습니다 getMapAsync() 메서드로 지도가 준비되면 알림을 받습니다
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        locationInit() // 위에서 선언한 변수들을 onCreate() 메서드의 끝에서 초기화합니다
    }


    // 위치 정보를 얻기 위한 각종 초기화
    // LocationRequest는 위치 정보 요청에 대한 세부 정보를 설정합니다
    // 프로퍼티의 의미
    /*
        priority : 정확도를 나타 냅니다
        - PRIORITY_HIGH_ACCURACY : 가장 정확한 위치를 요청합니다
        - PRIORITY_BALANCED_POWER_ACCURACY : '블록'수준의 정확도를 요청합니다
        - PRIORITY_LOW_POWER : 도시 수준의 정확도를 요청
        - PRIORITU_NO_POWER : 추가 전력 소모 없이 최상의 정확도를 요청함
        interval : 위치를 갱신하는 데 필요한 시간을 밀리초 단위로 입력합니다
        fastestInterval : 다은 앱에서 위치를 갱신했을 때 그 정보를 가장 빠른간격 으로 입력
     */
    private fun locationInit(){
        fusedLocationProviderClient = FusedLocationProviderClient(this)
        
        locationCallback = MyLocationCallBack()
        
        locationRequest = LocationRequest()
        
        // GPS 우선
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        // 업데이트 인터벌
        // 위치 정보가 없을 때는 업데이트 안함
        // 상황에 따라 짧아 질수 있음, 정확하지 않음
        // 다른 앱에서 짧은 인터벌로 위치 정보를 요청하면 짧아질 수 있음
        locationRequest.interval = 10000
        // 정확함 이것보다 짧은 업데이트는 하지않음
        locationRequest.fastestInterval = 5000
    }


    /*
     사용 가능한 맵을 조작합니다
     지도를 사용할 준비가 되면 이 콜백이 호출 됩니다
     여기서 마커나 선, 청취자를 추가하거나 카메라를 이동할 수 있습니다
     호주 시드니 근처에 마커를 추가하고 있습니다
     Google Paly 서비스가 기기에 설치되어 있지 않은 경우 사용자에게
     SupportMapFragment 안에 Google Play 서비스를 설치하라는 메시지가 표시 됩니다
     이 메서드는 사용자가 Google Play 서비스를 설치하고 앱으로 돌아온 후에만 호출(혹은 실행) 됩니다
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap // 지도가 준비가되면 GoogleMap 객체를 얻습니다

        // 시드니에 마커를 추가하고 카메라를 이동
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

    override fun onResume() {
        super.onResume()

        // 권한 요청
        permissionCheck(cancel = {
            // 위치 정보가 필요한 이유 다이얼로그 표시
            showPermissionInfoDialog()
        }, ok = {
            // 현재 위치를 주기적으로 요청
            addLocationListener() // 이러한 위치 요청은 액티비티가 활성화되는 onResume() 메서드에서 수행하며
        })
        
    }

    // 이와같이 별도의 메서드로 작성합니다 
    // 권한 요청 에러를 표시하지 않도록하는 코드
    @SuppressLint("MissingPermission")
    private fun addLocationListener() {
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            REQUEST_ACCESS_FINE_LOCATION -> {
                if((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)){
                    // 권한 허용됨
                    addLocationListener()
                }else{
                    // 권한 거부
                    toast("권한 거부 됨")
                }
                return
            }
        }
    }

    override fun onPause() {
        super.onPause()
        // onPause() 에서 위치 요청을 취소합니다
        removeLocationListener()
    }

    private fun removeLocationListener() {
        // 현재 위지 요청을 삭제
        // remoteLocationUpdates() 메서드에 LocationCallback 객체를 전달하여 주기적인 위치 정보 갱신 요청을 삭제
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    // requestLocationUpdates() 메서드에 전달되는 인자 중 LocationCallBack을 구현한 내부 클래스는
    // LocationResult 객체를 반환하고 lastLocation 프로퍼티로 Location 객체를 얻습니다
    inner class MyLocationCallBack : LocationCallback(){
        override fun onLocationResult(locationResult: LocationResult?) {
            super.onLocationResult(locationResult)

            val location = locationResult?.lastLocation
            // 기기 GPS 설정이 꺼져 있거나 현재 위치 정보를 얻을 수 없는 경우에 Location 객체가 null일수 있음
            // Location 객체가 null이 아닐 때 해당 위도와 경도 위치로 카메라를 이동합니다
            location?.run { 
                // 14 level로 확대하고 현재위치로 카메라 이동
                val latLng = LatLng(latitude, longitude)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,17f))

                Log.d("MapsActivity", "위도: $latitude, 경도: $longitude")

                // PolyLine에 좌표 추가
                // 위치정보가 갱신되면 해당 좌표를 polylineOptions 객체에 추가합니다
                polylineOptions.add(latLng)

                // 선그리기
                // polylineOptions 를 지도에 추가합니다
                mMap.addPolyline(polylineOptions)
            }
        }

    }



}