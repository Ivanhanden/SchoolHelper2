package com.handen.schoolhelper2.fragments.Week;

import android.util.Log;

import com.handen.schoolhelper2.MainActivity;

import java.util.ArrayList;
import java.util.Arrays;

/**
    Класс, отвечающий за содержимое списка дней недели
 */
public class WeekContent {

    /**
     * Сколько всего дней недели
      */
    private static final int TOTALDAYS = 7;

/*    static {

        if(MainActivity.days.size() == 0) {
            Log.d("static", "days.size() == 0");
            for (int i = 0; i < TOTALDAYS; i++) {

                addDay(createDay(i), i);
            }
        }
    }

    private static void addDay(Day day, int i) {
        MainActivity.days.add(day);
        MainActivity.daysMap.put(daysIds.get(i), day);
    }

    private static Day createDay(int position) {
        return new Day(position, daysNames.get(position));
    }

 */

}
