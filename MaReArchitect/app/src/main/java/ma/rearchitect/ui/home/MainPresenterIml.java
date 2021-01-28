package opswat.com.flow.home;

import android.content.Context;

import opswat.com.constant.MAContant;
import opswat.com.constant.MaCloudKey;
import opswat.com.data.AccountData;
import opswat.com.data.MACloudData;
import opswat.com.device.DeviceBuilder;
import opswat.com.handler.MACloudHandler;
import opswat.com.logger.LoggerUniversalLink;
import opswat.com.network.helper.MACloudFactory;
import opswat.com.network.helper.MACloudHelper;
import opswat.com.network.model.request.FetchPrivacyRequest;
import opswat.com.network.model.request.RegisterRequest;
import opswat.com.network.model.request.ReportRequest;
import opswat.com.network.model.response.AccountResponse;
import opswat.com.network.model.response.FetchPrivacyResponse;
import opswat.com.network.model.response.MACloudErrorResponse;
import opswat.com.network.model.response.MACloudResponse;
import opswat.com.network.model.response.PolicyCheckResponse;
import opswat.com.network.model.response.RegisterResponse;
import opswat.com.util.NetworkUtils;
import opswat.com.util.StringUtil;

public class


MainPresenterIml implements IMainPresenter {
    private IMainView mainView;
    private Context context;
    private int countRetry = 0;
    private static final int MAX_RETRY_REPORT = 3;

    @Override
    public void onAttach(IMainView mvpView) {
        this.mainView = mvpView;
        this.context = mvpView.getContext();
    }

    @Override
    public void onDetach() {
    }

    @Override
    public void registerOnLoaded() {
        final AccountData accountData = MAApplication.getInstance().getMaCloudData().getAccountData();
        if (!accountData.isRegistered()) {
            return;
        }
        if (!NetworkUtils.isNetworkConnected(context)) {
            return;
        }

        FetchPrivacyRequest fetchPrivacyRequest = new FetchPrivacyRequest();
        fetchPrivacyRequest.setLicenseKey(accountData.getLicenseKey());

        MACloudHelper.fetchPrivacy(accountData.getServerAddress(), accountData, fetchPrivacyRequest, new MACloudHandler() {
            @Override
            public void onFailed(MACloudErrorResponse response) {

            }

            @Override
            public void onSuccess(MACloudResponse response) {
                FetchPrivacyResponse fetchPrivacyResponse = (FetchPrivacyResponse) response;
                MAApplication.getInstance().getMaCloudData().setPrivacy(fetchPrivacyResponse);
                doRegisterOnLoad(accountData.getRegCode(), accountData.getGroupId(), accountData.getServerAddress(), fetchPrivacyResponse);
            }
        });
    }

    private void doRegisterOnLoad(final String regCode, String groupId, String serverName, FetchPrivacyResponse privacy) {
        RegisterRequest request = MACloudFactory.getRegisterRequest(MAApplication.getInstance().getMaCloudData().getAccountData(),
                MAApplication.getInstance().getDeviceBuilder(), privacy);
        String serverURL = MAContant.SERVER_URL;
        if (!StringUtil.isEmpty(serverName)) {
            serverURL = serverName;
            if (!serverName.startsWith("http://") && !serverName.startsWith("https://")) {
                serverURL = "https://" + serverName;
            }

            if (!serverName.endsWith("/")) {
                serverURL = serverURL + "/";
            }
        }
        if (!StringUtil.isEmpty(groupId)) {
            request.setGroupId(groupId);
        }
        final String finalURL = serverURL;
        MACloudHelper.register(serverURL, regCode, request, new MACloudHandler() {
            @Override
            public void onFailed(MACloudErrorResponse response) {
                if (MaCloudKey.isErrorOutOfToken(response.getStatusCode(), response.getError())) {
                        mainView.handleOutOfToken();
                }
            }

            @Override
            public void onSuccess(MACloudResponse response) {
                RegisterResponse registerResponse = (RegisterResponse) response;
                AccountData data = MAApplication.getInstance().getMaCloudData().getAccountData();
                data.setAccessKey(registerResponse.getAccessKey());
                data.setAccessToken(registerResponse.getAccessToken());
                data.setDeviceId(registerResponse.getDeviceId());
                data.setLicenseKey(registerResponse.getLicenseKey());
                data.setRegCode(regCode);
                data.setMoKey(registerResponse.getMoKey());
                data.setServerAddress(finalURL);
                MAApplication.getInstance().getMaCloudData().setAccountData(data);
                doGetAccountInfoOnLoaded();
            }
        });
    }

    private void doGetAccountInfoOnLoaded() {
        AccountData accountData = MAApplication.getInstance().getMaCloudData().getAccountData();
        MACloudHelper.getAccount(accountData, new MACloudHandler() {
            @Override
            public void onFailed(MACloudErrorResponse response) {
            }

            @Override
            public void onSuccess(MACloudResponse response) {
                AccountResponse accountResponse = (AccountResponse) response;
                AccountData data = MAApplication.getInstance().getMaCloudData().getAccountData();
                data.setOwnerEmail(accountResponse.getOwnerEmail());
                data.setAccountName(accountResponse.getAccountName());
                MAApplication.getInstance().getMaCloudData().setAccountData(data);
                mainView.bindingUpdatedAccountData();
            }
        });
    }

    @Override
    public void register(final String regCode, final String groupId, final String serverName) {
        if (!NetworkUtils.isNetworkConnected(context)) {
            mainView.handleRegisterWithNoNetwork();
            return;
        }
        AccountData accountData = MAApplication.getInstance().getMaCloudData().getAccountData();
        FetchPrivacyRequest fetchPrivacyRequest = new FetchPrivacyRequest();
        fetchPrivacyRequest.setRegCode(regCode);
        String serverURL = StringUtil.getServerUrlFromName(serverName);
        MACloudHelper.fetchPrivacy(serverURL, accountData, fetchPrivacyRequest, new MACloudHandler() {
            @Override
            public void onFailed(MACloudErrorResponse response) {
                if (MaCloudKey.isErrorNotFound(response.getStatusCode(), response.getError())) {
                    mainView.showDialogNotFoundRegCode();
                } else {
                    mainView.showDialogErrorGenerateRegister();
                }
            }

            @Override
            public void onSuccess(MACloudResponse response) {
                FetchPrivacyResponse fetchPrivacyResponse = (FetchPrivacyResponse) response;
                MAApplication.getInstance().getMaCloudData().setPrivacy(fetchPrivacyResponse);
                doRegister(regCode, groupId, serverName, fetchPrivacyResponse);
            }
        });

    }

    private void doRegister(final String regCode, final String groupId, String serverName, FetchPrivacyResponse privacy) {
        RegisterRequest request = MACloudFactory.getRegisterRequest(MAApplication.getInstance().getMaCloudData().getAccountData(),
                MAApplication.getInstance().getDeviceBuilder(), privacy);
        String serverURL = MAContant.SERVER_URL;
        if (!StringUtil.isEmpty(serverName)) {
            serverURL = serverName;
            if (!serverName.startsWith("http://") && !serverName.startsWith("https://")) {
                serverURL = "https://" + serverName;
            }

            if (!serverName.endsWith("/")) {
                serverURL = serverURL + "/";
            }
        }
        if (!StringUtil.isEmpty(groupId)) {
            request.setGroupId(groupId);
        }
        final String finalURL = serverURL;
        MACloudHelper.register(serverURL, regCode, request, new MACloudHandler() {
            @Override
            public void onFailed(MACloudErrorResponse response) {
                //Todo: Check status code: CONFLICT, OUT_OF_TOKEN, ... to clean MaCloudData and show dialog
                if (MaCloudKey.isErrorNotFound(response.getStatusCode(), response.getError())) {
                    mainView.showDialogNotFoundRegCode();
                } else if (MaCloudKey.isErrorOutOfToken(response.getStatusCode(), response.getError())) {
                    mainView.showDialogOutOfToken();
                } else {
                    mainView.showDialogErrorGenerateRegister();
                }
            }

            @Override
            public void onSuccess(MACloudResponse response) {
                RegisterResponse registerResponse = (RegisterResponse) response;
                AccountData data = MAApplication.getInstance().getMaCloudData().getAccountData();
                data.setAccessKey(registerResponse.getAccessKey());
                data.setAccessToken(registerResponse.getAccessToken());
                data.setDeviceId(registerResponse.getDeviceId());
                data.setLicenseKey(registerResponse.getLicenseKey());
                data.setRegCode(regCode);
                data.setGroupId(groupId);
                data.setServerAddress(finalURL);
                MAApplication.getInstance().getMaCloudData().setAccountData(data);
                doGetAccountInfo();
            }
        });
    }

    private void doGetAccountInfo() {
        AccountData accountData = MAApplication.getInstance().getMaCloudData().getAccountData();
        MACloudHelper.getAccount(accountData, new MACloudHandler() {
            @Override
            public void onFailed(MACloudErrorResponse response) {
                mainView.bindingRegistered();
            }

            @Override
            public void onSuccess(MACloudResponse response) {
                AccountResponse accountResponse = (AccountResponse) response;
                AccountData data = MAApplication.getInstance().getMaCloudData().getAccountData();
                data.setOwnerEmail(accountResponse.getOwnerEmail());
                data.setAccountName(accountResponse.getAccountName());
                MAApplication.getInstance().getMaCloudData().setAccountData(data);
                mainView.bindingRegistered();
            }
        });
    }


    @Override
    public void report(final boolean isUniversal) {
        final AccountData accountData = MAApplication.getInstance().getMaCloudData().getAccountData();
        if (!accountData.isRegistered()) {
            return;
        }
        if (!NetworkUtils.isNetworkConnected(context)) {
            if (isUniversal) {
                LoggerUniversalLink.writeLog(context, "Failed to send data report - No network connection");
                mainView.showReportUniversalFailed(MaCloudKey.NO_NETWORK_ERROR);
            }
            return;
        }
        countRetry = 0;
        FetchPrivacyRequest fetchPrivacyRequest = new FetchPrivacyRequest();
        fetchPrivacyRequest.setRegCode(accountData.getRegCode());
        MACloudHelper.fetchPrivacy(accountData.getServerAddress(), accountData, fetchPrivacyRequest, new MACloudHandler() {
            @Override
            public void onFailed(MACloudErrorResponse response) {
                sendDataReport(isUniversal);
            }

            @Override
            public void onSuccess(MACloudResponse response) {
                FetchPrivacyResponse privacyResponse = (FetchPrivacyResponse) response;
                MAApplication.getInstance().getMaCloudData().setPrivacy(privacyResponse);
                sendDataReport(isUniversal);
            }
        });
    }

    private void sendDataReport(final boolean isUniversal) {
        if (countRetry >= MAX_RETRY_REPORT) {
            if (isUniversal) {
                LoggerUniversalLink.writeLog(context, "Failed to send data report with " + MAX_RETRY_REPORT + " retry");
                mainView.showReportUniversalOutOfCountTry();
            }
            return;
        }
        MACloudData cloudData = MAApplication.getInstance().getMaCloudData();
        DeviceBuilder deviceBuilder = MAApplication.getInstance().getDeviceBuilder();
        if (cloudData.getPrivacy() == null) {
            LoggerUniversalLink.writeLog(context, "Failed to send data report - can not get the privacy");
            return;
        }
        countRetry++;
        AccountData accountData = cloudData.getAccountData();
        final ReportRequest request = MACloudFactory.getReportRequest(cloudData, deviceBuilder, cloudData.getPrivacy());
        MACloudHelper.report(request, accountData, new MACloudHandler() {
            @Override
            public void onFailed(MACloudErrorResponse response) {
                if(response.getStatusCode() == 401) {
                    sendDataReport(isUniversal);
                    return;
                }
                if (isUniversal) {
                    LoggerUniversalLink.writeLog(context, "Failed to send data report | Error = " +
                            response.getError());
                    mainView.showReportUniversalFailed(response.getError());
                }
            }

            @Override
            public void onSuccess(MACloudResponse response) {
                MACloudData maCloudData = MAApplication.getInstance().getMaCloudData();
                maCloudData.setLastConnected(System.currentTimeMillis());
                if (isUniversal) {
                    LoggerUniversalLink.writeLog(context, "Sent data report successfully");
                    mainView.showReportUniversalSuccessfully();
                }
                doPolicyCheck(context);
            }
        });
    }

    private void doPolicyCheck(Context context) {
        AccountData accountData = MAApplication.getInstance().getMaCloudData().getAccountData();
        if (!accountData.isRegistered()) {
            return;
        }

        if (!NetworkUtils.isNetworkConnected(context)) {
            return;
        }

        MACloudHelper.policyCheck(accountData, new MACloudHandler() {
            @Override
            public void onFailed(MACloudErrorResponse response) {
                if (MaCloudKey.isErrorOutOfToken(response.getStatusCode(), response.getError())) {
                    mainView.showDialogOutOfToken();
                }
            }

            @Override
            public void onSuccess(MACloudResponse response) {
                PolicyCheckResponse policyCheckResponse = (PolicyCheckResponse) response;
                mainView.handlePolicyCheckResponse(policyCheckResponse);
            }
        });
    }
}
