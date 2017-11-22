package com.handen.schoolhelper2.fragments.Week;

/**
 Класс дня
 */
public class Day {

    /**
     * Индекс расписания установленного для этого дня из MainActivity.timetables
     */
    private int timetableIndex = 0;
    private String name;

    public Day(String name) {
        this.name = name;
    }

    public void setTimetableIndex (int timetableIndex) {
        this.timetableIndex = timetableIndex;
    }

    public int getTimetableIndex(){
        return timetableIndex;
    }

    public String getName()
    {
        return name;
    }
}






