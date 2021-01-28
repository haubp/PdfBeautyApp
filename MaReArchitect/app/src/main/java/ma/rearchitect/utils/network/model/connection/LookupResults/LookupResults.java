package opswat.com.network.model.connection.LookupResults;

import com.google.gson.JsonArray;
import com.google.gson.annotations.SerializedName;

public class LookupResults {
    @SerializedName("start_time")
    private String startTime;

    @SerializedName("detected_by")
    private int detectedBy;

    @SerializedName("sources")
    private JsonArray sources;

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public int getDetectedBy() {
        return detectedBy;
    }

    public void setDetectedBy(int detectBy) {
        this.detectedBy = detectBy;
    }

    public JsonArray getSources() {
        return sources;
    }

    public void setSources(JsonArray sources) {
        this.sources = sources;
    }
}
