package opswat.com.flow.detail;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import opswat.com.R;
import opswat.com.device.DeviceDefine;
import opswat.com.device.HealthDevice;
import opswat.com.device.SecurityDevice;
import opswat.com.device.model.AdTracking;
import opswat.com.device.model.Battery;
import opswat.com.device.model.Encryption;
import opswat.com.device.model.Hardware;
import opswat.com.device.model.Jailbreak;
import opswat.com.device.model.Memory;
import opswat.com.device.model.OsUpToDate;
import opswat.com.device.model.PasswordAndLockScreen;
import opswat.com.device.model.RebootRecency;
import opswat.com.device.model.Storage;
import opswat.com.flow.base.BaseActivity;
import opswat.com.util.IntentUtils;

public class DetailActivity extends BaseActivity implements IDetailView {
    private static final String TAG_DETAIL_PAGE = "TagDetailPage";
    private TextView tvTitle;
    private View headerIcon;
    private View headerText;
    private View headerProgressBar;
    private View headerBatteryProgressBar;
    private IDetailPresenter presenter = new DetailPresenterIml();

    public static void start(Context context, String tag) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra(TAG_DETAIL_PAGE, tag);
        context.startActivity(intent);
    }

    @Override
    public IDetailPresenter getPresenter() {
        return presenter;
    }

    @Override
    protected Integer getSlideMenuId() {
        return R.id.detail_slide_menu;
    }

    @Override
    protected Integer getBtnMenuId() {
        return R.id.detail_right_menu;
    }

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_detail;
    }

    @Override
    protected void initialized() {
        tvTitle = findViewById(R.id.detail_tv_title_name);
        headerIcon = findViewById(R.id.detail_header_with_icon);
        headerProgressBar = findViewById(R.id.detail_header_with_progressbar);
        headerText = findViewById(R.id.detail_header_with_text);
        headerBatteryProgressBar = findViewById(R.id.detail_header_with_battery);

        SecurityDevice securityDevice = MAApplication.getInstance().getDeviceBuilder().getSecurityDevice();
        HealthDevice healthDevice = MAApplication.getInstance().getDeviceBuilder().getHealthDevice();
        if (getIntent().getStringExtra(TAG_DETAIL_PAGE) != null) {
            final String tag = getIntent().getStringExtra(TAG_DETAIL_PAGE);
            switch (tag) {
                case DeviceDefine.JAILBREAK_KEY:
                    bindingJailbreak(securityDevice.getJaibreak());
                    break;
                case DeviceDefine.PASSWORD_KEY:
                    bindingPassword(securityDevice.getPasswordAndLockScreen());
                    break;
                case DeviceDefine.AD_TRACKING_KEY:
                    bindingAdTracking(securityDevice.getAdTracking());
                    break;
                case DeviceDefine.OS_KEY:
                    bindingOsUpToDate(securityDevice.getOsUpToDate());
                    break;
                case DeviceDefine.ENCRYPTION_KEY:
                    bindingEncryption(securityDevice.getEncryption());
                    break;
                case DeviceDefine.STORAGE_KEY:
                    bindingFreeStorage(healthDevice.getStorage());
                    break;
                case DeviceDefine.MEMORY_KEY:
                    bindingMemory(healthDevice.getMemory());
                    break;
                case DeviceDefine.REBOOT_KEY:
                    bindingReboot(healthDevice.getRebootRecency());
                    break;
                case DeviceDefine.BATTERY_KEY:
                    bindingBattery(healthDevice.getBattery());
                    break;
                case DeviceDefine.HARDWARE_KEY:
                    bindingHardware(healthDevice.getHardware());
                    break;
            }
        }

        findViewById(R.id.detail_btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backHomePage();
            }
        });
    }

    private void backHomePage() {
        this.finish();
    }

    private void bindingJailbreak(Jailbreak jailbreak) {
        tvTitle.setText(getString(R.string.jailbreak));
        headerIcon.setVisibility(View.VISIBLE);
        setImageView(R.id.detail_img_ic_cate, R.drawable.ic_jailbreak_detail);

        setImageView(R.id.detail_img_ic_status, jailbreak.isAuthentic() ?
                R.drawable.ic_good : R.drawable.ic_bad);

        View jailbreakContent = jailbreak.isAuthentic() ? findViewById(R.id.detail_jailbreak_good) :
                findViewById(R.id.detail_jailbreak_bad);
        jailbreakContent.setVisibility(View.VISIBLE);
    }

    private void bindingPassword(PasswordAndLockScreen passwordAndLockScreen) {
        final Context context = this;
        tvTitle.setText(getString(R.string.password));
        headerIcon.setVisibility(View.VISIBLE);

        boolean isGoodStatus = passwordAndLockScreen.isProtected();
        setImageView(R.id.detail_img_ic_cate, R.drawable.ic_password_detail);
        setImageView(R.id.detail_img_ic_status, isGoodStatus ?R.drawable.ic_good : R.drawable.ic_bad);

        View viewContent = isGoodStatus ? findViewById(R.id.detail_password_good) : findViewById(R.id.detail_password_bad);
        viewContent.setVisibility(View.VISIBLE);

        Button btnOpenSettings = findViewById(R.id.detail_btn_open_settings);
        btnOpenSettings.setVisibility(isGoodStatus ? View.GONE : View.VISIBLE);
        btnOpenSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                //Show Lock screen + password page
                String strIntent = DevicePolicyManager.ACTION_SET_NEW_PASSWORD;
                if (!IntentUtils.startActivity(context, strIntent)) {
                    Toast.makeText(context, "Can not open this setting", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //Todo: Binding password type for detail page
    }

    private void bindingAdTracking(AdTracking adTracking) {
        final Context context = this;
        tvTitle.setText(getString(R.string.ad_tracking));
        headerIcon.setVisibility(View.VISIBLE);

        boolean isGoodStatus = adTracking.isEnable();
        setImageView(R.id.detail_img_ic_cate, R.drawable.ic_ad_tracking_detail);
        setImageView(R.id.detail_img_ic_status, adTracking.isEnable() ?
                R.drawable.ic_good : R.drawable.ic_bad);

        View viewContent = isGoodStatus ? findViewById(R.id.detail_ad_tracking_good) : findViewById(R.id.detail_ad_tracking_bad);
        viewContent.setVisibility(View.VISIBLE);

        Button btnOpenSettings = findViewById(R.id.detail_btn_open_settings);
        btnOpenSettings.setVisibility(isGoodStatus ? View.GONE : View.VISIBLE);
        btnOpenSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                //Show Ads page
                String strIntent = "com.google.android.gms.settings.ADS_PRIVACY";
                if (!IntentUtils.startActivity(context, strIntent)) {
                    Toast.makeText(context, "Can not open this setting", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void bindingOsUpToDate(OsUpToDate osUpToDate) {
        tvTitle.setText(getString(R.string.os_up_to_date));
        headerIcon.setVisibility(View.VISIBLE);

        setImageView(R.id.detail_img_ic_cate, R.drawable.ic_os_detail);

        findViewById(R.id.detail_tv_info).setVisibility(View.VISIBLE);
        String osVersion = getString(R.string.android) + " " + osUpToDate.getOsVersion();
        setTextView(R.id.detail_tv_info, osVersion);
        /*
        //Show general device information settings (serial number, software version, phone number, etc.).
        Button btnOpenSettings = findViewById(R.id.detail_btn_open_settings);
        btnOpenSettings.setVisibility(false ? View.GONE : View.VISIBLE);
        btnOpenSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent intent = new Intent(Settings.ACTION_DEVICE_INFO_SETTINGS);
                startActivity(intent);
            }
        });
        */
        findViewById(R.id.detail_os_good).setVisibility(View.VISIBLE);
    }

    private void bindingEncryption(Encryption encryption) {
        final Context context = this;
        tvTitle.setText(getString(R.string.encryption));
        headerIcon.setVisibility(View.VISIBLE);

        boolean isGoodStatus = encryption.isGoodStatus();
        setImageView(R.id.detail_img_ic_cate, R.drawable.ic_encrypt_detail);
        setImageView(R.id.detail_img_ic_status, isGoodStatus? R.drawable.ic_good : R.drawable.ic_bad);

        View viewContent = isGoodStatus ? findViewById(R.id.detail_encryption_good) : findViewById(R.id.detail_encryption_bad);
        viewContent.setVisibility(View.VISIBLE);

        Button btnOpenSettings = findViewById(R.id.detail_btn_open_settings);
        btnOpenSettings.setVisibility(isGoodStatus ? View.GONE : View.VISIBLE);
        btnOpenSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                //Show Encryption data page.
                String strIntent = DevicePolicyManager.ACTION_START_ENCRYPTION;
                if (!IntentUtils.startActivity(context, strIntent)) {
                    Toast.makeText(context, "Can not open this setting", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void bindingFreeStorage(Storage storage) {
        final Context context= this;
        tvTitle.setText(getString(R.string.free_storage));
        headerProgressBar.setVisibility(View.VISIBLE);
        int percent = 100 - (int) (storage.getFreeMem() * 100 / storage.getTotalMem());
        setTextView(R.id.detail_tv_begin_value, percent + getString(R.string.percent));
        setTextView(R.id.detail_tv_end_value, (100 - percent) + getString(R.string.percent));

        boolean isGoodStatus = storage.isGoodStatus();
        int colorStatus = isGoodStatus ?
                R.color.color_compliant : R.color.color_non_compliant;
        setColorTextView(R.id.detail_tv_begin_value, colorStatus);
        setColorTextView(R.id.detail_tv_end_value, colorStatus);

        ProgressBar progressBar = findViewById(R.id.detail_progress_bar);
        progressBar.setProgress(percent);
        progressBar.setProgressDrawable(isGoodStatus ?
                getDrawable(R.drawable.progress_bar_style_good) : getDrawable(R.drawable.progress_bar_style_bad));


        View viewContent = isGoodStatus ? findViewById(R.id.detail_storage_good) : findViewById(R.id.detail_storage_bad);
        viewContent.setVisibility(View.VISIBLE);

        Button btnOpenSettings = findViewById(R.id.detail_btn_open_settings);
        btnOpenSettings.setVisibility(isGoodStatus ? View.GONE : View.VISIBLE);
        btnOpenSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                //Show Internal storage settings
                String strIntent = "android.settings.INTERNAL_STORAGE_SETTINGS";
                if (!IntentUtils.startActivity(context, strIntent)) {
                    Toast.makeText(context, "Can not open this setting", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void bindingMemory(Memory memory) {
        tvTitle.setText(getString(R.string.available_memory));
        headerProgressBar.setVisibility(View.VISIBLE);
        int percent = (int) (memory.getAvailMem() * 100 / memory.getTotalMem());
        setTextView(R.id.detail_tv_begin_value, (100 - percent) + getString(R.string.percent));
        setTextView(R.id.detail_tv_end_value, percent + getString(R.string.percent));

        int colorStatus = memory.isGoodStatus() ?
                R.color.color_compliant : R.color.color_non_compliant;
        setColorTextView(R.id.detail_tv_begin_value, colorStatus);
        setColorTextView(R.id.detail_tv_end_value, colorStatus);

        ProgressBar progressBar = findViewById(R.id.detail_progress_bar);
        progressBar.setProgress(100 - percent);
        progressBar.setProgressDrawable(memory.isGoodStatus() ?
                getDrawable(R.drawable.progress_bar_style_good) : getDrawable(R.drawable.progress_bar_style_bad));

        View viewContent = memory.isGoodStatus() ?
                findViewById(R.id.detail_memory_good) : findViewById(R.id.detail_memory_bad);
        viewContent.setVisibility(View.VISIBLE);
    }

    private void bindingReboot(RebootRecency rebootRecency) {
        tvTitle.setText(getString(R.string.reboot_recency));
        headerText.setVisibility(View.VISIBLE);
        String upTime = "";
        // Less than 1 day, display same the format : 1 hour 2 mininutes ago
        if ( (float)rebootRecency.getUpTimeByMillis()/ TimeUnit.DAYS.toMillis(1) < 1){
            long upTimeByHrs = rebootRecency.getUpTimeByMillis()/ TimeUnit.HOURS.toMillis(1);
            long upTimeByMins = (rebootRecency.getUpTimeByMillis() / TimeUnit.MINUTES.toMillis(1)) - (upTimeByHrs * TimeUnit.HOURS.toMinutes(1));

            upTime = getString(R.string.reboot_time_detail).replace("%h", upTimeByHrs+"").replace("%m", upTimeByMins+"");
            if (upTimeByHrs < 2)
                upTime = upTime.replaceAll("hours", "hour");
            if (upTimeByMins < 2)
                upTime = upTime.replace("minutes", "minute");
            TextView detailValue = findViewById(R.id.detail_tv_value);
            detailValue.setText(upTime);
            detailValue.setTextSize(21);
            // Hide unused textview
            findViewById(R.id.detail_tv_value_description).setVisibility(View.GONE);

        }
        // More than or equal to 1 day, display the old format : 123 \n hours ago
        else {
            setTextView(R.id.detail_tv_value,  rebootRecency.getUpTime() + "");
            upTime = getString(R.string.reboot_time_by_hours).replace("%h ", "");
            setTextView(R.id.detail_tv_value_description, upTime);
            findViewById(R.id.detail_tv_value_description).setVisibility(View.VISIBLE);
        }


        int colorStatus = rebootRecency.isGoodStatus() ?
                R.color.color_compliant : R.color.color_non_compliant;
        setColorTextView(R.id.detail_tv_value, colorStatus);
        setColorTextView(R.id.detail_tv_value_description, colorStatus);

        View viewContent = rebootRecency.isGoodStatus() ?
                findViewById(R.id.detail_reboot_good) : findViewById(R.id.detail_reboot_bad);
        viewContent.setVisibility(View.VISIBLE);
    }

    private void bindingHardware(Hardware hardware) {
        tvTitle.setText(getString(R.string.hardware));
        headerIcon.setVisibility(View.VISIBLE);
        setImageView(R.id.detail_img_ic_cate, R.drawable.ic_hardware_detail);

        setImageView(R.id.detail_img_ic_status, hardware.isGoodStatus() ?
                R.drawable.ic_good : R.drawable.ic_bad);

        View viewContent = hardware.isGoodStatus() ? findViewById(R.id.detail_hardware_good) :
                findViewById(R.id.detail_hardware_bad);
        viewContent.setVisibility(View.VISIBLE);
    }

    private void bindingBattery(Battery battery) {
        tvTitle.setText(getString(R.string.battery));
        headerBatteryProgressBar.setVisibility(View.VISIBLE);

        ProgressBar progressBar = findViewById(R.id.detail_progress_bar_battery);
        progressBar.setProgress(battery.getPercent());
        progressBar.setProgressDrawable(battery.isGoodStatus() ?
                getDrawable(R.drawable.progress_bar_style_good) : getDrawable(R.drawable.progress_bar_style_bad));

        View viewContent = battery.isGoodStatus() ?
                findViewById(R.id.detail_battery_good) : findViewById(R.id.detail_battery_bad);
        viewContent.setVisibility(View.VISIBLE);
    }
}
