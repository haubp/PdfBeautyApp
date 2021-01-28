package ma.rearchitect.data.device.application;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

import opswat.com.enums.ApplicationStatus;
import opswat.com.network.model.application.Application;

/**
 * Created by LenVo on 7/15/18.
 */

public class ApplicationScanHelper {
    private static final int MAX_BUFFER = 1024*8;
    private static List<Application> applications = null;

    public static List<Application> getInstalledApplication(Context context, boolean forceRescan) {
        if (applications != null && !applications.isEmpty() && !forceRescan) {
            return applications;
        }
        applications = new ArrayList<>();

        try {
            final PackageManager pm = context.getPackageManager();
            List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

            for (ApplicationInfo applicationInfo : packages) {
                String packageName = applicationInfo.packageName;
                if (packageName.equals("com.android.gesture.builder")) {
                    continue;
                }

                if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0 ||
                        (applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
                    continue;
                }

                Application application = new Application();

                try {
                    PackageInfo info = pm.getPackageInfo(applicationInfo.packageName, 0);
                    String appName = pm.getApplicationLabel(applicationInfo).toString();
                    application.setPackageName(packageName);
                    application.setVersion(info.versionName == null ? "N/A" : info.versionName);
                    application.setAppName(appName);
                    application.setSourceDir(applicationInfo.sourceDir);
                    application.setStatus(ApplicationStatus.INPROGRESS);
                    applications.add(application);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return applications;
    }

    public static String getHashFile(String filePath) {
        Formatter formatter = new Formatter();
        try {
            final MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
            InputStream is = new BufferedInputStream(new FileInputStream(filePath));
            final byte[] buffer = new byte[MAX_BUFFER];
            for (int read; (read = is.read(buffer)) != -1;) {
                messageDigest.update(buffer, 0, read);
            }
            for (final byte b : messageDigest.digest())
                formatter.format("%02x", b);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return formatter.toString();
    }
}
