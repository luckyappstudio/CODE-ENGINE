package in.fitkitapp.fitkit;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.nineoldandroids.animation.Animator;

public class Unlimited extends AppCompatActivity {

    CardView cardView;
    Button buy;
    TextView price;
    Tracker mTracker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.unlimited);

        cardView= (CardView) findViewById(R.id.card_unlimited);
        buy= (Button) findViewById(R.id.buy_unlimited);
        price= (TextView) findViewById(R.id.price_ul);

        mTracker=AppController.getInstance().getDefaultTracker();
        mTracker.setScreenName("Image~" + "Unlimited");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());


        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View finalV = v;
                YoYo.with(Techniques.FadeOutDown).duration(500).withListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator)
                    {


                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {

                        Intent summary=new Intent(Unlimited.this,Summary.class);
                        summary.putExtra("type","unlimited_pass");
                        summary.putExtra("price","3999");
                        summary.putExtra("address", "any where");
                        startActivity(summary);

                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                }).playOn(cardView);
            }
        });




         }

    @Override
    protected void onStart()
    {
        super.onStart();
        YoYo.with(Techniques.FadeInLeft).duration(1000).withListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {

                YoYo.with(Techniques.Flash).duration(1000).withListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        animator.start();

                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                }).playOn(buy);

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        }).playOn(cardView);

    }

}
