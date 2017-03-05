package crixec.onbooting;

import android.app.Application;

/**
 * Created by crixec on 17-3-5.
 */

public class OnBooting extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ScriptManager.init(this);
    }
}
