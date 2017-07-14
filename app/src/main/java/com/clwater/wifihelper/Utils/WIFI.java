package com.clwater.wifihelper.Utils;

import java.util.List;

/**
 * Created by gengzhibo on 17/7/11.
 */

public class WIFI {
    String ssid;
    String bssid;
    List<String> pwd;
    String statu;

    public String getStatu() {
        return statu;
    }

    public void setStatu(String statu) {
        this.statu = statu;
    }

    public List<String> getPwd() {
        return pwd;
    }

    public void setPwd(List<String> pwd) {
        this.pwd = pwd;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getBssid() {
        return bssid;
    }

    public void setBssid(String bssid) {
        this.bssid = bssid;
    }
}
