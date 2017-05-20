package de.mytfg.apps.mytfg.widgets;

import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViewsService;

/**
 * RemoteViewsService for next Day.
 */

public class VplanWidgetServiceTomorrow extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Log.d("WIDGET", "VplanWidgetServiceTomorrow");
        return new VplanWidgetDataProvider(this, intent);
    }
}
