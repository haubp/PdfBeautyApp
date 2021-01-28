package opswat.com.flow.home;

import opswat.com.mvp.MvpView;
import opswat.com.network.model.response.PolicyCheckResponse;

public interface IMainView extends MvpView{
    void handleRegisterWithNoNetwork();
    void handlePolicyCheckResponse(PolicyCheckResponse policyCheckResponse);
    void bindingRegistered();
    void bindingUpdatedAccountData();
    void handleOutOfToken();
    void showDialogNotFoundRegCode();
    void showDialogErrorGenerateRegister();
    void showDialogOutOfToken();
    void showReportUniversalSuccessfully();
    void showReportUniversalOutOfCountTry();
    void showReportUniversalFailed(String errorCode);
}
