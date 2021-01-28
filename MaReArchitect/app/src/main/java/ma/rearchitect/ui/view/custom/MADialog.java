package opswat.com.view.custom;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import opswat.com.R;
import opswat.com.enums.DialogTypes;
import opswat.com.handler.MADialogHandler;
import opswat.com.handler.MADialogSubmitFeedbackHandler;
import opswat.com.handler.MADialogUserIdentityHandler;
import opswat.com.util.DeviceUtil;
import opswat.com.util.FontUtil;
import opswat.com.util.StringUtil;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by H. Len Vo on 8/22/18.
 */
public class MADialog {
    public static Dialog showDialogRegistering(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_loading, null, false);

        TextView tvTitle = view.findViewById(R.id.dialog_loading_tv_title);
        tvTitle.setTypeface(FontUtil.mediumTypeface(context));

        view.findViewById(R.id.dialog_loading_tv_description).setVisibility(View.GONE);

        GifImageView loadingImage = view.findViewById(R.id.dialog_loading_img_loading);
        loadingImage.setVisibility(View.VISIBLE);

        return generateDialog(context, view, false);
    }

    public static Dialog showDialogLoading(Context context, String title, String description) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_loading, null, false);

        TextView tvTitle = view.findViewById(R.id.dialog_loading_tv_title);
        tvTitle.setTypeface(FontUtil.mediumTypeface(context));
        tvTitle.setText(title);

        TextView tvDescription = view.findViewById(R.id.dialog_loading_tv_description);
        tvDescription.setVisibility(StringUtil.isEmpty(description)? View.GONE : View.VISIBLE);
        tvDescription.setText(StringUtil.isEmpty(description)? "" : description);

        GifImageView loadingImage = view.findViewById(R.id.dialog_loading_img_loading);
        loadingImage.setVisibility(View.VISIBLE);

        return generateDialog(context, view, false);
    }

    public static Dialog showDialogWithType(DialogTypes type, Context context, String title, String message,
                                            String titleOK, String titleCancel, final MADialogHandler handler) {
        View view = generateView(context, title, message, titleOK, titleCancel, type);
        final Dialog dialog = generateDialog(context, view, false);

        Button closeBtn = view.findViewById(R.id.dialog_btn_close);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (handler != null) {
                    handler.onClickOK();
                }
                dialog.dismiss();
            }
        });

        Button okBtn = view.findViewById(R.id.dialog_btn_ok);
        Button cancelBtn = view.findViewById(R.id.dialog_btn_cancel);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (handler != null) {
                    handler.onClickOK();
                }
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (handler != null) {
                    handler.onClickCancel();
                }
                dialog.dismiss();
            }
        });

        return dialog;
    }

    public static Dialog showDialogUserIdentity(Context context, String title, final String regex, final MADialogUserIdentityHandler handler) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_user_identity, null, false);

        TextView tvTitle = view.findViewById(R.id.dialog_identity_tv_title);
        tvTitle.setTypeface(FontUtil.mediumTypeface(context));
        tvTitle.setText(title);
        tvTitle.setMovementMethod(new ScrollingMovementMethod());
        final EditText edInput = view.findViewById(R.id.dialog_identity_ed_input);
        final TextView tvError = view.findViewById(R.id.dialog_identity_tv_error);
        edInput.setHint(regex);
        edInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                tvError.setVisibility(View.GONE);
            }
        });


        final Dialog dialog = generateDialog(context, view, false);
        dialog.setCancelable(false);

        tvError.setVisibility(View.GONE);
        Button btnSubmit = view.findViewById(R.id.dialog_identity_btn_submit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = edInput.getText().toString();
                if (!StringUtil.matches(input, regex)) {
                    tvError.setVisibility(View.VISIBLE);
                    return;
                }
                if (handler != null) {
                    handler.onClickSubmit(input);
                }
                dialog.dismiss();
            }
        });
        return dialog;
    }

    public static Dialog showDialogSubmitFeedback(Context context, String title, String description, final MADialogSubmitFeedbackHandler handler) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_submit_feedback, null, false);

        TextView tvTitle = view.findViewById(R.id.dialog_submit_feedback_tv_title);
        tvTitle.setTypeface(FontUtil.mediumTypeface(context));
        tvTitle.setText(title);

        if (!StringUtil.isEmpty(description)) {
            TextView tvDescription = view.findViewById(R.id.dialog_submit_feedback_tv_desc);
            tvDescription.setTypeface(FontUtil.regularTypeface(context));
            tvDescription.setText(description);
        }

//        final EditText edEmail = view.findViewById(R.id.dialog_submit_feedback_ed_email);
        final EditText edInput = view.findViewById(R.id.dialog_submit_feedback_ed_input);
        final Dialog dialog = generateDialog(context, view, true);
        final TextView tvError = view.findViewById(R.id.dialog_submit_feedback_tv_error);

//        edEmail.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                tvError.setVisibility(View.GONE);
//            }
//        });
        tvError.setVisibility(View.GONE);
        Button btnSubmit = view.findViewById(R.id.dialog_submit_feedback_btn_ok);
        Button btnCancel = view.findViewById(R.id.dialog_submit_feedback_btn_cancel);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = edInput.getText().toString();
                if (handler != null) {
                    handler.onClickSubmit(input);
                }
                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        return dialog;
    }

    private static View generateView(Context context, String title, String description, String titleOK,
                                     String titleCancel, DialogTypes type) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_format, null, false);
        TextView tvTitle = view.findViewById(R.id.dialog_tv_title);
        tvTitle.setTypeface(FontUtil.mediumTypeface(context));
        tvTitle.setText(title);

        TextView tvDescription = view.findViewById(R.id.dialog_tv_description);
        tvDescription.setTypeface(FontUtil.regularTypeface(context));
        tvDescription.setVisibility(StringUtil.isEmpty(description) ? View.GONE : View.VISIBLE);
        tvDescription.setText(StringUtil.isEmpty(description) ? "" : description);

        ImageView imgIcon = view.findViewById(R.id.dialog_img);
        View viewBackground = view.findViewById(R.id.background_line);
        switch (type) {
            case INFO:
                viewBackground.setBackgroundResource(R.color.color_compliant);
                imgIcon.setImageResource(R.drawable.ic_compliant);
                break;
            case ERROR:
                viewBackground.setBackgroundResource(R.color.color_non_compliant);
                imgIcon.setImageResource(R.drawable.ic_non_compliant);
                break;
            case WARNING:
                viewBackground.setBackgroundResource(R.color.color_warning);
                imgIcon.setImageResource(R.drawable.ic_warning);
                break;
            case NORMAL:
                viewBackground.setBackgroundResource(R.color.color_compliant);
                imgIcon.setVisibility(View.GONE);
                break;
            default:
                // do nothing;
                break;
        }

        boolean multipleBtn = true;
        if (StringUtil.isEmpty(titleOK) || StringUtil.isEmpty(titleCancel)) {
            multipleBtn = false;
        }

        Button closeBtn = view.findViewById(R.id.dialog_btn_close);
        View viewMultipleBtn = view.findViewById(R.id.dialog_view_multiple_buttons);
        Button okBtn = view.findViewById(R.id.dialog_btn_ok);
        Button cancelBtn = view.findViewById(R.id.dialog_btn_cancel);

        closeBtn.setVisibility(multipleBtn ? View.GONE : View.VISIBLE);
        viewMultipleBtn.setVisibility(multipleBtn ? View.VISIBLE : View.GONE);
        if (!StringUtil.isEmpty(titleOK)) {
            closeBtn.setText(titleOK);
            okBtn.setText(titleOK);
        }

        if (!StringUtil.isEmpty(titleCancel)) {
            cancelBtn.setText(titleCancel);
        }
        return view;
    }

    private static Dialog generateDialog(Context context, View view, boolean cancelOutSide) {
        Dialog dialog = new Dialog(context);
        try {
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCanceledOnTouchOutside(cancelOutSide);
            dialog.setContentView(view);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
            int maxWidth = DeviceUtil.convertDpToPixel(400, context);
            int measureWidth = (6 * DeviceUtil.getWithDevice(context)) / 7;
            dialog.getWindow().setLayout(Math.min(maxWidth, measureWidth), ViewGroup.LayoutParams.WRAP_CONTENT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dialog;
    }

    public static Dialog showDialogEula(Context context, String description, final MADialogHandler handler) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_eula, null, false);

        if (!StringUtil.isEmpty(description)) {
            TextView tvDescription = view.findViewById(R.id.eula_tv_content);
            tvDescription.setTypeface(FontUtil.regularTypeface(context));
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                tvDescription.setText(Html.fromHtml(description, Html.FROM_HTML_MODE_LEGACY));
            } else {
                tvDescription.setText(Html.fromHtml(description));
            }
        }

        final Dialog dialog = new Dialog(context, android.R.style.Theme_DeviceDefault_NoActionBar_Fullscreen);
        try {
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setContentView(view);
            dialog.setCancelable(false);
            dialog.show();
            dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Button btnAccept = view.findViewById(R.id.eula_btn_accept);

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (handler != null) {
                    handler.onClickOK();
                }
                dialog.dismiss();
            }
        });
        return dialog;
    }

}
