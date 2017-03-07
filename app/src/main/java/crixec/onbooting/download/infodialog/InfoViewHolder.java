package crixec.onbooting.download.infodialog;

import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import crixec.onbooting.R;

/**
 * Created by crixec on 17-3-6.
 */

public class InfoViewHolder extends RecyclerView.ViewHolder {
    AppCompatTextView KEY;
    AppCompatTextView VALUE;
    public View itemVew;

    public InfoViewHolder(View itemView) {
        super(itemView);
        this.itemVew = itemView;
        KEY = (AppCompatTextView) itemView.findViewById(R.id.key);
        VALUE = (AppCompatTextView) itemView.findViewById(R.id.value);
    }

    public CharSequence getkey() {
        return KEY.getText();
    }

    public void setKey(CharSequence text) {
        this.KEY.setText(text);
    }

    public CharSequence getValue() {
        return VALUE.getText();
    }

    public void setValue(CharSequence text) {
        this.VALUE.setText(text);
    }

    public View getItemVew() {
        return itemVew;
    }
}
