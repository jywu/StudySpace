package edu.upenn.cis573;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.LinearLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import edu.upenn.cis573.datastructure.Building;
import edu.upenn.cis573.datastructure.Preferences;
import edu.upenn.cis573.util.ConnectionDetector;

/**
 * The CustomBuildingMap class maps a list of StudySpace locations 
 * using the Google Maps API and an ArrayList of StudySpace objects 
 * that it is passed from the StudySpaceListActivity class.
 */
public class CustomBuildingMap extends Activity {
	
    LinearLayout linearLayout;
    //MapView mapView;
    GoogleMap mapView;
    MapController mc;
    GeoPoint p;
    GeoPoint q;
    GeoPoint avg;
    Drawable drawableRed, drawableBlue;
    Preferences pref;
    private ArrayList<StudySpace> olist;
    
    Boolean isInternetPresent = false;

    // Connection detector class
    ConnectionDetector cd;
    Hashtable<Marker, Building> markertable = new Hashtable<Marker, Building>();

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = super.getIntent();
        pref = (Preferences) i.getSerializableExtra("PREFERENCES");
        olist = (ArrayList<StudySpace>) i.getSerializableExtra("STUDYSPACELIST");

        cd = new ConnectionDetector(getApplicationContext());

        // get Internet status
        isInternetPresent = cd.isConnectingToInternet();

        // check for Internet status
        if (isInternetPresent) {
            setContentView(R.layout.buildingmapview);
            //mapView = (MapView) findViewById(R.id.mapview);
            mapView = ((MapFragment) getFragmentManager().findFragmentById(
                    R.id.mapview)).getMap();
            //mapView.setBuiltInZoomControls(true);

            drawableBlue = this.getResources().getDrawable(R.drawable.pushpin_blue);
            drawableRed = this.getResources().getDrawable(R.drawable.pushpin_red);
            
            double clat = 0.0;
            double clon = 0.0;
            int cnt = 0;

            LocationManager locationManager = (LocationManager) this
                    .getSystemService(Context.LOCATION_SERVICE);

            Criteria _criteria = new Criteria();

            String _bestProvider = locationManager.getBestProvider(_criteria, true);
            Location location = locationManager.getLastKnownLocation(_bestProvider);

            LocationListener loc_listener = new LocationListener() {
                public void onLocationChanged(Location l) {
                }

                public void onProviderEnabled(String p) {
                }

                public void onProviderDisabled(String p) {
                }

                public void onStatusChanged(String p, int status, Bundle extras) {
                }
            };
            locationManager.requestLocationUpdates(_bestProvider, 0, 0,
                    loc_listener);
            location = locationManager.getLastKnownLocation(_bestProvider);

            if (location != null) {
                double gpsLat = location.getLatitude();
                double gpsLong = location.getLongitude();

                q = new GeoPoint((int) (gpsLat), (int) (gpsLong));
                clat += gpsLat;
                clon += gpsLong;
                ++cnt;

                mapView.addMarker(new MarkerOptions()
                .position(new LatLng(gpsLat, gpsLong))
                .title("Current Position")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

            }

//            PinOverlay pinsRed = new PinOverlay(drawableRed);
            Set<String> buildings = new HashSet<String>();
            for (StudySpace o: olist)
                buildings.add(o.getBuildingName());
            for (String buildingName : buildings) {
                ArrayList<StudySpace> rooms = new ArrayList<StudySpace>();
                for (StudySpace o: olist) {
                    if (o.getBuildingName().equals(buildingName)) {
                        rooms.add(o);
                    }
                }
                double longitude = rooms.get(0).getSpaceLongitude();
                double latitude = rooms.get(0).getSpaceLatitude();
                
                p = new GeoPoint((int) (latitude), (int) (longitude));
                clat += latitude;
                clon += longitude;
                ++cnt;
//                OverlayItem overlayitem = new OverlayItem(p, "", "");
//                pinsRed.addOverlay(overlayitem, new Building(rooms));
                
                
                Marker marker = mapView.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .title(buildingName));
                
                markertable.put(marker, new Building(rooms));
                
                mapView.setOnMarkerClickListener(new OnMarkerClickListener() {

					@Override
					public boolean onMarkerClick(Marker marker) {
						Building building = markertable.get(marker);
						if (building == null) return false;
						showBuildingDialog(building);
						return true;
					}
                	
                });
//                
    
//              avgLong += longitude;
//              avgLat += latitude;
            }
            //mapView.getOverlays().add(pinsRed);

            clat /= cnt;
            clon /= cnt;
            
            
          //  GeoPoint center = new GeoPoint(clat, clon);
            //mc = mapView.getController();
            //mc.animateTo(center);
            //mc.setZoom(17);
            
            CameraPosition position = new CameraPosition.Builder()
            .target(new LatLng(clat,clon))
            .zoom(17).build();

             mapView.animateCamera(CameraUpdateFactory.newCameraPosition(position));
            
        } else {
            // Internet connection is not present
            // Ask user to connect to Internet
            cd.showAlertDialog(CustomBuildingMap.this, "No Internet Connection",
                    "You don't have internet connection.", false);
        }

    }

    protected boolean isRouteDisplayed() {
        return false;
    }
    
    
    protected boolean showBuildingDialog(final Building building) {        
        if (building == null)
            return true;
//        Geocoder geoCoder = new Geocoder(
//                getBaseContext(), Locale.getDefault());
        try {
//            List<Address> addresses = geoCoder.getFromLocation(
//                    building.getSpaceLatitude(), building.getSpaceLongitude(), 1); 
//
//            String add = "";
//            if (addresses.size() > 0) 
//            {
//                for (int i=0; i<addresses.get(0).getMaxAddressLineIndex(); 
//                        i++)
//                    add += addresses.get(0).getAddressLine(i) + "\n";
//            }

            AlertDialog.Builder builder = new AlertDialog.Builder(CustomBuildingMap.this);
            builder.setTitle("Building Information");
            builder.setMessage(building.getBuildingName() + ": " + building.getRoomCount() + 
                    " rooms \n");
            builder.setPositiveButton("Back to Map", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
            builder.setNegativeButton("View Rooms", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                    Intent i = new Intent();
                    i.putExtra("STUDYSPACELIST", building.getRooms());
                    setResult(RESULT_OK, i);
                    //ends this activity
                    finish();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();

        }
        catch (Exception e) {                
            e.printStackTrace();
        }   
        return true;
    }


    /**
     * The PinOverlay class is a private inner class that actually adds 
     * the pins on the map with listeners to display an alert dialog with 
     * more information about the study space when a user clicks on it.
     */
//    public class PinOverlay extends ItemizedOverlay<OverlayItem>{
//
//        private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
//        private ArrayList<Building> mBuildings = new ArrayList<Building>();
//        
//        public PinOverlay(Drawable defaultMarker) {
//            super(boundCenterBottom(defaultMarker));
//        }
//
//        public void addOverlay(OverlayItem overlay, Building building) {
//            mOverlays.add(overlay);
//            mBuildings.add(building);
//            populate();
//        }
//
//        @Override
//        protected OverlayItem createItem(int i) {
//            return mOverlays.get(i);
//        }
//
//
//        @Override
//        public int size() {
//            return mOverlays.size();
//        }
//
//        @Override
//        protected boolean onTap(int index) {
//            final Building building = mBuildings.get(index);
//            
//            if (building == null)
//                return true;
//            Geocoder geoCoder = new Geocoder(
//                    getBaseContext(), Locale.getDefault());
//            try {
//                List<Address> addresses = geoCoder.getFromLocation(
//                        building.getSpaceLatitude(), building.getSpaceLongitude(), 1); 
//
//                String add = "";
//                if (addresses.size() > 0) 
//                {
//                    for (int i=0; i<addresses.get(0).getMaxAddressLineIndex(); 
//                            i++)
//                        add += addresses.get(0).getAddressLine(i) + "\n";
//                }
//
//                AlertDialog.Builder builder = new AlertDialog.Builder(CustomBuildingMap.this);
//                builder.setTitle("Building Information");
//                builder.setMessage(building.getBuildingName() + ": " + building.getRoomCount() + 
//                        " rooms \n" + add + "Distance: " + Math.round(building.getDistance()) + " m");
//                builder.setPositiveButton("Back to Map", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        dialog.cancel();
//                    }
//                });
//                builder.setNegativeButton("View Rooms", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        dialog.cancel();
//                        Intent i = new Intent();
//                        i.putExtra("STUDYSPACELIST", building.getRooms());
//                        setResult(RESULT_OK, i);
//                        //ends this activity
//                        finish();
//                    }
//                });
//                AlertDialog alert = builder.create();
//                alert.show();
//
//            }
//            catch (IOException e) {                
//                e.printStackTrace();
//            }   
//            return true;
//        }
//
//    }
//
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Intent i = new Intent();
            ArrayList<StudySpace> nlist = new ArrayList<StudySpace>();
            i.putExtra("STUDYSPACELIST", nlist);
            setResult(RESULT_OK, i);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
