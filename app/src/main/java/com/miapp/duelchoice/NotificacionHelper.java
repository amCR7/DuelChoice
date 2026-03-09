package com.miapp.duelchoice;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class NotificacionHelper {

    private static final String CHANNEL_ID = "categorias_channel";
    private static final String CHANNEL_NAME = "Categorías";
    private static final int NOTIFICATION_ID = 1;

    public static void mostrarNotificacion(Context context, String nombreCategoria) {

        NotificationManager manager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("¡Nueva categoría creada!")
                .setContentText("Has creado la categoría \"" + nombreCategoria + "\", ¡enhorabuena!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        manager.notify(NOTIFICATION_ID, builder.build());
    }
}