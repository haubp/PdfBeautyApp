package ma.rearchitect.data.device.model;

import ma.rearchitect.data.device.DeviceDefine;

/**
 * Created by LenVo on 7/13/18.
 */

public class AdTracking extends DeviceModel{

    @Override
    public String getType() {
        return DeviceDefine.AD_TRACKING_KEY;
    }

    private boolean isEnable = true;

    public boolean isEnable() {
        return isEnable;
    }

    public void setEnable(boolean enable) {
        isEnable = enable;
    }
}
