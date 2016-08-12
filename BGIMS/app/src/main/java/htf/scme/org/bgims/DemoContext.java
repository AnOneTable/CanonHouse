package htf.scme.org.bgims;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;




public class DemoContext {

    private static DemoContext mDemoContext;
    public Context mContext;

    private SharedPreferences mPreferences;




    public static void init(Context context) {
        mDemoContext = new DemoContext(context);
    }

    public static DemoContext getInstance() {

        if (mDemoContext == null) {
            mDemoContext = new DemoContext();
        }
        return mDemoContext;
    }

    private DemoContext() {

    }

    private DemoContext(Context context) {
        mContext = context;
        mDemoContext = this;
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);


    }

    public SharedPreferences getSharedPreferences() {
        return mPreferences;
    }










}
