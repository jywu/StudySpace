package edu.upenn.cis573;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * This class could probably be merged with the StudySpace 
 * class, but it basically represents a building instead of 
 * a single room. This allows the CustomBuildingMap class 
 * to know the number of rooms available in a single building.
 */
public class Building implements Serializable {

    private static final long serialVersionUID = 1L;

    private ArrayList<StudySpace> rooms;

    public Building(ArrayList<StudySpace> olist) {
        rooms = new ArrayList<StudySpace>(olist);
    }

    public double getSpaceLatitude() {
        return rooms.get(0).getSpaceLatitude();
    }

    public double getSpaceLongitude() {
        return rooms.get(0).getSpaceLongitude();
    }

    public String getBuildingName() {
        return rooms.get(0).getBuildingName();
    }
    
    public int getRoomCount() {
        return rooms.size();
    }

    public double getDistance() {
        return rooms.get(0).getDistance();
    }

    public ArrayList<StudySpace> getRooms() {
        return rooms;
    }

}
