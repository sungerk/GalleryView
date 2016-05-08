package sunger.net.org.test;

/**
 * Created by sunger on 16/5/8.
 */
public class ImageEntity {


    public boolean isSeleted() {
        return seleted;
    }

    public void setSeleted(boolean seleted) {
        this.seleted = seleted;
    }

    public  boolean seleted;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCover() {
        return cover;
    }

    public void setCover(int cover) {
        this.cover = cover;
    }

    private  String name;
    private  int cover;
}
