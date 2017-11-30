package com.handen.schoolhelper2;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Класс, отвечающий за расписание контрольных работ
 * Created by Vanya on 23.11.2017.
 *
 */

public class Tests {

    public static ArrayList<Test> tests;
    private static SharedPreferences sharedPreferences;
    private Context context;


   // static {
   //     sharedPreferences = new SharedPreferences();
   // }

    public static int getCount() {
        //Проверяем на актуальность
        for(Test test : tests) {
            if(test.getDate().getTime() < new Date().getTime()) {
                tests.remove(test);
            }
        }
        return tests.size();
    }

    public static boolean hasTest(Subject subject) {
        //Проверяем на актуальность
        boolean ret = false;
        for(Test test : tests) {
            if(test.getDate().getTime() < new Date().getTime()) {
                tests.remove(test);
            if(test.getSubject().equals(subject))
                ret = true;
            }
        }
        return ret;

    }
    public static Test getTest(Subject subject) {
        for(Test test : tests) {
            if(test.getDate().getTime() < new Date().getTime()) {
                tests.remove(test);
            }
            if(test.getSubject().equals(subject))
                return test;
        }
        return null;
    }

    public static String getTestDate(Subject subject) {
        Test test = tests.get(0);
        for(Test test1 : tests) {
            if(test1.getDate().getTime() < new Date().getTime()) {
                tests.remove(test1);
                if(test1.getSubject().equals(subject))
                    test = test1;
            }
        }
        return new SimpleDateFormat("dd.MM.yyyy").format(test.getDate());
        //ХАха
    }

    public static Test getClosest() {
        //Test closest = tests.get(0);
        return tests.get(0);
    }


    public static void addTest(Subject subject, Date date) {
        tests.add(new Test(subject, date));
    }

    public static class Test{

        Date date;
        Subject subject;

        private Test(Subject subject, Date date) {
            this.subject = subject;

            this.date = date;
        }

        public Date getDate() {
            return date;
        }

        public Subject getSubject() {
            return subject;
        }
    }
}
