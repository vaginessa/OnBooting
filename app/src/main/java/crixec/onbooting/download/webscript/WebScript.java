package crixec.onbooting.download.webscript;

import crixec.onbooting.script.ScriptBean;

/**
 * Created by crixec on 17-3-6.
 */

public class WebScript {
    private ScriptBean scriptBean;
    private String downloadUrl;
    private int length;
    private int versionCode;
    private String versionName;
    private String description;
    private String author;
    private String scriptName;

    public String getScriptName() {
        return scriptName;
    }

    public void setScriptName(String scriptName) {
        this.scriptName = scriptName;
    }

    public ScriptBean getScriptBean() {
        return scriptBean;
    }

    public void setScriptBean(ScriptBean scriptBean) {
        this.scriptBean = scriptBean;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
