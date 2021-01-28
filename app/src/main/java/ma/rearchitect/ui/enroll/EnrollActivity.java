package opswat.com.flow.enroll;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import opswat.com.R;
import opswat.com.constant.MAContant;
import opswat.com.util.CommonUtil;
import opswat.com.util.StringUtil;
import opswat.com.validation.MaValidation;

public class EnrollActivity extends AppCompatActivity {

    public static void startActivityForResult(Activity activity) {
        Intent intent = new Intent(activity, EnrollActivity.class);
        activity.startActivityForResult(intent, MAContant.REQUEST_CODE_ENROLL);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enroll);
        findViewById(R.id.enroll_btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        bindingAdvancedView();

        findViewById(R.id.enroll_tv_need_help).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openHelpPage();
            }
        });
    }

    private void openHelpPage() {
        CommonUtil.openBrowser(this, getString(R.string.url_help_enroll));
    }

    private void bindingAdvancedView() {
        final TextView tvAdvanced = findViewById(R.id.enroll_tv_advanced);
        final View advancedView = findViewById(R.id.enroll_advanced);

        tvAdvanced.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = tvAdvanced.getText().toString();
                if (text.equals(getString(R.string.enroll_advanced))) {
                    tvAdvanced.setText(getString(R.string.enroll_hide_advanced));
                    advancedView.setVisibility(View.VISIBLE);
                } else {
                    tvAdvanced.setText(getString(R.string.enroll_advanced));
                    advancedView.setVisibility(View.GONE);
                }
            }
        });

        findViewById(R.id.enroll_btn_enroll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processEnrollDevice();
            }
        });

        bindingErrorTextView();
    }

    //Hide error message when text change on TextEdit
    private void bindingErrorTextView() {
        final EditText[] editTextList = {findViewById(R.id.enroll_et_registration_code),
                findViewById(R.id.enroll_et_group_identity),
                findViewById(R.id.enroll_et_server_address)};
        for (EditText editText : editTextList) {
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    showErrorText(false, null);
                }
            });
        }
    }

    private void processEnrollDevice() {
        showErrorText(false, null);
        final EditText etRegistrationCode = findViewById(R.id.enroll_et_registration_code),
                etGroupIdentity = findViewById(R.id.enroll_et_group_identity),
                etServerAddress = findViewById(R.id.enroll_et_server_address);

        String regCode = etRegistrationCode.getText().toString(),
                groupId = etGroupIdentity.getText().toString(),
                serverName = etServerAddress.getText().toString();

        if (regCode.isEmpty()) {
            showErrorText(true, getString(R.string.enroll_empty_reg_code));
            return;
        }

        if (!MaValidation.validateRegCode(regCode)) {
            showErrorText(true, getString(R.string.enroll_invalid_reg_code));
            return;
        }

        if (!groupId.isEmpty() && !MaValidation.validateGroupID(groupId)) {
            showErrorText(true, getString(R.string.enroll_invalid_group_identity));
            return;
        }

        if (!serverName.isEmpty() && !MaValidation.validateServerName(serverName) && !MaValidation.validateIpAddress(serverName)) {
            showErrorText(true, getString(R.string.enroll_invalid_server_name));
            return;
        }

        Intent intent = new Intent();
        intent.putExtra("regCode", regCode.toUpperCase());
        intent.putExtra("groupId", groupId);
        intent.putExtra("serverName", serverName);
        setResult(MAContant.REQUEST_CODE_ENROLL, intent);
        finish();
    }

    private void showErrorText(boolean isShow, String message) {
        TextView tvError = findViewById(R.id.enroll_tv_error);
        tvError.setVisibility((isShow) ? View.VISIBLE : View.GONE);
        if (!StringUtil.isEmpty(message)) {
            tvError.setText(message);
        }
    }
}
