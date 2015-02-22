package mikedvorscak.com.ribbit;

import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by mike on 2/21/15.
 */
public class FileManager {
    private static final String TAG = FileManager.class.getSimpleName();
    public static final int MEDIA_TYPE_IMAGE = 4;
    public static final int MEDIA_TYPE_VIDEO = 5;
    //10 MB
    public static final int FILE_SIZE_LIMIT = 1024*1024*10;
    private String mAppName;

    public FileManager(String appName){
        mAppName = appName;
    }

    public Uri getOutputFileUri(int mediaType){
        if (isExternalStorageAvailable()){
            //1. Get external storage directory
            File mainDir =  Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File mediaStorageDir = new File(mainDir, mAppName);
            //2. Create our subdirectory
            if(!mediaStorageDir.exists()){
                if(!mediaStorageDir.mkdirs()){
                    Log.e(TAG, "Failed to create directory.");
                    return null;
                }
            }
            //3. Create a file name
            //4. Create the file
            File mediaFile;
            Date now = new Date();
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(now);

            String path = mediaStorageDir.getPath() + File.separator;
            if(mediaType == MEDIA_TYPE_IMAGE){
                mediaFile = new File(path + "IMG_" + timestamp + ".jpg");
            } else if(mediaType == MEDIA_TYPE_VIDEO){
                mediaFile = new File(path + "VID_" + timestamp + ".mp4");
            } else {
                Log.e(TAG, "Unknown media type: " + mediaType);
                return null;
            }
            //5. Return the file's URI
            return Uri.fromFile(mediaFile);
        } else {
            return null;
        }
    }

    private boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }
}
