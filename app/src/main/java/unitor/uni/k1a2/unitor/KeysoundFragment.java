package unitor.uni.k1a2.unitor;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

/**
 * Created by K1A2 on 2017-08-01.
 */

public class KeysoundFragment extends Fragment {

    //뷰
    private LinearLayout Linear_play;
    private ListView list_sounds;
    private RelativeLayout left;
    private TextView txt_current;
    private TextView keySound_content;
    private CheckBox check_delete;
    private EditText keySound_edit;
    private ImageButton switch_edit;
    private Button btn_save, btn_load;
    private PlayButton[][] playButtons;
    private RadioButton chain_select;
    private RadioGroup chain_group, mode_group;

    //그밖에것
    private Context con;
    private Filerw filerw;
    private String chain, chains;
    private SoundFragDialog soundFragDialog;
    private String main_path, selected_name = "";
    private SharedPreferences pref_info;
    private int chain_int;
    private SoundListItem soundListItem;
    private SoundListViewAdapter soundListViewAdapter;
    private ArrayList<String> keySound;
    private ArrayList<String[]> source;
    private String th;
    private int multi_int[][][], now[][][];
    private boolean isCheck = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_keysound, container, false);
        con = root.getContext();

        //뷰 초기화
        Linear_play = (LinearLayout)root.findViewById(R.id.Layout_Btns);
        chain_group = (RadioGroup)root.findViewById(R.id.RadioG_chain);
        mode_group = (RadioGroup)root.findViewById(R.id.RadioG_mode);
        list_sounds = (ListView)root.findViewById(R.id.List_sounds);
        left = (RelativeLayout)root.findViewById(R.id.KeySound_Right);
        txt_current = (TextView)root.findViewById(R.id.Text_current);
        keySound_content = (TextView) root.findViewById(R.id.keysound_content);
        keySound_edit = (EditText)root.findViewById(R.id.keysound_edit);
        switch_edit = (ImageButton)root.findViewById(R.id.keysound_switch);
        check_delete = (CheckBox)root.findViewById(R.id.check_delete);
        btn_save = (Button)root.findViewById(R.id.Keysound_save);
        btn_load = (Button)root.findViewById(R.id.Sound_LoadSounds);

        keySound_content.setMovementMethod(ScrollingMovementMethod.getInstance());
        check_delete.setChecked(false);

        //그밖에것 초기화
        pref_info = con.getSharedPreferences("Packinfo", Context.MODE_PRIVATE);
        soundListViewAdapter = new SoundListViewAdapter();
        filerw = new Filerw(con);

        //프리퍼런스
        main_path = pref_info.getString("PATH", "NULL");
        chain = pref_info.getString("CHAIN", "null");
        final SharedPreferences theme = PreferenceManager.getDefaultSharedPreferences(con);
        th = theme.getString("setting_theme", "0");
        if (th.equals("0")) {
            switch_edit.setImageResource(R.drawable.ic_remove_red_eye_black_18dp);
        } else {
            switch_edit.setImageResource(R.drawable.ic_remove_red_eye_white_18dp);
        }

        //keysound읽어옴
        File keySound_path = new File(main_path + "/keySound");
        if (new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/unipackProject/worksound/keySound.txt").exists()) {
            keySound = filerw.getKeysound_work();
        } else {
            if (!keySound_path.exists()&&keySound == null) {
                try {
                    keySound_path.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (keySound == null) {
                //사운드 파일 읽어옴
                keySound = filerw.getKeysound(keySound_path);
                filerw.save_Keysound_work(keySound);
            }
        }

        //버튼생성
        playButtons = new PlayButton[8][8];
        for (int i = 0;i < 8;i++) {
            for (int a = 0;a < 8;a++) {
                playButtons[i][a] = (PlayButton)root.findViewWithTag(String.valueOf(i + 1) + " " + String.valueOf(a + 1));//버튼초기화
                playButtons[i][a].txt.setTag(String.valueOf(i + 1) + " " + String.valueOf(a + 1));
                playButtons[i][a].txt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RadioButton radioButtonmode = (RadioButton)root.findViewById(mode_group.getCheckedRadioButtonId());
                        RadioButton radioButton = (RadioButton)root.findViewById(chain_group.getCheckedRadioButtonId());
                        String[] btn = v.getTag().toString().split("\\s+");
                        if (radioButton != null) {
                            switch (radioButtonmode.getId()) {
                                case R.id.Radio_edit_BTN:
                                    if (check_delete.isChecked()) {
                                        String xyz = String.valueOf(radioButton.getText().toString()) + " " + v.getTag();
                                        StringBuilder stringBuilder = new StringBuilder();
                                        stringBuilder.append(keySound_content.getText().toString());

                                        boolean che = true;
                                        Collections.reverse(keySound);
                                        Iterator<String> iter = keySound.iterator();
                                        while (iter.hasNext()&&che) {
                                            String s = iter.next();
                                            if (s.startsWith(xyz)) {
                                                iter.remove();
                                                che = false;
                                            }
                                        }
                                        Collections.reverse(keySound);

                                        if ((!(stringBuilder == null||stringBuilder.toString().isEmpty()||stringBuilder.toString().equals("")))&&!che) {
                                            String[] check = stringBuilder.toString().split("\n");
                                            stringBuilder = new StringBuilder();
                                            boolean ch = true;
                                            for (String c:check) {
                                                if (c.startsWith(xyz)&&ch) {
                                                    ch = false;
                                                } else {
                                                    stringBuilder.append(c + "\n");
                                                }
                                            }
                                            keySound_content.setText(stringBuilder.toString());
                                            keySound_edit.setText(stringBuilder.toString());
                                            filerw.save_Keysound_work(keySound);
                                            changeBtnColor(radioButton.getText().toString());
                                        }
                                    } else {
                                        if ((!selected_name.equals(""))||(!selected_name.isEmpty())) {
                                            String xyz = String.valueOf(radioButton.getText().toString()) + " " + v.getTag() + " " + selected_name;
                                            StringBuilder stringBuilder = new StringBuilder();
                                            stringBuilder.append(keySound_content.getText().toString());
                                            stringBuilder.append(xyz + "\n");
                                            isCheck = false;
                                            keySound_content.setText(stringBuilder.toString());
                                            keySound_edit.setText(stringBuilder.toString());
                                            isCheck = true;
                                            if (keySound == null) {
                                                keySound = new ArrayList<String>();
                                            }
                                            keySound.add(xyz);
                                            filerw.save_Keysound_work(keySound);
                                            changeBtnColor(radioButton.getText().toString());
                                        }
                                    }
                                    break;

                                case R.id.Radio_test:
                                    String clicked_chain = String.valueOf(radioButton.getId());
                                    String clicked_Y = String.valueOf(Integer.parseInt(btn[0]) - 1);
                                    String clicked_X = String.valueOf(Integer.parseInt(btn[1]) - 1);

                                    if (source != null) {
                                        for (String[] im:source) {//TODO: NullPointerE soource
                                            String math = im[0] + im[1] + im[2] + im[3];
                                            if (math.equals(clicked_chain+clicked_Y+clicked_X+String.valueOf(now[Integer.parseInt(clicked_chain)][Integer.parseInt(clicked_Y)][Integer.parseInt(clicked_X)]))) {
                                                onplayed.onPlay(im[4]);
                                                continue;
                                            }
                                        }
                                    }

                                    if (multi_int[Integer.parseInt(clicked_chain)][Integer.parseInt(clicked_Y)][Integer.parseInt(clicked_X)] == now[Integer.parseInt(clicked_chain)][Integer.parseInt(clicked_Y)][Integer.parseInt(clicked_X)]) {
                                        now[Integer.parseInt(clicked_chain)][Integer.parseInt(clicked_Y)][Integer.parseInt(clicked_X)] = 1;
                                    } else {
                                        now[Integer.parseInt(clicked_chain)][Integer.parseInt(clicked_Y)][Integer.parseInt(clicked_X)]++;
                                    }
                                    break;
                            }
                        }
                    }
                });
            }
        }

        //리스트 만들기
        mkList();

        //체인변경
        chain_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                chains = String.valueOf(i +1);
                if (keySound != null) {
                    changeBtnColor(chain);

                    keySound_content.bringToFront();
                    keySound_edit.clearFocus();
                    keySound_edit.setVisibility(View.INVISIBLE);
                    keySound_content.setVisibility(View.VISIBLE);
                    keySound_content.invalidate();
                    keySound_edit.invalidate();
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
                chain_select.setId(i);//TODO: OutOfMemoryE
                chain_select.setText(String.valueOf(i + 1));
                chain_group.addView(chain_select);
                if (i == 0) {
                    chain_select.setChecked(true);
                }
            }
        }

        //내용 편집
        switch_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap now = ((BitmapDrawable)switch_edit.getDrawable()).getBitmap();

                if (th.equals("0")) {
                    Bitmap black_eye = ((BitmapDrawable)con.getResources().getDrawable(R.drawable.ic_remove_red_eye_black_18dp)).getBitmap();
                    if (now.equals(black_eye)) {
                        switch_edit.setImageResource(R.drawable.ic_mode_edit_black_18dp);
                        setViewInvalidate(0);
                        isCheck = true;
                    } else {
                        switch_edit.setImageResource(R.drawable.ic_remove_red_eye_black_18dp);
                        setViewInvalidate(1);
                        isCheck = false;
                    }
                } else {
                    Bitmap black_eye = ((BitmapDrawable) con.getResources().getDrawable(R.drawable.ic_remove_red_eye_white_18dp)).getBitmap();
                    if (now.equals(black_eye)) {
                        switch_edit.setImageResource(R.drawable.ic_mode_edit_white_18dp);
                        setViewInvalidate(0);
                        isCheck = true;
                    } else {
                        switch_edit.setImageResource(R.drawable.ic_remove_red_eye_white_18dp);
                        setViewInvalidate(1);
                        isCheck = false;
                    }
                }
            }
        });

        //리스너 등록
        //리스트 롱클릭
        list_sounds.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                soundListItem = (SoundListItem) soundListViewAdapter.getItem(i);

                PopupMenu popupMenu = new PopupMenu(con, view);
                popupMenu.getMenuInflater().inflate(R.menu.sound_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.sound_pop_Play:
                                onplayed.onPlay(soundListItem.getName());
                                break;

                            case R.id.sound_pop_Delete:
                                AlertDialog.Builder ask_delete = new AlertDialog.Builder(getActivity())
                                        .setTitle(getString(R.string.Dial_sound_del))
                                        .setMessage(String.format(getString(R.string.Dial_sound_ask), soundListItem.getName()))
                                        .setPositiveButton(getString(R.string.Dial_sound_del), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                d.onRemove(soundListItem.getName(), soundListItem.getPath());
                                                soundListViewAdapter.remove(i);
                                                list_sounds.setAdapter(soundListViewAdapter);
                                                if (chains != null) {
                                                    changeBtnColor(chains);
                                                }
                                            }
                                        })
                                        .setNegativeButton(getString(R.string.cancel), null);
                                ask_delete.show();
                                break;
                        }
                        return true;
                    }
                });
                popupMenu.show();
                return true;
            }
        });

        //리스트 그냥 클릭
        list_sounds.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                soundListItem = (SoundListItem) soundListViewAdapter.getItem(i);
                selected_name = soundListItem.getName();
                txt_current.setText(String.format(getString(R.string.Text_current), selected_name));

                //버튼색깔 변경
                RadioButton radioButtonmode = (RadioButton)root.findViewById(mode_group.getCheckedRadioButtonId());
                RadioButton radioButton = (RadioButton)root.findViewById(chain_group.getCheckedRadioButtonId());

                if ((radioButtonmode.getId() == R.id.Radio_edit_BTN)&&(keySound != null)&&(radioButton != null)) {
                    String chain = radioButton.getText().toString();
                    changeBtnColor(chain);
                }
            }
        });

        //모드변경
        mode_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.Radio_edit_BTN:
                        multi_int = null;
                        now = null;
                        source = null;
                        switch_edit.setEnabled(true);
                        check_delete.setEnabled(true);
                        break;

                    case R.id.Radio_test:
                        switch_edit.setEnabled(false);
                        check_delete.setEnabled(false);
                        setViewInvalidate(1);
                        if (th.equals("0")) {
                            switch_edit.setImageResource(R.drawable.ic_remove_red_eye_black_18dp);
                        } else {
                            switch_edit.setImageResource(R.drawable.ic_remove_red_eye_white_18dp);
                        }

                        keySound = filerw.getKeysound_work();

                        //다중매핑 검사
                        if ((!(chain.equals("null")))&&keySound != null) {
                            multi_int = new int[chain_int][8][8];
                            now = new int[chain_int][8][8];
                            source = new ArrayList<String[]>();
                            for (int c = 0;c < chain_int;c++) {
                                for (int is = 0; is < 8; is++) {
                                    for (int si = 0; si < 8; si++) {
                                        now[c][is][si] = 1;
                                        multi_int[c][is][si] = 0;
                                    }
                                }
                            }
                            for (String s:keySound) {
                                String[] as = s.split("\\s+");
                                try {
                                    if (multi_int[Integer.parseInt(as[0]) - 1][Integer.parseInt(as[1]) - 1][Integer.parseInt(as[2]) -1] >= 1) {
                                        multi_int[Integer.parseInt(as[0]) - 1][Integer.parseInt(as[1]) - 1][Integer.parseInt(as[2]) -1]++;
                                    } else {
                                        multi_int[Integer.parseInt(as[0]) - 1][Integer.parseInt(as[1]) - 1][Integer.parseInt(as[2]) -1] = 1;
                                    }
                                    source.add(new String[] {String.valueOf(Integer.parseInt(as[0]) - 1), String.valueOf(Integer.parseInt(as[1]) - 1), String.valueOf(Integer.parseInt(as[2]) -1), String.valueOf(multi_int[Integer.parseInt(as[0]) - 1][Integer.parseInt(as[1]) - 1][Integer.parseInt(as[2]) -1]), as[3]});
                                } catch (Exception e) {

                                }
                            }
                        }
                        break;
                }
            }
        });


        //내용편집 리스너
        keySound_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String[] in = editable.toString().split("\\s+");
                if (keySound != null&&isCheck&&in.length >= 4) {
                    RadioButton radioButton = (RadioButton)root.findViewById(chain_group.getCheckedRadioButtonId());
                    Iterator<String> key_it = keySound.iterator();
                    while (key_it.hasNext()) {
                        String s = key_it.next();

                        if (s.startsWith(radioButton.getText().toString())) {
                            key_it.remove();
                        }
                    }
                    for (String s:editable.toString().split("\n")) {
                        keySound.add(s);
                    }
                    keySound_content.setText(editable.toString());
                    filerw.save_Keysound_work(keySound);
                    changeBtnColor(radioButton.getText().toString());
                } else {

                }
            }
        });

        //저장 리스너
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder ask_save = new AlertDialog.Builder(con);
                ask_save.setTitle(getString(R.string.Dialog_Asksave));
                ask_save.setPositiveButton(getString(R.string.Dialog_save), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        keySound = filerw.getKeysound_work();
                        filerw.saveKeysound(main_path, keySound);
                    }
                });
                ask_save.setNegativeButton(getString(R.string.Dialog_cancel), null);
                ask_save.show();
            }
        });

        //사운드 로딩
        btn_load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getFragmentManager();
                soundFragDialog = new SoundFragDialog();
                soundFragDialog.setCancelable(false);
                soundFragDialog.show(fragmentManager, "sound");
            }
        });

        return root;
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

    //버튼 색깔변경
    private void changeBtnColor(String chain) {

        for (int si = 0;si < 8;si++) {
            for (int a = 0;a < 8;a++) {
                if (th.equals("0")) {
                    playButtons[si][a].setBackgroundResource(R.drawable.playbtn);
                } else {
                    playButtons[si][a].setBackgroundResource(R.drawable.playbtn_dark);
                }
            }
        }

        StringBuilder stringBuilder = new StringBuilder("");
        for (String s:keySound) {
            if (s.startsWith(chain + " ")) {
                String in[] = s.replaceFirst(chain + " ", "").split("\\s+");
                try {
                    if (th.equals("0")) {
                        playButtons[Integer.parseInt(in[0]) - 1][Integer.parseInt(in[1]) - 1].setBackgroundResource(R.drawable.playbtn_used);
                    } else {
                        playButtons[Integer.parseInt(in[0]) - 1][Integer.parseInt(in[1]) - 1].setBackgroundResource(R.drawable.playbtn_dark_used);
                    }
                    stringBuilder.append(s + "\n");
                } catch (Exception e) {

                }
            } else {
                continue;
            }
        }
        if (!isCheck) {
            keySound_edit.setText(stringBuilder.toString());
        }
        keySound_content.setText(stringBuilder.toString());
        isCheck = true;
    }

    //뷰 다시그림
    private void setViewInvalidate(int requestQode) {
        if (requestQode == 0) {
            keySound_edit.bringToFront();
            keySound_edit.setVisibility(View.VISIBLE);
            keySound_content.setVisibility(View.INVISIBLE);
            keySound_edit.setText(keySound_content.getText().toString());
        } else {
            keySound_content.bringToFront();
            keySound_edit.clearFocus();
            keySound_edit.setVisibility(View.INVISIBLE);
            keySound_content.setVisibility(View.VISIBLE);
            keySound_content.setText(keySound_edit.getText().toString());
        }

        keySound_edit.invalidate();
        keySound_content.invalidate();
    }

    //sound다이얼로그 닫음
    public void dialClose() {
        if (soundFragDialog != null) {
            soundFragDialog.dismiss();
        }
    }

    public void listChange() {
        soundListViewAdapter.clear();
        mkList();
    }

    private onPlayed onplayed;
    private delete d;

    public interface onPlayed {
        public void onPlay(String name);
    }

    public interface delete {
        public void onRemove(String name, String path);
    }

    private void mkList() {
        File sounds = new File(main_path + "/sounds/");

        //사운드 불러오기
        if (sounds.exists()) {
            File[] sounds_list_F = sounds.listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    return file.isFile()&&(file.getName().endsWith(".wav")||file.getName().endsWith(".mp3"));
                }
            });
            for (File f:sounds_list_F) {
                soundListViewAdapter.addItem(f.getName(), f.getAbsolutePath());
            }
            list_sounds.setAdapter(soundListViewAdapter);
        } else {
            sounds.mkdirs();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        onplayed = (onPlayed)activity;
        d = (delete)activity;
    }
}
