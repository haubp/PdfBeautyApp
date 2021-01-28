package opswat.com.network.model.device;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by H. Len Vo on 8/23/18.
 */
public class NetworkAdapter {
    @SerializedName("mac")
    private String mac;

    @SerializedName("ipv4")
    private List<String> ipv4;

    @SerializedName("ipv6")
    private List<String> ipv6;

    public NetworkAdapter() {

    }

    public NetworkAdapter(opswat.com.device.model.NetworkAdapter networkAdapter) {
        this.mac = networkAdapter.getMac();
        this.ipv4 = new ArrayList<>();
        this.ipv4.add(networkAdapter.getIpv4());
        this.ipv6 = new ArrayList<>();
        this.ipv6.add(networkAdapter.getIpv6());
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public List<String> getIpv4() {
        return ipv4;
    }

    public void setIpv4(List<String> ipv4) {
        this.ipv4 = ipv4;
    }

    public List<String> getIpv6() {
        return ipv6;
    }

    public void setIpv6(List<String> ipv6) {
        this.ipv6 = ipv6;
    }

    @Override
    public String toString() {
        return "NetworkAdapter{" +
                "mac='" + mac + '\'' +
                ", ipv4=" + ipv4 +
                ", ipv6=" + ipv6 +
                '}';
    }
}
