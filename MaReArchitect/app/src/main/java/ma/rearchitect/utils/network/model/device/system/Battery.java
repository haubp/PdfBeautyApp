package opswat.com.network.model.device.system;

import com.google.gson.annotations.SerializedName;

/**
 * Created by H. Len Vo on 9/11/18.
 */
public class Battery {
    @SerializedName("percentage")
    private int percent;

    @SerializedName("is_charging")
    private boolean isCharging;

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

    @Override
    public String toString() {
        return "Battery{" +
                "percent=" + percent +
                ", isCharging=" + isCharging +
                '}';
    }
}
