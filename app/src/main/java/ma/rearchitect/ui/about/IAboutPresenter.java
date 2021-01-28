package ma.rearchitect.ui.about;

import ma.rearchitect.ui.mvp.MvpPresenter;


/**
 * Created by H. Len Vo on 9/4/18.
 */
public interface IAboutPresenter extends MvpPresenter<IAboutView> {
    void register(String regCode, String groupId, String serverName);
}
