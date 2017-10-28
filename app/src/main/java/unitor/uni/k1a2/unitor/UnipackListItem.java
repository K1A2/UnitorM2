package unitor.uni.k1a2.unitor;

/**
 * Created by K1A2 on 2017-05-02.
 */

public class UnipackListItem {

    private String titleStr;
    private String producerStr;
    private String pathStr;
    private String chainStr;
    private String landStr;

    public void setTitle(String title) {
        titleStr = title;
    }

    public void setProducer(String producer) {
        producerStr = producer;
    }

    public void setPath(String path) {
        pathStr = path;
    }

    public void setChain(String chain) {
        chainStr = chain;
    }

    public void setLand(String land) {
        landStr = land;
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

    public String getChain() {
        return this.chainStr;
    }

    public String getLand() {
        return this.landStr;
    }
}
