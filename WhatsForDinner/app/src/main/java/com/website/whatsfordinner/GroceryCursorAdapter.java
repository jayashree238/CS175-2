package com.website.whatsfordinner;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import 	android.widget.CursorAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by jayashreemadhanraj on 9/15/16.
 */
public class GroceryCursorAdapter extends CursorAdapter implements View.OnClickListener{
    View.OnTouchListener mTouchListener;
    private static final String LOG_TAG = GroceryCursorAdapter.class.getSimpleName();

    public GroceryCursorAdapter(Context context, Cursor cursor, View.OnTouchListener listener) {
        super(context, cursor, 0);
        mTouchListener = listener;
    }

    // The newView method is used to inflate a new view and return it,
    // you don't bind any data to the view at this point.

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View root =  LayoutInflater.from(context).inflate(R.layout.grocery_item_textview_layout, parent, false);
        final TextView groceryText = (TextView)root.findViewById(R.id.grocery_item_textView);
        root.setOnTouchListener(mTouchListener);
        Button minus = (Button)root.findViewById(R.id.minusButton);
        minus.setVisibility(View.INVISIBLE);
        Button plus = (Button)root.findViewById(R.id.plusButton);
        plus.setVisibility(View.INVISIBLE);

        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = groceryText.getText().toString();
                Log.v(LOG_TAG ,"plus groceryText is " + groceryText.getText());
                String text2 = text.replaceAll("[^0-9]+", " ").trim();

                int d = (Integer.parseInt(text2.trim()))+1;

                Log.v(LOG_TAG, "t: " + text2);
                Log.v(LOG_TAG, "d: " + d);
                text = text.replace(text2, Integer.toString(d));
                Log.v(LOG_TAG, "text: " + text);
                groceryText.setText(text);
                if(d > 0){

                        groceryText.setPaintFlags(groceryText.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));

                }
            }
        });
        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = groceryText.getText().toString();
                Log.v(LOG_TAG ,"plus groceryText is " + groceryText.getText());
                String text2 = text.replaceAll("[^0-9]+", " ").trim();

                int d = (Integer.parseInt(text2.trim()))-1;

                Log.v(LOG_TAG, "t: " + text2);
                Log.v(LOG_TAG, "d: " + d);
                text = text.replace(text2, Integer.toString(d));
                Log.v(LOG_TAG, "text: " + text);
                if (d >= 0){

                    groceryText.setText(text);
                    if (d == 0){
                        groceryText.setPaintFlags(groceryText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    }
                    else{
                        groceryText.setPaintFlags(groceryText.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));

                    }
                }



            }
        });

        return root;
    }

    // The bindView method is used to bind all data to a given view
    // such as setting the text on a TextView.
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find fields to populate in inflated template
        TextView grocery_item = (TextView) view.findViewById(R.id.grocery_item_textView);

        // Extract properties from cursor
        String item = cursor.getString(cursor.getColumnIndexOrThrow("ITEM_NAME"));
        int measure = cursor.getInt(cursor.getColumnIndexOrThrow("ITEM_MEASURE"));
        String measure_unit = cursor.getString(cursor.getColumnIndexOrThrow("ITEM_MEASURE_TYPE"));
        // Populate fields with extracted properties
        if(!item.equals("0")) {
            String final_grocery = " (" + String.valueOf(measure) + " " + measure_unit + ")" + item;
            grocery_item.setText(final_grocery);
        }
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {

        switch(v.getId()) {
            case R.id.minusButton:
                Log.d(LOG_TAG, "Write code to minus quantity");

                break;
            case R.id.plusButton:
                Log.d(LOG_TAG,"Write code to pls quantity");
                break;
        }
    }
}