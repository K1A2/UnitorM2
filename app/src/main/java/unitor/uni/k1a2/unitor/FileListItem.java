package unitor.uni.k1a2.unitor;

import android.graphics.drawable.Drawable;

/**
 * Created by K1A2 on 2017-05-02.
 */

public class FileListItem {

    private Drawable icoDrawable;
    private String nameStr;
    private String pathStr;

    public void setIco(Drawable icon) {
        icoDrawable = icon;
    }

    public void setName(String name) {
        nameStr = name;
    }

    public void setPath(String path) {
        pathStr = path;
    }

    public Drawable getIco() {
        return this.icoDrawable;
    }

    public String getName() {
        return this.nameStr;
    }

    public String getPath() {
        return this.pathStr;
    }
}
