package mikedvorscak.com.ribbit;

import android.app.Application;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.SaveCallback;

/**
 * Created by mike on 2/1/15.
 */
public class RibbitApplication extends Application {

    @Override
    public void onCreate(){
        super.onCreate();
        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "eCRiFz7HFXwf1Z7ehwaw0x8VkYrAwZvCEfl3N5v5", "UYtdHfYM4C7Td5m0bqwvmIRCjcTOIHPmcCn4DrdW");

        ParsePush.subscribeInBackground("", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("com.parse.push", "successfully subscribed to the broadcast channel.");
                } else {
                    Log.e("com.parse.push", "failed to subscribe for push", e);
                }
            }
        });
    }
}
