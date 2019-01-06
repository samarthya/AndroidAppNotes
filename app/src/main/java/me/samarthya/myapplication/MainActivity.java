package me.samarthya.myapplication;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.samarthya.myapplication.database.NoteEntity;
import me.samarthya.myapplication.ui.NotesAdapter;
import me.samarthya.myapplication.viewmodel.MainViewModel;

/**
 * Main activity class
 */
public class MainActivity extends AppCompatActivity {

    /**
     * For application that needs a list or large data based or
     * too many changes.
     */
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @OnClick(R.id.fab)
    void fabClickHandler() {
        Intent intent = new Intent(this, ScrollingActivity.class);
        startActivity(intent);
    }


    private List<NoteEntity> noteEntityList = new ArrayList<>();
    private NotesAdapter mAdapter;
    private MainViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
        initRecyclerView();
        initViewModel();
    }

    /**
     * Initalizes the view model.
     */
    private void initViewModel() {

        final Observer<List<NoteEntity>> notesObserver = new Observer<List<NoteEntity>>() {
            @Override
            public void onChanged(@Nullable List<NoteEntity> noteEntities) {
                noteEntityList.clear();
                if (noteEntities != null && noteEntities.size() == 0) {
                    addSampleData();
                }
                noteEntityList.addAll(noteEntities);

                if (mAdapter == null) {
                    mAdapter = new NotesAdapter(noteEntityList, MainActivity.this);
                    mRecyclerView.setAdapter(mAdapter);
                } else {
                    mAdapter.notifyDataSetChanged();
                }

            }
        };

        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mViewModel.mNotes.observe(this, notesObserver);
    }

    /**
     * Recycler View.
     */
    private void initRecyclerView() {
        mRecyclerView.setHasFixedSize(true);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        DividerItemDecoration divider = new DividerItemDecoration(
                mRecyclerView.getContext(), linearLayoutManager.getOrientation()
        );

        mRecyclerView.addItemDecoration(divider);

    }

    /**
     * Deletes all notes.
     */
    private void deleteAllNotes() {
        Log.i("MyNotes", "deleteAllNotes: Delete all notes called.");
        mViewModel.deleteAllNotes();
    }

    /**
     * Adds sample data.
     */
    private void addSampleData() {
        Log.i("MyNotes", "addSampleData: Add sample data.");

        mViewModel.addSampleData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_delete_all: {
                deleteAllNotes();
                return true;
            }

           /* case R.id.action_show_other_note: {
                showNoteWithAttachment();
                return true;
            }*/
        }

        return super.onOptionsItemSelected(item);
    }

    private void showNoteWithAttachment() {
        Intent intent = new Intent(this, ScrollingActivity.class);
        startActivity(intent);
    }


}
