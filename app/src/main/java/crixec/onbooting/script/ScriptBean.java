package crixec.onbooting.script;

/**
 * Created by crixec on 17-3-3.
 */

public class ScriptBean {
    private String scriptName;
    private boolean isBootable;
    private String realPath;
    private boolean asRoot;

    public ScriptBean(String scriptName, boolean isBootable, String realPath, boolean asRoot) {
        this.scriptName = scriptName;
        this.isBootable = isBootable;
        this.realPath = realPath;
        this.asRoot = asRoot;
    }

    public boolean isAsRoot() {
        return asRoot;
    }

    public void setAsRoot(boolean asRoot) {
        this.asRoot = asRoot;
    }

    public ScriptBean() {
    }

    public String getScriptName() {
        return scriptName;
    }

    public void setScriptName(String scriptName) {
        this.scriptName = scriptName;
    }

    public boolean isBootable() {
        return isBootable;
    }

    public void setBootable(boolean bootable) {
        isBootable = bootable;
    }

    public String getRealPath() {
        return realPath;
    }

    public void setRealPath(String realPath) {
        this.realPath = realPath;
    }
}
