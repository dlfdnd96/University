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
 * 지하철역의 이전역, 다음역을 dialog에 연결하는 어댑터
 *
 * @since : 19-11-25
 * @author : 류일웅
 */
//아래 클래스는 BusLaneDialogAdapter 와 동일함. by 류일웅
class SubwayNearbyStationDialogAdapter (
        context: Context,
        jsonArray: JSONArray,
        countNum: Int
) : BaseAdapter() {

    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)
    private val subwayJsonArray = jsonArray
    private val count = countNum
    private var subwayNearbyStationData: SubwayNearbyStationData? = null

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val subwayWayCodeHolder: SubwayWayCodeHolder
        var view: View? = convertView

        if (view == null) {
            view = layoutInflater.inflate(R.layout.layout_subway_list, parent, false)

            subwayWayCodeHolder = SubwayWayCodeHolder(view.findViewById(R.id.layout_subway_list_info),
                    view.findViewById(R.id.layout_subway_list_direction))
            view.tag = subwayWayCodeHolder
        } else
            subwayWayCodeHolder = view.tag as SubwayWayCodeHolder

        try {
            var index = 0

            while (index < subwayJsonArray.length()) {
                val stationObject = subwayJsonArray.getJSONObject(index)

                subwayNearbyStationData = SubwayNearbyStationData(
                        stationObject.getString("stationName"),
                        stationObject.getString("laneName"),
                        stationObject.getString("direction")
                )

                when (position) {
                    index -> {
                        subwayWayCodeHolder.subwayNearbyStation.text =
                                subwayNearbyStationData!!.stationName
                        subwayWayCodeHolder.subwayNearbyStation.append("방면")
                        subwayWayCodeHolder.subwayDirection.text =
                                subwayNearbyStationData!!.direction
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

    data class SubwayWayCodeHolder(val subwayNearbyStation: TextView, val subwayDirection: TextView)
    data class SubwayNearbyStationData(val stationName: String, val stationLane: String,
                                       val direction: String)

}