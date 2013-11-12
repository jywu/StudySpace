package edu.upenn.cis573;

import java.io.Serializable;
import java.util.ArrayList;

import edu.upenn.cis573.datastructure.Room;

/**
 * This class stores all of the data regarding a building, such as its type 
 * (eg. Engineering, Wharton, Library, or Others), maximum occupancy, additional 
 * features (eg. whiteboard, projector), and an ArrayList of Room objects since 
 * some buildings can have multiple study rooms.
 */
public class StudySpace implements Serializable, Comparable<StudySpace> {

    private static final long serialVersionUID = 1L;

    // Constants, need to be public
    public final static String ENGINEERING = "Engineering";
    public final static String WHARTON = "Wharton";
    public final static String LIBRARIES = "Libraries";
    public final static String OTHER = "Other";

    // Attributes
    private String  buildingName;
    private String  spaceName;
    private String  privacy;
    private String  reserve_type;
    private String  comments;
    private double  latitude;
    private double  longitude;
    private double  distance;
    private int     number_of_rooms;
    private int     max_occupancy;
    private boolean has_whiteboard;
    private boolean has_computer;
    private boolean has_big_screen;
    private Room[]  rooms;
    private String note;
    private int startTime;
    private int endTime;
    
    private long date;

    public StudySpace(String name, double lat, double lon, int num_rooms,
            String b_name, int max_occ, boolean has_wh, String pri,
            boolean has_comp, String res_type, boolean has_big_s, String comm,
            Room[] r) {
        spaceName       = name;
        latitude        = lat;
        longitude       = lon;
        number_of_rooms = num_rooms;
        buildingName    = b_name;
        max_occupancy   = max_occ;
        has_whiteboard  = has_wh;
        privacy         = pri;
        has_computer    = has_comp;
        reserve_type    = res_type;
        has_big_screen  = has_big_s;
        comments        = comm;
        rooms           = r;
    }
    
    protected int getStartTime() {
        return startTime;
    }

    protected void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    protected int getEndTime() {
        return endTime;
    }

    protected void setEndTime(int endTime) {
        this.endTime = endTime;
    }

    public StudySpace() { }
    
    //sort the studyspace based on the distance
    @Override
    public int compareTo(StudySpace compareSpace){
        
        return (int)(this.distance - compareSpace.distance);
    
    }
    
    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
    
    public void setDistance(double d){
        this.distance = d;
    }
    
    public double getDistance(){
        return this.distance;
    }

    public String getSpaceName() {
        return spaceName;
    }

    public double getSpaceLatitude() {
        return latitude;
    }

    public double getSpaceLongitude() {
        return longitude;
    }

    public int getNumberOfRooms() {
        return number_of_rooms;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public int getMaximumOccupancy() {
        return max_occupancy;
    }

    public boolean hasWhiteboard() {
        return has_whiteboard;
    }

    public String getPrivacy() {
        return privacy;
    }

    public boolean hasComputer() {
        return has_computer;
    }

    public String getReserveType() {
        return reserve_type;
    }

    public boolean has_big_screen() {
        return has_big_screen;
    }

    public String getComments() {
        return comments;
    }

    public Room[] getRooms() {
        return rooms;
    }
    
    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public void setSpaceName(String spaceName) {
        this.spaceName = spaceName;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setNumber_of_rooms(int number_of_rooms) {
        this.number_of_rooms = number_of_rooms;
    }

    public void setMax_occupancy(int max_occupancy) {
        this.max_occupancy = max_occupancy;
    }

    public void setHas_whiteboard(boolean has_whiteboard) {
        this.has_whiteboard = has_whiteboard;
    }

    public void setPrivacy(String privacy) {
        this.privacy = privacy;
    }

    public void setHas_computer(boolean has_computer) {
        this.has_computer = has_computer;
    }

    public void setReserve_type(String reserve_type) {
        this.reserve_type = reserve_type;
    }

    public void setHas_big_screen(boolean has_big_screen) {
        this.has_big_screen = has_big_screen;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public void setRooms(Room[] rooms) {
        this.rooms = rooms;
    }

    // get the list of roomNames as a string
    public String getRoomNames() {

        // GSR has a lot of rooms so it's being formatted differently
        if (getSpaceName().equals("GSR"))
            return getGSRNames();
        else {
            String out = "";
            for (Room r : getRooms())
                out = out + r.getRoomName() + " ";
            return out;
        }
    }

    public String getGSRNames() {
        
        System.out.println("We got a GSRNAMES ----- What is it!-----");
        ArrayList<Integer> F = new ArrayList<Integer>();
        ArrayList<Integer> G = new ArrayList<Integer>();
        ArrayList<Integer> sec = new ArrayList<Integer>();
        ArrayList<Integer> third = new ArrayList<Integer>();
        ArrayList<String> oth = new ArrayList<String>();

        String out = "";

        for (Room r : getRooms()) {
            char floor = r.getRoomName().charAt(0);

            int num = Integer.parseInt(r.getRoomName().substring(1));

            if (floor == 'F')
                F.add(num);
            else if (floor == 'G')
                G.add(num);
            else if (floor == '2')
                sec.add(num);
            else if (floor == '3')
                third.add(num);
            else
                oth.add(r.getRoomName());
        }

        out = out + sortToString(F, "F") + "\n\n"; // string builders?
        out = out + sortToString(G, "G") + "\n\n";
        out = out + sortToString(sec, "2") + "\n\n";
        out = out + sortToString(third, "3") + "\n";
        for (String s : oth)
            out = out + s + " ";
        return out;
    }

    private String sortToString(ArrayList<Integer> arr, String floor) {
        // Counting Sort
        int[] C = new int[100];
        ArrayList<Integer> S = new ArrayList<Integer>();

        for (int i = 0; i < arr.size(); i++) {
            C[arr.get(i)]++;
            S.add(null);
        }

        for (int k = 1; k <= 99; k++) {
            C[k] += C[k - 1];
        }

        for (int j = arr.size() - 1; j >= 0; j--) {
            S.set(C[arr.get(j)] - 1, arr.get(j));
            C[arr.get(j)]--; // Should never have duplicates
        }

        String out = "";

        for (int i : S) {
            out = out + floor + Integer.toString(i) + " ";
        }
        return out;
    }

    public String getBuildingType() {

        if (buildingName.equals("Towne Building")
                || buildingName.equals("Levine Hall")
                || buildingName.equals("Skirkanich Hall")) {
            return ENGINEERING;
        } else if (buildingName.equals("Jon M. Huntsman Hall")) {
            return WHARTON;
        } else if (buildingName.equals("Van Pelt Library")
                || buildingName.equals("Biomedical Library")
                || buildingName.equals("Lippincott Library")
                || buildingName.equals("Museum Library")) {
            return LIBRARIES;
        } else {
            return OTHER;
        }
    }
    
    public String getNote(){
    	return this.note;
    }
    
    public void setNote(String note){
    	this.note = note;
    }

}
