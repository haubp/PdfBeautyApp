package ma.rearchitect.data.data;

import com.google.gson.annotations.SerializedName;

import opswat.com.constant.MaCloudKey;

/**
 * Created by H. Len Vo on 9/4/18.
 */
public class UserIdentity {
    private int enable;
    private String regex;
    private String msg;

    public UserIdentity(int enable, String regex, String msg) {
        this.enable = enable;
        this.regex = regex;
        this.msg = msg;
    }

    //User Input:
    @SerializedName(MaCloudKey.USER_INPUT)
    private String userInput;

    @SerializedName(MaCloudKey.RE_IDENTITY)
    private boolean reIdentity = false;

    public String getUserInput() {
        return userInput;
    }

    public void setUserInput(String userInput) {
        this.userInput = userInput;
    }

    public boolean isReIdentity() {
        return reIdentity;
    }

    public void setReIdentity(boolean reIdentity) {
        this.reIdentity = reIdentity;
    }

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

    public boolean equalValues(UserIdentity userIdentity) {
        return userIdentity.msg.equals(msg) && userIdentity.regex.equals(regex);
    }
}
