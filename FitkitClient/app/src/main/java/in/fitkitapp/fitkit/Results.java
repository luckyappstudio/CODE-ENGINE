package in.fitkitapp.fitkit;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.nineoldandroids.animation.Animator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.location.LocationManager.*;

public class Results extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    final int PERMISSION_REQUEST_LOCATION = 101;
    RelativeLayout mLayout;
    ListView results;
    CustomListAdapter customListAdapter;
    List<FitnessCenter> resultsList = new ArrayList<FitnessCenter>();
    ProgressDialog progressDialog;
    LocationManager locationManager;
    String provider;
    GoogleApiClient mGoogleApiClient;
    Location mLocation;
    Tracker mTracker;
    SharedPreferences sharedPreferences;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sharedPreferences = getSharedPreferences(new Constants().shared, 0);
        mLayout = (RelativeLayout) findViewById(R.id.relative);
        results = (ListView) findViewById(R.id.results);
        customListAdapter = new CustomListAdapter(Results.this, resultsList);
        results.setAdapter(customListAdapter);
        mTracker = AppController.getInstance().getDefaultTracker();
        mTracker.setScreenName("Image~" + "Results");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        progressDialog = new ProgressDialog(Results.this);
        resultsList.clear();
        showSearchPreview();
        if (mGoogleApiClient == null) {
            // ATTENTION: This "addApi(AppIndex.API)"was auto-generated to implement the App Indexing API.
            // See https://g.co/AppIndexing/AndroidStudio for more information.
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API).addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(Results.this).addApi(AppIndex.API).build();
            mGoogleApiClient.connect();

            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            // **************************
            builder.setAlwaysShow(true); // this is the key ingredient
            // **************************

            PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi
                    .checkLocationSettings(mGoogleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    final LocationSettingsStates state = result
                            .getLocationSettingsStates();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            // All location settings are satisfied. The client can
                            // initialize location
                            // requests here.
                            Log.d(AppController.TAG, mLocation + "SS");
                            resultsList.clear();
                            showSearchPreview();
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be
                            // fixed by showing the user
                            // a dialog.
                            try {
                                // Show the dialog by calling
                                // startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(Results.this, 1000);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have
                            // no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            });
        }


    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Results Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://in.fitkitapp.fitkit/http/host/path")
        );
        AppIndex.AppIndexApi.end(mGoogleApiClient, viewAction);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        // BEGIN_INCLUDE(onRequestPermissionsResult)
        if (requestCode == PERMISSION_REQUEST_LOCATION) {
            // Request for camera permission.
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted. Start camera preview Activity.
                Snackbar.make(mLayout, "Location permission was granted. Starting preview.",
                        Snackbar.LENGTH_SHORT)
                        .show();
                Log.d(AppController.TAG,"YESS YESS");
                resultsList.clear();
                showSearchPreview();

            } else {
                // Permission request was denied.
                Snackbar.make(mLayout, "Location permission request was denied.",
                        Snackbar.LENGTH_SHORT)
                        .show();
                resultsList.clear();
                    showSearchPreview();

            }
        }
        // END_INCLUDE(onRequestPermissionsResult)
    }



    private void showSearchPreview() {
        // BEGIN_INCLUDE(startCamera)
        // Check if the Camera permission has been granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // Permission is already available, start camera preview
            Snackbar.make(mLayout,
                    "location permission is available. Starting search.",
                    Snackbar.LENGTH_SHORT).show();

            progressDialog.setMessage("Finding near by fitness spots ");
            progressDialog.setCancelable(false);
            progressDialog.show();



            Log.d(AppController.TAG,mLocation+"SEARCH LOCATION");


            if (mLocation==null)
            {

                Log.d(AppController.TAG,"Cant make a volley request");

            }
            else
            {

                Log.d(AppController.TAG,"Canmak a volley request");
                final double lat=mLocation.getLatitude();
                final double longi=mLocation.getLongitude();

                Log.d(AppController.TAG,"Lat:"+lat+"-- ---------"+"Lat:"+longi+"");


                StringRequest fetch=new StringRequest(Request.Method.POST, new Constants().url + new Constants().fetch, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(AppController.TAG,response);
                        try {
                            JSONObject merchant_reslts=new JSONObject(response);
                            if (merchant_reslts.get("status").equals("success")) {
                                JSONArray resultslist = merchant_reslts.getJSONArray("result");
                                Log.d(AppController.TAG, resultslist.toString() + "");
                                for (int i = 0; i < resultslist.length(); i++) {
                                    JSONObject j = resultslist.getJSONObject(i);
                                    Log.d(AppController.TAG, j.toString());
                                    FitnessCenter f = new FitnessCenter(j.getString("name"), j.getString("area"), j.getString("price"), j.getString("distance").substring(0, 3), j.getString("rating"), j.getString("url"), j.getString("id"));
                                    resultsList.add(f);

                                }
                            }
                            else
                            {
                                AlertDialog.Builder a=new AlertDialog.Builder(Results.this);
                                a.setCancelable(false);
                                a.setMessage("We are not available in your area.");
                                a.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        Results.this.finish();
                                    }
                                });
                            }
                               // resultsList.add(new FitnessCenter("Gold's gym","Bangalore,KA","80","1","4.5","https://irp-cdn.multiscreensite.com/308d3e89/dms3rep/multi/mobile/golds%20gym%20logo-728x715.png","110"));
                                //resultsList.add(new FitnessCenter("Snap Fitness","Bangalore,KA","70","3","4.3","http://shortnorth.org/wp-content/uploads/2015/03/snap-fitness-logo.jpg","110"));
                                //resultsList.add(new FitnessCenter("Gym Network","Bangalore,KA","50","5","4.0","http://cdn.designbeep.com/wp-content/uploads/2012/07/24.gym-and-fitness-logos.png","110"));
                                //resultsList.add(new FitnessCenter("Strong hold gym","Bangalore,KA","30","7","3.0","http://webneel.com/sites/default/files/images/manual/logo-all/22-strong-gym-logo-design.gif","110"));
                                customListAdapter.notifyDataSetChanged();
                                progressDialog.dismiss();


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.d(AppController.TAG,error.toString());
                        progressDialog.dismiss();

                    }
                })
                {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String,String> params=new HashMap<String,String>();
                        params.put("lat", String.valueOf(lat));
                        params.put("lng", String.valueOf(longi));
                        params.put("type","type");
                        return params;
                    }
                };

                AppController.getInstance().addToRequestQueue(fetch);


            }






            results.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @SuppressWarnings("MissingPermission")
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                    Log.d(AppController.TAG,resultsList.get(position).getId());


                    sharedPreferences.edit().putString("cid",resultsList.get(position).getId().toString()).commit();
                    YoYo.with(Techniques.RubberBand).duration(300).withListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animator) {


                        }

                        @Override
                        public void onAnimationEnd(Animator animator) {


                            Log.d(AppController.TAG,sharedPreferences.getString("cid","n/a"));
                            startActivity(new Intent(Results.this,Profile.class).putExtra("id",resultsList.get(position).getId()));
                        }

                        @Override
                        public void onAnimationCancel(Animator animator) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animator) {

                        }
                    }).playOn(view);


                }
            });






        } else {
            // Permission is missing and must be requested.
            requestLocationPermission();
        }
        // END_INCLUDE(startCamera)
    }






    private void requestLocationPermission() {
        // Permission has not been granted and must be requested.
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // Display a SnackBar with a button to request the missing permission.

            AlertDialog.Builder alert=new AlertDialog.Builder(Results.this);
            alert.setMessage("Location permission is needed").setPositiveButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(Results.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            PERMISSION_REQUEST_LOCATION);

                }
            }).setCancelable(false).create().show();



        } else {
            Snackbar.make(mLayout,
                    "Permission is not available. Requesting Location permission.",
                    Snackbar.LENGTH_SHORT).show();
            // Request the permission. The result will be received in onRequestPermissionResult().
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_LOCATION);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.

                // Do something with the contact here (bigger example below)

                Log.d(AppController.TAG,resultCode+"OK");
                resultsList.clear();
                showSearchPreview();
            }
            else
            {

                Log.d(AppController.TAG,resultCode+"NOTOK");

                LocationRequest locationRequest = LocationRequest.create();
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                locationRequest.setInterval(30 * 1000);
                locationRequest.setFastestInterval(5 * 1000);
                LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                        .addLocationRequest(locationRequest);

                // **************************
                builder.setAlwaysShow(true); // this is the key ingredient
                // **************************

                PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi
                        .checkLocationSettings(mGoogleApiClient, builder.build());
                result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                    @Override
                    public void onResult(LocationSettingsResult result) {
                        final Status status = result.getStatus();
                        final LocationSettingsStates state = result
                                .getLocationSettingsStates();
                        switch (status.getStatusCode()) {
                            case LocationSettingsStatusCodes.SUCCESS:
                                // All location settings are satisfied. The client can
                                // initialize location
                                // requests here.
                                Log.d(AppController.TAG,mLocation+"SS");
                                resultsList.clear();
                                showSearchPreview();
                                break;
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                // Location settings are not satisfied. But could be
                                // fixed by showing the user
                                // a dialog.
                                try {
                                    // Show the dialog by calling
                                    // startResolutionForResult(),
                                    // and check the result in onActivityResult().
                                    status.startResolutionForResult(Results.this, 1000);
                                } catch (IntentSender.SendIntentException e) {
                                    // Ignore the error.
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                // Location settings are not satisfied. However, we have
                                // no way to fix the
                                // settings so we won't show the dialog.
                                break;
                        }
                    }
                });
            }

        }

        else
        {
            Log.d(AppController.TAG,requestCode+"");
        }

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {



        mLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        Log.d(AppController.TAG,mLocation+"");


    }

    @Override
    public void onConnectionSuspended(int i) {

        Log.e(AppController.TAG,"Connection Suspended");

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Log.e(AppController.TAG,connectionResult.toString());


    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mGoogleApiClient.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Results Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://in.fitkitapp.fitkit/http/host/path")
        );
        AppIndex.AppIndexApi.start(mGoogleApiClient, viewAction);
    }
}
