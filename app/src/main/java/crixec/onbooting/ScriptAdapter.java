package crixec.onbooting;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by crixec on 17-2-11.
 */

public class ScriptAdapter extends RecyclerView.Adapter<ScriptViewHolder> {
    private List<ScriptBean> scripts;
    private OnScriptItemClickListener onScriptItemClickListener;

    public ScriptAdapter(List<ScriptBean> scripts) {
        this.scripts = scripts;
    }

    @Override
    public ScriptViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_script_item, parent, false);
        return new ScriptViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ScriptViewHolder holder, int position) {
        if (holder != null) {
            ScriptBean script = scripts.get(position);
            if (script != null) {
                holder.setScriptName(script.getScriptName());
                holder.setBootableFlag(script.isBootable() ? R.drawable.ic_bootable : R.drawable.ic_not_bootable);
                if (onScriptItemClickListener != null) {
                    holder.itemVew.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onScriptItemClickListener.onScriptItemClick(holder.getLayoutPosition());
                        }
                    });
                    holder.itemVew.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            onScriptItemClickListener.onScriptItemLongClick(holder.getLayoutPosition());
                            return false;
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
