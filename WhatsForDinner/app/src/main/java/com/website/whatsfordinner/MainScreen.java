package com.website.whatsfordinner;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainScreen extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);


        //Listener for Pop-up Window
        ImageView imagePop = (ImageView) findViewById(R.id.whatsfordinner);
        imagePop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainScreen.this, Pop.class));
            }
        });

    }


    public void mealsShow(View view){
        Intent mealsIntent = new Intent(MainScreen.this, Meal_Plan.class);
        startActivity(mealsIntent);
    }

    public void newdishShow(View view){
        Intent newDishIntent = new Intent(MainScreen.this, NewDish.class);
        startActivity(newDishIntent);
    }

    public void recipesShow(View view){
        Intent recipesIntent = new Intent(MainScreen.this, Recipes.class);
        startActivity(recipesIntent);
    }

    public void groceriesShow(View view){
        Intent groceriesIntent = new Intent(MainScreen.this, Grocery.class);
        startActivity(groceriesIntent);
    }
}
