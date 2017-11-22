package com.handen.schoolhelper2;

import java.util.Date;

/**
 * Класс отметки
 * Created by Vanya on 15.07.2017.
 */

public class Note {

    Date mDate; //Дата
    String note; //Отметка

    //Конструктор пустой отметки
    public Note()
    {
        note = "";
    }

    //Конструктор отметки с датой и int
    public Note(Date date, String note)
    {
        mDate = date;
        this.note = note;
    }
    //Метод, возвращающий дату
    public Date getDate() {
        return mDate;
    }
    //Метод, который обрабатывает отметку и возвращает её в нужном формате
    public String getNote() {
        return note;
    }
    //Метод, устанавливающий отметку
    public void setNote(String note) {
        this.note = note;
    }
}


