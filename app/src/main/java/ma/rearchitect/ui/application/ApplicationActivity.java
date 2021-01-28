package opswat.com.flow.application;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import opswat.com.R;
import opswat.com.adapter.ApplicationAdapter;
import opswat.com.constant.MAContant;
import opswat.com.device.application.ApplicationDBHelper;
import opswat.com.device.application.ApplicationScanHelper;
import opswat.com.enums.ApplicationStatus;
import opswat.com.flow.base.BaseActivity;
import opswat.com.mvp.MvpPresenter;
import opswat.com.network.model.application.Application;
import opswat.com.util.DateUtil;
import opswat.com.util.NetworkUtils;

public class ApplicationActivity extends BaseActivity implements IApplicationView {
    private IApplicationPresenter presenter = new ApplicationPresenterIml();

    private RecyclerView recyclerViewScanning;
    private RecyclerView recyclerViewClean;
    private RecyclerView recyclerViewSuspect;
    private RecyclerView recyclerViewDirty;
    private RecyclerView recyclerViewUnknown;

    private ApplicationAdapter scanningAdapter = new ApplicationAdapter(ApplicationAdapter.TYPE_APP_DETAIL);
    private ApplicationAdapter cleanAdapter = new ApplicationAdapter(ApplicationAdapter.TYPE_APP_DETAIL);
    private ApplicationAdapter suspectAdapter = new ApplicationAdapter(ApplicationAdapter.TYPE_APP_DETAIL);
    private ApplicationAdapter dirtyAdapter = new ApplicationAdapter(ApplicationAdapter.TYPE_APP_DETAIL);
    private ApplicationAdapter unknownAdapter = new ApplicationAdapter(ApplicationAdapter.TYPE_APP_DETAIL);

    @Override
    protected MvpPresenter getPresenter() {
        return presenter;
    }

    @Override
    protected Integer getBtnMenuId() {
        return R.id.applications_right_menu;
    }

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_application;
    }

    @Override
    protected Integer getSlideMenuId() {
        return R.id.applications_slide_menu;
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, ApplicationActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void initialized() {
        initRecycleView();
        handleAppScanning();
        findViewById(R.id.applications_btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backHomePage();
            }
        });

        setTextView(R.id.applications_tv_title_name, getString(R.string.applications));

        findViewById(R.id.applications_btn_rescan_applications).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rescanApplications();
            }
        });
    }

    public void rescanApplications() {
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
    }

    @Override
    public void handleAppScanning() {
        super.handleAppScanning();
        bindingListApplications(MAApplication.getInstance().applications);
    }

    private void backHomePage() {
        this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void bindingListApplications(List<Application> applications) {
        List<Application> scanningApps = new ArrayList<>(), cleanApps = new ArrayList<>(),
                suspectApps = new ArrayList<>(), infectedApps = new ArrayList<>(), unknownApps = new ArrayList<>();
        ApplicationStatus status = ApplicationStatus.CLEAN;
        boolean isCompleted = true;
        int numOfInfected = 0, numOfSuspect = 0;

        for (Application application : applications) {
            switch (application.getStatus()) {
                case CLEAN:
                    cleanApps.add(application);
                    break;
                case INPROGRESS:
                    scanningApps.add(application);
                    isCompleted = false;
                    break;
                case INFECTED:
                    infectedApps.add(application);
                    status = ApplicationStatus.INFECTED;
                    numOfInfected++;
                    break;
                case UNKNOWN:
                    unknownApps.add(application);
                    break;
                case SUSPECT:
                    suspectApps.add(application);
                    status = (status == ApplicationStatus.INFECTED) ? status : ApplicationStatus.SUSPECT;
                    numOfSuspect++;
                    break;
            }
        }

        setImageView(R.id.applications_img_ic_status, (numOfInfected + numOfSuspect == 0) ?
                R.drawable.ic_good : R.drawable.ic_bad);

        findViewById(R.id.applications_view_result).setVisibility(isCompleted ? View.VISIBLE : View.GONE);
        if (isCompleted) {
            int colorText = (status == ApplicationStatus.SUSPECT || status == ApplicationStatus.INFECTED) ?
                    R.color.color_non_compliant : R.color.color_compliant;
            setColorTextView(R.id.applications_tv_last_scanned, colorText);
            setColorTextView(R.id.applications_tv_result, colorText);

            int numOfThreatsFound = numOfInfected + numOfSuspect;
            String resultText = (status == ApplicationStatus.CLEAN) ? getString(R.string.no_threats_found) :
                    getString(R.string.threat_found).replace("$Num", (numOfThreatsFound) + "");
            if (numOfThreatsFound == 1)
                resultText = resultText.replace("threats", "threat");
            setTextView(R.id.applications_tv_result, resultText);

            Application firstApp = (applications.isEmpty()) ? null : applications.get(0);
            String latestScanned = getString(R.string.latested_scanned) +
                    ((firstApp == null) ? "" : DateUtil.getFullFormatDate(firstApp.getLatestedTime()));
            setTextView(R.id.applications_tv_last_scanned, latestScanned);
        }

        recyclerViewScanning.setVisibility(scanningApps.isEmpty() ? View.GONE : View.VISIBLE);
        findViewById(R.id.applications_tv_scanning).setVisibility(scanningApps.isEmpty() ? View.GONE : View.VISIBLE);
        findViewById(R.id.applications_line_scanning).setVisibility(scanningApps.isEmpty() ? View.GONE : View.VISIBLE);
        scanningAdapter.setListApplications(scanningApps);

        recyclerViewClean.setVisibility(cleanApps.isEmpty() ? View.GONE : View.VISIBLE);
        findViewById(R.id.applications_tv_no_threats_found).setVisibility(cleanApps.isEmpty() ? View.GONE : View.VISIBLE);
        findViewById(R.id.applications_line_clean).setVisibility(cleanApps.isEmpty() ? View.GONE : View.VISIBLE);
        cleanAdapter.setListApplications(cleanApps);

        recyclerViewSuspect.setVisibility(suspectApps.isEmpty() ? View.GONE : View.VISIBLE);
        findViewById(R.id.applications_tv_suspect).setVisibility(suspectApps.isEmpty() ? View.GONE : View.VISIBLE);
        findViewById(R.id.applications_line_suspect).setVisibility(suspectApps.isEmpty() ? View.GONE : View.VISIBLE);
        suspectAdapter.setListApplications(suspectApps);

        recyclerViewDirty.setVisibility(infectedApps.isEmpty() ? View.GONE : View.VISIBLE);
        findViewById(R.id.applications_tv_dirty).setVisibility(infectedApps.isEmpty() ? View.GONE : View.VISIBLE);
        findViewById(R.id.applications_line_dirty).setVisibility(infectedApps.isEmpty() ? View.GONE : View.VISIBLE);
        dirtyAdapter.setListApplications(infectedApps);

        recyclerViewUnknown.setVisibility(unknownApps.isEmpty() ? View.GONE : View.VISIBLE);
        findViewById(R.id.applications_tv_unknown).setVisibility(unknownApps.isEmpty() ? View.GONE : View.VISIBLE);
        findViewById(R.id.applications_line_unknown).setVisibility(unknownApps.isEmpty() ? View.GONE : View.VISIBLE);
        unknownAdapter.setListApplications(unknownApps);
    }

    private void initRecycleView() {
        recyclerViewScanning = findViewById(R.id.applications_recycleView_scanning);
        recyclerViewClean = findViewById(R.id.applications_recycleView_clean);
        recyclerViewSuspect = findViewById(R.id.applications_recycleView_suspect);
        recyclerViewDirty = findViewById(R.id.applications_recycleView_dirty);
        recyclerViewUnknown = findViewById(R.id.applications_recycleView_unknown);

        RecyclerView[] recycleViewArr = {recyclerViewScanning, recyclerViewClean, recyclerViewSuspect, recyclerViewDirty, recyclerViewUnknown};
        ApplicationAdapter[] applicationAdapterArr = {scanningAdapter, cleanAdapter, suspectAdapter, dirtyAdapter, unknownAdapter};
        LinearLayoutManager[] layoutManagerArr = {new LinearLayoutManager(this), new LinearLayoutManager(this),
                new LinearLayoutManager(this), new LinearLayoutManager(this), new LinearLayoutManager(this)};

        for (int idx = 0; idx < recycleViewArr.length; idx++) {
            recycleViewArr[idx].setLayoutManager(layoutManagerArr[idx]);
            recycleViewArr[idx].setAdapter(applicationAdapterArr[idx]);
            recycleViewArr[idx].setNestedScrollingEnabled(false);
        }

        findViewById(R.id.applications_scrollbar).scrollTo(0, 0);
    }
}
