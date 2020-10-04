/**
 * 추천 애견용품을 네이버쇼핑으로 검색해주는 화면
 *
 * @author: 류일웅
 * @since: 19.10.31
 */
package kr.ac.gachon.searchdogs.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ExpandableListView
import androidx.appcompat.app.AppCompatActivity
import kr.ac.gachon.searchdogs.R
import kr.ac.gachon.searchdogs.adapter.RecommendAdapter

class RecommendActivity : AppCompatActivity() {

    private var expandableListView: ExpandableListView? = null
    private var expandableListViewAdapter: RecommendAdapter? = null
    private var listDataGroup: ArrayList<String>? = null
    private var listDataChild: HashMap<String, List<String>>? = null
    private val titleInt: Array<Int> = arrayOf(
        R.string.cloth, R.string.accessory, R.string.outside_product,
        R.string.bowl_tableware, R.string.bath_beauty_care, R.string.toy
    )
    private val childInt: Array<Int> = arrayOf(
        R.array.cloth_array, R.array.accessory_array,
        R.array.outside_product_array, R.array.bath_beauty_care_array
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recommend)

        initViews()

        initListeners()

        initObjects()

        initListData()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun initViews() {
        expandableListView = findViewById(R.id.recommend_ExListView)
    }
    
    private fun initListeners() {
        expandableListView!!.setOnChildClickListener(object: ExpandableListView.OnChildClickListener {
            override fun onChildClick(
                parent: ExpandableListView?,
                v: View?,
                groupPosition: Int,
                childPosition: Int,
                id: Long
            ): Boolean {
                val urlString = "https://msearch.shopping.naver.com/search/all.nhn?query=" +
                        listDataChild!![listDataGroup!![groupPosition]]!![childPosition] +
                        "&cat_id=&frm=NVSHATC"

                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(urlString))
                startActivity(intent)

                return false
            }
        })

        expandableListView!!.setOnGroupExpandListener(object: ExpandableListView.OnGroupExpandListener {
            override fun onGroupExpand(groupPosition: Int) {

            }
        })

        expandableListView!!.setOnGroupCollapseListener(object: ExpandableListView.OnGroupCollapseListener {
            override fun onGroupCollapse(groupPosition: Int) {

            }
        })
    }

    private fun initObjects() {
        listDataGroup = ArrayList()
        listDataChild = HashMap()
        expandableListViewAdapter = RecommendAdapter(this, listDataGroup!!, listDataChild!!)
        expandableListView!!.setAdapter(expandableListViewAdapter)
    }

    private fun initListData() {
        for (ti in titleInt) {
            listDataGroup!!.add(getString(ti))
        }

        val childList = ArrayList<String>()

        for ((index, ci) in childInt.withIndex()) {
            val mArray = resources.getStringArray(ci)

            for (item in mArray) {
                childList.add(item)
            }

            listDataChild!!.put(listDataGroup!![index], childList)
        }

        expandableListViewAdapter!!.notifyDataSetChanged()
    }

}