package unitor.uni.k1a2.unitor;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

/**
 * Created by K1A2 on 2017-08-01.
 */

public class InfoFragment extends Fragment {

    //뷰
    private EditText edit_title, edit_producer, edit_chain;
    private CheckBox checkland;
    private Button btn_save;

    //그밖에것
    private Context con;
    private Boolean bool_land;
    private SharedPreferences pref_info;
    private SharedPreferences.Editor pref_infoedit;
    private String pref_title, pref_producer, pref_chain, pack_path;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_info, container, false);
        con = root.getContext();

        //초기화
        edit_title = (EditText)root.findViewById(R.id.Edit_Title);
        edit_producer = (EditText)root.findViewById(R.id.Edit_Producer);
        edit_chain = (EditText)root.findViewById(R.id.Edit_Chain);
        checkland = (CheckBox)root.findViewById(R.id.Check_Square);
        btn_save = (Button)root.findViewById(R.id.Btn_save);

        //그밖에것
        pref_info = con.getSharedPreferences("Packinfo", Context.MODE_PRIVATE);
        pref_infoedit = pref_info.edit();

        //path
        pack_path = pref_info.getString("PATH", "");

        if (savedInstanceState != null) {
            //재실행시
            pref_title = savedInstanceState.getString("TITLE", "");
            pref_producer = savedInstanceState.getString("PRODUCER", "");
            pref_chain = savedInstanceState.getString("CHAIN", "");
            bool_land = savedInstanceState.getBoolean("LAND", true);
        } else {
            //일번적으로 프리퍼런스에서 가져옴
            pref_title = pref_info.getString("TITLE", "");
            pref_producer = pref_info.getString("PRODUCER", "");
            pref_chain = pref_info.getString("CHAIN", "");
            bool_land = pref_info.getBoolean("LAND", true);
        }

        edit_title.setText(pref_title);
        edit_producer.setText(pref_producer);
        edit_chain.setText(pref_chain);

        checkland.setChecked(bool_land);

        //리스너 등록
        //택스트 와쳐
        edit_title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                pref_infoedit.putString("TITLE", editable.toString())
                        .commit();
                pref_title = editable.toString();
            }
        });
        edit_producer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                pref_infoedit.putString("PRODUCER", editable.toString())
                        .commit();
                pref_producer = editable.toString();
            }
        });
        edit_chain.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                pref_infoedit.putString("CHAIN", editable.toString())
                        .commit();
                pref_chain = editable.toString();
            }
        });

        //체크박스리스너
        checkland.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                bool_land = b;
                pref_infoedit.putBoolean("LAND", b)
                        .commit();
            }
        });

        //세이브 버튼 리스너
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder ask_save = new AlertDialog.Builder(con);
                ask_save.setTitle(getString(R.string.Dialog_Asksave));
                ask_save.setPositiveButton(getString(R.string.Dialog_save), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        saveInfo();
                    }
                });
                ask_save.setNegativeButton(getString(R.string.Dialog_cancel), null);
                ask_save.show();
            }
        });

        return root;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (edit_title != null) outState.putString("TITLE", edit_title.getText().toString());
        if (edit_producer != null) outState.putString("PRODUCER", edit_producer.getText().toString());
        if (edit_chain != null) outState.putString("CHAIN", edit_chain.getText().toString());
        if (checkland != null) outState.putBoolean("LAND", checkland.isChecked());
    }

    private void saveInfo() {
        new Filerw(con).saveInfo(pack_path, "title=" + pref_title + "\nproducerName=" + pref_producer + "\n" + "buttonX=8\n" + "buttonY=8\n" + "chain=" + pref_chain + "\nsquareButton="+ isTrue(bool_land) + "\nlandscape=true");
    }

    private String isTrue(Boolean bool_land) {
        if (bool_land == true) {
            return "true";
        } else {
            return "false";
        }
    }
}
