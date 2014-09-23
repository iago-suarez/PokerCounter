package com.poker.iago.pokercounter.iu;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.poker.iago.pokercounter.R;


public class MainNawDraver extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    public static final String INITIAL_DRAWER_ITEM_CLASS = "INITIAL_DRAWER_ITEM_CLASS";

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private Fragment containedFragment;
    private Class drawerInitialClassItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Si nos han pasado una clase para que carguemos su fragment, se lo pasamos al
        // NavigationDrawerFragment
        Bundle mBundle = getIntent().getExtras();
        if (mBundle != null)
            drawerInitialClassItem = (Class) mBundle.getSerializable(INITIAL_DRAWER_ITEM_CLASS);

        setContentView(R.layout.activity_main_naw_draver);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position, boolean fromSavedInstanceState) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        containedFragment = BlindsCounterFrag.newInstance(position);
        fragmentManager.beginTransaction()
                .replace(R.id.container, containedFragment)
                .commit();
    }

    /**
     * Return the initial fragment class associated to a DrawerItem, to be started.
     *
     * @return the class or null if thre are no initial fragment to load.
     */
    @Override
    public Class getInitialDrawerClassItem() {
        return drawerInitialClassItem;
    }

    /**
     * Load in mTitle the name for the actual windows, it's not posible to meka it calloing
     * NavigationDrawerFragment ItemDrawer's list because is posible that the NavigationDrawerFragment
     * is not instanciated.
     * @param position
     */
    public void updateTitle(int position) {
        switch (position) {
            case 0:
                mTitle = getString(R.string.blinds_counter_label);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main_naw_draver, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
