package opswat.com.flow.submitFeedback;

import android.content.Context;

/**
 * Created by H. Len Vo on 9/4/18.
 */
public class SubmitFeedbackPresenterIml implements ISubmitFeedbackPresenter {
    private ISubmitFeedbackView view;
    private Context context;

    @Override
    public void onAttach(ISubmitFeedbackView mvpView) {
        this.view = mvpView;
        this.context = mvpView.getContext();
    }

    @Override
    public void onDetach() {
    }
}
