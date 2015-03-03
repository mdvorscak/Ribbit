package mikedvorscak.com.ribbit.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.List;

import mikedvorscak.com.ribbit.utils.ParseConstants;
import mikedvorscak.com.ribbit.R;
import mikedvorscak.com.ribbit.utils.Utils;

/**
 * Created by mike on 2/16/15.
 */
public class FriendsFragment extends ListFragment{

    public static final String TAG = FriendsFragment.class.getSimpleName();

    protected List<ParseUser> mFriends;
    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_friends, container, false);
        return rootView;
    }

    public void onResume() {
        super.onResume();

        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);
        ParseQuery<ParseUser> query = mFriendsRelation.getQuery();
        query.addAscendingOrder(ParseConstants.KEY_USERNAME);

        getActivity().setProgressBarIndeterminateVisibility(true);

        query.findInBackground(new FindCallback<ParseUser>() {

            Context context = getListView().getContext();
            @Override
            public void done(List<ParseUser> friends, ParseException e) {
                getActivity().setProgressBarIndeterminateVisibility(false);
                if(e == null){
                    mFriends = friends;

                    //TODO: Refactor, this is copypasta from EditFriendsActivity
                    String[] usernames = new String[mFriends.size()];
                    int i = 0;
                    for(ParseUser user: mFriends){
                        usernames[i] = user.getUsername();
                        i++;
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                            android.R.layout.simple_list_item_1, usernames);
                    setListAdapter(adapter);
                } else {
                    Log.e(TAG, e.getMessage());
                    Utils.displayErrorDialog(e.getMessage(),
                            getString(R.string.error_dialog_title),
                            context);
                }
            }
        });
    }
}
