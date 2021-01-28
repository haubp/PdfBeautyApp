package opswat.com.util;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import opswat.com.R;
import opswat.com.constant.MAContant;
import opswat.com.enums.DialogTypes;
import opswat.com.handler.MADialogHandler;
import opswat.com.view.custom.MADialog;

/**
 * Created by H. Len Vo on 8/23/18.
 */
public class PermissionUtils {
    private static void openSettingApp(Context context) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", context.getPackageName(), null));
        context.startActivity(intent);
    }

    public static boolean checkPermissionReadExternalStorage(final Context context, boolean showDialog) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
                if (!(context instanceof Activity)) {
                    return false;
                }
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_EXTERNAL_STORAGE)
                        && showDialog) {
                    showDialogRequestPermission(context);
                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MAContant.REQUEST_CODE_READ_STORAGE);
                }
            }
            return false;
        }
        return true;
    }

    public static boolean checkPermissionWriteExternalStorage(final Context context, boolean showDialog) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (!(context instanceof Activity)) {
                return false;
            }

            if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        && showDialog) {
                    showDialogRequestPermission(context);
                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MAContant.REQUEST_CODE_WRITE_STORAGE);
                }
            }
            return false;
        }
        return true;
    }

    private static Dialog dialog = null;

    private static void showDialogRequestPermission(final Context context) {
        MADialogHandler handler = new MADialogHandler() {
            @Override
            public void onClickOK() {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }

            @Override
            public void onClickCancel() {
                openSettingApp(context);
            }
        };
        dialog = MADialog.showDialogWithType(DialogTypes.INFO, context, context.getString(R.string.request_permission_storage),
                null, context.getString(R.string.deny), context.getString(R.string.settings), handler);
    }
}
