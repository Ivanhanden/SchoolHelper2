package com.handen.schoolhelper2.fragments.Week;

import com.handen.schoolhelper2.MainActivity;
import com.handen.schoolhelper2.Settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DayContent {

    /**
     * An array of sample (Timetable) items.
     */
    public static final List<DayItem> ITEMS = new ArrayList<DayItem>();

    /**
     * A map of sample (Timetable) items, by ID.
     */
    public static final Map<String, DayItem> ITEM_MAP = new HashMap<String, DayItem>();



    static {
        // Add some sample items.
        for (int i = 0; i < Settings.TOTALLESSONS; i++) {
            addItem(createDayItem(i));
        }
    }

    private static void addItem(DayItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static DayItem createDayItem(int position) {
        return new DayItem(String.valueOf(position), "Item " + position, makeDetails(position));
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    /**
     * A Timetable item representing a piece of content.
     */
    public static class DayItem {
        public final String id;
        public final String content;
        public final String details;

        public DayItem(String id, String content, String details) {
            this.id = id;
            this.content = content;
            this.details = details;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
