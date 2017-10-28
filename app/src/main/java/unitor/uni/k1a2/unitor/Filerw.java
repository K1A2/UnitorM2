package unitor.uni.k1a2.unitor;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by K1A2 on 2017-08-01.
 */

public class Filerw extends ContextWrapper {

    public Filerw(Context base) {
        super(base);
    }

    //인포 다저장
    public Boolean saveInfo (String path, String con) {
        final String info_path = path + "/info";
        File in = new File(info_path);

        try {
            in.createNewFile();
            PrintWriter out = new PrintWriter(in);
            out.println(con);
            out.close();

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    //키사운드 읽어옴
    public ArrayList<String> getKeysound (File path) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            String line;

            ArrayList<String> content = new ArrayList<String>();

            while ((line = br.readLine()) != null) {
                content.add(line);
            }

            if (content != null) {
                Iterator<String> v = content.iterator();
                while (v.hasNext()) {
                    String s = v.next();
                    if (s.equals("")) {
                        v.remove();
                    }
                }
            }
            br.close();

            return content;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    //키사운드 다가져옴(work)
    public ArrayList<String> getKeysound_work () {
        ArrayList<String> content = new ArrayList<String>();
        File input = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/unipackProject/worksound/keySound.txt");

        if (input.exists()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(Environment.getExternalStorageDirectory().getAbsolutePath() + "/unipackProject/worksound/keySound.txt"));
                String line;

                while ((line = br.readLine()) != null) {
                    content.add(line);
                }
                br.close();

                return content;
            } catch (IOException e) {
                content.add("");
                e.printStackTrace();
                return content;
            }
        } else {
            content.add("");
            return content;
        }
    }

    public Boolean save_Keysound_work (ArrayList<String> content) {
        String s_path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/unipackProject/worksound/";
        File in = new File(s_path);
        in.mkdirs();
        in = new File(s_path + "keySound.txt");

        try {
            in.createNewFile();
            PrintWriter out = new PrintWriter(in);
            for (int a = 0;a < content.size();a++) {
                out.println(content.get(a));
            }
            out.close();

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Boolean saveKeysound(String path, ArrayList<String> content) {
        File p = new File(path + "/keySound");

        try {
            p.createNewFile();
            PrintWriter out = new PrintWriter(p);
            for (int a = 0;a < content.size();a++) {
                out.println(content.get(a));
            }
            out.close();

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public ArrayList<String> getLEDwork (File path) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            String line;

            ArrayList<String> content = new ArrayList<String>();

            while ((line = br.readLine()) != null) {
                content.add(line);
            }

            if (content != null) {
                Iterator<String> v = content.iterator();
                while (v.hasNext()) {
                    String s = v.next();
                    if (s.equals("")) {
                        v.remove();
                    }
                }
            }
            br.close();

            return content;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Boolean saveLEDwork(File path, String content) {

        try {
            if (!path.exists()) {
                path.createNewFile();
            }
            PrintWriter out = new PrintWriter(path);
            out.println(content);
            out.close();

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
