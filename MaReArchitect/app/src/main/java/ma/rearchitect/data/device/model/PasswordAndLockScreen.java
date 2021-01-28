package ma.rearchitect.data.device.model;

import ma.rearchitect.data.device.DeviceDefine;

/**
 * Created by LenVo on 7/13/18.
 */

public class PasswordAndLockScreen extends DeviceModel{
    private boolean isProtected;
    private String lockType;

    @Override
    public String getType() {
        return DeviceDefine.PASSWORD_KEY;
    }

    public boolean isProtected() {
        return isProtected;
    }

    public void setProtected(boolean aProtected) {
        isProtected = aProtected;
    }

    public String getLockType() {
        return lockType;
    }

    public void setLockType(String lockType) {
        this.lockType = lockType;
    }
}
