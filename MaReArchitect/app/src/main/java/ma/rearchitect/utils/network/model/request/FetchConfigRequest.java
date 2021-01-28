package opswat.com.network.model.request;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by H. Len Vo on 9/6/18.
 */
public class FetchConfigRequest {
    @SerializedName("include")
    private List<String> include;

    public List<String> getInclude() {
        return include;
    }

    public void setInclude(List<String> include) {
        this.include = include;
    }

    @Override
    public String toString() {
        return "FetchConfigRequest{" +
                "include=" + include +
                '}';
    }
}
