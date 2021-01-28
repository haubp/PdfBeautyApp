package opswat.com.network.model.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by H. Len Vo on 8/23/18.
 */
public class RegisterResponse extends MACloudResponse {
    @SerializedName("access_key")
    private String accessToken;

    @SerializedName("access_token")
    private String accessKey;

    @SerializedName("device_id")
    private String deviceId;

    @SerializedName("license_key")
    private String licenseKey;

    @SerializedName("mo_key")
    private String moKey;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getLicenseKey() {
        return licenseKey;
    }

    public void setLicenseKey(String licenseKey) {
        this.licenseKey = licenseKey;
    }

    public String getMoKey() {
        return moKey;
    }

    public void setMoKey(String moKey) {
        this.moKey = moKey;
    }

    @Override
    public String toString() {
        return "RegisterResponse{" +
                "accessToken='" + accessToken + '\'' +
                ", accessKey='" + accessKey + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", licenseKey='" + licenseKey + '\'' +
                ", moKey='" + moKey + '\'' +
                '}';
    }
}
