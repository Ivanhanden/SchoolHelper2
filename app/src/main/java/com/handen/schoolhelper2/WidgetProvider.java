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
    private static final String ACTION_UPDATE = "ACTION_UPDATE";

    public static final String EXTRA_NUMBER = "EXTRA_NUMBER";

    private Context mContext;
    private static String defaultSubjectName;
    private static int maxNote = MAX_NOTE; //TODO избавиться от MAXNOTE
    private static int currentNote = maxNote;
    private static Timetable timetable;
    private static ArrayList<Subject> mSubjects = new ArrayList<>();
    private static Day currentDay;
    private static int currentLessonNumber = 0;
    private static Subject currentSubject;

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sharedPreferences = new SharedPreferences(context);
        System.out.println("onReceive");
        super.onReceive(context, intent);
        System.err.println("onReceive");
        mContext = context;
        defaultSubjectName = context.getResources().getString(R.string.defaultLessonName);
        System.err.println(intent.getAction());

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_widget);

        if (currentNote <= 1)
            views.setViewVisibility(R.id.decrease_note, View.INVISIBLE);
        else
            views.setViewVisibility(R.id.decrease_note, View.VISIBLE);
        if (currentNote >= maxNote)
            views.setViewVisibility(R.id.increase_note, View.INVISIBLE);
        else
            views.setViewVisibility(R.id.increase_note, View.VISIBLE);

        if (currentLessonNumber == 0)
            views.setViewVisibility(R.id.previous_subject, View.INVISIBLE);
        else
            views.setViewVisibility(R.id.previous_subject, View.VISIBLE);
        if (currentLessonNumber >= 7)
            views.setViewVisibility(R.id.next_subject, View.INVISIBLE);
        else
            views.setViewVisibility(R.id.next_subject, View.VISIBLE);


        String action = intent.getAction();



        if (ACTION_PREVIOUS_SUBJECT.equals(action)) {
            System.err.println("previous");
            currentLessonNumber--;
        }
        if (ACTION_NEXT_SUBJECT.equals(action)) {
            System.err.println("next");
            currentLessonNumber++;
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
            ArrayList<Subject> subjects = sharedPreferences.loadSubjects();
            int subjectIndex = subjects.indexOf(currentSubject);
            if (subjectIndex != -1) {
                subjects.get(subjectIndex).getNotes().add(new Note(new Date(), Integer.toString(currentNote)));
                Toast.makeText(context, context.getString(R.string.noteAddedSuccessfully), Toast.LENGTH_SHORT).show();
                sharedPreferences.saveSubjects(subjects);
            }
        }
        if (ACTION_UPDATE.equals(action))
            currentLessonNumber = intent.getIntExtra(EXTRA_NUMBER, 0);

        if(currentLessonNumber > 7) {
            currentLessonNumber--;
            views.setViewVisibility(R.id.next_subject, View.INVISIBLE);
        }

        getWidgetData();


        currentSubject = mSubjects.get(currentLessonNumber);

        bindSubjectData(views);

        views.setTextViewText(R.id.note, Integer.toString(currentNote));

        ComponentName appWidget = new ComponentName(context, WidgetProvider.class);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        appWidgetManager.updateAppWidget(appWidget, views);
    }

    void getWidgetData() {
        SharedPreferences sharedPreferences = new SharedPreferences(mContext);
        maxNote = MAX_NOTE;
        Date currentDate = new Date();

        currentDay = sharedPreferences.loadDaysMap().get(currentDate.getDay()); //Текущий день

        if (currentDay != null)
            if (currentDay.getTimetableIndex() != -1)
                timetable = sharedPreferences.loadTimetable().get(currentDay.getTimetableIndex()); //Получаем текущее расписание звонков

        fillSubjects();
    }

    static void bindSubjectData(RemoteViews views) {

        views.setTextViewText(R.id.classroom, currentSubject.getClassroom());
        views.setTextViewText(R.id.teacher, currentSubject.getTeacherName());

        Date begin = timetable.getLessonBegin(currentLessonNumber);
        Date end = timetable.getLessonEnd(currentLessonNumber);
        String timetable = new SimpleDateFormat("H.mm").format(begin) + " - " +
                new SimpleDateFormat("H.mm").format(end);
        views.setTextViewText(R.id.timetable, timetable);


        String subjectTitle = currentSubject.getName();
        double average = currentSubject.getAverage();

        if (average != -1.0) {
            average = (int) (average * 100) / 100.0;
            if ((int) average == average)
                subjectTitle += "   (" + Integer.toString((int) average) + ")";
            else
                subjectTitle += "   (" + Double.toString(average) + ")";
        }
        views.setTextViewText(R.id.subject_name, subjectTitle);
    }

    void fillSubjects() {
        SharedPreferences sharedPreferences = new SharedPreferences(mContext);
        int scheduleId = -1;
        ArrayList<Day> days = sharedPreferences.loadDays();
        for (Day day : days) {
            if(day.getName().equals(currentDay.getName()))
                scheduleId = days.indexOf(day);
        }
//        int scheduleId = sharedPreferences.loadDays().indexOf(currentDay);
        if (scheduleId != -1) {
            ArrayList<Integer> subjectIndexes = sharedPreferences.loadSchedule().get(scheduleId);
            mSubjects.clear();
            for (int i : subjectIndexes) {
                if (i > -1)
                    mSubjects.add(sharedPreferences.loadSubjects().get(i));
                else
                    mSubjects.add(new Subject(defaultSubjectName));
            }
        }
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
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}

