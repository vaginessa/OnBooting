package crixec.onbooting.download.webscript;

import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import crixec.onbooting.R;

/**
 * Created by crixec on 17-2-11.
 */

public class WebScriptViewHolder extends RecyclerView.ViewHolder {
    public View itemVew;
    private AppCompatTextView description;
    private AppCompatTextView scriptName;

    public WebScriptViewHolder(View itemView) {
        super(itemView);
        this.itemVew = itemView;
        description = (AppCompatTextView) itemView.findViewById(R.id.script_description);
        scriptName = (AppCompatTextView) itemView.findViewById(R.id.script_name);
    }

    public CharSequence getDescription() {
        return description.getText();
    }

    public void setDescription(CharSequence description) {
        this.description.setText(description);
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
