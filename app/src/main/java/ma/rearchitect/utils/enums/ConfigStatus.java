package opswat.com.enums;

public enum ConfigStatus {
    USER_UNSELECTED_CONFIG(-2),
    USER_SELECTED_CONFIG(-1),
    DEFAULT_UNSELECTED_CONFIG(0),
    DEFAULT_SELECTED_CONFIG(1);
    ConfigStatus(int i) {
        this.value = i;
    }
    private int value;
    public int getValue(){
        return value;
    }
    public String getStringValue(){
        return String.valueOf(value);
    }
    public static ConfigStatus getConfigStatus(String tag){
        switch (tag){
            case "-2":    return USER_UNSELECTED_CONFIG;
            case "-1":    return USER_SELECTED_CONFIG;
            case  "0":    return DEFAULT_UNSELECTED_CONFIG;
            case  "1":    return DEFAULT_SELECTED_CONFIG;
        }
        return DEFAULT_SELECTED_CONFIG;
    }

    public static ConfigStatus getConfigStatus(int i){
        switch (i){
            case -2:    return USER_UNSELECTED_CONFIG;
            case -1:    return USER_SELECTED_CONFIG;
            case  0:    return DEFAULT_UNSELECTED_CONFIG;
            case  1:    return DEFAULT_SELECTED_CONFIG;
        }
        return DEFAULT_SELECTED_CONFIG;
    }
}
