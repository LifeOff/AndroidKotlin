package com.example.bmicalculator

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.startActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // 이전에 입력한 값을 읽어오기
        loadData()
        
        resultButton.setOnClickListener{
            // 결과 버튼이 클릭되면 할 일을 작성하는 부분
            
            // 마지막에 입력한 내용을 저장
            saveData(heightEditText.text.toString().toInt(), weightEditText.text.toString().toInt())
            
            // 두 번째 화면으로 이동하는 코드
            /*
            val intent = Intent(this, ResultActivity::class.java)
            startActivity(intent)
            */

            // 인텐트에 데이터 담기
            // 인텐트는 데이터를 담아서 다른 액티비팅에 전달하는 역할을 함
            // 입력한 키와 몸무게를 문자열로 변경하여 인텐트에 담고 있음
            /*
            val intent = Intent(this, ResultActivity::class.java)
            intent.putExtra("weight", weightEditText.text.toString())
            intent.putExtra("height", heightEditText.text.toString())

            startActivity(intent)
            */

            // Anko 라이브러리를 적용한 코틀린의 액티비티 전환 코드
            startActivity<ResultActivity>(
                "weight" to weightEditText.text.toString(),
                "height" to heightEditText.text.toString()
            )
            
            
        }
    }
    // SharedPreference로 데이터 저장하기
    // 데이터 저장하기 메서드 추가
    private fun saveData(height: Int, weight: Int){
        val pref = PreferenceManager.getDefaultSharedPreferences(this) // 프리퍼런스 매니저를 이용해 객체를 얻는다
        val editor = pref.edit() // 프리퍼런스 객체의 에디터 객체를 얻는다. 이 객체를 이용해 프리퍼런스에 데이터를 담을수 있습니다

        editor.putInt("KEY_HEIGHT", height) // 에디터 객체에 put[데이터 타입]형식의 메서드를 사용 하여 키와 값의 형태의 쌍으로 저장을 한다
              .putInt("KEY_WEIGHT", weight)
              .apply() // 마지막으로 설정한 내용을 반영
    }
    
    // 데이터 불러오기 메서드 추가
    private fun loadData() {
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val height = pref.getInt("KEY_HEIGHT", 0) // getInt() 메서드롤 키와 몸무게에 저장된 값을 불러온다 두번째 인자는 저장된 값이 없을 때 기본값 0을 리턴한다
        val weight = pref.getInt("KEY_WEIGHT", 0)

        if(height != 0 && weight != 0){
            heightEditText.setText(height.toString()) // 저장된 값이 있다면 키와 몸무게를 입력하는 에디티텍스트에 마지막에 저장된 값을 표시
            weightEditText.setText(weight.toString())
        }
    }
}