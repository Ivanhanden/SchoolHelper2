package com.handen.schoolhelper2;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handen.schoolhelper2.fragments.Timetable.Timetable;
import com.handen.schoolhelper2.fragments.Week.Day;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Класс, который сохраняет и загружает данные из памяти
 * Created by Vanya on 16.07.2017.
 */

public class SharedPreferences {
    android.content.SharedPreferences mSharedPreferences;
    android.content.SharedPreferences.Editor mEditor;

    /**
     * Конструкор
     */
    public SharedPreferences() {

    }

    /**
     * Метод, который сохраняет предметы и отметки по каждому предмету
     *
     * @param context
     */
    public void saveSubjects(Context context) {
        String s = "";
        mSharedPreferences = context.getSharedPreferences("SchoolHelper", Context.MODE_PRIVATE);
        android.content.SharedPreferences.Editor prefsEditor = mSharedPreferences.edit();
        Gson gson = new Gson();

        for (int i = 0; i < MainActivity.subjectArrayList.size(); i++) {
            if (i != 0)
                s += "\t";
            s += gson.toJson(MainActivity.subjectArrayList.get(i));
        }
        prefsEditor.putString("subjects", s);
        prefsEditor.commit();

    }

    /**
     * Метод, который загружает список предметов и отметки по каждому предмету
     *
     * @param context
     */

    public void loadSubjects(Context context) {
        MainActivity.subjectArrayList = new ArrayList<>();
        Gson gson = new Gson();
        mSharedPreferences = context.getSharedPreferences("SchoolHelper", Context.MODE_PRIVATE);

        for (String s : mSharedPreferences.getString("subjects", "").split("\t")) {
            if (s.equals(""))
                continue;
            MainActivity.subjectArrayList.add(gson.fromJson(s, Subject.class));
        }
    }

    public void saveSchedule(Context context) {
        String s = "";
        mSharedPreferences = context.getSharedPreferences("SchoolHelper", Context.MODE_PRIVATE);
        android.content.SharedPreferences.Editor prefsEditor = mSharedPreferences.edit();
        Gson gson = new Gson();

        for (int i = 0; i < MainActivity.schedule.size(); i++) {
            if (i != 0)
                s += "\t";
            s += gson.toJson(MainActivity.schedule.get(i));

        }
        prefsEditor.putString("schedule", s);
        prefsEditor.commit();

    }

    public void loadSchedule(Context context) {
        Gson gson = new Gson();
        mSharedPreferences = context.getSharedPreferences("SchoolHelper", Context.MODE_PRIVATE);
        int i = 0;
        ArrayList<Integer> list;

        for (String s : mSharedPreferences.getString("schedule", "").split("\t")) {
            if (s.equals(""))
                list = new ArrayList<>(Arrays.asList(-1, -1, -1, -1, -1, -1, -1, -1));
            else
                list = gson.fromJson(s, new TypeToken<List<Integer>>() {
                }.getType());
            if(i < MainActivity.schedule.size())
                MainActivity.schedule.set(i++, list);
        }
    }

    public void saveTimetable(Context context) {
        String text = "";
        //Получаем sharedPreferences
        mSharedPreferences = context.getSharedPreferences("SchoolHelper", Context.MODE_PRIVATE);
        android.content.SharedPreferences.Editor prefsEditor = mSharedPreferences.edit();

        Gson gson = new Gson();
        //Конвертируем каждое расписание звонков в json
        for (int i = 0; i < MainActivity.timetables.size(); i++) {
            if (i != 0)
                text += "\t";
            text += gson.toJson(MainActivity.timetables.get(i));
        }

        prefsEditor.putString("timetable", text);
        prefsEditor.commit();
    }

    public void loadTimetable(Context context) {
        MainActivity.timetables = new CopyOnWriteArrayList<>();
        Gson gson = new Gson();
        mSharedPreferences = context.getSharedPreferences("SchoolHelper", Context.MODE_PRIVATE);
        //Получаем строку из данных
        String s = mSharedPreferences.getString("timetable", "");

        String[] array = s.split("\t");
        // Если Timetable не создавали
        if (s.length() == 0) {
            Timetable timetable = new Timetable();
            MainActivity.timetables.add(timetable);
            for(Day day : MainActivity.days)
            {
                day.setTimetableIndex(0);
            }
        }
        else {
            for(String s1 : array)
            {
                //Формируем экземпляр класса Timetable из json
                Timetable timetable = gson.fromJson(s1, Timetable.class);
                MainActivity.timetables.add(timetable);
            }
        }
    }

    public void saveSettings(Context context) {

        mSharedPreferences = context.getSharedPreferences("SchoolHelper", Context.MODE_PRIVATE);
        android.content.SharedPreferences.Editor prefsEditor = mSharedPreferences.edit();
        Gson gson = new Gson();

        prefsEditor.putString("settings", gson.toJson(MainActivity.settings));

        prefsEditor.putInt("MAX_NOTE", Settings.MAX_NOTE);
        prefsEditor.putBoolean("IS_SHOW_NOTIFICATIONS", Settings.IS_SHOW_NOTIFICATIONS);
        prefsEditor.putBoolean("IS_MUTING", Settings.IS_MUTING);
        prefsEditor.putString("YELLOW_COLOR", Double.toString(Settings.YELLOW_COLOR));
        prefsEditor.putString("GREEN_COLOR", Double.toString(Settings.GREEN_COLOR));



        prefsEditor.commit();
    }

    public void loadSettings(Context context) {

        int maxNote;
        boolean isShowNotifications;
        boolean isMuting;
        double yellowColor;
        double greenColor;

     //   Gson gson = new Gson();
        mSharedPreferences = context.getSharedPreferences("SchoolHelper", Context.MODE_PRIVATE);
        String s = mSharedPreferences.getString("settings", "");

        maxNote = mSharedPreferences.getInt("MAX_NOTE", 10);
        isShowNotifications = mSharedPreferences.getBoolean("IS_SHOW_NOTIFICATIONS", true);
        isMuting = mSharedPreferences.getBoolean("IS_MUTING", true);
        yellowColor = Double.parseDouble(mSharedPreferences.getString("YELLOW_COLOR", "5.5"));
        greenColor = Double.parseDouble(mSharedPreferences.getString("GREEN_COLOR", "8.0"));
        MainActivity.settings = new Settings(maxNote, isShowNotifications, isMuting, yellowColor, greenColor);
    }

    public void saveDays(Context context) {

        mSharedPreferences = context.getSharedPreferences("SchoolHelper", Context.MODE_PRIVATE);
        android.content.SharedPreferences.Editor prefsEditor = mSharedPreferences.edit();
        Gson gson = new Gson();

        prefsEditor.putString("days", gson.toJson(MainActivity.days));
        prefsEditor.commit();
    }

    public void loadDays(Context context) {
        Log.d("loadDays", "loadDays");
        Gson gson = new Gson();
        mSharedPreferences = context.getSharedPreferences("SchoolHelper", Context.MODE_PRIVATE);
        String s = mSharedPreferences.getString("days", "");
        if(!s.equals("")) {
            MainActivity.days = gson.fromJson(s, new TypeToken<List<Day>>() {
            }.getType());
            for(int i = 0; i < 7; i ++)
            {
                MainActivity.daysMap.put(MainActivity.daysIds.get(i), MainActivity.days.get(i));
            }

        }
        else {
            MainActivity.days = new ArrayList<>();
            for(int i = 0; i < 7; i ++)
            {
                Day day = new Day(MainActivity.daysNames.get(i));
                MainActivity.days.add(day);
                MainActivity.daysMap.put(MainActivity.daysIds.get(i), day);
            }
        }
    }

    public void loadTests(Context context) {
        Gson gson = new Gson();
        mSharedPreferences = context.getSharedPreferences("SchoolHelper", Context.MODE_PRIVATE);
        String s = mSharedPreferences.getString("tests", "");
        if(s.equals("")) {
            MainActivity.tests = new Tests();
        }
        else {
            MainActivity.tests = gson.fromJson(s, Tests.class);
        }
    }

    public void saveTests(Context context) {
        mSharedPreferences = context.getSharedPreferences("SchoolHelper", Context.MODE_PRIVATE);
        android.content.SharedPreferences.Editor prefsEditor = mSharedPreferences.edit();
        Gson gson = new Gson();

        prefsEditor.putString("tests", gson.toJson(MainActivity.tests));
        prefsEditor.commit();
    }

}
