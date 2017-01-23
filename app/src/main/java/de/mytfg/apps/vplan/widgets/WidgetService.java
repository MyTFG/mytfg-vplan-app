package de.mytfg.apps.vplan.widgets;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by bader on 23.01.2017.
 */

public class WidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new WidgetDataProvider(this, intent);
    }
}
