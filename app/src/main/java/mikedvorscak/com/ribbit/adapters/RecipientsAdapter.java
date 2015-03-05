package mikedvorscak.com.ribbit.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.Locale;

import mikedvorscak.com.ribbit.R;
import mikedvorscak.com.ribbit.ui.FriendsFragment;
import mikedvorscak.com.ribbit.ui.InboxFragment;
import mikedvorscak.com.ribbit.ui.RecipientsFragment;

/**
 * A {@link android.support.v4.app.FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class RecipientsAdapter extends FragmentPagerAdapter {

    protected Context mContext;
    public RecipientsAdapter(Context context,FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).

        switch (position){
            case 0:
                return new RecipientsFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Locale l = Locale.getDefault();
        switch (position) {
            case 0:
                return mContext.getString(R.string.title_activity_recipients).toUpperCase(l);
        }
        return null;
    }
}