package ma.rearchitect.data.device.model;

import ma.rearchitect.data.device.DeviceDefine;

/**
 * Created by LenVo on 7/13/18.
 */

public class Jailbreak extends DeviceModel{
    private boolean isAuthentic;

    @Override
    public String getType() {
        return DeviceDefine.JAILBREAK_KEY;
    }

    public boolean isAuthentic() {
        return isAuthentic;
    }

    public void setAuthentic(boolean authentic) {
        isAuthentic = authentic;
    }
}
