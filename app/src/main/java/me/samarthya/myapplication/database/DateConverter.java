package me.samarthya.myapplication.database;

import android.arch.persistence.room.TypeConverter;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateConverter {

    private static final SimpleDateFormat sm = new SimpleDateFormat("EEE, MMM d");

    @TypeConverter
    public static Date toDate(Long timeStamp) {
        if (timeStamp == null) {
            return null;
        } else {
            return new Date(timeStamp);
        }
    }

    @TypeConverter
    public static Long toTimeStamp(Date date) {
        return date == null ? null : new Long(date.getTime());
    }

    /**
     * Formats date
     *
     * @param dt
     * @return
     */
    public static String formatDate(Date dt) {

        if (dt == null) {
            dt = new Date();
        }

        return sm.format(dt);
    }
}
