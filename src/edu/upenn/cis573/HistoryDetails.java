package edu.upenn.cis573;

import java.util.ArrayList;
import edu.upenn.cis573.util.ConnectionDetector;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageView;

public class HistoryDetails extends FragmentActivity {

    private HistoryTabDetails tabdetails;
    private StudySpace o;
    Boolean isInternetPresent = false;

    // Connection detector class
    ConnectionDetector cd;
    
    public HistoryTabDetails getTabDetails(){
    	return this.tabdetails;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.historyviewdetails);

        Intent i = getIntent();
        o = (StudySpace) i.getSerializableExtra("STUDYSPACE");
        tabdetails = new HistoryTabDetails();
        
        // get Internet status
        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        // check for Internet status
        if (isInternetPresent) {

            // Saves the first state of the code
            ImageView image = (ImageView) findViewById(R.id.button_details_history);
            image.setImageResource(R.color.lightblue);
            FragmentTransaction transaction = getSupportFragmentManager()
                    .beginTransaction();
            transaction.replace(R.id.fragment_container_history, tabdetails);
            transaction.commit();
        }

        else {
            // Internet connection is not present
            // Ask user to connect to Internet
            cd.showAlertDialog(HistoryDetails.this, "No Internet Connection",
                    "You don't have internet connection.", false);
        }
    }

    public void onDetailsClickHistory(View v) {
        ImageView image = (ImageView) findViewById(R.id.button_details_history);
        image.setImageResource(R.color.lightblue);

        // Create new fragment and transaction
        // Fragment newFragment = new TabDetails();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container_history, tabdetails);
        // transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }

    public void onMapClickHistory(View v){
        Intent i = new Intent(this, CustomBuildingMap.class);
        ArrayList<StudySpace> olist = new ArrayList<StudySpace>();
        olist.add(o);
        i.putExtra("STUDYSPACELIST", olist);
        startActivity(i);
    }
    
//    @Override//XXX delete??
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//
//            Intent i = new Intent();
//
////            i.putExtra("PREFERENCES", (Serializable)p);
//            setResult(RESULT_OK, i);
//            //ends this activity
//            finish();
//        }
//        return super.onKeyDown(keyCode, event);
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

}
