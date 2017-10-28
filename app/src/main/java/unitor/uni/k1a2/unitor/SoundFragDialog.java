package unitor.uni.k1a2.unitor;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by K1A2 on 2017-08-05.
 */

public class SoundFragDialog extends DialogFragment {

    //뷰
    private TextView txt_current;
    private Button btn_ok, btn_no, btn_selectall;
    private ListView list_files;

    //그밖에것
    private Context con;
    private String path, ths;
    private ArrayList<String[]> selected;
    private FileListItem fileListItem;
    private FileListViewAdapter fileListViewAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragdial_sound, container, false);
        con = root.getContext();

        //뷰 초기화
        txt_current = (TextView)root.findViewById(R.id.FragDial_current_path);//현재 경로 보여줌
        btn_ok = (Button)root.findViewById(R.id.FragDial_add);//파일 추가
        btn_no = (Button)root.findViewById(R.id.FragDial_cancel);//다이얼로그 그냥 닫음
        list_files = (ListView)root.findViewById(R.id.FragDial_sound);//파일 리스트
        btn_selectall = (Button)root.findViewById(R.id.DialFrag_selectall);

        fileListViewAdapter = new FileListViewAdapter();//커스텀 어뎁터

        btn_no.setOnClickListener(onClickListener);
        btn_ok.setOnClickListener(onClickListener);

        //테마에 따라 색바꿈 (이부분은 무시해도됩니다)
        final SharedPreferences theme = PreferenceManager.getDefaultSharedPreferences(con);
        final String th = theme.getString("setting_theme", "0");
        if (th.equals("0")) {
            btn_ok.setTextColor(con.getResources().getColor(R.color.colorAccent));
            btn_no.setTextColor(con.getResources().getColor(R.color.colorAccent));
        } else {
            btn_ok.setTextColor(con.getResources().getColor(R.color.colorAccent_dark));
            btn_no.setTextColor(con.getResources().getColor(R.color.colorAccent_dark));
        }

        final SharedPreferences a = PreferenceManager.getDefaultSharedPreferences(con);
        ths = a.getString("setting_theme", "0");

        //저장소 경로 가져옴
        path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
        getDir(path);

        btn_selectall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int count = 0;
                count = fileListViewAdapter.getCount();

                for (int i = 0;i < count;i++){
                    fileListItem = (FileListItem) fileListViewAdapter.getItem(i);
                    if (fileListItem.getName().endsWith(".wav")) {
                        list_files.setItemChecked(i, true);
                    }
                }
            }
        });

       return root;
    }

    //파일 가져와서 리스트에 뿌려주는 함수
    private void getDir(String dir) {
        txt_current.setText(dir);//택스트에 현재경로 표시
        File main = new File(dir);
        File[] fires = main.listFiles();//파일목록 불러옴

        //파일 이름순으로 정렬
        Arrays.sort(fires, new Comparator<File>() {
            @Override
            public int compare(File file, File t1) {
                File file1 = (File)file;
                File file2 = (File)t1;
                return file1.getName().compareToIgnoreCase(file2.getName());
            }
        });

        fileListViewAdapter.clear();//어댑터 모두 삭제

        //루트경로가 아니면 뒤로가기칸 추가
        if (!dir.equals(path)) {
            fileListViewAdapter.addItem(getResources().getDrawable(android.support.design.R.drawable.navigation_empty_icon), ".../", "");
        }

        //파일에 따라 아이콘을 다르게해서 추가
        for (File fn:fires) {
            if (ths.equals("0")) {
                if (fn.isDirectory()) {
                    fileListViewAdapter.addItem(getResources().getDrawable(R.drawable.ic_folder_black_48dp), fn.getName(), fn.getAbsolutePath() + "/");
                } else if (fn.getName().endsWith(".wav")) {
                    fileListViewAdapter.addItem(getResources().getDrawable(R.drawable.ic_music_note_black_48dp), fn.getName(), fn.getAbsolutePath());
                }
            } else {
                if (fn.isDirectory()) {
                    fileListViewAdapter.addItem(getResources().getDrawable(R.drawable.ic_folder_white_48dp), fn.getName(), fn.getAbsolutePath() + "/");
                } else if (fn.getName().endsWith(".wav")) {
                    fileListViewAdapter.addItem(getResources().getDrawable(R.drawable.ic_music_note_white_48dp), fn.getName(), fn.getAbsolutePath());
                }
            }
        }

        //파일 리스트 클릭 리스너
        list_files.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                fileListItem = (FileListItem)fileListViewAdapter.getItem(i);

                //클릭된 파일 경로 가져옴
                File clicked = new File(fileListItem.getPath());

                //클릭된 항목이 폴더인지 파일인지 구분
                if (clicked.isDirectory()) {//폴더일때
                    if (clicked.canRead()) {//읽을수 있는지 없는지 확인
                        list_files.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
                        getDir(fileListItem.getPath());//폴더이므로 재귀호출로 하뤼폴더 리스트에 보여줌
                    } else {//못읽으면 토스트로 오류출력
                        Toast.makeText(con, getString(R.string.DialogFrag_FileS_cant), Toast.LENGTH_LONG).show();
                    }
                    list_files.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
                } else if (fileListItem.getName().equals(".../")&&fileListItem.getPath().equals("")) {//상위폴더 이동
                    String parent_path = new File(txt_current.getText().toString()).getParent();
                    getDir(parent_path + "/");//재귀호출로 상위폴더의 파일을 보여줌
                }
            }
        });

        //롱클릭시 사운드 재생(무시해도됩니다)
        list_files.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                fileListItem = (FileListItem)fileListViewAdapter.getItem(i);

                File clicked = new File(fileListItem.getPath());

                if (clicked.isFile()&&fileListItem.getName().endsWith(".wav")) {
                    SoundPool soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
                    soundPool.load(fileListItem.getPath(), 1);
                    soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                        @Override
                        public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                            soundPool.play(sampleId, 1, 1, 0, 0, 1);
                            soundPool.unload(sampleId);
                        }
                    });
                }
                return true;
            }
        });

        //리스트에 어댑터 등록으로 파일을 보여줌
        list_files.setAdapter(fileListViewAdapter);
    }

    //커스텀 리스너 구현한 부분(무시해도됩니다)
    private FileOnClickListener fileOnClickListener;

    public interface FileOnClickListener {
        public void fileOnClicked(int id, ArrayList<String[]> in);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.FragDial_add:
                    SparseBooleanArray checkedId = list_files.getCheckedItemPositions();
                    int count = fileListViewAdapter.getCount();
                    selected = new ArrayList<String[]>();
                    for (int i = 0;i < count;i++) {
                        if (checkedId.get(i)) {
                            fileListItem = (FileListItem)fileListViewAdapter.getItem(i);
                            selected.add(new String[] {fileListItem.getName(), fileListItem.getPath()});
                        }
                    }
                    fileOnClickListener.fileOnClicked(R.id.FragDial_add, selected);
                    break;

                case R.id.FragDial_cancel:
                    fileOnClickListener.fileOnClicked(R.id.FragDial_cancel, null);
                    break;
            }
        }
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        fileOnClickListener = (FileOnClickListener) activity;
    }
}
