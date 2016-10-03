package in.fitkitapp.fitkit;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.nineoldandroids.animation.Animator;


public class Monthly extends AppCompatActivity {

    Tracker mTracker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.monthly);

        mTracker=AppController.getInstance().getDefaultTracker();
        mTracker.setScreenName("Image~" + "Monthly");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());




    }


    @Override
    protected void onStart()
    {
        super.onStart();
        YoYo.with(Techniques.FadeInLeft).duration(100).withListener(new Animator.AnimatorListener() {
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




                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                }).playOn(findViewById(R.id.buy_monthly));

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        }).playOn(findViewById(R.id.buy_monthly));

        findViewById(R.id.buy_monthly).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent summary=new Intent(Monthly.this,Summary.class);
                summary.putExtra("type","monthly_pass");
                summary.putExtra("price","2500");
                summary.putExtra("address", "any where");
                startActivity(summary);
            }
        });
    }

}
