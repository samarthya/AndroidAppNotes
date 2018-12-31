package me.samarthya.myapplication.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import me.samarthya.myapplication.database.AppRepository;
import me.samarthya.myapplication.database.NoteEntity;

/**
 * For editor activity.
 * One ViewModel for each activity.
 */
public class EditorViewModel extends AndroidViewModel {

    public MutableLiveData<NoteEntity> mLiveNote = new MutableLiveData<>();
    private AppRepository mRepository;

    private Executor executor = Executors.newSingleThreadExecutor();

    public EditorViewModel(@NonNull Application application) {
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

    public void saveNote(String noteText) {
        NoteEntity note = mLiveNote.getValue();

        if (note == null) {
            if (TextUtils.isEmpty(noteText.trim())) {
                return;
            }
            note = new NoteEntity(new Date(), noteText.trim());
        } else {
            note.setText(noteText.trim());
        }

        mRepository.insertNote(note);
    }

    public void deleteNote() {
        mRepository.deleteNote(mLiveNote.getValue());
    }
}
