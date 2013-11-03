package edu.upenn.cis573;

import edu.upenn.cis573.database.DBManager;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class EditNoteActivity extends Activity {

	Button saveButton;
	EditText  editNoteText;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_note);
		
		 saveButton = (Button)findViewById(R.id.savebutton);
		 editNoteText  = (EditText)findViewById(R.id.editnote1);	   
	}
	
	public void onSaveNote(View view){
		Log.v("EditText", editNoteText.getText().toString());
		String noteText = editNoteText.getText().toString();
		addNoteToDB(noteText);
		
		showSaveDialog();	
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
	
	public void addNoteToDB(String noteText){
		DBManager.updateDb(noteText);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_note, menu);
		return true;
	}

}
