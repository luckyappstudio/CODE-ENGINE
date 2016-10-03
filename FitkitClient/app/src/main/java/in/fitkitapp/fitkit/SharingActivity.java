package in.fitkitapp.fitkit;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareButton;
import com.facebook.share.widget.ShareDialog;

public class SharingActivity extends AppCompatActivity {

    CallbackManager callbackManager;
    ShareDialog shareDialog;
    Button share,mypasses;
    ShareButton shareButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sharing);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        callbackManager = CallbackManager.Factory.create();
        mypasses= (Button) findViewById(R.id.mypasses);

        ShareButton shareButton = (ShareButton)findViewById(R.id.fb_share);
        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentTitle("Book Gym @ just rs 30 ")
                .setImageUrl(Uri.parse("https://lh6.googleusercontent.com/-FDLsB5FKKbU/AAAAAAAAAAI/AAAAAAAAAB4/67s-W-G_JPs/photo.jpg"))
                .setContentDescription(
                        "Workout any where in the city with one pass.")
                .setContentUrl(Uri.parse("http://www.fitkitapp.in"))
                .build();
        shareButton.setShareContent(content);


        mypasses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(SharingActivity.this,Myorders.class));
            }
        });








    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode,resultCode,data);
    }
}
