package edu.upenn.cis573;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import edu.upenn.cis573.R;
import edu.upenn.cis573.database.DBManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class HistoryListActivity extends ListActivity{

    private Button clearHistoryButton;
    private TextView searchButton;
    private ImageView statsButton;
    private ArrayList<StudySpace> historyList;
    private HistoryListAdapter historyListAdapter;
    
    public static final int ACTIVITY_ViewSpaceDetails = 1;
    public static final int ACTIVITY_SearchActivity = 2;
    public static final int ACTIVITY_ViewRooms = 3;
    public static final int ACTIVITY_ViewNote = 4;
    public static final int ACTIVITY_ViewStats = 5;
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.historyview);
        
        // retrieves history from database
        historyList = DBManager.query();
        historyListAdapter = new HistoryListAdapter(this, R.layout.historyviewitem, historyList);
        setListAdapter(historyListAdapter);
        
        // attaches listener to the filter
        final TextView search = (EditText) findViewById(R.id.filter_history);
        search.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                String query = search.getText().toString();
                historyListAdapter.filterResults(query);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            public void onTextChanged(CharSequence s, int start, int before, int count) { }
        });
        
        // attaches listener to the Clear History button
        clearHistoryButton = (Button) findViewById(R.id.clearHistoryButton);
        clearHistoryButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("Clear History button clicked!");
                confirmDialog();
            }
        });
        
        // attaches listener to the Search button(text view)
        searchButton = (TextView) findViewById(R.id.searchButton);
        final HistoryListActivity currContext = this;
        searchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("Search button clicked!");
                currContext.finish();
            }
        });
    }
    
    public void onClickStats(View v) {
        showStatsDialog();
    }
    
    public void showStatsDialog() {
        Stats stats = new Stats();
        new Builder(this)
        .setTitle("Statistics")
        .setMessage(stats.getResult())
        .show();
    }

    //XXX defines click behaviors for the MapView button
    public void onMapViewClickHistory(View view) {
        System.out.println("MapView button clicked!");
        Intent i = new Intent(this, CustomBuildingMap.class);
        i.putExtra("STUDYSPACELIST", historyList);
        startActivityForResult(i, -1);
    }
    
    protected void confirmDialog() {
        final Context currContext = this;
        AlertDialog.Builder builder = new AlertDialog.Builder(currContext);
        builder.setMessage("Are you sure to clear? (This action is unrecoverable)");
        builder.setTitle("Confirmation");
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
        });
    
        builder.setNegativeButton("Cancel", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    
        builder.create().show();
    }
    
    public void showNoNotesDialogue(){
        new Builder(this)
        .setTitle("No notes")
        .setMessage("No notes for this reservation!")
        .show();
    }
    
    private class HistoryListAdapter extends ArrayAdapter<StudySpace> {

        private ArrayList<StudySpace> list_items;
        private ArrayList<StudySpace> orig_items;
        private ArrayList<StudySpace> before_search;

        public HistoryListAdapter(Context context, int textViewResourceId,
                ArrayList<StudySpace> items) {
            super(context, textViewResourceId, items);
            list_items = items;
            orig_items = items;
            before_search = (ArrayList<StudySpace>) list_items.clone();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) getContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.sslistitem, null);
            }

            StudySpace studySpace = list_items.get(position);
            if (studySpace != null) {

                TextView tt = (TextView) v.findViewById(R.id.nametext);
                TextView bt = (TextView) v.findViewById(R.id.roomtext);
                if (tt != null) {
                    tt.setText(studySpace.getBuildingName() + " - " + studySpace.getSpaceName());
                }
                if (bt != null) {
                    if (studySpace.getNumberOfRooms() == 1) {
                        bt.setText(studySpace.getRooms()[0].getRoomName());
                    } else {
                        bt.setText(studySpace.getRooms()[0].getRoomName() + " (and "
                                + String.valueOf(studySpace.getNumberOfRooms() - 1)
                                + " others)");
                    }
                }
                ImageView image = (ImageView) v.findViewById(R.id.icon);
                int resID;
                if (image != null) {
                    Resources resource = getResources();
                    if (studySpace.getBuildingType().equals(StudySpace.ENGINEERING))
                        resID = resource.getIdentifier("engiicon", "drawable",
                                getPackageName());
                    else if (studySpace.getBuildingType().equals(StudySpace.WHARTON))
                        resID = resource.getIdentifier("whartonicon",
                                "drawable", getPackageName());
                    else if (studySpace.getBuildingType().equals(StudySpace.LIBRARIES))
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
                if (priv != null && studySpace.getPrivacy().equals("S"))
                    priv.setVisibility(View.INVISIBLE);
                else
                    priv.setVisibility(View.VISIBLE);
                if (wb != null && !studySpace.hasWhiteboard())
                    wb.setVisibility(View.INVISIBLE);
                else
                    wb.setVisibility(View.VISIBLE);
                if (comp != null && !studySpace.hasComputer())
                    comp.setVisibility(View.INVISIBLE);
                else
                    comp.setVisibility(View.VISIBLE);
                if (proj != null && !studySpace.has_big_screen())
                    proj.setVisibility(View.INVISIBLE);
                else
                    proj.setVisibility(View.VISIBLE);
            }
            
            
          //defines actions for on-click of each note button
            View note = v.findViewById(R.id.ViewNoteButton);
            note.setOnClickListener(new android.view.View.OnClickListener() {
                StudySpace studySpace;
                @Override
                public void onClick(View v) {
                    String noteText = DBManager.query(studySpace);
                    if(noteText.equals("")) {
                        showNoNotesDialogue();
                        return;
                    }
                    Intent i = new Intent(getContext(), EditNoteActivity.class);
                    Bundle extras = new Bundle();
                    extras.putString("NOTE_TEXT", noteText);
                    extras.putSerializable("HISTORY", studySpace);  
                    extras.putBoolean("ISVIEW", true);
                    i.putExtras(extras);
                    startActivityForResult(i,
                            HistoryListActivity.ACTIVITY_ViewNote);
                }
                
                public android.view.View.OnClickListener init(StudySpace st){
                	studySpace = st;
                	return this;
                }
                
            }.init(studySpace));
            
          //defines actions for on-click of each entry
            View entry = v.findViewById(R.id.entry);           
            entry.setOnClickListener(new android.view.View.OnClickListener(){
            	int position;
                @Override
                public void onClick(View v) {
                	ArrayList<StudySpace> historyList2 = DBManager.query();
                
                	StudySpace studySpace = historyList2.get(position);
                    Intent i = new Intent(getContext(), HistoryDetails.class);
                    
                    i.putExtra("STUDYSPACE", studySpace);
                    startActivityForResult(i,
                            HistoryListActivity.ACTIVITY_ViewSpaceDetails);                  
                }
                
                public android.view.View.OnClickListener init(int pos){
                	position = pos;
                	return this;
                }
                
            }.init(position));
            
            //defines actions for long on-click of each entry
            entry.setOnLongClickListener(new OnLongClickListener() {
            	StudySpace studySpace;
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
                            			StudySpace studySpace;
                                        public void onClick(DialogInterface dialog, int id) {
                                            Intent k = null;
                                            if(studySpace.getBuildingType().equals(StudySpace.WHARTON)) {
                                                k = new Intent(Intent.ACTION_VIEW, Uri.parse("https://spike.wharton.upenn.edu/Calendar/gsr.cfm?"));
                                            } else if(studySpace.getBuildingType().equals(StudySpace.ENGINEERING)) {
                                                k = new Intent(Intent.ACTION_VIEW, Uri.parse("https://weblogin.pennkey.upenn.edu/login/?factors=UPENN.EDU&cosign-seas-www_userpages-1&https://www.seas.upenn.edu/about-seas/room-reservation/form.php"));
                                            } else if(studySpace.getBuildingType().equals(StudySpace.LIBRARIES)) {
                                                k = new Intent(Intent.ACTION_VIEW, Uri.parse("https://weblogin.library.upenn.edu/cgi-bin/login?authz=grabit&app=http://bookit.library.upenn.edu/cgi-bin/rooms/rooms"));
                                            } else {
                                                Calendar cal = Calendar.getInstance(Locale.US);              
                                                k = new Intent(Intent.ACTION_EDIT);
                                                k.setType("vnd.android.cursor.item/event");
                                                k.putExtra("title", "PennStudySpaces Reservation confirmed. ");
                                                k.putExtra("description", "Supported by PennStudySpaces");
                                                k.putExtra("eventLocation", studySpace.getBuildingName()+" - "+studySpace.getRooms()[0].getRoomName());
                                                k.putExtra("beginTime", cal.getTimeInMillis());
                                                k.putExtra("endTime", cal.getTimeInMillis()+60*60*1000);
                                            }
                                            startActivity(k);
                                        }
                                        
                                        public DialogInterface.OnClickListener init(StudySpace st){
                                        	studySpace = st;
                                        	return this;
                                        }
                                        
                                    }.init(studySpace))
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
                
                public OnLongClickListener init(StudySpace st){
                	studySpace = st;
                	return this;
                }
                
            }.init(studySpace));
            return v;
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
    }
}
