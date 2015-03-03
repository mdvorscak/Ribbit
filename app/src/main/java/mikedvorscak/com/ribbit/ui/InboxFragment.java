package mikedvorscak.com.ribbit.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import mikedvorscak.com.ribbit.adapters.MessageAdapter;
import mikedvorscak.com.ribbit.utils.ParseConstants;
import mikedvorscak.com.ribbit.R;
import mikedvorscak.com.ribbit.utils.Utils;

/**
 * Created by mike on 2/16/15.
 */
public class InboxFragment extends ListFragment{

    public static final String TAG = ListFragment.class.getSimpleName();

    protected List<ParseObject> mMessages;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_inbox, container, false);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        final FragmentActivity currentActivity = getActivity();
        currentActivity.setProgressBarIndeterminateVisibility(true);
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(ParseConstants.CLASS_MESSAGES);
        query.whereEqualTo(ParseConstants.KEY_RECIPIENT_IDS, ParseUser.getCurrentUser().getObjectId());
        query.addDescendingOrder(ParseConstants.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> messages, ParseException e) {
                currentActivity.setProgressBarIndeterminateVisibility(false);
                Context context = getListView().getContext();
                if(e == null){
                    mMessages = messages;

                    //TODO: Refactor, this is copypasta from EditFriendsActivity
                    String[] usernames = new String[mMessages.size()];
                    int i = 0;
                    for(ParseObject message: mMessages){
                        usernames[i] = message.getString(ParseConstants.KEY_SENDER_NAME);
                        i++;
                    }

                    MessageAdapter adapter = (MessageAdapter) getListView().getAdapter();
                    if(adapter == null) {
                        adapter = new MessageAdapter(context, mMessages);
                        setListAdapter(adapter);
                    } else {
                        //refill the adapter, this solves jumping issue when coming back to the list after viewing an image
                        adapter.refill(mMessages);
                    }
                }
            }
        });
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        ParseObject message = mMessages.get(position);
        String messageType = message.getString(ParseConstants.KEY_FILE_TYPE);
        ParseFile file = message.getParseFile(ParseConstants.KEY_FILE);
        Uri fileUri = Uri.parse(file.getUrl());

        if(messageType.equals(ParseConstants.TYPE_IMAGE)){
            Utils.switchActivity(getActivity(), ViewImageActivity.class, false, fileUri);
        } else {
            //view video
            Intent intent = new Intent(Intent.ACTION_VIEW, fileUri);
            intent.setDataAndType(fileUri, "video/*");
            startActivity(intent);
        }

        deleteMessage(message);
    }

    private void deleteMessage(ParseObject message) {
        List<String> ids = message.getList(ParseConstants.KEY_RECIPIENT_IDS);

        if(ids.size() == 1){
            //Delete the whole message since we were the last to view it
            message.deleteInBackground();
        } else {
            //Remove the recipient and save
            String userId = ParseUser.getCurrentUser().getObjectId();
            ids.remove(userId);

            ArrayList<String> idsToRemove = new ArrayList<String>();
            idsToRemove.add(userId);

            message.removeAll(ParseConstants.KEY_RECIPIENT_IDS, idsToRemove);
            message.saveInBackground();
        }
    }
}
