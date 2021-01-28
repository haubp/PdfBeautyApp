package opswat.com.view.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import opswat.com.R;
import opswat.com.adapter.ApplicationAdapter;
import opswat.com.constant.MAContant;
import opswat.com.device.DeviceDefine;
import opswat.com.enums.ApplicationStatus;
import opswat.com.network.model.application.Application;
import opswat.com.network.model.connection.Connection;
import opswat.com.util.ColorUtil;
import opswat.com.util.CommonUtil;
import opswat.com.util.DateUtil;

/**
 * Created by LenVo on 7/15/18.
 */

public class ItemApplicationViewHolder extends BaseViewHolder {
    public ItemApplicationViewHolder(View itemView, Context context) {
        super(itemView, context);
    }

    public void onBind(final Application application, int type) {
        if (type == ApplicationAdapter.TYPE_APP_DETAIL) {
            itemView.setBackgroundColor(ColorUtil.getColor(context, R.color.white));
            itemView.findViewById(R.id.item_application_separate_line).setVisibility(View.GONE);
        }
        String appName = application.getAppName() + " " + application.getVersion();
        setText(R.id.item_application_tv_name, appName);

        itemView.findViewById(R.id.item_application_img_status)
                .setVisibility(application.getStatus() == ApplicationStatus.INPROGRESS? View.VISIBLE :
                        View.GONE);
        itemView.findViewById(R.id.item_application_result)
                .setVisibility(application.getStatus() != ApplicationStatus.INPROGRESS? View.VISIBLE :
                        View.GONE);

        int statusColor = R.color.color_compliant;
        if (application.getStatus() == ApplicationStatus.SUSPECT) {
            statusColor = R.color.color_warning;
        } else if (application.getStatus() == ApplicationStatus.INFECTED) {
            statusColor = R.color.color_non_compliant;
        }

        String status = (application.getStatus() == ApplicationStatus.UNKNOWN) ? MAContant.UNKNOWN_TEXT :
                        (application.getTotalDetectedAvs() + "/" + application.getTotalAvs());
        setText(R.id.item_application_tv_status, status);
        setTextColor(R.id.item_application_tv_status, statusColor);

        final TextView tvApplication = itemView.findViewById(R.id.item_application_tv_name);
        tvApplication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (application.getDataId() != null && !application.getDataId().isEmpty() && application.getStatus() != ApplicationStatus.UNKNOWN) {
                    String url = DeviceDefine.METADEFENDER_SERVER_FILE_RESULT_URL +
                            application.getDataId() + "/regular/overview";
                    CommonUtil.openBrowser(context, url);
                }
            }
        });
    }
}
