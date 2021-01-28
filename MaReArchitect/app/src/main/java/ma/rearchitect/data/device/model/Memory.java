package ma.rearchitect.data.device.model;

import ma.rearchitect.data.device.DeviceDefine;

/**
 * Created by LenVo on 7/13/18.
 */

public class Memory extends DeviceModel{
    private long availMem;
    private long totalMem;

    @Override
    public String getType() {
        return DeviceDefine.MEMORY_KEY;
    }

    public boolean isGoodStatus() {
        return availMem > 50;
    }

    public long getAvailMem() {
        return availMem;
    }

    public void setAvailMem(long availMem) {
        this.availMem = availMem;
    }

    public long getTotalMem() {
        return totalMem;
    }

    public void setTotalMem(long totalMem) {
        this.totalMem = totalMem;
    }
}
