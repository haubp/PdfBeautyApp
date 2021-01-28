package opswat.com.handler;

import opswat.com.network.model.response.MACloudErrorResponse;
import opswat.com.network.model.response.MACloudResponse;

public interface MACloudHandler {
    void onFailed(MACloudErrorResponse response);
    void onSuccess(MACloudResponse response);
}
