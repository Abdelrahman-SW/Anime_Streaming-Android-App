package com.anyone.smardy.motaj.badtrew.Utilites;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.anyone.smardy.motaj.badtrew.activities.ExoplayerActivity;

public class onSwipeTouchListener implements View.OnTouchListener {
    Context context;
    private final Float MIN_DISTANCE = 4f ;
    private boolean isHorizontal = false ;
    private boolean isVertical   = false ;
    float baseX , baseY ;

    public onSwipeTouchListener(Context context) {
        this.context = context;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float diffX , diffY ;
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            // the user first touch the screen
            Log.i("ab_do", "ACTION_DOWN (x,y) = " + event.getX() + "," + event.getY());
            isHorizontal = false ;
            isVertical = false ;
            baseX = event.getX() ;
            baseY = event.getY() ;
            // hide the controller view if visible and vice versa
            ((ExoplayerActivity) context).changeExoControllerVisibility();
            return true ;
        }
        else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            Log.i("ab_do", "ACTION_MOVE (x,y) = " + event.getX() + "," + event.getY());
            diffX = event.getX() - baseX ;
            diffY = event.getY() - baseY ;
            if (isHorizontal) {
                Log.i("ab_do" , "Swipe horizontal");
                if (diffX < 0 && Math.abs(diffX) > MIN_DISTANCE) {
                    //swipe left
                    onSwipeLeft();
                }
                else {
                    //swipe right
                    if (Math.abs(diffX) > MIN_DISTANCE)
                    onSwipeRight();
                }
                // show the seek bar if not showing
                ((ExoplayerActivity) context).ShowView();
            }
            else if (isVertical) {
                Log.i("ab_do" , "Swipe vertical");
                if (diffY < 0 && Math.abs(diffY) > MIN_DISTANCE) {
                    // swipe up
                    onSwipeTop();
                }
                else {
                    //swipe right
                    if(Math.abs(diffY) > MIN_DISTANCE)
                    onSwipeBottom();
                }
            }
            else {
               if (Math.abs(diffX) > Math.abs(diffY) && Math.abs(diffX) > MIN_DISTANCE) {
                   isHorizontal = true ;
               }
               else if (Math.abs(diffX) < Math.abs(diffY) && Math.abs(diffY) > MIN_DISTANCE) {
                   isVertical = true ;
               }
            }
            baseX = event.getX() ;
            baseY = event.getY()  ;
        }
        else if (event.getAction() == MotionEvent.ACTION_UP) {
            // the user release his finger from the screen
            Log.i("ab_do", "ACTION_UP (x,y) = " + event.getX() + "," + event.getY());
            isVertical = false ;
            isHorizontal = false;
            try {
                if (ExoplayerActivity.dialog != null && ExoplayerActivity.dialog.isShowing()) {
                        ExoplayerActivity.dialog.dismiss();
                }
            } catch (Exception exception) {
                Log.i("ab_do" , exception.getMessage());
            }

        }
        return true;
    }

    public void onSwipeRight() {
    }

    public void onSwipeLeft() {
    }

    public void onSwipeTop() {
    }

    public void onSwipeBottom() {
    }
}

