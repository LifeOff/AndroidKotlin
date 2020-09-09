package com.example.todolist

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

// 코틀린에서는 Realm에서 사용하는 클래스에 open 키워드를 추가합니다
open class Todo(
                @PrimaryKey var id : Long = 0, // id는 유일한 값이 되어야 하기 때문에 기본키(Primary Key) 제약을 주석으로 추가합니다
                                               // 데이터베이스에서는 데이터를 식별하는 유일한 키 값을 기본키라고 합니다
                                               // 기본키 제약은 Realm에서 제공하는 주석이며, 이 주석이 부여된 속성값은 중복을 허용하지 않습니다
                var title : String = "",
                var date : Long = 0
                ) : RealmObject() { // RealmObject 클래스를 상속받아 Realm 데이터베이스에서 다룰 수 있습니다
     }