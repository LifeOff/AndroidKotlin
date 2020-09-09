package com.example.flashlight


import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager


// 이 클래스는 플래시를 켜는 flashOn() 메서드와 플래시를 끄는 flashOff() 메서드를 기능을 제공합니다
// 플래시를 킬려면 CameraManager 객체가 필요하고 이를 얻을려면
// Context 객체가 필요하기 때문에 생성자로 Context를 받습니다
class Torch(context: Context) {

    private var cameraId : String? = null // 카메라를 켜고 끌 때 (flashOn(), flashOff()) 카메라 ID가 필요합니다. 클래스 초기화 때
    
    // context의 getSystemService() 메서드는 안드로이드 시스템에서 제공하는 각종 서비스를 관리하는 매니저 클래스를 생성합니다
    // 인자로 Context 클래스에 정의된 서비스를 정의한 상수를 지정합니다
    // 이 메서드는 Object 형을 반환하기 때문에 as 연산자를 사용하여 CameraService 형으로 형변환을 합니다(?)
    private val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager

    //  클래스 초기화 때 카메라 ID 를 얻어야합니다 카메라 ID는 기기에 내장된 카메라마다 고유한 ID가 부여됩니다
    init{
        cameraId = getCameraId()
    }

    fun flashOn(){
        cameraId?.let { cameraManager.setTorchMode(it, true) }
    }

    fun flashOff(){
        cameraId?.let { cameraManager.setTorchMode(it, false) }
    }

    private fun getCameraId(): String? { // getCameraId()는 카메라의 ID를 얻는 메서드입니다. 카메라가 없다면 ID가 null일 수 있기 때문에 반환값은 String? 로 지정합니다
        val cameraIds = cameraManager.cameraIdList // cameraManager 는 기기가 가지고 있는 모든 카메라에 대한 정보 목록를 제공합니다.
        for(id in cameraIds){ // 이 목록을 순회 하면서
            val info = cameraManager.getCameraCharacteristics(id) // 각 ID별로 세부 정보를 가지는 객체를 얻습니다
            val flashAvilable = info.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) // 플래시 가능 여부와
            val lensFacing = info.get(CameraCharacteristics.LENS_FACING) // 카메라의 렌즈 방향을 알 수 있습니다

            if(flashAvilable != null && flashAvilable && lensFacing != null && lensFacing == CameraCharacteristics.LENS_FACING_BACK){ // 플래시가 사용가능하고, 카메라가 기기의 뒷면을 항하고 있는 카메라 ID를 찾았다면
                return id // 이값을 반환합니다
            }
        }
        return null // 해당하는 카메라 ID를 찾지 못했다면  null를 반환함
    }
}