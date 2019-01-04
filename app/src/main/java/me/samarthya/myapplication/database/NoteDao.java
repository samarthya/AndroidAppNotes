package me.samarthya.myapplication.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Using room library to reuturn this from the abstract method.
 */
@Dao
public interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNote(NoteEntity noteEntity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<NoteEntity> notes);

    @Delete
    void deleteNote(NoteEntity noteEntity);

    @Query("Select * FROM notes where id = :id")
    NoteEntity getNoteByID(int id);

    @Query("Select * FROM notes order by date DESC")
    LiveData<List<NoteEntity>> getAll();

    @Query("DELETE FROM notes ")
    void deleteAll();

    @Query("Select count(*) FROM notes")
    int getCount();

}
