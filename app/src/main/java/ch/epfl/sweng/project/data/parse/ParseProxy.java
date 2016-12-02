package ch.epfl.sweng.project.data.parse;




import android.content.Context;
import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import static ch.epfl.sweng.project.util.InternetAvailable.isInternetAvailable;
import ch.epfl.sweng.project.BuildConfig;


public enum ParseProxy {
    PROXY;

    private static Context context = null;


    public static  void notifyContextChange(Context context){
        ParseProxy.context = context;
    }


    public static <T extends ParseObject> List<T> toQuery(ParseQuery<T > query, String tag) {

        if(context == null){
            Log.d(tag, "The context is null. ");

            throw new IllegalStateException();
        }

        List<T> queryList = new ArrayList<T>();


        if (!isInternetAvailable(context)) {
            query.fromLocalDatastore();
        }



        try {
            queryList = query.find();

            if (isInternetAvailable(context)) {

                ParseObject.pinAllInBackground(queryList);
            }


        } catch (ParseException e) {
            if (BuildConfig.DEBUG) {
                Log.d(tag, "Error: " + e.getMessage());
            }
        }

        if (queryList.size() > 1)
            Log.d(tag, "Warning: The same id has different Resources.");



        return queryList;

    }



}
