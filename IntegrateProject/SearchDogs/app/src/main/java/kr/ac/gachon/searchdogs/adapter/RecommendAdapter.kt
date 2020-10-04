/**
 * ExpandableList 어댑터
 *
 * @author: 류일웅
 * @since: 19.10.31
 */
package kr.ac.gachon.searchdogs.adapter

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView
import kr.ac.gachon.searchdogs.R

class RecommendAdapter(
    context: Context,
    listDataGroup: List<String>,
    listChildData: HashMap<String, List<String>>
) : BaseExpandableListAdapter() {

    private val mContext = context
    private val mListDataGroup = listDataGroup
    private val mListDataChild = listChildData

    override fun getChildrenCount(groupPosition: Int): Int {
        return mListDataChild[mListDataGroup[groupPosition]]!!.size
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return mListDataChild[mListDataGroup[groupPosition]]!![childPosition]
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val childText = getChild(groupPosition, childPosition) as String
        var mChildConvertView = convertView

        if (mChildConvertView == null) {
            val layoutInflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            mChildConvertView = layoutInflater.inflate(R.layout.recommend_list, null)
        }

        val textViewChild = mChildConvertView?.findViewById<TextView>(R.id.recommend_list_txt)

        textViewChild?.text = childText

        return mChildConvertView!!
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun getGroup(groupPosition: Int): Any {
        return mListDataGroup[groupPosition]
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val headerTitle = getGroup(groupPosition) as String
        var mParentConvertView = convertView

        if (mParentConvertView == null) {
            val layoutInflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            mParentConvertView = layoutInflater.inflate(R.layout.recommend_group, null)
        }

        val textViewGroup = mParentConvertView?.findViewById<TextView>(R.id.recommend_group_txt)

        textViewGroup?.setTypeface(null, Typeface.BOLD)
        textViewGroup?.text = headerTitle

        return mParentConvertView!!
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getGroupCount(): Int {
        return mListDataGroup.size
    }

}