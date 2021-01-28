package ma.rearchitect.data.device.connection;

import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.ParsedRequestListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ma.rearchitect.MAApplication;
import ma.rearchitect.data.data.MACloudData;
import ma.rearchitect.data.device.DeviceDefine;
import opswat.com.network.model.connection.Connection;
import opswat.com.network.model.request.ScanIpListRequest;
import opswat.com.network.model.response.ScanIpListResponse;
import opswat.com.util.NetworkUtils;

/**
 * Created by LenVo on 7/16/18.
 */

public class ScanIpConnectionThread {
    private HashMap<String, Connection> mapResult = new HashMap<>();

    public void scanIpConnections(final IpConnectionHandler handler) {
        if (!NetworkUtils.isNetworkConnected(MAApplication.getInstance().getApplicationContext())) {
            return;
        }

        List<String> ipsList = IpConnectionHelper.getActiveIps();
        JSONObject jsonObject = new JSONObject();
        JSONArray ipsArrayJson = new JSONArray();
        for(String ipAddress : ipsList) {
            if (mapResult.containsKey(ipAddress)) {
                continue;
            }
            ipsArrayJson.put(ipAddress);
        }
        try {
            jsonObject.put("address", ipsArrayJson);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (ipsArrayJson.length() == 0) {
            return;
        }

        ScanIpListRequest scanIpListRequest = new ScanIpListRequest();
        scanIpListRequest.setAddress(ipsList);

        AndroidNetworking.post(DeviceDefine.METADEFENDER_SERVER_IP_URL)
                .addHeaders("apikey", DeviceDefine.METADEFENDER_API_KEY)
                .addJSONObjectBody(jsonObject)
                .setContentType("application/json; charset=utf-8")
                .build()
                .getAsObject(ScanIpListResponse.class, new ParsedRequestListener<ScanIpListResponse>() {
                    @Override
                    public void onResponse(ScanIpListResponse response) {
                        List<Connection> data = response.getData();
                        if (data == null) {
                            return;
                        }

                        MACloudData maCloudData = MAApplication.getInstance().getMaCloudData();
                        maCloudData.setLastScanningIP(System.currentTimeMillis());

                        for (Connection connection: data) {
                            connection.setScanned(true);
                            mapResult.put(connection.getAddress(), connection);
                        }
                        if (handler != null) {
                            handler.onResult();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.i("Error", anError.getErrorBody());
                    }
                });
    }

    public List<Connection> getConnections() {
        List<String> ipsList = IpConnectionHelper.getActiveIps();
        List<Connection> connections = new ArrayList<>();
        for(String ipAddress : ipsList) {
            Connection connection = new Connection();
            connection.setAddress(ipAddress);
            if (mapResult.containsKey(ipAddress)) {
                connection = mapResult.get(ipAddress);
            }
            connections.add(connection);
        }
        return connections;
    }
}
