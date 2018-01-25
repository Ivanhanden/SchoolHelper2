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
    private static int maxNote;
    private static int currentNote;
    private static Timetable timetable;
    private static ArrayList<Subject> mSubjects = new ArrayList<>();
    private static Day currentDay;
    private static int currentLessonNumber = 0;
    private static Subject currentSubject;

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        SharedPreferences sharedPreferences = new SharedPreferences(context);

        mContext = context;
        defaultSubjectName = context.getResources().getString(R.string.defaultLessonName);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_widget);

        String action = intent.getAction();
        System.out.println(action);
        assert action != null;
        switch (action) {
            case ACTION_PREVIOUS_SUBJECT:
                currentLessonNumber--;
                break;
            case ACTION_NEXT_SUBJECT:
                currentLessonNumber++;
                break;
            case ACTION_INCREASE:
                currentNote++;
                views.setTextViewText(R.id.note, Integer.toString(currentNote));
                break;
            case ACTION_DECREASE:
                currentNote--;
                views.setTextViewText(R.id.note, Integer.toString(currentNote));
                break;
            case ACTION_ADD:
                ArrayList<Subject> subjects = sharedPreferences.loadSubjects();
                int subjectIndex = -1;
                for(Subject subject : subjects) {
                    fillSubjects();
                    currentSubject = mSubjects.get(currentLessonNumber);
                    if(subject.getName().equals(currentSubject.getName())) {
                        subjectIndex = subjects.indexOf(subject);
                        break;
                    }
                }
                if (subjectIndex != -1) {
                    Note newNote = new Note(new Date(), Integer.toString(currentNote));
                    int index = subjects.get(subjectIndex).getNotes().size() - 1;
                    subjects.get(subjectIndex).getNotes().add(index, newNote);
                    Toast.makeText(context, context.getString(R.string.noteAddedSuccessfully), Toast.LENGTH_SHORT).show();
                    sharedPreferences.saveSubjects(subjects);
                    if(MainActivity.subjectArrayList != null)
                        updateApplication();
                }
                break;
            case ACTION_UPDATE:
                currentLessonNumber = intent.getIntExtra(EXTRA_NUMBER, 0);
                Settings settings1 = sharedPreferences.loadSettings();
                maxNote = settings1.MAX_NOTE;
                currentNote = maxNote;
                break;
            default:
                System.out.println("default");
                currentLessonNumber = 0;
                Settings settings = sharedPreferences.loadSettings();
                maxNote = settings.MAX_NOTE;
                currentNote = maxNote;
                break;
        }

        if (currentNote <= 1) {
            views.setViewVisibility(R.id.decrease_note, View.INVISIBLE);
            views.setBoolean(R.id.decrease_note, "setEnabled", false);
            views.setViewVisibility(R.id.increase_note, View.VISIBLE);
        }
        else {
            views.setViewVisibility(R.id.decrease_note, View.VISIBLE);
            views.setBoolean(R.id.decrease_note, "setEnabled", true);
        }
        if (currentNote >= maxNote) {
            views.setViewVisibility(R.id.increase_note, View.INVISIBLE);
            views.setBoolean(R.id.increase_note, "setEnabled", false);
        }
        else {
            views.setViewVisibility(R.id.increase_note, View.VISIBLE);
            views.setBoolean(R.id.increase_note, "setEnabled", true);
        }
        if (currentLessonNumber <= 0) {
            views.setViewVisibility(R.id.previous_subject, View.INVISIBLE);
            views.setBoolean(R.id.previous_subject, "setEnabled", false);
        }
        else {
            views.setViewVisibility(R.id.previous_subject, View.VISIBLE);
            views.setBoolean(R.id.previous_subject, "setEnabled", true);
        }
        if (currentLessonNumber >= 7) {
            views.setViewVisibility(R.id.next_subject, View.INVISIBLE);
            views.setBoolean(R.id.next_subject, "setEnabled", false);
        }
        else {
            views.setViewVisibility(R.id.next_subject, View.VISIBLE);
            views.setBoolean(R.id.next_subject, "setEnabled", true);
        }
        if (currentLessonNumber > 7) {
            currentLessonNumber--;
            views.setViewVisibility(R.id.next_subject, View.INVISIBLE);
        }
        if(currentLessonNumber < 0) {
            currentLessonNumber++;
            views.setViewVisibility(R.id.previous_subject, View.INVISIBLE);
        }
        if(currentNote < 1) {
            currentNote++;
            views.setViewVisibility(R.id.decrease_note, View.INVISIBLE);
            views.setViewVisibility(R.id.increase_note, View.VISIBLE);
        }
        if(currentNote > maxNote) {
            currentNote--;
            views.setViewVisibility(R.id.increase_note, View.INVISIBLE);
        }

        getWidgetData();

        currentSubject = mSubjects.get(currentLessonNumber);

        bindSubjectData(views);
        System.out.println("CurrentNote: " + currentNote);
        views.setTextViewText(R.id.note, Integer.toString(currentNote));

        ComponentName appWidget = new ComponentName(context, WidgetProvider.class);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        appWidgetManager.updateAppWidget(appWidget, views);
    }

    private void updateApplication() {
        MainActivity.subjectArrayList = new ArrayList<>(new SharedPreferences(mContext).loadSubjects());
       // MainActivity.set
    }

    void getWidgetData() {
        SharedPreferences sharedPreferences = new SharedPreferences(mContext);
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
        int currentScheduleId = -1;
        ArrayList<Day> days = sharedPreferences.loadDays();
        for (Day day : days) {
            if(day.getName().equals(currentDay.getName()))
                currentScheduleId = days.indexOf(day);
        }
        if (currentScheduleId != -1) {
            ArrayList<Integer> subjectIndexes = sharedPreferences.loadSchedule().get(currentScheduleId);
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

