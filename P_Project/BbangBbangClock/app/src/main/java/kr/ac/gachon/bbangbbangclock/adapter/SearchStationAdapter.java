package kr.ac.gachon.bbangbbangclock.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import kr.ac.gachon.bbangbbangclock.R;
import kr.ac.gachon.bbangbbangclock.data.SearchStationData;

/**
 * 대중교통 정류장 정보를 recycler view에 연결하는 어댑터
 *
 * @since : 19-11-25
 * @author : 류일웅
 */
public class SearchStationAdapter
        extends RecyclerView.Adapter<SearchStationAdapter.SearchStationViewHolder> {

    private List<SearchStationData> searchStationList;

    // 리스너 객체 참조를 저장하는 변수
    private static OnItemClickListener mListener = null;

    public SearchStationAdapter(List<SearchStationData> data) {
        searchStationList = data;
    }

    @NonNull
    @Override
    public SearchStationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_item_station, parent, false);

        return new SearchStationViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchStationViewHolder holder, int position) {
        SearchStationData searchStationData = searchStationList.get(position);

        holder.stationType.setImageResource(R.drawable.ic_bus);
        holder.stationName.setText(searchStationData.getStationName());
        holder.route.setText(searchStationData.getArsID());
        holder.route.append(", ");
        holder.route.append(searchStationData.getCityName());
        holder.route.append(", ");
        holder.route.append(searchStationData.getDong());

        if (searchStationData.getLaneName() != null) {
            holder.stationType.setImageResource(R.drawable.ic_subway);
            holder.route.append(", ");
            holder.route.append(searchStationData.getLaneName());
        }
    }

    @Override
    public int getItemCount() {
        return searchStationList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    /**
     * OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
     *
     * @since : 19-12-09
     * @author : 류일웅
     * @param : listener
     * @return :
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public static class SearchStationViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.station_item_name)
        TextView stationName;
        @BindView(R.id.station_item_route)
        TextView route;
        @BindView(R.id.station_item_type)
        ImageView stationType;

        public SearchStationViewHolder(View view) {
            super(view);

            ButterKnife.bind(this, view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int pos = getAdapterPosition();

                    // 리스너 객체의 메서드 호출.
                    if (pos != RecyclerView.NO_POSITION) {
                        mListener.onItemClick(v, pos);
                    }
                }
            });
        }
    }

}
