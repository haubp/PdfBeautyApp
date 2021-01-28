package opswat.com.view.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;

import opswat.com.R;
import opswat.com.device.DeviceDefine;
import opswat.com.device.connection.IpConnectionHelper;
import opswat.com.network.model.connection.Connection;
import opswat.com.util.CommonUtil;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by LenVo on 7/15/18.
 */

public class ItemConnectionViewHolder extends BaseViewHolder {
    public ItemConnectionViewHolder(View itemView, Context context) {
        super(itemView, context);
    }

    public void onBind(Connection connection) {
        setText(R.id.item_connection_ip, connection.getAddress());
        String subTextItem = "";
        int colorStatus = R.color.color_compliant;

        if (connection.isScanned()) {
            subTextItem = connection.getGeoInfo() != null ? connection.getGeoInfo().getFullLocation() : context.getString(R.string.unknown);
            int numDetected = connection.getLookupResults() != null ? connection.getLookupResults().getDetectedBy() : 0;
            if (numDetected >= 3) {
                colorStatus = R.color.color_non_compliant;
            } else if (numDetected >= 1){
                colorStatus = R.color.color_warning;
            }
        }

        itemView.findViewById(R.id.item_connection_location).setVisibility(connection.isScanned()? View.VISIBLE : View.GONE);
        setText(R.id.item_connection_location, subTextItem);
        setTextColor(R.id.item_connection_location, colorStatus);

        itemView.findViewById(R.id.item_connection_img_status).setVisibility(connection.isScanned()? View.GONE : View.VISIBLE);
        itemView.findViewById(R.id.item_connection_tv_status).setVisibility(connection.isScanned()? View.VISIBLE : View.GONE);

        try {
            if (!connection.isScanned()) {
                GifDrawable gifFromResource = new GifDrawable(context.getResources(), R.drawable.loading);
                GifImageView gifStatus = itemView.findViewById(R.id.item_connection_img_status);
                gifStatus.setImageDrawable(gifFromResource);
            } else {
                String result = (connection.getLookupResults() == null || connection.getLookupResults().getSources() == null) ? "N/A" :
                        (connection.getLookupResults().getDetectedBy() + "/" + connection.getLookupResults().getSources().size());
                setText(R.id.item_connection_tv_status, result);
                setTextColor(R.id.item_connection_tv_status, colorStatus);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        final TextView tvIp = itemView.findViewById(R.id.item_connection_ip);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = DeviceDefine.METADEFENDER_SERVER_IP_RESULT_URL +
                        IpConnectionHelper.getBase64Ip(tvIp.getText().toString());
                CommonUtil.openBrowser(context, url);
            }
        });

    }
}
