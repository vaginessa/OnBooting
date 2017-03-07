package crixec.onbooting.download.infodialog;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import crixec.onbooting.script.OnScriptItemClickListener;
import crixec.onbooting.R;

/**
 * Created by crixec on 17-3-6.
 */

public class InfoAdapter extends RecyclerView.Adapter<InfoViewHolder> {
    private ArrayList<Info> infos;
    private OnScriptItemClickListener onScriptItemClickListener;


    public InfoAdapter(ArrayList<Info> infos) {
        this.infos = infos;
    }

    @Override
    public InfoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_info_item, parent, false);
        return new InfoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final InfoViewHolder holder, int position) {
        if (holder != null) {
            Info info = infos.get(position);
            if (info != null) {
                holder.setKey(info.getKey());
                holder.setValue(info.getValue());
                if (onScriptItemClickListener != null) {
                    holder.itemVew.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onScriptItemClickListener.onScriptItemClick(holder.getLayoutPosition());
                        }
                    });
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return infos.size();
    }

    public void setOnScriptItemClickListener(OnScriptItemClickListener onScriptItemClickListener) {
        this.onScriptItemClickListener = onScriptItemClickListener;
    }

}
