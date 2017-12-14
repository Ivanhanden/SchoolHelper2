package com.handen.schoolhelper2;

import android.content.Context;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Класс, описывающий предмет
 * Created by Vanya on 14.07.2017.
 */

public class Subject implements Serializable {
    private String name;
    private ArrayList<Note> notes = new ArrayList<>();
    private String teacherName;
    private String classroom;
    private Tests tests;

    //Конструктор
    public Subject(String name) {

        this.name = name;
        notes.add(new Note());
        teacherName = "";
        classroom = "";
        tests = new Tests();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Note> getNotes() {
        return notes;
    }

    public float getAverage() {
        float summ = 0;
        if (notes.size() != 1) {
            for (Note note : notes) {
                if(!note.getNote().replace(" ", "").equals("")) {
                    if (Integer.parseInt(note.getNote().replace(" ", "")) > -1) {
                        summ += Float.parseFloat(note.getNote().replace(" ", ""));
                    }
                }
            }
            return summ / (notes.size() - 1);
        }
        else
            return -1;
    }

    public String getNotesToNextLevel() {
        String ret = "";

        if (notes.size() > 2) {

            float average = getAverage();

            if (average < Settings.MAX_NOTE - 1) {
                int maxNote = getMaxNote();
                int i = 1;
                for (; i < 5; i++) {
                    if (Math.round(average) != Math.round((average * notes.size() + i * maxNote) / (notes.size() + i))) {
                        break;
                    }
                }
                if (i < 5)
                    ret = "+" + Integer.toString(i) + "(" + Integer.toString(maxNote) + ")";
            }
        }
        return ret;
    }

    public int getMaxNote() {
        int ret = 0;
        if (notes.size() != 1) {
            for (Note note : notes) {
                if(!note.getNote().replace(" ","").equals("")) {
                    if (Integer.parseInt(note.getNote().replace(" ","")) > ret) {
                        ret = Integer.parseInt(note.getNote().replace(" ", ""));
                    }
                }
            }
        }
        return ret;
    }

    /**
     * Метод, заполняющий список предметов в первый раз
     *

     */
    public static void initializeSubjects(Context context) {
        for (String s : context.getResources().getString(R.string.subjectList).split(",")) {
            MainActivity.subjectArrayList.add(new Subject(s));
        }
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getClassroom() {
        return classroom;
    }

    public void setClassroom(String classroom) {
        this.classroom = classroom;
    }

    public int getTestCount() {
        return tests.getCount();
    }
    public Date getClosestTestDate() {
        Date returnDate = null;
        if(getTestCount() > 0)
            returnDate = tests.getClosest().getDate();
        return  returnDate;
    }

    public void addTest(Date date) {
        tests.addTest(date);
    }

}
