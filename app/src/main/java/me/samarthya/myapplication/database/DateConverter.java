package me.samarthya.myapplication.database;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;

public class DateConverter {
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
}
