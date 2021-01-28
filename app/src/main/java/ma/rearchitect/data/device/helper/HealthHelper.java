package ma.rearchitect.data.device.helper;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.os.SystemClock;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import ma.rearchitect.data.device.model.Battery;
import ma.rearchitect.data.device.model.Hardware;
import ma.rearchitect.data.device.model.Memory;
import ma.rearchitect.data.device.model.RebootRecency;
import ma.rearchitect.data.device.model.Storage;
import opswat.com.network.model.device.system.DiskSpace;

/**
 * Created by LenVo on 7/14/18.
 */

public class HealthHelper {
    private Context context;
    private final static Double MB_SIZE = 1048576.0;
    private final static String MODEL_GALAXY_NOTE7 = "SM-N930";
    private final static String MODEL_GALAXY_S7 = "SM-G930";
    private final static String MODEL_GALAXY_S7_EDGE = "SM-G935";

    public HealthHelper(Context context) {
        this.context = context;
    }

    public Storage getStorage() {
        int sdkLevel = Build.VERSION.SDK_INT;
        Storage diskInfo;
        if (sdkLevel > 17)
            diskInfo = getDiskInfoAPI18();
        else
            diskInfo = getDiskInfoAPI16();
        return diskInfo;
    }

    public Memory getMemory() {
        Memory memory = new Memory();
        ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        if (activityManager != null)
            activityManager.getMemoryInfo(memoryInfo);
        memory.setAvailMem((long)(memoryInfo.availMem/MB_SIZE));
        memory.setTotalMem((long)(memoryInfo.totalMem/MB_SIZE));
        return memory;
    }

    public RebootRecency getRebootRecency() {
        Long upTime = SystemClock.elapsedRealtime();
        RebootRecency rebootRecency = new RebootRecency();

        float float_time = (float)upTime/TimeUnit.HOURS.toMillis(1);
        long rounded_time = Math.round(float_time);

        long lastReboot = (System.currentTimeMillis() - upTime)/TimeUnit.SECONDS.toMillis(1);

        rebootRecency.setUpTimeByMillis(upTime);
        rebootRecency.setUpTime(rounded_time);
        rebootRecency.setLastReboot(lastReboot);
        return rebootRecency;
    }

    public Battery getBattery() {
        Intent intent = new ContextWrapper(context).registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = 0;
        int chargeValue = 0;
        if (intent != null) {
            level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            chargeValue = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        }

        boolean is_charging = false;
        if ((chargeValue == BatteryManager.BATTERY_PLUGGED_AC) || (chargeValue == BatteryManager.BATTERY_PLUGGED_USB))
            is_charging = true;

        Battery battery = new Battery();
        battery.setPercent(level);
        battery.setCharging(is_charging);
        return battery;
    }

    public Hardware getHardware() {
        Hardware hardware = new Hardware();
        String cpuModel = getCpuModel();
        int cores = getNumCores();
        int cpuSpeed = getCpusSpeed(cores);

        hardware.setCores(cores);
        hardware.setCpuSpeed(cpuSpeed);
        hardware.setProcessorName(cpuModel);
        return hardware;
    }

    private int getCpusSpeed(int cores) {
        int cpuSpeed = 0;
        for (int i = 0; i < cores; i++) {
            try {
                String coreSystemFile = "/sys/devices/system/cpu/cpu" + String.valueOf(i) + "/cpufreq/cpuinfo_max_freq";
                final Process process = new ProcessBuilder(new String[] { "/system/bin/cat", coreSystemFile }).start();

                InputStream in = process.getInputStream();
                final StringBuilder sb = new StringBuilder();
                final Scanner sc = new Scanner(in);
                while(sc.hasNextLine()) {
                    sb.append(sc.nextLine());
                }
                final String content = sb.toString();
                if (content.isEmpty()) {
                    continue;
                }
                int temp = Integer.parseInt(content) / 1000;
                if (temp > cpuSpeed)
                    cpuSpeed = temp;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return cpuSpeed;
    }
    /*
    Get CPU Module by reading a system file
     */
    private String getCpuModel(){

        String processorName = "";
        try {
            StringBuilder strBuffer = new StringBuilder();
            if (new File("/proc/cpuinfo").exists()) {
                BufferedReader br = new BufferedReader(new FileReader(new File("/proc/cpuinfo")));
                String aLine;
                while ((aLine = br.readLine()) != null) {
                    strBuffer.append(aLine).append("\n");
                }
            }
            //cut the processor name here
            String text_find = "Processor\t: ";
            int startIndex = strBuffer.indexOf(text_find);
            int endIndex = strBuffer.indexOf("\n", startIndex);
            processorName = strBuffer.substring(startIndex + text_find.length(), endIndex);

            if(processorName.length() > 5)
                return processorName;
            //Get CpuModel by the "model name":
            text_find = "model name\t: ";
            startIndex = strBuffer.indexOf(text_find);
            if(startIndex != 1) {
                endIndex = strBuffer.indexOf("\n", startIndex);
                processorName = strBuffer.substring(startIndex + text_find.length(), endIndex);
            }
            if(processorName.length() > 5)
                return processorName;
            //Get CpuModel by the "cpu model":
            text_find = "cpu model\t: ";
            startIndex = strBuffer.indexOf(text_find);
            if(startIndex != 1) {
                endIndex = strBuffer.indexOf("\n", startIndex);
                processorName = strBuffer.substring(startIndex + text_find.length(), endIndex);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return processorName;
    }

    private int getNumCores() {
        //Private Class to display only CPU devices in the directory listing
        int cores_folder = 1;
        class CpuFilter implements FileFilter {
            @Override
            public boolean accept(File pathname) {
                //Check if filename is "cpu", followed by a single digit number
                return Pattern.matches("cpu[0-9]+", pathname.getName());
            }
        }

        try {
            //Get directory containing CPU info
            File dir = new File("/sys/devices/system/cpu/");
            //Filter to only list the devices we care about
            File[] files = dir.listFiles(new CpuFilter());
            //Return the number of cores (virtual CPU devices)
            cores_folder =  files.length;
        } catch(Exception e) {
            //Default to return 1 core
        }


        //Get cpu core from cat /proc/cpuinfo (Count processor)
        int cores_runtime = 1;
        try {
            String model = Build.MODEL;
            if(model.contains(MODEL_GALAXY_NOTE7))
                cores_runtime =  8;
            if(model.contains(MODEL_GALAXY_S7) || model.contains(MODEL_GALAXY_S7_EDGE))
                cores_runtime =  12;
        }catch (Exception e){
            e.printStackTrace();
        }
        return (cores_folder > cores_runtime)? cores_folder : cores_runtime;
    }

    public int readCPUUsage() {
        try {
            String model = Build.MODEL;
            if(model.contains(MODEL_GALAXY_NOTE7) || model.contains(MODEL_GALAXY_S7) || model.contains(MODEL_GALAXY_S7_EDGE)) {
                return 0;
            }

            RandomAccessFile reader = new RandomAccessFile("/proc/stat", "r");
            String load = reader.readLine();

            String[] toks = load.split(" +");  // Split on one or more spaces

            long idle1 = Long.parseLong(toks[4]);
            long cpu1 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[5])
                    + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

            try {
                Thread.sleep(360);
            } catch (Exception e) {
                e.printStackTrace();
            }

            reader.seek(0);
            load = reader.readLine();
            reader.close();

            toks = load.split(" +");

            long idle2 = Long.parseLong(toks[4]);
            long cpu2 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[5])
                    + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

            if ((cpu2 + idle2) - (cpu1 + idle1) == 0) {
                return 0;
            }

            return (int)((cpu2 - cpu1) / ((cpu2 + idle2) - (cpu1 + idle1)))*100;

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return 0;
    }

    public int readRamUsagePercent() {
        long memSize, memFree;
        ActivityManager myManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        if (myManager != null) {
            myManager.getMemoryInfo(memoryInfo);
        }
        memSize = memoryInfo.totalMem;
        memFree = memoryInfo.availMem;
        long memUsed = memSize - memFree;
        return (int) ((memUsed * 1.0 / memSize) * 100);
    }

    public int readStorageUsagePercent() {
        try{
            int sdkLevel = Build.VERSION.SDK_INT;
            Storage storage = new Storage();
            if (sdkLevel > 17)
                storage = getDiskInfoAPI18();
            else
                storage = getDiskInfoAPI16();
            long usedStorage = storage.getTotalMem() - storage.getFreeMem();
            return (int)(usedStorage / storage.getTotalMem()) * 100;
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return 0;
    }

    @TargetApi(18)
    private Storage getDiskInfoAPI18() {
        StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getPath());
        //convert to MB
        long totalSize, freeSize;

        totalSize = (long)((statFs.getBlockCountLong()/ 1048576.0) * statFs.getBlockSizeLong()) ;
        freeSize = (long)((statFs.getAvailableBlocksLong() / 1048576.0) * statFs.getBlockSizeLong());

        Storage diskInfo = new Storage();
        diskInfo.setFreeMem(freeSize);
        diskInfo.setTotalMem(totalSize);

        return diskInfo;
    }

    @TargetApi(16)
    private Storage getDiskInfoAPI16 () {
        StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getPath());

        //convert to MB
        long totalSize, freeSize;
        totalSize = (long)((statFs.getBlockCount() / MB_SIZE) * statFs.getBlockSize()) ;
        freeSize = (long)((statFs.getAvailableBlocks() / MB_SIZE) * statFs.getBlockSize());

        Storage diskInfo = new Storage();
        diskInfo.setFreeMem(freeSize);
        diskInfo.setTotalMem(totalSize);

        return diskInfo;
    }
}
