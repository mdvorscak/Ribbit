package mikedvorscak.com.ribbit.ui;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import mikedvorscak.com.ribbit.R;

import mikedvorscak.com.ribbit.utils.FileHelper;
import mikedvorscak.com.ribbit.utils.ParseConstants;
import mikedvorscak.com.ribbit.utils.Utils;

public class RecipientsFragment extends ListFragment {

    private static final String TAG = RecipientsFragment.class.getSimpleName();

    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;
    protected Activity mParentActivity;
    protected Context mContext;
    protected List<ParseUser> mFriends;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recipients, container, false);
        return rootView;
    }

    public void onResume() {
        super.onResume();
        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        mParentActivity = getActivity();
        mContext = getListView().getContext();

        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);
        ParseQuery<ParseUser> query = mFriendsRelation.getQuery();
        query.addAscendingOrder(ParseConstants.KEY_USERNAME);

        query.findInBackground(new FindCallback<ParseUser>() {

            @Override
            public void done(List<ParseUser> friends, ParseException e) {
                if(e == null){
                    mFriends = friends;

                    //TODO: Refactor, this is copypasta from EditFriendsActivity
                    String[] usernames = new String[mFriends.size()];
                    int i = 0;
                    for(ParseUser user: mFriends){
                        usernames[i] = user.getUsername();
                        i++;
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext,
                            android.R.layout.simple_list_item_checked, usernames);
                    setListAdapter(adapter);
                } else {
                    Utils.displayErrorDialog(e.getMessage(),
                            getString(R.string.error_dialog_title),
                            mContext);
                }
            }
        });
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        ((RecipientsActivity)getActivity()).getMainMenuItem().setVisible(l.getCheckedItemCount() > 0);
        ((RecipientsActivity)getActivity()).setRecipientIds(getRecipientIds());
    }

    protected ArrayList<String> getRecipientIds(){
        ArrayList<String> recipientIds = new ArrayList<String>();
        for(int i = 0; i < getListView().getCount(); i++){
            if(getListView().isItemChecked(i)){
                recipientIds.add(mFriends.get(i).getObjectId());
            }
        }
        return recipientIds;
    }

}
