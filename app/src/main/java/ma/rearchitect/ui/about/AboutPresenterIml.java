package opswat.com.flow.about;

import android.content.Context;

import opswat.com.constant.MaCloudKey;
import opswat.com.data.AccountData;
import opswat.com.handler.MACloudHandler;
import opswat.com.network.helper.MACloudFactory;
import opswat.com.network.helper.MACloudHelper;
import opswat.com.network.model.request.FetchPrivacyRequest;
import opswat.com.network.model.request.RegisterRequest;
import opswat.com.network.model.response.AccountResponse;
import opswat.com.network.model.response.FetchPrivacyResponse;
import opswat.com.network.model.response.MACloudErrorResponse;
import opswat.com.network.model.response.MACloudResponse;
import opswat.com.network.model.response.RegisterResponse;
import opswat.com.util.NetworkUtils;
import opswat.com.util.StringUtil;

/**
 * Created by H. Len Vo on 9/4/18.
 */
public class AboutPresenterIml implements IAboutPresenter {
    private IAboutView view;
    private Context context;

    @Override
    public void onAttach(IAboutView mvpView) {
        this.view = mvpView;
        this.context = mvpView.getContext();
    }

    @Override
    public void onDetach() {
    }

    @Override
    public void register(final String regCode, final String groupId, final String serverName) {
        if (!NetworkUtils.isNetworkConnected(context)) {
            view.handleRegisterWithNoNetwork();
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
                    view.showDialogNotFoundRegCode();
                } else {
                    view.showDialogErrorGenerateRegister();
                }
            }

            @Override
            public void onSuccess(MACloudResponse response) {
                FetchPrivacyResponse fetchPrivacyResponse = (FetchPrivacyResponse) response;
                doRegister(regCode, groupId, serverName, fetchPrivacyResponse);
            }
        });
    }

    private void doRegister(final String regCode, final String groupId, String serverName, FetchPrivacyResponse privacy) {
        RegisterRequest request = MACloudFactory.getRegisterRequest(MAApplication.getInstance().getMaCloudData().getAccountData(),
                MAApplication.getInstance().getDeviceBuilder(), privacy);
        String serverURL = StringUtil.getServerUrlFromName(serverName);
        if (!StringUtil.isEmpty(groupId)) {
            request.setGroupId(groupId);
        }
        final String finalURL = serverURL;
        MACloudHelper.register(serverURL, regCode, request, new MACloudHandler() {
            @Override
            public void onFailed(MACloudErrorResponse response) {
                //Todo: Check status code: CONFLICT, OUT_OF_TOKEN, ... to clean MaCloudData and show dialog
                if (MaCloudKey.isErrorNotFound(response.getStatusCode(), response.getError())) {
                    view.showDialogNotFoundRegCode();
                } else if (MaCloudKey.isErrorOutOfToken(response.getStatusCode(), response.getError())) {
                    view.showDialogOutOfToken();
                } else {
                    view.showDialogErrorGenerateRegister();
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
                view.bindingRegistered();
            }

            @Override
            public void onSuccess(MACloudResponse response) {
                AccountResponse accountResponse = (AccountResponse) response;
                AccountData data = MAApplication.getInstance().getMaCloudData().getAccountData();
                data.setOwnerEmail(accountResponse.getOwnerEmail());
                data.setAccountName(accountResponse.getAccountName());
                MAApplication.getInstance().getMaCloudData().setAccountData(data);
                view.bindingRegistered();
            }
        });
    }
}
