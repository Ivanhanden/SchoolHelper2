package com.handen.schoolhelper2.fragments.Name;

import com.handen.schoolhelper2.MainActivity;
import com.handen.schoolhelper2.Subject;

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
public class NameContent {

    /**
     * An array of sample (Name) items.
     */
    public static final List<Name> ITEMS = new ArrayList<Name>();

    /**
     * A map of sample (Name) items, by ID.
     */
    public static final Map<String, Name> ITEM_MAP = new HashMap<String, Name>();

    private static int COUNT = MainActivity.subjectArrayList.size();

    static {
        COUNT = MainActivity.subjectArrayList.size();
        // Add some sample items.
        for (int i = 0; i < COUNT; i++) {
            addItem(createNameItem(i));
        }
    }

    private static void addItem(Name item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.subjectName, item);
    }

    private static Name createNameItem(int position) {
        Subject subject = MainActivity.subjectArrayList.get(position);
        return new Name(subject.getName(), subject.getTeacherName());
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
     * A Name item representing a piece of content.
     */
    public static class Name {
        public String teacherName;
        public final String subjectName;

        public Name(String subjectName, String teacherName) {
            this.subjectName = subjectName;
            this.teacherName = teacherName;
        }

        @Override
        public String toString() {
            return teacherName;
        }

        public void setTeacherName(String teacherName, int position)
        {


        }
    }
}
