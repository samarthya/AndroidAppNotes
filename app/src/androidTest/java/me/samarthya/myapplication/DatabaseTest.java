package me.samarthya.myapplication;

import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import me.samarthya.myapplication.database.AppDatabase;
import me.samarthya.myapplication.database.NoteDao;
import me.samarthya.myapplication.database.NoteEntity;
import me.samarthya.myapplication.utilities.SampleData;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class DatabaseTest {
    public static final String TAG = "Junit";


    private AppDatabase mDb;
    private NoteDao mDao;

    @Before
    public void createDb() {
        Context context = InstrumentationRegistry.getTargetContext();
        mDb = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();

        if (mDb != null) {
            Log.i(TAG, "createDB: DB created.");
        }
        mDao = mDb.noteDao();
        Log.i(TAG, "createDB: Note DAO created");
    }


    @Test
    public void createAndRetrieveNotes() {
        mDao.insertAll(SampleData.getNotes());
        int count = mDao.getCount();
        Log.i(TAG, "createAndRetrieveNotes: count = " + count);
        assertEquals(SampleData.getNotes().size(), count);

    }

    @Test
    public void compareStrings() {
        mDao.insertAll(SampleData.getNotes());
        NoteEntity originalEntity = SampleData.getNotes().get(0);
        NoteEntity toBCompared = mDao.getNoteByID(1);
        Log.i(TAG, "compareStrings: " + originalEntity.getText());
        assertEquals(originalEntity.getText(), toBCompared.getText());
    }

    @Test
    public void addEmptyId() {
        mDao.insertAll(SampleData.getNotes());
        mDao.insertNote(new NoteEntity(SampleData.getDate(5), "Welcome Text!!"));
        Log.i(TAG, "addEmptyId: " + mDao.getNoteByID(4).getText());
        assertEquals(4, mDao.getNoteByID(4).getId());
    }

    @After
    public void closeDb() {
        if (mDb != null)
            mDb.close();
        Log.i(TAG, "closeDb");
    }
}
