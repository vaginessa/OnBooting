package crixec.onbooting;

import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by crixec on 17-2-11.
 */

public class ScriptViewHolder extends RecyclerView.ViewHolder {
    public View itemVew;
    private AppCompatImageView bootableFlag;
    private AppCompatTextView scriptName;

    public ScriptViewHolder(View itemView) {
        super(itemView);
        this.itemVew = itemView;
        bootableFlag = (AppCompatImageView) itemView.findViewById(R.id.script_bootable_flag);
        scriptName = (AppCompatTextView) itemView.findViewById(R.id.script_name);
    }

    public AppCompatImageView getBootableFlag() {
        return bootableFlag;
    }

    public void setBootableFlag(int bootableFlag) {
        this.bootableFlag.setImageResource(bootableFlag);
    }

    public CharSequence getScriptName() {
        return scriptName.getText();
    }

    public void setScriptName(CharSequence scriptName) {
        this.scriptName.setText(scriptName);
    }

    public View getItemVew() {
        return itemVew;
    }

}
