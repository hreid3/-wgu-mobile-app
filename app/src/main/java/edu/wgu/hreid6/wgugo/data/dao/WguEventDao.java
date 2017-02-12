package edu.wgu.hreid6.wgugo.data.dao;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.provider.CalendarContract;

import edu.wgu.hreid6.wgugo.Notifier;
import edu.wgu.hreid6.wgugo.data.model.WguEvent;

import static android.provider.CalendarContract.Events;
import static android.provider.CalendarContract.Reminders;

/**
 * Created by hreid on 2/11/17.
 */

public class WguEventDao {

    public void addEvent(Context context, WguEvent wguEvent) {
        if (context != null && wguEvent != null) {
            // Add to Calendar
            setNotificaition(context, "Scheduled Notification", wguEvent.getTitle() + ":: is starting", wguEvent.getStartTime());
            if (wguEvent.getEndTime() != wguEvent.getStartTime()) {
                setNotificaition(context, "Scheduled Notification", wguEvent.getTitle() + ":: is ending", wguEvent.getEndTime());
            }
            Intent intent = new Intent(Intent.ACTION_INSERT)
                    .setData(Events.CONTENT_URI)
                    .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, wguEvent.getStartTime())
                    .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, wguEvent.getEndTime())
                    .putExtra(Events.TITLE, wguEvent.getTitle())
                    .putExtra(Events.DESCRIPTION, wguEvent.getEventDescription())
                    .putExtra(Events.EVENT_LOCATION, wguEvent.getEventLocation())
                    .putExtra(Reminders.MINUTES, 10)
                    .putExtra(Events.AVAILABILITY, Events.AVAILABILITY_BUSY)
                    .putExtra(Events.UID_2445, wguEvent.getKey());
            context.startActivity(intent);
        } else {
            throw new IllegalArgumentException("Invalid parameters specified");
        }
    }

    private void setNotificaition(Context context, String title, String text, long time) {
        // Set a notification
        Notification.Builder builder = new Notification.Builder(context);
        builder.setContentTitle(title);
        builder.setContentText( text);
        Notification notification =  builder.build();

        Intent notificationIntent = new Intent(context, Notifier.class);
        notificationIntent.putExtra(Notifier.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(Notifier.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, time, pendingIntent);
    }
}
