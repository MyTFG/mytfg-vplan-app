package de.mytfg.apps.mytfg.widgets;

import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViewsService;

/**
 * RemoteViewsService for current Day.
 */

public class VplanWidgetServiceToday extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Log.d("WIDGET", "VplanWidgetServiceToday");
        return new VplanWidgetDataProvider(this, intent);
    }
}
