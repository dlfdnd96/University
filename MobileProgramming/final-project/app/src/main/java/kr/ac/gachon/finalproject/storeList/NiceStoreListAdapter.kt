/**
 * 상품 분류에 대한 착한가게 RecyclerView 어댑터
 *
 * @since: 2019-06-04
 * @author: 류일웅
 */
package kr.ac.gachon.finalproject.storeList

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.nice_store_list_fresh.view.*
import kr.ac.gachon.finalproject.R
import kr.ac.gachon.finalproject.detail.DetailNiceStoreActivity
import kr.ac.gachon.finalproject.model.NiceStoreData
import kr.ac.gachon.finalproject.storeList.NiceStoreListActivity.Companion.lookedCurrentStore

class NiceStoreListAdapter(var newList: ArrayList<NiceStoreData>)
    : RecyclerView.Adapter<NiceStoreListAdapter.ItemViewHolder>() {

    override fun getItemCount() = newList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val rootView = LayoutInflater.from(parent.context).inflate(
            R.layout.nice_store_list_fresh, parent, false
        )
        return ItemViewHolder(rootView)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bindItems(newList[position])
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        /**
         * NiceStoreListActivity의 RecyclerView에 표시할 내용 설정
         * 표시할 내용: 착한가게 이름, 주소, 추천수
         *
         * @since: 2019-06-04
         * @author: 류일웅
         * @param: niceStoreData
         * @return: None
         */
        fun bindItems(niceStoreData: NiceStoreData) {
            itemView.nice_store_name.text = niceStoreData.SH_NAME
            itemView.nice_store_addr.text = niceStoreData.SH_ADDR
            itemView.nice_store_rcmn.text = niceStoreData.SH_RCMN.toString()

            /*
            착한가게를 클릭하면 상세보기로 이동.
            착한가게 id, 이름, 서비스 분류 이름을 같이 전달
             */
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, DetailNiceStoreActivity::class.java)
                var redundancy = false
                intent.putExtra("detailStoreId", niceStoreData.SH_ID)
                intent.putExtra("detailStoreName", niceStoreData.SH_NAME)
                intent.putExtra("detailCodeItemName", niceStoreData.INDUTY_CODE_SE_NAME)

                /*
                현재 클릭한 착한가게가 최근 본 착한가게 리스트와 중복이 있는지 검사
                 */
                for (i in 0 until lookedCurrentStore.size) {
                    if (lookedCurrentStore[i] == niceStoreData) {
                        /*
                        가장 최근에 본 것이랑 일치하면 배열에 추가 작업 안 함
                         */
                        if (i == 0) {
                            redundancy = true

                            break
                        }

                        /*
                        최근 본 착한가게 리스트 중간에 중복이 있다면 리스트의 최상단으로 배치하고 나머지 정렬
                         */
                        configureLookedCurrentStore(lookedCurrentStore, niceStoreData, i)
                        redundancy = true

                        break
                    }
                }

                /*
                중복도 없고 최근 본 착한가게의 수가 5보다 적다면 리스트에 추가 후 정렬
                 */
                if (lookedCurrentStore.size < 5 && !redundancy) {
                    lookedCurrentStore.add(niceStoreData)
                    sortLookedCurrentStore(lookedCurrentStore, niceStoreData)
                }
                /*
                중복은 없고 최근 본 착한가게의 수가 5보다 크다면 리스트에 정렬만 수행
                 */
                else if (!redundancy) {
                    sortLookedCurrentStore(lookedCurrentStore, niceStoreData)
                }

                itemView.context.startActivity(intent)
            }
        }

    }

    /**
     * 최근 본 착한가게에 정렬해서 추가하는 기능
     *
     * @since: 2019-06-04
     * @author: 류일웅
     * @param: dataArray, currentStoreData
     * @return: None
     */
    fun sortLookedCurrentStore(dataArray: MutableList<NiceStoreData>, currentStoreData: NiceStoreData) {
        /*
        배열의 끝부분부터를 가장 최근에 본 항목으로 설정
         */
        for (i in (dataArray.size - 1) downTo 1) {
            dataArray[i] = dataArray[i - 1]
        }

        /*
        가장 최근 본 착한가게를 리스트의 맨 위로 설정
         */
        dataArray[0] = currentStoreData
    }

    /**
     * 중복된 최근 본 착한가게가 있다면 중복된 가게를 최상단으로 올리고 나머지를 정렬하는 기능
     *
     * @since: 2019-06-04
     * @author: 류일웅
     * @param: dataArray, currentStoreData, index
     * @return: None
     */
    fun configureLookedCurrentStore(
        dataArray: MutableList<NiceStoreData>, currentStoreData: NiceStoreData, index: Int
    ) {
        /*
        중복된 부분이 있는 위치까지만 정렬
         */
        for (i in index downTo 1) {
            dataArray[i] = dataArray[i - 1]
        }

        /*
        가장 최근 본 착한가게를 리스트의 맨 위로 설정
         */
        dataArray[0] = currentStoreData
    }

}