package opswat.com.network.model.request;

/**
 * Created by H. Len Vo on 9/10/18.
 */
public class ImHereRequest {
    private String hwid;
    private int opt;

    public String getHwid() {
        return hwid;
    }

    public void setHwid(String hwid) {
        this.hwid = hwid;
    }

    public int getOpt() {
        return opt;
    }

    public void setOpt(int opt) {
        this.opt = opt;
    }

    @Override
    public String toString() {
        return "ImHereRequest{" +
                "hwid='" + hwid + '\'' +
                ", opt=" + opt +
                '}';
    }
}
