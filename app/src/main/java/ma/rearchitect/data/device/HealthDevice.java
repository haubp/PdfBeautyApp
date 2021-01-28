package ma.rearchitect.data.device;

import ma.rearchitect.data.device.model.Battery;
import ma.rearchitect.data.device.model.Hardware;
import ma.rearchitect.data.device.model.Memory;
import ma.rearchitect.data.device.model.RebootRecency;
import ma.rearchitect.data.device.model.Storage;

/**
 * Created by LenVo on 7/13/18.
 */

public class HealthDevice {
    private Storage storage;
    private Memory memory;
    private RebootRecency rebootRecency;
    private Battery battery;
    private Hardware hardware;

    public Storage getStorage() {
        return storage;
    }

    public void setStorage(Storage storage) {
        this.storage = storage;
    }

    public Memory getMemory() {
        return memory;
    }

    public void setMemory(Memory memory) {
        this.memory = memory;
    }

    public RebootRecency getRebootRecency() {
        return rebootRecency;
    }

    public void setRebootRecency(RebootRecency rebootRecency) {
        this.rebootRecency = rebootRecency;
    }

    public Battery getBattery() {
        return battery;
    }

    public void setBattery(Battery battery) {
        this.battery = battery;
    }

    public Hardware getHardware() {
        return hardware;
    }

    public void setHardware(Hardware hardware) {
        this.hardware = hardware;
    }
}
