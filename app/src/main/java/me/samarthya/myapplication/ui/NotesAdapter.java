package me.samarthya.myapplication.ui;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.samarthya.myapplication.EditorActivity;
import me.samarthya.myapplication.R;
import me.samarthya.myapplication.database.NoteEntity;

import static me.samarthya.myapplication.utilities.Constants.NOTE_ID_KEY;

public class NotesAdapter extends RecyclerView.Adapter< NotesAdapter.ViewHolder > {

    private final List<NoteEntity> mNoteList;
    private final Context mContext;

    public NotesAdapter(List<NoteEntity> mNoteList, Context mContext) {
        this.mNoteList = mNoteList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View view = layoutInflater.inflate(R.layout.node_list_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        final NoteEntity note = mNoteList.get(position);
        viewHolder.mTextView.setText(note.getText());

        viewHolder.mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, EditorActivity.class);
                intent.putExtra(NOTE_ID_KEY, note.getId());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mNoteList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.note_text)
        TextView mTextView;

        @BindView(R.id.fab)
        FloatingActionButton mFab;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
