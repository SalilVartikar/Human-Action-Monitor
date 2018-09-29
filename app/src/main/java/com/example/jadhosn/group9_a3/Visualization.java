package com.example.jadhosn.group9_a3;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class Visualization extends AppCompatActivity {

    private static final boolean SET_AUTOMATIC_HIDE = true;
    private static final int AUTOMATIC_HIDE_DELAY = 3000; //MILLISECONDS
    private static final int ANIMATION_DELAY = 300;
    private final Handler hndler = new Handler();
    private View mContentView;
    private final Runnable runnableHide = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (SET_AUTOMATIC_HIDE) {
                delayedHide(AUTOMATIC_HIDE_DELAY);
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        WebView web = new WebView(getApplicationContext());
        setContentView(R.layout.activity_visualization);
        web =(WebView)findViewById(R.id.visualWeb);
        web.loadUrl("file:///android_asset/Visualization.html");
        web.getSettings().setBuiltInZoomControls(true);

        super.onCreate(savedInstanceState);
        WebSettings webSettingsObj = web.getSettings();
        webSettingsObj.setJavaScriptEnabled(true);
        webSettingsObj.setJavaScriptCanOpenWindowsAutomatically(true);
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            webSettingsObj.setAllowFileAccessFromFileURLs(true);
            webSettingsObj.setAllowUniversalAccessFromFileURLs(true);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;
        hndler.removeCallbacks(mShowPart2Runnable);
        hndler.postDelayed(runnableHide, ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        hndler.removeCallbacks(runnableHide);
        hndler.postDelayed(mShowPart2Runnable, ANIMATION_DELAY);
    }

    private void delayedHide(int delayMillis) {
        hndler.removeCallbacks(mHideRunnable);
        hndler.postDelayed(mHideRunnable, delayMillis);
    }



}
