package com.handen.schoolhelper2;

import java.util.ArrayList;
import java.util.Date;

/**
 * Класс, отвечающий за расписание контрольных работ
 * Created by Vanya on 23.11.2017.
 *
 */

public class Tests {

    public ArrayList<Test> tests = new ArrayList<>();
  //  private static SharedPreferences sharedPreferences;
  //  private Context context;
   // static {
   //     sharedPreferences = new SharedPreferences();
   // }

    public int getCount() {
        //Проверяем на актуальность
        checkForIrrelevant();
        return tests.size();
    }

    //

 /*   public static boolean hasTest(Subject subject) {
        //Проверяем на актуальность
        boolean hasTest = false;
        checkForIrrelevant();
        for(Test test : tests) {
            if(test.getSubject().equals(subject))
                hasTest = true;
        }
        return hasTest;

    }
    public static Test getTest(Subject subject) {
        checkForIrrelevant();
        for(Test test : tests) {
            if(test.getSubject().equals(subject))
                return test;
        }
        return null;
    }

    public static String getTestDate(Subject subject) {
        checkForIrrelevant();
        Test test = tests.get(0);
        for(Test test1 : tests) {
            if(test1.getSubject().equals(subject))
                test = test1;

        }
        return new SimpleDateFormat("dd.MM.yyyy").format(test.getDate());
    }
    */

    public Test getClosest() {
        checkForIrrelevant();
        Test closest = tests.get(0);
        for(Test test : tests) {
            if(test.getDate().getTime() < closest.getDate().getTime())
                closest = test;
        }
        return closest;
    }

    private void checkForIrrelevant() {
        for(Test test : tests) {
            if(test.getDate().getTime() < new Date().getTime() &&
                    test.getDate().getDay() != new Date().getDay()) {
                tests.remove(test);
            }
        }
    }

    public void addTest(Date date) {
        tests.add(new Test(date));
    }

    public static class Test{
        Date date;
       // Subject subject;

        private Test(Date date) {
 //           this.subject = subject;

            this.date = date;
        }

        public Date getDate() {
            return date;
        }

  //      public Subject getSubject() {
   //         return subject;
  //      }
    }
}
