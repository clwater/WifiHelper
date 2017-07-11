package com.clwater.wifihelper.Utils;

/**
 * Created by gengzhibo on 17/7/11.
 */

public class WIFI {
    String ssid;
    String bssid;
    String pwd;


    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
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
