package unitor.uni.k1a2.unitor;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by K1A2 on 2017-05-09.
 */

public class UnipackDialogFrag extends DialogFragment {

    private ListView files_list;
    private TextView path_now;

    private String path, ths;
    private UnipackFileListViewAdapter unipackFileListViewAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragdial_unipack, container, false);

        files_list = (ListView)root.findViewById(R.id.FragDial_Unipack_list);
        path_now = (TextView)root.findViewById(R.id.FragDial_Unipack_path_now);
        unipackFileListViewAdapter = new UnipackFileListViewAdapter();

        final SharedPreferences a = PreferenceManager.getDefaultSharedPreferences(root.getContext());
        ths = a.getString("setting_theme", "0");

        path = Environment.getExternalStorageDirectory().getAbsolutePath() +"/";
        getDir(path);

        return root;
    }

    private void getDir(String dir) {
        path_now.setText(dir);
        File main = new File(dir);
        File[] files = main.listFiles();

        Arrays.sort(files, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                File file1 = (File)o1;
                File file2 = (File)o2;
                return file1.getName().compareToIgnoreCase(file2.getName());
            }
        });

        unipackFileListViewAdapter.clear();

        if (!dir.equals(path)) {
            unipackFileListViewAdapter.addItem(getResources().getDrawable(android.support.design.R.drawable.navigation_empty_icon), ".../", "");
        }

        for (File fn:files) {
            if (ths.equals("0")) {
                if (fn.isDirectory()) {
                    unipackFileListViewAdapter.addItem(getResources().getDrawable(R.drawable.ic_folder_black_48dp), fn.getName(), fn.getAbsolutePath() + "/");
                } else if (fn.getName().endsWith(".zip")) {
                    unipackFileListViewAdapter.addItem(getResources().getDrawable(R.drawable.ic_insert_drive_file_black_48dp), fn.getName(), fn.getAbsolutePath());
                }
            } else {
                if (fn.isDirectory()) {
                    unipackFileListViewAdapter.addItem(getResources().getDrawable(R.drawable.ic_folder_white_48dp), fn.getName(), fn.getAbsolutePath() + "/");
                } else if (fn.getName().endsWith(".zip")) {
                    unipackFileListViewAdapter.addItem(getResources().getDrawable(R.drawable.ic_insert_drive_file_white_48dp), fn.getName(), fn.getAbsolutePath());
                }
            }
        }

        files_list.setOnItemClickListener(onItemClickListener);

        files_list.setAdapter(unipackFileListViewAdapter);
    }

    private OnUnipackSelect onUnipackSelect;

    public interface OnUnipackSelect {
        public void OnUnipackSelected(String pack_path, String name);
    }

    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            FileListItem fileListItem = (FileListItem)unipackFileListViewAdapter.getItem(position);
            String name = fileListItem.getName();

            File clicked = new File(fileListItem.getPath());

            if (name.endsWith(".zip")) {
                onUnipackSelect.OnUnipackSelected(fileListItem.getPath(), name);
            } else if (name.equals(".../")&&fileListItem.getPath().equals("")) {
                String parent_path = new File(path_now.getText().toString()).getParent();
                getDir(parent_path + "/");
            } else if (clicked.isDirectory()) {
                if (clicked.canRead()) {
                    getDir(fileListItem.getPath());
                } else {
                    Toast.makeText(getActivity(), getString(R.string.DialogFrag_FileS_cant), Toast.LENGTH_LONG).show();
                }
            }
        }
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        onUnipackSelect = (OnUnipackSelect) activity;
    }
}
