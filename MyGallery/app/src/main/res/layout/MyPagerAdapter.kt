package layout

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class MyPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
    // 뷰페이저가 표시할 프래그먼트 목록
    // 어뎁터가 프래그먼트의 목록을 리스트로 가지도록 합니다
    // 이목록은 updateFragments() 메서드를 사용해 외부에서 추가할 수 있습니다
    private val items = ArrayList<Fragment>()
    
    // position 위치의 프래그먼트
    // getItem() 메서드에는 position 위치에 어떤 프레그먼트를 표시할지를 정의해줌
    override fun getItem(position: Int): Fragment {
        return items[position]
    }

    // 아이템의 개수
    // getCount() 메서드에는 아이템 (프래그먼트) 개수를 정의해줍니다
    override fun getCount(): Int {
        return items.size
    }

    // 아이템 갱신
    fun updateFragments(items : List<Fragment>){
        this.items.addAll(items)
    }
}