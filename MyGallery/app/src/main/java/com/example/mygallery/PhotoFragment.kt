package com.example.mygallery

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_photo.*

private const val ARG__URI = "uri" // 클래스 선언 밖에 const 키워드를 사용하여 상수를 정의하면 컴파일 시간에 결정되는 상수가되고 이파일 내에서 어디든 사용 가능
                                   // 컴파일 시간 상수의 초기화는 String 또는 프리미티브형 으로만 초기화 가능

class PhotoFragment : Fragment() {
    private var uri : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 프래그먼트가 생성되면 onCreate() 메서드가 호출되고 ARG_URI 키에 저장된 uri값을 얻어서 변수에 저장합니다
        arguments?.let{
            uri = it.getString(ARG__URI)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // onCreateView() 메서드에서는 프래그먼트에 표시될 뷰를 생성합니다
        // 액티비티가 아닌곳에서 레이아웃 리소스를 가지고 오려면 LayoutInflater 객체의 inflate() 메서드를 사용합니다
        // R.layout.fragment_photo 레이아웃 파일을 가지고 와서 반환합니다
        return inflater.inflate(R.layout.fragment_photo, container, false)
    }

    // 이제 뷰가 완성된 직후에 호출되는 onViewCreated() 메서드를 오버라이드하고 Glide 라이브러리로 사진을 이미지뷰에 표시
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // view : 생성된 뷰
        // savedInstanceState : 상태를 저장하는 객체
        super.onViewCreated(view, savedInstanceState)
        Glide.with(this).load(uri).into(imageView) // Glide.with(this)로 사용 준비를 하고 load() 메서드에 uri값을 인자로 주고
                                                            // 해당 이미지를 부드럽게 로딩합니다. 이미지가 로디외면 into() 메서드로 imageView에 표시합니다
    }

    // newInstance() 메서드를 이용하여 프래그먼트를 생성할 수 있고 인자로 uri값을 전달합니다
    // 이 값은 Bundle 객체에 ARG_URI 키로 저장되고 arguments 프로퍼티에 저장합니다
    companion object {
        @JvmStatic
        fun newInstance(uri : String) =
            PhotoFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG__URI, uri)
                }
            }
    }
}