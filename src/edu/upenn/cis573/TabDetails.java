package edu.upenn.cis573;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import edu.upenn.cis573.R;
import edu.upenn.cis573.datastructure.Preferences;
import edu.upenn.cis573.datastructure.Room;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * The TabDetails class extends Fragment, and defines the 
 * behavior of events that occur when the checkboxes such 
 * as "Notify Friends" are checked, or when users click on 
 * the buttons for Add to Favorites or Add to Calendar.
 */
public class TabDetails extends Fragment {
    private StudySpace o;
    private Preferences p;
    private View fav;
    private View unfav;
    private Date begin;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tabdetails, container, false);
        return view;
    }
     
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Intent i = getActivity().getIntent();
        o = (StudySpace) i.getSerializableExtra("STUDYSPACE");
        p = (Preferences) i.getSerializableExtra("PREFERENCES");
        if(p == null) {
            p = new Preferences();
        }

        TextView tt = (TextView) getView().findViewById(R.id.spacename);
        tt.setText(o.getBuildingName());

        TextView rt = (TextView) getView().findViewById(R.id.roomtype);
        rt.setText(o.getSpaceName());

        TextView rn = (TextView) getView().findViewById(R.id.roomnumbers);

        rn.setText(o.getRoomNames());

        TextView mo = (TextView) getView().findViewById(R.id.maxoccupancy);
        mo.setText("Maximum occupancy: "+o.getMaximumOccupancy());

        TextView pi = (TextView) getView().findViewById(R.id.privacy);
        ImageView private_icon = (ImageView) getView().findViewById(R.id.private_icon);
        if(o.getPrivacy().equals("S")){
            pi.setText("This study space is a common Space");
            if(private_icon!=null){
                Resources resource = getResources();
                int resID = resource.getIdentifier("icon_no_private", "drawable", getActivity().getPackageName() );
                private_icon.setImageResource(resID);
            }
        }else{
            pi.setText("Private space");
            if(private_icon!=null){
                Resources resource = getResources();
                int resID = resource.getIdentifier("icon_private", "drawable", getActivity().getPackageName() );
                private_icon.setImageResource(resID);
            }
        }

        TextView res = (TextView) getView().findViewById(R.id.reservetype);
        View calLayout = getView().findViewById(R.id.addCal);
        View resLayout = getView().findViewById(R.id.reserve);
        if(o.getReserveType().equals("N")){
            res.setText("This study space is non-reservable.");
            calLayout.setVisibility(View.VISIBLE);
            resLayout.setVisibility(View.GONE);
        }else{ 
            res.setText("This study space can be reserved.");
            calLayout.setVisibility(View.GONE);
            resLayout.setVisibility(View.VISIBLE);
        }
        TextView wb = (TextView) getView().findViewById(R.id.whiteboard);
        ImageView wb_icon = (ImageView) getView().findViewById(R.id.whiteboard_icon);
        if(o.hasWhiteboard()){
            if(wb_icon!=null){
                Resources resource = getResources();
                int resID = resource.getIdentifier("icon_whiteboard", "drawable", getActivity().getPackageName() );
                wb_icon.setImageResource(resID);
            }
            wb.setText("This study space has a whiteboard.");
        }else{
            wb.setText("This study space does not have a whiteboard.");
            if(wb_icon!=null){
                Resources resource = getResources();
                int resID = resource.getIdentifier("icon_no_whiteboard", "drawable", getActivity().getPackageName() );
                wb_icon.setImageResource(resID);
            }
        }

        TextView com = (TextView) getView().findViewById(R.id.computer);
        ImageView com_icon = (ImageView) getView().findViewById(R.id.computer_icon);
        if(o.hasComputer()){
            com.setText("This study space has a computer.");
            if(com_icon!=null){
                Resources resource = getResources();
                int resID = resource.getIdentifier("icon_computer", "drawable", getActivity().getPackageName() );
                com_icon.setImageResource(resID);
            }
        }else{
            com.setText("This study space does not have computers.");
            if(com_icon!=null){
                Resources resource = getResources();
                int resID = resource.getIdentifier("icon_no_computer", "drawable", getActivity().getPackageName() );
                com_icon.setImageResource(resID);
            }
        }

        TextView proj = (TextView) getView().findViewById(R.id.projector);
        ImageView proj_icon = (ImageView) getView().findViewById(R.id.projector_icon);
        if(o.has_big_screen()){
            proj.setText("This study space has a big screen.");
            if(proj_icon!=null){
                Resources resource = getResources();
                int resID = resource.getIdentifier("icon_projector", "drawable", getActivity().getPackageName() );
                proj_icon.setImageResource(resID);
            }
        }else{
            proj.setText("This study space does not have a big screen.");
            if(proj_icon!=null){
                Resources resource = getResources();
                int resID = resource.getIdentifier("icon_no_projector", "drawable", getActivity().getPackageName() );
                proj_icon.setImageResource(resID);
            }
        }

        fav = getView().findViewById(R.id.favorite);
        unfav = getView().findViewById(R.id.unfavorite);
        //favorites
        if(p.isFavorite(o.getBuildingName()+o.getSpaceName())){
            unfav.setVisibility(View.VISIBLE);
            fav.setVisibility(View.GONE);
        }else{ 
            unfav.setVisibility(View.GONE);
            fav.setVisibility(View.VISIBLE);
        }

        View an = (View) getView().findViewById(R.id.availablenow);
        if(an != null){
            boolean availableNow = false;
            for(Room r : o.getRooms()){
                try {
                    if(r.availableNow())
                        availableNow = true;
                } catch (Exception e) {
                    availableNow = false;   //Calendar crashes
                }
            }
            if(availableNow)
                an.setVisibility(View.VISIBLE);
            else
                an.setVisibility(View.GONE);
        }
    }

    public void onFavClick(View v){
        unfav.setVisibility(View.VISIBLE);
        fav.setVisibility(View.GONE);
    }
    public void onRemoveFavClick(View v){
        fav.setVisibility(View.VISIBLE);
        unfav.setVisibility(View.GONE);
    }

    public Intent getReserveIntent(){
        Intent k = null;
        if(o.getBuildingType().equals(StudySpace.WHARTON)){
            k = new Intent(Intent.ACTION_VIEW, Uri.parse("https://spike.wharton.upenn.edu/Calendar/gsr.cfm?"));}
        else if(o.getBuildingType().equals(StudySpace.ENGINEERING)){
            k = new Intent(Intent.ACTION_VIEW, Uri.parse("https://weblogin.pennkey.upenn.edu/login/?factors=UPENN.EDU&cosign-seas-www_userpages-1&https://www.seas.upenn.edu/about-seas/room-reservation/form.php"));
        }else if(o.getBuildingType().equals(StudySpace.LIBRARIES)){
            k = new Intent(Intent.ACTION_VIEW, Uri.parse("https://weblogin.library.upenn.edu/cgi-bin/login?authz=grabit&app=http://bookit.library.upenn.edu/cgi-bin/rooms/rooms"));
        }
        return k;
    }

    public Intent getCalIntent(View v){
        Calendar cal = Calendar.getInstance(Locale.US);              
        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setType("vnd.android.cursor.item/event");
        intent.putExtra("title", "PennStudySpaces Reservation confirmed. ");
        intent.putExtra("description", "Supported by PennStudySpaces");
        intent.putExtra("eventLocation", o.getBuildingName()+" - "+o.getRooms()[0].getRoomName());
        begin = cal.getTime();
        intent.putExtra("beginTime", cal.getTimeInMillis());
        intent.putExtra("endTime", cal.getTimeInMillis()+60*60*1000);
        return intent;
    }

    public Intent getTextIntent(View v){
        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        try {
            sendIntent.putExtra("sms_body", "PennStudySpaces Reservation confirmed. Details - "+
                    o.getBuildingName() + " - " + o.getRooms()[0].getRoomName() + "\nTime: " + begin);
            sendIntent.setType("vnd.android-dir/mms-sms");
        } catch (Exception e) {
            Toast.makeText(v.getContext(),
                    "SMS failed, please try again later!",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        return sendIntent;
    }
    
    public Intent getEmailIntent(){
        // http://stackoverflow.com/questions/2197741/how-to-send-email-from-my-android-application
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"@"});
        i.putExtra(Intent.EXTRA_SUBJECT, "Penn Study Space Reservation Invitation");
        i.putExtra(Intent.EXTRA_TEXT   , "Building Name: " +
                    o.getBuildingName() + "\nRoom Name: " + o.getRooms()[0].getRoomName() + "\nStart Time: " + begin);
        return i;
    }

    public void onReserveClick(View v){
        Intent k = getReserveIntent();
        if(k!=null) {
            startActivity(k);
        }
        
        final Intent calIntent = getCalIntent(v);
        final Intent sendIntent = getTextIntent(v);

        CheckBox text = (CheckBox) getView().findViewById(R.id.resTextCheckBox);
        if(text!=null && text.isChecked()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Pick a sharing option:");
            builder.setPositiveButton("Text", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    startActivity(sendIntent);
                    dialog.cancel();
                }
            });
            builder.setNeutralButton("Email", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    try {
                        startActivity(Intent.createChooser(getEmailIntent(), "Send mail..."));
                        dialog.cancel();
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(getActivity(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                    }       
                }
            });
            builder.setNegativeButton("Both", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    startActivity(sendIntent);
                    dialog.cancel();
                        
                    try {
                        startActivity(Intent.createChooser(getEmailIntent(), "Send mail..."));
                        dialog.cancel();
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(getActivity(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
        
        CheckBox calBox = (CheckBox) getView().findViewById(R.id.calCheckBox);
        if(calBox !=null && calBox.isChecked()) {
            startActivity(calIntent);
        }

    }
    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }
    
    public void onCalClick(View v){

        final Intent calIntent = getCalIntent(v);
        final Intent sendIntent = getTextIntent(v);
        startActivity(calIntent);
        
        CheckBox text = (CheckBox) getView().findViewById(R.id.calTextCheckBox);
        if(text!=null && text.isChecked()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Pick a sharing option:");
            builder.setPositiveButton("Text", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    startActivity(sendIntent);
                    dialog.cancel();
                }
            });
            builder.setNeutralButton("Email", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    try {
                        startActivity(Intent.createChooser(getEmailIntent(), "Send mail..."));
                        dialog.cancel();
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(getActivity(), "Please check that your email client is setup.", Toast.LENGTH_SHORT).show();
                    }       
                }
            });
            builder.setNegativeButton("Both", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    startActivity(sendIntent);
                    dialog.cancel();
                    
                    try {
                        startActivity(Intent.createChooser(getEmailIntent(), "Send mail..."));
                        dialog.cancel();
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(getActivity(), "Please check that your email client is setup.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }
}
