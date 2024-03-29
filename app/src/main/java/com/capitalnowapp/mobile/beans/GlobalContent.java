
package com.capitalnowapp.mobile.beans;

import java.io.Serializable;

public class GlobalContent implements Serializable {
    private static GlobalContent instance;
    private UserLoginData userLoginData;

    public static GlobalContent getInstance() {
        if (instance == null)
            instance = new GlobalContent();

        return instance;
    }

    public UserLoginData getUserLoginData() {
        return userLoginData;
    }

    public void setUserLoginData(UserLoginData userLoginData) {
        this.userLoginData = userLoginData;
    }
}
