package crixec.commom.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
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
    private boolean modified = true;
    private boolean realModified = false;
    private Handler updater = new Handler();


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
            SyntaxColorSet.colorKeyword = ResourcesCompat.getColor(getResources(), R.color.colorCodeKeyword, null);
            SyntaxColorSet.colorString = ResourcesCompat.getColor(getResources(), R.color.colorCodeString, null);
            SyntaxColorSet.colorVariable = ResourcesCompat.getColor(getResources(), R.color.colorCodeVariable, null);
            SyntaxColorSet.colorComment = ResourcesCompat.getColor(getResources(), R.color.colorCodeComment, null);
            SyntaxColorSet.colorNumber = ResourcesCompat.getColor(getResources(), R.color.colorCodeNumber, null);
            addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    updater.removeCallbacks(HighlightUpdater);
                    if (modified || !realModified) {
                        updater.postDelayed(HighlightUpdater, 1000);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        }
    }

    public StringBuilder content() {
        Editable editable = new SpannableStringBuilder(getText());
        editable.clearSpans();
        return new StringBuilder(editable.toString());
    }

    private static class SyntaxPattern {
        static Pattern COMMENT = Pattern.compile("#[^\\n]*");
        static Pattern KEYWORD = Pattern.compile("\\b(if|fi|then|else|for|in|do|while|done|echo|su|cat|grep|sed|awk|cut|md5sum|readlink|cd|mv|cp|rm|mkdir|find|read|busybox|stop|start)\\b");
        static Pattern STRING = Pattern.compile("\".*?\"", Pattern.MULTILINE);
        static Pattern VARIABLE = Pattern.compile("\\$[A-Za-z0-9_\\.]*");
        static Pattern NUMBER = Pattern.compile("\\b[0-9]*\\b");
    }

    private static class SyntaxColorSet {
        static int colorKeyword;
        static int colorString;
        static int colorVariable;
        static int colorComment;
        static int colorNumber;
    }

    private void clearSpans(Editable e) {
        // remove foreground color spans
        {
            ForegroundColorSpan spans[] = e.getSpans(
                    0,
                    e.length(),
                    ForegroundColorSpan.class);

            for (int i = spans.length; i-- > 0; ) {
                e.removeSpan(spans[i]);
            }
        }

        // remove background color spans
        {
            BackgroundColorSpan spans[] = e.getSpans(
                    0,
                    e.length(),
                    BackgroundColorSpan.class);

            for (int i = spans.length; i-- > 0; ) {
                e.removeSpan(spans[i]);
            }
        }
    }

    private Runnable HighlightUpdater = new Runnable() {
        @Override
        public void run() {
            modified = false;
            realModified = true;
            Editable spans = getText();
            clearSpans(spans);
            for (Matcher m = SyntaxPattern.KEYWORD.matcher(spans); m.find(); ) {
                spans.setSpan(
                        new ForegroundColorSpan(SyntaxColorSet.colorKeyword),
                        m.start(),
                        m.end(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            for (Matcher m = SyntaxPattern.STRING.matcher(spans); m.find(); ) {
                spans.setSpan(
                        new ForegroundColorSpan(SyntaxColorSet.colorString),
                        m.start(),
                        m.end(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            for (Matcher m = SyntaxPattern.VARIABLE.matcher(spans); m.find(); ) {
                spans.setSpan(
                        new ForegroundColorSpan(SyntaxColorSet.colorVariable),
                        m.start(),
                        m.end(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            for (Matcher m = SyntaxPattern.COMMENT.matcher(spans); m.find(); ) {
                spans.setSpan(
                        new ForegroundColorSpan(SyntaxColorSet.colorComment),
                        m.start(),
                        m.end(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            for (Matcher m = SyntaxPattern.NUMBER.matcher(spans); m.find(); ) {
                spans.setSpan(
                        new ForegroundColorSpan(SyntaxColorSet.colorNumber),
                        m.start(),
                        m.end(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            modified = true;
        }
    };

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
