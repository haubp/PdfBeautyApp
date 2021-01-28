package ma.rearchitect;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;

import com.android.installreferrer.api.InstallReferrerClient;
import com.android.installreferrer.api.InstallReferrerStateListener;
import com.android.installreferrer.api.ReferrerDetails;
import com.androidnetworking.AndroidNetworking;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import opswat.com.constant.MAContant;
import opswat.com.constant.MaCloudKey;
import ma.rearchitect.data.data.AccountData;
import ma.rearchitect.data.data.MACloudData;
import ma.rearchitect.data.data.UserIdentity;
import ma.rearchitect.data.device.DeviceBuilder;
import ma.rearchitect.data.device.application.ApplicationDBHelper;
import ma.rearchitect.data.device.application.ApplicationScanning;
import ma.rearchitect.data.device.connection.IpConnectionHandler;
import ma.rearchitect.data.device.connection.ScanIpConnectionThread;
import opswat.com.enums.ApplicationScanningStatus;
import opswat.com.flow.base.BaseActivity;
import opswat.com.handler.MACloudHandler;
import opswat.com.logger.LoggerUniversalLink;
import opswat.com.network.helper.MACloudFactory;
import opswat.com.network.helper.MACloudHelper;
import opswat.com.network.model.connection.Connection;
import opswat.com.network.model.request.FetchConfigRequest;
import opswat.com.network.model.request.FetchPrivacyRequest;
import opswat.com.network.model.request.ImHereRequest;
import opswat.com.network.model.request.ReportRequest;
import opswat.com.network.model.request.ResultApiRequest;
import opswat.com.network.model.response.FetchConfigResponse;
import opswat.com.network.model.response.FetchPrivacyResponse;
import opswat.com.network.model.response.ImHereResponse;
import opswat.com.network.model.response.MACloudErrorResponse;
import opswat.com.network.model.response.MACloudResponse;
import opswat.com.network.model.response.PolicyCheckResponse;
import opswat.com.network.model.response.UserIdentityResponse;
import opswat.com.util.NetworkUtils;
import opswat.com.util.SharedPrefsUtils;
import opswat.com.util.StringUtil;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by LenVo on 7/2/18.
 */

public class MAApplication extends Application {
    private static final String TAG = "MAApplication";
    private MACloudData maCloudData;
    public List<Connection> connections = new ArrayList<>();
    public  List<opswat.com.network.model.application.Application> applications = new ArrayList<>();

    private static MAApplication instance;

    private final Timer timer = new Timer();
    private boolean isInitializedTimer = false;
    private BaseActivity currentActivity = null;
    private final Timer timerConnection = new Timer();
    private final Timer timerApplication = new Timer();
    public boolean isScanningIp = false;
    public boolean isScanningApp = false;
    TimerTask taskReport, taskImHere, taskFetchConfig, taskApplication;

    public static MAApplication getInstance() {
        return instance;
    }

    public DeviceBuilder getDeviceBuilder() {
        return DeviceBuilder.getDeviceBuilder(getApplicationContext());
    }

    public MACloudData getMaCloudData() {
        if (maCloudData == null) {
            maCloudData = new MACloudData(getApplicationContext());
        }

        if (!maCloudData.isInitialized()) {
            maCloudData.loadMaCloudData(getApplicationContext());
        }

        return maCloudData;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/SimplonNorm-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();

        AndroidNetworking.initialize(getApplicationContext(), okHttpClient);

        if (!BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics());
        }
        handleInstallReferrerClient();
//        Fabric.with(this, new Crashlytics());
//        Crashlytics.logException(new Exception("LOG HERE"));

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
    }

    private void handleInstallReferrerClient() {
        boolean firstRun = SharedPrefsUtils.getBooleanPreference(this, MAContant.FIRST_RUN, true);
        if (!firstRun) {
            return;
        }
        SharedPrefsUtils.setBooleanPreference(this, MAContant.FIRST_RUN, false);
        final InstallReferrerClient referrerClient;
        final Context context = this;
        referrerClient = InstallReferrerClient.newBuilder(this).build();
        referrerClient.startConnection(new InstallReferrerStateListener() {
            @Override
            public void onInstallReferrerSetupFinished(int responseCode) {
                switch (responseCode) {
                    case InstallReferrerClient.InstallReferrerResponse.OK:
                        // Connection established.
                        ReferrerDetails response = null;
                        try {
                            response = referrerClient.getInstallReferrer();
                        } catch (Exception e){
                        }
                        if(response == null)
                            return;
                        String rawReferrer = response.getInstallReferrer();
                        // for utm terms
                        try {
                            String referrer = URLDecoder.decode(rawReferrer, "UTF-8");
                            if(referrer == null || referrer.contains("utm_source"))
                                return;
                            // Persist the referrer string.
                            String[] refArr = referrer.split("_");
                            SharedPrefsUtils.setStringPreference(context, MAContant.REG_CODE_KEY, refArr[0]);

                            String group_id = "";
                            if (refArr.length == 2)
                                group_id = refArr[1];
                            SharedPrefsUtils.setStringPreference(context, MAContant.GROUP_ID_KEY, group_id);
                        } catch (Exception e) {
                        }
                        break;
                    case InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED:
                        // API not available on the current Play Store app.
                        break;
                    case InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE:
                        // Connection couldn't be established.
                        break;
                    default:
                        //Response Code not found
                }
                referrerClient.endConnection();
            }
            @Override
            public void onInstallReferrerServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                referrerClient.endConnection();
            }
        });
    }

    public synchronized void updateCurrentActivity(BaseActivity activity) {
        currentActivity = activity;
    }

    public synchronized void startConnectionTimer() {
        isScanningIp = true;
        timerConnection.purge();
        final Handler handlerConnection = new Handler();
        TimerTask taskConnection = new TimerTask() {
            @Override
            public void run() {
                handlerConnection.post(new Runnable() {
                    public void run() {
                        try {
                            doScanConnection();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        };
        timerConnection.schedule(taskConnection, 0, MAContant.INTERVAL_SCAN_CONNECTION);
    }

    public synchronized void stopApplicationTimer() {
        if(taskApplication != null)
            taskApplication.cancel();
        timerApplication.purge();
    }

    public synchronized void startApplicationTimer(List<opswat.com.network.model.application.Application> applications) {
        isScanningApp = true;
        final ApplicationScanning applicationScanning = getDeviceBuilder().getApplicationScanning();
        applicationScanning.startScanApplication(applications);

        final Handler handlerApplication = new Handler();
        taskApplication = new TimerTask() {
            @Override
            public void run() {
                handlerApplication.post(new Runnable() {
                    public void run() {
                        try {
                            doScanApplication();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        };
        timerApplication.schedule(taskApplication, 0, MAContant.INTERVAL_SCAN_APPLICATION);
    }

    private void doScanApplication() {
        final ApplicationScanning applicationScanning = getDeviceBuilder().getApplicationScanning();
        applications = applicationScanning.getListApplication();
        if (currentActivity != null) {
            currentActivity.handleAppScanning();
        }
        ApplicationDBHelper dbHelper = applicationScanning.getDbHelper();
        if (dbHelper.getApplicationScanningStatus() == ApplicationScanningStatus.COMPLETED) {
            stopApplicationTimer();
        }
    }

    private void doScanConnection() {
        DeviceBuilder deviceBuilder = MAApplication.getInstance().getDeviceBuilder();
        final ScanIpConnectionThread scanIpConnectionThread = deviceBuilder.getScanIpConnectionThread();
        scanIpConnectionThread.scanIpConnections(new IpConnectionHandler() {
            @Override
            public void onResult() {
                connections = scanIpConnectionThread.getConnections();
                if (currentActivity != null) {
                    currentActivity.handleIpConnection();
                }
            }
        });
        connections = scanIpConnectionThread.getConnections();
    }

    public synchronized void startMaCloudTimer() {
        if (isInitializedTimer) {
            return;
        }
        stopMaCloudTimer();
        isInitializedTimer = true;

        final Handler handlerReport = new Handler(), handlerImhere = new Handler(),
                handlerFetchConfig = new Handler();

        taskReport = new TimerTask() {
            @Override
            public void run() {
                handlerReport.post(new Runnable() {
                    public void run() {
                        try {
                            doReport();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        };

        taskImHere = new TimerTask() {
            @Override
            public void run() {
                handlerImhere.post(new Runnable() {
                    public void run() {
                        try {
                            doPing();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        };

        taskFetchConfig = new TimerTask() {
            @Override
            public void run() {
                handlerFetchConfig.post(new Runnable() {
                    public void run() {
                        try {
                            doFetchConfig();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        };
        timer.schedule(taskImHere, 0, MAContant.INTERVAL_IM_HERE);
        timer.schedule(taskReport, 0, MAContant.INTERVAL_REPORT);
        timer.schedule(taskFetchConfig, 0, MAContant.INTERVAL_FETCH_CONFIG);
    }

    public synchronized void stopMaCloudTimer() {
        if (taskImHere != null)
            taskImHere.cancel();
        if (taskReport != null)
            taskReport.cancel();
        if (taskFetchConfig != null)
            taskFetchConfig.cancel();
        timer.purge();
        isInitializedTimer = false;
    }

    private void doReport() {
        Log.i(TAG, "doReport");
        final AccountData accountData = MAApplication.getInstance().maCloudData.getAccountData();
        if (!accountData.isRegistered()) {
            return;
        }
        if (!NetworkUtils.isNetworkConnected(getApplicationContext())) {
            return;
        }

        FetchPrivacyRequest fetchPrivacyRequest = new FetchPrivacyRequest();
        fetchPrivacyRequest.setRegCode(accountData.getRegCode());
        MACloudHelper.fetchPrivacy(accountData.getServerAddress(), accountData, fetchPrivacyRequest, new MACloudHandler() {
            @Override
            public void onFailed(MACloudErrorResponse response) {
                sendDataReport();
            }

            @Override
            public void onSuccess(MACloudResponse response) {
                FetchPrivacyResponse privacyResponse = (FetchPrivacyResponse) response;
                MAApplication.getInstance().maCloudData.setPrivacy(privacyResponse);
                sendDataReport();
            }
        });
    }

    private void sendDataReport() {
        MACloudData cloudData = MAApplication.getInstance().maCloudData;
        DeviceBuilder deviceBuilder = MAApplication.getInstance().getDeviceBuilder();
        if (cloudData.getPrivacy() == null) {
            return;
        }
        AccountData accountData = cloudData.getAccountData();
        ReportRequest request = MACloudFactory.getReportRequest(cloudData, deviceBuilder, cloudData.getPrivacy());
        MACloudHelper.report(request, accountData, new MACloudHandler() {
            @Override
            public void onFailed(MACloudErrorResponse response) {
                if (MaCloudKey.isErrorOutOfToken(response.getStatusCode(), response.getError())
                        && currentActivity != null) {
                    currentActivity.handleOutOfToken();
                }
            }

            @Override
            public void onSuccess(MACloudResponse response) {
                MACloudData maCloudData = MAApplication.getInstance().maCloudData;
                maCloudData.setLastConnected(System.currentTimeMillis());
                doPolicyCheck();
            }
        });
    }

    private void doFetchConfig() {
        Log.i(TAG, "doFetchConfig");
        AccountData accountData = MAApplication.getInstance().maCloudData.getAccountData();
        if (!accountData.isRegistered()) {
            return;
        }
        if (!NetworkUtils.isNetworkConnected(getApplicationContext())) {
            return;
        }

        FetchConfigRequest request = new FetchConfigRequest();
        List<String> includes = new ArrayList<>();
        includes.add("mac_required");
        includes.add("user_identity");
        includes.add("privacy");
        includes.add("notification_install_app");
        request.setInclude(includes);

        MACloudHelper.fetchConfig(request, accountData, new MACloudHandler() {
            @Override
            public void onFailed(MACloudErrorResponse response) {
            }

            @Override
            public void onSuccess(MACloudResponse response) {
                FetchConfigResponse fetchConfigResponse = (FetchConfigResponse) response;
                MACloudData maCloudData = getMaCloudData();
                maCloudData.setPrivacy(fetchConfigResponse.getPrivacy());
                currentActivity.bindingFetchConfig(fetchConfigResponse.getNotifyInstalledApp());
                handleUserIdentityConfig(fetchConfigResponse);
            }
        });
    }

    private void handleUserIdentityConfig(FetchConfigResponse fetchConfigResponse) {
        UserIdentityResponse userIdentityResponse = fetchConfigResponse.getUserIdentity();
        UserIdentity userIdentity = new UserIdentity(userIdentityResponse.getEnable(),
                userIdentityResponse.getRegex(), userIdentityResponse.getMsg());
        AccountData accountData = MAApplication.getInstance().maCloudData.getAccountData();
        UserIdentity currentUserIdentity = accountData.getUserIdentity();
        boolean shouldShowDialog = false;
        do {
            if (userIdentity.getEnable() == 0) {
                if (currentUserIdentity != null) {
                    currentUserIdentity.setUserInput(null);
                }
                break;
            }

            //Enable User Identity
            if (currentUserIdentity == null || StringUtil.isEmpty(currentUserIdentity.getUserInput())) {
                shouldShowDialog = true;
                break;
            }

            if (currentUserIdentity.isReIdentity()) {
                shouldShowDialog = true;
                break;
            }

            if (!currentUserIdentity.getRegex().equals(userIdentity.getRegex())) {
                shouldShowDialog = true;
                break;
            }

        } while (false);

        if (currentUserIdentity == null) {
            currentUserIdentity = userIdentity;
        } else {
            currentUserIdentity.setEnable(userIdentity.getEnable());
            currentUserIdentity.setMsg(userIdentity.getMsg());
            currentUserIdentity.setRegex(userIdentity.getRegex());
        }
        MAApplication.getInstance().maCloudData.updateUserIdentity(currentUserIdentity);
        if (shouldShowDialog && currentActivity != null) {
            currentActivity.handleUserIdentity();
        }
    }

    private void doPolicyCheck() {
        AccountData accountData = MAApplication.getInstance().maCloudData.getAccountData();
        Log.i(TAG, "doPolicyCheck");
        if (!accountData.isRegistered()) {
            return;
        }

        if (!NetworkUtils.isNetworkConnected(getApplicationContext())) {
            return;
        }

        MACloudHelper.policyCheck(accountData, new MACloudHandler() {
            @Override
            public void onFailed(MACloudErrorResponse response) {
                if (MaCloudKey.isErrorOutOfToken(response.getStatusCode(), response.getError())) {
                    if (currentActivity != null) {
                        currentActivity.handleOutOfToken();
                    }
                }
            }

            @Override
            public void onSuccess(MACloudResponse response) {
                PolicyCheckResponse policyCheckResponse = (PolicyCheckResponse) response;
                if (currentActivity != null) {
                    currentActivity.handlePolicyCheck(policyCheckResponse);
                }
            }
        });
    }

    private void doPing() {
        Log.i(TAG, "doPing");
        AccountData accountData = MAApplication.getInstance().maCloudData.getAccountData();
        if (!accountData.isRegistered()) {
            return;
        }

        if (!NetworkUtils.isNetworkConnected(getApplicationContext())) {
            return;
        }

        final ImHereRequest request = new ImHereRequest();
        request.setHwid(accountData.getDeviceId());
        request.setOpt(0);

        MACloudHelper.ping(request, accountData, new MACloudHandler() {
            @Override
            public void onFailed(MACloudErrorResponse response) {
            }

            @Override
            public void onSuccess(MACloudResponse response) {
                ImHereResponse imHereResponse = (ImHereResponse) response;
                if (currentActivity == null) {
                    return;
                }
                MACloudData maCloudData = MAApplication.getInstance().maCloudData;
                maCloudData.setLastConnected(System.currentTimeMillis());

                switch (imHereResponse.getCode()) {
                    case MAContant.COMMAND_CODE_DELETE:
                        currentActivity.handleDeleteCommand();
                        break;
                    case MAContant.COMMAND_CODE_REIDENTITY:
                        handleUserIdentityCommand();
                        break;
                }
            }
        });
    }

    private void handleUserIdentityCommand() {
        AccountData accountData = MAApplication.getInstance().maCloudData.getAccountData();
        if (!accountData.isRegistered()) {
            return;
        }

        UserIdentity userIdentity = accountData.getUserIdentity();
        if (userIdentity != null) {
            userIdentity.setReIdentity(true);
            MAApplication.getInstance().maCloudData.updateUserIdentity(userIdentity);
        }

        ResultApiRequest request = new ResultApiRequest();
        request.setHwid(accountData.getDeviceId());
        request.setMethod(MAContant.COMMAND_CODE_REIDENTITY + "");
        request.setResult("1");
        MACloudHelper.result(request, accountData, new MACloudHandler() {
            @Override
            public void onFailed(MACloudErrorResponse response) {
            }

            @Override
            public void onSuccess(MACloudResponse response) {
            }
        });
    }
}
