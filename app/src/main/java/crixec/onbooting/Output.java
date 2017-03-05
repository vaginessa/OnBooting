package crixec.onbooting;

/**
 * Created by crixec on 17-3-5.
 */

public class Output {
    private boolean isStdout;
    private String content;

    public Output() {
    }

    public Output(boolean isStdout, String content) {
        this.isStdout = isStdout;
        this.content = content;
    }

    public boolean isStdout() {
        return isStdout;
    }

    public void setStdout(boolean stdout) {
        isStdout = stdout;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
