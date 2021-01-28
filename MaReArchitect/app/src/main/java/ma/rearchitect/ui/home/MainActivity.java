package opswat.com.flow.home;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import opswat.com.R;
import opswat.com.adapter.ApplicationAdapter;
import opswat.com.adapter.ConnectionAdapter;
import opswat.com.constant.MAContant;
import opswat.com.constant.MaCloudKey;
import opswat.com.data.AccountData;
import opswat.com.data.MACloudData;
import opswat.com.data.MASettingData;
import opswat.com.device.DeviceBuilder;
import opswat.com.device.HealthDevice;
import opswat.com.device.SecurityDevice;
import opswat.com.device.application.ApplicationDBHelper;
import opswat.com.device.application.ApplicationScanHelper;
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
import opswat.com.enums.ApplicationStatus;
import opswat.com.enums.DialogTypes;
import opswat.com.flow.application.ApplicationActivity;
import opswat.com.flow.base.BaseActivity;
import opswat.com.flow.connection.ConnectionActivity;
import opswat.com.flow.detail.DetailActivity;
import opswat.com.flow.enroll.EnrollActivity;
import opswat.com.handler.MADialogHandler;
import opswat.com.logger.LoggerUniversalLink;
import opswat.com.network.model.application.Application;
import opswat.com.network.model.connection.Connection;
import opswat.com.network.model.response.PolicyCheckResponse;
import opswat.com.receiver.ApplicationInstalledReceiver;
import opswat.com.receiver.MaReminderAlarmReceiver;
import opswat.com.receiver.PerformanceCheckAlarmReceiver;
import opswat.com.util.AppUtils;
import opswat.com.util.ColorUtil;
import opswat.com.util.CommonUtil;
import opswat.com.util.NetworkUtils;
import opswat.com.util.PermissionUtils;
import opswat.com.util.SharedPrefsUtils;
import opswat.com.util.StringUtil;
import opswat.com.validation.MaValidation;
import opswat.com.view.custom.MADialog;

public class MainActivity extends BaseActivity implements IMainView {
    private IMainPresenter presenter = new MainPresenterIml();
    private SwipeRefreshLayout swipeRefreshLayout;
    private ConnectionAdapter connectionAdapter = new ConnectionAdapter(ConnectionAdapter.TYPE_IP);
    private ApplicationAdapter applicationAdapter = new ApplicationAdapter(ApplicationAdapter.TYPE_APP);
    private String transactionId, appNameUniversal, appIdUniversal, urlUniversal;
    private ApplicationInstalledReceiver applicationInstalledReceiver;

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return new MainHeaderStatusPageFragmentActivity();
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

    public class DepthPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.75f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0f);

            } else if (position <= 0) { // [-1,0]
                // Use the default slide transition when moving to the left page
                view.setAlpha(1f);
                view.setTranslationX(0f);
                view.setScaleX(1f);
                view.setScaleY(1f);

            } else if (position <= 1) { // (0,1]
                // Fade the page out.
                view.setAlpha(1 - position);

                // Counteract the default slide transition
                view.setTranslationX(pageWidth * -position);

                // Scale the page down (between MIN_SCALE and 1)
                float scaleFactor = MIN_SCALE
                        + (1 - MIN_SCALE) * (1 - Math.abs(position));
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0f);
            }
        }
    }

    private static final int NUM_PAGES = 2;
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    private TabLayout mDotTab;

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public IMainPresenter getPresenter() {
        return presenter;
    }

    @Override
    protected Integer getSlideMenuId() {
        return R.id.home_slide_menu;
    }

    @Override
    protected Integer getBtnMenuId() {
        return R.id.home_right_menu;
    }

    @Override
    protected void initialized() {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
        {
            PermissionUtils.checkPermissionReadExternalStorage(this, false);
        }
        PermissionUtils.checkPermissionWriteExternalStorage(this, false);
        MACloudData maCloudData = MAApplication.getInstance().getMaCloudData();
        maCloudData.loadMaCloudData(this);

        swipeRefreshLayout = findViewById(R.id.home_refresh_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reloadDeviceBuilder(true);
                presenter.report(false);
            }
        });

        /* Page Selector experiment here */

        mPager = (ViewPager) findViewById(R.id.main_header_pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mDotTab = (TabLayout)findViewById(R.id.main_header_pager_tab_dots);
        mDotTab.setupWithViewPager(mPager);
        mPager.setPageTransformer(true, new DepthPageTransformer());

        /* ============================= */

        reloadDeviceBuilder(false);
        bindingAccountInfo(true);

        bindingBottomTab();

        bindingCollapseView();

        bindingViewDetailButton();

        bindingRecycleView();

        bindingBackgroundService();

        handleDeeplinkRequest();

        transactionId = SharedPrefsUtils.getStringPreference(this, MAContant.TRANSACTION_ID_KEY);
        appNameUniversal = SharedPrefsUtils.getStringPreference(this, MAContant.APP_NAME_KEY);
        appIdUniversal = SharedPrefsUtils.getStringPreference(this, MAContant.APP_ID_KEY);
        urlUniversal = SharedPrefsUtils.getStringPreference(this, MAContant.URL_REDIRECT_KEY);
        SharedPrefsUtils.setStringPreference(this, MAContant.TRANSACTION_ID_KEY, null);
        SharedPrefsUtils.setStringPreference(this, MAContant.APP_NAME_KEY, null);
        SharedPrefsUtils.setStringPreference(this, MAContant.APP_ID_KEY, null);
        SharedPrefsUtils.setStringPreference(this, MAContant.URL_REDIRECT_KEY, null);

        handleUniversalLinkRequest();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { //create a BroadcastReceiver in Android Oreo
            initReceiver();
        }
    }

    private void initReceiver() //create a BroadcastReceiver in Android Oreo
    {
        applicationInstalledReceiver = new ApplicationInstalledReceiver();
        final IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addDataScheme("package");
        registerReceiver(applicationInstalledReceiver, filter);
    }

    private void handleUniversalLinkRequest() {
        if (StringUtil.isEmpty(transactionId)) {
            return;
        }

        if (!MaValidation.validateTransactionId(transactionId)) {
            String message = getString(R.string.invalid_transaction_id).replace("$ErrorCode", MaCloudKey.INVALID_INPUT);
            MADialog.showDialogWithType(DialogTypes.ERROR, this, getString(R.string.could_not_report_to_cloud),
                    message, getString(R.string.close), "", null);
            LoggerUniversalLink.writeLog(this, "Invalid transactionId =  " + transactionId);
            return;
        }

        MACloudData maCloudData = MAApplication.getInstance().getMaCloudData();
        boolean registered = maCloudData.getAccountData() != null && maCloudData.getAccountData().isRegistered();
        if (!registered) {
            MADialog.showDialogWithType(DialogTypes.ERROR, this, getString(R.string.could_not_report_to_cloud),
                    getString(R.string.your_device_is_not_enrolled), getString(R.string.close), "", null);
            LoggerUniversalLink.writeLog(this, "The device was not enrolled. transactionId =  " + transactionId);
            return;
        }
        MAApplication.getInstance().getMaCloudData().setTransactionId(transactionId);

        maDialog = MADialog.showDialogLoading(this, getString(R.string.checking_security_status),
                getString(R.string.checking_security_status_description));
        presenter.report(true);
    }


    private void handleDeeplinkRequest() {
        String regCode = SharedPrefsUtils.getStringPreference(this, MAContant.REG_CODE_KEY);
        String groupId = SharedPrefsUtils.getStringPreference(this, MAContant.GROUP_ID_KEY);
        SharedPrefsUtils.setStringPreference(this, MAContant.REG_CODE_KEY, null);
        SharedPrefsUtils.setStringPreference(this, MAContant.GROUP_ID_KEY, null);

        if (StringUtil.isEmpty(regCode)) {
            return;
        }

        if (!MaValidation.validateRegCode(regCode)) {
            MADialog.showDialogWithType(DialogTypes.ERROR, this, getString(R.string.error_register_title),
                    getString(R.string.enroll_invalid_reg_code), null, null, null);
            return;
        }

        if (!MaValidation.validateGroupID(groupId)) {
            MADialog.showDialogWithType(DialogTypes.ERROR, this, getString(R.string.error_register_title),
                    getString(R.string.enroll_invalid_group_identity), null, null, null);
            return;
        }

        MACloudData maCloudData = MAApplication.getInstance().getMaCloudData();
        boolean registered = maCloudData.getAccountData() != null && maCloudData.getAccountData().isRegistered();
        AccountData accountData = maCloudData.getAccountData();
        if (registered && regCode.equalsIgnoreCase(accountData.getRegCode()) &&
                StringUtil.equalsWithNullable(groupId, accountData.getGroupId())) {
            return;
        }

        maDialog = MADialog.showDialogRegistering(this);
        presenter.register(regCode, groupId, MAContant.SERVER_URL);
    }
    @Override
    public void showReportUniversalOutOfCountTry() {
        final Context context = this;
        if (maDialog != null) {
            maDialog.dismiss();
        }
        String message = getString(R.string.could_not_report_to_cloud);
        maDialog = MADialog.showDialogWithType(DialogTypes.ERROR, this, message, null, getString(R.string.retry),
                getString(R.string.help), new MADialogHandler() {
                    @Override
                    public void onClickOK() {
                        handleUniversalLinkRequest();
                    }

                    @Override
                    public void onClickCancel() {
                        CommonUtil.openBrowser(context, getString(R.string.universal_help_url));
                    }
                });
    }


    @Override
    public void showReportUniversalFailed(String errorCode) {
        final Context context = this;
        if (maDialog != null) {
            maDialog.dismiss();
        }
        String message = getString(R.string.could_not_report_to_cloud) + "(" + errorCode + ")";
        maDialog = MADialog.showDialogWithType(DialogTypes.ERROR, this, message, null, getString(R.string.retry),
                getString(R.string.help), new MADialogHandler() {
                    @Override
                    public void onClickOK() {
                        if (maDialog != null) {
                            maDialog.dismiss();
                        }
                        handleUniversalLinkRequest();
                    }

                    @Override
                    public void onClickCancel() {
                        CommonUtil.openBrowser(context, getString(R.string.universal_help_url));
                    }
                });
    }

    @Override
    public void showReportUniversalSuccessfully() {
        if (maDialog != null) {
            maDialog.dismiss();
        }
        String message = getString(R.string.report_to_cloud_complete);
        String titleOk = getString(R.string.close);
        if (!StringUtil.isEmpty(appIdUniversal) || !StringUtil.isEmpty(urlUniversal)) {
            titleOk = getString(R.string.return_);
            if (!StringUtil.isEmpty(appNameUniversal)) {
                message = getString(R.string.report_to_cloud_complete_with_app_name).replace("$App", appNameUniversal);
            }
        }

        maDialog = MADialog.showDialogWithType(DialogTypes.INFO, this, message, null, titleOk,
                null, new MADialogHandler() {
                    @Override
                    public void onClickOK() {
                        if (!StringUtil.isEmpty(appIdUniversal)) {
                            if (!AppUtils.launchOtherApp(getContext(), appIdUniversal)) {
                                Toast.makeText(getContext(), getString(R.string.can_not_open_other_app),
                                        Toast.LENGTH_LONG).show();
                            }
                            return;
                        }

                        if (!StringUtil.isEmpty(urlUniversal)) {
                            if (!AppUtils.launchOtherAppByScheme(getContext(), urlUniversal)) {
                                Toast.makeText(getContext(), getString(R.string.can_not_open_other_app),
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    }

                    @Override
                    public void onClickCancel() {
                    }
                });
    }

    private void bindingBackgroundService() {
        //Performance check service
        Intent alarmPerformanceCheck = new Intent(this, PerformanceCheckAlarmReceiver.class);
        boolean alarmRunning = (PendingIntent.getBroadcast(this, 0, alarmPerformanceCheck, PendingIntent.FLAG_NO_CREATE) != null);
        if (!alarmRunning) {
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmPerformanceCheck, 0);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null) {
                alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), MAContant.PERFORMANCE_CHECK_INTERVAL, pendingIntent);
            }
        }

        Intent alarmAppReminder = new Intent(this, MaReminderAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmAppReminder, PendingIntent.FLAG_NO_CREATE);
        alarmRunning = (pendingIntent != null);
        if (alarmRunning) {
            pendingIntent.cancel();

        }
        MASettingData settingData = new MASettingData(this);
        if(settingData.isNotifyReminder()) {
            pendingIntent = PendingIntent.getBroadcast(this, 0, alarmAppReminder, 0);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null) {
                long reminderTime = (long)settingData.reminderTime()* 24 * 3600 * 1000;
                long nextTime = SystemClock.elapsedRealtime() + reminderTime;
                alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, nextTime, reminderTime, pendingIntent);
            }
        }
    }

    private void bindingAccountInfo(boolean isRegisteredOnLoad) {
        View headerManaged = findViewById(R.id.main_header_managed_content);
        View headerUnManaged = findViewById(R.id.main_header_unmanaged_content);

        MACloudData maCloudData = MAApplication.getInstance().getMaCloudData();
        boolean registered = maCloudData.getAccountData() != null && maCloudData.getAccountData().isRegistered();
        headerUnManaged.setVisibility(registered ? View.GONE : View.VISIBLE);

        if (registered) {
            if (isRegisteredOnLoad) {
                presenter.registerOnLoaded();
            }
            String accountName = maCloudData.getAccountData().getAccountName();
            if (accountName != null) {
                TextView managedByTv = findViewById(R.id.main_header_tv_managed_by);
                managedByTv.setText(getString(R.string.your_device_managed_by).replace("$N", accountName));
            }
            MAApplication.getInstance().startMaCloudTimer();

            if (maCloudData.isOutOfToken()) {
                showOutOfTokenStatus();
            }
        }

        headerUnManaged.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - lastClickTime < TIME_DURING_CLICK) {
                    return;
                }
                lastClickTime = SystemClock.elapsedRealtime();
                showEnrollPage();
            }
        });
    }

    private void showEnrollPage() {
        EnrollActivity.startActivityForResult(this);
    }

    private void bindingRecycleView() {
        RecyclerView recyclerViewConnections = findViewById(R.id.home_recycleView_connections);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewConnections.setLayoutManager(layoutManager);
        recyclerViewConnections.setAdapter(connectionAdapter);
        recyclerViewConnections.setNestedScrollingEnabled(false);

        RecyclerView recyclerViewApplications = findViewById(R.id.home_recycleView_applications);
        LinearLayoutManager layoutManagerApps = new LinearLayoutManager(this);
        recyclerViewApplications.setLayoutManager(layoutManagerApps);
        recyclerViewApplications.setAdapter(applicationAdapter);
        recyclerViewApplications.setNestedScrollingEnabled(false);
    }

    private void reloadDeviceBuilder(boolean forceRefresh) {
        DeviceBuilder deviceBuilder = MAApplication.getInstance().getDeviceBuilder();
        if (forceRefresh) {
            deviceBuilder.buildDeviceInfo();
        }
        bindingDataSecurity(deviceBuilder.getSecurityDevice());
        bindingDataHealth(deviceBuilder.getHealthDevice());
        swipeRefreshLayout.setRefreshing(false);
    }

    private void bindingDataHealth(HealthDevice healthDevice) {
        //Storage
        Storage storage = healthDevice.getStorage();
        if (storage == null) {
            reloadDeviceBuilder(true);
        }
        setImageView(R.id.home_img_storage, storage.isGoodStatus() ? R.drawable.ic_free_storage_good
                : R.drawable.ic_free_storage_bad);
        int textColor = storage.isGoodStatus() ? R.color.color_compliant :
                R.color.color_non_compliant;

        String freeStorage = storage.getFreeMem() + " " +
                getString(R.string.mb);
        setTextView(R.id.home_tv_free_storage, freeStorage);
        setColorTextView(R.id.home_tv_free_storage, textColor);

        String totalStorage = storage.getTotalMem() + " " +
                getString(R.string.mb);
        setTextView(R.id.home_tv_total_storage, totalStorage);
        setColorTextView(R.id.home_tv_total_storage, textColor);

        //Available Mem
        Memory memory = healthDevice.getMemory();
        textColor = memory.isGoodStatus() ? R.color.color_compliant :
                R.color.color_non_compliant;
        setImageView(R.id.home_img_memory, memory.isGoodStatus() ? R.drawable.ic_memory_good
                : R.drawable.ic_memory_bad);

        String availableMem = memory.getAvailMem() + " " +
                getString(R.string.mb);
        setTextView(R.id.home_tv_memory, availableMem);
        setColorTextView(R.id.home_tv_memory, textColor);

        //Reboot Recency
        RebootRecency rebootRecency = healthDevice.getRebootRecency();
        textColor = rebootRecency.isGoodStatus() ? R.color.color_compliant :
                R.color.color_non_compliant;
        setImageView(R.id.home_img_reboot, rebootRecency.isGoodStatus() ?
                R.drawable.ic_reboot_good : R.drawable.ic_reboot_bad);

        String rebootTime = "";
        // Less than 1 day, display same the format : 1 hour 2 mininutes ago
        if ( (float)rebootRecency.getUpTimeByMillis()/ TimeUnit.DAYS.toMillis(1) < 1) {
            long upTimeByHrs = rebootRecency.getUpTimeByMillis()/ TimeUnit.HOURS.toMillis(1);
            long upTimeByMins = (rebootRecency.getUpTimeByMillis() / TimeUnit.MINUTES.toMillis(1)) - (upTimeByHrs * TimeUnit.HOURS.toMinutes(1));

            rebootTime =  getString(R.string.reboot_time_menu).replace("%h", upTimeByHrs+"").replace("%m", upTimeByMins+"");
            if (upTimeByHrs < 2)
                rebootTime = rebootTime.replace("hrs", "hr");
            if (upTimeByMins < 2)
                rebootTime = rebootTime.replace("mins", "min");
        }
        // More than or equal to 1 day, display the old format : 123 hours ago
        else {
            rebootTime = getString(R.string.reboot_time_by_hours).replace("%h", rebootRecency.getUpTime()+"");
        }
        setTextView(R.id.home_tv_reboot, rebootTime);
        setColorTextView(R.id.home_tv_reboot, textColor);

        //Battery
        Battery battery = healthDevice.getBattery();
        textColor = battery.isGoodStatus() ? R.color.color_compliant :
                R.color.color_non_compliant;
        setImageView(R.id.home_img_battery, battery.isGoodStatus() ?
                R.drawable.ic_battery_good : R.drawable.ic_battery_bad);
        String batteryPercent = battery.getPercent() +
                getString(R.string.percent);
        setTextView(R.id.home_tv_battery_level, batteryPercent);
        setColorTextView(R.id.home_tv_battery_level, textColor);

        setTextView(R.id.home_tv_battery_charging, battery.isCharging() ?
                getString(R.string.battery_is_charging) : getString(R.string.battery_not_charging));
        setColorTextView(R.id.home_tv_battery_charging, textColor);

        //Hardware
        //TODO: How can we check hardware is good or bad?
        Hardware hardware = healthDevice.getHardware();
        textColor = hardware.isGoodStatus() ? R.color.color_compliant :
                R.color.color_non_compliant;
        String cpuModel = hardware.getProcessorName();
        setTextView(R.id.home_tv_cpu_model, cpuModel);
        setColorTextView(R.id.home_tv_cpu_model, textColor);

        String cpuSpeed = hardware.getCpuSpeed() + " " +
                getString(R.string.mhz);
        setTextView(R.id.home_tv_cpu_speed, cpuSpeed);
        setColorTextView(R.id.home_tv_cpu_speed, textColor);

        String core = hardware.getCores() + " "
                + getString(R.string.cores);
        setTextView(R.id.home_tv_cores, core);
        setColorTextView(R.id.home_tv_cores, textColor);

        String totalMem = memory.getTotalMem() + " " +
                getString(R.string.mb);
        setTextView(R.id.home_tv_total_mem, totalMem);
        setColorTextView(R.id.home_tv_total_mem, textColor);
    }

    private void bindingDataSecurity(SecurityDevice securityDevice) {
        //Jailbreak
        Jailbreak jailbreak = securityDevice.getJaibreak();
        if (jailbreak == null) {
            DeviceBuilder deviceBuilder = MAApplication.getInstance().getDeviceBuilder();
            deviceBuilder.buildDeviceInfo();
            jailbreak = securityDevice.getJaibreak();
        }

        setImageView(R.id.home_img_jailbreak, jailbreak.isAuthentic() ?
                R.drawable.ic_jailbreak_good : R.drawable.ic_jailbreak_bad);

        //Password
        PasswordAndLockScreen passwordAndLockScreen = securityDevice.getPasswordAndLockScreen();
        setImageView(R.id.home_img_password, passwordAndLockScreen.isProtected() ?
                R.drawable.ic_password_lock_good : R.drawable.ic_password_lock_bad);

        String passwordEnable = passwordAndLockScreen.isProtected() ? getString(R.string.enable) :
                getString(R.string.disable);
        int textColor = passwordAndLockScreen.isProtected() ? R.color.color_compliant :
                R.color.color_non_compliant;

        setTextView(R.id.home_tv_password_enable, passwordEnable);
        setColorTextView(R.id.home_tv_password_enable, textColor);

        String type = passwordAndLockScreen.getLockType();
        //findViewById(R.id.home_view_password_type).setVisibility(type.equals("none") ? View.GONE : View.VISIBLE);
        setTextView(R.id.home_tv_password_type, type);
        setColorTextView(R.id.home_tv_password_type, textColor);

        //Os up to date:
        OsUpToDate osUpToDate = securityDevice.getOsUpToDate();
        String version = getString(R.string.android) + " " + osUpToDate.getOsVersion();
        setTextView(R.id.home_tv_os, version);

        //Ad tracking
        AdTracking adTracking = securityDevice.getAdTracking();
        setImageView(R.id.home_img_ad_tracking, adTracking.isEnable() ?
                R.drawable.ic_ad_tracking_good : R.drawable.ic_ad_tracking_bad);

        setTextView(R.id.home_tv_ad_tracking, adTracking.isEnable() ?
               getString(R.string.restrict) :  getString(R.string.unrestrict));

        textColor = adTracking.isEnable() ? R.color.color_compliant : R.color.color_non_compliant;
        setColorTextView(R.id.home_tv_ad_tracking, textColor);

        //Encrypt:
        Encryption encryption = securityDevice.getEncryption();
        setImageView(R.id.home_img_encrypt, encryption.isGoodStatus() ?
                R.drawable.ic_encrypt_good : R.drawable.ic_encrypt_bad);

        textColor = encryption.isGoodStatus() ? R.color.color_compliant : R.color.color_non_compliant;
        setColorTextView(R.id.home_tv_encrypted, textColor);
        setTextView(R.id.home_tv_encrypted, encryption.getStateName());
    }

    private void bindingCollapseView() {
        final View[] headerViewArr = {findViewById(R.id.home_password_header), findViewById(R.id.home_os_header),
                findViewById(R.id.home_ad_tracking_header), findViewById(R.id.home_encrypt_header),
                findViewById(R.id.home_free_storage_header), findViewById(R.id.home_available_mem_header),
                findViewById(R.id.home_reboot_header), findViewById(R.id.home_battery_header),
                findViewById(R.id.home_hardware_header), findViewById(R.id.home_ip_scanning_header),
                findViewById(R.id.home_applications_header)};

        final View[] contentViewArr = {findViewById(R.id.home_password_content), findViewById(R.id.home_os_content),
                findViewById(R.id.home_ad_tracking_content), findViewById(R.id.home_encrypt_content),
                findViewById(R.id.home_free_storage_content), findViewById(R.id.home_memory_content),
                findViewById(R.id.home_reboot_content), findViewById(R.id.home_battery_content),
                findViewById(R.id.home_hardware_content), findViewById(R.id.home_connection_content),
                findViewById(R.id.home_application_content)};

        // Android 10 SDK 29 or higher
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            int scanIPConnectionIndex = 9;
            headerViewArr[scanIPConnectionIndex].setVisibility(View.GONE);
            contentViewArr[scanIPConnectionIndex].setVisibility(View.GONE);
        }

        for (int idx = 0; idx < headerViewArr.length; idx++) {
            View header = headerViewArr[idx];
            final int indexView = idx;
            final ScrollView scrollView = findViewById(R.id.home_scroll_view);
            header.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    View content = contentViewArr[indexView];
                    int visible = content.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE;
                    contentViewArr[indexView].setVisibility(visible);
                    if (visible == View.VISIBLE) {
                        scrollView.post(new Runnable() {
                            @Override
                            public void run() {
                                int offsetY = scrollView.getScrollY() + 100;
                                scrollView.scrollTo(scrollView.getScrollX(), offsetY);
                            }
                        });
                    }
                }
            });
        }

    }

    private void bindingViewDetailButton() {
        View homeContent = findViewById(R.id.home_content);
        View securityFragment = homeContent.findViewById(R.id.home_content_security);
        View healthFragment = homeContent.findViewById(R.id.home_content_health);

        final View[] arrowArr = {securityFragment.findViewById(R.id.home_btn_jailbreak_detail),
                securityFragment.findViewById(R.id.home_btn_password_detail),
                securityFragment.findViewById(R.id.home_btn_os_detail),
                securityFragment.findViewById(R.id.home_btn_ad_tracking_detail),
                securityFragment.findViewById(R.id.home_btn_encryption_detail),
                healthFragment.findViewById(R.id.home_btn_storage_detail),
                healthFragment.findViewById(R.id.home_btn_memory_detail),
                healthFragment.findViewById(R.id.home_btn_reboot_detail),
                healthFragment.findViewById(R.id.home_btn_battery_detail),
                healthFragment.findViewById(R.id.home_btn_hardware_detail)};
        final Context context = this;
        for (final View arrow : arrowArr) {
            arrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - lastClickTime < TIME_DURING_CLICK) {
                        return;
                    }
                    lastClickTime = SystemClock.elapsedRealtime();
                    String tag = arrow.getTag().toString();
                    DetailActivity.start(context, tag);
                }
            });
        }

        final Button btnConnections = securityFragment.findViewById(R.id.home_btn_scan_connections);
        btnConnections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - lastClickTime < TIME_DURING_CLICK) {
                    return;
                }
                lastClickTime = SystemClock.elapsedRealtime();
                bindingConnectionScanning();
            }
        });

        btnConnections.setVisibility(MAApplication.getInstance().isScanningIp ? View.GONE : View.VISIBLE);
        findViewById(R.id.home_btn_connection_detail).setVisibility(!MAApplication.getInstance().isScanningIp ?
                View.GONE : View.VISIBLE);

        final Button btnApplications = securityFragment.findViewById(R.id.home_btn_scan_applications);
        btnApplications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - lastClickTime < TIME_DURING_CLICK) {
                    return;
                }
                lastClickTime = SystemClock.elapsedRealtime();
                bindingApplicationScanning();
            }
        });

        btnApplications.setVisibility(MAApplication.getInstance().isScanningApp ? View.GONE : View.VISIBLE);
        findViewById(R.id.home_btn_application_detail).setVisibility(!MAApplication.getInstance().isScanningApp ?
                View.GONE : View.VISIBLE);

        View btnConnectionDetail = securityFragment.findViewById(R.id.home_btn_connection_detail);
        btnConnectionDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - lastClickTime < TIME_DURING_CLICK) {
                    return;
                }
                lastClickTime = SystemClock.elapsedRealtime();
                ConnectionActivity.start(context);
            }
        });

        View btnApplicationDetail = securityFragment.findViewById(R.id.home_btn_application_detail);
        btnApplicationDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - lastClickTime < TIME_DURING_CLICK) {
                    return;
                }
                lastClickTime = SystemClock.elapsedRealtime();
                ApplicationActivity.start(context);
            }
        });
    }

    @Override
    public void handleAppScanning() {
        super.handleAppScanning();
        bindingInfectedApps(MAApplication.getInstance().applications);
    }

    public void bindingApplicationScanning(){
        if (NetworkUtils.isGoodConnection(this)) {
            scanApplication();
            return;
        }
        MADialogHandler handler = new MADialogHandler() {
            @Override
            public void onClickOK() {
                if (maDialog != null)
                    maDialog.dismiss();
            }

            @Override
            public void onClickCancel() {
                scanApplication();
            }
        };
        showDialogWarningNetwork(getString(R.string.warning), getString(R.string.application_scanning_warning), handler);

    }
    private void scanApplication() {
        //Check network
        if (!NetworkUtils.isNetworkConnected(this)) {
            handleNoNetworkConnection();
            return;
        }
        ApplicationDBHelper dbHelper = MAApplication.getInstance().getDeviceBuilder().getApplicationScanning().getDbHelper();
        List<Application> applications = ApplicationScanHelper.getInstalledApplication(getApplicationContext(), true);
        if (System.currentTimeMillis() - dbHelper.getLastTimeScanApplication() < MAContant.INTERVAL_KEEP_SCAN_APPLICATION_RESULT){
            applications = MAApplication.getInstance().getDeviceBuilder().getApplicationScanning()
                            .getListApplicationsWithStatus(ApplicationStatus.UNKNOWN, ApplicationStatus.INPROGRESS);
        }
        else {
            dbHelper.resetAllResult();

        }
        MAApplication.getInstance().startApplicationTimer(applications);
        Button btnScan = findViewById(R.id.home_btn_scan_applications);
        btnScan.setVisibility(View.GONE);
        View btnDetail = findViewById(R.id.home_btn_application_detail);
        btnDetail.setVisibility(View.VISIBLE);
    }

    private void bindingInfectedApps(List<Application> applications) {
        List<Application> infectedApps = new ArrayList<>();
        for (Application application : applications) {
            if (application.getStatus() == ApplicationStatus.INFECTED || application.getStatus() == ApplicationStatus.SUSPECT) {
                infectedApps.add(application);
            }
        }

        if (!infectedApps.isEmpty()) {
            applicationAdapter.setListApplications(infectedApps);
            findViewById(R.id.home_tv_no_dirty_app).setVisibility(View.GONE);
        } else {
            findViewById(R.id.home_tv_no_dirty_app).setVisibility(View.VISIBLE);
            applicationAdapter.setListApplications(new ArrayList<Application>());
        }

        setImageView(R.id.home_img_application, infectedApps.isEmpty() ? R.drawable.ic_application_good :
                R.drawable.ic_application_bad);
    }

    @Override
    public void handleIpConnection() {
        super.handleIpConnection();
        bindingDirtyIps(MAApplication.getInstance().connections);
    }

    public void bindingConnectionScanning() {
        if (NetworkUtils.isGoodConnection(this)) {
            startScanConnections();
            return;
        }
        MADialogHandler handler = new MADialogHandler() {
            @Override
            public void onClickOK() {
                if (maDialog != null)
                    maDialog.dismiss();
            }

            @Override
            public void onClickCancel() {
                startScanConnections();
            }
        };
        showDialogWarningNetwork(getString(R.string.warning),getString(R.string.ip_scanning_warning), handler);
    }

    private void startScanConnections() {
        //Check network
        if (!NetworkUtils.isNetworkConnected(this)) {
            handleNoNetworkConnection();
            return;
        }
        MAApplication.getInstance().startConnectionTimer();
        View btnDetail = findViewById(R.id.home_btn_connection_detail);
        btnDetail.setVisibility(View.VISIBLE);
        Button btnScan = findViewById(R.id.home_btn_scan_connections);
        btnScan.setVisibility(View.GONE);
    }

    private void bindingDirtyIps(List<Connection> connections) {
        List<Connection> dirtyIps = new ArrayList<>();
        for (Connection connection : connections) {
            if (connection.getLookupResults() != null) {
                if (connection.getLookupResults().getDetectedBy() > 0) {
                    dirtyIps.add(connection);
                }
            }
        }

        if (!dirtyIps.isEmpty()) {
            connectionAdapter.setListIps(dirtyIps);
            findViewById(R.id.home_tv_no_dirty_ip).setVisibility(View.GONE);
        } else {
            findViewById(R.id.home_tv_no_dirty_ip).setVisibility(View.VISIBLE);
            connectionAdapter.setListIps(new ArrayList<Connection>());
        }

        setImageView(R.id.home_img_connection, dirtyIps.isEmpty() ? R.drawable.ic_connection_good :
                R.drawable.ic_connection_bad);
    }

    private void bindingBottomTab() {
        final Button[] arrayTabs = {findViewById(R.id.home_btn_security), findViewById(R.id.home_btn_health)};
        final View[] arrayContentView = {findViewById(R.id.home_content_security), findViewById(R.id.home_content_health)};
        final ScrollView scrollView = findViewById(R.id.home_scroll_view);

        for (int idx = 0; idx < arrayTabs.length; idx++) {
            final int idxBtn = idx;
            arrayTabs[idx].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for (int idxView = 0; idxView < arrayContentView.length; idxView++) {
                        int visibility = (idxBtn == idxView) ? View.VISIBLE : View.GONE;
                        arrayContentView[idxView].setVisibility(visibility);
                        Context context = getApplicationContext();

                        int colorBackground = (idxBtn == idxView) ?
                                ColorUtil.getColor(context, R.color.colorPrimary) :
                                ColorUtil.getColor(context, R.color.background_gray);
                        arrayTabs[idxView].setBackgroundColor(colorBackground);

                        int colorText = (idxBtn == idxView) ?
                                ColorUtil.getColor(context, R.color.white) :
                                ColorUtil.getColor(context, R.color.color_text_gray);
                        arrayTabs[idxView].setTextColor(colorText);

                        scrollView.fullScroll(ScrollView.FOCUS_UP);
                    }
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (MAApplication.getInstance().isScanningIp) {
            handleIpConnection();
        }

        if (MAApplication.getInstance().isScanningApp) {
            handleAppScanning();
        }

        bindingAccountInfo(false);
        bindingUnRegisterBtn();
    }

    @Override
    public void bindingRegistered() {
        if (maDialog != null) {
            maDialog.dismiss();
        }
        maDialog = MADialog.showDialogWithType(DialogTypes.INFO, this, getString(R.string.register_successfully),
                null, null, null, null);
        bindingAccountInfo(false);
        bindingUnRegisterBtn();
    }
    @Override
    public void bindingUpdatedAccountData() {
        bindingAccountInfo(false);
    }

    @Override
    public void handleRegisterWithNoNetwork() {
        if (maDialog != null) {
            maDialog.dismiss();
        }
        maDialog = MADialog.showDialogWithType( DialogTypes.ERROR, this, getString(R.string.error_register_title),
                getString(R.string.error_no_connection), null, null, null);
    }

    @Override
    public void showDialogNotFoundRegCode() {
        if (maDialog != null) {
            maDialog.dismiss();
        }
        maDialog = MADialog.showDialogWithType( DialogTypes.ERROR, this, getString(R.string.error_register_title),
                getString(R.string.error_register_not_found_reg_code), null, null, null);
    }

    public void showDialogWarningNetwork(String title, String message, MADialogHandler handler) {
        if (maDialog != null) {
            maDialog.dismiss();
        }
        maDialog = MADialog.showDialogWithType(DialogTypes.WARNING, this, title, message, getString(R.string.cancel), getString(R.string.eula_content_accept), handler);
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MAContant.REQUEST_CODE_WRITE_STORAGE || requestCode == MAContant.REQUEST_CODE_READ_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                MACloudData maCloudData = MAApplication.getInstance().getMaCloudData();
                maCloudData.loadMaCloudData(this);
                bindingAccountInfo(true);
            }
        }
    }

    @Override
    public void showDialogOutOfToken() {
        super.showDialogOutOfToken();
    }

    @Override
    public void resetAccountInfo() {
        super.resetAccountInfo();
        bindingAccountInfo(false);
    }

    @Override
    public void handlePolicyCheck(PolicyCheckResponse policyCheckResponse) {
        super.handlePolicyCheck(policyCheckResponse);
        boolean isNonCompliant = (MAContant.WARNING_STATUS.equalsIgnoreCase(policyCheckResponse.getStatus()) ||
                MAContant.CRITICAL_STATUS.equalsIgnoreCase(policyCheckResponse.getStatus()));
        ImageView imgCompliant = findViewById(R.id.main_header_img_compliant);
        imgCompliant.setImageResource((isNonCompliant) ? R.drawable.ic_non_compliant : R.drawable.ic_compliant);

        TextView tvCompliant = findViewById(R.id.main_header_tv_compliant);
        String compliantText = isNonCompliant ? getString(R.string.your_device_non_compliant) :
                getString(R.string.your_device_compliant);
        tvCompliant.setText(compliantText);
    }

    @Override
    public void handleOutOfToken() {
        super.handleOutOfToken();
        showOutOfTokenStatus();
    }

    private void showOutOfTokenStatus() {
        ImageView imgCompliant = findViewById(R.id.main_header_img_compliant);
        imgCompliant.setImageResource(R.drawable.out_of_token);

        MACloudData maCloudData = MAApplication.getInstance().getMaCloudData();
        String accountName = maCloudData.getAccountData().getAccountName();
        String description = getString(R.string.your_device_out_of_token).replace("${name}", accountName);
        TextView tvCompliant = findViewById(R.id.main_header_tv_compliant);
        tvCompliant.setText(description);
    }

    @Override
    public void handlePolicyCheckResponse(PolicyCheckResponse policyCheckResponse) {
        handlePolicyCheck(policyCheckResponse);
    }
}
