package ma.rearchitect.data.device.model;

import ma.rearchitect.data.device.DeviceDefine;

/**
 * Created by LenVo on 7/13/18.
 */

public class RebootRecency extends DeviceModel{
    private long upTime; //device up time time by hours
    private long lastReboot; // last reboot time by unix timestamp
    private long upTimeByMillis; // device up time by milliseconds

    @Override
    public String getType() {
        return DeviceDefine.REBOOT_KEY;
    }

    public boolean isGoodStatus() {
        return upTime < 7*24;
    }

    public long getUpTime() {
        return upTime;
    }

    public long getUpTimeByMillis() {
        return upTimeByMillis;
    }

    public void setUpTimeByMillis(long upTimeByMillis) {
        this.upTimeByMillis = upTimeByMillis;
    }

    public void setUpTime(long upTime) {
        this.upTime = upTime;
    }

    public long getLastReboot() {
        return lastReboot;
    }

    public void setLastReboot(long lastReboot) {
        this.lastReboot = lastReboot;
    }

}
