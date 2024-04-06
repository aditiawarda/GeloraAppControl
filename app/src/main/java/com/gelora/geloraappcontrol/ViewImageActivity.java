package com.gelora.geloraappcontrol;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class ViewImageActivity extends AppCompatActivity {

    private ScaleGestureDetector scaleGestureDetector;
    private float mScaleFactor = 1.0f;
    private ImageView mainImage;
    LinearLayout loadingPart;
    TextView titlePageTV, backBTN;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);
        mainImage = findViewById(R.id.main_image);
        backBTN = findViewById(R.id.back_btn);
        titlePageTV = findViewById(R.id.title_page);
        loadingPart = findViewById(R.id.loading_part);

        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());

        String url = getIntent().getExtras().getString("url");

        titlePageTV.setText("FOTO PROFIL");

        if(url.equals("https://hrisgelora.co.id/upload/avatar/null")){
            Picasso.get().load("https://hrisgelora.co.id/upload/avatar/default_profile.jpg").networkPolicy(NetworkPolicy.NO_CACHE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .into(mainImage);
        } else {
            Picasso.get().load(url).networkPolicy(NetworkPolicy.NO_CACHE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .into(mainImage);
        }

        new Handler().postDelayed(() -> loadingPart.setVisibility(View.GONE), 1000);

        backBTN.setOnClickListener(v -> onBackPressed());

    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        scaleGestureDetector.onTouchEvent(motionEvent);
        return true;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            mScaleFactor *= scaleGestureDetector.getScaleFactor();
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 10.0f));
            mainImage.setScaleX(mScaleFactor);
            mainImage.setScaleY(mScaleFactor);
            return true;
        }
    }

}