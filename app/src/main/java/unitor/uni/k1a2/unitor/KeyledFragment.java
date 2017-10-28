package unitor.uni.k1a2.unitor;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Pattern;

/**
 * Created by K1A2 on 2017-08-01.
 */

public class KeyledFragment extends Fragment {
    //뷰
    private LinearLayout Linear_play, linear_main, linear_edit;
    private PlayButton[][] playButtons;
    private RelativeLayout left;
    private RadioButton chain_select;
    private Spinner spinner_LED;
    private CheckBox check_delete, check_off;
    private ImageButton btn_LEDplay, btn_switch;
    private RadioGroup chain_group;
    private ListView list_LED, list_content;
    private TextView text_current, text_content;
    private Button btn_editan, btn_contentAdd, btn_newLED, btn_save;

    //그밖에것
    private Context con;
    private int[] velocity;
    private int chain_int, clicked_position;
    private String th, chain_radio, checked_velo, in, clicked;
    private File work_File;
    private File[] work_lists;
    private Filerw filerw;
    private ArrayList<String> content;
    private ArrayList<String[]> array_filename;
    private String main_path, chain, path_workLED, path_editLED, what, now_XY;
    private SharedPreferences pref_info;
    private SpinnerColordapter spinnerColordapter;
    private SoundListItem soundListItem, contentListItem, contentListItem_LONG;
    private SoundListViewAdapter soundListViewAdapter, contentListAdapter;
    private boolean isEdit = false, isPlaying = false;
    private playLED playled;
    private  final char[] back = new char[] {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'n', 'm', 'o', 'p', 'q', 'r', 's',
            't', 'u', 'v', 'w', 'x', 'y', 'z'};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_keyled, container, false);
        con = root.getContext();

        //뷰 초기화
        Linear_play = (LinearLayout)root.findViewById(R.id.Layout_Btns);
        chain_group = (RadioGroup)root.findViewById(R.id.RadioG_chain);
        left = (RelativeLayout)root.findViewById(R.id.KeySound_Right);
        btn_LEDplay = (ImageButton)root.findViewById(R.id.Btn_PLAY);
        btn_switch = (ImageButton)root.findViewById(R.id.LED_switch);
        spinner_LED = (Spinner)root.findViewById(R.id.Spinner_color);
        check_delete = (CheckBox)root.findViewById(R.id.Check_deleteLED);
        check_off = (CheckBox)root.findViewById(R.id.Check_LEDOFF);
        list_LED = (ListView)root.findViewById(R.id.List_LED);
        text_current = (TextView)root.findViewById(R.id.Text_current_LED);
        btn_editan = (Button)root.findViewById(R.id.Button_Editan);
        linear_main = (LinearLayout)root.findViewById(R.id.Layout_LED_MAIN);
        linear_edit = (LinearLayout)root.findViewById(R.id.Layout_editpannal);
        list_content = (ListView)root.findViewById(R.id.List_SOUND_CONTENT);
        text_content = (TextView)root.findViewById(R.id.content_frame);
        btn_contentAdd = (Button)root.findViewById(R.id.Btn_ADD);
        btn_newLED = (Button)root.findViewById(R.id.Btn_NEWLED);
        btn_save = (Button)root.findViewById(R.id.keyLED_save_btn);


        text_content.setMovementMethod(ScrollingMovementMethod.getInstance());

        //그밖에것
        filerw = new Filerw(con);
        content = new ArrayList<String>();
        pref_info = con.getSharedPreferences("Packinfo", Context.MODE_PRIVATE);
        array_filename = new ArrayList<String[]>();
        soundListViewAdapter = new SoundListViewAdapter();
        contentListAdapter = new SoundListViewAdapter();
        velocity = new int[] {getResources().getColor(R.color.v_0), getResources().getColor(R.color.v_1), getResources().getColor(R.color.v_2), getResources().getColor(R.color.v_3),
                getResources().getColor(R.color.v_4), getResources().getColor(R.color.v_5), getResources().getColor(R.color.v_6), getResources().getColor(R.color.v_7), getResources().getColor(R.color.v_8),
                getResources().getColor(R.color.v_9), getResources().getColor(R.color.v_10), getResources().getColor(R.color.v_11), getResources().getColor(R.color.v_12), getResources().getColor(R.color.v_13),
                getResources().getColor(R.color.v_14), getResources().getColor(R.color.v_15), getResources().getColor(R.color.v_16), getResources().getColor(R.color.v_17), getResources().getColor(R.color.v_18),
                getResources().getColor(R.color.v_19), getResources().getColor(R.color.v_20), getResources().getColor(R.color.v_21), getResources().getColor(R.color.v_22), getResources().getColor(R.color.v_23),
                getResources().getColor(R.color.v_24), getResources().getColor(R.color.v_25), getResources().getColor(R.color.v_26), getResources().getColor(R.color.v_27), getResources().getColor(R.color.v_28),
                getResources().getColor(R.color.v_29), getResources().getColor(R.color.v_30), getResources().getColor(R.color.v_31), getResources().getColor(R.color.v_32), getResources().getColor(R.color.v_33),
                getResources().getColor(R.color.v_34), getResources().getColor(R.color.v_35), getResources().getColor(R.color.v_36), getResources().getColor(R.color.v_37), getResources().getColor(R.color.v_38),
                getResources().getColor(R.color.v_39), getResources().getColor(R.color.v_40), getResources().getColor(R.color.v_41), getResources().getColor(R.color.v_42), getResources().getColor(R.color.v_43),
                getResources().getColor(R.color.v_44), getResources().getColor(R.color.v_45), getResources().getColor(R.color.v_46), getResources().getColor(R.color.v_47), getResources().getColor(R.color.v_48),
                getResources().getColor(R.color.v_49), getResources().getColor(R.color.v_50), getResources().getColor(R.color.v_51), getResources().getColor(R.color.v_52), getResources().getColor(R.color.v_53),
                getResources().getColor(R.color.v_54), getResources().getColor(R.color.v_55), getResources().getColor(R.color.v_56), getResources().getColor(R.color.v_57), getResources().getColor(R.color.v_58),
                getResources().getColor(R.color.v_59), getResources().getColor(R.color.v_60), getResources().getColor(R.color.v_61), getResources().getColor(R.color.v_62), getResources().getColor(R.color.v_63),
                getResources().getColor(R.color.v_64), getResources().getColor(R.color.v_65), getResources().getColor(R.color.v_66), getResources().getColor(R.color.v_67), getResources().getColor(R.color.v_68),
                getResources().getColor(R.color.v_69), getResources().getColor(R.color.v_70), getResources().getColor(R.color.v_71), getResources().getColor(R.color.v_72), getResources().getColor(R.color.v_73),
                getResources().getColor(R.color.v_74), getResources().getColor(R.color.v_75), getResources().getColor(R.color.v_76), getResources().getColor(R.color.v_77), getResources().getColor(R.color.v_78),
                getResources().getColor(R.color.v_79), getResources().getColor(R.color.v_80), getResources().getColor(R.color.v_81), getResources().getColor(R.color.v_82), getResources().getColor(R.color.v_83),
                getResources().getColor(R.color.v_84), getResources().getColor(R.color.v_85), getResources().getColor(R.color.v_86), getResources().getColor(R.color.v_87), getResources().getColor(R.color.v_88),
                getResources().getColor(R.color.v_89), getResources().getColor(R.color.v_90), getResources().getColor(R.color.v_91), getResources().getColor(R.color.v_92), getResources().getColor(R.color.v_93),
                getResources().getColor(R.color.v_94), getResources().getColor(R.color.v_95), getResources().getColor(R.color.v_96), getResources().getColor(R.color.v_97), getResources().getColor(R.color.v_98),
                getResources().getColor(R.color.v_99), getResources().getColor(R.color.v_100), getResources().getColor(R.color.v_101), getResources().getColor(R.color.v_102), getResources().getColor(R.color.v_103),
                getResources().getColor(R.color.v_104), getResources().getColor(R.color.v_105), getResources().getColor(R.color.v_106), getResources().getColor(R.color.v_107), getResources().getColor(R.color.v_108),
                getResources().getColor(R.color.v_109), getResources().getColor(R.color.v_110), getResources().getColor(R.color.v_111), getResources().getColor(R.color.v_112), getResources().getColor(R.color.v_113),
                getResources().getColor(R.color.v_114), getResources().getColor(R.color.v_115), getResources().getColor(R.color.v_116), getResources().getColor(R.color.v_117), getResources().getColor(R.color.v_118),
                getResources().getColor(R.color.v_119), getResources().getColor(R.color.v_120), getResources().getColor(R.color.v_121), getResources().getColor(R.color.v_122), getResources().getColor(R.color.v_123),
                getResources().getColor(R.color.v_124), getResources().getColor(R.color.v_125), getResources().getColor(R.color.v_126), getResources().getColor(R.color.v_127)};

        //프리퍼런스
        main_path = pref_info.getString("PATH", "NULL");
        chain = pref_info.getString("CHAIN", "null");
        final SharedPreferences theme = PreferenceManager.getDefaultSharedPreferences(con);
        th = theme.getString("setting_theme", "0");
        if (th.equals("0")) {
            btn_switch.setImageResource(R.drawable.ic_remove_red_eye_black_18dp);
            btn_LEDplay.setImageResource(R.drawable.ic_play_circle_filled_black_36dp);
            btn_editan.setTextColor(getResources().getColor(R.color.colorAccent));
        } else {
            btn_switch.setImageResource(R.drawable.ic_remove_red_eye_white_18dp);
            btn_LEDplay.setImageResource(R.drawable.ic_play_circle_filled_white_36dp);
            btn_editan.setTextColor(getResources().getColor(R.color.colorAccent_dark));
        }

        //파일 리스트 읽어옴
        path_workLED = Environment.getExternalStorageDirectory().getAbsolutePath() + "/unipackProject/workLED/";
        work_File = new File(path_workLED);
        if (work_File.exists()) {
            getLEDlist(work_File);
        } else {
            work_File.mkdirs();
        }

        spinner_LED.setEnabled(false);
        check_off.setEnabled(false);
        check_delete.setEnabled(false);
        btn_editan.setEnabled(false);

        //버튼생성
        playButtons = new PlayButton[8][8];
        for (int i = 0;i < 8;i++) {
            for (int a = 0;a < 8;a++) {
                playButtons[i][a] = (PlayButton)root.findViewWithTag(String.valueOf(i + 1) + " " + String.valueOf(a + 1));//버튼초기화
                playButtons[i][a].txt.setTag(String.valueOf(i + 1) + " " + String.valueOf(a + 1));
                playButtons[i][a].txt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RadioButton radioButton = (RadioButton)root.findViewById(chain_group.getCheckedRadioButtonId());
                        clicked = radioButton.getText().toString() + " " + v.getTag();
                        if (!isEdit) {
                            soundListViewAdapter.clear();
                            for (String a[]:array_filename) {
                                if (a[0].startsWith(clicked)) {
                                    soundListViewAdapter.addItem(a[0], a[1]);
                                }
                            }
                            list_LED.setAdapter(soundListViewAdapter);
                            text_current.setText(String.format(getString(R.string.Text_current), clicked));
                            now_XY = clicked;
                        } else if (isEdit&&what != null){
                            if (what.equals("Frame")) {
                                SoundListItem current = (SoundListItem)contentListAdapter.getItem(clicked_position);//TODO: ArrayOutOfBoundsE
                                String[] a = current.getPath().split("\n");
                                StringBuilder stringBuilder = new StringBuilder();

                                if (check_off.isChecked()) {
                                    for (String s:a) {
                                        if (s.startsWith("f " + v.getTag())) {
                                            continue;
                                        } else {
                                            stringBuilder.append(s + "\n");
                                        }
                                    }
                                    String content = "f " + v.getTag();
                                    stringBuilder.append(content + "\n");
                                    contentListAdapter.set(clicked_position, stringBuilder.toString());
                                    text_content.setText(stringBuilder.toString());

                                    StringBuilder stringBuilder1 = new StringBuilder();
                                    for (int i = 0;i < contentListAdapter.getCount();i++) {
                                        contentListItem = (SoundListItem)contentListAdapter.getItem(i);
                                        stringBuilder1.append(contentListItem.getPath());
                                    }
                                    filerw.saveLEDwork(new File(path_editLED), stringBuilder1.toString());
                                    for (int si = 0;si < 8;si++) {
                                        for (int as = 0;as < 8;as++) {
                                            GradientDrawable shapeDrawable = (GradientDrawable) playButtons[si][as].getBackground().getCurrent();
                                            shapeDrawable.setColor(Color.argb(0, 0, 0,0));
                                        }
                                    }
                                    showLED(stringBuilder.toString());


                                } else if (check_delete.isChecked()) {
                                    for (String s:a) {
                                        if (s.startsWith("o " + v.getTag())||s.startsWith("f " + v.getTag())) {
                                            continue;
                                        } else {
                                            stringBuilder.append(s + "\n");
                                        }
                                    }
                                    contentListAdapter.set(clicked_position, stringBuilder.toString());
                                    text_content.setText(stringBuilder.toString());

                                    StringBuilder stringBuilder1 = new StringBuilder();
                                    for (int i = 0;i < contentListAdapter.getCount();i++) {
                                        contentListItem = (SoundListItem)contentListAdapter.getItem(i);
                                        stringBuilder1.append(contentListItem.getPath());
                                    }
                                    filerw.saveLEDwork(new File(path_editLED), stringBuilder1.toString());
                                    for (int si = 0;si < 8;si++) {
                                        for (int as = 0;as < 8;as++) {
                                            GradientDrawable shapeDrawable = (GradientDrawable) playButtons[si][as].getBackground().getCurrent();
                                            shapeDrawable.setColor(Color.argb(0, 0, 0,0));
                                        }
                                    }
                                    showLED(stringBuilder.toString());

                                } else if (checked_velo != null){
                                    for (String s:a) {
                                        if (s.startsWith("o " + v.getTag())) {
                                            continue;
                                        } else {
                                            stringBuilder.append(s + "\n");
                                        }
                                    }
                                    String content = "o " + v.getTag() + " a " + checked_velo;
                                    stringBuilder.append(content + "\n");
                                    contentListAdapter.set(clicked_position, stringBuilder.toString());
                                    text_content.setText(stringBuilder.toString());

                                    StringBuilder stringBuilder1 = new StringBuilder();
                                    for (int i = 0;i < contentListAdapter.getCount();i++) {
                                        contentListItem = (SoundListItem)contentListAdapter.getItem(i);
                                        stringBuilder1.append(contentListItem.getPath());
                                    }
                                    filerw.saveLEDwork(new File(path_editLED), stringBuilder1.toString());
                                    for (int si = 0;si < 8;si++) {
                                        for (int as = 0;as < 8;as++) {
                                            GradientDrawable shapeDrawable = (GradientDrawable) playButtons[si][as].getBackground().getCurrent();
                                            shapeDrawable.setColor(Color.argb(0, 0, 0,0));
                                        }
                                    }
                                    showLED(stringBuilder.toString());
                                }
                            }
                        }
                    }
                });
            }
        }

        //체인 리스너
        chain_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                String c = String.valueOf(i + 1);
                if (array_filename != null) {
                    changeBtnColor(c);
                }
            }
        });

        //체인생성
        if (chain.equals("null")||chain.equals("")) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            left.setLayoutParams(params);
            TextView nu = new TextView(getActivity());
            nu.setText(getString(R.string.Text_nochain));
            left.addView(nu);
        } else {
            chain_int = Integer.parseInt(chain);
            for (int i = 0;i < chain_int;i++) {
                chain_select = new RadioButton(getActivity());
                chain_select.setId(i);
                chain_select.setText(String.valueOf(i + 1));
                chain_group.addView(chain_select);
                if (i == 0) {
                    chain_select.setChecked(true);
                }
            }
        }

        //스피너
        ArrayList<String> LED_num = new ArrayList<String>();
        final ArrayList<Integer> color = new ArrayList<Integer>();
        Integer[] v = new Integer[velocity.length];
        for (int t = 0;t < velocity.length;t++) {
            v[t] = velocity[t];
        }
        Collections.addAll(color, v);
        for (int i = 0;i < 128;i++) {
            String s = String.valueOf(i);
            LED_num.add(s);
        }
        spinnerColordapter = new SpinnerColordapter(getActivity(), LED_num, color);
        spinner_LED.setAdapter(spinnerColordapter);

        //LED파일 선택 리스너
        list_LED.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                soundListItem = (SoundListItem)soundListViewAdapter.getItem(i);
                path_editLED = soundListItem.getPath();
                final String name = soundListItem.getName();
                RadioButton radioButton = (RadioButton)root.findViewById(chain_group.getCheckedRadioButtonId());
                chain_radio = radioButton.getText().toString();
                clicked_position = 0;

                final PopupMenu popupMenu = new PopupMenu(con, view);
                popupMenu.getMenuInflater().inflate(R.menu.led_menu, popupMenu.getMenu());
                if (soundListViewAdapter.getCount() == i + 1&&i == 0) {
                    popupMenu.getMenu().getItem(4).setVisible(false);
                    popupMenu.getMenu().getItem(3).setVisible(false);
                } else if (soundListViewAdapter.getCount() == i + 1) {
                    popupMenu.getMenu().getItem(4).setVisible(false);
                } else if (i == 0) {
                    popupMenu.getMenu().getItem(3).setVisible(false);
                }
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.Menu_Edit_LED:
                                content = filerw.getLEDwork(new File(path_editLED));
                                if (content != null) {
                                    editLedContent(content);
                                }

                                btn_editan.setEnabled(true);
                                linear_edit.setVisibility(View.VISIBLE);
                                linear_main.setVisibility(View.INVISIBLE);
                                isEdit = true;
                                break;

                            case R.id.Menu_UPLED:
                                if (work_lists != null) {
                                    int i = 0;
                                    for (File f:work_lists) {
                                        if (f.getName().equals(name)) {
                                            break;
                                        }
                                        i++;
                                    }
                                    new changeLED().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, work_lists[i], work_lists[i - 1]);
                                }
                                break;

                            case R.id.Menu_DOLED:
                                if (work_lists != null) {
                                    int i = 0;
                                    for (File f:work_lists) {
                                        if (f.getName().equals(name)) {
                                            break;
                                        }
                                        i++;
                                    }
                                    new changeLED().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, work_lists[i], work_lists[i + 1]);
                                }
                                break;

                            case R.id.Menu_delete_LED:
                                File delete = new File(path_editLED);
                                delete.delete();

                                final File[] clickeds = work_File.listFiles(new FileFilter() {
                                    @Override
                                    public boolean accept(File file) {
                                        return file.isFile()&&file.getName().startsWith(now_XY);
                                    }
                                });
                                changeLatter(clickeds);
                                getLEDlist(work_File);
                                getLEDs();
                                break;

                            case R.id.Menu_play_LED:
                                content = filerw.getLEDwork(new File(path_editLED));
                                isPlaying = true;
                                break;
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });

        //off체크
        check_off.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    spinner_LED.setEnabled(false);
                    check_delete.setEnabled(false);
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int si = 0;si < 8;si++) {
                        for (int a = 0;a < 8;a++) {
                            GradientDrawable shapeDrawable = (GradientDrawable) playButtons[si][a].getBackground().getCurrent();
                            shapeDrawable.setColor(Color.argb(0, 0, 0,0));
                        }
                    }
                    for (int i = 0;i <= clicked_position - 1;i++) {
                        contentListItem = (SoundListItem)contentListAdapter.getItem(i);
                        stringBuilder.append(contentListItem.getPath() + "\n");
                    }
                    showLED(stringBuilder.toString());
                } else {
                    spinner_LED.setEnabled(true);
                    check_delete.setEnabled(true);
                    for (int si = 0;si < 8;si++) {
                        for (int a = 0;a < 8;a++) {
                                GradientDrawable shapeDrawable = (GradientDrawable) playButtons[si][a].getBackground().getCurrent();
                                shapeDrawable.setColor(Color.argb(0, 0, 0,0));
                        }
                    }
                    contentListItem = (SoundListItem)contentListAdapter.getItem(clicked_position);
                    showLED(contentListItem.getPath());
                }
            }
        });

        //delete 체크
        check_delete.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    spinner_LED.setEnabled(false);
                    check_off.setEnabled(false);
                } else {
                    spinner_LED.setEnabled(true);
                    check_off.setEnabled(true);
                }
            }
        });

        //내부 편집
        list_content.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                contentListItem = (SoundListItem)contentListAdapter.getItem(i);
                clicked_position = i;
                checked_velo = null;

                what = contentListItem.getName();
                chain_group.setVisibility(View.INVISIBLE);
                for (int si = 0;si < 8;si++) {
                    for (int a = 0;a < 8;a++) {
                        GradientDrawable shapeDrawable = (GradientDrawable) playButtons[si][a].getBackground().getCurrent();
                        shapeDrawable.setColor(Color.argb(0, 0, 0,0));
                    }
                }
                if (what.equals("Frame")) {
                    in = contentListItem.getPath();
                    spinner_LED.setEnabled(true);
                    check_delete.setEnabled(true);
                    check_off.setEnabled(true);
                    text_content.setText(in);
                    showLED(in);
                } else if (what.equals("Delay")) {
                    spinner_LED.setEnabled(false);
                    check_delete.setEnabled(false);
                    check_off.setEnabled(false);

                    final LinearLayout layout = (LinearLayout)View.inflate(con, R.layout.dialog_delay, null);
                    final EditText ed = (EditText)layout.findViewById(R.id.Edit_delay);
                    ed.setText(contentListItem.getPath().replaceFirst("d ", ""));
                    AlertDialog.Builder builder = new AlertDialog.Builder(con);
                    builder.setView(layout);
                    builder.setTitle(getString(R.string.Dialog_delay_set));
                    builder.setPositiveButton(getString(R.string.Dialog_btn_change), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String input = ed.getText().toString();
                            if (Pattern.matches("(^[0-9]*$)", input)) {
                                contentListAdapter.setDelay(clicked_position, "d " + input + "\n");
                                StringBuilder stringBuilder1 = new StringBuilder();
                                for (int is = 0;is < contentListAdapter.getCount();is++) {
                                    contentListItem = (SoundListItem)contentListAdapter.getItem(is);
                                    stringBuilder1.append(contentListItem.getPath());
                                }
                                filerw.saveLEDwork(new File(path_editLED), stringBuilder1.toString());
                            } else {
                                Toast.makeText(con, getString(R.string.Toast_only), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    builder.show();
                }
                check_delete.setChecked(false);
                check_off.setChecked(false);
            }
        });

        //내용물 삭제
        list_content.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long l) {
                //TODO
                contentListItem_LONG = (SoundListItem)contentListAdapter.getItem(position);
                String now = contentListItem_LONG.getName();

                if (now.equals("Frame")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(con);
                    builder.setTitle(getString(R.string.Dialog_delete));
                    builder.setMessage(contentListItem_LONG.getPath());
                    builder.setPositiveButton(getString(R.string.Dialog_btn_delete), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (contentListAdapter.getCount() - 1 == position + 1) {
                                contentListAdapter.remove(position + 1);
                            }
                            contentListAdapter.remove(position); StringBuilder stringBuilder1 = new StringBuilder();
                            for (int is = 0;is < contentListAdapter.getCount();is++) {
                                contentListItem_LONG = (SoundListItem)contentListAdapter.getItem(is);
                                stringBuilder1.append(contentListItem_LONG.getPath());
                            }
                            filerw.saveLEDwork(new File(path_editLED), stringBuilder1.toString());
                        }
                    });
                    builder.show();
                }
                return true;
            }
        });

        //내용추가
        btn_contentAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int a = contentListAdapter.getCount();
                if (a - 1 < 0) {
                    contentListAdapter.addItem("Frame", "");
                    list_content.setAdapter(contentListAdapter);
                    StringBuilder stringBuilder1 = new StringBuilder();
                    for (int is = 0;is < contentListAdapter.getCount();is++) {
                        contentListItem = (SoundListItem)contentListAdapter.getItem(is);
                        stringBuilder1.append(contentListItem.getPath());
                    }
                    filerw.saveLEDwork(new File(path_editLED), stringBuilder1.toString());
                } else {
                    contentListItem = (SoundListItem)contentListAdapter.getItem(a - 1);
                    String now = contentListItem.getName();
                    if (now.equals("Frame")) {
                        final LinearLayout layout = (LinearLayout)View.inflate(con, R.layout.dialog_delay, null);
                        final EditText ed = (EditText)layout.findViewById(R.id.Edit_delay);
                        AlertDialog.Builder alert = new AlertDialog.Builder(con);
                        alert.setView(layout);
                        alert.setTitle("Delay");
                        alert.setPositiveButton(getString(R.string.DialFrag_Sound_ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String input = ed.getText().toString();
                                if (Pattern.matches("(^[0-9]*$)", input)) {
                                    contentListAdapter.addItem("Delay", "d " + input);
                                    list_content.setAdapter(contentListAdapter);
                                    StringBuilder stringBuilder1 = new StringBuilder();
                                    for (int is = 0;is < contentListAdapter.getCount();is++) {
                                        contentListItem = (SoundListItem)contentListAdapter.getItem(is);
                                        stringBuilder1.append(contentListItem.getPath());
                                    }
                                    filerw.saveLEDwork(new File(path_editLED), stringBuilder1.toString());
                                } else {
                                    Toast.makeText(con, getString(R.string.Toast_only), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        alert.show();
                    } else {
                        contentListAdapter.addItem("Frame", "");
                        list_content.setAdapter(contentListAdapter);
                        StringBuilder stringBuilder1 = new StringBuilder();
                        for (int is = 0;is < contentListAdapter.getCount();is++) {
                            contentListItem = (SoundListItem)contentListAdapter.getItem(is);
                            stringBuilder1.append(contentListItem.getPath());
                        }
                        filerw.saveLEDwork(new File(path_editLED), stringBuilder1.toString());
                    }
                }
            }
        });

        //다른거편집
        btn_editan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_editan.setEnabled(false);
                spinner_LED.setEnabled(false);
                check_delete.setEnabled(false);
                check_off.setEnabled(false);
                btn_editan.setEnabled(false);
                chain_group.setVisibility(View.VISIBLE);
                linear_edit.setVisibility(View.INVISIBLE);
                linear_main.setVisibility(View.VISIBLE);
                text_content.setText("");
                changeBtnColor(chain_radio);
                isEdit = false;
            }
        });

        //스피너 리스너
        spinner_LED.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Object[] a = spinnerColordapter.getItem(i);
                checked_velo = a[0].toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //new LED
        btn_newLED.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (now_XY != null) {
                    File[] work_lists = work_File.listFiles(new FileFilter() {
                        @Override
                        public boolean accept(File pathname) {
                            return pathname.isFile()&&pathname.getName().startsWith(now_XY);
                        }
                    });

                    if (back.length > work_lists.length) {
                        changeLatter(work_lists);
                        File ne = new File(path_workLED + now_XY + " 1 " + back[work_lists.length]);
                        try {
                            ne.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        getLEDlist(work_File);
                        getLEDs();
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.Toast_mk_LED_Fail_count), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        //plaLED
        btn_LEDplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                content = filerw.getLEDwork(new File(path_editLED));
                isPlaying = true;
            }
        });

        //save LED
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!main_path.equals("NULL")) {
                    AlertDialog.Builder ask_save = new AlertDialog.Builder(con);
                    ask_save.setTitle(getString(R.string.Dialog_Asksave));
                    ask_save.setPositiveButton(getString(R.string.Dialog_save), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            new saveLED().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, main_path + "/keyLED/", path_workLED);
                        }
                    });
                    ask_save.setNegativeButton(getString(R.string.Dialog_cancel), null);
                    ask_save.show();
                }
            }
        });

        return root;
    }

    //LED to arraylist
    private void getLEDlist(File f) {
        work_lists = f.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isFile();
            }
        });
        array_filename.clear();
        for (File s : work_lists) {
            array_filename.add(new String[] {s.getName(), s.getAbsolutePath()});
        }
    }

    //LED가져옴
    private void getLEDs() {
        soundListViewAdapter.clear();

        for (String[] s:array_filename) {
            if (s[0].startsWith(now_XY)) {
                soundListViewAdapter.addItem(s[0], s[1]);
            }
        }
        list_LED.setAdapter(soundListViewAdapter);
    }

    //버튼 색깔변경
    private void changeBtnColor(String chain) {

        for (int si = 0;si < 8;si++) {
            for (int a = 0;a < 8;a++) {
                playButtons[si][a].setBackgroundResource(R.drawable.playbtn_used);
                if (th.equals("0")) {
                    playButtons[si][a].setBackgroundResource(R.drawable.playbtn);
                } else {
                    playButtons[si][a].setBackgroundResource(R.drawable.playbtn_dark);
                }
            }
        }

        for (String[] s:array_filename) {
            if (s[0].startsWith(chain + " ")) {
                String in[] = s[0].replaceFirst(chain + " ", "").split("\\s+");
                try {
                    if (th.equals("0")) {
                        playButtons[Integer.parseInt(in[0]) - 1][Integer.parseInt(in[1]) - 1].setBackgroundResource(R.drawable.playbtn_used);
                    } else {
                        playButtons[Integer.parseInt(in[0]) - 1][Integer.parseInt(in[1]) - 1].setBackgroundResource(R.drawable.playbtn_dark_used);
                    }
                } catch (Exception e) {

                }
            } else {
                continue;
            }
        }
    }

    private void showLED(String inside) {
        String[] a = inside.split("\n");
        for (String s:a) {
            String[] sp = s.split("\\s+");
            try {
                if (sp[0].equals("o")||sp[0].equals("on")) {
                    try {
                        GradientDrawable shapeDrawable = (GradientDrawable) playButtons[Integer.parseInt(sp[1]) - 1][Integer.parseInt(sp[2]) - 1].getBackground().getCurrent();
                        shapeDrawable.setColor(velocity[Integer.parseInt(sp[4])]);
                    } catch (NumberFormatException e) {

                    }
                } else if (sp[0].equals("f")||sp[0].equals("off")) {
                    try {
                        GradientDrawable shapeDrawable = (GradientDrawable) playButtons[Integer.parseInt(sp[1]) - 1][Integer.parseInt(sp[2]) - 1].getBackground().getCurrent();
                        shapeDrawable.setColor(Color.argb(0, 0, 0,0));
                    } catch (NumberFormatException e) {

                    }
                } else {
                    continue;
                }
            } catch (Exception e) {

            }
        }
    }

    //LED이름 변경
    private void changeLatter(File[] work_lists) {
        for (int now = 0;now < work_lists.length;now++) {
            File f = new File(work_lists[now].getAbsolutePath());
            String[] name = work_lists[now].getName().split("\\s+");
            try {
                String p = name[0] + " " + name[1] + " " + name[2] + " " + name[3] + " " + back[now];
                f.renameTo(new File(path_workLED + p));
            } catch (Exception e) {

            }
        }
    }

    //리스트 맞춤
    private void editLedContent(ArrayList<String> co) {
        contentListAdapter.clear();
        StringBuilder stringBuilder = new StringBuilder();
        int a = 0;
        for (String s:co) {
            if (s.equals("")) {
                continue;
            } else if (s.startsWith("d")||s.startsWith("delay")) {
                contentListAdapter.addItem("Frame", stringBuilder.toString());
                contentListAdapter.addItem("Delay", s);
                stringBuilder = new StringBuilder();
                a = 1;
            } else {
                stringBuilder.append(s + "\n");
                a = 0;
            }
        }
        if (a == 0) {
            contentListAdapter.addItem("Frame", stringBuilder.toString());
        }
        list_content.setAdapter(contentListAdapter);
    }

    //버튼 사리즈 맞춤
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ViewTreeObserver viewTreeObserver = Linear_play.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    Linear_play.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    Linear_play.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
                final int height = Linear_play.getHeight();
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(height, height);
                params.addRule(RelativeLayout.CENTER_HORIZONTAL);
                Linear_play.setLayoutParams(params);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        isEdit = false;
        playled = new playLED();
        playled.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (playled != null&&playled.getStatus() == AsyncTask.Status.RUNNING) {
            playled.cancel(true);
        }
    }




    //LED순서변경
    private class changeLED extends AsyncTask<File, String, Boolean> {

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(con);
            progressDialog.setTitle(getString(R.string.Progress_LED_change));
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(File... files) {
            ArrayList<String> first = filerw.getLEDwork(files[0]);
            ArrayList<String> second = filerw.getLEDwork(files[1]);

            filerw.saveLEDwork(files[0], ListToString(second));
            filerw.saveLEDwork(files[1], ListToString(first));
            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            progressDialog.dismiss();
        }

        private String ListToString (ArrayList<String> con) {
            StringBuilder stringBuilder = new StringBuilder();
            for (String s:con) {
                stringBuilder.append(s + "\n");
            }
            return stringBuilder.toString();
        }
    }



    //LED 재생
    private class playLED extends AsyncTask<ArrayList<String>, String, Boolean> {

        @Override
        protected Boolean doInBackground(ArrayList<String>[] arrayLists) {
            for (;;) {
                if (isPlaying&&content != null) {
                    publishProgress("reset");
                    for (String s:content) {
                        String[] split = s.split("\\s+");

                        if (split[0].equals("o")||split[0].equals("on")) {
                            if (split.length == 5) {
                                publishProgress("on", split[0], split[1], split[2], split[3], split[4]);
                            }
                        } else if (split[0].equals("f")||split[0].equals("off")) {
                            if (split.length == 3) {
                                publishProgress("off", split[0], split[1], split[2]);
                            }
                        } else if (split[0].equals("d")||split[0].equals("delay")) {
                            if (split.length == 2) {
                                synchronized (this) {
                                    try {
                                        this.wait(Integer.parseInt(split[1]));
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        } else {
                            continue;
                        }
                        synchronized (this) {
                            try {
                                this.wait(1);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    if (!isEdit) {
                        publishProgress("finish");
                    }
                }
                if (isCancelled()) {
                    return null;
                }
                isPlaying = false;
                synchronized (this) {
                    try {
                        this.wait(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            if (values[0].equals("reset")) {
                for (int si = 0;si < 8;si++) {
                    for (int a = 0;a < 8;a++) {
                        playButtons[si][a].setBackgroundResource(R.drawable.playbtn_used);
                        if (th.equals("0")) {
                            playButtons[si][a].setBackgroundResource(R.drawable.playbtn);
                        } else {
                            playButtons[si][a].setBackgroundResource(R.drawable.playbtn_dark);
                        }
                    }
                }
            } else if (values[0].equals("on")) {
                try {
                    GradientDrawable shapeDrawable = (GradientDrawable) playButtons[Integer.parseInt(values[2]) - 1][Integer.parseInt(values[3]) - 1].getBackground().getCurrent();
                    shapeDrawable.setColor(velocity[Integer.parseInt(values[5])]);
                } catch (Exception e) {

                }
            } else if (values[0].equals("off")) {
                try {
                    GradientDrawable shapeDrawable = (GradientDrawable) playButtons[Integer.parseInt(values[2]) - 1][Integer.parseInt(values[3]) - 1].getBackground().getCurrent();
                    shapeDrawable.setColor(Color.argb(0,0,0,0));
                } catch (Exception e) {

                }
            } else if (values[0].equals("finish")) {
                changeBtnColor(chain_radio);
            }
        }

        @Override
        protected void onCancelled(Boolean aBoolean) {
            super.onCancelled(aBoolean);
        }
    }


    //LED저장
    private class saveLED extends AsyncTask<String, String, Boolean> {

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(con);
            progressDialog.setTitle(getString(R.string.Progress_saveLED));
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... strings) {//[0] main [1] work
            File main = new File(strings[0]);
            File work = new File(strings[1]);

            File[] list_work = work.listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    return file.isFile();
                }
            });

            File[] list_main = main.listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    return file.isFile();
                }
            });

            //main 삭제
            if (main.exists()&&list_main != null) {//TODO: NullPointerE
                for (File f:list_main) {
                    f.delete();
                }
            }

            progressDialog.setMax(list_work.length);
            //Main으러 복사(저장)
            int count = 0;
            for (File f:list_work) {
                String path = f.getAbsolutePath();
                publishProgress(String.valueOf(count), path);
                copyFile(f, strings[0], f.getName());
                count++;
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            progressDialog.setProgress(Integer.parseInt(values[0]));
            progressDialog.setMessage(values[1]);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            progressDialog.dismiss();
        }

        private boolean copyFile(File file , String save_file, String name){
            boolean result;
            new File(save_file).mkdirs();
            if(file!=null&&file.exists()){
                try {
                    FileInputStream fis = new FileInputStream(file);
                    FileOutputStream newfos = new FileOutputStream(save_file + name);
                    int readcount=0;
                    byte[] buffer = new byte[1024];
                    while((readcount = fis.read(buffer,0,1024))!= -1){
                        newfos.write(buffer,0,readcount);
                    }
                    newfos.close();
                    fis.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                result = true;
            }else{
                result = false;
            }
            return result;
        }
    }
}