package ma.rearchitect.ui.mvp;

import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.Nullable;

/**
 * Created by LenVo on 22/08/18.
 */

abstract public class MvpActivity extends AppCompatActivity implements MvpView {
    @Override
    public Context getContext() {
        return this;
    }
    protected abstract MvpPresenter getPresenter();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPresenter().onAttach(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getPresenter().onDetach();
    }
}
