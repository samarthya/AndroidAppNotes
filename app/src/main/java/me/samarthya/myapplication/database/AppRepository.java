package me.samarthya.myapplication.database;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.util.Log;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import me.samarthya.myapplication.utilities.SampleData;

import static me.samarthya.myapplication.utilities.Constants.TAG_APP_REPOSITORY;

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

    public void deleteAllNotes() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                /**
                 * If you are returning anything that is not LiveData you need to use executor.
                 * If you are doing this without executor; which you can try. Your application will
                 * crash.
                 */
                mDb.noteDao().deleteAll();
            }
        });

    }


    public NoteEntity loadDataById(int noteId) {
        Log.i(TAG_APP_REPOSITORY, "loadDataById: " + noteId);
        return mDb.noteDao().getNoteByID(noteId);
    }

    /**
     * Add the nore existing or new note is irrelevant as it will be replaced.
     *
     * @param note
     */
    public void insertNote(NoteEntity note) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                mDb.noteDao().insertNote(note);
            }
        });
    }

    public void deleteNote(final NoteEntity note) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                mDb.noteDao().deleteNote(note);
            }
        });
    }
}
