package crixec.onbooting.download.infodialog;

/**
 * Created by crixec on 17-3-6.
 */

public class Info {
    private String key;
    private String value;

    public Info(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public Info() {

    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
