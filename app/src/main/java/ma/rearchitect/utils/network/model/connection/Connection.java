package opswat.com.network.model.connection;

import com.google.gson.JsonArray;
import com.google.gson.annotations.SerializedName;

import opswat.com.network.model.connection.LookupResults.LookupResults;
import opswat.com.network.model.connection.geoinfo.GeoInfo;

/**
 * Created by LenVo on 7/15/18.
 */

public class Connection {
    private boolean isScanned = false;

    @SerializedName("address")
    private String address;

    @SerializedName("geo_info")
    private GeoInfo geoInfo;

    @SerializedName("lookup_results")
    private LookupResults lookupResults;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public GeoInfo getGeoInfo() {
        return geoInfo;
    }

    public void setGeoInfo(GeoInfo geoInfo) {
        this.geoInfo = geoInfo;
    }

    public boolean isScanned() {
        return isScanned;
    }

    public void setScanned(boolean scanned) {
        isScanned = scanned;
    }

    public LookupResults getLookupResults() {
        return lookupResults;
    }

    public void setLookupResults(LookupResults lookupResults) {
        this.lookupResults = lookupResults;
    }

    @Override
    public String toString() {
        return "Connection{" +
                "isScanned=" + isScanned +
                ", address='" + address + '\'' +
                ", geoInfo=" + geoInfo +
                ", lookupResults=" + lookupResults +
                '}';
    }
}
