package com.website.whatsfordinner;

import android.app.Fragment;

import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jayashreemadhanraj on 9/16/16.
 */
public class InformationFragment extends Fragment{
    DataHelper helper;
    String recipeInstructionDetail;
    Integer position;
    String recipeNameDetail;
    ArrayList<String> recipeIngredientsDetail;
    String ingredients_final ="";

    private static final String LOG_TAG = InformationFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        helper = new DataHelper(getActivity().getApplicationContext());
       View v =  inflater.inflate(R.layout.information_fragment, container, false);
        TextView recipeName = (TextView) v.findViewById(R.id.recipeName);
        ImageView recipeImage = (ImageView) v.findViewById(R.id.recipePic);
        TextView ingredients = (TextView) v.findViewById(R.id.ingredients);
        TextView instruction = (TextView) v.findViewById(R.id.instruction);


        Bundle b = getArguments();
       Log.v(LOG_TAG, "In between bundle and setTExt with value " + String.valueOf(b.getInt("position")));
        position = Integer.parseInt(String.valueOf(b.getInt("position"))) + 1;

        recipeNameDetail = helper.getRecipeName(position);

        recipeIngredientsDetail = helper.getRecipeIngredientsArrayList(position);
        recipeInstructionDetail = helper.getRecipeInstruction(position);

        for(int i=1;i < (recipeIngredientsDetail.size())/2;i=i+2){
            Log.v(LOG_TAG, "Inside the Information Fragment:" + i);
            if(!recipeIngredientsDetail.get(i).equals("0")) {

                ingredients_final = ingredients_final + "(" + recipeIngredientsDetail.get(i) + ") " + recipeIngredientsDetail.get(i - 1) + "\n";
            }
        }


        recipeName.setText(recipeNameDetail);
        byte[] path = helper.getRecipeImage(position);
        Log.v(LOG_TAG,"Path:" + path);
        recipeImage.setImageBitmap(BitmapFactory.decodeByteArray(path,0,path.length));
        //recipeImage.setImageBitmap(BitmapFactory.decodeFile(path));
        ingredients.setText(ingredients_final);
        instruction.setText(recipeInstructionDetail);
        return v;
    }

}
