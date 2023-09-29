package com.example.whereitssnapapp3;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity  implements ActivityComs{
    private ListView mNavDrawerList;
    private DrawerLayout mDrawerLayout;
    private ArrayAdapter<String> mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mActivityTitle;
    public DataManager dataManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dataManager = new DataManager(getApplicationContext());
        mNavDrawerList = (ListView)findViewById(R.id.navList);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawerLayout);
        mActivityTitle = getTitle().toString();
        String[] navMenuTitles = getResources().
                getStringArray(R.array.nav_drawer_items);
// Initialize our ArrayAdapter
        mAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, navMenuTitles);
        mNavDrawerList.setAdapter(mAdapter);
        setupDrawer();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mNavDrawerList.setOnItemClickListener
                (new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapter,
                                            View view, int whichItem, long id) {
                        switchFragment(whichItem);
                    }
                });
        switchFragment(0);
    }

    private void switchFragment(int position) {
        Fragment fragment = null;
        String fragmentID ="";
        switch (position) {
            case 0:
                fragmentID = "TITLES";
                Bundle args = new Bundle();
                args.putString("Tag", "_NO_TAG");
                fragment = new TitlesFragment();
                fragment.setArguments(args);
                break;
            case 1:
                fragmentID = "TAGS";
                fragment = new TagsFragment();
                break;
            case 2:
                fragmentID = "CAPTURE";
                fragment = new CaptureFragment();
                break;
            default:
                break;
        }
// More code goes here next
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentHolder, fragment,
                        fragmentID).commit();
        mDrawerLayout.closeDrawer(mNavDrawerList);
    }
    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle
                (this, mDrawerLayout, R.string.drawer_open,
                        R.string.drawer_close) {
            // Called when drawer is opened
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Make selection");
// triggers call to onPrepareOptionsMenu
                invalidateOptionsMenu();
            }
            // Called when drawer is closed
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mActivityTitle);
// triggers call to onPrepareOptionsMenu
                invalidateOptionsMenu();
            }
        };
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
// Close drawer if open
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
//drawer is open so close it
            mDrawerLayout.closeDrawer(mNavDrawerList);
        } else {
// Go back to titles fragment
// Quit if already at titles fragment
            Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragmentHolder);
            if (f instanceof TitlesFragment) {
                finish();
                System.exit(0);
            } else {
                switchFragment(0);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
//noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
// Activate the navigation drawer toggle
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onTagsListItemSelected(String clickedTag){
// We have just received a String for the TitlesFragment
// Prepare a new Bundle
        Bundle args = new Bundle();
// Pack the string into the Bundle
        args.putString("Tag", clickedTag);

        TitlesFragment fragment = new TitlesFragment();
// Load the Bundle into the Fragment
        fragment.setArguments(args);
// Start the fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace
                (R.id.fragmentHolder, fragment, "TAGS").commit();
// update selected item and title, then close the drawer
        mNavDrawerList.setItemChecked(1, true);
        mNavDrawerList.setSelection(1);
        mDrawerLayout.closeDrawer(mNavDrawerList);
    }

    public void onTitlesListItemSelected(int position) {
// Load up the bundle with the row _id
        Bundle args = new Bundle();
        args.putInt("Position", position);
// Create the fragment and add the bundle
        ViewFragment fragment = new ViewFragment();
        fragment.setArguments(args);
// Start the fragment
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().
                    replace(R.id.fragmentHolder, fragment, "VIEW").commit();
// update selected item and title, then close the drawer
            mNavDrawerList.setItemChecked(1, true);
            mNavDrawerList.setSelection(1);
//setTitle(navMenuTitles[position]);
            mDrawerLayout.closeDrawer(mNavDrawerList);
        } else {
// error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
        }
    }
}