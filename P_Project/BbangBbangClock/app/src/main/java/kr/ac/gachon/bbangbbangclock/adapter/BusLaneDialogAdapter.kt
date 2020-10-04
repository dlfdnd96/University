package kr.ac.gachon.bbangbbangclock.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import kr.ac.gachon.bbangbbangclock.R
import org.json.JSONArray
import org.json.JSONException

/**
 * 버스 노선 정보를 dialog에 연결하는 어댑터
 *
 * @since : 19-11-25
 * @author : 류일웅
 */
class BusLaneDialogAdapter (context: Context, jsonArray: JSONArray, countNum: Int) : BaseAdapter() {

    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)
    private val busJsonArray = jsonArray
    private var busLaneData: BusLaneData? = null
    private var count = countNum

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val busLaneHolder: BusLaneHolder
        var view: View? = convertView

        // dialog에 표시할 textview 설정
        if (view == null) {
            view = layoutInflater.inflate(R.layout.layout_bus_list, parent, false)

            busLaneHolder = BusLaneHolder(view.findViewById(R.id.layout_bus_list_info)
                    , view.findViewById(R.id.layout_bus_list_route_id))
            view.tag = busLaneHolder
        } else {
            busLaneHolder = view.tag as BusLaneHolder
        }

        // 버스 노선 정보들을 데이터 클래스에 저장하여 정보를 표시
        try {
            var index = 0

            while (index < busJsonArray.length()) {
                val stationObject = busJsonArray.getJSONObject(index)

                busLaneData = BusLaneData(
                        stationObject.getString("busNo"),
                        stationObject.getString("busStartPoint"),
                        stationObject.getString("busEndPoint"),
                        stationObject.getString("busLocalBlID")
                )

                when (position) {
                    index -> {
                        busLaneHolder.busLane.text = busLaneData!!.busNum
                        busLaneHolder.busLane.append(", ")
                        busLaneHolder.busLane.append(busLaneData!!.busStartPoint)
                        busLaneHolder.busLane.append(", ")
                        busLaneHolder.busLane.append(busLaneData!!.busEndPoint)
                        busLaneHolder.busRouteId.text = busLaneData!!.busLocalBlId
                    }
                }

                index++
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return view!!
    }

    override fun getItem(position: Int): Any {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return count
    }

    data class BusLaneHolder(val busLane: TextView, val busRouteId: TextView)
    data class BusLaneData(val busNum: String, val busStartPoint: String, val busEndPoint: String
                           , val busLocalBlId: String)

}