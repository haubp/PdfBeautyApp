package opswat.com.network.model.device;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import opswat.com.network.model.connection.Connection;
import opswat.com.network.model.device.application.ApplicationReport;

/**
 * Created by H. Len Vo on 9/4/18.
 */
public class Soh {
    @SerializedName("os_info")
    private List<OsInfo> osInfos;

    @SerializedName("system")
    private System system;

    @SerializedName("last_time_ip_scanning")
    private long lastScanIp;

    @SerializedName("applications")
    private List<ApplicationReport> applicationReports;

    @SerializedName("ip_scanning")
    private List<Connection> ipConnections;

    public List<OsInfo> getOsInfos() {
        return osInfos;
    }

    public void setOsInfos(List<OsInfo> osInfos) {
        this.osInfos = osInfos;
    }

    public System getSystem() {
        return system;
    }

    public void setSystem(System system) {
        this.system = system;
    }

    public long getLastScanIp() {
        return lastScanIp;
    }

    public void setLastScanIp(long lastScanIp) {
        this.lastScanIp = lastScanIp;
    }

    public List<ApplicationReport> getApplicationReports() {
        return applicationReports;
    }

    public void setApplicationReports(List<ApplicationReport> applicationReports) {
        this.applicationReports = applicationReports;
    }

    public List<Connection> getIpConnections() {
        return ipConnections;
    }

    public void setIpConnections(List<Connection> ipConnections) {
        this.ipConnections = ipConnections;
    }

    @Override
    public String toString() {
        return "Soh{" +
                "osInfos=" + osInfos +
                ", system=" + system +
                ", lastScanIp=" + lastScanIp +
                ", applicationReports=" + applicationReports +
                ", ipConnections=" + ipConnections +
                '}';
    }
}
