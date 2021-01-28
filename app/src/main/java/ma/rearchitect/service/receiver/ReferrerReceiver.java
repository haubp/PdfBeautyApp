package ma.rearchitect.service.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.net.URLDecoder;

import opswat.com.constant.MAContant;
import opswat.com.util.SharedPrefsUtils;

public class ReferrerReceiver extends BroadcastReceiver {

    //--------------------------------------------------------------------------
    @Override public void onReceive(Context context, Intent intent)
    {
        try
        {
            // Make sure this is the intent we expect - it always should be.
            if (intent != null && intent.getAction() != null && intent.getAction().equals("com.android.vending.INSTALL_REFERRER"))
            {
                // This intent should have a referrer string attached to it.
                String rawReferrer = intent.getStringExtra("referrer");
                if (rawReferrer != null)
                {
                    // The string is usually URL Encoded, so we need to decode it.
                    //GRSC-2722: Need to skip the referrer from google play or other source.
                    String referrer = URLDecoder.decode(rawReferrer, "UTF-8");
                    if(referrer == null || referrer.contains("utm_source"))
                        return;

                    // Persist the referrer string.
                    String[] refArr = referrer.split("_");
                    SharedPrefsUtils.setStringPreference(context, MAContant.REG_CODE_KEY, refArr[0]);

                    String group_id = "";
                    if (refArr.length == 2)
                        group_id = refArr[1];
                    SharedPrefsUtils.setStringPreference(context, MAContant.GROUP_ID_KEY, group_id);

                }
            }
        }
        catch (Exception e)
        {
            //Logger.log(context, e.toString());
        }
    }

}
