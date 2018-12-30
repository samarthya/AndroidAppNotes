package me.samarthya.myapplication.database;

import android.arch.lifecycle.LiveData;
import android.content.Context;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import me.samarthya.myapplication.utilities.SampleData;

/**
 * All room database operation must be performed in the background.
 */
public class AppRepository {
    private static AppRepository ourInstance;

    public LiveData<List<NoteEntity>> mNotes;
    private AppDatabase mDb;

    /**
     * To ensure all operation are queued.
     */
    private Executor executor = Executors.newSingleThreadExecutor();

    /**
     * DB get first.
     *
     * @param context
     */
    private AppRepository(Context context) {
        mDb = AppDatabase.getInstance(context);
        mNotes = getAllNotes();
    }

    public static AppRepository getInstance(Context context) {
        if (ourInstance == null) {
            ourInstance = new AppRepository(context);
        }
        return ourInstance;
    }

    public void addSampleData() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                mDb.noteDao().insertAll(SampleData.getNotes());
            }
        });
    }

    /**
     * No need for single thread as it is automatically taken care in the background.
     *
     * @return
     */
    private LiveData<List<NoteEntity>> getAllNotes() {
        return mDb.noteDao().getAll();
    }
}
