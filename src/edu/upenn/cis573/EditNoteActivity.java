package edu.upenn.cis573;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import edu.upenn.cis573.database.DBManager;

public class EditNoteActivity extends Activity {

    Button saveButton;
    EditText  editNoteText;
    StudySpace history;
    TextView txtText;
    ImageButton btnSpeak;
    String fileName = "";
    
    protected static final int RESULT_SPEECH = 1;
    protected static final int CAMERA_PIC_REQUEST = 2;
    protected static final int SELECT_PHOTO = 3;
    private static String mFileName = null;
    private MediaRecorder mRecorder = null;
    private MediaPlayer   mPlayer = null;
    
    private boolean mStartRecording = true;
    private boolean mStartPlaying = true;
    ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);
        // check if this is a new note or just showing a saved note
        saveButton = (Button)findViewById(R.id.savebutton);
        editNoteText  = (EditText)findViewById(R.id.editnote1);
        image = (ImageView)findViewById(R.id.camerapicture);
        
        Bundle extras = getIntent().getExtras();		
        if(extras != null) {
            String text = extras.getString("NOTE_TEXT");
            history = (StudySpace)extras.getSerializable("HISTORY");
            editNoteText.setEnabled(false);
            saveButton.setEnabled(false);
            editNoteText.setText(text);
        }
        
        //initialize fields for audio recording
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        //mFileName += "/audiorecordtest.3gp";
        mFileName += "/" + DBManager.makeAudioFileName();
        
        //attach listener to audio buttons
        final Button record = (Button) findViewById(R.id.record_button);
        record.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                System.out.println("Record button clicked!");
                onRecord(mStartRecording);
                if (mStartRecording) {
                    record.setText("Stop recording");
                } else {
                    record.setText("Record");
                }
                mStartRecording = !mStartRecording;
            }
        });
        
        final Button play = (Button) findViewById(R.id.play_button);
        play.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                onPlay(mStartPlaying);
                if (mStartPlaying) {
                    play.setText("Stop playing");
                } else {
                    play.setText("Play");
                }
                mStartPlaying = !mStartPlaying;
            }
        });
        
        txtText = (TextView) findViewById(R.id.txtText);

		btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);

		btnSpeak.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(
						RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

				intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");

				try {
					startActivityForResult(intent, RESULT_SPEECH);
					txtText.setText("");
				} catch (ActivityNotFoundException a) {
					Toast t = Toast.makeText(getApplicationContext(),
							"Ops! Your device doesn't support Speech to Text",
							Toast.LENGTH_SHORT);
					t.show();
				}
			}
		});
    }
    
    
    
    /*
     * Methods for audio recording 
     */   
    @Override
    public void onPause() {
        super.onPause();
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }

        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }
    
    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }
    
    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }

    private void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e(null, "Audio playing prepare() failed");
        }
    }

    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(null, "Audio recording prepare() failed");
        }

        mRecorder.start();
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }
    
    /*
     * Methods for note editing and saving
     */
    public void onSaveNote(View view){
        Log.v("EditText", editNoteText.getText().toString());
        String noteText = editNoteText.getText().toString();
        
      	if(addNoteAndPhotoToDB(noteText, fileName))
      		showSaveDialog();	
    }
    
    public void onCamera(View view){
    	Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE); 
        startActivityForResult(intent, CAMERA_PIC_REQUEST);
    }
    
    public void onChoosePicture(View view){
    	Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
    	photoPickerIntent.setType("image/*");
    	startActivityForResult(photoPickerIntent, SELECT_PHOTO); 
    }

    public void onCancelNote(View view){
        ((Activity)this).finish();
    }

    public void showSaveDialog(){
        AlertDialog.Builder builder = new Builder(this);
        builder.setMessage("Your note has been saved!");
        builder.setTitle("Confirmation Dialog");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();		            
            }
        });	
        builder.create().show();
    }

    public boolean addNoteAndPhotoToDB(String noteText, String photoPath){
        if(DBManager.updateDb(noteText, photoPath) == -1) {
            showClearHistoryDialog();
            return false;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_note, menu);
        return true;
    }

    public void showClearHistoryDialog() {
        final Context currContext = this;
        AlertDialog.Builder builder = new AlertDialog.Builder(currContext);
        builder.setMessage("The database if full, would you like to clear history? (This action is unrecoverable)");
        builder.setTitle("Database Full");
        builder.setPositiveButton("Yes", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (DBManager.isEmpty()) {
                    new AlertDialog.Builder(currContext)
                    .setTitle("No History")
                    .setMessage("Current history is empty!")
                    .show();
                }
                else {
                    DBManager.clearDB();
                    ((Activity)currContext).finish();
                }
            }
        })
        .show();
    }
    
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
			case RESULT_SPEECH: {
				if (resultCode == RESULT_OK && null != data) {
	
					ArrayList<String> text = data
							.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
	
					editNoteText.setText(DBManager.getFirstEntryNote()+ " "+ text.get(0));
				}
				break;
			}
			
			case CAMERA_PIC_REQUEST:{
				if (resultCode == RESULT_OK && data != null) {
					FileOutputStream outStream = null;
					fileName = Environment.getExternalStorageDirectory().getAbsolutePath();
				    fileName += "/StudySpace/%d.jpg";
					fileName = String.format(fileName, System.currentTimeMillis());
					Log.i("path is: ", fileName);
					
					File photo = new File(fileName);
					photo.getParentFile().mkdirs();
					
					Bundle b = data.getExtras();
					Bitmap photos = (Bitmap)b.get("data");
					ByteArrayOutputStream stream = new ByteArrayOutputStream();
					photos.compress(Bitmap.CompressFormat.PNG, 100, stream);
					
					byte[] byteArray = stream.toByteArray();
					image.setImageBitmap(photos);
					try {
						outStream = new FileOutputStream(photo);
						outStream.write(byteArray);
						outStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				
				}
				break;
			}
			
			case SELECT_PHOTO: {
		        if(resultCode == RESULT_OK && data != null){  
		            Uri selectedImage = data.getData();
		            fileName = getAbsolutePathFromURI(this, selectedImage);
		            InputStream imageStream = null;
					try {
						imageStream = getContentResolver().openInputStream(selectedImage);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}	           
		            image.setImageBitmap(BitmapFactory.decodeStream(imageStream));
		        }
		    }

		}
	}
	
	public String getAbsolutePathFromURI(Context context, Uri contentUri) {
		  Cursor cursor = null;
		  try { 
		    String[] proj = { MediaStore.Images.Media.DATA };
		    cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
		    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		    cursor.moveToFirst();
		    return cursor.getString(column_index);
		  } finally {
		    if (cursor != null) {
		      cursor.close();
		    }
		  }
	}
    
}
