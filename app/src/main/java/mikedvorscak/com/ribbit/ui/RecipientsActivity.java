package mikedvorscak.com.ribbit.ui;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;

import mikedvorscak.com.ribbit.R;
import mikedvorscak.com.ribbit.adapters.RecipientsAdapter;
import mikedvorscak.com.ribbit.utils.FileHelper;
import mikedvorscak.com.ribbit.utils.ParseConstants;
import mikedvorscak.com.ribbit.utils.Utils;


public class RecipientsActivity extends ActionBarActivity implements ActionBar.TabListener{

    public static final String TAG = RecipientsActivity.class.getSimpleName();
    protected MenuItem mSendMenuItem;
    protected ViewPager mViewPager;
    protected android.support.v4.app.FragmentManager mFragmentManager;
    protected RecipientsAdapter mRecipientsAdapter;
    protected ArrayList<String> mRecipientIds;

    protected Uri mMediaUri;
    protected String mFileType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipients);

        mMediaUri = getIntent().getData();
        mFileType = getIntent().getExtras().getString(ParseConstants.KEY_FILE_TYPE);

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        //actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mFragmentManager = getSupportFragmentManager();
        mRecipientsAdapter = new RecipientsAdapter(this, mFragmentManager);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.recipients_pager);
        mViewPager.setAdapter(mRecipientsAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        //Currently only 1 tab
//        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
//            @Override
//            public void onPageSelected(int position) {
//                actionBar.setSelectedNavigationItem(position);
//            }
//        });
        //Currently just 1 tab
        // For each of the sections in the app, add a tab to the action bar.
        //for (int i = 0; i < sectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
        ActionBar.Tab tab = actionBar.newTab()
                .setText(mRecipientsAdapter.getPageTitle(0))
                .setTabListener(this);
        actionBar.addTab(tab);
        mViewPager.setCurrentItem(tab.getPosition());
        //}

    }

    public void onResume() {
        super.onResume();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recipients, menu);
        mSendMenuItem = menu.getItem(0);
        return true;
    }

    public void setRecipientIds(ArrayList<String> recipientIds){
        mRecipientIds = recipientIds;
    }

    protected ParseObject createMessage(){
        ParseObject message = new ParseObject(ParseConstants.CLASS_MESSAGES);
        ParseUser currentUser = ParseUser.getCurrentUser();
        message.put(ParseConstants.KEY_SENDER_ID, currentUser.getObjectId());
        message.put(ParseConstants.KEY_SENDER_NAME, currentUser.getUsername());
        message.put(ParseConstants.KEY_RECIPIENT_IDS, mRecipientIds);
        message.put(ParseConstants.KEY_FILE_TYPE, mFileType);

        byte[] fileBytes = FileHelper.getByteArrayFromFile(this, mMediaUri);

        if(fileBytes == null){
            return null;
        } else {
            if(mFileType.equals(ParseConstants.TYPE_IMAGE)){
                fileBytes = FileHelper.reduceImageForUpload(fileBytes);
            }

            String fileName = FileHelper.getFileName(this, mMediaUri, mFileType);
            ParseFile file = new ParseFile(fileName, fileBytes);
            message.put(ParseConstants.KEY_FILE, file);
            return message;
        }
    }


    protected void sendMessage(ParseObject message){
        message.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Context context = RecipientsActivity.this;
                if(e == null){
                    Utils.showToast(context, R.string.message_sent);
                    sendPushNotifications();
                } else {
                    Utils.displayErrorDialog(context.getString(R.string.error_sending_message),
                            context.getString(R.string.error_selecting_file_title), context);
                }
            }
        });
    }

    protected void sendPushNotifications() {
        ParseQuery<ParseInstallation> query = ParseInstallation.getQuery();
        query.whereContainedIn(ParseConstants.KEY_USER_ID, mRecipientIds);

        ParsePush push = new ParsePush();
        push.setQuery(query);
        push.setMessage(getString(R.string.push_message,
                                    ParseUser.getCurrentUser().getUsername()));
        push.sendInBackground();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_send:
                ParseObject message = createMessage();
                if(message == null){
                    Utils.displayErrorDialog(getString(R.string.error_selecting_file), getString(R.string.error_selecting_file_title), this);
                } else {
                    sendMessage(message);
                    finish();
                }
                return true;
            default:
                Log.e(TAG, "Unknown options item: " + item.getItemId());
        }
        return super.onOptionsItemSelected(item);
    }

    public MenuItem getMainMenuItem(){
        return mSendMenuItem;
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }
}
