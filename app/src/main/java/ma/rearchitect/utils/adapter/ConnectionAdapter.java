package opswat.com.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import opswat.com.R;
import opswat.com.network.model.connection.Connection;
import opswat.com.view.viewholder.ItemConnectionViewHolder;

/**
 * Created by LenVo on 7/15/18.
 */

public class ConnectionAdapter extends RecyclerView.Adapter<ItemConnectionViewHolder> {
    private List<Connection> listIps = new ArrayList<>();
    public static final int TYPE_IP = 0;
    public static final int TYPE_IP_DETAIL = 1;

    private int type = TYPE_IP;

    @Override
    public int getItemCount() {
        return listIps.size();
    }

    @NonNull
    @Override
    public ItemConnectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout = (type == TYPE_IP)? R.layout.item_connection : R.layout.item_connection_detail;
        return new ItemConnectionViewHolder(LayoutInflater.from(parent.getContext()).
                inflate(layout, parent, false), parent.getContext());
    }

    @Override
    public void onBindViewHolder(@NonNull ItemConnectionViewHolder holder, int position) {
        if (position < listIps.size()) {
            Connection connection = listIps.get(position);
            holder.onBind(connection);
        }
    }

    public void setListIps(List<Connection> listIps) {
        this.listIps = listIps;
        notifyDataSetChanged();
    }

    public ConnectionAdapter(int type) {
        this.type = type;
    }
}
