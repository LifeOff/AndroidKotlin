package com.example.xylophone

import android.content.pm.ActivityInfo
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    // 안드로이드 5.0 미만에서는 사용할 수 없음
    // private val soundPool = SoundPool.Builder().setMaxStreams(8).build()

    // 안드로이드 5.0 미만에서 SoundPool 사용 방법
    private val soundPool = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        SoundPool.Builder().setMaxStreams(8).build()
    } else {
        SoundPool(8, AudioManager.STREAM_MUSIC, 0)
    }

    // 먼저 list() 함수를 사용하여 텍스트 뷰의 ID와 음원 파일의 리소스 ID를 연관 지른 Pair 객체 8개를 리스트 객체 sounds로 만듭니다
    // Pair 클래스는 두 개의 연관된 객체를 저장 합니다
    private val sounds = listOf(
        Pair(R.id.do1, R.raw.do1),
        Pair(R.id.re, R.raw.re),
        Pair(R.id.mi, R.raw.mi),
        Pair(R.id.fa, R.raw.fa),
        Pair(R.id.sol, R.raw.sol),
        Pair(R.id.la, R.raw.la),
        Pair(R.id.si, R.raw.si),
        Pair(R.id.do2, R.raw.do2)
    )

    override fun onCreate(savedInstanceState: Bundle?) {

        // 화면이 가로 모드로 고정되게 하기
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // sounds 리스트를 forEach() 함수를 사용하여 요소를 하나 씩 꺼내서 tune() 메서드에 전달
        sounds.forEach { tune(it) }
    }

    // tune() 메서드는 Pair 객체를 받아서
    private fun tune(pitch: Pair<Int, Int>) {
        val soundId = soundPool.load(this, pitch.second, 1) // load() 메서드로 음원의 ID를 얻고

        // findViewById() 메서드로 텍스트 뷰의 ID에 해당하는 뷰를 얻고
        findViewById<TextView>(pitch.first).setOnClickListener {
            // 텍스트 뷰를 클릭 했을 때 음원을 재생합니다
            soundPool.play(soundId, 1.0f, 1.0f, 0, 0, 1.0f)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // 앱을 종료 할 때는 반드시 release() 메서드를 호출하여
        // SoundPool 객체의 자원을 해제합니다
        soundPool.release()
    }
}