package com.handen.schoolhelper2;

/**
 * Created by Vanya on 17.08.2017.
 */

public class Settings {
    /**
     * Максимальное кол-во уроков в день (ВСЕГДА РАВНО 8)
     */
    public static int TOTALLESSONS;
    /**
     * Максимальная оценка
     */
    public static int MAX_NOTE;
    /**
     * Показывать ли уведомления
     */
    public static boolean IS_SHOW_NOTIFICATIONS;
    /**
     * Отключать ли звук
     */
    public static boolean IS_MUTING;
    /**
     * С какого среднего балла жёлтый цвет
     */
    public static double YELLOW_COLOR;
    /**
     * С какого среднего балла зелёный цвет
     */
    public static double GREEN_COLOR;
    /**
     * Коструктор, который вызывается при первом запуске и заносит дефолтные настройки
     */
    public Settings() {
        MAX_NOTE = 10;
        TOTALLESSONS = 8;
        IS_SHOW_NOTIFICATIONS = true;
        IS_MUTING = true;
        YELLOW_COLOR = 5.5;
        GREEN_COLOR = 8.0;
    }

    public Settings(int maxNote, boolean isShowNotifications, boolean isMuting, double yellowColor, double greenColor) {
        MAX_NOTE = maxNote;
        TOTALLESSONS = 8;
        IS_SHOW_NOTIFICATIONS = isShowNotifications;
        IS_MUTING = isMuting;
        YELLOW_COLOR = yellowColor;
        GREEN_COLOR = greenColor;
    }
}
