package ma.rearchitect.data.device.helper;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;

import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import opswat.com.constant.MAContant;
import ma.rearchitect.data.device.model.DeviceInfo;
import ma.rearchitect.data.device.model.NetworkAdapter;

/**
 * Created by H. Len Vo on 8/23/18.
 */
public class DeviceInfoHelper {
    private static final Pattern IPV4_PATTERN = Pattern.compile("^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$");
    private static final Pattern IPV6_STD_PATTERN = Pattern.compile("^(?:[0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$");
    private static final Pattern IPV6_HEX_COMPRESSED_PATTERN = Pattern.compile("^((?:[0-9A-Fa-f]{1,4}(?::[0-9A-Fa-f]{1,4})*)?)::((?:[0-9A-Fa-f]{1,4}(?::[0-9A-Fa-f]{1,4})*)?)$");
    private Context context;

    public DeviceInfoHelper(Context context) {
        this.context = context;
    }

    public DeviceInfo getDeviceInfo() {
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setDeviceName(getDeviceName());
        deviceInfo.setOsFamily(MAContant.OS_FAMILY);
        deviceInfo.setOsVendor(MAContant.OS_VENDOR);
        deviceInfo.setDeviceType(MAContant.DEVICE_TYPE);
        deviceInfo.setOsVersion(getOsVersion());
        deviceInfo.setOsLanguage(getOsLanguage());
        deviceInfo.setOsName(getOSName());

        NetworkAdapter networkAdapter = new NetworkAdapter();
        networkAdapter.setIpv4(getIpV4());
        networkAdapter.setIpv6(getIpV6());
        networkAdapter.setMac(getMACAddress());
        deviceInfo.setNetworkAdapter(networkAdapter);

        deviceInfo.setWifiEnable(isEnableWifi());
        return deviceInfo;
    }

    private boolean isEnableWifi() {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        return wifiManager != null && wifiManager.isWifiEnabled();
    }

    private String getDeviceName() {
        String name = Settings.Secure.getString(context.getContentResolver(), "bluetooth_name");
        return (name == null)? "" : name;
    }

    private String getModel() {
        return Build.MODEL;
    }

    private String getOsVersion() {
        return Build.VERSION.RELEASE;
    }

    private String getOsLanguage() {
        return Locale.getDefault().getDisplayLanguage();
    }

    private String getOSName() {
        StringBuilder builder = new StringBuilder();
        Field[] fields = Build.VERSION_CODES.class.getFields();
        for (int i = fields.length - 1; i >= 0; i--) {
            Field field = fields[i];
            String fieldName = field.toString();
            try {
                int fieldValue = field.getInt(new Object());
                if (fieldValue == Build.VERSION.SDK_INT) {
                    builder.append(fieldName);
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String osName = builder.toString();
        int index = osName.indexOf("VERSION");
        osName = osName.substring(index + 14, osName.length());
        return osName;
    }

    private String getIpV4() {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress().toUpperCase();
                        boolean isIPv4 = isIPv4Address(sAddr);
                        if (isIPv4) {
                            return sAddr;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private String getIpV6() {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress().toUpperCase();
                        boolean isIPv4 = isIPv4Address(sAddr);
                        if (!isIPv4) {
                            int delim = sAddr.indexOf('%'); // drop ip6 port suffix
                            return delim<0 ? sAddr : sAddr.substring(0, delim);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private boolean isIPv4Address(String ip) {
        return IPV4_PATTERN.matcher(ip).matches();
    }

    private String getMACAddress() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X", b & 0xFF) + ":");
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
            //handle exception
        }
        return "";
    }
}
