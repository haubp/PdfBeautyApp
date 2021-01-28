package opswat.com.network.model.connection.geoinfo;

import com.google.gson.annotations.SerializedName;

/**
 * Created by LenVo on 7/16/18.
 */

public class Continent {
    @SerializedName("code")
    private String code;

    @SerializedName("name")
    private String name;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
