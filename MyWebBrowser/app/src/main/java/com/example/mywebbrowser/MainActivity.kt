package com.example.mywebbrowser

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.browse
import org.jetbrains.anko.email
import org.jetbrains.anko.sendSMS
import org.jetbrains.anko.share

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // 웹 뷰 기본 설정
        webView.apply {
            settings.javaScriptEnabled = true
            webViewClient = WebViewClient()
        }

        webView.loadUrl("http://www.google.com")

        // 컨텍스트 메뉴를 표시할 뷰 등록
        registerForContextMenu(webView)

        // 소프트 키보드의 검색 버튼 동작 정의하기
        // setOnEditorActionListener는 에디트텍스트가 선택되고 글자가 입력될 때마다 호출됨
        // 인자로는 반응한 뷰, 액션ID, 이벤트 세가지 이며 여기서는 뷰와 이벤트를 사용하지않기 때문에 _로 대치
        urlEditText.setOnEditorActionListener {_, actionId, _ -> 
            if(actionId == EditorInfo.IME_ACTION_SEARCH){ // actionId의 값은 EditorInfo 클래스에 상수로 정의된 값 중에서 검색 버튼에 해당하는 상수와 비교하여 검색버튼이 눌렸는지 확인 함
                webView.loadUrl(urlEditText.text.toString()) // 검색 창에 입력한 주소를 웹뷰로 전달하여 로딩합니다. 마지막으로 true를 반환하여 이벤트를 종료
                true
            }
            else{
                false
            }

        }
    }

    // 뒤로가기 동작 재정의
    override fun onBackPressed() {
        if(webView.canGoBack()){  // 웹뷰가 이전페이지로 갈 수 있다면
            webView.goBack()    // 이전 페이지로 넘어가고
        }
        else{
            super.onBackPressed() // 아니라면 원래 동작을 수행
        }
    }

    // 옵션 메뉴를 액티비티에 표시하기
    // 옵션 메뉴 리소스 지정
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu) // 메뉴 리소스를 액티비티의 메뉴로 표시하려면 menuInflater 객체의 inflate() 메서드를 사용하여 메뉴 리소스를 지정
        return true // true를 반환하면 액티비티에 메뉴가 있다고 인식
    }

    // 옵션 메뉴 클릭 이벤트 처리
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId){
            R.id.action_google, R.id.action_home -> {
                webView.loadUrl("http://www.google.com")
                return true
            }

            R.id.action_naver-> {
                webView.loadUrl("http://www.naver.com")
                return true
            }

            R.id.action_daum -> {
                webView.loadUrl("http://www.daum.net")
                return true
            }

            // 전화 걸기 암시적 인텐트
            R.id.action_call -> {
                val intent = Intent(Intent.ACTION_DIAL) // 인텐트를 정의하며 Intent 클래스에 정의된 액션 중 하나를 지정 ACTION_DIAL 액션은 전화 다이얼을 입력해주는 액션
                intent.data = Uri.parse("tel:010-4059-2512") // 인텐트에 데이터를 지정함 tel:로 시작하는 Uri는 전화번호를 나타냄 이를 Uri.parse() 메서드로 감싼 Uri 객체를 데이터로 설정
                // intent.resolveActivity() 메서드는 이 인텐트를 수행하는 액티비티가 있는지를 검사하여 반환
                // null이 반환 된다면 수행하는 액티비티가 없는 것
                if(intent.resolveActivity(packageManager) != null){
                    startActivity(intent)
                }
                return true
            }

            /*
                Anko를 활용한 암시적 인텐트
                
                전화걸기 : makecall(전화번호)
                문자보내기 : sendSMS(전화번호, [문자열])
                웹 브라우저에서 열기 : browse(url)
                문자열 공유 : share(문자열, [제목])
                이메일 보내기 : email(받는 메일주소,[제목],[내용])
                Anko에서 지원하는 암시적 인텐트 []는 옵션
                
            */
            R.id.action_send_text -> {
                // 문자 보내기
                sendSMS("010-4059-2512",webView.url)
                return true
            }
            
            R.id.action_email -> {
                // 이메일 보내기
                email("yoyo2900@naver.com", "좋은 사이트", webView.url)
                return true
            }
        }
        // when문에서는 각 메뉴 처리를 끝내고 true를 반환
        // 내가 처리하고자 하는 경우를 제외한 그 이외의 경우에는 super 메서드를 호출하는 것이
        // 안드로이드 시스템에서의 보편적인 규직
        return super.onOptionsItemSelected(item)
    }

    // 컨텍스트 메뉴 작성
    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.context, menu) // menuInflater.inflate 메서드를 사용하여 메뉴 리소스를 액티비티의 컨텍스트 메뉴로서 사용
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when(item?.itemId){
            R.id.action_share -> {
                // 페이지 공유
                share(webView.url)
                return true
            }
            R.id.action_browser -> {
                // 기본 웹 브라우저에서 열기
                browse(webView.url)
            }
        }

        return super.onContextItemSelected(item)
    }
}