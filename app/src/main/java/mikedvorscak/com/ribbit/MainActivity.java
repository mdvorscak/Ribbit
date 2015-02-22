package mikedvorscak.com.ribbit;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.parse.ParseUser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends ActionBarActivity implements ActionBar.TabListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    public static final int TAKE_PHOTO = 0;
    public static final int TAKE_VIDEO = 1;
    public static final int PICK_PHOTO = 2;
    public static final int PICK_VIDEO = 3;

    private static final String TAG = MainActivity.class.getSimpleName();
    protected Uri mMediaUri;

    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ParseUser currentUser = ParseUser.getCurrentUser();
        if(currentUser == null) {
            Utils.switchActivity(this, LoginActivity.class);
        } else {

        }

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch(id){
            case R.id.action_logout:
                ParseUser.logOut();
                Utils.switchActivity(this, LoginActivity.class);
                break;
            case R.id.action_edit_friends:
                Utils.switchActivity(this, EditFriendsActivity.class, false);
                break;
            case R.id.action_camera:
                Utils.displayChoiceDialog(this, R.array.camera_choices,
                        new DialogInterface.OnClickListener() {
                            FileManager fm = new FileManager(MainActivity.this.getString(R.string.app_name));
                            @Override
                            //TODO: Refactor the common code
                            public void onClick(DialogInterface dialog, int which) {
                                switch(which){
                                    case TAKE_PHOTO: //take picture
                                        mMediaUri = fm.getOutputFileUri(FileManager.MEDIA_TYPE_IMAGE);
                                        if(mMediaUri == null){
                                            Toast.makeText(MainActivity.this, getString(R.string.external_storage_error),
                                                    Toast.LENGTH_LONG).show();
                                        } else {
                                            Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                            takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
                                            startActivityForResult(takePhotoIntent, TAKE_PHOTO);
                                        }
                                        break;
                                    case TAKE_VIDEO: //take video

                                        mMediaUri = fm.getOutputFileUri(FileManager.MEDIA_TYPE_VIDEO);
                                        if(mMediaUri == null){
                                            Utils.showToast(MainActivity.this, R.string.external_storage_error);
                                        } else {
                                            Intent videoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                            videoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
                                            videoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);
                                            videoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0); // Low quality
                                            startActivityForResult(videoIntent, TAKE_VIDEO);
                                        }
                                        break;
                                    case PICK_PHOTO: //choose picture
                                        Intent choosePhotoIntent = new Intent(Intent.ACTION_GET_CONTENT);
                                        choosePhotoIntent.setType("image/*");
                                        startActivityForResult(choosePhotoIntent, PICK_PHOTO);
                                        break;
                                    case PICK_VIDEO: //choose video
                                        Intent chooseVideoIntent = new Intent(Intent.ACTION_GET_CONTENT);
                                        chooseVideoIntent.setType("video/*");
                                        Utils.showToast(MainActivity.this, R.string.video_length_warning);
                                        startActivityForResult(chooseVideoIntent, PICK_VIDEO);
                                        break;
                                }
                            }
                        });
                break;
            default:
                Log.e(TAG, "Unknown option id: " + id);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){

            if(requestCode == PICK_PHOTO || requestCode == PICK_VIDEO){
                if(data == null){
                    Utils.showErrorToast(this);
                } else {
                    mMediaUri = data.getData();
                }
                if(requestCode == PICK_VIDEO){
                    int fileSize = 0;

                    InputStream inputStream = null;
                    try {
                        inputStream = getContentResolver().openInputStream(mMediaUri);
                        fileSize = inputStream.available();
                    } catch (FileNotFoundException e) {
                        Utils.showToast(this, R.string.file_error_message);
                        Log.e(TAG, e.toString());
                        return;
                    } catch (IOException e){
                        Utils.showToast(this, R.string.file_error_message);
                        Log.e(TAG, e.toString());
                        return;
                    } finally {
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                           Log.e(TAG, e.toString());
                        }
                    }

                    if(fileSize >= FileManager.FILE_SIZE_LIMIT){
                        Utils.showToast(this, R.string.error_file_size_too_large);
                        return;
                    }


                }

            } else {
                //Add to the Gallery
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                mediaScanIntent.setData(mMediaUri);
                sendBroadcast(mediaScanIntent);
            }
        } else if (resultCode != RESULT_CANCELED){
            Utils.showErrorToast(this);
        }
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