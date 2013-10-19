package edu.upenn.cis573;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import edu.upenn.cis573.R;
import edu.upenn.cis573.database.DBManager;
import android.app.AlertDialog;
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

public class HistoryActivity extends ListActivity{

    private Button clearHistory;
    private ArrayList<StudySpace> historyList;
    private HistoryListAdapter historyListAdapter;
    
    public static final int ACTIVITY_ViewSpaceDetails = 1;
    public static final int ACTIVITY_SearchActivity = 2;
    public static final int ACTIVITY_ViewRooms = 3;
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.historyview);
        
        historyList = DBManager.query();
        historyListAdapter = new HistoryListAdapter(this, R.layout.historyviewitem, historyList);
        setListAdapter(historyListAdapter);
        // Start up the search options screen
//        Intent i = new Intent(this, SearchActivity.class);
//        startActivityForResult(i,
//                HistoryActivity.ACTIVITY_SearchActivity);
        final TextView search = (EditText) findViewById(R.id.filter);
        search.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                String query = search.getText().toString();
                historyListAdapter.filterResults(query);
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before,
                    int count) {
            }
        });
        
        
        //--------------------------old changes
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
    
    private class HistoryListAdapter extends ArrayAdapter<StudySpace> {

        private ArrayList<StudySpace> list_items;
        private ArrayList<StudySpace> orig_items;
        private ArrayList<StudySpace> before_search;
        private ArrayList<StudySpace> fav_orig_items;
        private ArrayList<StudySpace> temp; // Store list items for when favorites is displayed

        public HistoryListAdapter(Context context, int textViewResourceId,
                ArrayList<StudySpace> items) {
            super(context, textViewResourceId, items);
            this.list_items = items;
            this.orig_items = items;
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

//            v.setOnClickListener(new OnClickListener(){
//
//                @Override
//                public void onClick(View v) {
//                    Intent i = new Intent(getContext(), StudySpaceDetails.class);
//                    i.putExtra("STUDYSPACE", o);
//                    i.putExtra("PREFERENCES", preferences);
//                    startActivityForResult(i,
//                            StudySpaceListActivity.ACTIVITY_ViewSpaceDetails);
//                    
//                }});
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
