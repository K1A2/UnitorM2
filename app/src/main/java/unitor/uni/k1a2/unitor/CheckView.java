package unitor.uni.k1a2.unitor;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.LinearLayout;

/**
 * Created by K1A2 on 2017-05-05.
 */

public class CheckView extends LinearLayout implements Checkable {

    public CheckView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean isChecked() {
        CheckBox cb = (CheckBox) findViewById(R.id.FragDial_list_Check);

        return cb.isChecked();
    }

    @Override
    public void toggle() {
        CheckBox cb = (CheckBox) findViewById(R.id.FragDial_list_Check);

        setChecked(cb.isChecked() ? false : true);
    }

    @Override
    public void setChecked(boolean checked) {
        CheckBox cb = (CheckBox) findViewById(R.id.FragDial_list_Check);

        if (cb.isChecked() != checked) {
            cb.setChecked(checked);
        }
    }
}
