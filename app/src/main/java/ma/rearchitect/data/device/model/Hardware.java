package ma.rearchitect.data.device.model;

import ma.rearchitect.data.device.DeviceDefine;

/**
 * Created by LenVo on 7/13/18.
 */

public class Hardware extends DeviceModel{
    private String processorName;
    private int cpuSpeed;
    private int cores;
    private int totalMem;

    @Override
    public String getType() {
        return DeviceDefine.HARDWARE_KEY;
    }

    public boolean isGoodStatus() {
        return true;
    }

    public String getProcessorName() {
        return processorName;
    }

    public void setProcessorName(String processorName) {
        this.processorName = processorName;
    }

    public int getCpuSpeed() {
        return cpuSpeed;
    }

    public void setCpuSpeed(int cpuSpeed) {
        this.cpuSpeed = cpuSpeed;
    }

    public int getCores() {
        return cores;
    }

    public void setCores(int cores) {
        this.cores = cores;
    }

    public int getTotalMem() {
        return totalMem;
    }

    public void setTotalMem(int totalMem) {
        this.totalMem = totalMem;
    }
}
