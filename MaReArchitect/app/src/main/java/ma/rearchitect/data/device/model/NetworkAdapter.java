package ma.rearchitect.data.device.model;

/**
 * Created by H. Len Vo on 8/23/18.
 */
public class NetworkAdapter {
    private String mac;
    private String ipv4;
    private String ipv6;

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getIpv4() {
        return ipv4;
    }

    public void setIpv4(String ipv4) {
        this.ipv4 = ipv4;
    }

    public String getIpv6() {
        return ipv6;
    }

    public void setIpv6(String ipv6) {
        this.ipv6 = ipv6;
    }
}
