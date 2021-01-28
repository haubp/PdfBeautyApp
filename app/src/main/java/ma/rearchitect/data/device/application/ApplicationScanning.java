package ma.rearchitect.data.device.application;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import opswat.com.enums.ApplicationScanningStatus;
import opswat.com.enums.ApplicationStatus;
import opswat.com.network.model.application.Application;

/**
 * Created by LenVo on 7/18/18.
 */

public class ApplicationScanning {
    private final int NUM_THREADS = 2;
    private Context context;
    private ApplicationDBHelper dbHelper;
    private List<Runnable> threads;

    public ApplicationScanning(Context context) {
        this.context = context;
        this.dbHelper = new ApplicationDBHelper(context);
    }

    public List<Application> getListApplication() {
        List<Application> listApplications = new ArrayList<>();
        List<Application> applications = ApplicationScanHelper.getInstalledApplication(context, false);
        for (Application application: applications) {
            Application applicationDb = dbHelper.getApplication(application.getPackageName(), application.getVersion());
            listApplications.add((applicationDb == null)? application: applicationDb);
        }
        return listApplications;
    }

    public void startScanApplication(List<Application> applications) {
        if (applications.isEmpty()){
            dbHelper.setApplicationScanStatus(ApplicationScanningStatus.COMPLETED);
            return;
        }
        dbHelper.setApplicationScanStatus(ApplicationScanningStatus.IDLE);
        //Start scan application thread
        ApplicationScanningThread thread = new ApplicationScanningThread(dbHelper, applications);
        thread.start();
    }

    public List<Application> getListApplicationsWithStatus(ApplicationStatus... statuses){
        List<Application> applications =  getListApplication();
        if (statuses == null || statuses.length == 0){
            return applications;
        }
        List<Application> listApplications = new ArrayList<>();
        for (Application application : applications) {
            for (ApplicationStatus status : statuses) {
                if (application.getStatus() == status) {
                    listApplications.add(application);
                }
            }
        }
        return listApplications;
    }

    public ApplicationDBHelper getDbHelper() {
        return dbHelper;
    }
}
