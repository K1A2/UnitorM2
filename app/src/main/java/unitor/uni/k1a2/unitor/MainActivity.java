package unitor.uni.k1a2.unitor;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by K1A2 on 2017-07-30.
 */

public class MainActivity extends AppCompatActivity implements UnipackDialogFrag.OnUnipackSelect {

    //뷰
    private ListView list_unipack;
    private FloatingActionButton floating_new, floating_setting, floating_add;

    //변수
    private String main_path;
    private UnipackListItem unipackListItem;
    private UnipackListViewAdapter unipackListViewAdapter;
    private UnipackDialogFrag unipackDialogFrag;
    private InterstitialAd interstitialAd;
    private boolean show = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        final SharedPreferences theme = PreferenceManager.getDefaultSharedPreferences(this);
        final String th = theme.getString("setting_theme", "0");
        if (th.equals("0")) {
            setTheme(R.style.AppTheme);
        } else {
            setTheme(R.style.AppThemeDark);
        }
        setContentView(R.layout.activity_main);

        ActionBar a = getSupportActionBar();
        a.hide();

        //광고
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getString(R.string.app_ad));
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
            }

            @Override
            public void onAdFailedToLoad(int i) {
            }

            @Override
            public void onAdLeftApplication() {
            }

            @Override
            public void onAdOpened() {
            }

            @Override
            public void onAdLoaded() {
                if (show) {
                    interstitialAd.show();
                    show = false;
                }
            }
        });

        interstitialAd.loadAd(new AdRequest.Builder().build());

        //뷰 초기화
        list_unipack = (ListView)findViewById(R.id.Unipack_List);
        floating_add = (FloatingActionButton)findViewById(R.id.fab_import);
        floating_new = (FloatingActionButton)findViewById(R.id.fab_new);
        floating_setting = (FloatingActionButton)findViewById(R.id.fab_setting);

        //그밖에것 초기화
        unipackListViewAdapter = new UnipackListViewAdapter();
        final SharedPreferences.Editor pack_info = getSharedPreferences("Packinfo", Context.MODE_PRIVATE).edit();

        main_path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/unipackProject/";

        File main = new File(main_path);
        if (!main.exists()) {
            main.mkdirs();
        }

        //.nomedia생성
        File nomedia = new File(main_path + ".nomedia");
        if (!nomedia.exists()) {
            try {
                nomedia.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        createList();

        //리스너 등록
        list_unipack.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                unipackListItem = (UnipackListItem)unipackListViewAdapter.getItem(i);
                pack_info.putString("PATH", unipackListItem.getPath())
                        .putString("TITLE", unipackListItem.getTitle())
                        .putString("PRODUCER", unipackListItem.getProducer())
                        .putString("CHAIN", unipackListItem.getChain())
                        .putBoolean("LAND", isTrue(unipackListItem.getLand()))
                        .commit();
                startActivity(new Intent(MainActivity.this, TabActivity.class));
                finish();
            }
        });

        //리스트뷰 롱클릭
        list_unipack.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int is, long l) {
                unipackListItem = (UnipackListItem)unipackListViewAdapter.getItem(is);
                AlertDialog.Builder ask_delete = new AlertDialog.Builder(MainActivity.this);
                ask_delete.setTitle(String.format(getString(R.string.Dialog_Title_del), unipackListItem.getTitle()));
                ask_delete.setMessage(String.format(getString(R.string.Dial_sound_ask), unipackListItem.getTitle()));
                ask_delete.setPositiveButton(getString(R.string.Dial_sound_del), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        new unipackDelete().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, unipackListItem.getTitle(), unipackListItem.getPath());
                        unipackListViewAdapter.remove(is);
                    }
                });
                ask_delete.setNegativeButton(getString(R.string.Dialog_cancel), null);
                ask_delete.show();

                return true;
            }
        });

        //플로팅 버튼
        View.OnClickListener fab_click = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.fab_new:
                        final LinearLayout layout = (LinearLayout)View.inflate(MainActivity.this, R.layout.dialog_input, null);
                        final EditText title = (EditText)layout.findViewById(R.id.Dialog_title);
                        final EditText producer = (EditText)layout.findViewById(R.id.Dialog_producer);
                        final EditText chain = (EditText)layout.findViewById(R.id.Dialog_chain);
                        AlertDialog.Builder input = new AlertDialog.Builder(MainActivity.this);
                        input.setView(layout);
                        input.setTitle("Info");
                        input.setPositiveButton(getString(R.string.Dialog_make), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                final String t = title.getText().toString(), p = producer.getText().toString(), c = chain.getText().toString();
                                if ((t.length() == 0||t.equals(""))||(p.length() == 0||p.equals(""))||(c.length() == 0||c.equals(""))) {
                                    Toast.makeText(MainActivity.this, getString(R.string.Dialog_null), Toast.LENGTH_SHORT).show();
                                } else {
                                    String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/unipackProject/" + t + "/";
                                    new File(path).mkdirs();
                                    pack_info.putString("TITLE", t);
                                    pack_info.putString("PRODUCER", p);
                                    pack_info.putString("CHAIN", c);
                                    pack_info.putString("PATH", path);
                                    pack_info.putBoolean("LAND", isTrue("true"));
                                    pack_info.commit();
                                    new Filerw(getApplicationContext()).saveInfo(path, "title=" + t + "\nproducerName=" + p + "\n" + "buttonX=8\n" + "buttonY=8\n" + "chain=" + c + "\nsquareButton=true" + "\nlandscape=true");
                                    startActivity(new Intent(MainActivity.this, TabActivity.class));
                                    finish();
                                }
                            }
                        });
                        input.show();
                        break;

                    case R.id.fab_import:
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        unipackDialogFrag = new UnipackDialogFrag();
                        unipackDialogFrag.setCancelable(true);
                        unipackDialogFrag.show(fragmentManager, "select_unipack");
                        break;

                    case R.id.fab_setting:
                        startActivity(new Intent(MainActivity.this, SettingActivity.class));
                        finish();
                        break;
                }
            }
        };

        floating_setting.setOnClickListener(fab_click);
        floating_new.setOnClickListener(fab_click);
        floating_add.setOnClickListener(fab_click);

        //강제종료여부 확인
        boolean kill = theme.getBoolean("KILL", true);

        if (!kill) {
            SharedPreferences info = getSharedPreferences("Packinfo", Context.MODE_PRIVATE);
            String title = info.getString("TITLE", "");

            AlertDialog.Builder die = new AlertDialog.Builder(MainActivity.this);
            die.setTitle(String.format(getString(R.string.Dialog_die_T), title));
            die.setMessage(String.format(getString(R.string.Dialog_die_M), title));
            die.setPositiveButton(getString(R.string.Dialog_die_OK), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(MainActivity.this, TabActivity.class));
                    finish();
                }
            });
            die.setNegativeButton(getString(R.string.Dialog_die_NO), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    theme.edit().putBoolean("KILL", true).commit();
                }
            });
            die.setCancelable(false);
            die.show();
        }
    }

    //리스트 생성
    private void createList() {
        unipackListViewAdapter.clear();

        File main = new File(main_path);
        File[] list_unipack_file = main.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isDirectory();
            }
        });

        if (list_unipack_file != null) {
            for (File path:list_unipack_file) {
                String list_path = path.getAbsolutePath();

                File info_path = new File(list_path + "/info");
                if (info_path.exists()) {
                    String content[] = getInfo(info_path);

                    if (content[0] == null) {
                        content[0] = "";
                    } else if (content[1] == null) {
                        content[1] = "";
                    } else if (content[2] == null) {
                        content[2] = "";
                    } else if (content[3] == null) {
                        content[3] = "true";
                    }
                    unipackListViewAdapter.addItem(content[0], content[1], list_path, content[2], content[3]);
                }
            }
        }

        list_unipack.setAdapter(unipackListViewAdapter);
    }

    //info읽어옴
    private String[] getInfo(File info) {
        ArrayList<String> content = new ArrayList<String>();

        try {
            BufferedReader br = new BufferedReader(new FileReader(info));
            String line;

            while ((line = br.readLine()) != null) {
                content.add(line);
            }
            br.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] con = new String[4];
        for (String s : content) {
            if (s.startsWith("title=")) {
                con[0] = s.replaceFirst("title=", "");
            } else if (s.startsWith("producerName=")) {
                con[1] = s.replaceFirst("producerName=", "");
            } else if (s.startsWith("chain=")) {
                con[2] = s.replaceFirst("chain=", "");
            } else if (s.startsWith("squareButton=")) {
                con[3] = s.replaceFirst("squareButton=", "");
            }
        }
        return con;
    }

    //true false
    private Boolean isTrue(String boolStr) {
        if (boolStr.equals("true")) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void OnUnipackSelected(String pack_path, String name) {
        unipackDialogFrag.dismiss();
        new unzipUnipck().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,pack_path, name, Environment.getExternalStorageDirectory().getAbsolutePath() + "/unipackProject/" + name);
    }

    //유니팩 삭제
    private class unipackDelete extends AsyncTask<String, Object, Boolean> {

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... objects) {
            progressDialog.setTitle(String.format(getString(R.string.Dialog_Title_del), objects[0]));
            String path = objects[1];
            delete(path);
            return null;
        }

        private void delete(String path) {
            File d = new File(path);
            if (d.exists()) {
                File[] childFileList = d.listFiles();
                for (File childFile : childFileList) {
                    if (childFile.isDirectory()) {
                        delete(childFile.getPath());
                    } else {
                        childFile.delete();
                    }
                }
                d.delete();
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            progressDialog.dismiss();
            unipackListViewAdapter.DataChange();
        }
    }


    //유니팩 불러오기
    private class unzipUnipck extends AsyncTask<String, String, Boolean> {

        private ProgressDialog progressDialog;
        private String target;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setTitle(getString(R.string.Dialog_getUniapck));
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            String pack_P = strings[0];
            String pack_N = strings[1];
            target = strings[2];
            publishProgress(pack_N);

            try {
                FileInputStream fileInputStream = new FileInputStream(pack_P);
                ZipInputStream zipInputStream = new ZipInputStream(fileInputStream);
                ZipEntry zipEntry = null;

                while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                    String filenameTounzip = zipEntry.getName();
                    File targetFile = new File(target, filenameTounzip);

                    if (zipEntry.isDirectory()) {
                        File path = new File(targetFile.getAbsolutePath());
                        path.mkdirs();
                    } else {
                        File path = new File(targetFile.getParent());
                        path.mkdirs();
                        Unzip(zipInputStream, targetFile);
                    }
                }

                fileInputStream.close();
                zipInputStream.close();
                return true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            progressDialog.setMessage(String.format(getString(R.string.Progress_Unzip_M), values[0]));
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (aBoolean == false) {
                progressDialog.setMessage(getString(R.string.Progress_Unzip_F));
                progressDialog.setButton(getString(R.string.Dialog_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        progressDialog.dismiss();
                    }
                });
            } else {
                progressDialog.dismiss();
                createList();
            }
        }

        private File Unzip(ZipInputStream zipInputStream, File targetFile) throws IOException {
            FileOutputStream fileOutputStream = null;

            final int BUFFER_SIZE = 1024 * 2;

            try {
                fileOutputStream = new FileOutputStream(targetFile);

                byte[] buffer = new byte[BUFFER_SIZE];
                int len = 0;
                try {
                    while ((len = zipInputStream.read(buffer)) != -1) {
                        fileOutputStream.write(buffer, 0, len);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    fileOutputStream.close();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            return targetFile;
        }
    }
}
