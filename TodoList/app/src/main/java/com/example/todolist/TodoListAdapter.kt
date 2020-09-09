package com.example.todolist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.realm.OrderedRealmCollection
import io.realm.RealmBaseAdapter
import io.realm.RealmResults
import android.text.format.DateFormat
import android.widget.TextView


class TodoListAdapter(realmResults: OrderedRealmCollection<Todo>) : 
    RealmBaseAdapter<Todo>(realmResults) {
    // 아이템에 표시하는 뷰를 구성합니다
    /*
        position : 리스트 뷰의 아이템 위치 입니다
        convertView : 재활용되는 아이템의 뷰 입니다
        parent : 부모 뷰 즉 여기서는 리스트 뷰의 참조를 가리킵니다
     */
    // getView() 메서드는 매 아이템이 화면에 보일 때 마다 호출됩니다
    // getView() 메서드의 두 번째 인자인 convertView 는 아이템 작성되기 전에는 null 이고 한 번 작성되면 이전에 작성했던 뷰를 전달합니다
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        val view : View
        val vh : ViewHolder

        if(convertView == null){ // convertView 가 null 이면 레이아웃을 작성합니다

            // LayoutInflater 클래스는 XML 레이아웃 파일을 코드로 불러오는 기능을 제공합니다
            // LayoutInflater.from(parent?.context) 메서드로 객체를 얻고 inflate()메서드로 XML 레이아웃을 읽어서 뷰로 반환하여 view 변수에 할당합니다
            view = LayoutInflater.from(parent?.context).inflate(R.layout.item_todo, parent, false)
            // XML 레이아웃 파일을 읽어서 뷰로 반환합니다
            // inflate(resource: Int, root : ViewGroup, attachToRoot : Boolean)
            /*
                resource : 불러올 레이아웃 XML 리소스 ID를 지정합니다
                root : 불러온 레이아웃 파일이 붙을 뷰그룹인 parent를 지정합니다
                attachToRoot : XML 파일을 불러올 때는 false를 지정합니다
             */

            vh = ViewHolder(view) // 뷰 홀더 객체를 초기화합니다
            view.tag = vh // 뷰 홀더 객체는 tag 프로퍼티로 view에 저장
                          // tag 프로퍼티에는 Any형으로 어떠한 객체도 저장할 수 있습니다

        }else{ // convertView가 null이 아니라면
            view = convertView // 이전에 작성했던 convertView를 재사용 합니다
            vh = view.tag as ViewHolder // 그리고 뷰 홀더 객체를 tag 프로퍼티에서 꺼냅니다
                                        // 반환되는 데이터 형이 Any 이므로 ViewHolder형으로 형변환을 합니다
        }

        // RealmBaseAdapter는 adapterData 프로퍼티를 제공합니다 여기서 데이터에 접근할 수 있습니다
        if (adapterData != null){ // 값이 있다면
            val item = adapterData!![position] // 해당위치의 데이터를 item 변수에 담습니다
            vh.textTextView.text = item.title // 할일 텍스트와
            vh.dateTextView.text = DateFormat.format("yyyy/MM/dd", item.date) // 날짜를 각각 텍스트 뷰에 표시합니다
        }

        return view // 완성된 view 변수를 반환합니다 이 뷰는 다음 번에 호출되면 convertView로 재사용됩니다
    }

    // getItemId() 메서드를 오바라이드 합니다 
    // 리스트 뷰를 클릭하여 이벤트를 처리할 때 인자로 position, id 등이 넘어오게 되는데 이때 넘어오는 id값을 결정합니다
    // 데이터 베이스를 다룰 때 레코드마다 고유한 아이디를 가지고 있는데 그것을 반환하도록 정의 합니다
    override fun getItemId(position: Int): Long {
        if(adapterData != null){
            // adapterView가 Realm 데이터를 가지고 있으므로
            // 요청한 해당 위치에 있는 데이터의 id 값을 반환 하도록 합니다
            return adapterData!![position].id
        }

        return super.getItemId(position)
    }
}

// 뷰 홀더 클래스는 전달 받은 view 에서 text1,text2 아이디를 가진 텍스트 뷰들의 참조를 저장하는 역할
class ViewHolder(view : View){
    val dateTextView : TextView = view.findViewById(R.id.text1)
    val textTextView : TextView = view.findViewById(R.id.text2)
}