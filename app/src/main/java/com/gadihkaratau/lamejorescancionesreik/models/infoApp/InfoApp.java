package com.gadihkaratau.lamejorescancionesreik.models.infoApp;

import com.google.gson.annotations.SerializedName;

public class InfoApp {


    @SerializedName("updated")
    private long updated;
    @SerializedName("version")
    private String version;
    @SerializedName("recentChanges")
    private String recentChanges;

    public long getUpdated() {
        return updated;
    }

    public void setUpdated(long updated) {
        this.updated = updated;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getRecentChanges() {
        return recentChanges;
    }

    public void setRecentChanges(String recentChanges) {
        this.recentChanges = recentChanges;
    }
}
