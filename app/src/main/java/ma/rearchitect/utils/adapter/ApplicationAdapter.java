package opswat.com.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import opswat.com.R;
import opswat.com.network.model.application.Application;
import opswat.com.view.viewholder.ItemApplicationViewHolder;

/**
 * Created by LenVo on 7/15/18.
 */

public class ApplicationAdapter extends RecyclerView.Adapter<ItemApplicationViewHolder> {
    private List<Application> listApplications = new ArrayList<>();
    public static final int TYPE_APP = 0;
    public static final int TYPE_APP_DETAIL = 1;
    private int type = TYPE_APP;

    public ApplicationAdapter(int type) {
        this.type = type;
    }

    @Override
    public int getItemCount() {
        return listApplications.size();
    }

    @NonNull
    @Override
    public ItemApplicationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemApplicationViewHolder(LayoutInflater.from(parent.getContext()).
                inflate(R.layout.item_application, parent, false), parent.getContext());
    }

    @Override
    public void onBindViewHolder(@NonNull ItemApplicationViewHolder holder, int position) {
        if (position < listApplications.size()) {
            Application application = listApplications.get(position);
            holder.onBind(application, type);
        }
    }

    public void setListApplications(List<Application> listApplications) {
        this.listApplications.clear();
        this.listApplications = listApplications;
        notifyDataSetChanged();
    }
}
