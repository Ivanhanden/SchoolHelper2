package com.handen.schoolhelper2.fragments.Timetable;

import android.content.res.Resources;

import com.handen.schoolhelper2.App;
import com.handen.schoolhelper2.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class Timetable {
    /**
     * Список дней для данного расписания
     */
    private ArrayList<Lesson> lessons = new ArrayList<>();
    public Resources mResources;


    public Timetable() {

        ArrayList<String> defaultTimetable = new ArrayList<>(Arrays.asList(App.getContext().getResources().getString(R.string.defaultTimetable).split(";")));
        for (int i = 0; i < defaultTimetable.size(); i++) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH.mm");
            try {
                Date begin = dateFormat.parse(defaultTimetable.get(i));
                i++;
                Date end = dateFormat.parse(defaultTimetable.get(i));
                lessons.add(new Lesson(begin, end));
            }
            catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public Date getLessonBegin(int index) {
        return lessons.get(index).getLessonBegin();
    }

    public Date getLessonEnd(int index) {
        return lessons.get(index).getLessonEnd();
    }

    public void setLessonBegin(Date date, int index) {
        lessons.get(index).setLessonBegin(date);
    }

    public void setLessonEnd(Date date, int index) {
        lessons.get(index).setLessonEnd(date);
    }


    /**
     * Внутренний класс Timetable
     */
    private class Lesson {

        Date lessonBegin;
        Date lessonEnd;

        public Lesson(Date date1, Date date2) {
            lessonBegin = date1;
            lessonEnd = date2;
        }

        public void setLessonBegin(Date lessonBegin) {
            this.lessonBegin = lessonBegin;
        }

        public void setLessonEnd(Date lessonEnd) {
            this.lessonEnd = lessonEnd;
        }

        public Date getLessonBegin() {
            return lessonBegin;
        }

        public Date getLessonEnd() {
            return lessonEnd;
        }
    }

}
