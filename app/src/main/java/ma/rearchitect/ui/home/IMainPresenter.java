package opswat.com.flow.home;

import opswat.com.mvp.MvpPresenter;

public interface IMainPresenter extends MvpPresenter<IMainView> {
    void registerOnLoaded();
    void register(String regCode, String groupId, String serverName);
    void report(boolean isUniversal);
}
