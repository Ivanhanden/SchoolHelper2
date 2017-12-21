package com.handen.schoolhelper2;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.handen.schoolhelper2.fragments.Timetable.Timetable;
import com.handen.schoolhelper2.fragments.Week.Day;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.handen.schoolhelper2.MainActivity.days;
import static com.handen.schoolhelper2.MainActivity.daysMap;
import static com.handen.schoolhelper2.MainActivity.schedule;
import static com.handen.schoolhelper2.MainActivity.subjectArrayList;
import static com.handen.schoolhelper2.MainActivity.timetables;
import static com.handen.schoolhelper2.Settings.MAX_NOTE;

/**
 * Implementation of App Widget functionality.
 */
public class WidgetProvider extends AppWidgetProvider {

    private static final String ACTION_PREVIOUS_SUBJECT = "ACTION_PREVIOUS_SUBJECT";
    private static final String ACTION_NEXT_SUBJECT = "ACTION_NEXT_SUBJECT";
    private static final String ACTION_INCREASE = "ACTION_INCREASE";
    private static final String ACTION_DECREASE = "ACTION_DECREASE";
    private static final String ACTION_ADD = "ACTION_ADD";

    private Context mContext;
    private static String defaultSubjectName;
    private static int maxNote = MAX_NOTE;
    private static int currentNote = maxNote;
    private static Timetable timetable;
    private static ArrayList<Subject> mSubjects = new ArrayList<>();
    private static Day currentDay;
    private static int currentLessonNumber = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        System.err.println("onReceive");
        mContext = context;
        defaultSubjectName = context.getResources().getString(R.string.defaultLessonName);
        System.err.println(intent.getAction());

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_widget);

        String action = intent.getAction();

        if (ACTION_PREVIOUS_SUBJECT.equals(action)) {
            System.err.println("previous");
        }
        if (ACTION_NEXT_SUBJECT.equals(action)) {
            System.err.println("next");
        }
        if (ACTION_INCREASE.equals(action)) {
            System.err.println("increase");
            currentNote++;
            views.setTextViewText(R.id.note, Integer.toString(currentNote));
        }
        if (ACTION_DECREASE.equals(action)) {
            System.err.println("decrease");
            currentNote--;
            views.setTextViewText(R.id.note, Integer.toString(currentNote));
        }
        if (ACTION_ADD.equals(action)) {
            System.err.println("add");
        }

        if(currentNote <= 1)
            views.setViewVisibility(R.id.decrease_note, View.INVISIBLE);
        else
            views.setViewVisibility(R.id.decrease_note, View.VISIBLE);
        if(currentNote >= maxNote - 1)
            views.setViewVisibility(R.id.increase_note, View.INVISIBLE);
        else
            views.setViewVisibility(R.id.increase_note, View.VISIBLE);


        ComponentName appWidget = new ComponentName(context, WidgetProvider.class);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        appWidgetManager.updateAppWidget(appWidget, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        System.err.println("onUpdate");
        mContext = context;
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_widget);
        // Construct an Intent which is pointing this class.

        Intent previousIntent = new Intent(context, WidgetProvider.class);
        previousIntent.setAction(ACTION_PREVIOUS_SUBJECT);
        PendingIntent previous = PendingIntent.getBroadcast(context, 0, previousIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.previous_subject, previous);

        Intent nextIntent = new Intent(context, WidgetProvider.class);
        nextIntent.setAction(ACTION_NEXT_SUBJECT);
        PendingIntent next = PendingIntent.getBroadcast(context, 1, nextIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.next_subject, next);

        Intent increaseIntent = new Intent(context, WidgetProvider.class);
        increaseIntent.setAction(ACTION_INCREASE);
        PendingIntent increase = PendingIntent.getBroadcast(context, 2, increaseIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.increase_note, increase);

        Intent decreaseIntent = new Intent(context, WidgetProvider.class);
        decreaseIntent.setAction(ACTION_DECREASE);
        PendingIntent decrease = PendingIntent.getBroadcast(context, 3, decreaseIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.decrease_note, decrease);

        Intent addIntent = new Intent(context, WidgetProvider.class);
        addIntent.setAction(ACTION_ADD);
        PendingIntent add = PendingIntent.getBroadcast(context, 4, addIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.add_note, add);

        views.setTextViewText(R.id.note, Integer.toString(currentNote));

        if(subjectArrayList == null && timetables == null && days == null &&
                daysMap == null && schedule == null) {
            Toast.makeText(context, "Всё null", Toast.LENGTH_SHORT).show();
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
        getWidgetData();
        bindSubjectData(views);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    static void getWidgetData() {
        maxNote = MAX_NOTE;
        Date currentDate = new Date();

        currentDay = daysMap.get(currentDate.getDay()); //Текущий день

        if(currentDay.getTimetableIndex() != -1)
            timetable = MainActivity.timetables.get(currentDay.getTimetableIndex()); //Получаем текущее расписание звонков

        fillSubjects();
    }
    static void bindSubjectData(RemoteViews views) {
        Subject subject = mSubjects.get(currentLessonNumber);
        views.setTextViewText(R.id.classroom, subject.getClassroom());
        views.setTextViewText(R.id.teacher, subject.getTeacherName());
        if(timetable != null) {
            Date begin = timetable.getLessonBegin(currentLessonNumber);
            Date end = timetable.getLessonEnd(currentLessonNumber);
            String timetable = new SimpleDateFormat("H.mm").format(begin) + " - " +
                    new SimpleDateFormat("H.mm").format(end);
            views.setTextViewText(R.id.timetable, timetable);
        }
    }

    static void fillSubjects() {
        ArrayList<Integer> subjectIndexes = schedule.get(days.indexOf(currentDay));
        mSubjects.clear();
        for (int i : subjectIndexes) {
            if (i > -1)
                mSubjects.add(subjectArrayList.get(i));
            else
                mSubjects.add(new Subject(defaultSubjectName));
        }
    }
}

