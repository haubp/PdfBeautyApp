package ma.rearchitect.data.device;

import android.content.Context;

import ma.rearchitect.data.device.application.ApplicationScanning;
import ma.rearchitect.data.device.connection.ScanIpConnectionThread;
import ma.rearchitect.data.device.helper.DeviceInfoHelper;
import ma.rearchitect.data.device.helper.HealthHelper;
import ma.rearchitect.data.device.helper.SecurityHelper;
import ma.rearchitect.data.device.model.DeviceInfo;

/**
 * Created by LenVo on 7/13/18.
 */

public class DeviceBuilder {
    private HealthDevice healthDevice;
    private SecurityDevice securityDevice;
    private Context context;
    private ScanIpConnectionThread scanIpConnectionThread;
    private ApplicationScanning applicationScanning;
    private static DeviceBuilder deviceBuilder;
    private DeviceInfo deviceInfo;
    private boolean initilazed = false;

    private DeviceBuilder() {}

    private DeviceBuilder(Context context) {
        this.context = context;
        securityDevice = new SecurityDevice();
        healthDevice = new HealthDevice();
        scanIpConnectionThread = new ScanIpConnectionThread();
        applicationScanning = new ApplicationScanning(context);
        deviceInfo = new DeviceInfo();
    }

    public static DeviceBuilder getDeviceBuilder(Context context) {
        if (deviceBuilder == null) {
            deviceBuilder = new DeviceBuilder(context);
        }
        if (!deviceBuilder.initilazed) {
            deviceBuilder.buildDeviceInfo();
        }
        return deviceBuilder;
    }

    public void buildDeviceInfo() {
        initilazed = true;
        buildHealthDevice();
        buildSecurityDevice();
        DeviceInfoHelper deviceInfoHelper = new DeviceInfoHelper(context);
        deviceInfo = deviceInfoHelper.getDeviceInfo();
    }

    public HealthDevice getHealthDevice() {
        return healthDevice;
    }

    public SecurityDevice getSecurityDevice() {
        return securityDevice;
    }

    public DeviceInfo getDeviceInfo() {
        return deviceInfo;
    }

    private void buildSecurityDevice() {
        SecurityHelper securityHelper = new SecurityHelper(context);
        securityDevice.setJaibreak(securityHelper.getJaibreak());
        securityDevice.setAdTracking(securityHelper.getAdTracking());
        securityDevice.setEncryption(securityHelper.getEncryption());
        securityDevice.setOsUpToDate(securityHelper.getOsUpToDate());
        securityDevice.setPasswordAndLockScreen(securityHelper.getPasswordAndLockScreen());
    }

    private void buildHealthDevice() {
        HealthHelper healthHelper = new HealthHelper(context);
        healthDevice.setStorage(healthHelper.getStorage());
        healthDevice.setBattery(healthHelper.getBattery());
        healthDevice.setHardware(healthHelper.getHardware());
        healthDevice.setMemory(healthHelper.getMemory());
        healthDevice.setRebootRecency(healthHelper.getRebootRecency());
    }

    public ScanIpConnectionThread getScanIpConnectionThread() {
        return scanIpConnectionThread;
    }

    public ApplicationScanning getApplicationScanning() {
        return applicationScanning;
    }

    public Context getContext() {
        return context;
    }
}
