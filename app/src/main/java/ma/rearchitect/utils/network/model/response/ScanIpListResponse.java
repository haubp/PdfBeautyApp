package opswat.com.network.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import opswat.com.network.model.connection.Connection;

/**
 * Created by LenVo on 7/15/18.
 */

public class ScanIpListResponse {
    @SerializedName("data")
    private List<Connection> data;

    public List<Connection> getData() {
        return data;
    }

    public void setData(List<Connection> data) {
        this.data = data;
    }
}
