package opswat.com.flow.about;

import opswat.com.mvp.MvpView;

/**
 * Created by H. Len Vo on 9/4/18.
 */
public interface IAboutView extends MvpView {
    void handleRegisterWithNoNetwork();
    void bindingRegistered();
    void showDialogNotFoundRegCode();
    void showDialogErrorGenerateRegister();
    void showDialogOutOfToken();
}
