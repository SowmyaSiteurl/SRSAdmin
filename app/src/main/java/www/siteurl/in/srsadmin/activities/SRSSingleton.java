package www.siteurl.in.srsadmin.activities;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by siteurl on 6/4/18.
 */

public class SRSSingleton {
    private static SRSSingleton minstance;
    private static Context mctx;
    private RequestQueue requestQueue;

    private SRSSingleton(Context context) {
        mctx = context;
        requestQueue = getRequestQueue();
    }


    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(mctx.getApplicationContext());
        }
        return requestQueue;
    }

    public static synchronized SRSSingleton getInstance(Context context) {
        if (minstance == null) {
            minstance = new SRSSingleton(context);
        }
        return minstance;
    }

    public <T> void addtorequestqueue(Request<T> request) {
        requestQueue.add(request);
    }
}


