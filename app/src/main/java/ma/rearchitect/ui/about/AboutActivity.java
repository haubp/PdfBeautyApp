package opswat.com.flow.about;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import java.util.Calendar;

import opswat.com.R;
import opswat.com.constant.MAContant;
import opswat.com.data.AccountData;
import opswat.com.enums.DialogTypes;
import opswat.com.flow.base.BaseActivity;
import opswat.com.flow.enroll.EnrollActivity;
import opswat.com.mvp.MvpPresenter;
import opswat.com.util.AppUtils;
import opswat.com.util.CommonUtil;
import opswat.com.util.DateUtil;
import opswat.com.util.StringUtil;
import opswat.com.view.custom.MADialog;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class AboutActivity extends BaseActivity implements IAboutView {
    private IAboutPresenter presenter = new AboutPresenterIml();

    @Override
    protected MvpPresenter getPresenter() {
        return presenter;
    }

    @Override
    protected Integer getBtnMenuId() {
        return null;
    }

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_about;
    }

    @Override
    protected Integer getSlideMenuId() {
        return null;
    }

    @Override
    protected void initialized() {

        //Update year of copyright
        String yearString = Integer.toString(Calendar.getInstance().get(Calendar.YEAR));
        TextView copyright = (TextView) findViewById(R.id.about_copyright);
        copyright.setText(getString(R.string.copyright).replace("%d", yearString));

        findViewById(R.id.about_btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        findViewById(R.id.about_btn_enroll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startEnrollDevice();
            }
        });

        findViewById(R.id.about_btn_unregister).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unregisterDevice();
            }
        });

        final Context context = this;
        findViewById(R.id.about_tv_term).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtil.openBrowser(context, getString(R.string.url_term));
            }
        });

        bindingAppInfo();
    }

    private void bindingAppInfo() {
        AccountData accountData = MAApplication.getInstance().getMaCloudData().getAccountData();

        findViewById(R.id.about_btn_unregister).setVisibility(accountData.isRegistered() ? View.VISIBLE : View.GONE);
        findViewById(R.id.about_btn_enroll).setVisibility(accountData.isRegistered() ? View.GONE : View.VISIBLE);
        findViewById(R.id.about_view_managed).setVisibility(accountData.isRegistered() ? View.VISIBLE : View.GONE);

        TextView tvVersion = findViewById(R.id.about_tv_version), tvDeviceId = findViewById(R.id.about_tv_device_id),
                tvLastConnected = findViewById(R.id.about_tv_last_connected),
                tvManagedBy = findViewById(R.id.about_tv_managed_by),
                tvServer = findViewById(R.id.about_tv_server);

        String appVersion = AppUtils.appVersion(this);
        tvVersion.setText(appVersion);

        String deviceId = MAContant.UNKNOWN_TEXT, lastConnected = MAContant.UNKNOWN_TEXT,
                managedBy = MAContant.UNKNOWN_TEXT;

        if (!StringUtil.isEmpty(accountData.getDeviceId())) {
            deviceId = accountData.getDeviceId();
        }
        tvDeviceId.setText(deviceId);

        String serverAddress = accountData.getServerAddress().replace("https://", "")
                .replace("http://", "");
        if (serverAddress.charAt(serverAddress.length() - 1) == '/') {
            serverAddress = serverAddress.substring(0, serverAddress.length() - 1);
        }
        tvServer.setText(serverAddress);

        if (accountData.isRegistered()) {
            long lastConnectedTime = MAApplication.getInstance().getMaCloudData().getLastConnected();
            if (lastConnectedTime != 0) {
                lastConnected = DateUtil.getFullFormatDate(lastConnectedTime);
            }
            if (!StringUtil.isEmpty(accountData.getAccountName())) {
                managedBy = accountData.getAccountName();
            }
            tvLastConnected.setText(lastConnected);
            tvManagedBy.setText(managedBy);
        }
    }

    private void startEnrollDevice() {
        EnrollActivity.startActivityForResult(this);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void resetAccountInfo() {
        super.resetAccountInfo();
        bindingAppInfo();
    }

    @Override
    public void bindingRegistered() {
        if (maDialog != null) {
            maDialog.dismiss();
        }
        maDialog = MADialog.showDialogWithType(DialogTypes.INFO, this, getString(R.string.register_successfully),
                null, null, null, null);
        bindingAppInfo();
    }

    @Override
    public void handleRegisterWithNoNetwork() {
        if (maDialog != null) {
            maDialog.dismiss();
        }
        maDialog = MADialog.showDialogWithType(DialogTypes.ERROR, this, getString(R.string.error_register_title),
                getString(R.string.error_no_connection), null, null, null);
    }

    @Override
    public void showDialogNotFoundRegCode() {
        if (maDialog != null) {
            maDialog.dismiss();
        }
        maDialog = MADialog.showDialogWithType(DialogTypes.ERROR, this, getString(R.string.error_register_title),
                getString(R.string.error_register_not_found_reg_code), null, null, null);
    }

    @Override
    public void showDialogErrorGenerateRegister() {
        if (maDialog != null) {
            maDialog.dismiss();
        }
        maDialog = MADialog.showDialogWithType(DialogTypes.ERROR, this, getString(R.string.error_register_title),
                getString(R.string.error_register_generate), null, null, null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MAContant.REQUEST_CODE_ENROLL) {
            if (data == null) {
                return;
            }
            String regCode = data.getStringExtra("regCode");
            String groupId = data.getStringExtra("groupId");
            String serverName = data.getStringExtra("serverName");
            maDialog = MADialog.showDialogRegistering(this);
            presenter.register(regCode, groupId, serverName);
        }
    }

    @Override
    public void showDialogOutOfToken() {
        super.showDialogOutOfToken();
    }
}
