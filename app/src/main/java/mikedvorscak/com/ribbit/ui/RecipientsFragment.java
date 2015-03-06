package mikedvorscak.com.ribbit.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import mikedvorscak.com.ribbit.R;

public class RecipientsFragment extends FriendsFragment {

    protected ParseRelation<ParseUser> mFriendsRelation;
    protected List<ParseUser> mFriends;
    protected GridView mGridView;

    protected AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ImageView checkImageView = (ImageView) view.findViewById(R.id.checkImageView);

            if(mFriends == null){
                mFriends = RecipientsFragment.super.getFriends();
            }
            if(mFriendsRelation == null){
                mFriendsRelation = RecipientsFragment.super.getFriendsRelation();
            }

            ParseUser userClicked = mFriends.get(position);
            if(mGridView.isItemChecked(position)) {
                //Add friend
                mFriendsRelation.add(userClicked);
                checkImageView.setVisibility(View.VISIBLE);
            } else {
                //remove friend
                mFriendsRelation.remove(userClicked);
                checkImageView.setVisibility(View.INVISIBLE);
            }
            ((RecipientsActivity)getActivity()).getMainMenuItem().setVisible(mGridView.getCheckedItemCount() > 0);
            ((RecipientsActivity)getActivity()).setRecipientIds(getRecipientIds());
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);

        mGridView = (GridView) rootView.findViewById(R.id.friendsGrid);
        TextView emptyTextView = (TextView) rootView.findViewById(android.R.id.empty);
        mGridView.setEmptyView(emptyTextView);

        mGridView.setOnItemClickListener(mOnItemClickListener);
        mGridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);

        return rootView;
    }

    public void onResume() {
        super.onResume();
    }

    protected ArrayList<String> getRecipientIds(){
        ArrayList<String> recipientIds = new ArrayList<String>();
        for(int i = 0; i < mGridView.getCount(); i++){
            if(mGridView.isItemChecked(i)){
                recipientIds.add(mFriends.get(i).getObjectId());
            }
        }
        return recipientIds;
    }

}
