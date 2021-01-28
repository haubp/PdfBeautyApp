package ma.rearchitect.data.data;

import com.google.gson.annotations.SerializedName;

import opswat.com.constant.MAContant;
import opswat.com.constant.MaCloudKey;
import opswat.com.util.StringUtil;

/**
 * Created by H. Len Vo on 8/23/18.
 */
public class AccountData {
    @SerializedName(MaCloudKey.ACCESS_TOKEN)
    private String accessToken;

    @SerializedName(MaCloudKey.ACCESS_KEY)
    private String accessKey;

    @SerializedName(MaCloudKey.DEVICE_ID)
    private String deviceId;

    @SerializedName(MaCloudKey.LICENSE_KEY)
    private String licenseKey;

    @SerializedName(MaCloudKey.REG_CODE)
    private String regCode;

    @SerializedName(MaCloudKey.OWNER_EMAIL)
    private String ownerEmail;

    @SerializedName(MaCloudKey.ACCOUNT_NAME)
    private String accountName;

    @SerializedName(MaCloudKey.SERVER_ADDRESS)
    private String serverAddress;

    @SerializedName(MaCloudKey.MO_KEY)
    private String moKey;

    @SerializedName(MaCloudKey.GROUP_ID)
    private String groupId;

    @SerializedName(MaCloudKey.LAST_MODIFIED)
    private long lastModified;

    @SerializedName(MaCloudKey.USER_IDENTITY)
    private UserIdentity userIdentity;

    public boolean isRegistered() {
        return (regCode != null || licenseKey != null) && (!MAContant.REG_CODE_MARKETING.equalsIgnoreCase(regCode)
                || !MAContant.EMAIL_MARKETING.equalsIgnoreCase(ownerEmail));
    }

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

    public String getRegCode() {
        return regCode;
    }

    public void setRegCode(String regCode) {
        this.regCode = regCode;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getServerAddress() {
        if (StringUtil.isEmpty(serverAddress)) {
            return MAContant.SERVER_URL;
        }
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public String getMoKey() {
        return moKey;
    }

    public void setMoKey(String moKey) {
        this.moKey = moKey;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    public UserIdentity getUserIdentity() {
        return userIdentity;
    }

    public void setUserIdentity(UserIdentity userIdentity) {
        this.userIdentity = userIdentity;
    }

    public void resetAccountData() {
        serverAddress = null;
        moKey = null;
        groupId = null;
        regCode = null;
        ownerEmail = null;
        accessKey = null;
        accessToken = null;
        licenseKey = null;
        accountName = null;
        userIdentity = null;
    }
}
