package opswat.com.network.helper;

import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.OkHttpResponseListener;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import okhttp3.Response;
import opswat.com.constant.MAContant;
import opswat.com.data.AccountData;
import opswat.com.handler.MACloudHandler;
import opswat.com.network.model.request.FetchConfigRequest;
import opswat.com.network.model.request.FetchPrivacyRequest;
import opswat.com.network.model.request.ImHereRequest;
import opswat.com.network.model.request.RegisterRequest;
import opswat.com.network.model.request.ReportRequest;
import opswat.com.network.model.request.ResultApiRequest;
import opswat.com.network.model.response.AccountResponse;
import opswat.com.network.model.response.FetchConfigResponse;
import opswat.com.network.model.response.FetchPrivacyResponse;
import opswat.com.network.model.response.ImHereResponse;
import opswat.com.network.model.response.MACloudErrorResponse;
import opswat.com.network.model.response.MACloudResponse;
import opswat.com.network.model.response.PolicyCheckResponse;
import opswat.com.network.model.response.RegisterResponse;
import opswat.com.network.model.response.ReportResponse;
import opswat.com.network.model.response.ResultApiResponse;
import opswat.com.util.StringUtil;

public class MACloudHelper {
    private static String TAG = MACloudHelper.class.getSimpleName();
    public static void fetchPrivacy(String url, AccountData data, final FetchPrivacyRequest request, final MACloudHandler handler) {
        String urlRequest = url + MAContant.API_FETCH_PRIVACY;
        Log.i(TAG, "fetchPrivacy - " + urlRequest + " Data: " + request.toString());
        AndroidNetworking.post(urlRequest)
                .addApplicationJsonBody(request)
                .build()
                .getAsObject(FetchPrivacyResponse.class, new ParsedRequestListener<FetchPrivacyResponse>() {
                    @Override
                    public void onResponse(FetchPrivacyResponse response) {
                        handler.onSuccess(response);
                        Log.i(TAG, "fetchPrivacy - onSuccess " + response.toString());
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.i(TAG, "fetchPrivacy - anError " + anError.toString());
                        MACloudErrorResponse response = new MACloudErrorResponse(anError.getErrorCode());
                        if (anError.getErrorBody() != null) {
                            try {
                                JsonObject bodyJson = (new JsonParser()).parse(anError.getErrorBody()).getAsJsonObject();
                                if (bodyJson.has("error")) {
                                    response.setError(bodyJson.get("error").getAsString());
                                }
                                if (bodyJson.has("desc")) {
                                    response.setDescription(bodyJson.get("desc").getAsString());
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        handler.onFailed(response);
                    }
                });
    }

    public static void register(final String serverName, String regCode, RegisterRequest request, final MACloudHandler handler) {
        String urlRequest = serverName + MAContant.API_REGISTER;
        Log.i(TAG, "register - " + urlRequest + " - Data: " + request.toString());
        AndroidNetworking.post(urlRequest)
                .addHeaders("Gears-Auth", regCode)
                .addApplicationJsonBody(request)
                .build()
                .getAsObject(RegisterResponse.class, new ParsedRequestListener<RegisterResponse>() {
                    @Override
                    public void onResponse(RegisterResponse response) {
                        Log.i(TAG, "register - onSuccess " + response.toString());
                        handler.onSuccess(response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.i(TAG, "register - anError " + anError.toString());
                        MACloudErrorResponse response = new MACloudErrorResponse(anError.getErrorCode());
                        if (anError.getErrorBody() != null) {
                            try {
                                JsonObject bodyJson = (new JsonParser()).parse(anError.getErrorBody()).getAsJsonObject();
                                if (bodyJson.has("errors")) {
                                    JsonArray errorsArr = bodyJson.getAsJsonArray("errors");
                                    if (errorsArr != null && errorsArr.size() > 0) {
                                        JsonObject errorJson = errorsArr.get(0).getAsJsonObject();
                                        if (errorJson.has("error_type")) {
                                            response.setError(errorJson.get("error_type").getAsString());
                                        }
                                        if (errorJson.has("details")) {
                                            response.setDescription(errorJson.get("details").toString());
                                        }
                                    }
                                }
                                if(bodyJson.has("access_key") && (bodyJson.has("access_token"))) {
                                    final AccountData accountData = MAApplication.getInstance().getMaCloudData().getAccountData();
                                    accountData.setAccessKey(bodyJson.get("access_key").getAsString());
                                    accountData.setAccessToken(bodyJson.get("access_token").getAsString());
                                    MAApplication.getInstance().getMaCloudData().setAccountData(accountData);

                                    //Send api result when the device was un registered
                                    if (!accountData.isRegistered()) {
                                        accountData.setServerAddress(serverName);
                                        ResultApiRequest request = new ResultApiRequest();
                                        request.setHwid(accountData.getDeviceId());
                                        request.setMethod(MAContant.COMMAND_CODE_DELETE + "");
                                        request.setResult("1");
                                        result(request, accountData, new MACloudHandler() {
                                            @Override
                                            public void onFailed(MACloudErrorResponse response) {
                                            }

                                            @Override
                                            public void onSuccess(MACloudResponse response) {
                                            }
                                        });
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        handler.onFailed(response);
                    }
                });
    }

    public static void getAccount(AccountData data, final MACloudHandler handler) {
        if (!data.isRegistered()) {
            return;
        }
        String urlRequest = data.getServerAddress() + MAContant.API_GET_ACCOUNT_INFO
                + data.getLicenseKey();
        Log.i(TAG, "getAccount - " + urlRequest);
        AndroidNetworking.get(urlRequest)
                .addHeaders("Gears-Access-Key", data.getAccessKey())
                .addHeaders("Gears-Access-Token", data.getAccessToken())
                .addHeaders("Content-Type", "application/json")
                .build()
                .getAsObject(AccountResponse.class, new ParsedRequestListener<AccountResponse>() {
                    @Override
                    public void onResponse(AccountResponse response) {
                        Log.i(TAG, "getAccount - onSuccess " + response.toString());
                        handler.onSuccess(response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.i(TAG, "getAccount - anError " + anError.toString());
                        MACloudErrorResponse response = new MACloudErrorResponse(anError.getErrorCode());
                        if (anError.getErrorBody() != null) {
                            try {
                                JsonObject bodyJson = (new JsonParser()).parse(anError.getErrorBody()).getAsJsonObject();
                                if (bodyJson.has("errors")) {
                                    JsonArray errorsArr = bodyJson.getAsJsonArray("errors");
                                    if (errorsArr != null && errorsArr.size() > 0) {
                                        JsonObject errorJson = errorsArr.get(0).getAsJsonObject();
                                        if (errorJson.has("error_type")) {
                                            response.setError(errorJson.get("error_type").getAsString());
                                        }
                                        if (errorJson.has("details")) {
                                            response.setDescription(errorJson.get("details").toString());
                                        }
                                    }
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        handler.onFailed(response);
                    }
                });
    }

    public static void deleteDevice(AccountData data, final MACloudHandler handler) {
        if (!data.isRegistered()) {
            return;
        }
        String urlRequest = (data.getServerAddress() + MAContant.API_DELETE_DEVICE).
                replace(":license_key", data.getLicenseKey()).
                replace(":device_id", data.getDeviceId());
        Log.i(TAG, "deleteDevice - " + urlRequest);
        AndroidNetworking.delete(urlRequest)
                .addHeaders("Gears-Access-Key", data.getAccessKey())
                .addHeaders("Gears-Access-Token", data.getAccessToken())
                .addHeaders("Content-Type", "application/json")
                .build()
                .getAsOkHttpResponse(new OkHttpResponseListener() {
                    @Override
                    public void onResponse(Response response) {
                        Log.i(TAG, "deleteDevice - onSuccess " + response.toString());
                        handler.onSuccess(new MACloudResponse());
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.i(TAG, "deleteDevice - anError " + anError.toString());
                        MACloudErrorResponse response = new MACloudErrorResponse(anError.getErrorCode());
                        if (anError.getErrorBody() != null) {
                            try {
                                JsonObject bodyJson = (new JsonParser()).parse(anError.getErrorBody()).getAsJsonObject();
                                if (bodyJson.has("errors")) {
                                    JsonArray errorsArr = bodyJson.getAsJsonArray("errors");
                                    if (errorsArr != null && errorsArr.size() > 0) {
                                        JsonObject errorJson = errorsArr.get(0).getAsJsonObject();
                                        if (errorJson.has("error_type")) {
                                            response.setError(errorJson.get("error_type").getAsString());
                                        }
                                        if (errorJson.has("details")) {
                                            response.setDescription(errorJson.get("details").toString());
                                        }
                                    }
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        handler.onFailed(response);
                    }
                });
    }

    public static void policyCheck(AccountData data, final MACloudHandler handler) {
        if (!data.isRegistered()) {
            return;
        }
        String urlRequest = (data.getServerAddress() + MAContant.API_POLICY_CHECK).
                replace(":license_key", data.getLicenseKey()).
                replace(":device_id", data.getDeviceId());
        Log.i(TAG, "policyCheck - " + urlRequest);
        AndroidNetworking.get(urlRequest)
                .addHeaders("Gears-Access-Key", data.getAccessKey())
                .addHeaders("Gears-Access-Token", data.getAccessToken())
                .addHeaders("Content-Type", "application/json")
                .build()
                .getAsObject(PolicyCheckResponse.class, new ParsedRequestListener<PolicyCheckResponse>() {
                    @Override
                    public void onResponse(PolicyCheckResponse response) {
                        Log.i(TAG, "policyCheck - onSuccess " + response.toString());
                        handler.onSuccess(response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.i(TAG, "policyCheck - anError " + anError.toString());
                        MACloudErrorResponse response = new MACloudErrorResponse(anError.getErrorCode());
                        if (anError.getErrorBody() != null) {
                            try {
                                JsonObject bodyJson = (new JsonParser()).parse(anError.getErrorBody()).getAsJsonObject();
                                if (bodyJson.has("errors")) {
                                    JsonArray errorsArr = bodyJson.getAsJsonArray("errors");
                                    if (errorsArr != null && errorsArr.size() > 0) {
                                        JsonObject errorJson = errorsArr.get(0).getAsJsonObject();
                                        if (errorJson.has("error_type")) {
                                            response.setError(errorJson.get("error_type").getAsString());
                                        }
                                        if (errorJson.has("details")) {
                                            response.setDescription(errorJson.get("details").toString());
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        handler.onFailed(response);
                    }
                });
    }

    public static void fetchConfig(FetchConfigRequest request, AccountData data, final MACloudHandler handler) {
        if (!data.isRegistered()) {
            return;
        }
        String urlRequest = (data.getServerAddress() + MAContant.API_FETCH_CONFIG).
                replace(":license_key", data.getLicenseKey()).
                replace(":device_id", data.getDeviceId());
        Log.i(TAG, "fetchConfig - " + urlRequest + " - Data: " + request.toString());
        AndroidNetworking.post(urlRequest)
                .addHeaders("Gears-Access-Key", data.getAccessKey())
                .addHeaders("Gears-Access-Token", data.getAccessToken())
                .addHeaders("Content-Type", "application/json")
                .addApplicationJsonBody(request)
                .build()
                .getAsObject(FetchConfigResponse.class, new ParsedRequestListener<FetchConfigResponse>() {
                    @Override
                    public void onResponse(FetchConfigResponse response) {
                        Log.i(TAG, "fetchConfig - onSuccess " + response.toString());
                        handler.onSuccess(response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.i(TAG, "fetchConfig - anError " + anError.toString());
                        MACloudErrorResponse response = new MACloudErrorResponse(anError.getErrorCode());
                        if (anError.getErrorBody() != null) {
                            try {
                                JsonObject bodyJson = (new JsonParser()).parse(anError.getErrorBody()).getAsJsonObject();
                                if (bodyJson.has("errors")) {
                                    JsonArray errorsArr = bodyJson.getAsJsonArray("errors");
                                    if (errorsArr != null && errorsArr.size() > 0) {
                                        JsonObject errorJson = errorsArr.get(0).getAsJsonObject();
                                        if (errorJson.has("error_type")) {
                                            response.setError(errorJson.get("error_type").getAsString());
                                        }
                                        if (errorJson.has("details")) {
                                            response.setDescription(errorJson.get("details").toString());
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        handler.onFailed(response);
                    }
                });
    }

    public static void report(ReportRequest request, AccountData data, final MACloudHandler handler) {
        if (!data.isRegistered()) {
            return;
        }
        String urlRequest = (data.getServerAddress() + MAContant.API_REPORT).
                replace(":license_key", data.getLicenseKey()).
                replace(":device_id", data.getDeviceId());
        Log.i(TAG, "report - " + urlRequest + " - Data: " + request.toString());
        AndroidNetworking.put(urlRequest)
                .addHeaders("Gears-Access-Key", data.getAccessKey())
                .addHeaders("Gears-Access-Token", data.getAccessToken())
                .addHeaders("Content-Type", "application/json")
                .addApplicationJsonBody(request)
                .build()
                .getAsOkHttpResponse(new OkHttpResponseListener() {
                    @Override
                    public void onResponse(Response response) {
                        Log.i(TAG, "report - onSuccess " + response.toString());
                        if(response.isSuccessful()) {
                            handler.onSuccess(new ReportResponse());
                            return;
                        }
                        MACloudErrorResponse responseError = new MACloudErrorResponse(response.code());
                        if (response.message() != null) {
                            responseError.setError(response.message());
                        }
                        handler.onFailed(responseError);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.i(TAG, "report - anError " + anError.toString());
                        MACloudErrorResponse response = new MACloudErrorResponse(anError.getErrorCode());
                        if (anError.getErrorBody() != null) {
                            try {
                                JsonObject bodyJson = (new JsonParser()).parse(anError.getErrorBody()).getAsJsonObject();
                                if (bodyJson.has("errors")) {
                                    JsonArray errorsArr = bodyJson.getAsJsonArray("errors");
                                    if (errorsArr != null && errorsArr.size() > 0) {
                                        JsonObject errorJson = errorsArr.get(0).getAsJsonObject();
                                        if (errorJson.has("error_type")) {
                                            response.setError(errorJson.get("error_type").getAsString());
                                        }
                                        if (errorJson.has("details")) {
                                            response.setDescription(errorJson.get("details").toString());
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        handler.onFailed(response);
                    }
                });
    }

    public static void ping(ImHereRequest request, AccountData data, final MACloudHandler handler) {
        if (!data.isRegistered()) {
            return;
        }
        String urlRequest = (data.getServerAddress() + MAContant.API_IM_HERE);
        Log.i(TAG, "ping - " + urlRequest + " - Data: " + request.toString());
        AndroidNetworking.post(urlRequest)
                .addHeaders("Gears-Access-Key", data.getAccessKey())
                .addHeaders("Gears-Access-Token", data.getAccessToken())
                .addHeaders("Content-Type", "application/json")
                .addApplicationJsonBody(request)
                .build()
                .getAsObject(ImHereResponse.class, new ParsedRequestListener<ImHereResponse>() {
                    @Override
                    public void onResponse(ImHereResponse response) {
                        Log.i(TAG, "ping - onSuccess " + response.toString());
                        handler.onSuccess(response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.i(TAG, "ping - anError " + anError.toString());
                        MACloudErrorResponse response = new MACloudErrorResponse(anError.getErrorCode());
                        if (anError.getErrorBody() != null) {
                            try {
                                JsonObject bodyJson = (new JsonParser()).parse(anError.getErrorBody()).getAsJsonObject();
                                if (bodyJson.has("code")) {
                                    response.setError(bodyJson.get("code").getAsString());
                                }
                                if (bodyJson.has("msg")) {
                                    response.setDescription(bodyJson.get("msg").getAsString());
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        handler.onFailed(response);
                    }
                });
    }

    public static void result(ResultApiRequest request, AccountData data, final MACloudHandler handler) {
        if (StringUtil.isEmpty(data.getAccessKey()) || StringUtil.isEmpty(data.getAccessToken())) {
            return;
        }
        String urlRequest = (data.getServerAddress() + MAContant.API_RESULT);
        Log.i(TAG, "result - " + urlRequest + " - Data: " + request.toString());
        AndroidNetworking.post(urlRequest)
                .addHeaders("Gears-Access-Key", data.getAccessKey())
                .addHeaders("Gears-Access-Token", data.getAccessToken())
                .addHeaders("Content-Type", "application/json")
                .addApplicationJsonBody(request)
                .build()
                .getAsObject(ResultApiResponse.class, new ParsedRequestListener<ResultApiResponse>() {
                    @Override
                    public void onResponse(ResultApiResponse response) {
                        Log.i(TAG, "result - onSuccess " + response.toString());
                        handler.onSuccess(response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.i(TAG, "result - anError " + anError.toString());
                        MACloudErrorResponse response = new MACloudErrorResponse(anError.getErrorCode());
                        if (anError.getErrorBody() != null) {
                            try {
                                JsonObject bodyJson = (new JsonParser()).parse(anError.getErrorBody()).getAsJsonObject();
                                if (bodyJson.has("code")) {
                                    response.setError(bodyJson.get("code").getAsString());
                                }
                                if (bodyJson.has("msg")) {
                                    response.setDescription(bodyJson.get("msg").getAsString());
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        handler.onFailed(response);
                    }
                });
    }
}
