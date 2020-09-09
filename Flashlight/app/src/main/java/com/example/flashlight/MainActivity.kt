package com.example.flashlight

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.intentFor

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 서비스 사용전
        // val torch = Torch(this) // 작성한 Torch 클래스를 인스턴스화합니다

        flashSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked) {
                startService(intentFor<TorchService>().setAction("on"))

                /*  Anko 라이브러리 사용전
                    val intent Intent(this, TorchService::class.java)
                    intent.action = "on"
                    startService(intent)
                 */

                // 서비스 사용 전
                // torch.flashOn()
            }else{

                startService(intentFor<TorchService>().setAction("off"))
                // 서비스 사용 전
                // torch.flashOff()
            }
        }
    }
}