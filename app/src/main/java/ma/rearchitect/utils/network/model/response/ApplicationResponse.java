package opswat.com.network.model.response;

import com.google.gson.annotations.SerializedName;

import opswat.com.network.model.application.ScanApplicationResult;

/**
 * Created by LenVo on 7/18/18.
 */

public class ApplicationResponse {
    @SerializedName("data_id")
    private String dataId;

    @SerializedName("file_id")
    private String file_id;

    @SerializedName("scan_results")
    private ScanApplicationResult scanRsults;

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public String getFile_id() {
        return file_id;
    }

    public void setFile_id(String file_id) {
        this.file_id = file_id;
    }

    public ScanApplicationResult getScanRsults() {
        return scanRsults;
    }

    public void setScanRsults(ScanApplicationResult scanRsults) {
        this.scanRsults = scanRsults;
    }
}
