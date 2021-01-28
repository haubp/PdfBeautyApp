package opswat.com.network.model.device.system;

import com.google.gson.annotations.SerializedName;

/**
 * Created by H. Len Vo on 9/11/18.
 */
public class Cpu {
    @SerializedName("max_speed_mhz")
    private long maxSpeed;

    @SerializedName("model")
    private String model;

    public long getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(long maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    @Override
    public String toString() {
        return "Cpu{" +
                "maxSpeed=" + maxSpeed +
                ", model='" + model + '\'' +
                '}';
    }
}
