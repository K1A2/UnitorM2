package unitor.uni.k1a2.unitor;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by K1A2 on 2017-05-03.
 */

public class PlayButton extends LinearLayout {

    LinearLayout main;
    TextView txt;

    public PlayButton(Context context) {
        super(context);
        iniView(context);
    }

    public PlayButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        iniView(context);
        getAttrs(attrs);
    }

    public PlayButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        iniView(context);
        getAttrs(attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PlayButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        iniView(context);
        getAttrs(attrs, defStyleAttr, defStyleRes);
    }

    private void iniView(Context context) {
        String infServide = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li = (LayoutInflater)getContext().getSystemService(infServide);
        View v = li.inflate(R.layout.view_playbtn, PlayButton.this, false);
        v.setBackgroundResource(R.drawable.playbtn_background);

        txt = (TextView)v.findViewById(R.id.View_Play_text);

        final SharedPreferences theme = PreferenceManager.getDefaultSharedPreferences(context);
        final String th = theme.getString("setting_theme", "0");
        if (th.equals("0")) {
            context.setTheme(R.style.AppTheme);
            txt.setBackgroundResource(R.drawable.playbtn);
        } else {
            context.setTheme(R.style.AppThemeDark);
            txt.setBackgroundResource(R.drawable.playbtn_dark);
        }

        this.setClickable(true);
        txt.setClickable(true);

        addView(v);
    }

    private void getAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.PlayButton);
        setTypeArray(typedArray);
    }


    private void getAttrs(AttributeSet attrs, int defStyle) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.PlayButton, defStyle, 0);
        setTypeArray(typedArray);
    }

    private void getAttrs(AttributeSet attrs, int defStyle, int defStyleAttr) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.PlayButton, defStyle, defStyleAttr);
        setTypeArray(typedArray);
    }

    private void setTypeArray(TypedArray typeArray) {
        String text_string = typeArray.getString(R.styleable.PlayButton_name);
        txt.setText(text_string);
        typeArray.recycle();
    }
}
