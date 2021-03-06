package edu.upenn.cis573;

import java.io.Serializable;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageView;
import edu.upenn.cis573.database.DBManager;
import edu.upenn.cis573.datastructure.Preferences;
import edu.upenn.cis573.util.ConnectionDetector;

/**
 * The StudySpaceDetails class extends FragmentActivity and 
 * is used to define the methods to be called when a tab is 
 * clicked (eg. Info, Map), or when the buttons on the page 
 * (eg. Add to Favorites) are clicked.
 */
public class StudySpaceDetails extends FragmentActivity {

    private TabDetails tabdetails;
    private StudySpace space;
    private Preferences p;
    private SharedPreferences favorites;
    Boolean isInternetPresent = false;

    // Connection detector class
    ConnectionDetector cd;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ssdetails);

        favorites = getSharedPreferences(StudySpaceListActivity.FAV_PREFERENCES, 0);

        Intent i = getIntent();
        space = (StudySpace) i.getSerializableExtra("STUDYSPACE");
        p = (Preferences) i.getSerializableExtra("PREFERENCES");
        if(p == null) {
            p = new Preferences();
        }
        tabdetails = new TabDetails();

        cd = new ConnectionDetector(getApplicationContext());


        // get Internet status
        isInternetPresent = cd.isConnectingToInternet();

        // check for Internet status
        if (isInternetPresent) {

            // Saves the first state of the code
            ImageView image = (ImageView) findViewById(R.id.button_details);
            image.setImageResource(R.color.lightblue);
            FragmentTransaction transaction = getSupportFragmentManager()
                    .beginTransaction();
            transaction.replace(R.id.fragment_container, tabdetails);
            transaction.commit();
        }

        else {
            // Internet connection is not present
            // Ask user to connect to Internet
            cd.showAlertDialog(StudySpaceDetails.this, "No Internet Connection",
                    "You don't have internet connection.", false);

        }
    }

    public void onShareClick(View v) { }

    public void onDetailsClick(View v) {
        ImageView image = (ImageView) findViewById(R.id.button_details);
        image.setImageResource(R.color.lightblue);

        // Create new fragment and transaction
        // Fragment newFragment = new TabDetails();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, tabdetails);
        // transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }

    public void onMapClick(View v){
        Intent i = new Intent(this, CustomBuildingMap.class);
        ArrayList<StudySpace> olist = new ArrayList<StudySpace>();
        olist.add(space);
        i.putExtra("STUDYSPACELIST", olist);
        startActivity(i);
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            Intent i = new Intent();

            i.putExtra("PREFERENCES", (Serializable)p);
            setResult(RESULT_OK, i);
            //ends this activity
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    public void onFavClick(View v){
        p.addFavorites(space.getBuildingName()+space.getSpaceName());
        tabdetails.onFavClick(v);

        SharedPreferences.Editor editor = favorites.edit();
        editor.putBoolean(space.getBuildingName()+space.getSpaceName(), true);
        editor.commit();
    }
    
    public void onRemoveFavClick(View v){
        p.removeFavorites(space.getBuildingName()+space.getSpaceName());
        tabdetails.onRemoveFavClick(v);
        SharedPreferences.Editor editor = favorites.edit();
        editor.putBoolean(space.getBuildingName()+space.getSpaceName(), false);
        editor.commit();
    }
    
    public void onCalClick(View v) {
        addToHistory();
        tabdetails.onCalClick(v);
    }

    public void onReserveClick(View v){
        addToHistory();
        tabdetails.onReserveClick(v);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    
    /**
     * Adds this StudySpace object to the database.
     */
    private void addToHistory() {
        //o.setDate(System.currentTimeMillis());
        if(DBManager.add(space) == -1) {
            showClearHistoryDialog();
        }
    }
    
    public void showClearHistoryDialog() {
        final Context currContext = this;
        AlertDialog.Builder builder = new AlertDialog.Builder(currContext);
        builder.setMessage("The database if full, would you like to clear history? (This action is unrecoverable)");
        builder.setTitle("Database Full");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
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

        });
        builder.show();


    }

}
