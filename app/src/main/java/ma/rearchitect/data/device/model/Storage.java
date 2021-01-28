package ma.rearchitect.data.device.model;

import ma.rearchitect.data.device.DeviceDefine;

/**
 * Created by LenVo on 7/13/18.
 */

public class Storage extends DeviceModel{
    private long freeMem;
    private long totalMem;

    @Override
    public String getType() {
        return DeviceDefine.STORAGE_KEY;
    }

    public boolean isGoodStatus() {
        return (float)freeMem/totalMem > 0.2;
    }

    public long getTotalMem() {
        return totalMem;
    }

    public void setTotalMem(long totalMem) {
        this.totalMem = totalMem;
    }

    public long getFreeMem() {
        return freeMem;
    }

    public void setFreeMem(long freeMem) {
        this.freeMem = freeMem;
    }
}
