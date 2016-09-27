package com.website.whatsfordinner;

import android.app.ListActivity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by jayashreemadhanraj on 9/10/16.
 */
public class Grocery extends ListActivity {


    ListView grocery_list;
    ArrayList<String> items;

    GroceryCursorAdapter groceryAdapter;
    private boolean mSwiping = false; // detects if user is swiping on ACTION_UP
    private boolean mItemPressed = false; // Detects if user is currently holding down a view

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.grocery_layout);
        items = new ArrayList<>();

        DataHelper handler = new DataHelper(this);
        SQLiteDatabase db = handler.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT ID as _id, ITEM_NAME, SUM(COUNT) as ITEM_MEASURE, ITEM_MEASURE_TYPE FROM grocery_list GROUP BY ITEM_NAME", null);
        for(int i=0; i<c.getCount(); i++){
            c.moveToPosition(i);
            items.add(c.getString(1));

        }

        // Find ListView to populate
        grocery_list = (ListView) findViewById(android.R.id.list);
        // Setup cursor adapter using cursor from last step
        groceryAdapter = new GroceryCursorAdapter(this, c, mTouchListener);
        // Attach cursor adapter to the ListView
        grocery_list.setAdapter(groceryAdapter);

    }


    private View.OnTouchListener mTouchListener = new View.OnTouchListener()
    {
        float mDownX;
        private int mSwipeSlop = -1;
        boolean swiped;

        @Override
        public boolean onTouch(final View v, MotionEvent event) {
            if (mSwipeSlop < 0)
            {
                mSwipeSlop = ViewConfiguration.get(Grocery.this).getScaledTouchSlop();
            }
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (mItemPressed)
                    {
                        // Doesn't allow swiping two items at same time
                        return false;
                    }
                    mItemPressed = true;
                    mDownX = event.getX();
                    swiped = false;
                    break;
                case MotionEvent.ACTION_CANCEL:
                    v.setTranslationX(0);
                    mItemPressed = false;
                    break;
                case MotionEvent.ACTION_MOVE:
                {
                    float x = event.getX() + v.getTranslationX();
                    float deltaX = x - mDownX;
                    float deltaXAbs = Math.abs(deltaX);

                    if (!mSwiping)
                    {
                        if (deltaXAbs > mSwipeSlop) // tells if user is actually swiping or just touching in sloppy manner
                        {
                            mSwiping = true;
                            grocery_list.requestDisallowInterceptTouchEvent(true);
                        }
                    }
                    if (mSwiping && !swiped) // Need to make sure the user is both swiping and has not already completed a swipe action (hence mSwiping and swiped)
                    {
                        v.setTranslationX((x - mDownX)); // moves the view as long as the user is swiping and has not already swiped

                        if (deltaX > v.getWidth() / 3) // swipe to right
                        {
                            mDownX = x;
                            swiped = true;
                            mSwiping = false;
                            mItemPressed = false;

                            v.findViewById(R.id.minusButton).setVisibility(View.VISIBLE);
                            v.findViewById(R.id.plusButton).setVisibility(View.VISIBLE);
                            v.animate().setDuration(200).translationX(v.getWidth()/3);

                            return true;
                        }
                        else if (deltaX < -1 * (v.getWidth() / 3)) // swipe to left
                        {
                            mDownX = x;
                            swiped = true;
                            mSwiping = false;
                            mItemPressed = false;
                            v.findViewById(R.id.minusButton).setVisibility(View.INVISIBLE);
                            v.findViewById(R.id.plusButton).setVisibility(View.INVISIBLE);
                            v.animate().setDuration(200).translationX(-v.getWidth()/3);


                            return true;
                        }
                    }

                }
                break;
                case MotionEvent.ACTION_UP:
                {
                    if (mSwiping)
                    {
                        v.animate().setDuration(300).translationX(0).withEndAction(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                mSwiping = false;
                                mItemPressed = false;
                                grocery_list.setEnabled(true);
                            }
                        });
                    }
                    else // user was not swiping; registers as a click
                    {
                        mItemPressed = false;
                        grocery_list.setEnabled(true);

                        int i = grocery_list.getPositionForView(v);

                        Toast.makeText(Grocery.this, items.get(i), Toast.LENGTH_LONG).show();

                        return false;
                    }
                }
                default:
                    return false;
            }
            return true;
        }
    };
}
