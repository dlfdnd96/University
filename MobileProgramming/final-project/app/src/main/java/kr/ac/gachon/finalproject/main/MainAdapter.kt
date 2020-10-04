/**
 * 메인 액티비티 RecyclerView 어댑터
 *
 * @since: 2019-06-04
 * @author: 류일웅
 */
package kr.ac.gachon.finalproject.main

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.nice_store_item_code_name.view.*
import kr.ac.gachon.finalproject.R
import kr.ac.gachon.finalproject.model.NiceStores
import kr.ac.gachon.finalproject.storeList.NiceStoreListActivity

class MainAdapter : RecyclerView.Adapter<MainAdapter.ItemViewHolder>() {

    override fun getItemCount() = NiceStores.values().size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val rootView = LayoutInflater.from(parent.context).inflate(
            R.layout.nice_store_item_code_name, parent, false
        )
        return ItemViewHolder(rootView)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bindNiceStoreData(NiceStores.values()[position])
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        /**
         * MainActivity의 RecyclerView에 표시할 내용 설정
         * 표시할 내용: 서비스 분류 이름
         *
         * @since: 2019-06-04
         * @author: 류일웅
         * @param: niceStoreData
         * @return: None
         */
        fun bindNiceStoreData(niceStoreData: NiceStores) {
            itemView.txt_main_store_code_name.text = niceStoreData.holder
            itemView.store_image.setImageResource(niceStoreData.imageId)

            /*
            서비스 분류를 선택하면 착한가게 리스트 화면으로 전환.
            NiceStoreData에 있는 enum data를 같이 전달
             */
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, NiceStoreListActivity::class.java)
                intent.putExtra("selectedItem", niceStoreData.name)
                intent.putExtra("selectedCode", niceStoreData.mcode.toLong())
                intent.putExtra("selectedCodeItemName", niceStoreData.holder)

                itemView.context.startActivity(intent)
            }
        }

    }

}