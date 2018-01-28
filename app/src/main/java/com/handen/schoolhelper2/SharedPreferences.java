package com.handen.schoolhelper2;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handen.schoolhelper2.fragments.Timetable.Timetable;
import com.handen.schoolhelper2.fragments.Week.Day;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Класс, который сохраняет и загружает данные из памяти
 * Created by Vanya on 16.07.2017.
 */

public class SharedPreferences {
    android.content.SharedPreferences mSharedPreferences;
    android.content.SharedPreferences.Editor editor;
    Context context;

    /**
     * Конструкор
     */
    public SharedPreferences(Context context) {
        mSharedPreferences = context.getSharedPreferences("SchoolHelper", Context.MODE_PRIVATE);
        editor = mSharedPreferences.edit();
        this.context = context;
    }

    /**
     * Метод, который сохраняет предметы и отметки по каждому предмету
     */
    public void saveSubjects(ArrayList<Subject> subjects) {
        String s = "";
        
        editor = mSharedPreferences.edit();
        Gson gson = new Gson();

        for (int i = 0; i < subjects.size(); i++) {
            if (i != 0)
                s += "\t";
            s += gson.toJson(subjects.get(i));
        }
        editor.putString("subjects", s);
        editor.commit();

    }
    /**
     * Метод, который загружает список предметов и отметки по каждому предмету
     */
    public ArrayList<Subject> loadSubjects() {
        ArrayList<Subject> ret = new ArrayList<>();
        Gson gson = new Gson();

        String s = mSharedPreferences.getString("subjects", "");

        if(s.equals("")) {
            for (String subject : context.getResources().getString(R.string.subjectList).split(",")) {
                ret.add(new Subject(subject));
            }
        }
        else {
            for (String subject : s.split("\t")) {
                ret.add(gson.fromJson(subject, Subject.class));
            }
        }
        return ret;
    }

    public void saveSchedule() {
        String s = "";
        
        editor = mSharedPreferences.edit();
        Gson gson = new Gson();

        for (int i = 0; i < MainActivity.schedule.size(); i++) {
            if (i != 0)
                s += "\t";
            s += gson.toJson(MainActivity.schedule.get(i));
        }
        editor.putString("schedule", s);
        editor.commit();
    }

    public ArrayList<ArrayList<Integer>> loadSchedule() {
        Gson gson = new Gson();
        ArrayList<ArrayList<Integer>> ret = new ArrayList<>();
        String schedules = mSharedPreferences.getString("schedule", "");
        if(schedules.equals("")) {
            for(int i = 0; i < 7; i ++) {
                ret.add(new ArrayList<>(Arrays.asList(-1, -1, -1, -1, -1, -1, -1, -1)));
            }
        }
        else {
            for (String schedule : schedules.split("\t")) {
                ArrayList<Integer> list = (gson.fromJson(schedule, new TypeToken<List<Integer>>() {
                }.getType()));
                ret.add(list);
            }
        }
        return ret;
    }

    public void saveTimetable() {
        String text = "";
        //Получаем sharedPreferences
        
        editor = mSharedPreferences.edit();

        Gson gson = new Gson();
        //Конвертируем каждое расписание звонков в json
        for (int i = 0; i < MainActivity.timetables.size(); i++) {
            if (i != 0)
                text += "\t";
            text += gson.toJson(MainActivity.timetables.get(i));
        }

        editor.putString("timetable", text);
        editor.commit();
    }

    public CopyOnWriteArrayList<Timetable> loadTimetable() {
       // MainActivity.timetables = new CopyOnWriteArrayList<>();
        CopyOnWriteArrayList<Timetable> ret = new CopyOnWriteArrayList<>();
        Gson gson = new Gson();
        
        //Получаем строку из данных
        String s = mSharedPreferences.getString("timetable", "");

        String[] array = s.split("\t");
        // Если Timetable не создавали
        if (s.length() == 0) {
            Timetable timetable = new Timetable();
            ret.add(timetable);
            for(Day day : MainActivity.days)
            {
                day.setTimetableIndex(0);
            }
        }
        else {
            for(String s1 : array) {
                //Формируем экземпляр класса Timetable из json
                Timetable timetable = gson.fromJson(s1, Timetable.class);
                ret.add(timetable);
            }
        }
        return ret;
    }

    public void saveSettings() {
        
        editor = mSharedPreferences.edit();
        Gson gson = new Gson();

        editor.putString("settings", gson.toJson(MainActivity.settings));

        editor.putInt("MAX_NOTE", Settings.MAX_NOTE);
        editor.putBoolean("IS_SHOW_NOTIFICATIONS", Settings.IS_SHOW_NOTIFICATIONS);
        editor.putBoolean("IS_MUTING", Settings.IS_MUTING);
        editor.putString("YELLOW_COLOR", Double.toString(Settings.YELLOW_COLOR));
        editor.putString("GREEN_COLOR", Double.toString(Settings.GREEN_COLOR));

        editor.commit();
    }

    public Settings loadSettings() {
        int maxNote;
        boolean isShowNotifications;
        boolean isMuting;
        double yellowColor;
        double greenColor;

        maxNote = mSharedPreferences.getInt("MAX_NOTE", 10);
        isShowNotifications = mSharedPreferences.getBoolean("IS_SHOW_NOTIFICATIONS", true);
        isMuting = mSharedPreferences.getBoolean("IS_MUTING", true);
        yellowColor = Double.parseDouble(mSharedPreferences.getString("YELLOW_COLOR", "5.5"));
        greenColor = Double.parseDouble(mSharedPreferences.getString("GREEN_COLOR", "8.0"));
        return new Settings(maxNote, isShowNotifications, isMuting, yellowColor, greenColor);
    }

    public void saveDays() {
        
        editor = mSharedPreferences.edit();
        Gson gson = new Gson();

        editor.putString("days", gson.toJson(MainActivity.days));
        editor.commit();
    }

    public ArrayList<Day> loadDays() {
        Gson gson = new Gson();
        ArrayList<Day> ret;
        ArrayList<String> daysNames = new ArrayList<>(Arrays.asList(context.getResources().getString(R.string.daysNames).split(",")));
        ArrayList<Integer> daysIds = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 0));
        
        String s = mSharedPreferences.getString("days", "");
        if(s.equals("")) {
            ret = new ArrayList<>();
            for(int i = 0; i < 7; i ++) {
                Day day = new Day(daysNames.get(i));
                ret.add(day);
            }
        }
        else {
            ret = gson.fromJson(s, new TypeToken<List<Day>>(){}.getType());
        }

        return ret;
    }

    public HashMap<Integer, Day> loadDaysMap() {
        ArrayList<Integer> daysIds = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 0));
        ArrayList<String> daysNames = new ArrayList<>(Arrays.asList(context.getResources().getString(R.string.daysNames).split(",")));

        String s = mSharedPreferences.getString("days", "");
        ArrayList<Day> days = loadDays();
        HashMap<Integer, Day> ret = new HashMap<>();
        if(s.equals("")) {
            for(int i = 0; i < 7; i ++) {
                Day day = new Day(daysNames.get(i));
                ret.put(daysIds.get(i), day);
            }
        }
        else {
            for(int i = 0; i < 7; i ++) {
                ret.put(daysIds.get(i), days.get(i));
            }
        }
        return ret;
    }

    public int loadCurrentNote() {
        Settings settings = this.loadSettings();
        return mSharedPreferences.getInt("note", settings.MAX_NOTE);
    }

    public void saveCurrentNote(int note) {
        editor = mSharedPreferences.edit();
        editor.putInt("note", note);
        editor.commit();
    }
}
