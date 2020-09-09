package com.example.mygallery

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.*
import android.util.Log
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_main.*
import layout.MyPagerAdapter
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton
import kotlin.concurrent.timer

class MainActivity : AppCompatActivity() {
    private val REQUEST_READ_EXTERNAL_STORAGE = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // 권한이 부여되었는지 확인
        if(ContextCompat.checkSelfPermission(this,
            Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            
            // 권한이 허용되지 않음
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)){ // 사용자가 전에 권한 요청을 거부했는지를 반환 true를 반환하면 거부한적 있음
                // 이전에 이미 권한이 거부되었을 떄 설명
                alert("사진 정보를 얻으려면 외부 저장소 권한이 필수로 필요 합니다.", "권한이 필요한 이유"){
                    yesButton { 
                        // 권한 요청
                        // requestPermissions() 메서드를 사용하여 외부 저장소 읽기 권한을 요청
                        ActivityCompat.requestPermissions(this@MainActivity, 
                                                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                                                            REQUEST_READ_EXTERNAL_STORAGE)
                    }
                    noButton {  }
                }.show()
            }else{
                // 권한 요청
                ActivityCompat.requestPermissions(this,
                                                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                                                    REQUEST_READ_EXTERNAL_STORAGE)
            }
            
        }else{
            // 권한이 이미 허용됨
            getAllPhotos()
        }

    }

    // 사용자가 권한을 요청하면 onRequestPermissionsResult() 메서드를 호출하고 사용자 응답을 전달
    // 권한이 부여되었는지 확인하려면 이 메서드를 오버라이드 해야함
    // 응답 결과를 확인하여 사진 정보를 가져오거나 권한이 거부 됐다는 토스트 메세지를 표시하는 코드를 다음과 같이 작성
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            REQUEST_READ_EXTERNAL_STORAGE -> {
                if((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)){
                    // 권한 허용됨
                    getAllPhotos()
                }else{
                    // 권한 거부
                    toast("권한 거부 됨")
                }
                return
            }
        }

    }

    private fun getAllPhotos(){
        // 모든 사진 정보 가져오기
        val cursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                            null, // 가져올 항목 배열
                                            null, // 데이터를 가져올 조건
                                            null, // 세번째 인자와 조합하여 조건을 지정
                                            MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC") // 찍은 날짜 내림차순


        // 프래그먼트를 아이템으로 하는 ArrayList를 생성합니다

        val fragments = ArrayList<Fragment>()
        // 사진 정보를 담고 있는 Cursor 객체는 내부적으로 데이터를 이동하는 포인터를 가지고있음
        // moveToNext() 메서드로 다음 정보로 이동하고 그 결과를 true로 반환
        // while문에 사용하면 모든 데이터를 순회 할 수 있음
        // 만약 사진이 없다면 null
        if(cursor != null){
            while (cursor.moveToNext()){
                // 사진 경로 Uri 가져오기
                val uri = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA))
                Log.d("MainActivity", uri)
                // 사진을 Cursor 객체로부터 가져올 때마다
                // PhotoFragment.newInstance(uri)로 프래그먼트를 생성하면서 fragments 리스트에 추가합니다
                fragments.add(PhotoFragment.newInstance(uri))
            }
            cursor.close() // Cursor 객체를 더이상 사용하지 않을 때는 close() 메서드로 닫아야 함 닫지 않으면 메모리 누수가 발생
        }

        // 어댑터
        val adapter = MyPagerAdapter(supportFragmentManager)
        adapter.updateFragments(fragments)
        viewPager.adapter = adapter

        // 3초 마다 자동 슬라이드
        timer(period = 3000){
            runOnUiThread {
                if(viewPager.currentItem < adapter.count - 1){
                    viewPager.currentItem =viewPager.currentItem + 1
                }else{
                    viewPager.currentItem = 0
                }
            }
        }

    }
}