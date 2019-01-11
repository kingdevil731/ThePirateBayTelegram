import java.util.Date;
import java.util.Random;

public class Torrent
{
    private String name;
    private String desc;
    private String magnet;
    
    public Torrent(){}

    public Torrent(String name, String desc, String magnet)
    {
        this.name = name;
        this.desc = desc;
        this.magnet = magnet;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getMagnet() {
        return magnet;
    }

    public void setMagnet(String magnet) {
        this.magnet = magnet;
    }

    @Override
    public String toString() {
        return "Torrent{" +
                "name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                ", magnet='" + magnet + '\'' +
                '}';
    }
}