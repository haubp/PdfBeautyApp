package opswat.com.network.helper;

import java.util.ArrayList;
import java.util.List;

import opswat.com.constant.MAContant;
import opswat.com.data.AccountData;
import opswat.com.data.MACloudData;
import opswat.com.data.UserIdentity;
import opswat.com.device.DeviceBuilder;
import opswat.com.device.HealthDevice;
import opswat.com.device.SecurityDevice;
import opswat.com.device.application.ApplicationScanning;
import opswat.com.device.model.DeviceInfo;
import opswat.com.network.model.application.Application;
import opswat.com.network.model.device.NetworkAdapter;
import opswat.com.network.model.device.OsInfo;
import opswat.com.network.model.device.Soh;
import opswat.com.network.model.device.System;
import opswat.com.network.model.device.application.ApplicationReport;
import opswat.com.network.model.device.system.Battery;
import opswat.com.network.model.device.system.Cpu;
import opswat.com.network.model.device.system.DiskSpace;
import opswat.com.network.model.device.system.Encryption;
import opswat.com.network.model.device.system.Memory;
import opswat.com.network.model.request.RegisterRequest;
import opswat.com.network.model.request.ReportRequest;
import opswat.com.network.model.response.FetchPrivacyResponse;
import opswat.com.util.AppUtils;
import opswat.com.util.StringUtil;

/**
 * Created by H. Len Vo on 8/23/18.
 */
public class MACloudFactory {
    public static RegisterRequest getRegisterRequest(AccountData accountData, DeviceBuilder builder, FetchPrivacyResponse privacy) {
        RegisterRequest request = new RegisterRequest();
        DeviceInfo deviceInfo = builder.getDeviceInfo();
        request.setDeviceType(deviceInfo.getDeviceType());
        request.setDeviceName(deviceInfo.getDeviceName());
        request.setAgentVersion(AppUtils.appVersion(builder.getContext()));

        if (!StringUtil.isEmpty(accountData.getDeviceId())) {
            request.setDeviceId(accountData.getDeviceId());
        }

        OsInfo osInfo = new OsInfo();
        osInfo.setDeviceType(deviceInfo.getDeviceType());
        osInfo.setFamily(deviceInfo.getOsFamily());
        osInfo.setName(deviceInfo.getOsName());
        osInfo.setOsLanguage(deviceInfo.getOsLanguage());
        osInfo.setVendor(deviceInfo.getOsVendor());
        osInfo.setVersion(deviceInfo.getOsVersion());

        List<NetworkAdapter> networkAdapters = new ArrayList<>();
        networkAdapters.add(new NetworkAdapter(deviceInfo.getNetworkAdapter()));
        osInfo.setNetworkAdapters(networkAdapters);
        request.setOsInfo(osInfo);
        request.setNetworkAdapters(networkAdapters);
        return request;
    }

    public static ReportRequest getReportRequest(MACloudData maCloudData, DeviceBuilder builder, FetchPrivacyResponse privacy) {
        ReportRequest request = new ReportRequest();
        SecurityDevice securityDevice = builder.getSecurityDevice();
        if (securityDevice.getJaibreak() == null) {
            builder.buildDeviceInfo();
            securityDevice = builder.getSecurityDevice();
        }
        HealthDevice healthDevice = builder.getHealthDevice();
        AccountData accountData = maCloudData.getAccountData();

        request.setAgentVersion(AppUtils.appVersion(builder.getContext()));
        DeviceInfo deviceInfo = builder.getDeviceInfo();
        request.setDeviceName((privacy.getHostName() == 1)? "" : deviceInfo.getDeviceName());

        request.setDeviceId(accountData.getDeviceId());

        //User Identity
        UserIdentity userIdentity = accountData.getUserIdentity();
        if (userIdentity != null && userIdentity.getEnable() == 1 && userIdentity.getUserInput() != null) {
            request.setUserIdentity(userIdentity.getUserInput());
        }

        if (!StringUtil.isEmpty(maCloudData.getTransactionId())) {
            request.setTransId(maCloudData.getTransactionId());
        }

        Soh soh = new Soh();

        //OsInfo
        List<OsInfo> osInfos = new ArrayList<>();
        OsInfo osInfo = new OsInfo();
        osInfo.setDeviceType(deviceInfo.getDeviceType());
        osInfo.setFamily(deviceInfo.getOsFamily());
        osInfo.setName(deviceInfo.getOsName());
        osInfo.setOsLanguage(deviceInfo.getOsLanguage());
        osInfo.setVendor(deviceInfo.getOsVendor());
        osInfo.setVersion(deviceInfo.getOsVersion());
        List<NetworkAdapter> networkAdapters = new ArrayList<>();
        networkAdapters.add(new NetworkAdapter(deviceInfo.getNetworkAdapter()));
        osInfo.setNetworkAdapters(networkAdapters);
        osInfos.add(osInfo);
        soh.setOsInfos(osInfos);

        //Applications
        List<ApplicationReport> applicationReports = new ArrayList<>();
        ApplicationScanning applicationScanning = builder.getApplicationScanning();
        List<Application> applications = applicationScanning.getListApplication();
        for (Application application: applications) {
            ApplicationReport applicationReport = new ApplicationReport();
            applicationReport.getProduct().setName(application.getAppName());
            applicationReport.getProduct().setVersion(application.getVersion());
            applicationReports.add(applicationReport);
        }
        soh.setApplicationReports(applicationReports);

        //System
        System system = new System();
        Cpu cpu = new Cpu();
        cpu.setMaxSpeed(healthDevice.getHardware().getCpuSpeed());
        cpu.setModel(healthDevice.getHardware().getProcessorName());
        system.setCpu(cpu);

        DiskSpace diskSpace = new DiskSpace();
        diskSpace.setAvailable(healthDevice.getStorage().getFreeMem());
        diskSpace.setTotal(healthDevice.getStorage().getTotalMem());
        system.setDiskSpace(diskSpace);

        Memory memory = new Memory();
        memory.setAvailable(healthDevice.getMemory().getAvailMem());
        memory.setTotal(healthDevice.getMemory().getTotalMem());
        system.setMemory(memory);

        Battery battery = new Battery();
        battery.setCharging(healthDevice.getBattery().isCharging());
        battery.setPercent(healthDevice.getBattery().getPercent());
        system.setBattery(battery);

        system.setUpTime(healthDevice.getRebootRecency().getUpTime());
        system.setLastReboot(healthDevice.getRebootRecency().getLastReboot());

        system.setPasscode(securityDevice.getPasswordAndLockScreen().isProtected());
        system.setAuthentic(securityDevice.getJaibreak().isAuthentic());
        system.setPasscodeType(securityDevice.getPasswordAndLockScreen().getType());
        system.setWifiEnable(deviceInfo.isWifiEnable());
        system.setAdTracking(securityDevice.getAdTracking().isEnable());

        Encryption encryption = new Encryption();
        encryption.setInternal(securityDevice.getEncryption().isGoodStatus());
        system.setEncryption(encryption);

        soh.setSystem(system);

        //Todo: Ip Scanning added dirty IP
        soh.setLastScanIp(maCloudData.getLastScanningIP());
        request.setSoh(soh);
        return request;
    }
}
