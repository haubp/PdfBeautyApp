package opswat.com.util;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;

import opswat.com.constant.MAContant;

public class StringUtil {
    private static final String REGEX_EMAIL = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";

    public static boolean isEmpty(String str) {
        return (str == null || "".equals(str));
    }

    public static boolean matches(String str, String regex) {
        return str.matches(regex);
    }

    public static boolean validateEmail(String email) {
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(REGEX_EMAIL);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }

    public static boolean equalsWithNullable(String str1, String str2) {
        if (str1 == null && str2 == null) {
            return true;
        }
        if (str1 != null) {
            return str1.equalsIgnoreCase(str2);
        }
        return str2.equalsIgnoreCase(str1);
    }

    public static Map<String, String> splitQuery(URL url) throws UnsupportedEncodingException {
        Map<String, String> query_pairs = new LinkedHashMap<String, String>();
        String query = url.getQuery();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"),
                    URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
        }
        return query_pairs;
    }

    public static  String getServerUrlFromName(String serverName) {
        String serverURL = MAContant.SERVER_URL;
        if (!StringUtil.isEmpty(serverName)) {
            serverURL = serverName;
            if (!serverName.startsWith("http://") && !serverName.startsWith("https://")) {
                serverURL = "https://" + serverName;
            }

            if (!serverName.endsWith("/")) {
                serverURL = serverURL + "/";
            }
        }
        return serverURL;
    }
}
