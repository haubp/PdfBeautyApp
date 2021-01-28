package ma.rearchitect.data.device.model;

import ma.rearchitect.data.device.DeviceDefine;

/**
 * Created by LenVo on 7/13/18.
 */

public class Encryption extends DeviceModel{
    private String state;
    private String stateName;

    @Override
    public String getType() {
        return DeviceDefine.ENCRYPTION_KEY;
    }

    public boolean isGoodStatus() {
        return state.equals(DeviceDefine.FULLY_ENCRYPTED);
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }
}
