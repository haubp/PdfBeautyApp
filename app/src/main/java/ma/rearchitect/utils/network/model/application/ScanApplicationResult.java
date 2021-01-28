package opswat.com.network.model.application;

import com.google.gson.annotations.SerializedName;

/**
 * Created by LenVo on 7/15/18.
 */

public class ScanApplicationResult {
    @SerializedName("rescan_available")
    private boolean rescanAvailable;

    @SerializedName("scan_all_result_i")
    private int scan_all_result_i;

    @SerializedName("total_avs")
    private int totalAvs;

    @SerializedName("total_detected_avs")
    private int totalDetectedAvs;

    @SerializedName("progress_percentage")
    private int progressPercentage;

    @SerializedName("scan_all_result_a")
    private String scan_all_result_a;

    public boolean isRescanAvailable() {
        return rescanAvailable;
    }

    public void setRescanAvailable(boolean rescanAvailable) {
        this.rescanAvailable = rescanAvailable;
    }

    public int getScan_all_result_i() {
        return scan_all_result_i;
    }

    public void setScan_all_result_i(int scan_all_result_i) {
        this.scan_all_result_i = scan_all_result_i;
    }

    public int getTotalAvs() {
        return totalAvs;
    }

    public void setTotalAvs(int totalAvs) {
        this.totalAvs = totalAvs;
    }

    public int getTotalDetectedAvs() {
        return totalDetectedAvs;
    }

    public void setTotalDetectedAvs(int totalDetectedAvs) {
        this.totalDetectedAvs = totalDetectedAvs;
    }

    public int getProgressPercentage() {
        return progressPercentage;
    }

    public void setProgressPercentage(int progressPercentage) {
        this.progressPercentage = progressPercentage;
    }

    public String getScan_all_result_a() {
        return scan_all_result_a;
    }

    public void setScan_all_result_a(String scan_all_result_a) {
        this.scan_all_result_a = scan_all_result_a;
    }
}
