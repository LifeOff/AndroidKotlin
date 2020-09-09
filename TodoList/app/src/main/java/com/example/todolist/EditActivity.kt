package com.example.todolist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_edit.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.yesButton

import java.util.*

class EditActivity : AppCompatActivity() {
    
    // 인스턴스 얻기
    val realm = Realm.getDefaultInstance() // MyApplication 클래스에서 Realm을 초기ㅗ하 했다면 액티비티에서는
                                           // Realm.getDefaultInstance() 메서드를 이용해 Realm 객체의 인스턴스를 얻을 수 있습니다
    
    // 1. 할 일 추가
    val calendar : Calendar = Calendar.getInstance() // 날자를 다룰 캘린더 객체

    // 1. 할 일 추가
    private fun insertTodo() {
        
        // Realm에서 데이터를 추가, 삭제, 업데이트할 때는 beginTransaction() 메서드로 트랜젝션 시작
        realm.beginTransaction() // 트랜젝션 시작 (트랜젝션이란 데이터베이스의 작업 단위입니다)

        val newItem = realm.createObject<Todo>(nextId()) // 새 객체 생성 
                                                               // createObject() 메서드로 새로운 Realm 객체를 생성합니다
        
        // 값 설정
        // 객체를 생성 했다면 할 일과 시간을 설정합니다
        newItem.title = todoEditText.text.toString()
        newItem.date = calendar.timeInMillis

        realm.commitTransaction() // 트랜젝션 종료 반영
        
        // 다이얼 로그 표시
        // 할일이 추가되면 다이얼 로그를 표시 
        // 확인 버튼을 누르면 finish() 메서드를 호출하여 현재 액티비티를 종료
        alert("내용이 추가되었습니다") {
            yesButton { finish() }
        }.show()
        
        /*
            beginTransaction() 메서드와 commitTransaction() 메서드 사이에 작성한 코드들은
            전체가 하나의 작업으로 도중에 에러가 나면 모두 취소 됩니다. 이 과정이 모두 한 트랜잭션이기 때문입니다
            따라서 트랜잭션은 데이터베이스에서 아주 중요한 개념입니다. 
            데이터 베이스 추가, 삭제, 업데이트를 하려면 항상 트랜잭션을 시작하고 닫아야 합니다
         */
    }

    // 다음 id를 반환
    // Realm은 기본키 자동 증가 기능을 지원하지 않습니다.
    // 그래서 아래와 같이 현재 가장 큰 id값을 얻고 1을 더한 값을 반환하는 메서드를 추가로 작성
    private fun nextId(): Int {
        /*
            Todo 테이블의 모든 값을 얻으려면 where<Todo>() 메서드를 사용합니다
            이 메서드는 RealmQuery 객체를 반환하고 다음에 이어지는 조건을 수행합니다 
         */
        val maxId = realm.where<Todo>().max("id")

        if(maxId != null){
            return maxId.toInt() + 1
        }
        return 0
    }

    // 2. 할 일 수정
    private fun updateTodo(id: Long){ // updateTodo() 메서드는 id를 인자로 받습니다
        realm.beginTransaction() // 트랜 잭션 시작

        // Realm 객체의 where<T>() 메서드가 반환하는 T 타입 객체로부터 데이터를 얻습니다
        // equalTo() 메서드로 조건을 설정합니다
        // "id" 칼럼에 id값이 있다면 findFirst() 메서드로 첫 번째 데이터를 반환합니다
        val updateItem = realm.where<Todo>().equalTo("id", id).findFirst()!!

        // 값 수정
        updateItem.title = todoEditText.text.toString()
        updateItem.date = calendar.timeInMillis

        realm.commitTransaction() // 트랜젝션 종료 반영

        alert("내용이 변경 되었습니다") {
            yesButton { finish() }
        }.show()
    }

    // 3. 할 일 삭제
    private fun deleteTodo(id: Long){
        realm.beginTransaction()

        val deleteItem = realm.where<Todo>().equalTo("id", id).findFirst()!!
        // 삭제할 객체
        deleteItem.deleteFromRealm() // 삭제
        realm.commitTransaction()

        alert("내용이 삭제되었습니다") {
            yesButton { finish() }
        }.show()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        
        // 추가 / 수정 분기 처리
        
        // 업데이트 조건
        // 인텐트로부터 데이터를 꺼내 반환합니다
        val id = intent.getLongExtra("id", -1L) // getLongExtra(name : String, defaultValue : Long)
                                                                         // name : 아이템을 가리키는 키 입니다
                                                                         // defaultValue : 반환되는 값이 없을 때 기본값을 설정 합니다
        // id 값이 0 이상이면 업데이트 모드이고 무엇도 넘어오지 않아
        // 기본값 그대로 -1d이라면 추가 모드입니다
        if(id == -1L){
            insertMode()
        }else{
            updateMode(id)
        }
        
        // 캘린더 뷰의 날짜를 선택했을 때 Caleandar 객체에 설정
        // CalendarView에서 날짜를 선택하면 수행할처리를 setOnDateChangeListener() 메서드로 구현합니다
        // 변경된 년, 월, 일이 year, month, dayOfMonth로 넘어오므로 Calendar 객체에 년,월,일을 설정해주면\
        // 데이터베이스에 추가, 수정 시 설정한 날짜가 반영
        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        }
    }

    // 추가 모드 초기화
    private fun insertMode(){
        
        // 삭제 버튼 감추기
        deleteFab.visibility = View.GONE
        
        // 완료 버튼을 클릭하면 추가
        doneFab.setOnClickListener {
            insertTodo()
        }
    }
    
    // 수정 모드 초기화
    // 수정 모드에서는 id를 전달받아야 합니다
    private fun updateMode(id: Long){
        
        // id에 해당하는 객체를 화면에 표시
        // 전달받은 id를 가진 할 일 데이터를 찾아서 화면에 데이터를 표시합니다
        val todo = realm.where<Todo>().equalTo("id", id).findFirst()!!
        todoEditText.setText(todo.title)
        calendarView.date = todo.date
        
        // 완료 버튼을 클릭하면 수정
        doneFab.setOnClickListener {
            updateTodo(id)
        }

        // 삭제 버튼을 클릭하면 삭제
        deleteFab.setOnClickListener {
            deleteTodo(id)
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        
        // 인스턴스 해제
        realm.close() // 사용이 끝난 인스턴스는 액티비티가 소멸되는 생성 주기인 onDestroy()에서 헤제합니다
    }
}