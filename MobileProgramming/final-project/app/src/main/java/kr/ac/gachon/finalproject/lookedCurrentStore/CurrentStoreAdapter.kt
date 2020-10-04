/**
 * 최근에 본 착한가게 RecyclerView 어댑터
 *
 * @since: 2019-06-04
 * @author: 류일웅
 */
package kr.ac.gachon.finalproject.lookedCurrentStore

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.current_store_list.view.*
import kr.ac.gachon.finalproject.R
import kr.ac.gachon.finalproject.detail.DetailNiceStoreActivity
import kr.ac.gachon.finalproject.model.NiceStoreData

class CurrentStoreAdapter(var newList: MutableList<NiceStoreData>)
    : RecyclerView.Adapter<CurrentStoreAdapter.ItemViewHolder>() {

    override fun getItemCount() = newList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val rootView = LayoutInflater.from(parent.context).inflate(
            R.layout.current_store_list, parent, false
        )
        return ItemViewHolder(rootView)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bindItems(newList[position])
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        /**
         * CurrentActivity의 RecyclerView에 내용을 표시해주는 기능
         * 표시할 내용: 착한가게 이름, 주소, 추천수
         *
         * @since: 2019-06-04
         * @author: 류일웅
         * @param: niceStoreData
         * @return: None
         */
        fun bindItems(niceStoreData: NiceStoreData) {
            itemView.current_store_name.text = niceStoreData.SH_NAME
            itemView.current_store_addr.text = niceStoreData.SH_ADDR
            itemView.current_store_rcmn.text = niceStoreData.SH_RCMN.toString()

            /*
            최근 본 착한가게를 클릭하면 상세보기로 이동
             */
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, DetailNiceStoreActivity::class.java)
                intent.putExtra("detailStoreId", niceStoreData.SH_ID)
                intent.putExtra("detailStoreName", niceStoreData.SH_NAME)

                itemView.context.startActivity(intent)
            }
        }

    }

}