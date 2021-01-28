package ma.rearchitect.data.device.model;

import ma.rearchitect.data.device.DeviceDefine;

/**
 * Created by LenVo on 7/13/18.
 */

public class Battery extends DeviceModel{
    private int percent;
    private boolean isCharging;

    @Override
    public String getType() {
        return DeviceDefine.BATTERY_KEY;
    }

    public boolean isGoodStatus() {
        return percent > 10;
    }

    public int getPercent() {
        return percent;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }

    public boolean isCharging() {
        return isCharging;
    }

    public void setCharging(boolean charging) {
        isCharging = charging;
    }
}
