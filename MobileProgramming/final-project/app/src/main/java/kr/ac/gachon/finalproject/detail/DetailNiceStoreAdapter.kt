/**
 * 착한가게 상세보기 RecyclerView 어댑터
 *
 * @since: 2019-06-04
 * @author: 류일웅
 */
package kr.ac.gachon.finalproject.detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.detail_nice_store.view.*
import kr.ac.gachon.finalproject.R
import kr.ac.gachon.finalproject.model.NiceStoreData

class DetailNiceStoreAdapter(var newOtherList: ArrayList<NiceStoreData>) :
    RecyclerView.Adapter<DetailNiceStoreAdapter.ItemViewHolder>() {

    override fun getItemCount() = newOtherList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val rootView = LayoutInflater.from(parent.context).inflate(
            R.layout.detail_nice_store, parent, false
        )
        return ItemViewHolder(rootView)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bindOtherNiceStoreData(newOtherList[position])
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        /**
         * DetailActivity의 RecyclerView에 표시할 내용 설정
         * 표시할 내용: 착한가게의 대표 서비스 품목과 가격
         *
         * @since: 2019-06-04
         * @author: 류일웅
         * @param: newOtherList
         * @return: None
         */
        fun bindOtherNiceStoreData(newOtherList: NiceStoreData) {
            itemView.im_name_and_price.text = newOtherList.IM_NAME
            itemView.im_name_and_price.append(": ")
            itemView.im_name_and_price.append(newOtherList.IM_PRICE.toString())
        }

    }

}