package ch.epfl.sweng.project.itemDisplayer;

import android.icu.text.DateFormat;
import android.util.Log;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

class TimeSchedulerSynchronise {

    private static final String TAG = "SchedulerSynchronise";
    private static final int timeBetweenSchedule = 10000;

    void schedule() {

        final Timer myTimer = new Timer();

        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (ListActivity.hasLocalDataChange()) {
                    ListActivity.synchronizeServer();
                    Log.d(TAG, "Synchronisation on the server:" + DateFormat.getDateTimeInstance()
                            .format(new Date()));
                }
            }
        }, 0, timeBetweenSchedule);
    }
}
