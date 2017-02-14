package com.is_great.pro.sunshine.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.text.format.DateUtils;

import com.is_great.pro.sunshine.data.SunshinePreferences;
import com.is_great.pro.sunshine.data.WeatherContract;
import com.is_great.pro.sunshine.utilities.NetworkUtils;
import com.is_great.pro.sunshine.utilities.NotificationUtils;
import com.is_great.pro.sunshine.utilities.OpenWeatherJsonUtils;

import java.net.URL;

public class SunshineSyncTask {

    synchronized public static void syncWeather(Context context) {
        URL weatherRequestUrl = NetworkUtils.getUrl(context);
        try {
            String jsonWeatherResponse = NetworkUtils.getResponseFromHttpUrl(weatherRequestUrl);
            ContentValues[] weatherValues = OpenWeatherJsonUtils
                    .getWeatherContentValuesFromJson(context, jsonWeatherResponse);

            if (weatherValues != null && weatherValues.length != 0) {
                ContentResolver sunshineContentResolver = context.getContentResolver();

                sunshineContentResolver.delete(
                        WeatherContract.WeatherEntry.CONTENT_URI,
                        null,
                        null
                );

                sunshineContentResolver.bulkInsert(
                        WeatherContract.WeatherEntry.CONTENT_URI,
                        weatherValues
                );
            }

            /*
                 * Finally, after we insert data into the ContentProvider, determine whether or not
                 * we should notify the user that the weather has been refreshed.
                 */
            boolean notificationsEnabled = SunshinePreferences.areNotificationsEnabled(context);

                /*
                 * If the last notification was shown was more than 1 day ago, we want to send
                 * another notification to the user that the weather has been updated. Remember,
                 * it's important that you shouldn't spam your users with notifications.
                 */
            long timeSinceLastNotification = SunshinePreferences
                    .getEllapsedTimeSinceLastNotification(context);

            boolean oneDayPassedSinceLastNotification = false;

//              COMPLETED (14) Check if a day has passed since the last notification
            if (timeSinceLastNotification >= DateUtils.DAY_IN_MILLIS) {
                oneDayPassedSinceLastNotification = true;
            }

                /*
                 * We only want to show the notification if the user wants them shown and we
                 * haven't shown a notification in the past day.
                 */
//              COMPLETED (15) If more than a day have passed and notifications are enabled, notify the user
            if (notificationsEnabled && oneDayPassedSinceLastNotification) {
                NotificationUtils.notifyUserOfNewWeather(context);
            }

            /* If the code reaches this point, we have successfully performed our sync */


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}