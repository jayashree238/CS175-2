package com.website.whatsfordinner;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by jayashreemadhanraj on 9/11/16.
 */
public class DataHelper extends SQLiteOpenHelper {

    private static final String LOG_TAG = DataHelper.class.getSimpleName();
    private static final int DATABASE_VERSION = 20;
    private static final String ITEM_TABLE_NAME = "items";
    private static final String DISH_TABLE_NAME = "dish";
    private static final String RECIPE_TABLE_NAME = "recipenew";
    private static final String GROCERY_LIST_TABLE_NAME = "grocery_list";
    private static final String DATABASE_NAME = "chef";
    private static final String AVAILABLE_MEALS_TABLE_NAME = "available_meals";
    private static final String MEALS_PLAN_TABLE = "meals_plan";
    int check = 0;


    //Queries to create Tables
    private static final String ITEM_TABLE_CREATE =
            "CREATE TABLE " + ITEM_TABLE_NAME + " (ID INTEGER PRIMARY KEY, ITEM_NAME TEXT);";
    private static final String DISH_TABLE_CREATE =
            "CREATE TABLE " + DISH_TABLE_NAME + " (ID INTEGER PRIMARY KEY, DISH_NAME TEXT);";

    private static final String GROCERY_LIST_CREATE = "CREATE TABLE " + GROCERY_LIST_TABLE_NAME + " (ID INTEGER PRIMARY KEY, ITEM_NAME TEXT, COUNT INTEGER, ITEM_MEASURE_TYPE TEXT)";
    private static final String AVAILABLE_MEALS_CREATE = "CREATE TABLE " + AVAILABLE_MEALS_TABLE_NAME + " (ID INTEGER PRIMARY KEY, MEAL_NAME TEXT)";

    private static final String RECIPE_TABLE_CREATE =
            "CREATE TABLE " + RECIPE_TABLE_NAME + " (ID INTEGER PRIMARY KEY, RECIPE_NAME TEXT, RECIPE_IMAGE BLOB, ITEM1 TEXT, COUNT1 INTEGER, ITEM2 TEXT, COUNT2 INTEGER, ITEM3 TEXT, COUNT3 INTEGER," +
                    "ITEM4 TEXT, COUNT4 INTEGER, ITEM5 TEXT, COUNT5 INTEGER, ITEM6 TEXT, COUNT6 INTEGER, ITEM7 TEXT, COUNT7 INTEGER, ITEM8 TEXT, COUNT8 INTEGER, ITEM9 TEXT, COUNT9 INTEGER, ITEM10 TEXT, COUNT10 INTEGER, INSTRUCTIONS TEXT);";

    private static final String MEALS_PLAN_CREATE = "CREATE TABLE " + MEALS_PLAN_TABLE + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, TEXTVIEW TEXT, MEAL_NAME TEXT)";


    DataHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ITEM_TABLE_CREATE);
        db.execSQL(DISH_TABLE_CREATE);
        db.execSQL(RECIPE_TABLE_CREATE);
        db.execSQL(GROCERY_LIST_CREATE);
        db.execSQL(AVAILABLE_MEALS_CREATE);
        db.execSQL(MEALS_PLAN_CREATE);
    }


    //MEALS_PLAN - Set the values for the fields when the user comes back to the Activity

    public void updateMeals_Plan(int id, String meal_name){
        SQLiteDatabase db = getWritableDatabase();
        String update_meal_plan = "UPDATE " + MEALS_PLAN_TABLE + " SET MEAL_NAME = '" + meal_name +"' WHERE ID = " + id;
        db.execSQL(update_meal_plan);
        db.close();
    }

    public ArrayList<String> setMeal_Plan_onLoad(){

        SQLiteDatabase db = getWritableDatabase();
        ArrayList<String> items = new ArrayList<>();
        Cursor cMeal_Plan = db.rawQuery("SELECT MEAL_NAME FROM " + MEALS_PLAN_TABLE, null);
        cMeal_Plan.moveToFirst();
        for(int i =0; i<cMeal_Plan.getCount();i++){
            cMeal_Plan.moveToPosition(i);
            items.add(cMeal_Plan.getString(0));
        }
        db.close();
        cMeal_Plan.close();
        return items;

    }


    public void saveMealPlanForFuture(String textView, String meal){
        Log.v(LOG_TAG, "textView:" + textView);
        SQLiteDatabase db = getWritableDatabase();
        String insert = "INSERT INTO " + MEALS_PLAN_TABLE + " (TEXTVIEW, MEAL_NAME) VALUES ('" + textView + "', '" + meal + "')";
        db.execSQL(insert);
        db.close();
    }
    public void clearMeals_Plan(){
        SQLiteDatabase db = getWritableDatabase();
        String deleteAll = "DELETE FROM " + MEALS_PLAN_TABLE;
            db.execSQL(deleteAll);
        db.close();
    }

    //MEALS ACTIVITY

    public void addToAvailableMeals(int position) {
        SQLiteDatabase db = getWritableDatabase();
        int new_pos = position + 1;
        Cursor cSelect = db.rawQuery("SELECT RECIPE_NAME from recipenew WHERE ID = " + new_pos, null);

        if (cSelect != null && cSelect.moveToFirst()) {

            String temp = cSelect.getString(0);
            addtoMealsArray(temp);
            cSelect.close();

        }

        db.close();
    }

    public void addtoMealsArray(String meal_name){
        SQLiteDatabase db = getWritableDatabase();
        Cursor c =db.rawQuery("INSERT INTO " + AVAILABLE_MEALS_TABLE_NAME + " (MEAL_NAME) VALUES ('" + meal_name + "')", null);
        c.moveToFirst();
        c.close();
        db.close();
    }


    public void checkMeals(){
        SQLiteDatabase db = getWritableDatabase();
        Cursor c =db.rawQuery("SELECT min(ID), MEAL_NAME FROM " + AVAILABLE_MEALS_TABLE_NAME + " GROUP BY MEAL_NAME", null);
        c.moveToFirst();
        for(int i = 0; i<c.getCount(); i++){
            c.moveToPosition(i);
            Log.v(LOG_TAG, "Checkmeal:" + c.getString(0) +": " + c.getString(1));
        }
        c.close();
        db.close();
    }

    public boolean deleteFromAvailableMeals(String item_name){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(AVAILABLE_MEALS_TABLE_NAME,"MEAL_NAME IS '" + item_name + "' AND ID = (SELECT min(ID) FROM " + AVAILABLE_MEALS_TABLE_NAME + " WHERE MEAL_NAME = '" + item_name + "')",new String[]{} );
        //db.execSQL("DELETE FROM " + AVAILABLE_MEALS_TABLE_NAME + " WHERE MEAL_NAME IS '" + item_name + "' AND ID = (SELECT min(ID) FROM " + AVAILABLE_MEALS_TABLE_NAME + " WHERE MEAL_NAME = '" + item_name + "')");
        db.close();
        return true;
//
    }

    public ArrayList<String> getFromAvailableMeals(){

        SQLiteDatabase db = getWritableDatabase();
        ArrayList<String> temp = new ArrayList<>();

        Cursor c = db.rawQuery("SELECT MEAL_NAME FROM " + AVAILABLE_MEALS_TABLE_NAME, null);
        Log.v(LOG_TAG, "Count in getFromAvaiMeals" + c.getCount());
        c.moveToFirst();

        for (int i = 0; i < c.getCount(); i++) {
            c.moveToPosition(i);
            temp.add(c.getString(0));
        }
        c.close();
        db.close();
        Log.v(LOG_TAG,"Meals available: " + temp.toString());
        return temp;
    }

    //GROCERY ACTIVITY
    public void addToGrocery(String recipe_name, String item_name){
        String item_measure_type;

        List<String> pieces = Arrays.asList("tomato", "onion", "garlic");
        List<String> pounds = Arrays.asList("chicken", "beef", "pork", "goat", "shrimp", "salmon");
        List<String> cups = Arrays.asList("all-purpose", "wheat", "sugar", "salt", "pepper", "baking powder", "baking soda", "cheese");
        List<String> teaspoons = Arrays.asList("oil", "olive oil", "canola oil", "sunflower oil", "extra virgin oil oil");

        if(pieces.contains(item_name)){
            item_measure_type = "pieces";
        }
        else if(pounds.contains(item_name)){
            item_measure_type = "pounds";
        }
        else if(cups.contains(item_name)){
            item_measure_type = "cups";
        }
        else if(teaspoons.contains(item_name)){
            item_measure_type = "teaspoons";
        }
        else{
            item_measure_type = "unit";
        }
        //Log.v(LOG_TAG, "item_name in AddToGrocery:" + item_name);
       // ArrayList<String> itemAndCount = itemsListForGrocery(recipe_name);
        HashMap<String, String> itemAndCount = itemsListForGrocery(recipe_name);
        String count = itemAndCount.get(item_name);
        //Cursor c = db.rawQuery("SELECT COUNT FROM ")
        SQLiteDatabase db = getWritableDatabase();
        String insert = "INSERT or replace INTO " + GROCERY_LIST_TABLE_NAME + " (ITEM_NAME, COUNT, ITEM_MEASURE_TYPE) VALUES ('"+ item_name + "', '" + count + "', '" + item_measure_type +"');";
        db.execSQL(insert);
        db.close();
    }

//    public void readFromGroceryList(){
//        SQLiteDatabase db = getWritableDatabase();
//        ArrayList<String> temp = new ArrayList<>();
//        Cursor c = db.rawQuery("SELECT ITEM_NAME, COUNT(ITEM_NAME), ITEM_MEASURE_TYPE FROM grocery_list GROUP BY ITEM_NAME", null);
//        //int count = c.getCount();
//        for (int i = 0; i < c.getCount(); i++) {
//            c.moveToPosition(i);
//            temp.add(c.getString(0));
//            Log.v(LOG_TAG, "RETREIVING FROM GROCERY LIST" + c.getString(0) + " count: " + c.getString(1));
//        }
//        c.moveToFirst();
//
//        c.close();
//        db.close();
//        //return temp;
//    }

    public HashMap<String, String> itemsListForGrocery(String recipe_name){
        SQLiteDatabase db = getWritableDatabase();
        //ArrayList<String> temp = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT ITEM1, COUNT1, ITEM2, COUNT2, ITEM3, COUNT3, ITEM4, COUNT4, ITEM5, COUNT5, ITEM6, COUNT6, ITEM7, COUNT7, ITEM8, COUNT8, ITEM9, COUNT9, ITEM10, COUNT10 FROM recipenew WHERE RECIPE_NAME = '" + recipe_name + "'", null);
        c.moveToFirst();
        HashMap<String, String> temp = new HashMap<>();
        for (int i = 0; i < 19; i=i+2) {

            if(!c.getString(i).equals("")){
                if(!c.getString(i).equals(0)){
                    temp.put(c.getString(i), c.getString(i+1));
                    //temp.add(Integer.parseInt(c.getString(i)), c.getString(i+1));
                }
            }
        }

        c.close();
        db.close();
        return temp;
    }

    //RECIPE ACTIVITY
    /* When the Save Button is clicked for a new recipe */
    public void createRecipe(AddDish recipe) {
        SQLiteDatabase db = getWritableDatabase();
        ArrayList<String> items = recipe.getItems();
        ArrayList<Integer> counts = recipe.getCount();

        Log.v(LOG_TAG, "RECIPE_NAME before inserting in table:" + recipe.getdishName());
        String insert = "INSERT or replace INTO " + RECIPE_TABLE_NAME + "(RECIPE_NAME, RECIPE_IMAGE, ITEM1, COUNT1, ITEM2, COUNT2, ITEM3, COUNT3, ITEM4, COUNT4, ITEM5, COUNT5, ITEM6, COUNT6, ITEM7, COUNT7, ITEM8, COUNT8, ITEM9, COUNT9, ITEM10, COUNT10, INSTRUCTIONS) VALUES ('"
                + recipe.getdishName() + "', '" + recipe.getPhotoURI() + "', '" + items.get(0) +"', '"+ counts.get(0)  + "', '"+ items.get(1) +"', '"+ counts.get(1)  +"', '"+ items.get(2) +"', '"+ counts.get(2)  +"', '"+ items.get(3) +"', '"+ counts.get(3)  +"', '"+ items.get(4) +"', '"+ counts.get(4)  +"', '"
                + items.get(5) +"', '"+ counts.get(5)  +"', '"+ items.get(6) +"', '"+ counts.get(6)  +"', '"+ items.get(7) +"', '"+ counts.get(7)  +"', '"+ items.get(8) +"', '"+ counts.get(8)  +"', '" + items.get(9) +"', '"+ counts.get(9)+"', '" +  recipe.getInstruction() + "');";
        db.execSQL(insert);
        Log.v(LOG_TAG, "Recipe.getPhotoURI in db:" + recipe.getPhotoURI());
        db.close();
        getRecipeArrayList();

    }

    /* Update the values in the recipes table when the users change or add more values */
    public void updateRecipe(AddDish recipe_name){
        SQLiteDatabase db = getWritableDatabase();
        ArrayList<String> items = recipe_name.getItems();
        ArrayList<Integer> counts = recipe_name.getCount();
        String update = "UPDATE " + RECIPE_TABLE_NAME + " SET RECIPE_IMAGE = '" + recipe_name.getPhotoURI() + "' WHERE RECIPE_NAME = '" + recipe_name.getdishName() + "'";
        for(int i=1;i < 11;i++){
            String update2 = "UPDATE " + RECIPE_TABLE_NAME + " SET ITEM" + i + "= '" + items.get(i-1) + "' WHERE RECIPE_NAME = '" + recipe_name.getdishName() + "'";
            String update3 = "UPDATE " + RECIPE_TABLE_NAME + " SET COUNT" + i + "= " + counts.get(i-1)+ " WHERE RECIPE_NAME = '" + recipe_name.getdishName() + "'";
            db.execSQL(update2);
            db.execSQL(update3);
        }

      String update4 = "UPDATE " + RECIPE_TABLE_NAME + " SET RECIPE_IMAGE = '" + recipe_name.getPhotoURI() + "' WHERE RECIPE_NAME = '" + recipe_name.getInstruction() + "'";
        db.execSQL(update);
        db.execSQL(update4);
        db.close();

    }

    /* To get a list of all the available recipes to display in list_fragment */
    public ArrayList<String> getRecipeArrayList() {

        SQLiteDatabase db = getWritableDatabase();
        ArrayList<String> temp = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT * FROM " + RECIPE_TABLE_NAME, null);
        c.moveToFirst();
        //int count = c.getCount();
        for (int i = 0; i < c.getCount(); i++) {
            c.moveToPosition(i);
            temp.add(c.getString(1));
            Log.v(LOG_TAG, "Recipe_List" + c.getString(0));
        }
        c.close();
        db.close();

        return temp;
    }

    /* Function to return only the recipe name given an ID */
    public String getRecipeName(Integer id){
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.rawQuery("SELECT RECIPE_NAME FROM " + RECIPE_TABLE_NAME + " WHERE ID = " + id, null);
        c.moveToFirst();
        String temp = c.getString(0);
        c.close();
        db.close();
        return temp;
    }

    /* Function to return only the recipe image given an ID */
    public byte[] getRecipeImage(Integer id) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor c=db.rawQuery("SELECT RECIPE_IMAGE FROM " + RECIPE_TABLE_NAME + " WHERE ID = " + id, null);
        c.moveToFirst();
//        Uri image = Uri.parse("android.resource://com.website.whatsfordinner/drawable/none.png");
//        if(c!=null){
//            c.moveToFirst();
//           image = Uri.parse(c.getString(0));
//        }
        byte[] image = c.getBlob(0);
        c.close();
        db.close();

        return image;

    }

    /*Get ONLY the Items and their Count for Recipe if ID is given*/
    public ArrayList<String> getRecipeIngredientsArrayList(Integer id) {

        SQLiteDatabase db = getWritableDatabase();
        ArrayList<String> temp = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT ITEM1, COUNT1, ITEM2, COUNT2, ITEM3, COUNT3, ITEM4, COUNT4, ITEM5, COUNT5, ITEM6, COUNT6, ITEM7, COUNT7, ITEM8, COUNT8, ITEM9, COUNT9, ITEM10, COUNT10 FROM recipenew WHERE ID = " + id, null);
        c.moveToFirst();
        for (int i = 0; i < 20; i++) {

            if(!c.getString(i).equals("")){
                if(!c.getString(i).equals(0)){
                    temp.add(c.getString(i));
                }
            }
        }

        c.close();
        db.close();
        return temp;
    }
    /*Get ONLY the Items and their Count for Recipe if recipe name is given*/
    public ArrayList<String> getRecipeIngredientsArrayList(String recipe_name) {

        SQLiteDatabase db = getWritableDatabase();
        ArrayList<String> temp = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT ITEM1, ITEM2, ITEM3, ITEM4, ITEM5, ITEM6, ITEM7, ITEM8, ITEM9, ITEM10 FROM recipenew WHERE RECIPE_NAME = '" + recipe_name + "'", null);
        c.moveToFirst();
        for (int i = 0; i < 10; i++) {

            if(c.getString(i).equals("")){
                break;
            }
            temp.add(c.getString(i));
        }

        c.close();
        db.close();
        return temp;
    }

    /* Function to return only the recipe Instructions given an ID */
    public String getRecipeInstruction(Integer id) {

        SQLiteDatabase db = getWritableDatabase();
        //ArrayList<String> temp = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT INSTRUCTIONS FROM recipenew WHERE ID = " + id, null);
        c.moveToFirst();
        String temp = c.getString(0);
        c.close();
        db.close();
        return temp;
    }

    //ADD AND CHECK ITEMS FOR SPINNER
    /* To add to Spinner */
    public ArrayList<String> getItemArrayList() {

        ArrayList<String> temp = new ArrayList<>();
        getReadableDatabase().beginTransaction();
        Cursor cursor = getReadableDatabase().query(ITEM_TABLE_NAME, new String[]{"ITEM_NAME"}, null, null, null, null, null);
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToPosition(i);
            temp.add(cursor.getString(0));
        }
        cursor.close();
        getReadableDatabase().endTransaction();
        return temp;

    }

    /* Add any new ingredient to the Item list to be read by Spinner */
    public boolean addItem(String item_name) {
        getReadableDatabase().beginTransaction();
        Cursor cursor = getReadableDatabase().query(ITEM_TABLE_NAME, new String[]{"ITEM_NAME"}, null, null, null, null, null);
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToPosition(i);
            String temp = cursor.getString(0);
            Log.v(LOG_TAG, "Item " + i + ":" + cursor.getString(0));
            if (temp.equals(item_name)) {
                getReadableDatabase().endTransaction();
                return false;
            }
        }
        cursor.close();
        getReadableDatabase().endTransaction();
        getWritableDatabase().beginTransaction();
        ContentValues cv = new ContentValues(1);
        cv.put("ITEM_NAME", item_name);
        long rowId = getWritableDatabase().insert(ITEM_TABLE_NAME, null, cv);
        Log.v(LOG_TAG, "Added row id:" + rowId);
        getWritableDatabase().setTransactionSuccessful();
        getWritableDatabase().endTransaction();
        return true;
    }

    /*ADD AND CHECK DUPLICATE DISHES */
    public ArrayList<String> getDishArrayList() {
        ArrayList<String> temp = new ArrayList<>();
        getReadableDatabase().beginTransaction();
        Cursor cursor = getReadableDatabase().query(DISH_TABLE_NAME, new String[]{"DISH_NAME"}, null, null, null, null, null);
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToPosition(i);
            temp.add(cursor.getString(0));
        }
        cursor.close();
        getReadableDatabase().endTransaction();
        return temp;
    }

    public boolean addDish(String dish_name) {
        getReadableDatabase().beginTransaction();
        Cursor cursor = getReadableDatabase().query(DISH_TABLE_NAME, new String[]{"DISH_NAME"}, null, null, null, null, null);
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToPosition(i);
            String temp = cursor.getString(0);
            if (temp.equals(dish_name)) {
                getReadableDatabase().endTransaction();
                cursor.close();
                return false;
            }
            }
            cursor.close();

            getReadableDatabase().endTransaction();
            getWritableDatabase().beginTransaction();
            ContentValues cv = new ContentValues(1);
            cv.put("DISH_NAME", dish_name);
            long rowId = getWritableDatabase().insert(DISH_TABLE_NAME, null, cv);
            Log.v(LOG_TAG, "Added row id:" + rowId);
            getWritableDatabase().setTransactionSuccessful();
            getWritableDatabase().endTransaction();
            return true;
        }


        //getWritableDatabase().setTransactionSuccessful();
        //getWritableDatabase().endTransaction();
        //return false;






    /**
     * Called when the database needs to be upgraded. The implementation
     * should use this method to drop tables, add tables, or do anything else it
     * needs to upgrade to the new schema version.
     * <p>
     * <p>
     * The SQLite ALTER TABLE documentation can be found
     * <a href="http://sqlite.org/lang_altertable.html">here</a>. If you add new columns
     * you can use ALTER TABLE to insert them into a live table. If you rename or remove columns
     * you can use ALTER TABLE to rename the old table, then create the new table and then
     * populate the new table with the contents of the old table.
     * </p><p>
     * This method executes within a transaction.  If an exception is thrown, all changes
     * will automatically be rolled back.
     * </p>
     *
     * @param db         The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
