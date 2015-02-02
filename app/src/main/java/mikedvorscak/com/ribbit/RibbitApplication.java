package mikedvorscak.com.ribbit;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

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
    }
}
