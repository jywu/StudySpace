package edu.upenn.cis573;

import java.text.DecimalFormat;
import java.util.ArrayList;
import android.app.Activity;
import android.os.Bundle;
import edu.upenn.cis573.database.DBManager;

public class Stats extends Activity {

    private ArrayList<StudySpace> histories;
    private int totalNum;
    private int[] buildingStats;
    private int[] timeStats;
    private int[] dayStats;
    private double averageGroupSize;
    private double averageHours;
    private double hours;
    private String result;

    public Stats() {
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
        hours = 0.0;

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
        DecimalFormat df = new DecimalFormat("#.##");
        result = "Total number of reservation: " + totalNum + "\n"
                + "Total hours of meeting: " + hours + "\n"
                + "Average hours of meeting: " + df.format(averageHours) + "\n"
                + "Average meeting group size: " + df.format(averageGroupSize) + "\n"                
                + "Number of reservations in building: \n"
                + "\tEngineering: " + buildingStats[0] + "\n"
                + "\tWharton: " + buildingStats[1] + "\n"
                + "\tLibrary: " + buildingStats[2] + "\n"
                + "\tOthers: " + buildingStats[3] + "\n";
    }

    private void getStats() {
        totalNum = getTotalNum();
        int groupSize = 0;
        for(StudySpace history: histories) {
            getBuildingStats(history.getBuildingType());
            hours += getHours(history);
            groupSize += 0.0 + history.getGroupSize();

        }
        if(totalNum == 0) {
            averageGroupSize = 0;
            averageHours = 0;
        }else {
            averageGroupSize  = groupSize * 1.0 / totalNum;
            averageHours = hours * 1.0 / totalNum;
        }
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

    private double getHours(StudySpace history) {
        int startDate = history.getStartDate();
        int endDate = history.getEndDate();
        int startHour = history.getStartHour();
        int endHour = history.getEndHour();
        int startMin = history.getStartMin();
        int endMin = history.getEndMin();
        
        int diffDays = endDate - startDate;
        int diffHours = endHour - startHour;
        int diffMins = endMin - startMin;
        
        int duration = diffDays * 24 + diffHours + diffMins / 60;
        return duration;
    }


}
