package opswat.com.network.model.request;

/**
 * Created by H. Len Vo on 9/11/18.
 */
public class ResultApiRequest {
    private String hwid;
    private String method;
    private String result;

    public String getHwid() {
        return hwid;
    }

    public void setHwid(String hwid) {
        this.hwid = hwid;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "ResultApiRequest{" +
                "hwid='" + hwid + '\'' +
                ", method='" + method + '\'' +
                ", result='" + result + '\'' +
                '}';
    }
}
