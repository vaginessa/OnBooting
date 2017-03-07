package crixec.onbooting.download.webscript;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import crixec.onbooting.script.OnScriptItemClickListener;
import crixec.onbooting.R;

/**
 * Created by crixec on 17-2-11.
 */

public class WebScriptAdapter extends RecyclerView.Adapter<WebScriptViewHolder> {
    private List<WebScript> scripts;
    private OnScriptItemClickListener onScriptItemClickListener;

    public WebScriptAdapter(List<WebScript> scripts) {
        this.scripts = scripts;
    }

    @Override
    public WebScriptViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_webscript_item, parent, false);
        return new WebScriptViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final WebScriptViewHolder holder, int position) {
        if (holder != null) {
            WebScript script = scripts.get(position);
            if (script != null) {
                holder.setScriptName(script.getScriptName());
                holder.setDescription(script.getDescription());
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

    public void setOnScriptItemClickListener(OnScriptItemClickListener onScriptItemClickListener) {
        this.onScriptItemClickListener = onScriptItemClickListener;
    }

    @Override
    public int getItemCount() {
        return scripts.size();
    }

}
