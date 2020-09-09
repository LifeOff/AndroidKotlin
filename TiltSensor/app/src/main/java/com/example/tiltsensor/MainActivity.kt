package com.example.tiltsensor

import android.content.Context
import android.content.pm.ActivityInfo
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager

// SensorManager 준비
class MainActivity : AppCompatActivity(), SensorEventListener {

    private val sensorManager by lazy{
        getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    private lateinit var tiltView: TiltView // TiltView의 늦은 초기화 선언을 합니다
    
    override fun onCreate(savedInstanceState: Bundle?) {
        // 화면이 꺼지지 않게 하기
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        // 화면에 가로모드로 고정되게 하기
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        super.onCreate(savedInstanceState)

        tiltView = TiltView(this) // onCreate() 메서드에서 생성자에 this를 넘겨서 TiltView로 초기화
        setContentView(tiltView) // 기존의 R.layout.activity_main 대신에 tiltView를 secContentView() 메서드에 전달합니다 이제 tiltView가 전체 레이아웃이 됩니다
    }

    // 센서 등록
    // SensorManager 객체가 준비되면 액티비티가 동작할 때만 센서가 동작해야 베터리를 아낄 수 있다
    // 일반적으로 센서의 사용등록은 액티비티의 onResume() 메서드에서 수행합니다
    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, // registerListener() 메서드로 사용할 센서를 등록 함. 첫번째 인자는 센서값을 받을 SensorEventListener입니다
                                                    // 여기서는 this를 지정하여 액티비티에서 센서값을 받도록 합니다
                                        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), // getDefaultSensor() 메서드로 사용할 센서 종류를 지정
                                        SensorManager.SENSOR_DELAY_NORMAL) // 세번째 인자는 센서값을 얼마나 자주 받을지를 지정함 SensorManager 클래스에 정의된 상수중 하나를 선택함
    }

    // 센서 정밀도가 변경되면 호출
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        TODO("Not yet implemented")
    }

    // 센서값이 변경되면 호출됩니다 SensorEvent 객체에 센서가 측정한 값들과 여러 정보가 넘어옵니다
    override fun onSensorChanged(event: SensorEvent?) {
        /*
            센서는 SensorEvent 객체로 값을 넘겨 줍니다
            SensorEvent.values[0] : x축 값
            SensorEvent.values[1] : y축 값
            SensorEvent.values[2] : z축 값
        */
        // 센서값 읽기
        // 센서 값이 변경되면 호출됨
        // values[0] : x축 값 : 위로 기울이면 -10~0, 아래로 기울이면 0~10
        // values[1] : y축 값 : 왼쪽으로 기울이면 -10~0, 오른쪽으로 기울이면 0~10
        // values[2] : z축 값 : 미사용
        event?.let {
            Log.d("MainActivity", "onSensorChanged: x :" + "${event.values[0]}, y : ${event.values[1]}, z : ${event.values[2]}")

            tiltView.onSensorEvent(event) // 액티비티에 센서값이 변경되면 onSensorChanged() 메서드가 호출 되므로 여기서 TiltView에 센서값을 던달
        }
        // 디버그용 로그를 표시 할 때 사용합니다
        // Log.d([태그], [메시지]) : 
        // 태그 : 로그캣에는 많은 내용이 표시되므로 필터링할 때 사용합니다
        // 메시지 : 출력할 메시지를 작성합니다
        /*
            이외의 로그 메서드
            Log.e() : 에러를 표시할 때 사용
            Log.w() : 경고를 표시할 때 사용
            Log.i() : 정보성 로그를 표시할 때 사용
            Log.v() : 모든 로그를 표시할 때 사용
         */

    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this) // unregisterListener() 메서드를 이용하여 센서사용을 해제할 수 있으며 인자로 SensorEventListener 객체를 지정합니다
                                                      // MainActivity 클래스에서 이 객체를 구현 중이므로 this를 지정합니다
    }
}