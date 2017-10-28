package unitor.uni.k1a2.unitor;

/**
 * Created by K1A2 on 2017-05-02.
 */

public class SoundListItem {

    private String nameStr;
    private String pathStr;

    public void setName(String name) {
        nameStr = name;
    }

    public void setPath(String path) {
        pathStr = path;
    }

    public String getName() {
        return this.nameStr;
    }

    public String getPath() {
        return this.pathStr;
    }
}
