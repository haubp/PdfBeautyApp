package opswat.com.flow.submitFeedback;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.util.Calendar;

import opswat.com.R;
import opswat.com.constant.MAContant;
import opswat.com.data.AccountData;
import opswat.com.device.DeviceBuilder;
import opswat.com.enums.DialogTypes;
import opswat.com.flow.base.BaseActivity;
import opswat.com.handler.MADialogHandler;
import opswat.com.handler.MADialogSubmitFeedbackHandler;
import opswat.com.logger.LoggerFactory;
import opswat.com.mvp.MvpPresenter;
import opswat.com.util.AppUtils;
import opswat.com.util.CommonUtil;
import opswat.com.util.ExternalStorageUtils;
import opswat.com.util.PermissionUtils;
import opswat.com.view.custom.MADialog;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SubmitFeedbackActivity extends BaseActivity implements ISubmitFeedbackView {
    private ISubmitFeedbackPresenter presenter = new SubmitFeedbackPresenterIml();

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_submit_feedback;
    }

    @Override
    protected Integer getSlideMenuId() {
        return null;
    }

    @Override
    protected Integer getBtnMenuId() {
        return null;
    }

    @Override
    protected MvpPresenter getPresenter() {
        return presenter;
    }

    @Override
    protected void initialized() {
        //Update year of copyright
        String yearString = Integer.toString(Calendar.getInstance().get(Calendar.YEAR));
        TextView copyright = (TextView) findViewById(R.id.feedback_copyright);
        copyright.setText(getString(R.string.copyright).replace("%d", yearString));
        findViewById(R.id.feedback_btn_include_log).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tongleCheckbox();
            }
        });

        findViewById(R.id.feedback_tv_include_log).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tongleCheckbox();
            }
        });

        findViewById(R.id.feedback_btn_love_it).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendLoveIt();
            }
        });

        findViewById(R.id.feedback_btn_missing_st).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMissingSt();
            }
        });

        findViewById(R.id.feedback_btn_broken).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendBroken();
            }
        });

        findViewById(R.id.feedback_btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        findViewById(R.id.feedback_tv_term).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTermAndCondition();
            }
        });
    }

    private void openTermAndCondition() {
        CommonUtil.openBrowser(this, getString(R.string.url_term));
    }

    private void sendLoveIt() {
        if (maDialog != null) {
            maDialog.dismiss();
        }
        maDialog = MADialog.showDialogWithType(DialogTypes.NORMAL, this, getString(R.string.title_love_it) , getString(R.string.open_google_play_5_start), getString(R.string.no_later),
                getString(R.string.rate_now), new MADialogHandler() {
                    @Override
                    public void onClickOK() {
                        if (maDialog != null) {
                            maDialog.dismiss();
                        }
                    }

                    @Override
                    public void onClickCancel() {
                        openGooglePlayApp();
                    }
                });
    }

    private void openGooglePlayApp() {
        AppUtils.openPlayStoreForApp(this);
    }

    private void sendMissingSt() {
        MADialog.showDialogSubmitFeedback(this, getString(R.string.submit_feedback_title),
                getString(R.string.submit_feedback_missing_desc),
                new MADialogSubmitFeedbackHandler() {
                    @Override
                    public void onClickSubmit(String userInput) {
                        sendLogData(getString(R.string.submit_feedback_subject_missing), userInput);
                    }
                });
    }

    private void sendLogData(String subject, String body) {
        Button btnIncludeLog = findViewById(R.id.feedback_btn_include_log);
        String tag = btnIncludeLog.getTag().toString();
        boolean isIncludeFile = tag.equals("1");
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        String to[] = {getString(R.string.submit_feedback_email_to)};
        emailIntent.setType("application/octet-stream");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, to);

        File file = null;
        if (isIncludeFile) {
            DeviceBuilder deviceBuilder = MAApplication.getInstance().getDeviceBuilder();
            AccountData accountData = MAApplication.getInstance().getMaCloudData().getAccountData();
            String jsonData = LoggerFactory.generateLogger(this, deviceBuilder, accountData);
            if (PermissionUtils.checkPermissionWriteExternalStorage(this, false)) {
                if (ExternalStorageUtils.saveTextToFile(jsonData, MAContant.LOGGER_FILE)) {
                    file = new File(ExternalStorageUtils.getApplicationDir() + "/" + MAContant.LOGGER_FILE);
                }
            }
        }

        if (file != null) {
            emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        }
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, body);
        startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }

    private void sendBroken() {
        MADialog.showDialogSubmitFeedback(this, getString(R.string.submit_feedback_title),
                getString(R.string.submit_feedback_broken_desc),
                new MADialogSubmitFeedbackHandler() {
                    @Override
                    public void onClickSubmit(String userInput) {
                        sendLogData(getString(R.string.submit_feedback_subject_broken), userInput);
                    }
                });
    }

    private void tongleCheckbox() {
        Button btnIncludeLog = findViewById(R.id.feedback_btn_include_log);
        String tag = btnIncludeLog.getTag().toString();
        String newTag = tag.equals("0") ? "1" : "0";
        Drawable checkDrawable = tag.equals("0") ? getDrawable(R.drawable.ic_checkbox) :
                getDrawable(R.drawable.ic_uncheck);
        btnIncludeLog.setBackground(checkDrawable);
        btnIncludeLog.setTag(newTag);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
