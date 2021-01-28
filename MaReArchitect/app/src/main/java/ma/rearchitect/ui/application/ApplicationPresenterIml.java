package opswat.com.flow.application;

import android.content.Context;

/**
 * Created by H. Len Vo on 9/3/18.
 */
public class ApplicationPresenterIml implements IApplicationPresenter {
    private IApplicationView view;
    private Context context;

    @Override
    public void onAttach(IApplicationView mvpView) {
        this.view = mvpView;
        this.context = mvpView.getContext();
    }

    @Override
    public void onDetach() {
    }
}
