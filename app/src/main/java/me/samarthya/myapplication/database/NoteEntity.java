package me.samarthya.myapplication.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.text.TextUtils;

import java.util.Date;

import io.reactivex.annotations.Nullable;

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

    @ColumnInfo(name = "attachment")
    @Nullable
    private boolean attachment;

    @ColumnInfo(name = "image_url")
    @Nullable
    private String imgUrl;

    @Ignore
    public NoteEntity() {
    }

    public NoteEntity(int id, Date date, String text) {
        this.id = id;
        this.date = date;
        this.text = text;
        this.imgUrl = null;
        this.attachment = false;
    }

    @Ignore
    public NoteEntity(Date date, String text) {
        this.date = date;
        this.text = text;
        this.imgUrl = null;
        this.attachment = false;
    }

    @Ignore
    public NoteEntity(Date date, String text, String imgUrl) {
        this.date = date;
        this.text = text;
        this.imgUrl = imgUrl;
        this.attachment = (!TextUtils.isEmpty(imgUrl));
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
                ", attachment='" + getImgUrl() + '\'' +
                '}';
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public boolean isAttachment() {
        return attachment;
    }

    public void setAttachment(boolean attachment) {
        this.attachment = attachment;
    }
}
