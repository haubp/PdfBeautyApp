package opswat.com.flow.connection;

import android.content.Context;

/**
 * Created by H. Len Vo on 9/3/18.
 */
public class ConnectionPresenterIml implements IConnectionPresenter {
    private IConnectionView view;
    private Context context;

    @Override
    public void onAttach(IConnectionView mvpView) {
        this.view = mvpView;
        this.context = mvpView.getContext();
    }

    @Override
    public void onDetach() {
    }
}
