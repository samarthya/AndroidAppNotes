package me.samarthya.myapplication.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

/**
 * Class representing the model for the respective entry in the my notes list.
 */
@Entity(tableName = "notes")
public class NoteEntity {
    /**
     * ID: Should be unique for every Item in the list.
     */
    @PrimaryKey(autoGenerate = true)
    private int id;
    /**
     * The date it was created.
     */
    private Date date;
    /**
     * Textual information.
     */
    private String text;

    @Ignore
    public NoteEntity() {
    }

    public NoteEntity(int id, Date date, String text) {
        this.id = id;
        this.date = date;
        this.text = text;
    }

    @Ignore
    public NoteEntity(Date date, String text) {
        this.date = date;
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "NoteEntity{" +
                "id=" + id +
                ", date=" + date +
                ", text='" + text + '\'' +
                '}';
    }
}
