import java.util.Date;
import java.util.Random;

public class Torrent
{
    private String name;
    private String desc;
    private String magnet;
    private String key;

    public Torrent(){
        this.key = String.valueOf(generateKey() + new Date().getTime());
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

    public void setKey(String key) {
        this.key = key;
    }

    public String getMagnet() {
        return magnet;
    }

    public void setMagnet(String magnet) {
        this.magnet = magnet;
    }

    public String getKey()
    {
        return key;
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

    public String generateKey()
    {
        Random rand = new Random();

        String possible = "qwertyuiopasdfghjklzxcvbnm1234567890";

        String res = "";

        for(int i = 0; i < 10; i++)
        {
            res += possible.charAt(rand.nextInt(possible.length()));
        }

        return res;
    }
}