package opswat.com.flow.detail;

import android.content.Context;

import opswat.com.mvp.MvpView;

/**
 * Created by H. Len Vo on 8/22/18.
 */
public class DetailPresenterIml implements IDetailPresenter {
    private IDetailView mainView;
    private Context context;

    @Override
    public void onAttach(IDetailView mvpView) {
        this.mainView = mvpView;
        this.context = mvpView.getContext();
    }

    @Override
    public void onDetach() {
    }
}
