package opswat.com.constant;

import opswat.com.util.StringUtil;

/**
 * Created by H. Len Vo on 8/22/18.
 */
public class MaCloudKey {
    public static final int NOT_FOUND_STATUS = 404;
    public static final int CONFLICT_STATUS = 409;
    public static final int OUT_OF_TOKEN_STATUS = 406;

    public static final String ACCESS_KEY = "access_key";
    public static final String ACCESS_TOKEN = "access_token";
    public static final String LICENSE_KEY = "license_key";
    public static final String MO_KEY = "mo_key";
    public static final String OWNER_EMAIL = "owner_email";
    public static final String DEVICE_ID = "device_id";
    public static final String LAST_MODIFIED = "last_modified";
    public static final String SERVER_ADDRESS = "server_address";
    public static final String ACCOUNT_NAME = "account_name";
    public static final String REG_CODE = "reg_code";
    public static final String GROUP_ID = "group_id";
    public static final String USER_IDENTITY = "user_identity";
    public static final String USER_INPUT = "user_input";
    public static final String RE_IDENTITY = "re_identity";

    public static final String NO_NETWORK_ERROR = "404001";
    public static final String NOT_FOUND_ERROR = "404006";
    public static final String INVALID_INPUT = "400000";
    public static final String OUT_OF_TOKEN_ERROR = "Exceeded token usage";

    public static boolean isErrorNotFound(int statusCode, String error) {
        return statusCode == NOT_FOUND_STATUS && NOT_FOUND_ERROR.equalsIgnoreCase(error);
    }

    public static boolean isErrorOutOfToken(int statusCode, String error) {
        return statusCode == OUT_OF_TOKEN_STATUS && OUT_OF_TOKEN_ERROR.equalsIgnoreCase(error);
    }
}
