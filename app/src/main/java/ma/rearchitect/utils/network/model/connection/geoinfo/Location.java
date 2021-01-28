package opswat.com.network.model.connection.geoinfo;

import com.google.gson.annotations.SerializedName;

/**
 * Created by LenVo on 7/16/18.
 */

public class Location {
    @SerializedName("latitude")
    private Float latitude;

    @SerializedName("longitude")
    private Float longitude;

    public Float getLatitude() {
        return latitude;
    }

    public void setLatitude(Float latitude) {
        this.latitude = latitude;
    }

    public Float getLongitude() {
        return longitude;
    }

    public void setLongitude(Float longitude) {
        this.longitude = longitude;
    }
}
