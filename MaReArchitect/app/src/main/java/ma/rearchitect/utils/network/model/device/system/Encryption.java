package opswat.com.network.model.device.system;

/**
 * Created by H. Len Vo on 9/11/18.
 */
public class Encryption {
    private boolean internal;
    private boolean external;

    public boolean isInternal() {
        return internal;
    }

    public void setInternal(boolean internal) {
        this.internal = internal;
    }

    public boolean isExternal() {
        return external;
    }

    public void setExternal(boolean external) {
        this.external = external;
    }

    @Override
    public String toString() {
        return "Encryption{" +
                "internal=" + internal +
                ", external=" + external +
                '}';
    }
}
