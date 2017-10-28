package unitor.uni.k1a2.unitor;

/**
 * Created by K1A2 on 2017-05-02.
 */

public class UnipackListViewItem {

    private String titleStr;
    private String producerStr;
    private String pathStr;

    public void setTitle(String title) {
        titleStr = title;
    }

    public void setProducer(String producer) {
        producerStr = producer;
    }

    public void setPath(String path) {
        pathStr = path;
    }

    public String getTitle() {
        return this.titleStr;
    }

    public String getProducer() {
        return this.producerStr;
    }

    public String getPath() {
        return this.pathStr;
    }
}
