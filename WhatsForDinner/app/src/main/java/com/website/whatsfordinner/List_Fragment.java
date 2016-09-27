package com.website.whatsfordinner;


import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by jayashreemadhanraj on 9/16/16.
 */
public class List_Fragment extends ListFragment{
    private static final String LOG_TAG = List_Fragment.class.getSimpleName();
    DataHelper helper;
    public ArrayList<String> recipeListing;
    ListFragmentItemClickListener iFaceItemClickListener;


    public interface ListFragmentItemClickListener{
        void onListFragmentItemClick(int position);
    }


    @Override
    public void onAttach(Context context){
        super.onAttach(context);

        Activity a = null;
        if(context instanceof Activity){
            a = (Activity) context;
        }
        try{
            iFaceItemClickListener = (ListFragmentItemClickListener)a;
        }
        catch (Exception e){
            Toast.makeText(a.getBaseContext(), "Exception", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        helper = new DataHelper(getActivity().getApplicationContext());
        recipeListing = helper.getRecipeArrayList();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(inflater.getContext(), R.layout.list_item_recipe, R.id.list_item_recipe_textview, recipeListing);
        setListAdapter(adapter);
        View root =  inflater.inflate(R.layout.list_fragment,container,false);

        return root;
    }

    /**
     * Called when the fragment's activity has been created and this
     * fragment's view hierarchy instantiated.  It can be used to do final
     * initialization once these pieces are in place, such as retrieving
     * views or restoring state.  It is also useful for fragments that use
     * {@link #setRetainInstance(boolean)} to retain their instance,
     * as this callback tells the fragment when it is fully associated with
     * the new activity instance.  This is called after {@link #onCreateView}
     * and before {@link #onViewStateRestored(Bundle)}.
     *
     * @param savedInstanceState If the fragment is being re-created from
     *                           a previous saved state, this is the state.
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                Log.v(LOG_TAG, "insideOnItemLongClick() at position:" + position);
                Log.v(LOG_TAG, "getActivity:" + getActivity() );
                Intent intent = new Intent(getActivity(), NewDish.class);
                intent.putExtra("position", position);
                startActivity(intent);
                return true;
            }
        });
    }


    /**
     * Called to do initial creation of a fragment.  This is called after
     * {@link #onAttach(Activity)} and before
     * {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}, but is not called if the fragment
     * instance is retained across Activity re-creation (see {@link #setRetainInstance(boolean)}).
     * <p>
     * <p>Note that this can be called while the fragment's activity is
     * still in the process of being created.  As such, you can not rely
     * on things like the activity's content view hierarchy being initialized
     * at this point.  If you want to do work once the activity itself is
     * created, see {@link #onActivityCreated(Bundle)}.
     * <p>
     * <p>If your app's <code>targetSdkVersion</code> is 23 or lower, child fragments
     * being restored from the savedInstanceState are restored after <code>onCreate</code>
     * returns. When targeting N or above and running on an N or newer platform version
     * they are restored by <code>Fragment.onCreate</code>.</p>
     *
     * @param savedInstanceState If the fragment is being re-created from
     *                           a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(LOG_TAG,"On create");
    }



    @Override
    public void onListItemClick(ListView l, View view, int position, long id){
        int orientation = getResources().getConfiguration().orientation;
        Log.v(LOG_TAG, "inside clicked function");
        if (orientation == Configuration.ORIENTATION_LANDSCAPE){
            FragmentManager fragmentManager = getActivity().getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            //Checkpoint
            Fragment prevFragment = fragmentManager.findFragmentByTag("com.website.whatsfordinner.Recipes.details");

            if(prevFragment!=null){
                fragmentTransaction.remove(prevFragment);
            }

            InformationFragment informationFragment = new InformationFragment();
            Bundle b = new Bundle();
            b.putInt("position", position);
            informationFragment.setArguments(b);

            fragmentTransaction.add(R.id.detail_fragment_container, informationFragment, "com.website.whatsfordinner.Recipes.details");

            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
        else
        {
            helper = new DataHelper(getActivity());
           helper.addToAvailableMeals(position);

        }

    }


}
