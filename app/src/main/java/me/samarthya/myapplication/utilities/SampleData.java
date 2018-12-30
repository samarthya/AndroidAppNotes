package me.samarthya.myapplication.utilities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import me.samarthya.myapplication.database.NoteEntity;

public class SampleData {
    private final static String SAMPLE_STRING_1 = "Simple list message that I have.";
    private final static String SAMPLE_STRING_2 = "A note with \na line feed.";
    private final static String SAMPLE_STRING_3 = "lorem ipsum \n lorem ipsum crack" +
            "Lorem Ipsum is simply dummy text of the printing and typesetting industry. " +
            "Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, " +
            "when an unknown printer took a galley of type and scrambled it to make a type specimen " +
            "book. It has survived not only five centuries, \n\nbut also the leap into electronic " +
            "typesetting, remaining essentially unchanged. It was popularised in the 1960s with " +
            "the release of Letraset sheets containing Lorem Ipsum passages, and more recently " +
            "with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.";

    public final static Date getDate(int diff) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.add(Calendar.MILLISECOND, diff);
        return cal.getTime();
    }

    public static List<NoteEntity> getNotes() {
        List<NoteEntity> noteEntityList = new ArrayList<>();
        noteEntityList.add(new NoteEntity(1, getDate(0), SAMPLE_STRING_1));
        noteEntityList.add(new NoteEntity(2, getDate(-1), SAMPLE_STRING_2));
        noteEntityList.add(new NoteEntity(3, getDate(-3), SAMPLE_STRING_3));
        return noteEntityList;
    }
}
