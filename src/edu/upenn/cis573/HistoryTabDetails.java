package edu.upenn.cis573;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import edu.upenn.cis573.database.DBManager;
import edu.upenn.cis573.datastructure.Room;

public class HistoryTabDetails extends Fragment {
    private StudySpace o;
    private EditText showNoteText;
    private String noteText;
    private Bitmap photos;

    private TextView txtText;
    private ImageButton btnSpeak;
    private ImageView photoImage;
    private static final int RESULT_OK = -1;
    private static final int RESULT_SPEECH = 1;
    private static final int CAMERA_PIC_REQUEST = 2;
    private static final int SELECT_PHOTO = 3;
    String fileName;
    

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.historyviewtabdetails, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Intent i = getActivity().getIntent();
        o = (StudySpace) i.getSerializableExtra("STUDYSPACE");

        TextView tt = (TextView) getView().findViewById(R.id.spacename_history);
        tt.setText(o.getBuildingName());

        TextView rt = (TextView) getView().findViewById(R.id.roomtype_history);
        rt.setText(o.getSpaceName());

        TextView rn = (TextView) getView().findViewById(R.id.roomnumbers_history);

        rn.setText(o.getRoomNames());

        TextView mo = (TextView) getView().findViewById(R.id.maxoccupancy_history);
        mo.setText("Maximum occupancy: "+o.getMaximumOccupancy());

        TextView pi = (TextView) getView().findViewById(R.id.privacy_history);
        ImageView private_icon = (ImageView) getView().findViewById(R.id.private_icon_history);
        if(o.getPrivacy().equals("S")){
            pi.setText("This study space is a common Space");
            if(private_icon!=null){
                Resources resource = getResources();
                int resID = resource.getIdentifier("icon_no_private", "drawable", getActivity().getPackageName() );
                private_icon.setImageResource(resID);
            }
        }else{
            pi.setText("Private space");
            if(private_icon!=null){
                Resources resource = getResources();
                int resID = resource.getIdentifier("icon_private", "drawable", getActivity().getPackageName() );
                private_icon.setImageResource(resID);
            }
        }

        TextView res = (TextView) getView().findViewById(R.id.reservetype_history);
        if(o.getReserveType().equals("N")){
            res.setText("This study space is non-reservable.");
        }else{ 
            res.setText("This study space can be reserved.");
        }
        TextView wb = (TextView) getView().findViewById(R.id.whiteboard_history);
        ImageView wb_icon = (ImageView) getView().findViewById(R.id.whiteboard_icon_history);
        if(o.hasWhiteboard()){
            if(wb_icon!=null){
                Resources resource = getResources();
                int resID = resource.getIdentifier("icon_whiteboard", "drawable", getActivity().getPackageName() );
                wb_icon.setImageResource(resID);
            }
            wb.setText("This study space has a whiteboard.");
        }else{
            wb.setText("This study space does not have a whiteboard.");
            if(wb_icon!=null){
                Resources resource = getResources();
                int resID = resource.getIdentifier("icon_no_whiteboard", "drawable", getActivity().getPackageName() );
                wb_icon.setImageResource(resID);
            }
        }

        TextView com = (TextView) getView().findViewById(R.id.computer_history);
        ImageView com_icon = (ImageView) getView().findViewById(R.id.computer_icon_history);
        if(o.hasComputer()){
            com.setText("This study space has a computer.");
            if(com_icon!=null){
                Resources resource = getResources();
                int resID = resource.getIdentifier("icon_computer", "drawable", getActivity().getPackageName() );
                com_icon.setImageResource(resID);
            }
        }else{
            com.setText("This study space does not have computers.");
            if(com_icon!=null){
                Resources resource = getResources();
                int resID = resource.getIdentifier("icon_no_computer", "drawable", getActivity().getPackageName() );
                com_icon.setImageResource(resID);
            }
        }

        TextView proj = (TextView) getView().findViewById(R.id.projector_history);
        ImageView proj_icon = (ImageView) getView().findViewById(R.id.projector_icon_history);
        if(o.has_big_screen()){
            proj.setText("This study space has a big screen.");
            if(proj_icon!=null){
                Resources resource = getResources();
                int resID = resource.getIdentifier("icon_projector", "drawable", getActivity().getPackageName() );
                proj_icon.setImageResource(resID);
            }
        }else{
            proj.setText("This study space does not have a big screen.");
            if(proj_icon!=null){
                Resources resource = getResources();
                int resID = resource.getIdentifier("icon_no_projector", "drawable", getActivity().getPackageName() );
                proj_icon.setImageResource(resID);
            }
        }

        View an = (View) getView().findViewById(R.id.availablenow_history);
        if(an != null){
            boolean availableNow = false;
            for(Room r : o.getRooms()){
                try {
                    if(r.availableNow())
                        availableNow = true;
                } catch (Exception e) {
                    availableNow = false;   //Calendar crashes
                }
            }
            if(availableNow)
                an.setVisibility(View.VISIBLE);
            else
                an.setVisibility(View.GONE);
        }

        //displays date of last reservation
        TextView lastReservation = (TextView) getView().findViewById(R.id.last_reservation_time);
        lastReservation.setText(new Date(o.getStartDate()).toString());

        showNoteText = (EditText)getView().findViewById(R.id.note);
        showNoteText.setText(o.getNote());

        Button saveButton = (Button)getView().findViewById(R.id.savenote1);
        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                noteText = showNoteText.getText().toString();

                Log.i("In history details page building name is: ", o.getBuildingName());
                if(DBManager.updateDbWithSpecficEntry(o, noteText, fileName, false) == -1) {
                    showClearHistoryDialog();
                }else {
                    showSaveDialog();
                }
            }
        });
        
        txtText = (TextView) getView().findViewById(R.id.txtText2);

 		btnSpeak = (ImageButton) getView().findViewById(R.id.btnSpeak2);
 		photoImage = (ImageView)getView().findViewById(R.id.camerapicture2);
 		String path = o.getPhotoPath();
 		
 		if (path != null && !path.isEmpty()) {
	 		byte[] byteArray = readPictureFromFile(path);
	 
	 		Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
	 		photoImage.setImageBitmap(bitmap);
	 		
 		}
 		
 		else{
 			photoImage.setImageBitmap(null);
 		}
 		
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
 				}
 			}
 		});
 		
 		
 		Button takePictureBtn = (Button)getView().findViewById(R.id.takepic);
 		takePictureBtn.setOnClickListener(new View.OnClickListener() {
 			@Override
 			public void onClick(View v) {
 				Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE); 
 		        startActivityForResult(intent, CAMERA_PIC_REQUEST);
 			}
 		});
 		
 		Button choosePictureBtn = (Button)getView().findViewById(R.id.choosepic);
 		choosePictureBtn.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
		    	photoPickerIntent.setType("image/*");
		    	startActivityForResult(photoPickerIntent, SELECT_PHOTO); 			
			}
		});
 		
 		Button deletePictureBtn = (Button)getView().findViewById(R.id.deletepic);
 		deletePictureBtn.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				Log.i("path: ", o.getPhotoPath());
				Log.i("photos ", String.valueOf(photos == null));
				if((o.getPhotoPath() == null || o.getPhotoPath().isEmpty()) && photos == null){					
					showNoPicDialog();
				}
				else{
					showDeleteConfirm(noteText);
				}
			}
		});
    }

    public void showNoPicDialog() {
        new Builder(this.getActivity())
        .setTitle("No Picture")
        .setMessage("No picture assciated. Cannot delete!")
        .show();
    }
    
    public void showDeleteConfirm(String noteText) {
        final Context currContext = this.getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(currContext);
        builder.setMessage("Are you sure to delete the picture?");
        builder.setTitle("Delete picture");
        builder.setNegativeButton("No", new OnClickListener() {
         	 public void onClick(DialogInterface dialog, int which) {
         		 
         	 }
       });
        builder.setPositiveButton("Yes", new OnClickListener() {
        	String note = "";
            @Override
            public void onClick(DialogInterface dialog, int which) {
            	DBManager.updateDbWithSpecficEntry(o, note, "", true);
               
                    new AlertDialog.Builder(currContext)
                    .setTitle("Picture deleted")
                    .setMessage("The picture is deleted!")
                    .show();
               
                  //  ((Activity)currContext).finish();
            }
            
            public OnClickListener setNoteText(String note){
            	this.note = note;
            	return this;
            }
        }.setNoteText(noteText)).show();
      
    }
    
    
    public byte[] readPictureFromFile(String path){
    	File file = new File(path);
        int size = (int) file.length();
        byte[] bytes = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(bytes, 0, bytes.length);
            buf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    public void showSaveDialog(){
        AlertDialog.Builder builder = new Builder(getActivity());
        builder.setMessage("Your note has been saved!");
        builder.setTitle("Confirmation Dialog");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();                           
            }
        });     
        builder.create().show();
    }

    public void showClearHistoryDialog() {
        final Context currContext = this.getActivity();
        new AlertDialog.Builder(currContext)
        .setMessage("The database if full, would you like to clear history? (This action is unrecoverable)")
        .setTitle("Database Full")
        .setPositiveButton("Yes", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (DBManager.isEmpty()) {
                    new AlertDialog.Builder(currContext)
                    .setTitle("No History")
                    .setMessage("Current history is empty!")
                    .show();
                }else {
                    DBManager.clearDB();
                    Intent i = new Intent(currContext, SearchActivity.class);
                    startActivityForResult(i,
                            StudySpaceListActivity.ACTIVITY_SearchActivity);
                    ((Activity)currContext).finish();
                }
            }
        })
        .show();
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }
   
    @Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
			case RESULT_SPEECH: {
				if (resultCode == RESULT_OK && null != data) {
	
					ArrayList<String> text = data
							.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
	
					showNoteText.setText(DBManager.getSpecificEntryNote(o)+ " "+ text.get(0));
				}
				break;
			}

			case CAMERA_PIC_REQUEST:
				if (resultCode == RESULT_OK && data != null){
					FileOutputStream outStream = null;
					fileName = Environment.getExternalStorageDirectory().getAbsolutePath();
				    fileName += "/StudySpace/%d.jpg";
					fileName = String.format(fileName, System.currentTimeMillis());
					Log.i("path is: ", fileName);
					
					File photo = new File(fileName);
					photo.getParentFile().mkdirs();
					
					Bundle b = data.getExtras();
					photos = (Bitmap)b.get("data");
					ByteArrayOutputStream stream = new ByteArrayOutputStream();
					photos.compress(Bitmap.CompressFormat.PNG, 100, stream);
					
					byte[] byteArray = stream.toByteArray();
			
					photoImage.setImageBitmap(photos);
					try {
						outStream = new FileOutputStream(photo);
						outStream.write(byteArray);
						outStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				
				}
				break;
				
		case SELECT_PHOTO: 
	        if(resultCode == RESULT_OK && data != null){  
	            Uri selectedImage = data.getData();
	            fileName = getAbsolutePathFromURI(this.getActivity(), selectedImage);
	            InputStream imageStream = null;
				try {
					imageStream = getActivity().getContentResolver().openInputStream(selectedImage);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	           
				
				photos = BitmapFactory.decodeStream(imageStream);
				photoImage.setImageBitmap(photos);
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
