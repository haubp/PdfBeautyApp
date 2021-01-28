package opswat.com.network.model.request;

import com.google.gson.annotations.SerializedName;

public class FetchPrivacyRequest {
    @SerializedName("reg_code")
    private String regCode;

    @SerializedName("license_key")
    private String licenseKey;

    public String getRegCode() {
        return regCode;
    }

    public void setRegCode(String regCode) {
        this.regCode = regCode;
    }

    public String getLicenseKey() {
        return licenseKey;
    }

    public void setLicenseKey(String licenseKey) {
        this.licenseKey = licenseKey;
    }

    @Override
    public String toString() {
        return "FetchPrivacyRequest{" +
                "regCode='" + regCode + '\'' +
                ", licenseKey='" + licenseKey + '\'' +
                '}';
    }
}
