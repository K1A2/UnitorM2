package unitor.uni.k1a2.unitor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.view.WindowManager;

/**
 * Created by K1A2 on 2017-07-31.
 */

public class SettingActivity extends PreferenceActivity implements Preference.OnPreferenceClickListener {

    private ListPreference theme;
    private SwitchPreference screen;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        final SharedPreferences themes = PreferenceManager.getDefaultSharedPreferences(this);
        final String th = themes.getString("setting_theme", "0");
        if (th.equals("0")) {
            setTheme(R.style.AppTheme);
        } else {
            setTheme(R.style.AppThemeDark);
            this.getListView().setBackgroundColor(getResources().getColor(R.color.pref_background));
        }
        addPreferencesFromResource(R.xml.setting);

        theme = (ListPreference)findPreference("setting_theme");
        screen = (SwitchPreference)findPreference("setting_screen");
        theme.setOnPreferenceClickListener(this);
        screen.setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (preference.getKey().equals("setting_theme")) {

        } else if (preference.getKey().equals("setting_screen")){

        }
        return false;
    }

    public void onBackPressed() {
        startActivity(new Intent(SettingActivity.this, MainActivity.class));
        finish();
    }
}
