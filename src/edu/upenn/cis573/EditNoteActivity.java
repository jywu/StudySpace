package edu.upenn.cis573;

import edu.upenn.cis573.database.DBManager;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class EditNoteActivity extends Activity {

    Button saveButton;
    EditText  editNoteText;
    StudySpace history;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);
        // check if this is a new note or just showing a saved note
        saveButton = (Button)findViewById(R.id.savebutton);
        editNoteText  = (EditText)findViewById(R.id.editnote1);

        Bundle extras = getIntent().getExtras();		
        if(extras != null) {
            String text = extras.getString("NOTE_TEXT");
            history = (StudySpace)extras.getSerializable("HISTORY");
            editNoteText.setEnabled(false);
            saveButton.setEnabled(false);
            editNoteText.setText(text);
        }	   
    }
    


    public void onSaveNote(View view){
        Log.v("EditText", editNoteText.getText().toString());
        String noteText = editNoteText.getText().toString();
        if(addNoteToDB(noteText) == 1)
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

    public int addNoteToDB(String noteText){
        if(DBManager.updateDb(noteText) == -1) {
            showClearHistoryDialog();
            return -1;
        };
        return 1;
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
}
