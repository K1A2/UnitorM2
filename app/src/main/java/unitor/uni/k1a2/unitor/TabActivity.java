package unitor.uni.k1a2.unitor;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by K1A2 on 2017-07-31.
 */

public class TabActivity extends AppCompatActivity implements KeysoundFragment.onPlayed, KeysoundFragment.delete, SoundFragDialog.FileOnClickListener {

    private long backKeyPress;
    private String path;
    private KeysoundFragment keysoundFragment;
    private ArrayList<Object[]> sound_load;
    private SoundPool soundPool;
    private SharedPreferences info, kill;
    private boolean isfinish = false;
    private boolean isUnloaded = false;
    private getLED getled;
    private Filerw filerw;
    private SoundLoad soundLoad;
    private PowerManager.WakeLock wakeLock;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        final SharedPreferences theme = PreferenceManager.getDefaultSharedPreferences(this);
        final String th = theme.getString("setting_theme", "0");
        if (th.equals("0")) {
            setTheme(R.style.AppTheme);
        } else {
            setTheme(R.style.AppThemeDark);
        }
        setContentView(R.layout.activity_tab);

        //프리퍼런스
        info = getSharedPreferences("Packinfo", Context.MODE_PRIVATE);
        filerw = new Filerw(TabActivity.this);
        kill = PreferenceManager.getDefaultSharedPreferences(TabActivity.this);
        path = info.getString("PATH", "null");

        if (kill.getBoolean("KILL", true)) {
            File dele = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/unipackProject/worksound/keySound.txt");
            if (dele.exists()) {
                dele.delete();
            }
            File LED = new File(path + "/keyLED/");
            if (LED.exists()&&kill.getBoolean("LED", true)) {
                kill.edit().putBoolean("LED", false).commit();
                getled = new getLED();
                getled.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, path + "/keyLED/", Environment.getExternalStorageDirectory().getAbsolutePath() + "/unipackProject/workLED/");
            } else {
                LED.mkdirs();
            }
        }

        //화면유지
        if (theme.getBoolean("setting_screen", false)) {
            PowerManager powerManager = (PowerManager)getSystemService(Context.POWER_SERVICE);
            wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "ScreenWake");
            wakeLock.acquire();

        }

        //초기화
        sound_load = new ArrayList<Object[]>();

        //액션바
        ActionBar main = getSupportActionBar();
        main.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        main.setLogo(R.mipmap.ic_launcher);
        main.setDisplayShowHomeEnabled(true);
        main.setDisplayUseLogoEnabled(true);

        //인포탭
        ActionBar.Tab infoTab = main.newTab();
        infoTab.setText("info");
        TabsListener<InfoFragment> a = new TabsListener<InfoFragment>(this, "1", InfoFragment.class);
        infoTab.setTabListener(a);
        main.addTab(infoTab);

        ActionBar.Tab keySoundTab = main.newTab();
        keySoundTab.setText("keySound");
        TabsListener<KeysoundFragment> b = new TabsListener<KeysoundFragment>(this, "2", KeysoundFragment.class);
        keySoundTab.setTabListener(b);
        main.addTab(keySoundTab);

        ActionBar.Tab keyLEDTab = main.newTab();
        keyLEDTab.setText("keyLED");
        TabsListener<KeyledFragment> c = new TabsListener<KeyledFragment>(this, "3", KeyledFragment.class);
        keyLEDTab.setTabListener(c);
        main.addTab(keyLEDTab);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("seltab", getSupportActionBar().getSelectedNavigationIndex());
    }

    @Override
    protected void onResume() {
        super.onResume();
        File sounds = new File(path + "/sounds/");
        if (sounds.exists()&&(!path.equals("null"))&&(getled == null||getled.getStatus() != AsyncTask.Status.RUNNING)) {
            loadSound(sounds);
        }
    }

    //멈출시 언로드
    @Override
    protected void onPause() {
        super.onPause();
        unLoad();
        if (wakeLock != null) {
            wakeLock.release();
        }
        if (!isfinish) {
            kill.edit().putBoolean("KILL", false).commit();
        }
    }

    //메뉴 생성
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.actionbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //메뉴 선택


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //TODO: MENU CLICKED
            case R.id.Menu_saveall:
                Toast toast = new Toast(TabActivity.this);
                String title = info.getString("TITLE", "null");
                String producer = info.getString("PRODUCER", "null");
                String chain = info.getString("CHAIN", "null");
                if ((title.equals("")||title.equals("null"))||(producer.equals("")||producer.equals("null"))||(chain.equals("")||chain.equals("null"))) {
                    toast.cancel();
                    toast.makeText(TabActivity.this, getString(R.string.Info_null_all), Toast.LENGTH_SHORT).show();
                } else {
                    new saveAll().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "SAVE");
                }
                return true;

            case R.id.Menu_exportzip:
                Toast toasts = new Toast(TabActivity.this);
                String titles = info.getString("TITLE", "null");
                String producers = info.getString("PRODUCER", "null");
                String chains = info.getString("CHAIN", "null");
                if ((titles.equals("")||titles.equals("null"))||(producers.equals("")||producers.equals("null"))||(chains.equals("")||chains.equals("null"))) {
                    toasts.cancel();
                    toasts.makeText(TabActivity.this, getString(R.string.Info_null_all), Toast.LENGTH_SHORT).show();
                } else {
                    new saveAll().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "ZIP");
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //두번 클릭시 종료
    public void onBackPressed() {
        Toast toast = new Toast(TabActivity.this);
        if (System.currentTimeMillis() - backKeyPress < 2000) {
            String title = info.getString("TITLE", "null");
            String producer = info.getString("PRODUCER", "null");
            String chain = info.getString("CHAIN", "null");
            if ((title.equals("")||title.equals("null"))||(producer.equals("")||producer.equals("null"))||(chain.equals("")||chain.equals("null"))) {
                toast.cancel();
                toast.makeText(TabActivity.this, getString(R.string.Info_null_all), Toast.LENGTH_SHORT).show();
            } else {
                info.edit().clear().commit();
                startActivity(new Intent(TabActivity.this, MainActivity.class));
                kill.edit().putBoolean("KILL", true)
                        .putBoolean("LED", true).commit();
                isfinish = true;
                new Delete_LEDs().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, Environment.getExternalStorageDirectory().getAbsolutePath() + "/unipackProject/workLED/");
            }
        } else {
            toast.makeText(TabActivity.this, getString(R.string.back), Toast.LENGTH_SHORT).show();
            backKeyPress = System.currentTimeMillis();
        }
    }

    //사운드 로딩
    private void loadSound(File sounds) {
        unLoad();
        File[] sounds_list_F = sounds.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isFile()&&(file.getName().endsWith(".wav")||file.getName().endsWith(".mp3"));
            }
        });//TODO: ConcurrentModificationE
        if (sounds_list_F == null) {
            soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        } else {
            soundPool = new SoundPool(sounds_list_F.length, AudioManager.STREAM_MUSIC, 0);
        }
        soundLoad = new SoundLoad();
        soundLoad.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, sounds_list_F);
    }

    //사운드 언로드
    private void unLoad() {
        if (soundPool != null) {
            isUnloaded = true;
            for (Object[] objects:sound_load) {
                soundPool.unload((Integer)objects[1]);
            }
            isUnloaded = false;
        }
    }

    @Override
    public void onPlay(String name) {
        for (Object[] objects:sound_load) {
            if (objects[0].toString().equalsIgnoreCase(name)) {
                soundPool.play((Integer)objects[1], 1, 1, 0, 0, 1f);
            }
        }
    }

    public void onReload() {
        unLoad();
        File sounds = new File(path + "/sounds/");
        if (sounds.exists()&&(!path.equals("null"))) {
            loadSound(sounds);
        }
        if (keysoundFragment != null) {
            keysoundFragment.listChange();
        }
    }

    @Override
    public void onRemove(String name, String path) {
        for (Object[] objects:sound_load) {
            if (objects[0].toString().equals(name)) {
                soundPool.unload((Integer) objects[1]);
                new File(path).delete();
            }
        }
    }

    //사운드 선택 리스너
    @Override
    public void fileOnClicked(int id, ArrayList<String[]> in) {
        keysoundFragment = (KeysoundFragment)getSupportFragmentManager().findFragmentByTag("2");
        switch (id) {
            case R.id.FragDial_add:
                keysoundFragment.dialClose();
                new copyFile().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, in);
                break;

            case R.id.FragDial_cancel:
                keysoundFragment.dialClose();
                break;
        }
    }

    //탭클릭리스너
    private class TabsListener<T extends Fragment> implements ActionBar.TabListener {

        private Fragment mfragment;
        private final Activity mactivity;
        private final String mtag;
        private final Class<T> mclass;

        public TabsListener (Activity activity, String tag, Class<T> clz) {
            mactivity = activity;
            mtag = tag;
            mclass = clz;
        }

        @Override
        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
            if (mfragment == null) {
                mfragment = Fragment.instantiate(mactivity, mclass.getName());
                ft.replace(android.R.id.content, mfragment, mtag);
            } else {
                ft.attach(mfragment);
            }
        }

        @Override
        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
            if (mfragment != null) {
                ft.detach(mfragment);
            }
        }

        @Override
        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

        }
    }


    //LED work폴더에서 삭제
    private class Delete_LEDs extends AsyncTask<String, Integer, Boolean> {

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(TabActivity.this);
            progressDialog.setTitle(getString(R.string.ProgressDial_LED_T_Delete));
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            delete(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            progressDialog.dismiss();
            finish();
        }
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



    //LED로드
    //LED work폴더로 복사
    private class getLED extends AsyncTask<String, Integer, Boolean> {

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(TabActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setTitle(getString(R.string.ProgressDial_LED_T));
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {//[0]원레 [1]work폴더
            Log.e("LED", "LEDLOADING start");
            File dir;
            File origin = new File(params[0]);
            File[] origin_LEDs = origin.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.isFile();
                }
            });

            int max = origin_LEDs.length;
            progressDialog.setMax(max);

            int now = 1;
            for (File s:origin_LEDs) {
                publishProgress(now);
                String path_s = s.getAbsolutePath();
                dir = new File(path_s);	//복사 원본 폴더
                copyFile(dir, params[1], s.getName());	//복사 실행
                now++;
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressDialog.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            progressDialog.dismiss();
            if (soundLoad == null||soundLoad.getStatus() != Status.RUNNING) {
                loadSound(new File(path + "/sounds/"));
            }
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


    //사운드 로딩
    private class SoundLoad extends AsyncTask<File[], Object, Boolean> {

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(TabActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setTitle(getString(R.string.Asynk_Sound_load));
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(File[]... files) {
            progressDialog.setMax(files[0].length);
            Log.e("SOUNDLOAD", "SOUNDLOADING start");

            progressDialog.setMessage(getString(R.string.Progress_W_Unload));
            while(isUnloaded) {
                synchronized (this) {
                    try {
                        wait(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            //로딩
            for (int a = 0; a < files[0].length; a++) {
                int load = soundPool.load(files[0][a].getAbsolutePath(), 0);
                String name = files[0][a].getName();
                sound_load.add(new Object[]{name, load});
                publishProgress(a, files[0][a].getAbsolutePath());
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Object... values) {
            progressDialog.setProgress((Integer) values[0]);
            progressDialog.setMessage(values[1].toString());
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            progressDialog.dismiss();
        }
    }



    //파일복사
    private class copyFile extends AsyncTask<ArrayList<String[]>, Integer, Boolean> {

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(TabActivity.this);
            progressDialog.setTitle(getString(R.string.Dialog_copy_sounds));
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(ArrayList<String[]>... lists) {
            if (lists[0].size() != 0) {
                progressDialog.setMax(lists[0].size());
                int a = 0;
                for (String[] s:lists[0]) {
                    progressDialog.setMessage(path + "/sounds/" + s[0]);
                    copy(new File(s[1]), path + "/sounds/", s[0]);
                    publishProgress(++a);
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressDialog.setProgress(values[0]);

        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            progressDialog.dismiss();
            Toast.makeText(TabActivity.this, getString(R.string.Toast_Reload), Toast.LENGTH_LONG).show();
            onReload();
        }

        private boolean copy(File file , String save_file, String name){
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



    //Save All
    private class saveAll extends AsyncTask<String, String, String> {

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(TabActivity.this);
            progressDialog.setTitle(getString(R.string.Menu_saveAll));
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            publishProgress("info");
            //info 저장
            String pref_title = info.getString("TITLE", "");
            String pref_producer = info.getString("PRODUCER", "");
            String pref_chain = info.getString("CHAIN", "");
            Boolean bool_land = info.getBoolean("LAND", true);
            filerw.saveInfo(path + "/info", "title=" + pref_title + "\nproducerName=" + pref_producer + "\n" + "buttonX=8\n" + "buttonY=8\n" + "chain=" + pref_chain + "\nsquareButton="+ isTrue(bool_land) + "\nlandscape=true");

            //keySound 저장
            if (new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/unipackProject/worksound/keySound.txt").exists()) {
                publishProgress("sound");
                ArrayList<String> keySound = filerw.getKeysound_work();
                filerw.saveKeysound(path + "/keySound", keySound);
            }

            //keyLED저장
            File main = new File(path + "/keyLED/");
            File work = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/unipackProject/workLED/");

            if (main.exists()) {//TODO: NullPointerE
                File[] main_list = main.listFiles(new FileFilter() {
                    @Override
                    public boolean accept(File file) {
                        return file.isFile();
                    }
                });

                File[] work_list = work.listFiles(new FileFilter() {
                    @Override
                    public boolean accept(File file) {
                        return file.isFile();
                    }
                });

                if (work_list.length != 0) {
                    publishProgress("LED");

                    for (File f:main_list) {
                        f.delete();
                    }

                    progressDialog.setMax(work_list.length);
                    int count = 0;
                    for (File f:work_list) {
                        String path = f.getAbsolutePath();
                        publishProgress(String.valueOf(count), path);
                        copyFile(f, path + "/keyLED/", f.getName());
                        count++;
                    }
                }
            }

            return strings[0];
        }

        @Override
        protected void onProgressUpdate(String... values) {
            if (values[0].equals("info")) {
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setMessage(getString(R.string.Dialog_msg_info));
            } else if (values[0].equals("sound")) {
                progressDialog.setMessage(getString(R.string.Dialog_msg_keySound));
            } else if (values[0].equals("LED")) {
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            } else {
                progressDialog.setProgress(Integer.parseInt(values[0]));
            }
        }

        @Override
        protected void onPostExecute(String aString) {
            progressDialog.dismiss();
            Toast.makeText(TabActivity.this, getString(R.string.Toast_saveFinish), Toast.LENGTH_LONG).show();
            if (aString.equals("ZIP")) {
                new Export().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
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

        private String isTrue(Boolean bool_land) {
            if (bool_land == true) {
                return "true";
            } else {
                return "false";
            }
        }
    }


    //ZIP
    private class Export extends AsyncTask<String, String, String> {

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(TabActivity.this);
            progressDialog.setTitle(getString(R.string.Dialog_Zip));
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            String title = info.getString("TITLE", "");
            try {
                File mk = new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/unipackExport/");
                mk.mkdirs();
                zip(path, Environment.getExternalStorageDirectory().getAbsoluteFile() + "/unipackExport/" + title + ".zip");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return Environment.getExternalStorageDirectory().getAbsoluteFile() + "/unipackExport/" + title + ".zip";
        }

        private static final int COMPRESSION_LEVEL = 8;
        private static final int BUFFER_SIZE = 1024 * 2;

        public void zip(String sourcePath, String output) throws Exception {

            // 압축 대상(sourcePath)이 디렉토리나 파일이 아니면 리턴한다.
            File sourceFile = new File(sourcePath);
            if (!sourceFile.isFile() && !sourceFile.isDirectory()) {
                throw new Exception("압축 대상의 파일을 찾을 수가 없습니다.");
            }

            FileOutputStream fos = null;
            BufferedOutputStream bos = null;
            ZipOutputStream zos = null;

            try {
                fos = new FileOutputStream(output); // FileOutputStream
                bos = new BufferedOutputStream(fos); // BufferedStream
                zos = new ZipOutputStream(bos); // ZipOutputStream
                zos.setLevel(COMPRESSION_LEVEL); // 압축 레벨 - 최대 압축률은 9, 디폴트 8

                zipEntry(sourceFile, sourcePath, zos); // Zip 파일 생성
                zos.finish(); // ZipOutputStream finish
            } finally {
                if (zos != null) {
                    zos.close();
                }
                if (bos != null) {
                    bos.close();
                }
                if (fos != null) {
                    fos.close();
                }
            }
        }

        private void zipEntry(File sourceFile, String sourcePath, ZipOutputStream zos) throws Exception {
            if (sourceFile.isDirectory()) {
                if (sourceFile.getName().equalsIgnoreCase(".metadata")) { // .metadata 디렉토리 return
                    return;
                }
                File[] fileArray = sourceFile.listFiles(); // sourceFile 의 하위 파일 리스트
                for (int i = 0; i < fileArray.length; i++) {
                    zipEntry(fileArray[i], sourcePath, zos); // 재귀 호출
                }
            } else { // sourcehFile 이 디렉토리가 아닌 경우
                BufferedInputStream bis = null;
                try {
                    String sFilePath = sourceFile.getPath();
                    String zipEntryName = sFilePath.substring(sourcePath.length(), sFilePath.length());

                    bis = new BufferedInputStream(new FileInputStream(sourceFile));
                    ZipEntry zentry = new ZipEntry(zipEntryName);
                    zentry.setTime(sourceFile.lastModified());
                    zos.putNextEntry(zentry);

                    byte[] buffer = new byte[BUFFER_SIZE];
                    int cnt = 0;
                    while ((cnt = bis.read(buffer, 0, BUFFER_SIZE)) != -1) {
                        zos.write(buffer, 0, cnt);
                    }
                    zos.closeEntry();
                } finally {
                    if (bis != null) {
                        bis.close();
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();
            AlertDialog.Builder dia = new AlertDialog.Builder(TabActivity.this);
            dia.setTitle(getString(R.string.Dialog_Zip_finish));
            dia.setMessage(getString(R.string.Dialog_Zip_path) + "\n" + s);
            dia.setPositiveButton(getString(R.string.Dialog_btn_close), null);
            dia.show();
        }
    }
}
