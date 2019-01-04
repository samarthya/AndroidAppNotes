package me.samarthya.myapplication.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import me.samarthya.myapplication.database.AppRepository;
import me.samarthya.myapplication.database.NoteEntity;

import static me.samarthya.myapplication.utilities.Constants.TAG_SVM;

public class ScrollingViewModel extends AndroidViewModel {

    public MutableLiveData<NoteEntity> mLiveNote = new MutableLiveData<>();
    private AppRepository mRepository;

    private Executor executor = Executors.newSingleThreadExecutor();

    public ScrollingViewModel(@NonNull Application application) {
        super(application);
        mRepository = AppRepository.getInstance(getApplication());
    }

    public void loadData(final int noteId) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                NoteEntity noteEntity = mRepository.loadDataById(noteId);
                mLiveNote.postValue(noteEntity);
            }
        });

    }

    public void saveNote(String noteText, String imageLocation) {
        NoteEntity note = mLiveNote.getValue();

        if (note == null) {
            if (TextUtils.isEmpty(noteText.trim()) && TextUtils.isEmpty(imageLocation)) {
                return;
            }
            if (TextUtils.isEmpty(imageLocation)) {
                saveNote(noteText);
                return;
            } else {
                note = new NoteEntity(new Date(), noteText.trim(), imageLocation.trim());
            }
        } else {
            note.setText(noteText.trim());
            if (!TextUtils.isEmpty(imageLocation) && (imageLocation.compareToIgnoreCase(imageLocation.trim()) == 0)) {
                note.setImgUrl(imageLocation.trim());
                note.setAttachment(true);
            } else {
                note.setImgUrl(null);
                note.setAttachment(false);
            }

        }

        mRepository.insertNote(note);
    }

    public void saveNote(String noteText) {
        Log.i(TAG_SVM, "saveNote: OnlyNote");
        NoteEntity note = mLiveNote.getValue();

        if (note == null) {
            if (TextUtils.isEmpty(noteText.trim())) {
                return;
            }
            note = new NoteEntity(new Date(), noteText.trim());
        } else {
            note.setText(noteText.trim());
        }

        note.setImgUrl(null);
        note.setAttachment(false);
        mRepository.insertNote(note);
    }

    public void deleteNote() {
        mRepository.deleteNote(mLiveNote.getValue());
    }
}
