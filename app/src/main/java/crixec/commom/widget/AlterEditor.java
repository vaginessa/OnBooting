package crixec.commom.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import crixec.onbooting.R;

public class AlterEditor extends EditText {
    private Rect rect;
    private Paint paint;
    private Paint deviderLine;
    private int lastLineLevel;
    private int lastLineCount;
    private int basePadding = 8;
    private int lineNumberPadding = basePadding;
    private int codeColor;
    private int deviderColor;
    private int lineNumberColor;

    public AlterEditor(Context context) {
        super(context);
        init();
    }

    public AlterEditor(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typed = context.obtainStyledAttributes(attrs, R.styleable.AlterEditorAttributes);
        codeColor = typed.getColor(R.styleable.AlterEditorAttributes_codeColor, Color.BLACK);
        deviderColor = typed.getColor(R.styleable.AlterEditorAttributes_deviderColor, Color.GRAY);
        lineNumberColor = typed.getColor(R.styleable.AlterEditorAttributes_lineNumberColor, Color.parseColor("#212121"));
        basePadding = (int) typed.getDimension(R.styleable.AlterEditorAttributes_basePadding, 8);
        typed.recycle();
        init();
    }

    private void init() {
        if (rect == null) {
            setInputType(InputType.TYPE_CLASS_TEXT |
                    InputType.TYPE_TEXT_FLAG_MULTI_LINE |
                    InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
            setGravity(Gravity.START | Gravity.TOP);
            setEllipsize(TextUtils.TruncateAt.END);
            setTypeface(Typeface.MONOSPACE);
            setTextSize(14);
            setPadding(basePadding, 0, 0, 0);
            rect = new Rect();
            paint = new Paint();
            paint.setFakeBoldText(true);
            paint.setAntiAlias(true);
            deviderLine = new Paint();
            deviderLine.setColor(deviderColor);
            paint.setStyle(Paint.Style.FILL);
            paint.setTypeface(Typeface.MONOSPACE);
            paint.setTextSize(getTextSize() * 8 / 10);
            paint.setColor(lineNumberColor);
            setTextColor(codeColor);
            setCursorVisible(true);
            setHorizontallyScrolling(true);
            SyntaxColorSet.colorKeywords = ResourcesCompat.getColor(getResources(), R.color.colorCodeKeywords, null);
            SyntaxColorSet.colorStructures = ResourcesCompat.getColor(getResources(), R.color.colorCodeStructures, null);
            SyntaxColorSet.colorVariables = ResourcesCompat.getColor(getResources(), R.color.colorCodeVariables, null);
        }
    }

    public StringBuilder content() {
        return new StringBuilder(getText().toString());
    }

    private static class SyntaxPattern {
        static Pattern KEYWORDS = Pattern.compile("(^[\\t ]*.*(\\[|\\[\\[|acpid|adjtimex|ar|arp|arping|ash|awk|basename|blockdev|brctl|bunzip2|bzcat|bzip2|cal|cat|chgrp|chmod|chown|chroot|chvt|clear|cmp|cp|cpio|cttyhack|cut|date|dc|dd|deallocvt|depmod|devmem|df|diff|dirname|dmesg|dnsdomainname|dos2unix|du|dumpkmap|dumpleases|echo|egrep|env|expand|expr|false|fgrep|find|fold|free|freeramdisk|fstrim|ftpget|ftpput|getopt|getty|grep|groups|gunzip|gzip|halt|head|hexdump|hostid|hostname|httpd|hwclock|id|ifconfig|init|insmod|ionice|ip|ipcalc|kill|killall|klogd|last|less|ln|loadfont|loadkmap|logger|login|logname|logread|losetup|ls|lsmod|lzcat|lzma|lzop|lzopcat|md5sum|mdev|microcom|mkdir|mkfifo|mknod|mkswap|mktemp|modinfo|modprobe|more|mount|mt|mv|nameif|nc|netstat|nslookup|od|openvt|patch|pidof|ping|ping6|pivot_root|poweroff|printf|ps|pwd|rdate|readlink|realpath|reboot|renice|reset|rev|rm|rmdir|rmmod|route|rpm|rpm2cpio|run-parts|sed|seq|setkeycodes|setsid|sh|sha1sum|sha256sum|sha512sum|sleep|sort|start-stop-daemon|stat|strings|stty|swapoff|swapon|switch_root|sync|sysctl|syslogd|tac|tail|tar|taskset|tee|telnet|test|tftp|time|timeout|top|touch|tr|traceroute|traceroute6|true|tty|udhcpc|udhcpd|umount|uname|uncompress|unexpand|uniq|unix2dos|unlzma|unlzop|unxz|unzip|uptime|usleep|uudecode|uuencode|vconfig|vi|watch|watchdog|wc|wget|which|who|whoami|xargs|xz|xzcat|yes|zcat)[\\t ]{1})", Pattern.MULTILINE);
        static Pattern STRUCTURES = Pattern.compile("(([\" \']{1}(.*)[\" \']{1}))", Pattern.MULTILINE);
        static Pattern VARIABLES = Pattern.compile("^[\\t ]*.*([a-z][A-Z].*)*(?==)", Pattern.MULTILINE);
    }

    private static class SyntaxColorSet {
        static int colorKeywords;
        static int colorStructures;
        static int colorVariables;
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        Editable e = getText();
        //Log.i(AlterEditor.class.getSimpleName(), "text=" + text + " start=" + start + "   " + lengthAfter + " " + lengthBefore);
        for (Matcher m = SyntaxPattern.KEYWORDS.matcher(e); m.find(); ) {
            e.setSpan(
                    new ForegroundColorSpan(SyntaxColorSet.colorKeywords),
                    m.start(),
                    m.end(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        for (Matcher m = SyntaxPattern.STRUCTURES.matcher(e); m.find(); ) {
            e.setSpan(
                    new ForegroundColorSpan(SyntaxColorSet.colorStructures),
                    m.start(),
                    m.end(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        for (Matcher m = SyntaxPattern.VARIABLES.matcher(e); m.find(); ) {
            e.setSpan(
                    new ForegroundColorSpan(SyntaxColorSet.colorVariables),
                    m.start(),
                    m.end(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    private String appendWhiteSpaces(int lineNumber, int lineCount) {
        StringBuilder lineText = new StringBuilder();
        int fakeLN = lineNumber + 1;
        int wsCount = String.valueOf(lineCount).length() - String.valueOf(fakeLN).length();
        for (int i = 0; i < wsCount; i++) {
            lineText.append(" ");
        }
        lineText.append(fakeLN);
        return lineText.toString();

    }

    private void adjustPadding(String maxLineText) {
        int textWidth = (int) paint.measureText(maxLineText);
        lineNumberPadding = basePadding + textWidth;
        setPadding(lineNumberPadding, 0, 0, 0);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO: Implement this method
        super.onDraw(canvas);
        int lineHeight = getBaseline();
        String maxLineText = appendWhiteSpaces(getLineCount() - 1, getLineCount());
        int width = lineNumberPadding - basePadding + 1;
        canvas.drawLine(width, 0, width, getHeight(), deviderLine);
        if (lastLineCount != getLineCount()) {
            adjustPadding(maxLineText);
            lastLineCount = getLineCount();
        }
        for (int i = 0; i < getLineCount(); i++) {
            int lineLevel = String.valueOf(getLineCount() - 1).length();
            if (lineLevel > lastLineLevel) {
                lastLineLevel = lineLevel;
                adjustPadding(maxLineText);
            }
            canvas.drawText(appendWhiteSpaces(i, getLineCount()), rect.left, lineHeight, paint);
            lineHeight += getLineHeight();
        }
    }

}
