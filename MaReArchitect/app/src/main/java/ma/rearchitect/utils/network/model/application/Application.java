package opswat.com.network.model.application;

import com.google.gson.annotations.SerializedName;

import opswat.com.enums.ApplicationStatus;

/**
 * Created by LenVo on 7/15/18.
 */

public class Application {
    private String appName;

    private String packageName;

    private String version;

    private String sourceDir;

    private ApplicationStatus status;

    private String hash;

    private String dataId;

    private int totalAvs;

    private int totalDetectedAvs;

    private long latestedTime;


    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getSourceDir() {
        return sourceDir;
    }

    public void setSourceDir(String sourceDir) {
        this.sourceDir = sourceDir;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public String getHash() {
        return hash;
    }

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public void setHash(String hash) {
        this.hash = hash;
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

    public long getLatestedTime() {
        return latestedTime;
    }

    public void setLatestedTime(long latestedTime) {
        this.latestedTime = latestedTime;
    }
}
