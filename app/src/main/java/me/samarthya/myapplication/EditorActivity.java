package me.samarthya.myapplication;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.samarthya.myapplication.database.NoteEntity;
import me.samarthya.myapplication.viewmodel.EditorViewModel;

import static me.samarthya.myapplication.utilities.Constants.CAPTURE_PHOTO;
import static me.samarthya.myapplication.utilities.Constants.EDITING_KEY;
import static me.samarthya.myapplication.utilities.Constants.NOTE_ID_KEY;
import static me.samarthya.myapplication.utilities.Constants.TAG_EDITOR_ACTIVITY;

public class EditorActivity extends AppCompatActivity {

    @BindView(R.id.note_text)
    TextView mTextView;

    @BindView(R.id.image_attached)
    ImageView mImageView;

    String mCurrentPhotoPath;
    private EditorViewModel mViewModel;
    private boolean mNewNote, mEditing;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_check);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ButterKnife.bind(this);

        if (savedInstanceState != null) {
            mEditing = savedInstanceState.getBoolean(EDITING_KEY);
        }

        initViewModel();
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);

        if (mCurrentPhotoPath != null) {
            File f = new File(mCurrentPhotoPath);
            Uri contentUri = Uri.fromFile(f);
            mediaScanIntent.setData(contentUri);
            this.sendBroadcast(mediaScanIntent);
        }
    }

    /**
     * INitializes the view model.
     */
    private void initViewModel() {
        mViewModel = ViewModelProviders.of(this).get(EditorViewModel.class);

        mViewModel.mLiveNote.observe(this, new Observer<NoteEntity>() {
            @Override
            public void onChanged(@Nullable NoteEntity noteEntity) {
                if (noteEntity != null && !mEditing) {
                    mTextView.setText(noteEntity.getText());
                }

            }
        });

        Bundle extras = getIntent().getExtras();

        if (extras == null) {
            setTitle(R.string.new_note);
            mNewNote = true;
        } else {
            setTitle(R.string.edit_note);
            Log.i(TAG_EDITOR_ACTIVITY, "initViewModel: Edit Mode");
            int noteId = extras.getInt(NOTE_ID_KEY);
            mViewModel.loadData(noteId);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNewNote) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_editor, menu);
        } else {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_attach, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                saveAndReturn();
                return true;
            }

            case R.id.action_delete: {
                mViewModel.deleteNote();
                finish();
            }

            case R.id.action_attach_picture: {
                delegateToCaptureImage();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    void attachImage() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;

            try {
                photoFile = createImageFile();

                /** Continue only if the File was successfully created **/
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(this,
                            "me.samarthya.myapplication.fileprovider",
                            photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, CAPTURE_PHOTO);
                    galleryAddPic();
                }
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e(TAG_EDITOR_ACTIVITY, "attachImage: ", ex.getCause());
            }
        }
    }

    /**
     * This function should capture the image from the camera
     * and then save it as part of the note.
     */
    private void delegateToCaptureImage() {
        Log.i(TAG_EDITOR_ACTIVITY, "delegateToCaptureImage: Clicked");
        attachImage();
    }

    @Override
    public void onBackPressed() {
        saveAndReturn();
    }

    private void saveAndReturn() {
        mViewModel.saveNote(mTextView.getText().toString());
        finish(); // Close the current activity and reutrn to list
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(EDITING_KEY, true);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == CAPTURE_PHOTO && resultCode == RESULT_OK) {
            Log.i(TAG_EDITOR_ACTIVITY, "onActivityResult: @ " + mCurrentPhotoPath);
            galleryAddPic();
            setPic();
        }
    }

    private void setPic() {
        // Get the dimensions of the View
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        mImageView.setImageBitmap(bitmap);
    }


}
