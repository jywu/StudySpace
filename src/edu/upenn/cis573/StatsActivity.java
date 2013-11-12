package edu.upenn.cis573;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import edu.upenn.cis573.database.DBManager;

public class StatsActivity extends Activity {

    private ArrayList<StudySpace> histories;
    private int totalNum;
    private int[] buildingStats;
    private int[] timeStats;
    private int[] dayStats;
    private int averageGroupSize;
    private int averageHours;
    private String result;
    
    public StatsActivity() {
        histories = DBManager.query();
        setUp();
        getStats();
        generateResult();
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stats); 

        histories = DBManager.query();
        setUp();
        getStats();
    }
    
    private void setUp() {
        totalNum = 0;
        averageGroupSize = 0;
        averageHours = 0;

        buildingStats = new int[4]; //Engineering, Wharton, Library, Others
        for(int i = 0; i < buildingStats.length; i++)
            buildingStats[i] = 0;

        timeStats = new int[4]; //morning, afternoon, evening, night
        for(int i = 0; i < timeStats.length; i++)
            timeStats[i] = 0;

        dayStats = new int[2];
        for(int i = 0; i < dayStats.length; i++)
            dayStats[0] = 0;

    }
    
    protected String getResult() {
        return result;
    }

    private void generateResult() {
        result = "Total number of reservation: " + totalNum + "\n"
                + "Number of reservations in building: \n"
                + "\tEngineering: " + buildingStats[0] + "\n"
                + "\tWharton: " + buildingStats[1] + "\n"
                + "\tLibrary: " + buildingStats[2] + "\n"
                + "\tOthers: " + buildingStats[3] + "\n";
    }

    private void getStats() {
        getTotalNum();
        int hours = 0;
        int groupSize = 0;
        for(StudySpace history: histories) {
            getBuildingStats(history.getBuildingType());
            //hours += history.getHours();
            //groupSize += history.getGroupSize();

        }
        averageGroupSize  = groupSize / totalNum;
        averageHours = hours / totalNum;
    }

    private int getTotalNum() {
        return histories.size();
    }

    private void getBuildingStats(String type) {
        if (type.equals(StudySpace.ENGINEERING))
            ++buildingStats[0];
        else if (type.equals(StudySpace.WHARTON))
            ++buildingStats[1];
        else if (type.equals(StudySpace.LIBRARIES))
            ++buildingStats[2];
        else
            ++buildingStats[3];
    }

    private void getTimeStats() {
    }

    private void getDayStats() {
    }

    private int getAverageHours() {
        return -1;
    }


}
