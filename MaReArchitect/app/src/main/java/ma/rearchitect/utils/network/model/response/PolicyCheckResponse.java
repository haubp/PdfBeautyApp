package opswat.com.network.model.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by H. Len Vo on 9/6/18.
 */
public class PolicyCheckResponse extends MACloudResponse{
    private String status;

    @SerializedName("total_issue")
    private int totalIssue;

    @SerializedName("total_critical_issue")
    private int totalCriticalIssue;

    @SerializedName("remediation_link")
    private String remediationLink;

    @SerializedName("last_seen")
    private String lastSeen;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getTotalIssue() {
        return totalIssue;
    }

    public void setTotalIssue(int totalIssue) {
        this.totalIssue = totalIssue;
    }

    public int getTotalCriticalIssue() {
        return totalCriticalIssue;
    }

    public void setTotalCriticalIssue(int totalCriticalIssue) {
        this.totalCriticalIssue = totalCriticalIssue;
    }

    public String getRemediationLink() {
        return remediationLink;
    }

    public void setRemediationLink(String remediationLink) {
        this.remediationLink = remediationLink;
    }

    public String getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(String lastSeen) {
        this.lastSeen = lastSeen;
    }

    @Override
    public String toString() {
        return "PolicyCheckResponse{" +
                "status='" + status + '\'' +
                ", totalIssue=" + totalIssue +
                ", totalCriticalIssue=" + totalCriticalIssue +
                ", remediationLink='" + remediationLink + '\'' +
                ", lastSeen='" + lastSeen + '\'' +
                '}';
    }
}
