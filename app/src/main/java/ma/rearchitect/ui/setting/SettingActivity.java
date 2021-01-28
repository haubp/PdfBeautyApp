package opswat.com.flow.setting;

import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import opswat.com.R;
import opswat.com.data.MASettingData;
import opswat.com.enums.ConfigStatus;
import opswat.com.flow.base.BaseActivity;

public class SettingActivity extends BaseActivity implements ISettingView {
    private ISettingPresenter presenter = new SettingPresenterIml();

    @Override
    public ISettingPresenter getPresenter() {
        return presenter;
    }

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    protected Integer getSlideMenuId() {
        return null;
    }

    @Override
    protected Integer getBtnMenuId() {
        return null;
    }

    @Override
    protected void initialized() {
        Button btnUpdate = findViewById(R.id.setting_btn_update);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateSetting();
            }
        });

        EditText etReminder = findViewById(R.id.et_reminder_time);
        etReminder.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                findViewById(R.id.setting_tv_error_input).setVisibility(View.GONE);
                findViewById(R.id.setting_tv_message).setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        findViewById(R.id.setting_tv_notification).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickCheckbox(R.id.setting_btn_notification);
            }
        });

        findViewById(R.id.setting_btn_notification).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickCheckbox(R.id.setting_btn_notification);
            }
        });

        findViewById(R.id.setting_tv_remind_notification).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tongleCheckbox(R.id.setting_btn_remind_notification);
            }
        });

        findViewById(R.id.setting_btn_remind_notification).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tongleCheckbox(R.id.setting_btn_remind_notification);
            }
        });

        findViewById(R.id.setting_tv_enable_debug_log).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tongleCheckbox(R.id.setting_btn_enable_debug_log);
            }
        });

        findViewById(R.id.setting_btn_enable_debug_log).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tongleCheckbox(R.id.setting_btn_enable_debug_log);
            }
        });

        findViewById(R.id.setting_btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        updateView();
    }

    private void tongleCheckbox(int buttonId) {
        findViewById(R.id.setting_tv_message).setVisibility(View.GONE);
        Button btnNotification = findViewById(buttonId);
        String tag = btnNotification.getTag().toString();
        String newTag = tag.equals("0") ? "1" : "0";
        setCheckBox(buttonId, newTag);
    }

    private void clickCheckbox(int buttonId) {
        findViewById(R.id.setting_tv_message).setVisibility(View.GONE);
        Button btnNotification = findViewById(buttonId);
        String tag = btnNotification.getTag().toString();
        String newTag = tag.equals(ConfigStatus.DEFAULT_UNSELECTED_CONFIG.getStringValue()) || tag.equals(ConfigStatus.USER_UNSELECTED_CONFIG.getStringValue()) ?
                ConfigStatus.USER_SELECTED_CONFIG.getStringValue() : ConfigStatus.USER_UNSELECTED_CONFIG.getStringValue();
        setCheckBox(buttonId, newTag);
    }

    @Override
    public void bindingFetchConfig(int notifyInstalledApp) {
        super.bindingFetchConfig(notifyInstalledApp);
        updateView();
    }

    public void updateView() {
        MASettingData settingData = new MASettingData(this);
        String enableNotifyInstallApp = settingData.getNotifyInstalledApp().getStringValue();
        setCheckBox(R.id.setting_btn_notification, enableNotifyInstallApp);

        String enableDebugLog = settingData.isEnableDebugLog() ? "1" : "0";
        setCheckBox(R.id.setting_btn_enable_debug_log, enableDebugLog);

        String enableNotifyReminder = settingData.isNotifyReminder() ? "1" : "0";
        setCheckBox(R.id.setting_btn_remind_notification, enableNotifyReminder);

        EditText etReminderTime = findViewById(R.id.et_reminder_time);
        String reminderTime = String.valueOf(settingData.reminderTime());
        etReminderTime.setText(reminderTime);

        TextView tv_day = findViewById(R.id.setting_tv_days);
        String temp = getString(R.string.days);
        if (settingData.reminderTime() == 1)
            temp = temp.replace("days", "day");
        tv_day.setText(temp);
    }

    private void setCheckBox(int buttonId, String newTag) {
        Button btnCheckbox = findViewById(buttonId);
        Drawable checkDrawable = newTag.equals(ConfigStatus.DEFAULT_SELECTED_CONFIG.getStringValue()) || newTag.equals(ConfigStatus.USER_SELECTED_CONFIG.getStringValue())?
                getDrawable(R.drawable.ic_checkbox) : getDrawable(R.drawable.ic_uncheck);
        btnCheckbox.setBackground(checkDrawable);
        btnCheckbox.setTag(newTag);
    }

    private void updateSetting() {
        Button btnNotification = findViewById(R.id.setting_btn_notification);
        String tagNotification = btnNotification.getTag().toString();
        ConfigStatus enableNotification = ConfigStatus.getConfigStatus(tagNotification);

        Button btnDebugLog = findViewById(R.id.setting_btn_enable_debug_log);
        String tagEnableDebug = btnDebugLog.getTag().toString();
        boolean enableDebugLog = tagEnableDebug.equals("1");

        Button btnNotifyReminder = findViewById(R.id.setting_btn_remind_notification);
        String tagNotifyReminder = btnNotifyReminder.getTag().toString();
        boolean enableNotifyReminder = tagNotifyReminder.equals("1");

        EditText etReminderTime = findViewById(R.id.et_reminder_time);
        if (!isNumeric(etReminderTime.getText().toString())) {
            findViewById(R.id.setting_tv_error_input).setVisibility(View.VISIBLE);
            return;
        }
        int reminderTime;
        try {
            reminderTime = Integer.parseInt(etReminderTime.getText().toString());
        } catch(Exception e) {
            findViewById(R.id.setting_tv_error_input).setVisibility(View.VISIBLE);
            return;
        }
        if (reminderTime < 1  || reminderTime > 30) {
            findViewById(R.id.setting_tv_error_input).setVisibility(View.VISIBLE);
            return;
        }
        TextView tv_day = findViewById(R.id.setting_tv_days);
        String temp = getString(R.string.days);
        if (reminderTime == 1)
            temp = temp.replace("days", "day");
        tv_day.setText(temp);

        MASettingData settingData = new MASettingData(this);
        settingData.updateSetting(enableNotification, enableDebugLog, enableNotifyReminder, reminderTime);
        findViewById(R.id.setting_tv_message).setVisibility(View.VISIBLE);
        findViewById(R.id.setting_tv_error_input).setVisibility(View.GONE);

        presenter.bindingService();
    }

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}

