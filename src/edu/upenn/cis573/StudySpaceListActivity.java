package edu.upenn.cis573;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import edu.upenn.cis573.database.DBManager;
import edu.upenn.cis573.datastructure.Preferences;
import edu.upenn.cis573.datastructure.Room;
import edu.upenn.cis573.datastructure.SearchOptions;
import edu.upenn.cis573.util.APIAccessor;
import edu.upenn.cis573.util.ConnectionDetector;

/**
 * The StudySpaceListActivity class is the main class of the application, 
 * from which many of the other classes are called. It extends ListActivity 
 * to display the various lists of study spaces according to the user's 
 * specifications.
 */
public class StudySpaceListActivity extends ListActivity {
    /*
     ******************************************
     *                Members
     ******************************************
     */
    public static final int ACTIVITY_ViewSpaceDetails = 1;
    public static final int ACTIVITY_SearchActivity = 2;
    public static final int ACTIVITY_ViewRooms = 3;
    static final String FAV_PREFERENCES = "favoritePreferences";

    private ProgressDialog ss_ProgressDialog = null; // Dialog when loading
    private ArrayList<StudySpace> ss_list = null; // List containing available rooms
    private StudySpaceListAdapter ss_adapter; // Adapter to format list items
    private Runnable viewAvailableSpaces; // runnable to get available spaces
    private SearchOptions searchOptions; // create a default searchoption later
    private Preferences preferences;
    private SharedPreferences favorites;

    private boolean mapViewClicked;
    private boolean favSelected;

    /*
     ******************************************
     *          Overridden Methods
     ******************************************
     */
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        DBManager.closeDB();
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sslist);

        favorites = getSharedPreferences(FAV_PREFERENCES, 0);
        ss_list = new ArrayList<StudySpace>(); // List to store StudySpaces
        ss_adapter = new StudySpaceListAdapter(this, R.layout.sslistitem, ss_list);
        setListAdapter(ss_adapter); // Adapter to read list and display
        Map<String, ?> items = favorites.getAll();
        preferences = new Preferences(); // Change this when bundle is implemented.

        for (String s : items.keySet()) {
            if (Boolean.parseBoolean(items.get(s).toString())) {
                preferences.addFavorites(s);
            }
        }

        // runs a new thread to scrap text from the website
        viewAvailableSpaces = new Runnable() {
            public void run() {
                getSpaces(); // retrieves list of study spaces
            }
        };
        Thread thread = new Thread(null, viewAvailableSpaces, "ThreadName"); // change name?
        thread.start();

        // flag for Internet connection status
        Boolean isInternetPresent = false;

        // Connection detector class
        ConnectionDetector cd = new ConnectionDetector(getApplicationContext());

        // get Internet status
        isInternetPresent = cd.isConnectingToInternet();

        if (isInternetPresent) {
            ss_ProgressDialog = ProgressDialog.show(
                    StudySpaceListActivity.this, "Please wait...",
                    "Retrieving data ...", true);
        } else {
            ss_ProgressDialog = ProgressDialog.show(
                    StudySpaceListActivity.this, "Connection error ",
                    " Please connect to internet.....", true);
        }

        // Start up the search options screen
        if(searchOptions == null) {
            Intent i = new Intent(this, SearchActivity.class);
            startActivityForResult(i,
                    StudySpaceListActivity.ACTIVITY_SearchActivity);
        }


    }

    /**
     * This methods handles key events that are not handled by
     * any Views within this Activity.
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if (!mapViewClicked) {
                Intent i = new Intent(this, SearchActivity.class);
                startActivityForResult(i,
                        StudySpaceListActivity.ACTIVITY_SearchActivity);
            } else {
                ss_adapter.favToAll();
                Intent i = new Intent(this, CustomBuildingMap.class);
                i.putExtra("STUDYSPACELIST", this.ss_adapter.list_items);
                startActivityForResult(i,
                        StudySpaceListActivity.ACTIVITY_ViewRooms);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    /**
     * This method is called when an activity you launched exits, giving you 
     * the requestCode you started it with, the resultCode it returned, and 
     * any additional data from it. The resultCode will be RESULT_CANCELED 
     * if the activity explicitly returned that, didn't return any result, 
     * or crashed during its operation. 
     */
    protected void onActivityResult(int requestCode, int resultCode,
            Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
        case ACTIVITY_SearchActivity:
            searchOptions = (SearchOptions) intent
            .getParcelableExtra("SEARCH_OPTIONS");
            ImageView image = (ImageView) this
                    .findViewById(R.id.favorite_button);
            image.setImageResource(R.color.yellow);
            favSelected = searchOptions.getFavSelected();
            System.out.println("SEARCH ACTIVITY " + favSelected);
            if (favSelected) {
                ss_adapter.allToFav();
            } else {
                ss_adapter.filterSpaces();
            }
            ss_adapter.updateFavorites(preferences);

            // attaches listener to the filter
            final TextView search = (EditText) findViewById(R.id.filter);
            search.addTextChangedListener(new TextWatcher() {
                public void afterTextChanged(Editable s) {
                    String query = search.getText().toString();
                    ss_adapter.filterResults(query);
                }

                public void beforeTextChanged(CharSequence s, int start, int count,
                        int after) {
                }

                public void onTextChanged(CharSequence s, int start, int before,
                        int count) {
                }
            });

            break;
        case ACTIVITY_ViewSpaceDetails:
            preferences = (Preferences) intent
            .getSerializableExtra("PREFERENCES");
            ss_adapter.updateFavorites(preferences);
            break;
        case ACTIVITY_ViewRooms:
            Log.d("List", "ViewRooms");
            @SuppressWarnings("unchecked")
            ArrayList<StudySpace> nlist = (ArrayList<StudySpace>) intent
            .getSerializableExtra("STUDYSPACELIST");
            if (!nlist.isEmpty()) {
                ss_adapter.setListItems(nlist);
                this.mapViewClicked = true;
            } else {
                this.mapViewClicked = false;
            }
            break;
        }
    }

    /*
     ********************************************
     * Defines click behaviors for main buttons 
     ********************************************
     */
    public void onFavClick(View v) {
        Log.d("fav", "FavClick");
        ImageView image = (ImageView) this.findViewById(R.id.favorite_button);
        if (favSelected) {
            favSelected = false;
            image.setImageResource(R.color.yellow);
            ss_adapter.favToAll();
        } else {
            favSelected = true;
            image.setImageResource(R.color.lightblue);
            ss_adapter.allToFav();
        }
    }

    public void onSearchClick(View view) {
        // Start up the search options screen
        Intent i = new Intent(this, SearchActivity.class);
        startActivityForResult(i,
                StudySpaceListActivity.ACTIVITY_SearchActivity);
    }

    public void onMapViewClick(View view) {
        // Start up the search options screen
        Log.d("MapView", "Clicked");
        Intent i = new Intent(this, CustomBuildingMap.class);
        i.putExtra("STUDYSPACELIST", this.ss_adapter.list_items);
        startActivityForResult(i,
                StudySpaceListActivity.ACTIVITY_ViewRooms);
    }

    // detail of the nearest study space
    public void onFNButtonSelected(ArrayList<StudySpace> arr) {
        Intent i = new Intent(this, StudySpaceDetails.class);
        i.putExtra("STUDYSPACE", (StudySpace) arr.get(0));
        i.putExtra("PREFERENCES", preferences);
        startActivityForResult(i,
                StudySpaceListActivity.ACTIVITY_ViewSpaceDetails);
    }

    /*
     ******************************************
     *                Helpers
     ******************************************
     */
    private Runnable returnRes = new Runnable() {
        public void run() {
            if(ss_ProgressDialog.isShowing()) {
                ss_ProgressDialog.dismiss();
            }
            ss_adapter.notifyDataSetChanged();
            if (searchOptions != null)
                ss_adapter.filterSpaces();
        }
    };

    public void getSpaces() {
        try {
            System.out.println("Calling the getSpace method!");
            ss_list.addAll(APIAccessor.getStudySpaces(this.getApplicationContext()));
            ss_adapter.updateFavorites(preferences);
            Thread.sleep(2000); // appears to load for 2 seconds

            Log.i("ARRAY", "" + ss_list.size());
        } catch (Exception e) {
            Log.e("BACKGROUND_PROC", "Something went wrong!");
        }
        runOnUiThread(returnRes);
    }

    public ArrayList<StudySpace> filterByDate(ArrayList<StudySpace> arr) {
        Date d1 = searchOptions.getCompleteStartTime();
        Date d2 = searchOptions.getCompleteEndTime();

        for (int i = arr.size() - 1; i >= 0; i--) {
            boolean flag = false;
            for (Room r : arr.get(i).getRooms()) {
                try {
                    if (r.searchAvailability(d1, d2)) {
                        flag = true;
                    }
                } catch (Exception e) {
                    // Log.e("exception","here");
                    // arr.remove(i); shouldn't be here
                }
            }

            if (!flag) {
                arr.remove(i);
            }else {
                arr.get(i).setStartHour(searchOptions.getStartHour());
                arr.get(i).setEndHour(searchOptions.getEndHour());
                arr.get(i).setStartMin(searchOptions.getStartMinute());
                arr.get(i).setEndMin(searchOptions.getEndMinute());
                arr.get(i).setStartDate(searchOptions.getDay());
                arr.get(i).setEndDate(searchOptions.getDay());
                arr.get(i).setMonth(searchOptions.getMonth());
                arr.get(i).setYear(searchOptions.getYear());
                arr.get(i).setGroupSize(searchOptions.getNumberOfPeople());
            }
        }
        return arr;
    }

    public ArrayList<StudySpace> filterByPeople(ArrayList<StudySpace> arr) {
        for (int i = arr.size() - 1; i >= 0; i--) {
            if (arr.get(i).getMaximumOccupancy() < searchOptions.getNumberOfPeople())
                arr.remove(i);
        }
        return arr;
    }

    public ArrayList<StudySpace> sortByDistance(ArrayList<StudySpace> arr) {
        double currentLatitude = SearchActivity.latitude;
        double currentLongitude = SearchActivity.longitude;
        for (StudySpace temp : arr) {
            double spaceLatitude = temp.getSpaceLatitude();
            double spaceLongitude = temp.getSpaceLongitude();
            float results[] = new float[3];
            Location.distanceBetween(currentLatitude, currentLongitude,
                    spaceLatitude, spaceLongitude, results);
            double distance = results[0];
            temp.setDistance(distance);
        }
        Collections.sort(arr);
        System.out.println("Results have already been sorted!");

        if (arr.isEmpty()) {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    this);
            // set title
            alertDialogBuilder.setTitle("No Results Found!");

            // set dialog message
            alertDialogBuilder
            .setMessage(
                    "No rooms available with the selected criteria")
                    .setCancelable(false)
                    .setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();
        }
        return arr;
    }

    public ArrayList<StudySpace> getList() {
        return ss_list;
    }

    /**
     * StudySpaceListAdapter is a private inner class that the StudySpaceListActivity 
     * class uses to switch the user between the various lists of study spaces displayed 
     * in the ListActivity views.
     */
    private class StudySpaceListAdapter extends ArrayAdapter<StudySpace> {

        private ArrayList<StudySpace> list_items;
        private ArrayList<StudySpace> orig_items;
        private ArrayList<StudySpace> before_search;
        private ArrayList<StudySpace> fav_orig_items;
        private ArrayList<StudySpace> temp; // Store list items for when favorites is displayed

        public StudySpaceListAdapter(Context context, int textViewResourceId,
                ArrayList<StudySpace> items) {
            super(context, textViewResourceId, items);
            this.list_items = items;
            this.orig_items = items;
            //this.before_search = items;
            this.fav_orig_items = new ArrayList<StudySpace>();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) getContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.sslistitem, null);
            }

            final StudySpace o = list_items.get(position);
            if (o != null) {

                TextView tt = (TextView) v.findViewById(R.id.nametext);
                TextView bt = (TextView) v.findViewById(R.id.roomtext);
                if (tt != null) {
                    tt.setText(o.getBuildingName() + " - " + o.getSpaceName());
                }
                if (bt != null) {
                    if (o.getNumberOfRooms() == 1) {
                        bt.setText(o.getRooms()[0].getRoomName());
                    } else {
                        bt.setText(o.getRooms()[0].getRoomName() + " (and "
                                + String.valueOf(o.getNumberOfRooms() - 1)
                                + " others)");
                    }
                }
                ImageView image = (ImageView) v.findViewById(R.id.icon);
                int resID;
                if (image != null) {
                    Resources resource = getResources();
                    if (o.getBuildingType().equals(StudySpace.ENGINEERING))
                        resID = resource.getIdentifier("engiicon", "drawable",
                                getPackageName());
                    else if (o.getBuildingType().equals(StudySpace.WHARTON))
                        resID = resource.getIdentifier("whartonicon",
                                "drawable", getPackageName());
                    else if (o.getBuildingType().equals(StudySpace.LIBRARIES))
                        resID = resource.getIdentifier("libicon", "drawable",
                                getPackageName());
                    else
                        resID = resource.getIdentifier("othericon", "drawable",
                                getPackageName());
                    image.setImageResource(resID);
                }
                ImageView priv = (ImageView) v.findViewById(R.id.priv);
                ImageView wb = (ImageView) v.findViewById(R.id.wb);
                ImageView comp = (ImageView) v.findViewById(R.id.comp);
                ImageView proj = (ImageView) v.findViewById(R.id.proj);
                if (priv != null && o.getPrivacy().equals("S"))
                    priv.setVisibility(View.INVISIBLE);
                else
                    priv.setVisibility(View.VISIBLE);
                if (wb != null && !o.hasWhiteboard())
                    wb.setVisibility(View.INVISIBLE);
                else
                    wb.setVisibility(View.VISIBLE);
                if (comp != null && !o.hasComputer())
                    comp.setVisibility(View.INVISIBLE);
                else
                    comp.setVisibility(View.VISIBLE);
                if (proj != null && !o.has_big_screen())
                    proj.setVisibility(View.INVISIBLE);
                else
                    proj.setVisibility(View.VISIBLE);
            }

            View noteButton = ((ViewGroup) v).getChildAt(0);
            System.out.println(noteButton.getId());
            //action to perform note button click
            noteButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    //read note
                    //Intent intent = new Intent(this, EditNoteActivity.class);
                    //startActivityForResult(intent, -1);
                }
            });


            //defines actions for on-click of each entry

            v.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getContext(), StudySpaceDetails.class);
                    i.putExtra("STUDYSPACE", o);
                    i.putExtra("PREFERENCES", preferences);
                    startActivityForResult(i,
                            StudySpaceListActivity.ACTIVITY_ViewSpaceDetails);

                }
            });

            //defines actions for long on-click of each entry
            v.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            getContext());
                    alertDialogBuilder.setTitle("Reservation");
                    alertDialogBuilder
                    .setMessage("Would you like to make a reservation?")
                    .setCancelable(false)
                    .setPositiveButton("Reserve",
                            new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if(DBManager.add(o) == -1) {
                                showClearHistoryDialog();

                            }else {

                                Intent k = null;
                                if(o.getBuildingType().equals(StudySpace.WHARTON)) {
                                    k = new Intent(Intent.ACTION_VIEW, Uri.parse("https://spike.wharton.upenn.edu/Calendar/gsr.cfm?"));
                                } else if(o.getBuildingType().equals(StudySpace.ENGINEERING)) {
                                    k = new Intent(Intent.ACTION_VIEW, Uri.parse("https://weblogin.pennkey.upenn.edu/login/?factors=UPENN.EDU&cosign-seas-www_userpages-1&https://www.seas.upenn.edu/about-seas/room-reservation/form.php"));
                                } else if(o.getBuildingType().equals(StudySpace.LIBRARIES)) {
                                    k = new Intent(Intent.ACTION_VIEW, Uri.parse("https://weblogin.library.upenn.edu/cgi-bin/login?authz=grabit&app=http://bookit.library.upenn.edu/cgi-bin/rooms/rooms"));
                                } else {
                                    Calendar cal = Calendar.getInstance(Locale.US);              
                                    k = new Intent(Intent.ACTION_EDIT);
                                    k.setType("vnd.android.cursor.item/event");
                                    k.putExtra("title", "PennStudySpaces Reservation confirmed. ");
                                    k.putExtra("description", "Supported by PennStudySpaces");
                                    k.putExtra("eventLocation", o.getBuildingName()+" - "+o.getRooms()[0].getRoomName());
                                    k.putExtra("beginTime", cal.getTimeInMillis());
                                    k.putExtra("endTime", cal.getTimeInMillis()+60*60*1000);
                                }
                                startActivity(k);
                            }
                        }
                    })
                    .setNegativeButton("Cancel", 
                            new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                int id) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                    return true;
                }
            });
            return v;
        }


        public void showClearHistoryDialog() {
            final Context currContext = this.getContext();
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
                        Intent i = new Intent(currContext, SearchActivity.class);
                        startActivityForResult(i,
                                StudySpaceListActivity.ACTIVITY_SearchActivity);
                        //((Activity)currContext).finish();
                        
                    }
                }

            });
            builder.show();

        }

        @Override
        public int getCount() {
            return list_items.size();
        }

        @Override
        public StudySpace getItem(int position) {
            return list_items.get(position);
        }

        @SuppressWarnings("unchecked")
        public void filterSpaces() { //must be completed before calling filterResults

            ArrayList<StudySpace> filtered = (ArrayList<StudySpace>) orig_items.clone();
            int i = 0;
            while (i < filtered.size()) {
                if (!searchOptions.getEngi()
                        && filtered.get(i).getBuildingType()
                        .equals(StudySpace.ENGINEERING)) {
                    filtered.remove(i);
                    continue;
                }
                if (!searchOptions.getWhar()
                        && filtered.get(i).getBuildingType()
                        .equals(StudySpace.WHARTON)) {
                    filtered.remove(i);
                    continue;
                }
                if (!searchOptions.getLib()
                        && filtered.get(i).getBuildingType()
                        .equals(StudySpace.LIBRARIES)) {
                    filtered.remove(i);
                    continue;
                }
                if (!searchOptions.getOth()
                        && filtered.get(i).getBuildingType()
                        .equals(StudySpace.OTHER)) {
                    filtered.remove(i);
                    continue;
                }
                if (searchOptions.getPrivate()
                        && filtered.get(i).getPrivacy().equals("S")) {
                    filtered.remove(i);
                    continue;
                }

                if (searchOptions.getWhiteboard()
                        && !filtered.get(i).hasWhiteboard()) {
                    filtered.remove(i);
                    continue;
                }
                if (searchOptions.getComputer()
                        && !filtered.get(i).hasComputer()) {
                    filtered.remove(i);
                    continue;
                }
                if (searchOptions.getProjector()
                        && !filtered.get(i).has_big_screen()) {
                    filtered.remove(i);
                    continue;
                }
                if (searchOptions.getReservable()
                        && filtered.get(i).getReserveType().equals("N")) {
                    filtered.remove(i);
                    continue;
                }
                i++;
            }
            list_items = filtered;
            list_items = filterByPeople(list_items);
            list_items = filterByDate(list_items);
            list_items = sortByDistance(list_items);
            if (SearchActivity.isFNButtonClicked()) {
                System.out.println("Remove Array Method called");
                list_items = findNearest(list_items);
                SearchActivity.setFNButtonClicked(false);
            }
            this.before_search = (ArrayList<StudySpace>) this.list_items.clone();
            System.out.println("before search size = " + before_search.size());

            notifyDataSetChanged();
        }

        public ArrayList<StudySpace> findNearest(ArrayList<StudySpace> arr) {
            if(arr.size() > 1) {
                ArrayList<StudySpace> nArr = new ArrayList<StudySpace>();
                StudySpace nSpace = arr.get(0);
                double nDistance = nSpace.getDistance();
                int index = 0;
                while(index < arr.size()) {
                    if (arr.get(index).getDistance() == nDistance){
                        nArr.add(arr.get(index));
                    }
                    index++;
                }
                onFNButtonSelected(nArr);
                return nArr;
            } else {
                return arr;
            }
        }

        @SuppressWarnings("unchecked")
        public void filterResults(String query) {
            query = query.toLowerCase(Locale.US);
            list_items = (ArrayList<StudySpace>) before_search.clone();
            if (!query.equals("")) {
                for (int i = list_items.size() - 1; i >= 0; i--) {
                    StudySpace s = list_items.get(i);
                    if (s.getBuildingName().toLowerCase(Locale.US)
                            .indexOf(query) >= 0
                            || s.getSpaceName().toLowerCase(Locale.US).indexOf(query) >= 0
                            || s.getRoomNames().toLowerCase(Locale.US).indexOf(query) >= 0) {
                    } else {
                        list_items.remove(i);
                    }
                }
            }
            notifyDataSetChanged();
        }

        // switch to favorites
        public void allToFav() {
            this.temp = this.list_items; // remember list items
            this.list_items = fav_orig_items;
            notifyDataSetChanged();
        }

        public void setListItems(ArrayList<StudySpace> nlist) {
            this.temp = this.list_items; // remember list items
            this.list_items = nlist;
            notifyDataSetChanged();
        }

        public void favToAll() {
            this.list_items = this.temp; // restore list items
            notifyDataSetChanged();
        }

        @SuppressWarnings("unchecked")
        public void updateFavorites(Preferences p) {
            this.fav_orig_items = (ArrayList<StudySpace>) this.orig_items.clone();
            for (int i = fav_orig_items.size() - 1; i >= 0; i--) {
                if (!p.isFavorite(fav_orig_items.get(i).getBuildingName()
                        + fav_orig_items.get(i).getSpaceName()))
                    fav_orig_items.remove(i);
            }
        }
    }
}