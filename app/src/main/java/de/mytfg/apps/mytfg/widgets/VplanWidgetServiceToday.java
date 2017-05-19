package de.mytfg.apps.mytfg.widgets;

import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViewsService;

/**
 * Created by bader on 19.05.2017.
 */

public class VplanWidgetServiceToday extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Log.d("WIDGET", "VplanWidgetServiceToday");
        return new VplanWidgetDataProvider(this, intent);
    }
}
