package shellService;

import android.util.Log;

public class LogUtil {
    private static String TAG = "KEVIN_SHELL";
    public static void d(String tag, String log){
        if (tag.length() > 0) {
            Log.d(TAG , tag +":"+log);
        } else {
            Log.d(TAG, log);
        }
    }
}
