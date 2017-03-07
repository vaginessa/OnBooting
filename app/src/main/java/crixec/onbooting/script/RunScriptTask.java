package crixec.onbooting.script;

import android.content.ClipboardManager;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;

import crixec.onbooting.R;
import crixec.onbooting.util.ShellUtils;
import crixec.onbooting.ui.TerminalDialog;

/**
 * Created by crixec on 17-3-5.
 */

public class RunScriptTask extends AsyncTask<String, Output, Boolean> {

    private ScriptBean scriptBean;
    private Context context;
    private TerminalDialog dialog;

    public RunScriptTask(Context context, ScriptBean scriptBean) {
        this.context = context;
        this.scriptBean = scriptBean;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = new TerminalDialog(context);
        dialog.setCancelable(false);
        dialog.setTitle(scriptBean.getScriptName());
        dialog.show();
    }


    @Override
    protected void onPostExecute(Boolean msg) {
        super.onPostExecute(msg);
        dialog.setSecondButton(R.string.copy, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE)).setText(dialog.getContent());
            }
        });
        if (msg) {
            dialog.writeStdout(context.getString(R.string.run_script_successful));
        } else {
            dialog.writeStderr(context.getString(R.string.run_script_failed));
        }
    }


    @Override
    protected Boolean doInBackground(String... params) {
        return ShellUtils.exec(scriptBean.getRealPath(), dialog, scriptBean.isAsRoot()) == 0;
    }

}
