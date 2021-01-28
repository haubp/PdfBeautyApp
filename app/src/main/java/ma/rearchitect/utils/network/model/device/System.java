package opswat.com.network.model.device;

import com.google.gson.annotations.SerializedName;

import opswat.com.network.model.device.system.Battery;
import opswat.com.network.model.device.system.Cpu;
import opswat.com.network.model.device.system.DiskSpace;
import opswat.com.network.model.device.system.Encryption;
import opswat.com.network.model.device.system.Memory;

/**
 * Created by H. Len Vo on 9/11/18.
 */
public class System {
    private Cpu cpu;

    @SerializedName("disk_space")
    private DiskSpace diskSpace;

    private Memory memory;
    private Battery battery;

    @SerializedName("device_up_time")
    private long upTime;

    private boolean passcode;
    private boolean authentic;

    @SerializedName("passcode_type")
    private String passcodeType;

    @SerializedName("wifi_enabled")
    private boolean wifiEnable;

    @SerializedName("prevent_ad_tracking")
    private boolean adTracking;

    @SerializedName("encryption")
    private Encryption encryption;

    @SerializedName("last_reboot")
    private long lastReboot;

    public long getLastReboot() {
        return lastReboot;
    }

    public void setLastReboot(long lastReboot) {
        this.lastReboot = lastReboot;
    }

    public Cpu getCpu() {
        return cpu;
    }

    public void setCpu(Cpu cpu) {
        this.cpu = cpu;
    }

    public DiskSpace getDiskSpace() {
        return diskSpace;
    }

    public void setDiskSpace(DiskSpace diskSpace) {
        this.diskSpace = diskSpace;
    }

    public Memory getMemory() {
        return memory;
    }

    public void setMemory(Memory memory) {
        this.memory = memory;
    }

    public Battery getBattery() {
        return battery;
    }

    public void setBattery(Battery battery) {
        this.battery = battery;
    }

    public long getUpTime() {
        return upTime;
    }

    public void setUpTime(long upTime) {
        this.upTime = upTime;
    }

    public boolean isPasscode() {
        return passcode;
    }

    public void setPasscode(boolean passcode) {
        this.passcode = passcode;
    }

    public boolean isAuthentic() {
        return authentic;
    }

    public void setAuthentic(boolean authentic) {
        this.authentic = authentic;
    }

    public String getPasscodeType() {
        return passcodeType;
    }

    public void setPasscodeType(String passcodeType) {
        this.passcodeType = passcodeType;
    }

    public boolean isWifiEnable() {
        return wifiEnable;
    }

    public void setWifiEnable(boolean wifiEnable) {
        this.wifiEnable = wifiEnable;
    }

    public boolean isAdTracking() {
        return adTracking;
    }

    public void setAdTracking(boolean adTracking) {
        this.adTracking = adTracking;
    }

    public Encryption getEncryption() {
        return encryption;
    }

    public void setEncryption(Encryption encryption) {
        this.encryption = encryption;
    }

    @Override
    public String toString() {
        return "System{" +
                "cpu=" + cpu +
                ", diskSpace=" + diskSpace +
                ", memory=" + memory +
                ", battery=" + battery +
                ", upTime=" + upTime +
                ", passcode=" + passcode +
                ", authentic=" + authentic +
                ", passcodeType='" + passcodeType + '\'' +
                ", wifiEnable=" + wifiEnable +
                ", adTracking=" + adTracking +
                ", encryption=" + encryption +
                '}';
    }
}
