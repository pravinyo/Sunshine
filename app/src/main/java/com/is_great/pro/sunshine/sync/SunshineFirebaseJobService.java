package com.is_great.pro.sunshine.sync;

import android.content.Context;
import android.os.AsyncTask;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

/**
 * Created by Pravinyo on 1/29/2017.
 */

public class SunshineFirebaseJobService extends JobService {

    private AsyncTask<Void,Void,Void> mFeatchWeatherTask;

    @Override
    public boolean onStartJob(final JobParameters job) {
        mFeatchWeatherTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                Context context = getApplicationContext();
                SunshineSyncTask.syncWeather(context);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                jobFinished(job, false);
            }
        };
        mFeatchWeatherTask.execute();
        return true;
    }


    @Override
    public boolean onStopJob(JobParameters job) {
        if(mFeatchWeatherTask != null){
            mFeatchWeatherTask.cancel(true);
        }
        return true;
    }
}
