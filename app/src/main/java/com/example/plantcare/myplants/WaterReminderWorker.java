package com.example.plantcare.myplants;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.plantcare.R;

public class WaterReminderWorker extends Worker {

    private static final String CHANNEL_ID = "watering_reminder_channel";

    public WaterReminderWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params
    ) {
        super(context, params);
        createNotificationChannel(context);
    }

    @NonNull @Override
    public Result doWork() {
        // Construction de la notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_leaf_logo)  // icône remplacée
                .setContentTitle(getApplicationContext()
                        .getString(R.string.notif_reminder_title))
                .setContentText(getApplicationContext()
                        .getString(R.string.notif_reminder_text))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        // Envoi
        NotificationManager nm =
                (NotificationManager) getApplicationContext()
                        .getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify((int) System.currentTimeMillis(), builder.build());

        return Result.success();
    }

    private void createNotificationChannel(Context ctx) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = ctx.getString(R.string.channel_watering);
            String desc = ctx.getString(R.string.channel_watering_desc);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel chan =
                    new NotificationChannel(CHANNEL_ID, name, importance);
            chan.setDescription(desc);
            NotificationManager nm =
                    ctx.getSystemService(NotificationManager.class);
            nm.createNotificationChannel(chan);
        }
    }
}
