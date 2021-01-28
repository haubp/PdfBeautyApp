package ma.rearchitect.service.receiver;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import java.net.URL;
import java.util.Map;

import ma.rearchitect.R;
import opswat.com.constant.MAContant;
import opswat.com.flow.splash.SplashActivity;
import opswat.com.logger.LoggerUniversalLink;
import opswat.com.util.SharedPrefsUtils;
import opswat.com.util.StringUtil;

/**
 * Created by H. Len Vo on 9/13/18.
 */
public class DeeplinkReceiver extends Activity {
    public static DeeplinkReceiver instance;
    private static String TAG = DeeplinkReceiver.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        Intent intent = getIntent();
        handleLinkRequest(intent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleLinkRequest(intent);
    }

    private void handleLinkRequest(Intent intent) {
        setIntent(intent); //must store the new intent unless getIntent() will return the old one
        Uri data = intent.getData();
        if (data == null) {
            launchApp();
            return;
        }
        Log.i(TAG, "Start DeepLinkReceiver, getPath: " + data.getPath() + " - getHost: " + data.getHost() + " - getQuery: " + data.getQuery());
        if (data.getHost().contains(getString(R.string.universal_url))) {
            processUniversalLink(data.toString(), data.getPath(), data.getQuery());
            return;
        } else if (data.getScheme() != null && data.getScheme().contains(getString(R.string.universal_url_scheme))){
            processUniversalLink(data.toString(), data.getHost(), data.getQuery());
            return;

        }
        processDeeplink(data.getPath());
    }

    private void processDeeplink(String path) {
        if (StringUtil.isEmpty(path) || path.length() < 10) {
            launchApp();
            return;
        }

        //Remove /register/
        String refLink = path.substring(10);
        if (refLink.isEmpty()) {
            launchApp();
            return;
        }
        if (refLink.charAt(refLink.length() - 1) == '/') {
            refLink = refLink.substring(0, refLink.length() - 2);
        }

        String[] refArr = refLink.split("_");
        SharedPrefsUtils.setStringPreference(this, MAContant.REG_CODE_KEY, refArr[0]);

        String group_id = "";
        if (refArr.length == 2)
            group_id = refArr[1];
        SharedPrefsUtils.setStringPreference(this, MAContant.GROUP_ID_KEY, group_id);
        Log.i(TAG, "startActivity DeepLinkReceiver, reg_code: " + refArr[0] + " - group_id: " + group_id + " ");

        launchApp();
    }

    private void launchApp() {
        Intent myIntent = new Intent(this, SplashActivity.class);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        instance.startActivity(myIntent);
        finish();
    }

    private void processUniversalLink(String url, String transactionPath, String queriesPath) {
        LoggerUniversalLink.writeLog(this, "Received universal link: url =  " + url +
                " | transactionPath = " + transactionPath + " | queriesPath = " + queriesPath);
        if (transactionPath == null || transactionPath.equals("")) {
            launchApp();
            return;
        }
        //Get transaction Id:
        String transactionId = transactionPath.replace("/", "");

        //Get AppName and AppId
        String appName = "";
        String appId = "";
        String redirectUrl = "";
        try {
            String preformatURL = url;
            if (url.startsWith(getString(R.string.universal_url_scheme))) {
                preformatURL = "https://" + url;
            }

            Map<String, String> queries = StringUtil.splitQuery(new URL(preformatURL));
            if (queries.containsKey(MAContant.APP_NAME_KEY)) {
                appName = queries.get(MAContant.APP_NAME_KEY);
            }
            if (queries.containsKey(MAContant.APP_ID_KEY)) {
                appId = queries.get(MAContant.APP_ID_KEY);
            }
            if (queries.containsKey(MAContant.URL_REDIRECT_KEY)) {
                redirectUrl = queries.get(MAContant.URL_REDIRECT_KEY);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        SharedPrefsUtils.setStringPreference(this, MAContant.TRANSACTION_ID_KEY, transactionId);
        SharedPrefsUtils.setStringPreference(this, MAContant.APP_NAME_KEY, appName);
        SharedPrefsUtils.setStringPreference(this, MAContant.APP_ID_KEY, appId);
        SharedPrefsUtils.setStringPreference(this, MAContant.URL_REDIRECT_KEY, redirectUrl);
        LoggerUniversalLink.writeLog(this, "Parsed universal link: transactionId =  " + transactionId +
                " | appName = " + appName + " | appId = " + appId + " | redirectUrl = " + redirectUrl);
        Log.i(TAG, "startActivity DeepLinkReceiver - Universal Link, transactionId: " + transactionId + " - appName: " + appName + " appId: " + appId);
        launchApp();
    }
}
