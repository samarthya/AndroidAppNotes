package me.samarthya.myapplication.utilities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import me.samarthya.myapplication.database.NoteEntity;

public class SampleData {
    private final static String SAMPLE_STRING_1 = "Welcome to My Diary. It is a convenient way of " +
            "storing all the information at one place. It does not uses the cloud so everything " +
            "you do will be stored on your system only. No invasion of privacy!!";

    public final static Date getDate(int diff) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.add(Calendar.MILLISECOND, diff);
        return cal.getTime();
    }

    /**
     * Sample dummy data.
     *
     * @return
     */
    public static List<NoteEntity> getNotes() {
        List<NoteEntity> noteEntityList = new ArrayList<>();
        noteEntityList.add(new NoteEntity(getDate(0), SAMPLE_STRING_1));
        return noteEntityList;
    }
}
