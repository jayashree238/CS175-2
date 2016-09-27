package com.website.whatsfordinner;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by jayashreemadhanraj on 9/10/16.
 */
public class NewDish extends Activity implements View.OnFocusChangeListener, AdapterView.OnItemSelectedListener {
    private static final String LOG_TAG = NewDish.class.getSimpleName();
    //Dew Dish Data Entry Screen
    Button addDishImage;
    ImageView inputPhotoId;
    Drawable noDishImage;
    Uri defualtImage = Uri.parse("android.resource://com.website.whatsfordinner/drawable/none.png");
    String myfileName;
    Boolean newEntry = true;
    EditText recipe_Name;
    ArrayAdapter<String> adapter;
    DataHelper dataHelper;
    DataHelper dataHelper_recipes;
    DataHelper dataHelper_saveRecipe;
    DataHelper dataHelper_groceryList;
    ArrayAdapter<String> recipeAdapter;
    Button save_Dish;
    int new_pos = 0;
    int count = 0;
    EditText cookingDirections;
    String final_recipeName;
    String final_cookingDirections;
    byte[] bytearray = null;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newdish_layout);
        int position=0;

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            position = extras.getInt("position");
            new_pos = position + 1;
           // Log.v(LOG_TAG, "Position:" + new_pos);
        }

        dataHelper = new DataHelper(getApplicationContext());
        dataHelper_recipes = new DataHelper(getApplicationContext());
        dataHelper_saveRecipe = new DataHelper(getApplicationContext());
        dataHelper_groceryList = new DataHelper(getApplicationContext());

        //Request focus
        recipe_Name = (EditText) findViewById(R.id.recipeName);
        if(new_pos == 0) {
            recipe_Name.requestFocus();
        }

        recipe_Name.setOnFocusChangeListener(this);

        //Saving Recipe button
        save_Dish = (Button) findViewById(R.id.saveDish);
        save_Dish.setOnClickListener(saveDish);

        cookingDirections = (EditText) findViewById(R.id.cookingDirections);


        //Reference Input UI componenets from the Layout
        addDishImage = (Button) findViewById(R.id.newdish_addimage);
        inputPhotoId = (ImageView) findViewById(R.id.newdish_image);
        noDishImage = inputPhotoId.getDrawable();
        inputPhotoId.setImageResource(0);
//
        //Listener Events for Photo Selection
        addDishImage.setOnClickListener(getPhotoFromGallery);

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);

        recipeAdapter = new ArrayAdapter<String>(this, R.layout.newdish_layout, R.id.recipeName);

        //

        //Adding Text to Edit it passed from Intent
        if(new_pos > 0){
            int count = 1;
            String data = dataHelper.getRecipeName(new_pos);
            recipe_Name.setText(data);

            byte[] path = dataHelper.getRecipeImage(position);
            inputPhotoId.setImageBitmap(BitmapFactory.decodeByteArray(path,0,path.length));

            ArrayList<String> ingredients = dataHelper.getRecipeIngredientsArrayList(new_pos);

            int index = 0;
            for (int i = 1; i < ingredients.size()-1; i=i+1) {
                if(index < ingredients.size()) {
                    EditText edtText = (EditText) findViewById(getResources().getIdentifier("item" + i, "id", getPackageName()));
                    if(!ingredients.get(index).equals("0")){
                        edtText.setText(ingredients.get(index));
                        //index = index + 1;
                        //edtText.requestFocus();
                    }
                    //edtText.setText(ingredients.get(index));
                    index = index + 1;
                    edtText.requestFocus();
                }
                if(index < ingredients.size()) {
                //    Log.v(LOG_TAG, "i:" + i);
                    EditText countText = (EditText) findViewById(getResources().getIdentifier("count" + i, "id", getPackageName()));
                    countText.setText(ingredients.get(index));
                    index = index + 1;
                }
            }

            String instructions = dataHelper.getRecipeInstruction(new_pos);
            cookingDirections.setText(instructions);
        }
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        for (int i = 1; i < 11; i++) {
            Spinner spinner = (Spinner) findViewById(getResources().getIdentifier("spinner" + i, "id", getPackageName()));
            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(this);
            EditText edtText = (EditText) findViewById(getResources().getIdentifier("item" + i, "id", getPackageName()));
            edtText.setOnFocusChangeListener(this);
        }

        adapter.clear();
        adapter.add("");
        adapter.addAll(dataHelper.getItemArrayList());

        recipeAdapter.clear();
        recipeAdapter.add("");
        recipeAdapter.addAll(dataHelper_recipes.getDishArrayList());

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();



    }

    //*** Choose a Photo from the Photo Gallery
    private final View.OnClickListener getPhotoFromGallery = new View.OnClickListener() {

        public void onClick(View v) {
            Intent dishimageintent = new Intent();
            dishimageintent.setType("image/*");
            dishimageintent.setAction(Intent.ACTION_GET_CONTENT);

            startActivityForResult(
                    Intent.createChooser(dishimageintent, "Select Dish Image"), 1);
        }
    };

    //Intent returns a photo selected from the Photo Gallery
    @Override
    public void onActivityResult(int reqCode, int resCode, Intent data) {
        if (resCode == RESULT_OK) {
            if (reqCode == 1) {
                newEntry = false;
                defualtImage = data.getData();
                inputPhotoId.setImageURI(data.getData());

                Uri selectedImageURI = data.getData();
                Bitmap b = null;
                try {
                    b = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                b.compress(Bitmap.CompressFormat.PNG, 0, stream);
                bytearray = stream.toByteArray();
                // File imageFile = new File(getRealPathFromURI(selectedImageURI));
                ;
                //myfileName = myFile.getAbsolutePath();

              //  String m = null;


            }
        }
    }

    /* Convert the URI to Path */
    public String getPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        if( cursor != null ){
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        // this is our fallback here
        return contentUri.getPath();
    }

    /* When the user presses the Save Button, all the data gets stored in the Database */
    private final View.OnClickListener saveDish = new View.OnClickListener() {

        @Override
        public void onClick(View v){
            //Do stuff here


            final_recipeName = recipe_Name.getText().toString(); //Getting Recipe Name upon Submitting
            final_cookingDirections = cookingDirections.getText().toString();
           ArrayList<String> final_items = new ArrayList<>();
            ArrayList<Integer> final_counts = new ArrayList<>();
            for (int i = 1; i < 11; i++) {
                EditText edtText = (EditText) findViewById(getResources().getIdentifier("item" + i, "id", getPackageName()));
                EditText qty = (EditText) findViewById(getResources().getIdentifier("count" + i, "id", getPackageName()));
                //Getting Ingredients and count array list
                int count;
                if(edtText.getText().toString().trim().isEmpty() && qty.getText().toString().isEmpty()){
                    final_items.add("");
                    count = 0;
                }
                else if(!edtText.getText().toString().trim().isEmpty() && qty.getText().toString().isEmpty()){
                    final_items.add(edtText.getText().toString());
                    count = 1;
                }
                else
                {
                    count = Integer.parseInt(qty.getText().toString());
                    final_items.add(edtText.getText().toString());
                }

                final_counts.add(count);

            }


          //  Log.v(LOG_TAG, "Only recipe name: " + final_recipeName);
            AddDish new_recipe;
          //  Log.v(LOG_TAG, "myFileName:" + myfileName);
            new_recipe = new AddDish(final_recipeName, bytearray, final_items, final_counts, final_cookingDirections);
            if(new_pos >0){
                dataHelper_saveRecipe.updateRecipe(new_recipe);
            }
            else{
                dataHelper_saveRecipe.createRecipe(new_recipe);
            }

           //Log.v(LOG_TAG, "defaultImage" + defualtImage);
            finish();
        }
    };


    /**
     * Called when the focus state of a view has changed.
     *
     * @param v        The view whose state has changed.
     * @param hasFocus The new focus state of v.
     */
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (getResources().getResourceName(v.getId()).contains("item")) {
            if (!hasFocus) {
                if (dataHelper.addItem(((EditText)v).getText().toString())) {
                    adapter.clear();
                    adapter.addAll(dataHelper.getItemArrayList());
                    adapter.setNotifyOnChange(true);
                    adapter.notifyDataSetChanged();
                }
            }
        }
        else{
            if (!hasFocus) {
                if (dataHelper.addDish(((EditText) v).getText().toString())) {
                    recipeAdapter.clear();
                    recipeAdapter.addAll(dataHelper_recipes.getDishArrayList());
                    recipeAdapter.setNotifyOnChange(true);
                    recipeAdapter.notifyDataSetChanged();
                } else {
                    // TODO Auto-generated method stub
                    AlertDialog.Builder alert = new AlertDialog.Builder(this);
                    alert.setTitle("Error");
                    alert.setMessage("This Recipe has already been added!");
                    alert.setPositiveButton("OK", null);
                    alert.show();
                    recipe_Name.getText().clear();

                }
            }
        }
    }

    /**
     * <p>Callback method to be invoked when an item in this view has been
     * selected. This callback is invoked only when the newly selected
     * position is different from the previously selected position or if
     * there was no selected item.</p>
     * <p>
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
        if(count > 11){
            Spinner spinner = (Spinner) parent;
            if (getResources().getResourceName(spinner.getId()).contains("spinner")) {
                String temp = getResources().getResourceName(spinner.getId());
                temp = String.valueOf(temp.charAt(temp.length() - 1));
                if (temp.equals("0"))
                    temp = "10";
               // Log.v(LOG_TAG, "final value:" + temp);
                EditText txt = (EditText) findViewById(getResources().getIdentifier("item" + temp, "id", getPackageName()));
                txt.setText(adapter.getItem(position));
            }
        }
        count = count + 1;
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

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "NewDish Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.website.whatsfordinner/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "NewDish Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.website.whatsfordinner/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    //Add pic to imageView???
}
