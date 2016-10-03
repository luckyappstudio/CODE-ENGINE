package in.fitkitapp.fitkit;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.freshdesk.hotline.Hotline;
import com.freshdesk.hotline.HotlineConfig;
import com.freshdesk.hotline.HotlineNotificationConfig;
import com.freshdesk.hotline.HotlineUser;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.HitBuilders.EventBuilder;
import com.google.android.gms.analytics.Tracker;
import com.nineoldandroids.animation.Animator;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalTime;
import org.threeten.bp.Month;
import org.threeten.bp.format.TextStyle;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import solar.blaz.date.week.WeekDatePicker;

/**
 * Created by Akhilkamsala on 5/29/2016.
 */
public class Test extends AppCompatActivity{

    BottomBar mBottomBar;
    ListView listView;
    List<WorkoutClass> nearbyList=new ArrayList<WorkoutClass>();
    List<WorkoutClass> groupList=new ArrayList<WorkoutClass>();
    List<WorkoutClass> historyList=new ArrayList<WorkoutClass>();
    List<WorkoutClass> memberList=new ArrayList<WorkoutClass>();
    ProgressBar progressBar;
    SharedPreferences sharedPreferences;
    ProgressDialog progressDialog;
    NearbyAdapter nearbyAdapter;
    Tracker mTracker;

    CustomListAdapter customListAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        int count=0;
        sharedPreferences=getSharedPreferences(new Constants().shared,0);
        AppController appController= (AppController) getApplication();
        mTracker=appController.getDefaultTracker();

        AppviralityUI.showLaunchBar(this, AppviralityUI.GH.Word_of_Mouth);



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
        hlUser.setEmail(UserEmailFetcher.getEmail(Test.this));
        hlUser.setExternalId(UserEmailFetcher.getEmail(Test.this));
        String phno=sharedPreferences.getString("phno","N/A");
        hlUser.setPhone("+91", phno);

//Call updateUser so that the user information is synced with Hotline's servers
        Hotline.getInstance(getApplicationContext()).updateUser(hlUser);








        mTracker.setScreenName("Image~" + "App UI");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        progressDialog=new ProgressDialog(Test.this);
        progressDialog.setCancelable(false);
        Log.d(AppController.TAG,getApplication().toString()+"TestActivity");


        listView= (ListView) findViewById(R.id.list);

         mBottomBar = BottomBar.attach(this, savedInstanceState);
        mBottomBar.setItemsFromMenu(R.menu.bottom, new OnMenuTabClickListener() {
            @Override
            public void onMenuTabSelected(@IdRes int menuItemId) {

                switch (menuItemId)
                {
                    case R.id.bb_menu_nearby:
                        groupList.clear();
                        historyList.clear();
                        memberList.clear();
                        mTracker.send(new HitBuilders.EventBuilder()
                                .setCategory("Action")
                                .setAction("nearby")
                                .build());

                        progressDialog.setMessage("Fetching nearby workouts");
                        progressDialog.show();
                         nearbyAdapter=new NearbyAdapter(Test.this,nearbyList);
                        listView.setAdapter(nearbyAdapter);
                        StringRequest nearby=new StringRequest(Request.Method.POST, new Constants().url + new Constants().nearby, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d(AppController.TAG,response);

                                try {
                                    JSONObject nearby=new JSONObject(response);
                                    String status= (String) nearby.get("status");
                                    JSONArray jsonArray=nearby.getJSONArray("result");
                                    for (int i=0;i<jsonArray.length();i++)
                                    {
                                        Log.d(AppController.TAG,jsonArray.get(i).toString());
                                        JSONObject j= (JSONObject) jsonArray.get(i);
                                        Log.d(AppController.TAG, (String) j.get("url"));
                                        WorkoutClass w=new WorkoutClass(j.getString("url"),j.getString("name"));
                                        nearbyList.add(w);

                                    }
                                    nearbyAdapter.notifyDataSetChanged();
                                    progressDialog.dismiss();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(),"CHECK YOUR INTERNET CONNECTION",Toast.LENGTH_LONG).show();

                            }
                        })
                        {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String,String> params=new HashMap<String, String>();
                                params.put("type","nearby");
                                return params;
                            }
                        };
                        AppController.getInstance().addToRequestQueue(nearby);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                final WorkoutClass w=nearbyList.get(position);
                                YoYo.with(Techniques.BounceIn).duration(300).withListener(new Animator.AnimatorListener() {
                                    @Override
                                    public void onAnimationStart(Animator animator) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animator) {

                                        mTracker.send(new HitBuilders.EventBuilder()
                                                .setCategory("Action")
                                                .setAction(w.getName())
                                                .build());
                                        Log.d(AppController.TAG,w.getName());

                                        sharedPreferences.edit().putString("type",w.getName().toString()).commit();
                                        sharedPreferences.edit().putString("time", LocalTime.now().toString()).commit();
                                        sharedPreferences.edit().putString("date",LocalDate.now().toString()).commit();
                                        sharedPreferences.edit().putString("day",LocalDate.now().getDayOfWeek().toString()).commit();




                                        Log.d(AppController.TAG,w.getName()+w.getUrl()+sharedPreferences.getAll());


                                        startActivity(new Intent(Test.this,Results.class));
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

                        break;
                    case R.id.bb_menu_unlimited:
                        nearbyList.clear();
                        groupList.clear();
                        historyList.clear();
                        mTracker.send(new HitBuilders.EventBuilder()
                                .setCategory("Action")
                                .setAction("Fitkit Passes")
                                .build());

                        progressDialog.setMessage("Fetching available passes");
                        progressDialog.show();
                        nearbyAdapter=new NearbyAdapter(Test.this,memberList);
                        listView.setAdapter(nearbyAdapter);
                        StringRequest unlimited=new StringRequest(Request.Method.POST, new Constants().url + new Constants().passes, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d(AppController.TAG,response);

                                try {
                                    JSONObject nearby=new JSONObject(response);
                                    String status= (String) nearby.get("status");
                                    JSONArray jsonArray=nearby.getJSONArray("result");
                                    for (int i=0;i<jsonArray.length();i++)
                                    {
                                        Log.d(AppController.TAG,jsonArray.get(i).toString());
                                        JSONObject j= (JSONObject) jsonArray.get(i);
                                        Log.d(AppController.TAG, (String) j.get("url"));
                                        WorkoutClass w=new WorkoutClass(j.getString("url"),j.getString("name"));
                                        memberList.add(w);

                                    }
                                    nearbyAdapter.notifyDataSetChanged();
                                    progressDialog.dismiss();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(),"CHECK YOUR INTERNET CONNECTION",Toast.LENGTH_LONG).show();

                            }
                        })
                        {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String,String> params=new HashMap<String, String>();
                                params.put("type","group");
                                return params;
                            }
                        };
                        AppController.getInstance().addToRequestQueue(unlimited);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                final WorkoutClass w=memberList.get(position);

                                YoYo.with(Techniques.RubberBand).duration(300).withListener(new Animator.AnimatorListener() {
                                    @Override
                                    public void onAnimationStart(Animator animator) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animator) {



                                        if (w.getName().equalsIgnoreCase("monthly pass")) {
                                            sharedPreferences.edit().putString("cid","FITKIT PREMIUM").commit();
                                            startActivity(new Intent(Test.this, Monthly.class));
                                        }else
                                        {
                                            sharedPreferences.edit().putString("cid","FITKIT PREMIUM").commit();
                                            startActivity(new Intent(Test.this, Unlimited.class));

                                        }


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
                        break;

                    case R.id.bb_menu_history:
                        memberList.clear();
                        groupList.clear();
                        nearbyList.clear();
                        historyList.clear();
                        mTracker.send(new HitBuilders.EventBuilder()
                                .setCategory("Action")
                                .setAction("My passes")
                                .build());

                        progressDialog.setMessage("Fetching available passes");
                        progressDialog.show();
                        nearbyAdapter=new NearbyAdapter(Test.this,historyList);
                        listView.setAdapter(nearbyAdapter);
                        StringRequest mypasses=new StringRequest(Request.Method.POST, new Constants().url + new Constants().mypasses, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d(AppController.TAG,response);

                                try {
                                    JSONObject nearby=new JSONObject(response);
                                    String status= (String) nearby.get("status");
                                    JSONArray jsonArray=nearby.getJSONArray("result");
                                    for (int i=0;i<jsonArray.length();i++)
                                    {
                                        Log.d(AppController.TAG,jsonArray.get(i).toString());
                                        JSONObject j= (JSONObject) jsonArray.get(i);
                                        Log.d(AppController.TAG, (String) j.get("url"));
                                        WorkoutClass w=new WorkoutClass(j.getString("url"),j.getString("name"));
                                        historyList.add(w);

                                    }
                                    nearbyAdapter.notifyDataSetChanged();
                                    progressDialog.dismiss();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(),"CHECK YOUR INTERNET CONNECTION",Toast.LENGTH_LONG).show();
                            }
                        })
                        {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String,String> params=new HashMap<String, String>();
                                params.put("type","mypasses");
                                return params;
                            }
                        };
                        AppController.getInstance().addToRequestQueue(mypasses);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                final WorkoutClass w=historyList.get(position);
                                YoYo.with(Techniques.RubberBand).duration(300).withListener(new Animator.AnimatorListener() {
                                    @Override
                                    public void onAnimationStart(Animator animator) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animator) {

                                        if (w.getName().equalsIgnoreCase("my passes")) {

                                            startActivity(new Intent(Test.this, Myorders.class));
                                        }
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
                        break;

                    case R.id.bb_menu_about:
                        nearbyList.clear();
                        memberList.clear();
                        groupList.clear();
                        historyList.clear();
                        mTracker.send(new HitBuilders.EventBuilder()
                                .setCategory("Action")
                                .setAction("Chat with us")
                                .build());

                        progressDialog.setMessage("please wait");
                        progressDialog.show();
                        nearbyAdapter=new NearbyAdapter(Test.this,historyList);
                        listView.setAdapter(nearbyAdapter);
                        StringRequest about=new StringRequest(Request.Method.POST, new Constants().url + new Constants().about, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d(AppController.TAG,response);

                                try {
                                    JSONObject nearby=new JSONObject(response);
                                    String status= (String) nearby.get("status");
                                    JSONArray jsonArray=nearby.getJSONArray("result");
                                    for (int i=0;i<jsonArray.length();i++)
                                    {

                                        JSONObject j= (JSONObject) jsonArray.get(i);Log.d(AppController.TAG,jsonArray.get(i).toString());
                                        Log.d(AppController.TAG, (String) j.get("url"));
                                        WorkoutClass w=new WorkoutClass(j.getString("url"),j.getString("name"));
                                        historyList.add(w);

                                    }
                                    nearbyAdapter.notifyDataSetChanged();
                                    progressDialog.dismiss();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(),"CHECK YOUR INTERNET CONNECTION",Toast.LENGTH_LONG).show();
                            }
                        })
                        {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String,String> params=new HashMap<String, String>();
                                params.put("type","group");
                                return params;
                            }
                        };
                        AppController.getInstance().addToRequestQueue(about);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                                final WorkoutClass w=historyList.get(position);




                                YoYo.with(Techniques.RubberBand).duration(300).withListener(new Animator.AnimatorListener() {
                                    @Override
                                    public void onAnimationStart(Animator animator) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animator) {

                                        if (w.getName().equalsIgnoreCase("support")) {
                                            Hotline.showConversations(getApplicationContext());
                                        }
                                        else if ( w.getName().equalsIgnoreCase("policies"))
                                        {
                                            Hotline.showFAQs(getApplicationContext());
                                        }



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
                        break;

                }









            }

            @Override
            public void onMenuTabReSelected(@IdRes int menuItemId) {
                Toast.makeText(getApplicationContext(), getMessage(menuItemId, true), Toast.LENGTH_SHORT).show();
            }
        });



        mBottomBar.useDarkTheme();
        // Setting colors for different tabs when there's more than three of them.
        // You can set colors for tabs in three different ways as shown below.
        //mBottomBar.mapColorForTab(0, ContextCompat.getColor(this,R.color.colorAccent));
        //mBottomBar.mapColorForTab(1, Color.parseColor("#2196F3"));
        //mBottomBar.mapColorForTab(2, ContextCompat.getColor(this,R.color.colorAccent));
        //mBottomBar.mapColorForTab(3, Color.parseColor("#2196F3"));



        mBottomBar.setMinimumHeight(50);
    }
    @Override
    protected void onStart()
    {
        super.onStart();
    }

    private String getMessage(int menuItemId, boolean isReselection) {
        String message = "Content for ";

        switch (menuItemId) {
            case R.id.bb_menu_nearby:
                message += "near by";
                break;
            case R.id.bb_menu_unlimited:
                message += "Passes";
                break;

            case R.id.bb_menu_history:
                message += "my passes";
                break;
        }

        if (isReselection) {
            message += " WAS RESELECTED! YAY!";
        }

        return message;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Necessary to restore the BottomBar's state, otherwise we would
        // lose the current tab on orientation change.
        mBottomBar.onSaveInstanceState(outState);






    }

    @Override
    protected void onPause() {
        super.onPause();
        AppviralityUI.onStop();
    }


}
