package com.kristaappel.jobspot.objects;


import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

// This Singleton class improves performance by ensuring  that there is only one instance of a Volley RequestQueue

public class VolleySingleton {
    private static VolleySingleton volleySingleton;
    private RequestQueue requestQueue;
    private static Context mContext;

    private VolleySingleton(Context context){
        mContext = context;
        requestQueue = getRequestQueue();
    }

    public static synchronized VolleySingleton getInstance(Context context){
        if (volleySingleton == null){
            volleySingleton = new VolleySingleton(context);
        }
        return volleySingleton;
    }

    public RequestQueue getRequestQueue(){
        if (requestQueue == null){
            requestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> request){
        getRequestQueue().add(request);
    }
}
