import java.util.Date;
import java.util.Random;
import java.util.UUID;

public class Torrent
{
    private String name;
    private String desc;
    private String magnet;
    private String key;
    
    public Torrent(){
        this.key = String.valueOf(UUID.randomUUID() + "" + new Date().getTime());
    }

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
                ", key='" + key + '\'' +
                '}';
    }

    public String getKey() {
        return key;
    }
}