package mikedvorscak.com.ribbit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;

/**
 * Created by mike on 2/7/15.
 */
public class Utils {
    public static void displayErrorDialog(String message, String title, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
                .setTitle(title)
                .setPositiveButton(android.R.string.ok, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static void switchActivity(Context fromContext, Class toClass, Boolean clearHistory){
        Intent intent = new Intent(fromContext, toClass);
        if(clearHistory) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }
        fromContext.startActivity(intent);
    }

    public static void switchActivity(Context fromContext, Class toClass){
        //Clear the history by default
        switchActivity(fromContext, toClass, true);
    }
}
