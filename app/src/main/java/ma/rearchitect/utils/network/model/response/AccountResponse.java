package opswat.com.network.model.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by H. Len Vo on 8/23/18.
 */
public class AccountResponse extends MACloudResponse{
    @SerializedName("owner_email")
    private String ownerEmail;

    @SerializedName("account_name")
    private String accountName;

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    @Override
    public String toString() {
        return "AccountResponse{" +
                "ownerEmail='" + ownerEmail + '\'' +
                ", accountName='" + accountName + '\'' +
                '}';
    }
}
