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
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.samarthya.myapplication.database.NoteEntity;
import me.samarthya.myapplication.viewmodel.ScrollingViewModel;

import static me.samarthya.myapplication.utilities.Constants.CAPTURE_PHOTO;
import static me.samarthya.myapplication.utilities.Constants.EDITING_KEY;
import static me.samarthya.myapplication.utilities.Constants.NOTE_ID_KEY;
import static me.samarthya.myapplication.utilities.Constants.TAG_EDITOR_ACTIVITY;
import static me.samarthya.myapplication.utilities.Constants.TAG_SVM;

/**
 * Just a copy of EditorActivity that I am trying to play around with.
 */
public class ScrollingActivity extends AppCompatActivity {


    @BindView(R.id.note_text)
    TextView mTextView;

    @BindView(R.id.image_attached)
    ImageView mImageView;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.add_image)
    FloatingActionButton mAddImageButton;

    @BindView(R.id.delete_image)
    FloatingActionButton mDeleteImageButton;
    ScrollingViewModel mViewModel;
    private String mCurrentPhotoPath;
    private boolean mNewNote, mEditing;


    @OnClick(R.id.add_image)
    void addOrEditImageClicked() {
        delegateToCaptureImage();
        setImageInformation();
    }

    /**
     * The image delete path.
     */
    void deleteAttachedImage() {

        /**
         * Delete the existing file if any and then clear out
         * the holders.
         */
        if (mCurrentPhotoPath != null) {
            File mFile = new File(mCurrentPhotoPath);
            if (mFile.exists()) {
                Boolean bDeleted = mFile.delete();
                Log.i(TAG_EDITOR_ACTIVITY, "deleteAttachedImage: " + bDeleted);
            }
            mCurrentPhotoPath = null;
        }

        setImageInformation();
    }

    /**
     * Sets the image in the Image view either loaded from Camera
     * or from the URL.
     */
    private void setPic() {
        if (mCurrentPhotoPath != null) {
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
            Log.i(TAG_EDITOR_ACTIVITY, "setPic: File at " + mCurrentPhotoPath + " is set.");

            mImageView.setImageBitmap(bitmap);
        } else {
            Log.i(TAG_EDITOR_ACTIVITY, "setPic: ImageHolder blank.");
            mImageView.setImageResource(R.drawable.image_holder);
        }

    }

    /**
     * Change the edit information.
     */
    private void setImageInformation() {
        if (mCurrentPhotoPath != null) {
            mAddImageButton.setImageResource(R.drawable.ic_edit);
            mDeleteImageButton.setClickable(true);
        } else {
            mAddImageButton.setImageResource(R.drawable.ic_add);
            mDeleteImageButton.setClickable(false);
        }

        setPic();
    }

    /**
     * Create a temporary file with a unique name that we can use to save the image.
     *
     * @return
     * @throws IOException
     */
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
    protected void onStart() {
        super.onStart();
        Log.i(TAG_EDITOR_ACTIVITY, "onStart: launched");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_check);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initViewModel();

        mDeleteImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAttachedImage();
            }
        });
    }

    private void initViewModel() {
        mViewModel = ViewModelProviders.of(this).get(ScrollingViewModel.class);

        mViewModel.mLiveNote.observe(this, new Observer<NoteEntity>() {
            @Override
            public void onChanged(@Nullable NoteEntity noteEntity) {
                if (noteEntity != null && !mEditing) {
                    mTextView.setText(noteEntity.getText());
                    if (noteEntity.isAttachment()) {
                        mCurrentPhotoPath = noteEntity.getImgUrl();
                        Log.i(TAG_EDITOR_ACTIVITY, "onChanged: URL = " + mCurrentPhotoPath);
                        setImageInformation();
                    }
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
            inflater.inflate(R.menu.menu_scrolling, menu);
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
                return true;
            }

            case R.id.action_attach_picture: {
                delegateToCaptureImage();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        saveAndReturn();
    }

    /**
     * Saves the Note and returns.
     */
    private void saveAndReturn() {
        Log.i(TAG_SVM, "saveAndReturn: " + mCurrentPhotoPath);
        mViewModel.saveNote(mTextView.getText().toString(), mCurrentPhotoPath);
        finish(); // Close the current activity and return to list
    }

    /**
     * Attach image.
     */
    void attachImage() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            try {

                photoFile = createImageFile();

                /** Continue only if the File was successfully created **/
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(this,
                            "me.samarthya.myapplication.fileprovider",
                            photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    /**
                     * Go for the image activity.
                     */
                    startActivityForResult(takePictureIntent, CAPTURE_PHOTO);
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
        Log.i(TAG_EDITOR_ACTIVITY, "delegateToCaptureImage: called.");
        attachImage();
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
            setImageInformation();
        }
    }


}
