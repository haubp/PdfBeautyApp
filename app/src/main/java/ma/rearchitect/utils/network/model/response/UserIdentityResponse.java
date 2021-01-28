package opswat.com.network.model.response;

/**
 * Created by H. Len Vo on 9/6/18.
 */
public class UserIdentityResponse {
    private int enable;
    private String regex;
    private String msg;

    public int getEnable() {
        return enable;
    }

    public void setEnable(int enable) {
        this.enable = enable;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "UserIdentityResponse{" +
                "enable=" + enable +
                ", regex='" + regex + '\'' +
                ", msg='" + msg + '\'' +
                '}';
    }
}
