package com.miapp.duelchoice;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

public class WidgetOpcion extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // Actualizar cada widget
        for (int appWidgetId : appWidgetIds) {
            actualizarWidget(context, appWidgetManager, appWidgetId);
        }
    }

    private void actualizarWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        // Obtener la última opción guardada en SharedPreferences
        SharedPreferences prefs = context.getSharedPreferences("app", Context.MODE_PRIVATE);
        String ultimaOpcion = prefs.getString("ultima_opcion", "Juega una partida");

        // Crear vista remota
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_opcion);
        views.setTextViewText(R.id.widget_texto, ultimaOpcion);

        // Aplicar al widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}