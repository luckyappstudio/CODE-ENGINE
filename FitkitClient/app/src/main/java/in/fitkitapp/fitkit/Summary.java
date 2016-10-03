package in.fitkitapp.fitkit;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.analytics.ecommerce.Product;
import com.google.android.gms.analytics.ecommerce.ProductAction;
import com.paytm.pgsdk.PaytmMerchant;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import org.json.JSONException;
import org.json.JSONObject;
import org.threeten.bp.LocalDate;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Akhilkamsala on 6/24/2016.
 */
public class Summary extends AppCompatActivity
{
    TextView passtype,purchasedate,expirydate,location,price;
    Button checkout;
    SharedPreferences sharedPreferences;
    CallbackManager callbackManager;
    ShareDialog shareDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.summary);
        sharedPreferences=getSharedPreferences(new Constants().shared,0);
        passtype= (TextView) findViewById(R.id.passtype);
        purchasedate= (TextView) findViewById(R.id.purchasedate);
        expirydate= (TextView) findViewById(R.id.expirydate);
        location= (TextView) findViewById(R.id.adress_merchant);
        price= (TextView) findViewById(R.id.passprice);
        checkout= (Button) findViewById(R.id.pay);
        callbackManager = CallbackManager.Factory.create();
        Intent intent=getIntent();
        final String pass_type=intent.getStringExtra("type");
        final String pass_price=intent.getStringExtra("price");
        final String pass_location=intent.getStringExtra("address");
        final String purchase_date= LocalDate.now().toString();
        int days=0;
        int sesions=0;
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);


        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "in.fitkitapp.fitkit",
                    PackageManager.GET_SIGNATURES);
            for (android.content.pm.Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

        if (pass_type.equalsIgnoreCase("oneday_pass"))
        {


            days=10;
            sesions=1;

        }
        else if (pass_type.equalsIgnoreCase("sevenday_pass"))
        {
            days=15;
            sesions=7;

        }
        else if (pass_type.equalsIgnoreCase("fifteenday_pass"))
        {
            days=30;
            sesions=15;

        }
        else if (pass_type.equalsIgnoreCase("monthly_pass"))
        {
            days=30;
            sesions=30;

        }
        else if (pass_type.equalsIgnoreCase("unlimited_pass"))
        {
            days=30;
            sesions=50;

        }

        final String expiry_date=LocalDate.now().plusDays(days).toString();

        final int finalSesions = sesions;


        passtype.setText(pass_type);
        purchasedate.setText(purchase_date);
        expirydate.setText(expiry_date);
        price.setText(pass_price);
        location.setText(pass_location);


        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final ProgressDialog progressDialog=new ProgressDialog(Summary.this);
                progressDialog.setMessage("Loading...");
                progressDialog.setCancelable(false);
                progressDialog.create();
                progressDialog.show();
                StringRequest order=new StringRequest(Request.Method.POST, new Constants().url + new Constants().order, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        Log.d(AppController.TAG,response);

                        String id=null;
                        JSONObject jsonObject= null;
                        try {
                            jsonObject = new JSONObject(response);
                            id= String.valueOf(jsonObject.get("id"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        Product product =  new Product()
                                .setId(id)
                                .setName(pass_type)
                                .setCategory(pass_location)
                                .setBrand(pass_type)
                                .setVariant(pass_type)
                                .setPrice(Double.parseDouble(pass_price))
                                .setCouponCode("APPARELSALE")
                                .setQuantity(1);

                        double pass= Double.parseDouble(pass_price);
                        double commission=(20*pass)/100;
                        double servicetax=(14.5*commission)/100;
                        ProductAction productAction = new ProductAction(ProductAction.ACTION_PURCHASE)
                                .setTransactionId(id)
                                .setTransactionAffiliation("Fitkit")
                                .setTransactionRevenue(commission)
                                .setTransactionTax(servicetax);
                        HitBuilders.ScreenViewBuilder builder = new HitBuilders.ScreenViewBuilder()
                                .addProduct(product)
                                .setProductAction(productAction);

                        Tracker t = AppController.getInstance().getDefaultTracker();
                        t.setScreenName("Booking Confirmed");
                        t.setScreenName("transaction");
                        t.set("&cu", "INR");  // Set tracker currency to INR.
                        t.send(builder.build());


                        PaytmPGService Service =null;
                        Service=PaytmPGService.getProductionService();

                        Map<String,String> params=new HashMap<String, String>();
                        params.put("REQUEST_TYPE","DEFAULT");
                        params.put("ORDER_ID",id);
                        params.put("MID","luckyy00264452806409");
                        params.put("CUST_ID",sharedPreferences.getString("phno","n/a"));
                        params.put("CHANNEL_ID","WAP");
                        params.put("INDUSTRY_TYPE_ID","Retail110");
                        params.put("WEBSITE","Luckyappwap");
                        params.put("TXN_AMOUNT",price.getText().toString());
                        params.put("THEME","merchant");
                        params.put("MOBILE_NO",sharedPreferences.getString("phno","n/a"));
                        params.put("EMAIL",sharedPreferences.getString("email","n/a"));


                        PaytmOrder paytmOrder=new PaytmOrder(params);

                        PaytmMerchant paytmMerchant= new PaytmMerchant("http://www.fitkitapp.in/paytmchecksum/generateChecksum.php","http://fitkitapp.in/paytmchecksum/verifyChecksum.php");
                        Service.initialize(paytmOrder,paytmMerchant,null);
                        final String finalId = id;
                        Service.startPaymentTransaction(Summary.this, true, true, new PaytmPaymentTransactionCallback() {
                            @Override
                            public void onTransactionSuccess(final Bundle bundle) {

                                Log.d(AppController.TAG,bundle.toString());
                                Toast.makeText(getApplicationContext(),"PAYMENT SUCCESS",Toast.LENGTH_SHORT).show();
                                Toast.makeText(getApplicationContext(),"Share this on your facebook and inspire your friends",Toast.LENGTH_SHORT).show();
                                if (bundle.get("STATUS").equals("TXN_SUCCESS"));
                                {
                                    Log.d(AppController.TAG,"updating order details");
                                    progressDialog.setMessage("Upadting order details...");
                                    progressDialog.create();
                                    progressDialog.show();

                                    StringRequest updateOrder=new StringRequest(Request.Method.POST, new Constants().url + new Constants().updateorder, new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            progressDialog.dismiss();
                                            Log.d(AppController.TAG,response);
                                            try {
                                                JSONObject jsonObject1=new JSONObject(response);
                                                if (jsonObject1.get("status").equals("success"))
                                                {
                                                    // this part is optional
                                                    /**shareDialog = new ShareDialog(Summary.this);
                                                    shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
                                                        @Override
                                                        public void onSuccess(Sharer.Result result) {

                                                        }

                                                        @Override
                                                        public void onCancel() {

                                                        }

                                                        @Override
                                                        public void onError(FacebookException error) {

                                                        }
                                                    });

                                                    if (ShareDialog.canShow(ShareLinkContent.class)) {
                                                        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                                                                .setContentTitle("Just now i booked my workout using fitkit app it is just awsome . Download app and get Fit")
                                                                .setShareHashtag(new ShareHashtag.Builder().setHashtag("#fitkit").build())
                                                                .setImageUrl(new Uri.Builder().build().parse("http://www.fitkitapp.in/logo1.png"))
                                                                .setContentDescription(
                                                                        "You can buy 1 day pass with 10 days vlidity ,1 week pass with 15 days validy and  15 days pass with 30 day validity and ofcourse monthly pass.")
                                                                .build();

                                                        shareDialog.show(linkContent);
                                                    }
                                                    **/

                                                    startActivity(new Intent(Summary.this,SharingActivity.class));
                                                }
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
                                            Map<String,String> params=new HashMap<String, String>();
                                            params.put("order_id", finalId);
                                            params.put("transaction_id", String.valueOf(bundle.get("TXNID")));
                                            params.put("transaction_status", String.valueOf(bundle.get("STATUS")));
                                            params.put("transaction_amount", String.valueOf(bundle.get("TXNAMOUNT")));
                                            params.put("transaction_date", String.valueOf(bundle.get("TXNDATE")));
                                            return params;
                                        }
                                    };
                                    AppController.getInstance().addToRequestQueue(updateOrder);




                                }

                            }

                            @Override
                            public void onTransactionFailure(String s, Bundle bundle) {
                                Log.d(AppController.TAG,s);
                                Toast.makeText(getApplicationContext(),"PAYMENT FAILED TRY AGAIN",Toast.LENGTH_SHORT).show();
                                Toast.makeText(getApplicationContext(),bundle.toString(),Toast.LENGTH_SHORT).show();







                                StringRequest updateOrder=new StringRequest(Request.Method.POST, new Constants().url + new Constants().updateorder, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        progressDialog.dismiss();
                                        Log.d(AppController.TAG,response);
                                        try {
                                            JSONObject jsonObject1=new JSONObject(response);
                                            if (jsonObject1.get("status").equals("success"))
                                            {
                                                startActivity(new Intent(Summary.this,SharingActivity.class));
                                            }
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
                                        Map<String,String> params=new HashMap<String, String>();
                                        params.put("order_id","FAILED");
                                        params.put("transaction_id","FAILED");
                                        params.put("transaction_status", "FAILED");
                                        params.put("transaction_amount", "FAILED");
                                        params.put("transaction_date", "FAILED");
                                        return params;
                                    }
                                };
                                AppController.getInstance().addToRequestQueue(updateOrder);


                            }

                            @Override
                            public void networkNotAvailable() {

                            }

                            @Override
                            public void clientAuthenticationFailed(String s) {

                            }

                            @Override
                            public void someUIErrorOccurred(String s) {

                            }

                            @Override
                            public void onErrorLoadingWebPage(int i, String s, String s1) {

                            }

                            @Override
                            public void onBackPressedCancelTransaction() {
                                Log.d(AppController.TAG,"BACK_PRESS");
                                Toast.makeText(getApplicationContext(),"PAYMENT FAILED TRY AGAIN",Toast.LENGTH_SHORT).show();
                                StringRequest updateOrder=new StringRequest(Request.Method.POST, new Constants().url + new Constants().updateorder, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        progressDialog.dismiss();
                                        Log.d(AppController.TAG,response);
                                        try {
                                            JSONObject jsonObject1=new JSONObject(response);
                                            if (jsonObject1.get("status").equals("success"))
                                            {
                                                //startActivity(new Intent(Summary.this,Myorders.class));
                                                startActivity(new Intent(Summary.this,SharingActivity.class));
                                            }
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
                                        Map<String,String> params=new HashMap<String, String>();
                                        params.put("order_id", finalId);
                                        params.put("transaction_id","FAILED");
                                        params.put("transaction_status", "FAILED");
                                        params.put("transaction_amount", "FAIELD");
                                        params.put("transaction_date", "FAILED");
                                        return params;
                                    }
                                };
                                AppController.getInstance().addToRequestQueue(updateOrder);


                            }
                        });





                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Log.d(AppController.TAG,error.toString());
                    }
                })
                {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String,String> params=new HashMap<String, String>();
                        params.put("type",pass_type);
                        params.put("purchasedate",purchase_date);
                        params.put("expirydate",expiry_date);
                        params.put("location",pass_location);
                        params.put("centerid",sharedPreferences.getString("cid","n/a"));
                        params.put("mobile_no",sharedPreferences.getString("phno","n/a"));
                        params.put("email",sharedPreferences.getString("email","n/a"));
                        params.put("sessions", String.valueOf(finalSesions));
                        return params;
                    }
                };

                AppController.getInstance().addToRequestQueue(order);

            }
        });






    }


    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


}
