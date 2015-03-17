package com.androidtraining.nanofaroque.ofaro.googleapiclienttesting;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class MainActivity extends FragmentActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener{

    TextView tvLocation;
    TextView tvLocationUpdate;
    GoogleApiClient mGoogleApiClient;
    Location locationUp;
    LocationRequest mLocationRequest=new LocationRequest();
    boolean requestingLocationUpdate=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvLocation = (TextView) findViewById(R.id.tvLoc);//last location
        tvLocationUpdate=(TextView)findViewById(R.id.tvLocUpdate);//update location

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(500);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();//be connected with GoogleApiClient
    }

    @Override
    public void onConnected(Bundle bundle) {
        updateUI();
        if(requestingLocationUpdate){
            startLocationUpdate();//trying to get the update location
        }
    }

    private void startLocationUpdate() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest,this);
    }

    private void updateUI() {
        tvLocation.setText("Last Known Location:"+String.valueOf(getLocation().getLatitude())+String.valueOf(getLocation().getLongitude()));
    }

    private Location getLocation() {
        Location lastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        return lastKnownLocation;
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("result:", "connection has been suspended");

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Log.d("result:","not connected with GoogleApiClient");
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdate();//when activity is on pause, need to stop the location update since we
        //do not need that any more
    }

    private void stopLocationUpdate() {

        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,this);//stopping the update location
    }


    //no need to use googleplayservices
    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();//disconnect the GoogleApiClient from the google play services
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mGoogleApiClient.isConnected()){
        startLocationUpdate();// since we need to get the update location continuously like gps
            //when activity is in foreground(resume), then we have to request to get update location
            //here also
        }
    }

    //to get the location change
    @Override
    public void onLocationChanged(Location location) {

        locationUp=location;//this location is the update location,
        // came from the requestLocationUpdate() method's interface
        updateView();//put your value in the text or whatever you want to do
        }

    private void updateView() {
        tvLocationUpdate.setText("Update Location:"+String.valueOf(locationUp.getLatitude())+String.valueOf(locationUp.getLongitude()));
    }
}