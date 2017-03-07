package crixec.onbooting;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import crixec.onbooting.script.ScriptBean;
import crixec.onbooting.script.ScriptManager;
import crixec.onbooting.util.ShellUtils;

public class AutoRunService extends Service {
    public AutoRunService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ScriptManager.init(this);
        ScriptManager.readAll();
        List<ScriptBean> beans = ScriptManager.getBootableScripts();
        for (ScriptBean bean : beans) {
            Toast.makeText(this, String.format(getString(R.string.running_script), bean.getScriptName()), Toast.LENGTH_SHORT).show();
            Log.i(getClass().getSimpleName(), bean.getScriptName());
            int r = ShellUtils.exec(bean.getRealPath(), null, bean.isAsRoot());
            Log.i(getClass().getSimpleName(), "result_code=" + r);
        }
        return super.onStartCommand(intent, flags, startId);
    }
}
