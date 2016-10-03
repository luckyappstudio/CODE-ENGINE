package in.fitkitapp.fitkit;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Profile extends AppCompatActivity {

    Tracker mTracker;
    ProgressDialog progressDialog;
    NetworkImageView logo,timeline,pro,ul;
    Button locate,oneday,sevenday,fifteenday;
    TextView oneday_price,seven_day_price,fifteen_day_price,address,rating,trainers,title;
    Geocoder geocoder;
    SharedPreferences sharedPreferences;
    String lat,lng,name,type,price,address2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sharedPreferences=getSharedPreferences(new Constants().shared,0);

        progressDialog=new ProgressDialog(Profile.this);

        progressDialog.setMessage("Loading...");

        progressDialog.setCancelable(false);
        progressDialog.create();
        progressDialog.show();
        geocoder = new Geocoder(this, Locale.getDefault());
        mTracker=AppController.getInstance().getDefaultTracker();

        mTracker.setScreenName("Image~" + "Profile");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        //initializee the views
        logo= (NetworkImageView) findViewById(R.id.logo);
        timeline= (NetworkImageView) findViewById(R.id.timeline);
        locate= (Button) findViewById(R.id.locatecenter);
        oneday= (Button) findViewById(R.id.one_day_pass);
        sevenday= (Button) findViewById(R.id.seven_day_pass);
        fifteenday= (Button) findViewById(R.id.buy_fifteen_day_pass);
        oneday_price= (TextView) findViewById(R.id.one_day_pass_price);
        seven_day_price= (TextView) findViewById(R.id.seven_day_pass_price);
        fifteen_day_price= (TextView) findViewById(R.id.fifteen_day_pass_price);
        address= (TextView) findViewById(R.id.address);
        pro= (NetworkImageView) findViewById(R.id.pro_image);
        ul= (NetworkImageView) findViewById(R.id.ul_image);
        rating= (TextView) findViewById(R.id.center_rating);
        trainers= (TextView) findViewById(R.id.center_trainers);
        title= (TextView) findViewById(R.id.centertitle);

        pro.setImageUrl("http://icons.iconarchive.com/icons/dtafalonso/android-lollipop/512/calendar-icon.png",AppController.getInstance().getImageLoader());
        ul.setImageUrl("http://www.teachinghealthglobal.com/wp-content/uploads/2014/12/icon4.jpg",AppController.getInstance().getImageLoader());
        //call the getProfile()
     final String id=sharedPreferences.getString("cid","n/a");
        Log.d(AppController.TAG,id.toString());
        StringRequest postRequest = new StringRequest(Request.Method.POST, new Constants().url+new Constants().profile,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d(AppController.TAG, response);
                        progressDialog.dismiss();


                        try {
                            JSONObject jsonObject=new JSONObject(response);
                            JSONArray jsonArray=jsonObject.getJSONArray("result");
                            Log.d(AppController.TAG,jsonArray.toString());
                            JSONObject data= (JSONObject) jsonArray.get(0);
                            Log.d(AppController.TAG, String.valueOf(data));
                            logo.setImageUrl(String.valueOf(data.get("url")),AppController.getInstance().getImageLoader());
                            timeline.setImageUrl(data.getString("profile"),AppController.getInstance().getImageLoader());
                            oneday_price.setText(data.getString("oneday"));
                            seven_day_price.setText(data.getString("tenday"));
                            fifteen_day_price.setText(data.getString("fifteenday"));
                            address.setText(data.getString("address"));
                            address2=data.getString("address");
                            rating.setText(data.getString("rating"));
                            trainers.setText(data.getString("trainers"));
                            lat=data.getString("lat");
                            lng=data.getString("lng");
                            name=data.getString("name");
                            title.setText(name);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d(AppController.TAG,error.toString());
                        progressDialog.dismiss();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("name", "Alif");
                params.put("domain", "http://itsalif.info");
                params.put("id",sharedPreferences.getString("cid","n/a"));
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(postRequest);


        CardView c= (CardView) findViewById(R.id.monthlypass);
        c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YoYo.with(Techniques.BounceIn).duration(300).playOn(v);
                startActivity(new Intent(Profile.this,Monthly.class));
            }
        });


        CardView c2= (CardView) findViewById(R.id.unlimitedpass);
        c2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YoYo.with(Techniques.BounceIn).duration(300).playOn(v);
                startActivity(new Intent(Profile.this,Unlimited.class));
            }
        });

        locate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start the maps activity
                String geoUri = "http://maps.google.com/maps?q=loc:" + lat + "," + lng + " (" + name + ")";
                Uri gmmIntentUri = Uri.parse(geoUri);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                }
            }
        });

        oneday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                type="oneday_pass";
                price= (String) oneday_price.getText();

                Intent summary=new Intent(Profile.this,Summary.class);
                summary.putExtra("type",type);
                summary.putExtra("price",price);
                summary.putExtra("address", address2);
                startActivity(summary);
            }
        });

        sevenday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                type="sevenday_pass";
                price= (String) seven_day_price.getText();

                Intent summary=new Intent(Profile.this,Summary.class);
                summary.putExtra("type",type);
                summary.putExtra("price",price);
                summary.putExtra("address", address2);
                startActivity(summary);

            }
        });

        fifteenday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                type="fifteenday_pass";
                price= (String) fifteen_day_price.getText();

                Intent summary=new Intent(Profile.this,Summary.class);
                summary.putExtra("type",type);
                summary.putExtra("price",price);
                summary.putExtra("address", address2);
                startActivity(summary);

            }
        });

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }








}
