package in.fitkitapp.fitkit;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.freshdesk.hotline.Hotline;
import com.freshdesk.hotline.HotlineConfig;
import com.freshdesk.hotline.HotlineNotificationConfig;
import com.freshdesk.hotline.HotlineUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Myorders extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    ListView orderslist;
    List<Order> myorders=new ArrayList<Order>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myorders);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sharedPreferences=getSharedPreferences(new Constants().shared,0);
        orderslist= (ListView) findViewById(R.id.orderslist);
        final OrderAdapter orderAdapter=new OrderAdapter(this,myorders);
        orderslist.setAdapter(orderAdapter);
        final ProgressDialog progressDialog=new ProgressDialog(Myorders.this);
        progressDialog.setMessage("searcing your orders");
        progressDialog.setCancelable(false);
        progressDialog.create();
        progressDialog.show();

        final StringRequest myorders1 = new StringRequest(Request.Method.POST, new Constants().url + new Constants().myorders, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progressDialog.dismiss();
                Log.d(AppController.TAG,response);

                try {
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray jsonArray=jsonObject.getJSONArray("result");
                    for (int i=0;i<jsonArray.length();i++)
                    {
                        JSONObject jsonObject1=jsonArray.getJSONObject(i);
                        myorders.add(new Order(jsonObject1.getString("id"),jsonObject1.getString("expirydate"),jsonObject1.getString("location"),jsonObject1.getString("remainginsessions"),jsonObject1.getString("TXN_AMOUNT")));
                    }
                    orderAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                progressDialog.dismiss();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<String,String>();
                params.put("phno",sharedPreferences.getString("phno","n/a"));
                params.put("email",sharedPreferences.getString("email","n/a"));
                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(myorders1);



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Hotline.showConversations(getApplicationContext());

            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);





        HotlineConfig hlConfig=new HotlineConfig(" 28a3ece5-54f6-4641-9046-e3cba839d3de"," 8d927c79-9392-4f14-aaa6-f5be6a727a57");
        hlConfig.setVoiceMessagingEnabled(true);
        hlConfig.setCameraCaptureEnabled(true);
        hlConfig.setPictureMessagingEnabled(true);
        Hotline.getInstance(getApplicationContext()).init(hlConfig);
        HotlineNotificationConfig notificationConfig = new HotlineNotificationConfig()
                .setNotificationSoundEnabled(true)
                .setSmallIcon(R.drawable.notify)
                .setLargeIcon(R.drawable.favo)
                .launchDeepLinkTargetOnNotificationClick(true)
                .launchActivityOnFinish(MainActivity.class.getName())
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        Hotline.getInstance(getApplicationContext()).setNotificationConfig(notificationConfig);

        //Get the user object for the current installation
        HotlineUser hlUser=Hotline.getInstance(getApplicationContext()).getUser();

        hlUser.setName("N/A");
        hlUser.setEmail(sharedPreferences.getString("email","N/A"));
        hlUser.setExternalId(sharedPreferences.getString("email","N/A"));
        String phno=sharedPreferences.getString("phno","N/A");
        hlUser.setPhone("+91", phno);

//Call updateUser so that the user information is synced with Hotline's servers
        Hotline.getInstance(getApplicationContext()).updateUser(hlUser);











    }

}
