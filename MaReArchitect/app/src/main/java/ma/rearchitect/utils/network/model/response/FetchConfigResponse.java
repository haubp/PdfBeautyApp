package opswat.com.network.model.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by H. Len Vo on 9/6/18.
 */
public class FetchConfigResponse extends MACloudResponse {
    @SerializedName("user_identity")
    private UserIdentityResponse userIdentity;

    @SerializedName("privacy")
    private FetchPrivacyResponse privacy;

    @SerializedName("notification_install_app")
    private int notifyInstalledApp;

    public int getNotifyInstalledApp() {
        return notifyInstalledApp;
    }

    public void setNotifyInstalledApp(int notifyInstalledApp) {
        this.notifyInstalledApp = notifyInstalledApp;
    }
    
    public UserIdentityResponse getUserIdentity() {
        return userIdentity;
    }

    public void setUserIdentity(UserIdentityResponse userIdentity) {
        this.userIdentity = userIdentity;
    }

    public FetchPrivacyResponse getPrivacy() {
        return privacy;
    }

    public void setPrivacy(FetchPrivacyResponse privacy) {
        this.privacy = privacy;
    }

    @Override
    public String toString() {
        return "FetchConfigResponse{" +
                "notifyInstalledApp=" + notifyInstalledApp +
                ", userIdentity=" + userIdentity +
                ", privacy=" + privacy +
                '}';
    }
}
