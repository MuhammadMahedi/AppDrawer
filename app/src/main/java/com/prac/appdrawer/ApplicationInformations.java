package com.prac.appdrawer;

import android.graphics.drawable.Drawable;

public class ApplicationInformations {
    private String appName;
    private String pkgName;
    private Drawable appIcon;

    public ApplicationInformations(String appName,String pkgName, Drawable appIcon) {
        this.appName = appName;
        this.pkgName = pkgName;
        this.appIcon = appIcon;
    }

    public String getAppName() {
        return appName;
    }
    public String getPkgName() {
        return pkgName;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }
}
