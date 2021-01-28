package opswat.com.flow.base;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import opswat.com.R;
import opswat.com.constant.MAContant;
import opswat.com.data.AccountData;
import opswat.com.data.MASettingData;
import opswat.com.data.UserIdentity;
import opswat.com.enums.ConfigStatus;
import opswat.com.enums.DialogTypes;
import opswat.com.flow.about.AboutActivity;
import opswat.com.flow.setting.SettingActivity;
import opswat.com.flow.submitFeedback.SubmitFeedbackActivity;
import opswat.com.handler.MACloudHandler;
import opswat.com.handler.MADialogUserIdentityHandler;
import opswat.com.mvp.MvpActivity;
import opswat.com.network.helper.MACloudHelper;
import opswat.com.network.model.request.ResultApiRequest;
import opswat.com.network.model.response.MACloudErrorResponse;
import opswat.com.network.model.response.MACloudResponse;
import opswat.com.network.model.response.PolicyCheckResponse;
import opswat.com.util.ColorUtil;
import opswat.com.util.CommonUtil;
import opswat.com.view.custom.MADialog;
import opswat.com.view.custom.SlideMenuLayout.SlideMenuLayout;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public abstract class BaseActivity extends MvpActivity {
    @LayoutRes
    protected abstract Integer getLayoutId();

    @IdRes
    protected abstract Integer getSlideMenuId();

    @IdRes
    protected abstract Integer getBtnMenuId();

    private SlideMenuLayout slideMenuLayout;
    private Button unregisterBtn;
    protected Dialog maDialog;
    protected Dialog maDialogDeleteCommand;
    protected Dialog maDialogUserIdentity;
    private UserIdentity currentUserIdentity;
    protected long TIME_DURING_CLICK = 200;
    protected long lastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        MAApplication.getInstance().updateCurrentActivity(this);
        initialized();

        initSlideMenu();
    }

    abstract protected void initialized();

    protected void bindingUnRegisterBtn() {
        if (unregisterBtn == null) {
            return;
        }
        AccountData accountData = MAApplication.getInstance().getMaCloudData().getAccountData();
        unregisterBtn.setVisibility(accountData.isRegistered() ? View.VISIBLE : View.GONE);
        unregisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slideMenuLayout.closeRightSlide();
                unregisterDevice();
            }
        });
    }

    protected void unregisterDevice() {
        maDialog = MADialog.showDialogLoading(this, getString(R.string.un_registering_your_device), null);
        AccountData accountData = MAApplication.getInstance().getMaCloudData().getAccountData();
        MACloudHelper.deleteDevice(accountData, new MACloudHandler() {
            @Override
            public void onFailed(MACloudErrorResponse response) {
                showDialogErrorGenerateUnRegister();
            }

            @Override
            public void onSuccess(MACloudResponse response) {
                bindingUnregister();
            }
        });
    }

    private void initSlideMenu() {
        if (getSlideMenuId() == null) {
            return;
        }
        slideMenuLayout = findViewById(getSlideMenuId());

        findViewById(getBtnMenuId()).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slideMenuLayout.toggleRightSlide();
            }
        });

        slideMenuLayout.findViewById(R.id.menu_submit_feedback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - lastClickTime < TIME_DURING_CLICK) {
                    return;
                }
                lastClickTime = SystemClock.elapsedRealtime();

                slideMenuLayout.closeRightSlide();
                Intent intent = new Intent(getApplicationContext(), SubmitFeedbackActivity.class);
                startActivity(intent);
            }
        });

        slideMenuLayout.findViewById(R.id.menu_about).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - lastClickTime < TIME_DURING_CLICK) {
                    return;
                }
                lastClickTime = SystemClock.elapsedRealtime();

                slideMenuLayout.closeRightSlide();
                Intent intent = new Intent(getApplicationContext(), AboutActivity.class);
                startActivity(intent);
            }
        });

        final Context context = this;
        slideMenuLayout.findViewById(R.id.menu_try_other_product).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - lastClickTime < TIME_DURING_CLICK) {
                    return;
                }
                lastClickTime = SystemClock.elapsedRealtime();

                slideMenuLayout.closeRightSlide();
                CommonUtil.openBrowser(context, getString(R.string.url_try_other_product));
            }
        });

        slideMenuLayout.findViewById(R.id.menu_setting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - lastClickTime < TIME_DURING_CLICK) {
                    return;
                }
                lastClickTime = SystemClock.elapsedRealtime();

                slideMenuLayout.closeRightSlide();
                Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
                startActivity(intent);
            }
        });

        slideMenuLayout.findViewById(R.id.menu_faq).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - lastClickTime < TIME_DURING_CLICK) {
                    return;
                }
                lastClickTime = SystemClock.elapsedRealtime();

                slideMenuLayout.closeRightSlide();
                CommonUtil.openBrowser(context, getString(R.string.url_faq));
            }
        });

        unregisterBtn = slideMenuLayout.findViewById(R.id.menu_unregister);
        bindingUnRegisterBtn();
    }

    protected void setImageView(@IdRes int imgId, int resId) {
        ImageView imageView = findViewById(imgId);
        imageView.setImageDrawable(getDrawable(resId));
    }

    protected void setTextView(@IdRes int tvId, String text) {
        TextView textView = findViewById(tvId);
        textView.setText(text);
    }

    protected void setBackgroundColor(@IdRes int viewId, int colorId) {
        View view = findViewById(viewId);
        view.setBackgroundColor(ColorUtil.getColor(this, colorId));
    }

    protected void setColorTextView(@IdRes int tvId, int colorId) {
        TextView textView = findViewById(tvId);
        textView.setTextColor(ColorUtil.getColor(this, colorId));
    }

    protected void setFontTextView(@IdRes int tvId, Typeface typeface) {
        TextView textView = findViewById(tvId);
        textView.setTypeface(typeface);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(base));
    }

    @Override
    public void onBackPressed() {
        if (slideMenuLayout != null && slideMenuLayout.isRightSlideOpen()) {
            slideMenuLayout.closeRightSlide();
        } else {
            super.onBackPressed();
        }
    }

    public void showDialogOutOfToken() {
        if (maDialog != null) {
            maDialog.dismiss();
        }
        String outOfToken = getString(R.string.error_register_generate);
        maDialog = MADialog.showDialogWithType(DialogTypes.ERROR, this, getString(R.string.error_register_title),
                outOfToken, null, null, null);
    }

    public void bindingUnregister() {
        if (maDialog != null) {
            maDialog.dismiss();
        }
        maDialog = MADialog.showDialogWithType(DialogTypes.INFO, this, getString(R.string.un_register_successfully),
                null, null, null, null);

        resetAccountInfo();
    }

    protected void resetAccountInfo() {
        MAApplication.getInstance().getMaCloudData().resetAccountInfo();
        MAApplication.getInstance().stopMaCloudTimer();
        bindingUnRegisterBtn();
    }

    public void showDialogErrorGenerateUnRegister() {
        if (maDialog != null) {
            maDialog.dismiss();
        }
        maDialog = MADialog.showDialogWithType(DialogTypes.ERROR, this, getString(R.string.error_un_register_title),
                null, null, null, null);
    }

    public void handleDeleteCommand() {
        final Context context = this;
        AccountData accountData = MAApplication.getInstance().getMaCloudData().getAccountData();
        ResultApiRequest request = new ResultApiRequest();
        request.setHwid(accountData.getDeviceId());
        request.setMethod(MAContant.COMMAND_CODE_DELETE + "");
        request.setResult("1");
        MACloudHelper.result(request, accountData, new MACloudHandler() {
            @Override
            public void onFailed(MACloudErrorResponse response) {
            }

            @Override
            public void onSuccess(MACloudResponse response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (maDialogDeleteCommand == null || !maDialogDeleteCommand.isShowing()) {
                            maDialogDeleteCommand = MADialog.showDialogWithType(DialogTypes.ERROR, context, getString(R.string.error_your_account_deleted_title),
                                    getString(R.string.error_your_account_deleted_message), null, null, null);
                        }
                        resetAccountInfo();
                    }
                });
            }
        });
    }

    public void handleUserIdentity() {
        final AccountData accountData = MAApplication.getInstance().getMaCloudData().getAccountData();
        final UserIdentity userIdentity = accountData.getUserIdentity();
        if (userIdentity == null) {
            return;
        }

        if (maDialogUserIdentity != null && maDialogUserIdentity.isShowing()) {
            if (currentUserIdentity == null || currentUserIdentity.equalValues(userIdentity)) {
                return;
            }
            maDialogUserIdentity.dismiss();
        }

        currentUserIdentity = userIdentity;
        final Context context = this;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                maDialogUserIdentity = MADialog.showDialogUserIdentity(context, userIdentity.getMsg(),
                        userIdentity.getRegex(), new MADialogUserIdentityHandler() {
                            @Override
                            public void onClickSubmit(String userInput) {
                                userIdentity.setUserInput(userInput);
                                userIdentity.setReIdentity(false);
                                MAApplication.getInstance().getMaCloudData().updateUserIdentity(userIdentity);
                            }
                        });
            }
        });
    }

    public void handleOutOfToken() {
        showDialogOutOfToken();
        MAApplication.getInstance().getMaCloudData().setOutOfToken(true);
    }

    public void bindingFetchConfig(int notifyInstalledApp) {
        MASettingData settingData = new MASettingData(this);
        ConfigStatus configStatus = notifyInstalledApp == 1 ? ConfigStatus.DEFAULT_SELECTED_CONFIG : ConfigStatus.DEFAULT_UNSELECTED_CONFIG;
        settingData.updateNotifyInstalledApp(configStatus);
    }

    public void handlePolicyCheck(PolicyCheckResponse policyCheckResponse) {
        MAApplication.getInstance().getMaCloudData().setOutOfToken(false);
    }

    public void handleIpConnection() {

    }

    public void handleAppScanning() {

    }

    public void handleNoNetworkConnection() {
        if (maDialog != null) {
            maDialog.dismiss();
        }
        maDialog = MADialog.showDialogWithType(DialogTypes.ERROR, this, getString(R.string.error_no_connection), null, null, null, null);
    }
}
