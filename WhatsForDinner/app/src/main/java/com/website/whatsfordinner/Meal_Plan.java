package com.website.whatsfordinner;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import java.util.ArrayList;


/**
 * Created by jayashreemadhanraj on 9/22/16.
 */
public class Meal_Plan extends Activity implements AdapterView.OnItemSelectedListener  {
    private static final String LOG_TAG = Meal_Plan.class.getSimpleName();
    ArrayAdapter<String> adapter;
    DataHelper helper = new DataHelper(this);
    ArrayList<String> items;
    int check=0;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);


        for (int i = 1; i < 22; i++) {
            Spinner spinner = (Spinner) findViewById(getResources().getIdentifier("spinner" + i, "id", getPackageName()));
            outState.putInt("yourSpinner", spinner.getSelectedItemPosition());
        }
        // do this for each or your Spinner
        // You might consider using Bundle.putStringArray() instead
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meals_planner_layout);

        items = new ArrayList<>();
        items = helper.setMeal_Plan_onLoad();
        for (int i = 1; i < 22; i++) {
            TextView tv = (TextView) findViewById(getResources().getIdentifier("textView" + i, "id", getPackageName()));
            if(i<items.size()){
                tv.setText(items.get(i-1));
            }

        }

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.clear();
        adapter.add("");
        adapter.addAll(helper.getFromAvailableMeals());
        adapter.notifyDataSetChanged();

        for (int i = 1; i < 22; i++) {

            Spinner spinner = (Spinner) findViewById(getResources().getIdentifier("spinner" + i, "id", getPackageName()));


            spinner.setOnItemSelectedListener(this);
            spinner.setAdapter(adapter);
            if (savedInstanceState != null) {
                //spinner.setSelection(savedInstanceState.getInt("yourSpinner", 0));
                // do this for each of your text views
            }
        }



    }

    public void saveMealPlan(View view){
        helper.clearMeals_Plan();
        for (int i = 1; i < 22; i++) {
            TextView tv = (TextView) findViewById(getResources().getIdentifier("textView" + i, "id", getPackageName()));
            helper.saveMealPlanForFuture("textView"+i, tv.getText().toString());
        }
    }

    /**
     * <p>Callback method to be invoked when an item in this view has been
     * selected. This callback is invoked only when the newly selected
     * position is different from the previously selected position or if
     * there was no selected item.</p>
     * <p/>
     * Impelmenters can call getItemAtPosition(position) if they need to access the
     * data associated with the selected item.
     *
     * @param parent   The AdapterView where the selection happened
     * @param view     The view within the AdapterView that was clicked
     * @param position The position of the view in the adapter
     * @param id       The row id of the item that is selected
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String temp2;
        ArrayList<String> data;
        String item = adapter.getItem(position);
        check=check+1;
        if(position == 0) {
            return;
        }
        if(check>21) {

            Spinner spinner = (Spinner) parent;
            if (getResources().getResourceName(spinner.getId()).contains("spinner")) {
                String temp = getResources().getResourceName(spinner.getId());
                temp2 = String.valueOf(temp.charAt(temp.length() - 1));
                if (String.valueOf(temp.charAt(temp.length() -2)).equals("1")){
                    switch (String.valueOf(temp.charAt(temp.length() - 1))){
                        case "0":
                            temp2 = "10";
                            break;
                        case "1":
                            temp2 = "11";
                            break;
                        case "2":
                            temp2 = "12";
                            break;
                        case "3":
                            temp2 = "13";
                            break;
                        case "4":
                            temp2 = "14";
                            break;
                        case "5":
                            temp2 = "15";
                            break;
                        case "6":
                            temp2 = "16";
                            break;
                        case "7":
                            temp2 = "17";
                            break;
                        case "8":
                            temp2 = "18";
                            break;
                        case "9":
                            temp2 = "19";
                            break;
                    }
                }
                else if(String.valueOf(temp.charAt(temp.length() -2)).equals("2")){
                    switch (String.valueOf(temp.charAt(temp.length() - 1))){
                        case "0":
                            temp2 = "20";
                            break;
                        case "1":
                            temp2 = "21";
                            break;
                    }
                }


                Log.v(LOG_TAG, "temp2:" + temp2);
                TextView txt = (TextView) findViewById(getResources().getIdentifier("textView" + temp2, "id", getPackageName()));
                txt.setText(item);
                Log.v(LOG_TAG, "adapter.getItem(position) at position" + position + ":" + item);
                //Adding list to Grocery

                    data = helper.getRecipeIngredientsArrayList(item);

                    for (int i = 0; i < data.size(); i++) {
                        helper.addToGrocery(item, data.get(i));
                    }

                Log.v(LOG_TAG, "Before Deleting");
                helper.checkMeals();
                helper.deleteFromAvailableMeals(item);
                adapter.clear();
                adapter.add("");
                Log.v(LOG_TAG, "After Deleting");
                adapter.addAll(helper.getFromAvailableMeals());

                helper.checkMeals();
                adapter.notifyDataSetChanged();
            }

        }
    }

    /**
     * Callback method to be invoked when the selection disappears from this
     * view. The selection can disappear for instance when touch is activated
     * or when the adapter becomes empty.
     *
     * @param parent The AdapterView that now contains no selected item.
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


}
