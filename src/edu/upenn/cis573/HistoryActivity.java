package edu.upenn.cis573;

import edu.upenn.cis573.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HistoryActivity extends Activity{

	private Button clearHistory;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.historyview);      
        getWidget();
        clearHistory.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				System.out.println("clicked!");
				confirmDialog();
			}
		});		
    }
    
    protected void confirmDialog() {
    	  AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	  builder.setMessage("Are you sure to clear?");

    	  builder.setTitle("Confirmation");

    	  builder.setPositiveButton("Yes", new OnClickListener() {

    	   @Override
    	   public void onClick(DialogInterface dialog, int which) {
    	    dialog.dismiss();
    	   }
    	  });

    	  builder.setNegativeButton("Cancel", new OnClickListener() {

    	   @Override
    	   public void onClick(DialogInterface dialog, int which) {
    	    dialog.dismiss();
    	   }
    	  });

    	  builder.create().show();
    	 }
    
    private void getWidget(){
    	clearHistory = (Button) findViewById(R.id.clearHistory);
    }
}
