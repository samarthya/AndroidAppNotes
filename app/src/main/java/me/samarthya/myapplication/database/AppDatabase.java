package me.samarthya.myapplication.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.support.annotation.NonNull;

/**
 * Room persistence library is an abstraction layer over SQLite
 */
@Database(entities = {NoteEntity.class}, version = 2, exportSchema = false)
@TypeConverters(DateConverter.class)
public abstract class AppDatabase extends RoomDatabase {
    public static final String DATABASE_NAME = "AppDatabase.db";
    public static volatile AppDatabase instance;

    public static final Object LOCK = new Object();

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("BEGIN TRANSACTION;");
            database.execSQL("ALTER TABLE notes " +
                    "add COLUMN attachment BOOLEAN NULL");

            database.execSQL("ALTER TABLE notes " +
                    "ADD COLUMN image_url VARCHAR(255) NULL");
            database.execSQL("COMMIT;");
        }
    };

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (LOCK){
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class,
                            DATABASE_NAME).addMigrations(MIGRATION_1_2).build();
                }
            }
        }
        return instance;
    }

    /**
     * DAO
     *
     * @return
     */
    public abstract NoteDao noteDao();


}
