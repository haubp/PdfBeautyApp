package ma.rearchitect.data.device.application;

import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.List;

import opswat.com.device.DeviceDefine;
import opswat.com.enums.ApplicationScanningStatus;
import opswat.com.enums.ApplicationStatus;
import opswat.com.network.model.application.Application;
import opswat.com.network.model.application.ScanApplicationResult;

/**
 * Created by LenVo on 7/18/18.
 */

public class ApplicationScanningThread extends Thread{
    private ApplicationDBHelper dbHelper;
    private List<Application> applications;
    private Gson gson = new Gson();
    private final String TAG = ApplicationScanningThread.class.getName();

    public ApplicationScanningThread(ApplicationDBHelper dbHelper, List<Application> applications) {
        this.dbHelper = dbHelper;
        this.applications = applications;
        for (Application application: applications) {
            application.setStatus(ApplicationStatus.INPROGRESS);
            dbHelper.saveApplication(application);
        }
        dbHelper.setApplicationScanStatus(ApplicationScanningStatus.INPROGRESS);
        dbHelper.setLastTimeScanApplication(System.currentTimeMillis());
    }

    @Override
    public void run() {
        super.run();
        if (applications.isEmpty()) {
            dbHelper.setApplicationScanStatus(ApplicationScanningStatus.COMPLETED);
            return;
        }

        for (final Application application: applications) {
            application.setDataId(null);
            application.setTotalAvs(0);
            application.setTotalDetectedAvs(0);
            String hashFile = application.getHash();
            if (application.getHash() == null || application.getHash().isEmpty()) {
                hashFile = ApplicationScanHelper.getHashFile(application.getSourceDir());
                application.setHash(hashFile);
            }

            final String url = DeviceDefine.METADEFENDER_SERVER_FILE_URL + hashFile;
            Log.i(TAG, "Application " + application.getPackageName() + " - Call api: " + url);
            AndroidNetworking.get(url)
                    .addHeaders("apikey", DeviceDefine.METADEFENDER_API_KEY)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            updateScanResult(application, response);
                            Log.i(TAG, "Application " + application.getPackageName() + " - Response OK api: " + url + " Response: " + response.toString());
                        }

                        @Override
                        public void onError(ANError anError) {
                            anError.printStackTrace();
                            updateUnknownStatus(application);
                            Log.i(TAG, "Application " + application.getPackageName() + " - Response Error api: " + url + " anError: " + anError.toString());
                        }
                    });

        }
    }

    private void updateScanResult(Application application, JSONObject response) {
        try {
            if (!response.has("data_id")) {
                updateUnknownStatus(application);
                return;
            }
            String dataId = response.getString("data_id");
            if(dataId.equals("null")) {
                updateUnknownStatus(application);
                return;
            }

            if (!response.has("scan_results")) {
                updateUnknownStatus(application);
                return;
            }

            JSONObject jsonResult = response.getJSONObject("scan_results");
            //Remove scan_details json to speed up the convert Json Object
            jsonResult.remove("scan_details");

            ScanApplicationResult result = gson.fromJson(jsonResult.toString(), ScanApplicationResult.class);
            if (result == null || result.getProgressPercentage() != 100) {
                updateUnknownStatus(application);
                return;
            }

            application.setDataId(dataId);
            application.setTotalAvs(result.getTotalAvs());
            application.setTotalDetectedAvs(result.getTotalDetectedAvs());
            //Ref: https://onlinehelp.opswat.com/mdcloud/Description_on_scan_result_codes.html
            switch (result.getScan_all_result_i()) {
                case 0:
                    application.setStatus(ApplicationStatus.CLEAN);
                    break;
                case 1: //Infected/Known
                case 4: //Failed To Scan
                case 8: //Skipped Infected
                    application.setStatus(ApplicationStatus.INFECTED);
                    break;
                case 2: //Suspicious
                    application.setStatus(ApplicationStatus.SUSPECT);
                    break;
                default:
                    application.setStatus(ApplicationStatus.UNKNOWN);
                    break;
            }
            application.setLatestedTime(System.currentTimeMillis());
            dbHelper.saveApplication(application);
            bindingApplicationScanningStatus();
        }catch (Exception e) {
            e.printStackTrace();
            updateUnknownStatus(application);
        }
    }

    private void updateUnknownStatus(Application application) {
        application.setStatus(ApplicationStatus.UNKNOWN);
        application.setDataId(null);
        application.setTotalAvs(0);
        application.setTotalDetectedAvs(0);
        application.setLatestedTime(System.currentTimeMillis());
        dbHelper.saveApplication(application);
        bindingApplicationScanningStatus();
    }

    private void bindingApplicationScanningStatus() {
        for (Application application: applications) {
            if (application.getStatus() == ApplicationStatus.INPROGRESS) {
                return;
            }
        }
        dbHelper.setApplicationScanStatus(ApplicationScanningStatus.COMPLETED);
    }
}
