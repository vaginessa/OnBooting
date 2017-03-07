package crixec.onbooting.script;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;

import crixec.onbooting.R;

/**
 * Created by crixec on 17-3-4.
 */

public class ScriptDialog extends DialogFragment implements DialogInterface.OnClickListener {
    private ScriptBean scriptBean;
    private TextInputLayout textInputLayout;
    private AppCompatTextView realPath;
    private AppCompatCheckBox checkBox1;
    private AppCompatCheckBox checkBox2;
    private IOnScriptChanged iOnScriptChanged;
    public static final int ACTION_CHANGE = 111, ACTION_NEW = 222;
    private int action;

    @SuppressLint("ValidFragment")
    public ScriptDialog(ScriptBean scriptBean, IOnScriptChanged iOnScriptChanged, int action) {
        this.scriptBean = scriptBean;
        this.iOnScriptChanged = iOnScriptChanged;
        this.action = action;
    }

    public ScriptDialog() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View contentView = getActivity().getLayoutInflater().inflate(R.layout.dialog_script, null, false);
        textInputLayout = (TextInputLayout) contentView.findViewById(R.id.layout1);
        realPath = (AppCompatTextView) contentView.findViewById(R.id.text1);
        checkBox1 = (AppCompatCheckBox) contentView.findViewById(R.id.checkbox1);
        checkBox2 = (AppCompatCheckBox) contentView.findViewById(R.id.checkbox2);
        realPath.setText(scriptBean.getRealPath());
        checkBox1.setChecked(scriptBean.isBootable());
        checkBox2.setChecked(scriptBean.isAsRoot());
        textInputLayout.getEditText().setText(scriptBean.getScriptName());
        return new AlertDialog.Builder(getActivity())
                .setView(contentView)
                .setPositiveButton(R.string.save, this)
                .setNegativeButton(android.R.string.no, this)
                .setNeutralButton(R.string.delete, this)
                .setCancelable(false)
                .create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        boolean isBootable = checkBox1.isChecked();
        boolean isAsRoot = checkBox2.isChecked();
        String scriptName = textInputLayout.getEditText().getText().toString();
        ScriptBean bean = new ScriptBean(scriptName, isBootable, scriptBean.getRealPath(), isAsRoot);
        if (which == DialogInterface.BUTTON_NEUTRAL) {
            if (iOnScriptChanged != null) iOnScriptChanged.onDeleted(scriptBean);
            return;
        }
        if (iOnScriptChanged != null) iOnScriptChanged.onChanged(bean, action);
    }

    public interface IOnScriptChanged {
        void onChanged(ScriptBean newBean, int action);

        void onDeleted(ScriptBean ben);
    }
}
