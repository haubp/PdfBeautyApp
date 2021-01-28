package opswat.com.network.model.device.system;

import com.google.gson.annotations.SerializedName;

/**
 * Created by H. Len Vo on 9/11/18.
 */
public class Memory {
    @SerializedName("available_memory_mb")
    private long available;

    @SerializedName("total_memory_mb")
    private long total;

    public long getAvailable() {
        return available;
    }

    public void setAvailable(long available) {
        this.available = available;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "Memory{" +
                "available=" + available +
                ", total=" + total +
                '}';
    }
}
