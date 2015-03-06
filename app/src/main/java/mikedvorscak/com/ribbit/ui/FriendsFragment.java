package mikedvorscak.com.ribbit.ui;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.List;

import mikedvorscak.com.ribbit.adapters.UserAdapter;
import mikedvorscak.com.ribbit.utils.ParseConstants;
import mikedvorscak.com.ribbit.R;
import mikedvorscak.com.ribbit.utils.Utils;

/**
 * Created by mike on 2/16/15.
 */
public class FriendsFragment extends Fragment {

    public static final String TAG = FriendsFragment.class.getSimpleName();

    protected List<ParseUser> mFriends;
    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;
    protected GridView mGridView;

    public List<ParseUser> getFriends(){
        return mFriends;
    }

    public ParseRelation<ParseUser> getFriendsRelation(){
        return mFriendsRelation;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.user_grid, container, false);
        mGridView = (GridView) rootView.findViewById(R.id.friendsGrid);

        TextView emptyTextView = (TextView) rootView.findViewById(android.R.id.empty);
        mGridView.setEmptyView(emptyTextView);

        return rootView;
    }

    public void onResume() {
        super.onResume();

        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);
        ParseQuery<ParseUser> query = mFriendsRelation.getQuery();
        query.addAscendingOrder(ParseConstants.KEY_USERNAME);

        //getActivity().setProgressBarIndeterminateVisibility(true);

        query.findInBackground(new FindCallback<ParseUser>() {

            Context context = getActivity();
            @Override
            public void done(List<ParseUser> friends, ParseException e) {
                //getActivity().setProgressBarIndeterminateVisibility(false);
                if(e == null){
                    mFriends = friends;

                    //TODO: Refactor, this is copypasta from EditFriendsActivity
                    String[] usernames = new String[mFriends.size()];
                    int i = 0;
                    for(ParseUser user: mFriends){
                        usernames[i] = user.getUsername();
                        i++;
                    }
                    if(mGridView.getAdapter() == null) {
                        UserAdapter adapter = new UserAdapter(context, mFriends);
                        mGridView.setAdapter(adapter);
                    }
                    else {
                        ((UserAdapter) mGridView.getAdapter()).refill(mFriends);
                    }
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
