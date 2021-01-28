package opswat.com.flow.splash;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import opswat.com.R;
import opswat.com.constant.MAContant;
import opswat.com.device.DeviceBuilder;
import opswat.com.flow.home.MainActivity;
import opswat.com.handler.MADialogHandler;
import opswat.com.util.ResourceUtils;
import opswat.com.util.SharedPrefsUtils;
import opswat.com.view.custom.MADialog;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        LoadDeviceInfoTask loadDeviceInfoTask = new LoadDeviceInfoTask();
        loadDeviceInfoTask.execute();
    }

    private class LoadDeviceInfoTask extends AsyncTask<String, Integer, String> {

        LoadDeviceInfoTask() {}

        @Override
        protected String doInBackground(String... param) {
            try {
                DeviceBuilder deviceBuilder = MAApplication.getInstance().getDeviceBuilder();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showEulaText();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
            return "success";
        }
    }

    private void showEulaText() {
        boolean showedEula = SharedPrefsUtils.getBooleanPreference(this, MAContant.EULA_PAGE_KEY, false);
        if (showedEula) {
            showMainPage();
            return;
        }

        String content = ResourceUtils.loadResourceFile(this, R.raw.license); //sync name with iOS

        int startHeadTag = content.indexOf("<head>");
        int endHeadTag = content.indexOf("</head>");
        content = new StringBuilder(content).delete(startHeadTag, endHeadTag).toString(); //remove <head>...</head> tag, </head> still exists
        content = content.replace("</head>", "") ; //remove </head>

        MADialog.showDialogEula(this, content, new MADialogHandler() {
            @Override
            public void onClickOK() {
                showMainPage();
            }

            @Override
            public void onClickCancel() {
            }
        });
    }

    private void showMainPage() {
        SharedPrefsUtils.setBooleanPreference(this, MAContant.EULA_PAGE_KEY, true);
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
    }
}
