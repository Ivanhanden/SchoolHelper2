package com.handen.schoolhelper2;

import android.app.Application;
import android.content.Context;

/**
 * Created by Vanya on 21.01.2018.
 */

public class App extends Application {
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static Context getContext(){
        return mContext;
    }
}
