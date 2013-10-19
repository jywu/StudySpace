package edu.upenn.cis573.database;

import edu.upenn.cis573.Room;
import edu.upenn.cis573.StudySpace;

public class HistoryEntry {

    private int _id;
    private String date;
    
    private String buildingName;
    private String spaceName;
    private double latitude;
    private double longitude;
    private int number_of_rooms;
    private int max_occupancy;
    private boolean has_whiteboard;
    private String privacy;
    private boolean has_computer;
    private String reserve_type;
    private boolean has_big_screen;
    private String comments;
    private Room[] rooms;
    
    public HistoryEntry(int _id, String date, String buildingName,
            String spaceName, double latitude, double longitude,
            int number_of_rooms, int max_occupancy, boolean has_whiteboard,
            String privacy, boolean has_computer, String reserve_type,
            boolean has_big_screen, String comments, Room[] rooms) {
        this._id = _id;
        this.date = date;
        this.buildingName = buildingName;
        this.spaceName = spaceName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.number_of_rooms = number_of_rooms;
        this.max_occupancy = max_occupancy;
        this.has_whiteboard = has_whiteboard;
        this.privacy = privacy;
        this.has_computer = has_computer;
        this.reserve_type = reserve_type;
        this.has_big_screen = has_big_screen;
        this.comments = comments;
        this.rooms = rooms;
    }
    
    public HistoryEntry(StudySpace s) {
        this(0,"",s.getBuildingName(),s.getSpaceName(),s.getSpaceLatitude(),
                s.getSpaceLatitude(),s.getNumberOfRooms(),s.getMaximumOccupancy(),
                s.hasWhiteboard(),s.getPrivacy(),s.hasComputer(),s.getReserveType(),
                s.has_big_screen(),s.getComments(),s.getRooms());
    }
    
    public HistoryEntry() { }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public String getSpaceName() {
        return spaceName;
    }

    public void setSpaceName(String spaceName) {
        this.spaceName = spaceName;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getNumber_of_rooms() {
        return number_of_rooms;
    }

    public void setNumber_of_rooms(int number_of_rooms) {
        this.number_of_rooms = number_of_rooms;
    }

    public int getMax_occupancy() {
        return max_occupancy;
    }

    public void setMax_occupancy(int max_occupancy) {
        this.max_occupancy = max_occupancy;
    }

    public boolean hasWhiteboard() {
        return has_whiteboard;
    }

    public void setWhiteboard(boolean has_whiteboard) {
        this.has_whiteboard = has_whiteboard;
    }

    public String getPrivacy() {
        return privacy;
    }

    public void setPrivacy(String privacy) {
        this.privacy = privacy;
    }

    public boolean hasComputer() {
        return has_computer;
    }

    public void setComputer(boolean has_computer) {
        this.has_computer = has_computer;
    }

    public String getReserve_type() {
        return reserve_type;
    }

    public void setReserve_type(String reserve_type) {
        this.reserve_type = reserve_type;
    }

    public boolean hasBigScreen() {
        return has_big_screen;
    }

    public void setBigScreen(boolean has_big_screen) {
        this.has_big_screen = has_big_screen;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Room[] getRooms() {
        return rooms;
    }

    public void setRooms(Room[] rooms) {
        this.rooms = rooms;
    }
    
}
