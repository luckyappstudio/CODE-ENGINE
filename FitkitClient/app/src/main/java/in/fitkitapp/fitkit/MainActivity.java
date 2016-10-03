package in.fitkitapp.fitkit;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.appvirality.AppviralityUI;
import com.appvirality.CampaignHandler;
import com.appvirality.android.AppviralityAPI;
import com.appvirality.android.CampaignDetails;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.nineoldandroids.animation.Animator;
import com.paytm.pgsdk.PaytmOrder;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.mixpanel.android.mpmetrics.MixpanelAPI.*;

public class MainActivity extends AppCompatActivity {

    TextInputEditText phno;
    TextInputLayout textInputLayout;
    ProgressBar progressBar;
    Button signup;
    LinearLayout mlay;
    AppController appController;
    Tracker mTracker;
    SharedPreferences sharedPreferences;
    TextView tv;
    MixpanelAPI mixpanel;

    int PERMISSION_REQUEST_CONTACTS=100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        mixpanel = getInstance(MainActivity.this, "cdc14c3a2e1cc4d7ae13580e0c64928d");
        appController= AppController.getInstance();
        mTracker=appController.getDefaultTracker();
        mTracker.enableAdvertisingIdCollection(true);
        mlay= (LinearLayout) findViewById(R.id.mlay);
        AppviralityAPI.init(getApplicationContext());

        AppviralityAPI.UserDetails.setInstance(getApplicationContext())
                .setUserEmail(UserEmailFetcher.getEmail(MainActivity.this))
                .setUserName(UserEmailFetcher.getEmail(MainActivity.this))
                .setUseridInStore(UserEmailFetcher.getEmail(MainActivity.this))
                .setProfileImage(UserEmailFetcher.getEmail(MainActivity.this))
                .setCountry("Country")
                .setState("State")
                .setCity("City")
                //.isExistingUser(false)
                //.setPushRegID("User-Push-Registration-ID")
                .Update(new AppviralityAPI.UpdateUserDetailsListner() {
                    @Override
                    public void onSuccess(boolean isSuccess) {
                        Log.i("AppViralitySDK","Update UserDetails Status : " + isSuccess);
                    }
                });

        sharedPreferences=getSharedPreferences(new Constants().shared,0);
        String login=sharedPreferences.getString("login","CAN'T FETCH");
        phno= (TextInputEditText) findViewById(R.id.phno);
        signup= (Button) findViewById(R.id.signup);
        progressBar= (ProgressBar) findViewById(R.id.progress);
        textInputLayout= (TextInputLayout) findViewById(R.id.textlayout);
        tv= (TextView) findViewById(R.id.message);




        showSearchPreview();
        if (login.equals("true"))
        {


            textInputLayout.setVisibility(View.INVISIBLE);
            signup.setVisibility(View.INVISIBLE);
            phno.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            tv.setText("Loading....");
            startActivity(new Intent(MainActivity.this,Test.class));
            mTracker.send(new HitBuilders.EventBuilder()
                    .setCategory("Action")
                    .setAction("App opened")
                    .build());


        }
        else
        {
            Log.d(AppController.TAG,login);
        }

    }

    @Override
    protected void onStart()
    {
        super.onStart();
        phno= (TextInputEditText) findViewById(R.id.phno);
        signup= (Button) findViewById(R.id.signup);
        progressBar= (ProgressBar) findViewById(R.id.progress);
        textInputLayout= (TextInputLayout) findViewById(R.id.textlayout);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (phno.getText().length()!=10)
                {
                    YoYo.with(Techniques.FadeIn).duration(500).withListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animator) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animator) {


                            mTracker.send(new HitBuilders.EventBuilder()
                                    .setCategory("Action")
                                    .setAction("failed login")
                                    .build());



                        }

                        @Override
                        public void onAnimationCancel(Animator animator) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animator) {

                        }
                    }).playOn(findViewById(R.id.signup));
                    Snackbar.make(v,"Please enter 10 digit phno",Snackbar.LENGTH_INDEFINITE).show();


                }
                else
                {


                    phno.setVisibility(View.INVISIBLE);
                    textInputLayout.setVisibility(View.INVISIBLE);
                    signup.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.VISIBLE);







                    final String mobile=phno.getText().toString();
                    String url=new Constants().url+new Constants().signup;
                    Log.d(AppController.TAG,url);
                    StringRequest stringRequest=new StringRequest(Request.Method.POST,url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            try {
                                JSONObject login=new JSONObject(response);
                                if(login.get("status").equals("success"))
                                {

                                    mTracker.send(new HitBuilders.EventBuilder()
                                            .setCategory("Action")
                                            .setAction("User details fetched")
                                            .build());

                                    sharedPreferences=getSharedPreferences(new Constants().shared,0);
                                    sharedPreferences.edit().putString("phno",phno.getText().toString()).commit();
                                    sharedPreferences.edit().putString("email",UserEmailFetcher.getEmail(MainActivity.this)).commit();
                                    sharedPreferences.edit().putString("login","true").commit();
                                    Log.d(AppController.TAG,sharedPreferences.getAll()+"");
                                    startActivity(new Intent(MainActivity.this,Test.class));



                                }
                                else
                                {
                                    AlertDialog.Builder a=new AlertDialog.Builder(MainActivity.this);

                                    a.setMessage("Unable to signup");
                                    a.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            Bundle b=new Bundle();
                                            MainActivity.this.onCreate(b);

                                        }
                                    });
                                    a.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            MainActivity.this.finish();
                                        }
                                    });
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }



                            // now that response is availablle work on it.
                            Log.d(AppController.TAG,response);

                            // startActivity(new Intent(MainActivity.this,Test.class));




                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            Log.d(AppController.TAG,error.toString());


                        }
                    })
                    {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String,String> params=new HashMap<String, String>();
                            params.put("phno",mobile);
                            params.put("email",UserEmailFetcher.getEmail(MainActivity.this));
                            params.put("type","consumer");
                            return params;
                        }
                    };
                    AppController.getInstance().addToRequestQueue(stringRequest);



                }

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }




    private void requestLocationPermission() {
        // Permission has not been granted and must be requested.
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // Display a SnackBar with a button to request the missing permission.

            AlertDialog.Builder alert=new AlertDialog.Builder(MainActivity.this);
            alert.setMessage("Contacts permission is needed").setPositiveButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.GET_ACCOUNTS},
                            PERMISSION_REQUEST_CONTACTS);

                }
            }).setCancelable(false).create().show();



        } else {
            Snackbar.make(mlay,
                    "Permission is not available. Requesting Location permission.",
                    Snackbar.LENGTH_SHORT).show();
            // Request the permission. The result will be received in onRequestPermissionResult().
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.GET_ACCOUNTS},
                    PERMISSION_REQUEST_CONTACTS);
        }
    }





    private void showSearchPreview() {
        // BEGIN_INCLUDE(startCamera)
        // Check if the Camera permission has been granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS)
                == PackageManager.PERMISSION_GRANTED) {
            // Permission is already available, start camera preview
            Snackbar.make(mlay,
                    "contacts permission is available.",
                    Snackbar.LENGTH_SHORT).show();


            ///
            ///









        } else {
            // Permission is missing and must be requested.
            requestLocationPermission();
        }
        // END_INCLUDE(startCamera)
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        Log.i(AppController.TAG, "Setting screen name: " +"Main_Screen");
        mTracker.setScreenName("Image~" + "Main_Screen");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

    }

    @Override
    protected void onDestroy() {
        mixpanel.flush();
        super.onDestroy();
    }
    @Override
    protected void onStop() {
        super.onStop();
        AppviralityAPI.onStop();
    }







    //Snippet for checkPlayServices method below
    private boolean checkPlayServices(Activity activityContext) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(activityContext);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(activityContext, resultCode, 121).show();
            } else {
                Log.i(AppController.TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
}
