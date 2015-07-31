package com.apps.hulios.examineapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.apps.hulios.examineapp.view.SlidingTabLayout;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.Serializable;
import java.util.ArrayList;


public class GenericPageActivity extends AppCompatActivity implements TaskFragment.TaskCallbacks{

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    /**
     * The {@link ViewPager} that will host the section contents.
     */

    // Statics used by Navigation Drawer
    private static final String ITEM_VIEW_SUPPLEMENTS = "View Supplements";
    private static final String ITEM_SUPPLEMENT_STACKS = "Supplement Stacks";
    private static final String ITEM_VIEW_TOPICS = "View Topics";
    private static final String ITEM_FAQ = "FAQ";

    // Statics for fragment with asyncTask
    private static final String TAG_TASK_FRAGMENT = "task_fragment";

    // Statics for intent extras
    public static final String CURRENT_TYPE_EXTRA = "com.apps.hulios.examineapp.current_type_extra";
    public static final String CURRENT_PAGE_EXTRA = "com.apps.hulios.examineapp.current_page_extra";

    // Statics for page type recognition
    public static final int TYPE_MAIN = 0;
    public static final int TYPE_SUPPLEMENTS = 1;
    public static final int TYPE_SUPPLEMENT = 2;
    public static final int TYPE_FAQ = 3;
    public static final int TYPE_FAQ_ITEM = 4;
    public static final int TYPE_TOPICS = 5;
    public static final int TYPE_TOPIC = 6;
    public static final int TYPE_STACKS = 7;
    public static final int TYPE_STACK = 8;
    public static final int TYPE_RUBRIC = 9;

    ViewPager mViewPager;
    LinearLayout lina;

    private int mCurrentType = 0;
    SectionsPagerAdapter mSectionsPagerAdapter;
    private Document mCurrentPageDoc;
    private ArrayList<String[]> mPageInfo;

    private TaskFragment mTaskFragment;

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mTitle = "Examine.com";
    private String mDrawerTitle = "Examine.com - page navigation";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get extras from intent
        mCurrentType = getIntent().getIntExtra(CURRENT_TYPE_EXTRA, TYPE_MAIN);
        String pageExtra = getIntent().getStringExtra(CURRENT_PAGE_EXTRA);
        if(mCurrentType==0) mCurrentType = TYPE_SUPPLEMENTS;
        if(pageExtra==null){
          getIntent().putExtra(CURRENT_PAGE_EXTRA,"http://www.examine.com/supplements");
        }

        // Set view
        setContentView(R.layout.activity_generic_page);

        setUpToolbar();
        setUpNavigationDrawer();

        //get progress "bar" (as linear layout)
        lina = (LinearLayout) findViewById(R.id.linlaHeaderProgress);

        // Get FragmentManager to add fragment with AsyncTask downloading page code
        android.app.FragmentManager fm = getFragmentManager();
        mTaskFragment = (TaskFragment) (fm.findFragmentByTag(TAG_TASK_FRAGMENT));

        // If the Fragment is non-null, then it is currently being
        // retained across a configuration change.
        if (mTaskFragment == null) {
            mTaskFragment = new TaskFragment();
            fm.beginTransaction().add(mTaskFragment, TAG_TASK_FRAGMENT).commit();
        }

        // Restore saved state
        mPageInfo = new ArrayList<>();
        mCurrentPageDoc = null;

        if (savedInstanceState != null) {
        String temp = savedInstanceState.getString("mCurrentPageDoc");
        Serializable ser = savedInstanceState.getSerializable("mPageInfo");
        if (temp != null && ser != null) {

            mCurrentPageDoc = Jsoup.parse(savedInstanceState.getString("mCurrentPageDoc"));
            //TODO
            //noinspection unchecked
            mPageInfo = (ArrayList<String[]>) (savedInstanceState.getSerializable("mPageInfo"));
        }
    }

        //set up tabs bar
        setUpSlidingTabBar();

    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }
    @Override
    protected void onResume() {
        super.onResume();
        mViewPager.getAdapter().notifyDataSetChanged();
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {

        if(mCurrentPageDoc != null) {
            outState.putString("mCurrentPageDoc", mCurrentPageDoc.toString());
            outState.putSerializable("mPageInfo", mPageInfo);
        }
        super.onSaveInstanceState(outState);
    }

    public void setUpSlidingTabBar(){
        // Setup PagerAdapter and ViewPager
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setOffscreenPageLimit(7);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // Get SlidingTabLayout
        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        // If there's only 1 Tab, remove SlidingTabBar
        if(mSectionsPagerAdapter.getCount()==1){
            ((ViewGroup) (slidingTabLayout.getParent())).removeView(slidingTabLayout);
        }else slidingTabLayout.setViewPager(mViewPager);

        slidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.Indicator);
            }

            @Override
            public int getDividerColor(int position) {
                return getResources().getColor(R.color.Divider);
            }
        });
    }


    private void setUpToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(mTitle);
        setSupportActionBar(mToolbar);
    }

    private void setUpNavigationDrawer(){
        String[] listArray;
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView)findViewById(R.id.left_drawer);
        listArray = new String[]{ITEM_VIEW_SUPPLEMENTS, ITEM_SUPPLEMENT_STACKS, ITEM_VIEW_TOPICS, ITEM_FAQ};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listArray);
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, findViewById(R.id.left_drawer)); // only if have listView with more than 1 item

        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                mToolbar,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        ) {
            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                mToolbar.setTitle(mTitle);
            }
            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                mToolbar.setTitle(mDrawerTitle);
            }
        };
        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if(item.getNumericShortcut()=='0') {
            if(mSectionsPagerAdapter.getCount()>0) {
                WebViewFragment temp = ((WebViewFragment)getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.pager + ":" + mViewPager.getCurrentItem()));
                temp.scrollToTop();
            }
        }
        if(item.getNumericShortcut()=='1'){
            TitleDialog dialog = TitleDialog.newInstance(mTitle);
            dialog.show(getSupportFragmentManager(),"Title Dialog");
        }
        if (mDrawerToggle.onOptionsItemSelected(item)) {

            return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
            menu.add(0,0,0,"Show full title").setShortcut('1','d');
        if(!(mCurrentType == TYPE_SUPPLEMENTS || mCurrentType == TYPE_FAQ || mCurrentType == TYPE_TOPICS))
            menu.add(0,0,0,"Back to Top").setShortcut('0','c');
        return true;
    }

    public void setRefTab(String ref){
        mViewPager.setCurrentItem(mSectionsPagerAdapter.getCount());
        WebViewFragment temp = ((WebViewFragment)getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.pager + ":" + mViewPager.getCurrentItem()));
        temp.scrollToCitation(ref);
    }

    public void showDisclaimer(){
        DisclaimerDialog dialog = DisclaimerDialog.newInstance();
        dialog.show(getSupportFragmentManager(), "Disclaimer Dialog");
    }




    public Document getCurrentPageDoc() {
        return mCurrentPageDoc;
    }


    // Callback needed to communicate with AsyncTask fragment
    @Override
    public void onPreExecute() {
        if(lina!=null) {
            lina.setVisibility(View.VISIBLE);
        }
    }
    // Callback needed to communicate with AsyncTask fragment
    @Override
    public void onProgressUpdate(int percent) {

    }
    // Callback needed to communicate with AsyncTask fragment
    @Override
    public void onCancelled() {

    }
    // Callback needed to communicate with AsyncTask fragment
    @Override
    public void onPostExecute(Document doc, ArrayList<String[]> pageInfo, String pageTitle) {
            mTitle = pageTitle;
            assert getSupportActionBar() != null;
            getSupportActionBar().setTitle(mTitle);
            if(doc == null){
                lina.setVisibility(View.GONE);
                Log.d("olol","Couldn't load page. Try again later");
                LinearLayout error = (LinearLayout) findViewById(R.id.linear_layout_error);
                if(isNetworkAvailable()){
                    TextView largeText = (TextView) findViewById(R.id.text_view_error_large);
                    largeText.setText(R.string.error_text_large);
                    TextView smallText = (TextView) findViewById(R.id.text_view_error_small);
                    smallText.setText(R.string.error_text_small);
                } else{
                    TextView largeText = (TextView) findViewById(R.id.text_view_error_large);
                    largeText.setText(R.string.error_text_large_nonet);
                    TextView smallText = (TextView) findViewById(R.id.text_view_error_small);
                    smallText.setText(R.string.error_text_small_nonet);
                }

                error.setVisibility(View.VISIBLE);

            } else {
                mCurrentPageDoc = doc;
                mPageInfo = pageInfo;
                mViewPager.getAdapter().notifyDataSetChanged();
                setUpSlidingTabBar();
                lina.setVisibility(View.GONE);
            }
    }

    public void refresh(View view) {
        view.setVisibility(View.GONE);
        android.app.FragmentManager fm = getFragmentManager();
        mTaskFragment = new TaskFragment();
        fm.beginTransaction().add(mTaskFragment, TAG_TASK_FRAGMENT).commit();
        lina.setVisibility(View.VISIBLE);
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onBackPressed() {

        if (this.isTaskRoot()) {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(R.string.app_exit_title)
                    .setMessage(R.string.app_exit_text)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }

                    })
                    .setNegativeButton(android.R.string.no, null)
                    .show();
        }
        else{
            super.onBackPressed();
        }
    }
    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
//            Log.d("pager","instantiated at pos: "+position);
            return super.instantiateItem(container, position);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            Fragment f;
            String id;
            switch (mCurrentType) {
                case TYPE_MAIN:
                    id = mPageInfo.get(position)[1];
                    f = WebViewFragment.newInstance(id);
                    return f;
                case TYPE_SUPPLEMENTS:
                    id = mPageInfo.get(position)[1];
                    f = RecyclerFragment.newInstance(id);
                    return f;
                case TYPE_SUPPLEMENT:
                    id = mPageInfo.get(position)[1];
                    f = WebViewFragment.newInstance(id);
                    return f;
                case TYPE_FAQ:
                    id = mPageInfo.get(position)[1];
                    f = RecyclerFragment.newInstance(id);
                    return f;
                case TYPE_FAQ_ITEM:
                    id = mPageInfo.get(position)[1];
                    f = WebViewFragment.newInstance(id);
                    return f;
                case TYPE_TOPICS:
                    id = mPageInfo.get(position)[1];
                    f = RecyclerFragment.newInstance(id);
                    return f;
                case TYPE_TOPIC:
                    id = mPageInfo.get(position)[1];
                    f = WebViewFragment.newInstance(id);
                    return f;
                case TYPE_STACKS:
                    id = mPageInfo.get(position)[1];
                    f = WebViewFragment.newInstance(id);
                    return f;
                case TYPE_STACK:
                    id = mPageInfo.get(position)[1];
                    f = WebViewFragment.newInstance(id);
                    return f;
                case TYPE_RUBRIC:
                    id = mPageInfo.get(position)[1];
                    f = WebViewFragment.newInstance(id);
                    return f;
                default:
                    return null;
            }
        }
        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            Loadable mCurrentView = (Loadable) object;
            if (mCurrentView != null) {
                if (mCurrentView.isLoading()) {
                    findViewById(R.id.linlaHeaderProgress).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.linlaHeaderProgress).setVisibility(View.GONE);

                }
            }
            Log.d("pager", "current View at pos: " + position);
        }

        @Override
        public int getCount() {
            return mPageInfo.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mPageInfo.get(position)[0];
        }
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
        private void selectItem(int position) {
            Intent i;
            switch(mDrawerList.getItemAtPosition(position).toString()){
                case ITEM_VIEW_SUPPLEMENTS:
                    i = new Intent(GenericPageActivity.this, GenericPageActivity.class);
                    i.putExtra(GenericPageActivity.CURRENT_TYPE_EXTRA, GenericPageActivity.TYPE_SUPPLEMENTS);
                    i.putExtra(GenericPageActivity.CURRENT_PAGE_EXTRA, "http://www.examine.com/supplements");
                    startActivity(i);
                    mDrawerLayout.closeDrawer(mDrawerList);
                    break;
                case ITEM_SUPPLEMENT_STACKS:
                    i = new Intent(GenericPageActivity.this, GenericPageActivity.class);
                    i.putExtra(GenericPageActivity.CURRENT_TYPE_EXTRA, GenericPageActivity.TYPE_STACKS);
                    i.putExtra(GenericPageActivity.CURRENT_PAGE_EXTRA, "http://www.examine.com/stacks");
                    startActivity(i);
                    break;
                case ITEM_VIEW_TOPICS:
                    i = new Intent(GenericPageActivity.this, GenericPageActivity.class);
                    i.putExtra(GenericPageActivity.CURRENT_TYPE_EXTRA, GenericPageActivity.TYPE_TOPICS);
                    i.putExtra(GenericPageActivity.CURRENT_PAGE_EXTRA, "http://www.examine.com/topics");
                    startActivity(i);
                    break;
                case ITEM_FAQ:
                    i = new Intent(GenericPageActivity.this,GenericPageActivity.class);
                    i.putExtra(GenericPageActivity.CURRENT_TYPE_EXTRA,GenericPageActivity.TYPE_FAQ);
                    i.putExtra(GenericPageActivity.CURRENT_PAGE_EXTRA, "http://www.examine.com/faq");
                    startActivity(i);
                    break;
            }
            mDrawerLayout.closeDrawer(mDrawerList);

        }
    }
}
