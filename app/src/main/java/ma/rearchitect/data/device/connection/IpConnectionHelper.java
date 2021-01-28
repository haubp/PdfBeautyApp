package ma.rearchitect.data.device.connection;

import android.util.Base64;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import opswat.com.network.model.connection.Connection;
import opswat.com.util.ShellUtil;

/**
 * Created by LenVo on 7/16/18.
 */

public class IpConnectionHelper {
    public static List<String> getActiveIps() {
        List<String> ips = new ArrayList<>();
        ArrayList<String> outputScript = ShellUtil.executeCommand(ShellUtil.SHELL_CMD.check_netstat);
        HashSet<String> ipSet = new HashSet<>();

        if (outputScript == null) {
            return ips;
        }

        for (int i = 0; i < outputScript.size(); i++) {
            String eachString = outputScript.get(i);
            if (eachString.contains("ESTABLISHED")) {
                eachString = eachString.trim();
                eachString = eachString.replaceAll("( )+", " ");
                String[] splitString = eachString.split(" ");
                String ip = splitString[4];
                int index = ip.lastIndexOf(":");
                if (index != -1) {
                    ip = ip.substring(0, index);
                    ipSet.add(ip);
                }
            }
        }

        Object[] ipsArr = ipSet.toArray();
        for (Object item : ipsArr) {
            String ip = item.toString();
            String[] split = ip.split("\\.");
            if (split.length == 4) {
                if (split[0].equals("10") || split[0].equals("127")) {
                    continue;
                } else if (split[0].equals("172")) {
                    int num = Integer.parseInt(split[1]);
                    if (16 < num && num < 31) {
                        continue;
                    }
                } else if (split[0].equals("192") && split[1].equals("168")) {
                    continue;
                }
            }
            ips.add(ip);
        }

        return ips;
    }

    public static String getBase64Ip(String ipAddress) {
        try {
            byte[] data = ipAddress.getBytes("UTF-8");
            return Base64.encodeToString(data, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
