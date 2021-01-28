package opswat.com.validation;

import java.util.regex.Pattern;

import opswat.com.util.StringUtil;

public class MaValidation {
    private static final Pattern PATTERN = Pattern.compile("^"
            + "(((?!-)[A-Za-z0-9-]{1,63}(?<!-)\\.)+[A-Za-z]{2,6}" // Domain name
            + "|"
            + "localhost" // localhost
            + "|"
            + "(([0-9]{1,3}\\.){3})[0-9]{1,3})" // Ip
            + ":"
            + "[0-9]{1,5}$"); // Port

    public static boolean validateRegCode(String regCode) {
        if(regCode == null || StringUtil.isEmpty(regCode)) {
            return true; // Empty group ID
        }
        // Reg code has value we have to validate it
        String pattern = "^[a-zA-Z0-9-_]+$";
        return regCode.matches(pattern);
    }

    public static boolean validateGroupID(String groupID) {
        if(groupID == null || StringUtil.isEmpty(groupID)) {
            return true; // Empty group ID
        }
        // Group ID has value we have to validate it
        String pattern = "^[a-zA-Z0-9-_]+$";
        return groupID.matches(pattern);
    }

    public static boolean validateServerName(String serverName) {
        if(serverName == null || StringUtil.isEmpty(serverName)) {
            return true; // Empty group ID
        }

        if (serverName.contains("https://")) {
            serverName = serverName.replace("https://", "");
        }

        if (serverName.contains("http://")) {
            serverName = serverName.replace("http://", "");
        }

        // Server Name has value we have to validate it
        String pattern = "^((?!-))(xn--)?[a-z0-9][a-z0-9-_]{0,61}[a-z0-9]{0,1}\\.(xn--)?([a-z0-9\\-]{1,61}|[a-z0-9-]{1,30}\\.[a-z]{2,})$";
        return serverName.matches(pattern);
    }

    public static boolean validateIpAddress(String serverName) {
        if (serverName.contains("https://")) {
            serverName = serverName.replace("https://", "");
        }

        if (serverName.contains("http://")) {
            serverName = serverName.replace("http://", "");
        }

        return PATTERN.matcher(serverName).matches();
    }

    public static boolean validateTransactionId(String transactionId) {
        if(transactionId == null || StringUtil.isEmpty(transactionId) || transactionId.length() > 50) {
            return false; // Empty transaction Id
        }
        // TransactionId has value we have to validate it
        String pattern = "^[a-zA-Z0-9-_]+$";
        return transactionId.matches(pattern);
    }

}
