package com.handen.schoolhelper2;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.Random;

/**
 * Created by user2 on 07.12.2017.
 */

public class UpdateWidgetService extends Service {
    //http://www.vogella.com/tutorials/AndroidWidgets/article.html#updates Удалено
    //Current
    //https://medium.com/android-bits/android-widgets-ad3d166458d3

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
